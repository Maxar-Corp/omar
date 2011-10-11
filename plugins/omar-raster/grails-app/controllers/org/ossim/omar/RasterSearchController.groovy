package org.ossim.omar


class RasterSearchController
{
  def search = {
    def model = [:]
    if ( request.method.toUpperCase() == "POST" )
    {
      model['startDate'] = params['startDate']
      model['endDate'] = params['endDate']
      println model
    }
    [model:model]
  }

  def results = {

  }
}