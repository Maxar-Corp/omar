package omar.ossim.omar.federation

import grails.converters.JSON
//import org.apache.xerces.util.ParserConfigurationSettings
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
  def jabberIp
  def jabberPort
  def jabberUser
  def jabberPassword
  def jabberChatRoomId
  def jabberChatRoomPassword
  def jabber
  def participantListener
  def enabled
  def wasConnected
  def federationEnabled

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

        vCard.setField("IP", settings?.vcard?.IP)
        vCard.setField("URL", settings?.vcard?.URL)
        if(settings?.vcard?.config){
          vCard.setField("config", settings?.vcard?.config.toString())
        }
        def port = 5222
        if(settings?.server?.port)
        {
          port =settings?.server?.port?.toInteger()
        }
        jabberIp                  = settings?.server?.ip
        jabberDomain              = settings?.server?.domain
        jabberPort                = port
        jabberUser                = settings?.server?.username
        jabberPassword            = settings?.server?.password
        jabberChatRoomId          = settings?.chatRoom?.id
        jabberChatRoomPassword    = settings?.chatRoom?.password
        enabled = settings?.chatRoom?.enabled
        if(!jabberDomain) jabberDomain = jabberIp;
        //vCard.setJabberId("${jabberUser}@${jabberDomain}")//"${config?.omar?.serverIP}@${jabberDomain}")
      }
      //println vCard.toString()
      //println "${jabberDomain}, ${jabberPort}, ${jabberUser}, ${jabberPassword}, ${jabberChatRoomId}, ${jabberChatRoomPassword}"
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
      makeAvailable(jabberUser);
      vcardList.each{vcard->
        try{
          def user = vcard.jabberId.split("@")[0];
          //println user
          makeAvailable(user)
        }
        catch(def e)
        {

        }
      }
    }
  }
  private replaceSpecialCharacters(def value)
  {
    return value.replaceAll(~/\@|\.|\ |\&/, "")
  }
  def makeFullUserNameAndId(def userName)
  {
    def full = userName + "@" + jabberDomain
    def fullId = replaceSpecialCharacters(full)
    return [user:full, id:fullId]
  }
  def makeAvailable(def userName)
  {
    try{
      def fullUserId = makeFullUserNameAndId(userName)//userName + "@" + jabberDomain
      def id = fullUserId.id
      def vcard
      if (userName.equals(jabberUser))
      {
        vcard = vCard
      }
      else
      {
        vcard = new VCard();
        vcard.load(jabber.connection, fullUserId.user);
      }
      if (vcard)
      {
        def ip =  vcard.getField("IP");
        if(ip)
        {
          if(federationEnabled)
          {
            FederatedServer.withTransaction{
              def federatedServer = new FederatedServer([serverId:id,
                                                         available: true,
                                                         vcard: vcard.toString()])
              federatedServer.save()
            }

          }
        }
      }

    }
    catch(def e)
    {

    }
  }
  def makeUnavailable(def userName)
  {
    try{
      def fullUser = makeFullUserNameAndId(userName)
      FederatedServer.withTransaction{
        FederatedServer.where{serverId==fullUser.id}.deleteAll()
      }
    }
    catch(def e)
    {

    }
  }
  def getServerList()
  {
    def result = []
    if(federationEnabled)
    {
      FederatedServer.withTransaction{
        FederatedServer.list(sort:"id", order: 'asc').each{server->
          def vcard = VCardProvider.createVCardFromXML(server.vcard)
          result << [
                  id: server.serverId,
                  firstName:vcard.firstName,
                  lastName:vcard.lastName,
                  nickname:vcard.nickName,
                  organization:vcard.organization,
                  ip:vcard.getField("IP"),
                  config:vcard.getField("config"),
                  url:vcard.getField("URL"),
                  phone:vcard.getPhoneHome("VOICE")?:vcard.getPhoneWork("VOICE")
          ]
        }
      }

    }
    if(!result)
    {
      result << [
              id: makeFullUserNameAndId(jabberUser).id,//userName + "@" + jabberDomain
              firstName:vCard.firstName,
              lastName:vCard.lastName,
              nickname:vCard.nickName,
              organization:vCard.organization,
              ip:vCard.getField("IP"),
              config:vCard.getField("config"),
              url:vCard.getField("URL"),
              phone:vCard.getPhoneHome("VOICE")?:vCard.getPhoneWork("VOICE")
      ]
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
        jabber.chatRoom.removeParticipantStatusListener(participantListener)
        jabber?.connection.disconnect()
      }
    }
    catch(def e)
    {

    }

    wasConnected = false;

    jabber = [:]
  }
  def connect()
  {
    if(!enabled||!federationEnabled)
    {
      if (isConnected())
      {
        disconnect()
      }
      refreshServerTable()
      return jabber
    }
    if (isConnected())
    {
      refreshServerTable()
      return jabber
    }
    jabber = [:]
    try{
      jabber.config     =  new ConnectionConfiguration(jabberIp,
                                                       jabberPort);
      switch (grailsApplication.config.jabber.securityMode)
      {
        case "disabled":
          //println "disabled....."
          jabber.config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
          break;
        case "enabled":
          //println "enabled....."
          jabber.config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
          break;
        case "required":
          //println "required....."
          jabber.config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
          break;
        default:
          //println "default disable....."
          jabber.config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
          break;
      }

      jabber.connection = new XMPPConnection(jabber.config);
      jabber.connection.connect();
      jabber.connection.login(jabberUser, jabberPassword)

      vCard.setJabberId(jabber.connection.getUser().split("/")[0])//"${config?.omar?.serverIP}@${jabberDomain}")

      //println "${vCard}";
    }
    catch(def e)
    {
      refreshServerTable()
      //log.error(e2)
      return [:]
    }
    if (jabber.connection.isAuthenticated()&&federationEnabled)
    {
      try{

        vCard.save(jabber.connection)
        jabber.chatRoom = new MultiUserChat(jabber.connection,
                                            "${jabberChatRoomId}")

        jabber.chatRoom.join(jabberUser,
                "${jabberChatRoomPassword}")

        participantListener = new JabberParticipantListener([federatedServerService:this])
        jabber.chatRoom.addParticipantStatusListener(participantListener)
      }
      catch(def e)
      {
        //println e
        jabber.chatRoom = null
        disconnect();
        jabber.connection = null
        //println e
        // refreshServerTable()
        // return [:]
      }
    }
    refreshServerTable()

    if(isConnected())
    {
      wasConnected = true;
    }
    return jabber
  }
  /*
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
  */
  def getAllVCards()
  {
    def vCards = []

    try{
      if (isConnected()&&jabber.chatRoom)
      {
        def occupants = jabber.chatRoom?.occupants
        occupants.each{
          def user = it.split("/")[-1] + "@" + jabberDomain
          def vCard = new VCard()
          vCard.load(jabber.connection, user)
          vCards << vCard
        }
      }
    }
    catch(def e)
    {
      log.warn(e.toString());
    }
    vCards
  }


  void afterPropertiesSet() throws Exception {
    federationEnabled = grailsApplication.config.federation.enabled
    
  }

  void initialize()
  {
    loadFromTable()
    connect()    
  }
}
