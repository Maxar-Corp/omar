package org.ossim.omar

class LoggingFilters
{

  def filters = {
    all(controller: '*', action: '*') {
      before = {
        log.debug "Parameters: ${params.inspect()}"
      }
      after = {model ->
        log.debug "Model: ${model.inspect()}"
      }
      afterView = {

      }
    }
  }

}
