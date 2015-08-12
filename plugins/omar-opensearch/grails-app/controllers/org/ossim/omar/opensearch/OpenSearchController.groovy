package org.ossim.omar.opensearch

import grails.converters.JSON

class OpenSearchController {

    def index() { }
    def imageResults()
    {
        def initParams =[
                cqlFilter:params?.cqlFilter?:"",
                //wfsUrl:
        ]
        [initParams:initParams as JSON]
    }
}
