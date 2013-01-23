class OmarCoreUrlMappings
{
    static mappings = {
        "/configSettings/${settingsName}" {

            controller = "configSettings"
            action = "action"

            constraints {
                // apply constraints here
            }
        }
    }
}