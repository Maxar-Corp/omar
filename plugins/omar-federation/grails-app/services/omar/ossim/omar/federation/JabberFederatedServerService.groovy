package omar.ossim.omar.federation

import grails.converters.JSON
import org.apache.xerces.util.ParserConfigurationSettings
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.packet.VCard
import org.jivesoftware.smackx.provider.VCardProvider
import org.ossim.omar.federation.FederatedServer
import org.ossim.omar.federation.JabberParticipantListener
import org.springframework.beans.factory.InitializingBean
import org.ossim.omar.core.ConfigSettings
import groovy.json.JsonBuilder

class JabberFederatedServerService implements InitializingBean{
    def grailsApplication
    def federationConfigSettingsService
    def vCard
    def jabberDomain
    def jabberPort
    def jabberAdminUser
    def jabberAdminPassword
    def jabberChatRoomId
    def jabberChatRoomPassword
    def jabber
    def participantListener
    def enabled
    void loadFromTable(){
        def record = federationConfigSettingsService.getSettingsRecord()
        if(record)
        {
            def settings = JSON.parse(record.settings);
            if(settings)
            {
                vCard = new VCard()
                vCard.setNickName(settings?.vcard?.nickName);
                vCard.setFirstName(settings?.vcard?.firstName);
                vCard.setLastName(settings?.vcard?.lastName);

                vCard.setField("IP", settings.vcard.IP)
                vCard.setField("URL", settings.vcard.URL)

                jabberDomain              = settings?.server?.ip
                jabberPort                = Integer.parseInt(settings?.server?.port)
                jabberAdminUser           = settings?.server?.username
                jabberAdminPassword       = settings?.server?.password
                jabberChatRoomId          = settings?.chatRoom?.id
                jabberChatRoomPassword    = settings?.chatRoom?.password
                enabled = settings?.chatRoom?.enabled
                vCard.setJabberId("${settings.vcard.IP}@${jabberDomain}")//"${config?.omar?.serverIP}@${jabberDomain}")
            }
           //println vCard.toString()
           //println "${jabberDomain}, ${jabberPort}, ${jabberAdminUser}, ${jabberAdminPassword}, ${jabberChatRoomId}, ${jabberChatRoomPassword}"
        }
    }
    /*
    def loadFromConfig(def config)
    {
        vCard = new VCard()
        def federation = config.federation
        federation?.vcard?.fields?.each{key,value->
            switch(key.toLowerCase())
            {
                case "firstname":
                    vCard.setFirstName(value)
                    break
                case "lastname":
                    vCard.setLastName(value)
                case "nickname":
                    vCard.setNickName(value)
                case "organization":
                    vCard.setOrganization(value)
                default:
                    vCard.setField(key, value)
                    break
            }
        }
        vCard.setField("IP", config?.omar?.serverIP)
        vCard.setField("URL", config?.omar?.serverURL)
        if (!vCard.getNickName())
        {
            vCard.setNickName("${config?.omar?.serverIP}");
        }

        jabberDomain        = federation?.server?.ip
        jabberPort          = federation?.server?.port
        jabberAdminUser     = federation?.server?.username
        jabberAdminPassword = federation?.server?.password
        jabberChatRoomId    = federation?.chatRoom?.id
        jabberChatRoomPassword    = federation?.chatRoom?.password
        vCard.setJabberId("${config?.omar?.serverIP}@${jabberDomain}")
    }
    */
    def isConnected()
    {
        jabber?.connection?.isConnected()
    }
    def refreshServerTable()
    {
        def vcardList = getAllVCards()

        FederatedServer.withTransaction {
            FederatedServer.executeUpdate('delete from FederatedServer')

            def fullUserId = makeFullUserNameAndId(vCard.getField("IP"));
                def federatedServer = new FederatedServer([serverId:fullUserId.id,
                        available: true,
                        vcard: vCard.toString()])
                federatedServer.save()

            vcardList.each{vcard->
                def ip = vcard.getField("IP")
                if (ip)
                {
                    makeAvailable(vcard.getField("IP"))
                }
            }
        }
    }
    def makeFullUserNameAndId(def userName)
    {
        def full = userName + "@" + jabberDomain
        def fullId = full.replaceAll(~/\@|\.|\ |\&/, "")
        return [user:full, id:fullId]
    }
    def makeAvailable(def userName)
    {
        def fullUserId = makeFullUserNameAndId(userName)//userName + "@" + jabberDomain
        def tempCard = new VCard();
        try{
            tempCard.load(jabber.connection, fullUserId.user);
            def ip =  tempCard.getField("IP");
            if(ip)
            {
                FederatedServer.withTransaction{
                    def federatedServer = new FederatedServer([serverId:fullUserId.id,
                            available: true,
                            vcard: tempCard.toString()])
                    federatedServer.save()
                }
            }
        }
        catch(def e)
        {
        }
    }
    def makeUnavailable(def userName)
    {
        def fullUser = userName + "@" + jabberDomain
        fullUser =fullUser.replaceAll(~/\@|\.|\ |\&/, "")
        FederatedServer.withTransaction{
            FederatedServer.where{serverId==fullUser}.deleteAll()
        }
    }
    def getServerList()
    {
        def result = []
        FederatedServer.withTransaction{
            FederatedServer.findAll(sort:"id", order: 'asc').each{server->
                def vcard = VCardProvider.createVCardFromXML(server.vcard)
                result << [
                        id: server.serverId,
                        firstName:vcard.firstName,
                        lastName:vcard.lastName,
                        nickname:vcard.nickName,
                        organization:vcard.organization,
                        ip:vcard.getField("IP"),
                        url:vcard.getField("URL"),
                        phone:vcard.getPhoneHome("VOICE")?:vcard.getPhoneWork("VOICE")
                ]
            }
        }
        /*
        if (!result)
        {
            def fullUser = grailsApplication.config?.omar?.serverIP;

           result << [id: fullUser.replaceAll(~/\@|\.|\ |\&/, ""),
                      firstName: grailsApplication.config?.omar?.serverIP,
                      ip:grailsApplication.config?.omar?.serverIP,
                      url:grailsApplication.config?.omar?.serverURL,
                      nickname:"${System.properties['user.name']}"
                     ]
        } */
        // println result
        result
    }
    def reconnect()
    {
        if (isConnected())
        {
            disconnect()
        }
        loadFromTable()

        connect()
    }
    def disconnect()
    {
        try{

            if(isConnected())
            {
                jabber.chatRoom.removeParticipantListener(participantListener)
                jabber?.connection.disconnect()
            }
        }
        catch(def e)
        {

        }
        jabber = [:]
    }
    def connect()
    {
        if (isConnected())
        {
            refreshServerTable()
            return jabber
        }
        jabber = [:]
        if (!enabled)
        {
            refreshServerTable()

            return jabber
        }
        try{
            jabber.config     =  new ConnectionConfiguration(jabberDomain,
                                                             jabberPort);
            jabber.connection = new XMPPConnection(jabber.config);
            jabber.connection.connect();
            jabber.connection.login(vCard.getField("IP"), "abc123!@#")
        }
        catch(def e)
        {
            //log.error(e)
            if (!jabber.connection){
                refreshServerTable()
                return [:]
            }
            try{
                jabber.connection.disconnect()
                jabber.connection.connect()
                jabber.connection.login(jabberAdminUser, jabberAdminPassword);
                jabber.connection.accountManager.createAccount(vCard.getField("IP"),
                        "abc123!@#",
                        [name:vCard.getField("IP")]);
                jabber.connection.disconnect()
                jabber.connection.connect()
                jabber.connection.login(vCard.getField("IP"), "abc123!@#")
            }
            catch(def e2)
            {
                refreshServerTable()
                //log.error(e2)
                return [:]
            }
        }
        if (jabber.connection.isAuthenticated())
        {
            try{

                vCard.save(jabber.connection)
                jabber.chatRoom = new MultiUserChat(jabber.connection,
                        "${jabberChatRoomId}")
                jabber.chatRoom.join(vCard.getField("IP"),
                        "${jabberChatRoomPassword}")

                participantListener = new JabberParticipantListener([federatedServerService:this])
                jabber.chatRoom.addParticipantListener(participantListener)
            }
            catch(def e)
            {
                //println e
                refreshServerTable()
                return [:]
            }
        }
        refreshServerTable()
        return jabber
    }
    def createAdminConnection()
    {
        def result = [connection:null,
                config: null,
                chatRoom: null]
        try{

            def config     =  new ConnectionConfiguration(jabberDomain,
                    jabberPort);
            def connection = new XMPPConnection(config);
            connection.connect();
            connection.login(jabberAdminUser, jabberAdminPassword);
            def chatRoom = new MultiUserChat(connection,
                    "${jabberChatRoomId}")
            chatRoom.join(jabberAdminUser,
                    "${jabberChatRoomPassword}")
            result = [
                    connection:connection,
                    config:config,
                    chatRoom:chatRoom
            ]
        }
        catch(def e)
        {

        }

        result
    }
    def getAllVCards()
    {
        def adminConnection = createAdminConnection()
        def vCards = []

        if (adminConnection.connection?.isConnected())
        {
            def occupants = adminConnection.chatRoom.participants
            occupants.each{
                def user = it.jid.split("/")[0]
                def vCard = new VCard()
                vCard.load(adminConnection.connection, user)
                vCards << vCard
            }
            adminConnection.connection.disconnect();
        }
        vCards
    }


    void afterPropertiesSet() throws Exception {
        loadFromTable()
        connect()
        //loadFromConfig(grailsApplication.config)
        //connect()
    }
}
