package org.ossim.omar.raster

class GetTileLogService {
    def getTileLoggingAppender

    def fixParamsForRouting(def params){
        def paramsSave = [:]

        params.each {k, v ->
            if ( params."${k}" != null )
            {
                paramsSave."${k}" = v
            }
        }
        paramsSave["startDate"] = params.startDate //.toString()
        paramsSave["endDate"] = params.endDate //.toString()

        paramsSave
    }
    def logParams(def params) {
        try{

            def paramsSave = fixParamsForRouting(params)
            def getTileLogRecord = new GetTileLog()
            getTileLogRecord.properties = paramsSave

            if ( getTileLogRecord.hasErrors() || !getTileLogRecord.save() )
            {
                getTileLogRecord.errors.allErrors.each { println it }
            }
        }
        catch(def e)
        {
            println e
        }
    }
}
