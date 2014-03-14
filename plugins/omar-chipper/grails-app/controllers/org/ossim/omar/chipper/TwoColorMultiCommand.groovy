package org.ossim.omar.chipper

import grails.validation.Validateable
import groovy.transform.ToString
import org.ossim.omar.chipper.ChipCommand

/**
 * Created by gpotts on 3/13/14.
 */
@ToString( )
@Validateable( )
class TwoColorMultiCommand extends ChipCommand
{
  String newLayers
  String oldLayers

  static constraints = {
    newLayers(nullable:true, validator:{val, obj->
      def message = true

      if(val == null)
      {
        message = "newLayers must be supplied for algorithm 2 color multi view"
      }
      message
    })
    oldLayers(nullable:true, validator:{val, obj->
      def message = true

      if(val == null)
      {
        message = "oldLayers must be supplied for algorithm 2 color multi view"
      }
      message
    })
  }

  def toMap()
  {
    def result = super.toMap()

    result << [newLayers:newLayers, oldLayers:oldLayers]

    result
  }

}
