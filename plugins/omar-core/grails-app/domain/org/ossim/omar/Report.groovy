package org.ossim.omar

class Report {
  String name
  String email
  String phone
  Date createdDate
  String report

  static constraints = {
    name(blank:false)
    email(blank:false)
    phone(blank:true, nullable:true)
    createdDate()
    report(maxSize:1000, blank:false)
  }
}