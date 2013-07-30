package org.ossim.omar.core

class ConfigSettings {
    String name
    String settings
    static constraints = {
        name(unique: true)
        settings(nullable: true)
    }
    static mapping = {
        columns {
            settings type: 'text'
            name index: 'config_settings_name_idx', unique: true
       }
    }

}
