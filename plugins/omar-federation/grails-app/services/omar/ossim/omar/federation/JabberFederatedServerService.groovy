package omar.ossim.omar.federation

import grails.converters.JSON
import org.apache.xerces.util.ParserConfigurationSettings
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Presence
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
    def isConnected()
    {
        if (!jabber||!jabber?.connection||!jabber.chatRoom) return false;
        jabber?.connection?.isConnected()?true:false;
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
        def fullUser = makeFullUserNameAndId(userName)// + "@" + jabberDomain
        //fullUser =fullUser.replaceAll(~/\@|\.|\ |\&/, "")
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
        if(!enabled)
        {
            if (isConnected())
            {
                disconnect()
                refreshServerTable()
            }
            return jabber
        }
        if (isConnected())
        {
            refreshServerTable()
            return jabber
        }
        jabber = [:]
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
                jabber.chatRoom = null
                jabber.connection.disconnect()
                jabber.connection = null
                //println e
               // refreshServerTable()
               // return [:]
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
        def config     =  new ConnectionConfiguration(jabberDomain,
                jabberPort);
        def connection = new XMPPConnection(config);
        try{
            connection.connect();
            connection.login(jabberAdminUser, jabberAdminPassword);
        }
        catch(e)
        {
            connection = null

        }
        if (connection)
        {

        }
        def chatRoom = null
        if (connection)
        {
            chatRoom = new MultiUserChat(connection,
                    "${jabberChatRoomId}")
            try{
                chatRoom.join(jabberAdminUser,
                        "${jabberChatRoomPassword}")
            }
            catch(e)
            {
                chatRoom = null
                connection.disconnect();
                connection = null
            }
        }
        result = [
                connection:connection,
                config:config,
                chatRoom:chatRoom
        ]
        result
    }
    def getAllVCards()
    {
        def adminConnection = createAdminConnection()
        def vCards = []

        if (adminConnection.connection?.isConnected()&&adminConnection.chatRoom)
        {
            def occupants = adminConnection.chatRoom?.participants
            occupants.each{
                def user = it.jid.split("/")[0]
                def vCard = new VCard()
                vCard.load(adminConnection.connection, user)
                vCards << vCard
            }
           adminConnection.connection?.disconnect();//new Presence(Presence.Type.unavailable));
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
