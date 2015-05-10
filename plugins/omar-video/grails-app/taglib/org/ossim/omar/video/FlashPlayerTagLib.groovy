package org.ossim.omar.video

class FlashPlayerTagLib {
    static defaultEncodeAs = [taglib:'raw']

    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    private static String swfPlayerDefault = null
    private static String swfExpressInstallDefault = null
    private static String swfWidthDefault = "425"
    private static String swfHeightDefault = "355"
    private static String swfVersionDefault = "10"
    private static final String flashvarPrefix = "var"
    private static final String paramPrefix = "param"
    private static final String attribPrefix = "attrib"

    static namespace = 'omar'

    def grailsApplication

    def flashPlayer = {attrs, body ->
        if (!swfPlayerDefault) {
            synchronized (flashvarPrefix) {
                def val = grailsApplication.config.swf.player
                if (val && val instanceof String) {
                    swfPlayerDefault = val
                } else {
                    swfPlayerDefault = g.resource(dir: "mediaplayer-5.8", file: "player.swf", plugin: 'flashPlayer')
                }

                val = grailsApplication.config.swf.expressInstall
                if (val != null && val instanceof String) {
                    swfExpressInstallDefault = val
                } else {
                    swfExpressInstallDefault = g.resource(dir: "js/swfobject", file: "expressInstall.swf", plugin: 'swfobject')
                }

                val = grailsApplication.config.swf.width
                if (val && val instanceof Integer && val > 0) {
                    swfWidthDefault = "${val}"
                }

                val = grailsApplication.config.swf.height
                if (val && val instanceof Integer && val > 0) {
                    swfHeightDefault = "${val}"
                }

                val = grailsApplication.config.swf.version
                if (val && val instanceof String) {
                    swfVersionDefault = val
                }
            }
        }

        def swfId
        def swfPlayer = swfPlayerDefault
        def swfExpressInstall = swfExpressInstallDefault
        def swfWidth = swfWidthDefault
        def swfHeight = swfHeightDefault
        def swfVersion = swfVersionDefault
        def flashvars = [:]
        def parameters = [:]
        def attributes = [:]
        def key
        attrs.each {
            key = it.key

            if (key.equalsIgnoreCase("id")) {
                swfId = it.value
            } else if (key.equalsIgnoreCase("player")) {
                swfPlayer = it.value
            } else if (key.equalsIgnoreCase("expressinstall")) {
                swfExpressInstall = it.value
            } else if (key.equalsIgnoreCase("width")) {
                swfWidth = it.value
            } else if (key.equalsIgnoreCase("height")) {
                swfHeight = it.value
            } else if (key.equalsIgnoreCase("version")) {
                swfVersion = it.value
            } else if (key.startsWith(flashvarPrefix) && key.length() > flashvarPrefix.length()) {
                key = key.substring(flashvarPrefix.length(), flashvarPrefix.length() + 1).toLowerCase() + key.substring(flashvarPrefix.length() + 1)
                flashvars.put(key, it.value)
            } else if (key.startsWith(paramPrefix) && key.length() > paramPrefix.length()) {
                key = key.substring(paramPrefix.length(), paramPrefix.length() + 1).toLowerCase() + key.substring(paramPrefix.length() + 1)
                parameters.put(key, it.value)
            } else if (key.startsWith(attribPrefix) && key.length() > attribPrefix.length()) {
                key = key.substring(attribPrefix.length(), attribPrefix.length() + 1).toLowerCase() + key.substring(attribPrefix.length() + 1)
                attributes.put(key, it.value)
            }
        }

        out << """<script type="text/javascript">swfobject.embedSWF("${swfPlayer}", "${swfId}", "${swfWidth}", "${swfHeight}", "${swfVersion}", """
        out << (swfExpressInstall ? '"' + swfExpressInstall + '"' : 'false')
        out << ", {"
        def ftt = true
        flashvars.each {
            if (ftt) {
                ftt = false
            } else {
                out << ', '
            }

            out << it.key + ': "' + it.value + '"'
        }
        out << "}, {"
        ftt = true
        parameters.each {
            if (ftt) {
                ftt = false
            } else {
                out << ', '
            }

            out << it.key + ': "' + it.value + '"'
        }
        out << "}, {"
        ftt = true
        attributes.each {
            if (ftt) {
                ftt = false
            } else {
                out << ', '
            }

            out << it.key + ': "' + it.value + '"'
        }
        out << '});</script>'
    }    
}
