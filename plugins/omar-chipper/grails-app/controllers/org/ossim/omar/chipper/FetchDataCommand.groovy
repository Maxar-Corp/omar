package org.ossim.omar.chipper

/**
 * Created by sbortman on 4/21/14.
 */
@grails.validation.Validateable
class FetchDataCommand
{
  Integer rows = 10
  Integer page = 1
  String sort = 'id'
  String order = 'desc'
  String filter
}
