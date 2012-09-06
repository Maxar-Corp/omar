package org.ossim.omar.raster

class GetTileLog {
    Double x
    Double y
    Long   width
    Long   height
    String format
    String layers
    Double scale
    Double internalTime
    Double renderTime
    Double totalTime
    Date   startDate
    Date   endDate
    String userName
    String ip
    String url

    static constraints = {
        x(nullable:true)
        y(nullable:true)
        width(nullable:true)
        height(nullable:true)
        format(nullable:true)
        layers(nullable:true)
        scale(nullable:true)
        internalTime(nullable:true)
        renderTime(nullable:true)
        totalTime(nullable:true)
        startDate(nullable:true)
        endDate(nullable:true)
        userName(nullable:true)
        ip(nullable:true)
        url(nullable:true)
    }
    static mapping = {
        version false
        columns
        {
           url type:'text'
           layers type: 'text'
        }
    }
}
