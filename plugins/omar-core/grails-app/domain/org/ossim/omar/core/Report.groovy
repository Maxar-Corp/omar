package org.ossim.omar.core

class Report
{
  String name
  String email
  String phone
  String report
  String status
  String comment

  Date dateCreated
  Date lastUpdated

  static constraints = {
    name(blank: false)
    email(blank: false)
    phone(blank: true, nullable: true)
    report(maxSize: 1000, blank: false)
    status(nullable: false, blank: false)
    comment(nullable: true)
  }
  static mapping = {
    columns {
      report type: 'text'
      comment type: 'text'
    }
  }
}