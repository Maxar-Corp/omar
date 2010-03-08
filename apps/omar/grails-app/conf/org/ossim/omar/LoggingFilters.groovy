package org.ossim.omar

class LoggingFilters
{

  def filters = {
    all(controller: '*', action: '*') {
      before = {
        log.info "Parameters: ${params.inspect()}"
      }
      after = {model ->
        log.info "Model: ${model.inspect()}"
      }
      afterView = {

      }
    }
  }

}
