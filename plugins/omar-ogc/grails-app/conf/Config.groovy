// configuration for plugin testing - will not be included in the plugin zip
 
log4j = {
  // WMS logging appender
  appender new org.ossim.omar.DbAppender(  name: "wmsLoggingAppender",
          threshold: org.apache.log4j.Level.INFO,
          modifyParametersClosure: {map->
            def newMap = [:]
            map.each{k,v->
              try{
                switch(k)
                {
                  case "width":
                  case "height":
                    newMap."${k}" = v as Long
                    break
                  case "internal_time":
                  case "render_time":
                  case "total_time":
                  case "mean_gsd":
                    newMap."${k}" = v as Double
                    break
                  case "start_date":
                  case "end_date":
                    def dateTime  = org.ossim.omar.ISO8601DateParser.parseDateTime(v)
                    newMap."${k}" =  new java.sql.Timestamp(dateTime.millis)
                    break
                  default:
                    newMap."${k}" = "${v}" as String
                }
              }
              catch(Exception e)
              {
                println "PROBLEMS with ${k}"
                println "ERROR = ${e}"
                newMap."${k}" = v
              }
            }
            newMap
          },
          sqlStatement: """INSERT INTO wms_log(version, width, height, layers, style,
                                               format, request, internal_time, render_time, total_time,
                                               start_date, end_date, user_name, ip, url, mean_gsd,
                                               geometry) VALUES
                           (0, :width, :height, :layers, :style,
                            :format, :request, :internal_time, :render_time, :total_time,
                            :start_date, :end_date, :user_name, :ip, :url, :mean_gsd,
                            ST_GeomFromText(:geometry, 4326))"""
          )
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    info wmsLoggingAppender: 'grails.app.service.org.ossim.omar.WmsLogService', additivity: true
 
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

// Added by the Joda-Time plugin:
grails.gorm.default.mapping = {
	"user-type" type: org.joda.time.contrib.hibernate.PersistentDateTime, class: org.joda.time.DateTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentDuration, class: org.joda.time.Duration
	"user-type" type: org.joda.time.contrib.hibernate.PersistentInstant, class: org.joda.time.Instant
	"user-type" type: org.joda.time.contrib.hibernate.PersistentInterval, class: org.joda.time.Interval
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalDate, class: org.joda.time.LocalDate
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalTimeAsString, class: org.joda.time.LocalTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalDateTime, class: org.joda.time.LocalDateTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentPeriod, class: org.joda.time.Period
}

// The following properties have been added by the Upgrade process...
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
