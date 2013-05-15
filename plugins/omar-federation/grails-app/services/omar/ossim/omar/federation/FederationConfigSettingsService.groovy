package omar.ossim.omar.federation

import grails.converters.JSON
import groovy.json.JsonBuilder
import org.ossim.omar.core.ConfigSettings

class FederationConfigSettingsService {
    def grailsApplication
    static transactional = true
    def getSettingsRecord()
    {
        def federationSettingsRecord = ConfigSettings.findByName("omar-federation");


        def tempIP  = grailsApplication?.config?.omar?.serverIP
        def tempURL = grailsApplication?.config?.omar?.serverURL
        if (!federationSettingsRecord)
        {
            federationSettingsRecord = new ConfigSettings([name:"omar-federation",settings:"{}"])
        }
        def settings
        try{
          settings = JSON.parse(federationSettingsRecord.settings);
        }
        catch(e)
        {
          settings = JSON.parse("{}");
        }

        if (settings.vcard==null||settings.server==null||settings.chatRoom==null)
        {
            def builder = new JsonBuilder()
            builder{
                if(!settings.vcard)
                {
                    vcard{
                        config{

                          wms(
                             grailsApplication.config.wms
                          )
                        }
                        version(
                             grailsApplication.metadata['app.version']
                        )
                        if(!settings?.vcard?.URL)
                        {
                            URL "${tempURL}"
                        }
                        if(!settings?.vcard?.IP)
                        {
                            IP "${tempIP}"
                        }
                        if(!settings?.vcard?.nickName)
                        {
                            nickName "${System.properties['user.name']}"
                        }
                    }
                }
                if (!settings.server)
                {
                    server{
                        if(!settings?.server?.port)
                        {
                            port "5222"
                        }
                    }
                }
                if (!settings.chatRoom)
                {
                    chatRoom{

                    }
                }
            }
            settings = builder.toString()
        }
        else
        {
          settings.vcard.URL = "${tempURL}"
          settings.vcard.IP  = "${tempIP}"
          settings.vcard.version = "${grailsApplication.metadata['app.version']}"

          def tempBuilder = new JsonBuilder()
            tempBuilder{
              wms(
                grailsApplication.config.wms
              )
            }
            settings.vcard.config = JSON.parse(tempBuilder.toString())
        }
        federationSettingsRecord.settings = settings.toString()
        federationSettingsRecord.save(flush: true)

        federationSettingsRecord
    }

    def updateSettings(def settings)
    {
        def federationSettingsRecord = getSettingsRecord();

        federationSettingsRecord?.settings = settings;

        federationSettingsRecord?.save(flush: true)

        federationSettingsRecord
    }
}
