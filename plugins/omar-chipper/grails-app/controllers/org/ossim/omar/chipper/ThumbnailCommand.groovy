package org.ossim.omar.chipper

/**
 * Created by sbortman on 4/21/14.
 */
@grails.validation.Validateable
class ThumbnailCommand
{
  Long id
  Integer size = 128
  String type = 'jpeg'
}
