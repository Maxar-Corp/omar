package org.ossim.omar.core

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 8/7/15.
 */
@Validateable
@ToString
class ReportCommand
{
   String name
   String phone
   String emailTo
   String subject
   String cc
   String emailFrom
   String report
   static constraints = {
      name (blank: false)
      phone  (blank: false)
      emailTo  (blank: false)
      subject  (blank: false)
      cc  (nullable: true, blank: true)
      emailFrom  (nullable: true)
      report  (blank: false)
   }
   def ccToArray()
   {
      cc?.split(",")
   }
   def emailToArray()
   {
       emailTo?.split(",")
   }
}
