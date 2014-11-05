package org.ossim.omar

import com.budjb.rabbitmq.MessageContext
import groovy.json.*
import grails.converters.*


class JobStatusConsumer {
    def publishService
    def jobService
    //def grailsApplication
    /**
     * Consumer configuration.
     */
    static rabbitConfig = [
       // "exchange": "amq.topic",
       // "binding" : "omar.job.status"

       "queue" : "omar.job.status"    
       ]

    /**
     * Handle an incoming RabbitMQ message.
     *
     * @param body    The converted body of the incoming message.
     * @param context Properties of the incoming message.
     * @return
     */
    def handleMessage(def body, MessageContext context) {
      //  println "---------------${body}------------------"
        def slurper = new JsonSlurper()
        def jsonObj
        if(body instanceof byte[])
        {
          jsonObj = slurper.parseText(new String(body,"UTF-8"))
        }
        else if(body instanceof String)
        {
        //  println "DOING INSTANCE!!!!"
          jsonObj = slurper.parseText(body)

            // publish the message for anyone else listening.
            // publishService.routeStatus(body.toString())
        }
        else if(body instanceof HashMap)
        {
          jsonObj = body
        }
        if(jsonObj) jobService.updateJob(jsonObj)
    }
}