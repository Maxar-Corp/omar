package org.ossim.omar

class ReportController {
  def scaffold = Report

  def index = {
    redirect(action: "create", params: params)
  }
}
