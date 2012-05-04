package org.ossim.omar.oms

import joms.oms.AdjustmentModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.ossimColumnVector3d
import joms.oms.ossimString
import joms.oms.ossimFilename
import joms.oms.ossimPointObservation
// temporary
import joms.oms.Init
// temporary

class AdjustmentService
{

  static transactional = false

  /**
   * @param obsCollection
   *  [[obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]],
   *   [obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]],
   *      :
   *   [obs:[id:,lat:,lon:,hgt:,sigLat:,sigLon:,sigHgt:], meas:[[filename:,x:,y:],[filename:,x:,y:],...,[filename:,x:,y:]]]]
   *
   * @return
   */
  def adjustImages(def obsCollection)
  {
      // temporary
      Init.instance().initialize()
      // temporary

      def adjModel = new AdjustmentModel()

      // Observation (obs) loop
      obsCollection.each{pobs->
          def gPt = new ossimGpt(pobs.obs.lat as double,
                                 pobs.obs.lon as double,
                                 pobs.obs.hgt as double)
          def gPtSigs = new ossimColumnVector3d(pobs.obs.sigLat as double,
                                                pobs.obs.sigLon as double,
                                                pobs.obs.sigHgt as double)
          def gPtId = new ossimString(pobs.obs.id)
          def ptObs = new ossimPointObservation(gPt, gPtId, gPtSigs)
          println pobs.obs

          // Measurement (meas) loop
          pobs.meas.each{pmeas->
              def iPt = new ossimDpt(pmeas.x as double, pmeas.y as double)
              def imgFile = new ossimFilename(pmeas.filename)
              ptObs.addMeasurement(iPt, imgFile)
          }

          adjModel.addObservation(ptObs)
      }

      if ( adjModel.initAdjustment() )
      {
          adjModel.runAdjustment()
      }

      adjModel.destroy()
      adjModel.delete()

      return []
  }
}
