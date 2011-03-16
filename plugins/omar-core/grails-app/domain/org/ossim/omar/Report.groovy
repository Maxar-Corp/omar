package org.ossim.omar

class Report {
  String name
  String email
  String phone
  Date createdDate
  String report
  String status
  String comment

  static constraints = {
    name(blank:false)
    email(blank:false)
    phone(blank:true, nullable:true)
    createdDate()
    report(maxSize:1000, blank:false)
    status(nullable:true)
    comment(nullable:true)
  }
    static mapping = {
       columns {
         report    type: 'text'
         comment  type: 'text'
       }
     }
}