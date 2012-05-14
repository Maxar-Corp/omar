package org.ossim.omar.oms

import joms.oms.AdjustmentModel
import joms.oms.ossimPointObservation

class AdjustmentService
{

  static transactional = false


  /**
   * @param obsCollection
   *  [[obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]],
   *   [obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]],
   *      :
   *   [obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]]]
   * @param report   string containing '/path/to/report_file' OR 'cout' to output to screen
   *
   * @return
   */
  def adjustImages2(def obsCollection, def report)
  {
      def adjModel = new AdjustmentModel(report)

      def obsList = new ArrayList()

      // Observation (obs) loop
      obsCollection.each{pobs->
          obsList.add(new ossimPointObservation())
          obsList.last().setID(pobs.obs.id)
          obsList.last().setGroundPoint(pobs.obs.lat as double, pobs.obs.lon as double, pobs.obs.hgt as double,)
          obsList.last().setGroundSigmas(pobs.obs.sigLat as double, pobs.obs.sigLon as double, pobs.obs.sigHgt as double,)
          println pobs.obs

          // Measurement (meas) loop
          pobs.meas.each{pmeas->
              obsList.last().addMeasurement(pmeas.x as double, pmeas.y as double, pmeas.filename)
          }

          // Add complete observation to model
          adjModel.addObservation(obsList.last())
      }

      // Initialize solution
      if ( adjModel.initAdjustment() )
      {
          // Run solution
          adjModel.runAdjustment()
      }

      boolean adjOK = adjModel.isValid()

      obsList.clear()

      adjModel.delete()
      adjModel = null

      // Temporary return for now
      return [status:adjOK]
  }
}