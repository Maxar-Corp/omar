package org.ossim.omar.oms

import joms.oms.GeodeticEvaluator
import joms.oms.ossimGpt
import joms.oms.Init


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
        geodeticEvaluatorModel.destroy()
        geodeticEvaluatorModel.delete()
        groundPoint.delete()

        result;
    }
}
