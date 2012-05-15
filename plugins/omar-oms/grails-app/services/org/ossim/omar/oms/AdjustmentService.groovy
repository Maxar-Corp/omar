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
  def adjustImageParameters(def obsCollection, def report)
  {
      def adjModel = new AdjustmentModel(report)

      def obs = new ossimPointObservation()

      // Observation (obs) loop
      obsCollection.each{pobs->
          obs.setID(pobs.obs.id)
          obs.setGroundSigmas(pobs.obs.sigLat as double, pobs.obs.sigLon as double, pobs.obs.sigHgt as double)
          obs.setGroundPoint(pobs.obs.lat as double, pobs.obs.lon as double, pobs.obs.hgt as double)

          // Measurement (meas) loop
          pobs.meas.each{pmeas->
              obs.addMeasurement(pmeas.x as double, pmeas.y as double, pmeas.filename)
          }

          // Add complete observation to model
          adjModel.addObservation(obs)

          obs.reset();
      }

      obs.delete()
      obs = null

      // Initialize solution
      if ( adjModel.initAdjustment() )
      {
          // Run solution
          adjModel.runAdjustment()
      }

      boolean adjOK = adjModel.isValid()

      adjModel.delete()
      adjModel = null

      // Return solution status
      return [status:adjOK]
  }
}