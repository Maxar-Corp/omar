package org.ossim.omar.oms

import joms.oms.ImageModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.ossimEcefPoint
import joms.oms.ossimString
import joms.oms.GeodeticEvaluator
import joms.oms.ossimElevationAccuracyInfo
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


        if(groundPoint.isHgtNan())
        {
            groundPoint
        }
        result = [lat:groundPoint.latd(),
                lon:groundPoint.lond(),
                hgt:groundPoint.height()];

        groundPoint.delete();
        groundPoint = null;
        result;
    }
    /**
     * @brief Single-ray projection ground to image
     * @param filename
     * @param geoPoint (lat,lon,hgt)
     * @param entryId
     * @return
     */
    def groundSpaceToImageSpace(def filename, def geoPoint, def entryId)
    {
        def result;
        def imageSpaceModel = new ImageModel()
        def groundPoint = new ossimGpt(geoPoint.lat, geoPoint.lon, geoPoint.hgt)
        def imagePoint = new ossimDpt(0.0, 0.0)

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.groundToImage(groundPoint, imagePoint, entryId)
        }
        imageSpaceModel.destroy()
        imageSpaceModel.delete()


        result = [x:imagePoint.x,
                  y:imagePoint.y];

        imagePoint.delete();
        imagePoint = null;
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
        def gdist = 0.0;
        def area = 0.0;
        def result = [gdist:0.0, distance:0.0, area: 0.0, unit: "m", azimuth: 0.0];
        def coordinateList = []
        def geodeticEvaluator = new GeodeticEvaluator()
        double [] daArray = new double[3]

        try{
            geom = Geometry.fromWKT(params.wkt);
        }
        catch(def e)
        {

        }
        if(geom)
        {
            distance = 0.0;
            gdist = 0.0;
            area = 0.0;
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
                        // Linear distance
                        distance += lastGroundPoint.distanceTo(groundPoint);

                        // Geodetic distance & azimuth
                        geodeticEvaluator.computeEllipsoidalDistAz(lastGroundPoint, groundPoint, daArray)
                        gdist += daArray[0]

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
                }

                // Add area calculations
                if(geom instanceof geoscript.geom.Polygon)
                {
                    def tempPoly = new geoscript.geom.Polygon([coordinateList])
                    area = tempPoly.area;
                }
                result.gdist = gdist
                result.distance = distance
                result.area = area
                result.azimuth = daArray[1]
          }
          imageSpaceModel.delete()
          imageSpaceModel = null
        }
        imagePoint.delete()
        groundPoint.delete()
        ecefPoint.delete()
        geodeticEvaluator.delete()
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
        def geodeticEvaluator = new GeodeticEvaluator()
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

                Double hgtMsl = geodeticEvaluator.getHeightMSL(groundPoint);
                if(hgtMsl.naN)
                {
                    hgtMsl = 0.0;
                }
                result.add([x:pt.x,
                            y:pt.y,
                            lat:groundPoint.latd(),
                            lon:groundPoint.lond(),
                            hgt:groundPoint.height(),
                            hgtMsl:hgtMsl]);
            }
        }

        result;
    }

    /**
     * @param filename
     * @param pointList List of points of
     * @param entryId
     * @return
     */
    def groundSpaceListToImageSpace(def filename, def pointList, def entryId)
    {
        def result = [];
        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(0.0,0.0);
        def groundPoint = new ossimGpt()
        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            pointList.each{pt->
                groundPoint.makeNan();
                groundPoint.latd = pt.lat as double;
                groundPoint.lond = pt.lon as double;
                if(pt.hgt)
                {
                  groundPoint.height = pt.hgt;
                }
                imageSpaceModel.groundToImage(groundPoint,
                                              imagePoint) ;
                if(imagePoint.hasNans())
                {
                    imagePoint.x = 0;
                    imagePoint.y = 0;
                }
                if(groundPoint.isHgtNan())
                {
                    groundPoint.height = 0.0;
                }
                result.add([x:imagePoint.x,
                            y:imagePoint.y,
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
     * @param imgPt
     * @param probLev probability level (.5,.9,.95)
     * @param angInc angular increment (deg) for image space ellipse points
     * @param entryId
     * @return
     */
    def imageSpaceToGroundSpace(def filename, def imgPt, def probLev, def angInc, def entryId)
    {
        def result = [];
        def ellPts = [];

        def imageSpaceModel = new ImageModel()
        def geodeticEvaluator = new GeodeticEvaluator()
        def imagePoint = new ossimDpt(imgPt.x as double, imgPt.y as double)
        def groundPoint = new ossimGpt()
        boolean errorPropAvailable = false

        boolean surfaceInfoAvailable = false
        def accuracyInfo = new ossimElevationAccuracyInfo()
        def typeString   = new ossimString()
        String projType

        int numPnts = 360/angInc + 1
        double [] ellSamp = new double[numPnts]
        double [] ellLine = new double[numPnts]
        double [] pqeArray = new double[6]
        String hgtMSL
        String hgtHAE

        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
        {
            // Perform projection
            imageSpaceModel.imageToGround(imagePoint, groundPoint)
            if(groundPoint.isHgtNan())
            {

                Double ellipsHeight =  geodeticEvaluator.getHeightEllipsoid(groundPoint)
                if(!ellipsHeight.naN)
                {
                    def hgtM = geodeticEvaluator.getHeightMSL(groundPoint)
                    def hgtE   = ellipsHeight
                    groundPoint.height = ellipsHeight
                    hgtHAE = Double.toString(hgtE)
                    hgtMSL = Double.toString(hgtM)
                }
                else
                {
                    hgtHAE = "---";
                    hgtMSL = "---";
                    groundPoint.height = 0.0
                }
            }
            else
            {
                def hgtE = groundPoint.height()
                def hgtM = geodeticEvaluator.getHeightMSL(groundPoint)
                hgtHAE = Double.toString(hgtE)
                hgtMSL = Double.toString(hgtM)
            }


            // Get projection info
            typeString = imageSpaceModel.getType()
            projType = typeString
            projType = projType.minus("ossim")

            // Perform error propagation
            errorPropAvailable =
                imageSpaceModel.imageToGroundErrorPropagation(groundPoint,
                                                              probLev as double,
                                                              angInc as double,
                                                              pqeArray,
                                                              ellSamp,
                                                              ellLine)
        }

        // Get surface info
        surfaceInfoAvailable = imageSpaceModel.getProjSurfaceInfo(groundPoint, accuracyInfo)
        String info
        info = accuracyInfo.surfaceName
        accuracyInfo.delete()
        accuracyInfo = null
        if (!surfaceInfoAvailable)
        {
            info = "NO SURFACE INFO"
        }

        if (errorPropAvailable)
        {
            for(int i = 0; i < numPnts; i++){
                ellPts << [xe: ellSamp[i], ye: ellLine[i]]
            }

            result = [x:      imgPt.x,
                      y:      imgPt.y,
                      lat:    groundPoint.latd(),
                      lon:    groundPoint.lond(),
                      hgt:    hgtHAE,
                      type:   projType,
                      hgtMsl: hgtMSL,
                      sInfo:  info,
                      CE:     pqeArray[0],
                      LE:     pqeArray[1],
                      SMA:    pqeArray[2],
                      SMI:    pqeArray[3],
                      AZ:     Math.toDegrees(pqeArray[4]),
                      lvl:    probLev,
                      nELL:   pqeArray[5]]
        }
        else
        {
            result = [x:      imgPt.x,
                      y:      imgPt.y,
                      lat:    groundPoint.latd(),
                      lon:    groundPoint.lond(),
                      hgt:    hgtHAE,
                      type:   projType,
                      hgtMsl: hgtMSL,
                      sInfo:  info];
        }

        imageSpaceModel.delete()
        geodeticEvaluator.delete()
        groundPoint.delete();
        groundPoint = null;

        [ellpar: result, ellpts: ellPts]
    }
}
