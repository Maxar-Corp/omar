package org.ossim.omar.oms

import joms.oms.ImageModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.Init

class ProjectionService
{

  static transactional = false

    // Projection with no error propagation
    def imageSpaceToGroundSpace(def filename, def samp, def line, def entryId)
    {

        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(samp, line)
        def groundPoint = new ossimGpt()

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.imageToGround(imagePoint, groundPoint, entryId)
        }
        imageSpaceModel.destroy()
        imageSpaceModel.delete()

        return [lat:groundPoint.latd(),
                lon:groundPoint.lond(),
                hgt:groundPoint.height()];

    }
    /**
     *
     * @param filename
     * @param pointList List of points of
     * @param entryId
     * @return
     */
    def imageSpaceListToGroundSpace(def filename, def pointList, def entryId)
    {
        def result = [];
        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(0.0,0.0);
        def groundPoint = new ossimGpt()
        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            pointList.each{pt->
                imagePoint.x = pt.x as double;
                imagePoint.y = pt.y as double;
                imageSpaceModel.imageToGround(imagePoint,
                                              groundPoint,
                                              entryId) ;
                if(groundPoint.isHgtNan())
                {
                    groundPoint.height = 0.0;
                }
                result.add([x:pt.x, 
                            y:pt.y,
                            lat:groundPoint.latd(),
                            lon:groundPoint.lond(),
                            hgt:groundPoint.height()]);
            }
        }

        result;
    }


    // Projection with RPC error propagation
    //   Error prop input...
    //      probLev probability level (.5,.9,.95)
    //      angInc  angular increment (deg) for image space ellipse points
    //  Error prop output...
    //      ellSamp array of sample coordinates
    //      ellLine array of line coordinates
    //  Returns...
    //      intersected position lat, lon, hgt (deg)
    //      pqeArray [0]   CE
    //               [1]   LE
    //               [2]   ellipse SMA
    //               [3]   ellipse SMI
    //               [4]   ellipse SMA azimuth (rad)
    //               [5]   number of image ellipse points
    def imageSpaceToGroundSpace(String filename, double samp, double line, Integer entryId,
                                double probLev, double angInc, int [] ellSamp, int [] ellLine)
    {

        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(samp, line)
        def groundPoint = new ossimGpt()
        boolean errorPropAvailable = false
        double [] pqeArray = new double[6]

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.imageToGround(imagePoint, groundPoint, entryId)

            // Perform error propagation
            errorPropAvailable =
                imageSpaceModel.imageToGroundErrorPropagation(groundPoint, probLev, angInc, pqeArray, ellSamp, ellLine)
        }
        imageSpaceModel.destroy()
        imageSpaceModel.delete()

        return [groundPoint.latd(),
                groundPoint.lond(),
                groundPoint.height(),
                pqeArray[0],
                pqeArray[1],
                pqeArray[2],
                pqeArray[3],
                pqeArray[4],
                pqeArray[5]
        ]

    }
}
