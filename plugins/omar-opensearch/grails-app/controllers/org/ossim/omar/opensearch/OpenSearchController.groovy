package org.ossim.omar.opensearch

import grails.converters.JSON

class OpenSearchController {

    def grailsApplication

    def index() { }

    def generalSearch()
    {
        def cqlFilter = ""
        if(!params.s)
        {
            // need to create the filter
        }
        forward(action: "imageResults", controller: "openSearch", params: [cqlFilter:cqlFilter])
    }

    def imageResults()
    {
        def initParams =[
                cqlFilter:params?.cqlFilter?:"",
                //wfsUrl:
        ]
        [initParams:initParams as JSON]
    }
    def generalSearchDescriptor()
    {
        render contentType: "application/xml", text: grailsApplication?.config?.opensearch?.generalSearch?.descriptor?:""
    }
}
