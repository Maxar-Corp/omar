package org.ossim.omar.federation

import grails.converters.JSON

class FederationController {
    def jabberFederatedServerService

    def rasterSearch() {
    }
    def serverList(){
       render  (jabberFederatedServerService.getServerList() as JSON)
    }
    def resetConnection(){
        jabberFederatedServerService.reconnect();
        render ([connected:jabberFederatedServerService.isConnected()] as JSON)
    }
}
