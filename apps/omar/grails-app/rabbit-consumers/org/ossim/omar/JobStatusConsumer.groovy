package org.ossim.omar

import com.budjb.rabbitmq.MessageContext
import groovy.json.*


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
        println "---------------${body}------------------"
        if(body instanceof String)
        {
            def slurper = new JsonSlurper()
            def jsonObj = slurper.parseText(body)
            
            jobService.updateJob(jsonObj)
            
            // publish the message for anyone else listening.
            // publishService.routeStatus(body.toString())
        }
    }
}