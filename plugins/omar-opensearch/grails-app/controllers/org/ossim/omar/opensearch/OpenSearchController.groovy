package org.ossim.omar.opensearch

import grails.converters.JSON

class OpenSearchController {

    def grailsApplication

    def index() { }

    def pointRadiusSearch()
    {
        forward(action: "imageResults", controller: "openSearch", params: [pointRadius:params.value])
    }

    def imageResults()
    {
        def initParams =[
                pointRadius:params?.pointRadius?:"",
        ]
        [initParams:initParams as JSON]
    }
    def searchDescriptor()
    {
        String description = grailsApplication?.config?.opensearch?.pluginList?."${params.name}"?.description

        render contentType: "application/xml", text: description?:""
    }
}
