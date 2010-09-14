package org.ossim.omar

class Report {
  String name
  String email
  Date createdDate
  String report

  static constraints = {
    name(blank:false)
    email(blank:false)
    createdDate()
    report(maxSize:1000, blank:false)
  }
}