package org.ossim.omar.oms

import joms.oms.ImageModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.ossimEcefPoint
import joms.oms.Init
import geoscript.geom.Geometry

class ProjectionService
{

  static transactional = false

    /**
     * @brief Single-ray projection with no error propagation
     * @param filename
     * @param samp
     * @param line
     * @param entryId
     * @return
     */
    def imageSpaceToGroundSpace(def filename, def samp, def line, def entryId)
    {
        Init.instance().initialize()


        def result;
        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(samp, line)
        def groundPoint = new ossimGpt()

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.imageToGround(imagePoint, groundPoint)
        }
        imageSpaceModel.destroy()
        imageSpaceModel.delete()


        result = [lat:groundPoint.latd(),
                lon:groundPoint.lond(),
                hgt:groundPoint.height()];

        groundPoint.delete();
        groundPoint = null;
        result;
    }
    /**
     *
     * @param params should contain a filename, entryId and a wkt variable
     * @return
     */
    def imageSpaceWKTMeasure(def params)
    {
        def geom = null
        def imagePoint = new ossimDpt(0.0, 0.0)
        def groundPoint = new ossimGpt()
        def ecefPoint = new ossimEcefPoint()
        def lastGroundPoint;
        def distance = 0.0;
        def area     = 0.0;
        def result = [distance:0.0, area: 0.0, baseUnit: "m"];
        def coordinateList = []
        try{
            geom = Geometry.fromWKT(params.wkt);
        }
        catch(def e)
        {

        }
        if(geom)
        {
            distance = 0.0;
            area     = 0.0;
            def imageSpaceModel = new ImageModel()
            if( imageSpaceModel.setModelFromFile(params.filename, params.entryId) )
            {
                def coordinates = geom.getCoordinates();
                coordinates.each{pt->
                    imagePoint.x = pt.x;
                    imagePoint.y = pt.y;
                    imageSpaceModel.imageToGround(imagePoint, groundPoint);
                    if(lastGroundPoint)
                    {
                        distance += lastGroundPoint.distanceTo(groundPoint);
                        lastGroundPoint.assign(groundPoint);
                        ecefPoint.assign(groundPoint);
                        coordinateList << [ecefPoint.x, ecefPoint.y, ecefPoint.z];
                    }
                    else
                    {
                        lastGroundPoint = new ossimGpt(groundPoint);
                        ecefPoint.assign(lastGroundPoint);
                        coordinateList << [ecefPoint.x, ecefPoint.y, ecefPoint.z];
                    }
                    //coordinateList << coordinateList[0];
               }
                // add area calculations
                if(geom instanceof geoscript.geom.Polygon)
                {
                    def tempPoly = new geoscript.geom.Polygon([coordinateList])
                    area = tempPoly.area;
                }
                result.distance = distance
                result.area     = area
          }
          imageSpaceModel.delete()
          imageSpaceModel = null
        }
        imagePoint.delete()
        groundPoint.delete()
        ecefPoint.delete()
        if(lastGroundPoint) lastGroundPoint.delete();
        result;
    }
    /**
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
                                              groundPoint) ;
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
    /**
     * @brief Single-ray projection with RPC error propagation
     * @param filename
     * @param samp
     * @param line
     * @param entryId
     * @param probLev probability level (.5,.9,.95)
     * @param angInc angular increment (deg) for image space ellipse points
     * @return
     */
    def imageSpaceToGroundSpace(def filename, def samp, def line, def entryId, def probLev, def angInc)
    {
        def result = [];
        def ellPts = [];

        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(samp, line)
        def groundPoint = new ossimGpt()
        boolean errorPropAvailable = false

        int numPnts = 360/angInc + 1
        double [] ellSamp = new double[numPnts]
        double [] ellLine = new double[numPnts]
        double [] pqeArray = new double[6]

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.imageToGround(imagePoint, groundPoint)
            if(groundPoint.isHgtNan())
            {
                groundPoint.height = 0.0;
            }

            // Perform error propagation
            errorPropAvailable =
                imageSpaceModel.imageToGroundErrorPropagation(groundPoint,
                                                              probLev as double,
                                                              angInc as double,
                                                              pqeArray,
                                                              ellSamp,
                                                              ellLine)
        }
        imageSpaceModel.destroy()
        imageSpaceModel.delete()

        if (errorPropAvailable)
        {
            for(int i = 0; i < numPnts; i++){
                ellPts << [xe: ellSamp[i], ye: ellLine[i]]
            }

            result = [x: samp,
                      y: line,
                      lat:  groundPoint.latd(),
                      lon:  groundPoint.lond(),
                      hgt:  groundPoint.height(),
                      CE:   pqeArray[0],
                      LE:   pqeArray[1],
                      SMA:  pqeArray[2],
                      SMI:  pqeArray[3],
                      AZ:   Math.toDegrees(pqeArray[4]),
                      lvl:  probLev,
                      nELL: pqeArray[5]]
        }
        else
        {
            result = [x: samp,
                      y: line,
                      lat:  groundPoint.latd(),
                      lon:  groundPoint.lond(),
                      hgt:  groundPoint.height()]
        }

        groundPoint.delete();
        groundPoint = null;

        [ellpar: result, ellpts: ellPts]
    }
}
