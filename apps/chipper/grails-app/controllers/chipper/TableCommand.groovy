package chipper

import grails.validation.Validateable

import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Created by sbortman on 3/26/14.
 */
@Validateable
class TableCommand
{
  String filter
  Integer page
  Integer rows
  String sort
  String order

  @Override
  String toString()
  {
    return ToStringBuilder.reflectionToString( this )
  }
}
