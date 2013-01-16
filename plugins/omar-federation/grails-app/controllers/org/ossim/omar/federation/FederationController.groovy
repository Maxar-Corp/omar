package org.ossim.omar.federation

import grails.converters.JSON
import org.apache.commons.collections.map.CaseInsensitiveMap

class FederationController  {
    def jabberFederatedServerService
    def grailsApplication

    def index(){
        forward controller: "federation", action: "rasterSearch"
    }
    def rasterSearch() {
        def wmsBaseLayers = (grailsApplication.config.wms as JSON).toString()
        render view: 'rasterSearch', model:[wmsBaseLayers:wmsBaseLayers]
    }
    def serverList(){
        def tempParam = new CaseInsensitiveMap(params);
        def result = jabberFederatedServerService.serverList as JSON
        def callback = ""
        if (tempParam.callback) callback = tempParam.callback
        else if (tempParam.jsonCallback) callback = tempParam.jsonCallback
        if (callback){
            result = "${callback}(${result})"// added for cross domain support
        }
        render contentType: 'application/json', text: result.toString()
    }
    def reconnect(){
        jabberFederatedServerService.reconnect();
        def tempParam = new CaseInsensitiveMap(params);
        def result = [connected:jabberFederatedServerService.isConnected()] as JSON
        def callback = ""
        if (tempParam.callback) callback = tempParam.callback
        if (callback){
            result = "${callback}(${result})"// added for cross domain support
        }
        render contentType: 'application/json', text: result.toString()
    }
    def admin(){

    }
}
