package org.ossim.omar.federation

import grails.converters.JSON

class FederationController {
    def JabberFederatedServerService

    def rasterSearch() {
    }
    def serverList(){
       render  (JabberFederatedServerService.getServerList() as JSON)
    }
}
