package org.ossim.omar.oms

import joms.oms.GeodeticEvaluator
import joms.oms.ossimGpt


class GeodeticEvaluationService {

    static transactional = false

    /**
     * @brief Determine MSL height
     * @param groundPointList
     * @return
     */
    def getHgtMSL(def groundPointList)
    {
        def result = [];
        def geodeticEvaluatorModel = new GeodeticEvaluator()
        def groundPoint = new ossimGpt()
        groundPointList.each{pt->
            groundPoint.makeNan()
            groundPoint.latd = pt.lat as double
            groundPoint.lond = pt.lon as double
            if(pt.hgt) {groundPoint.height = pt.hgt}
            double mslHgt = geodeticEvaluatorModel.getHeightMSL(groundPoint)
            result.add([mslHgt:mslHgt])
        }
        geodeticEvaluatorModel.delete()
        geodeticEvaluatorModel = null
        groundPoint.delete()

        result;
    }

    /**
     * @brief Compute distance/azimuth between two points
     * @param pointPair
     *  [pt1:[lat:, lon:, hgt:], pt2:[lat:, lon:, hgt:]]
     * @return [dist:(meters), az12:(deg), az21:(deg)
     */
    def computeEllipsoidalDistAz(def pointPair)
    {
        def geodeticEvaluatorModel = new GeodeticEvaluator()
        def gp1 = new ossimGpt(pointPair.pt1.lat as double,
                pointPair.pt1.lon as double,
                pointPair.pt1.hgt as double)
        def gp2 = new ossimGpt(pointPair.pt2.lat as double,
                pointPair.pt2.lon as double,
                pointPair.pt2.hgt as double)
        double [] daArray = new double[3]

        geodeticEvaluatorModel.computeEllipsoidalDistAz(gp1, gp2, daArray)

        geodeticEvaluatorModel.delete()
        geodeticEvaluatorModel = null
        gp1.delete()
        gp2.delete()

        [dist:daArray[0], az12:Math.toDegrees(daArray[1]), az21:Math.toDegrees(daArray[2])];
    }
}
