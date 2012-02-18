package org.ossim.omar.oms

import joms.oms.CoordinateUtility
import joms.oms.WktUtility;

import joms.oms.ossimUnitType;
import joms.oms.ossimGpt;


class CoordinateConversionService
{

  static transactional = true

  String convertToDecimalDegrees(String input)
  {
    def dms = ~/\d{1,3}\s*\d{2}\s*\d{2}(\.\d+)?\s*[NnSsEeWw]$/
    def dd = ~/^-?\d{1,3}(\.\d+)?$/
    String output = null

    //println "convertToDecimalDegrees -> input: ${input}"

    switch ( input )
    {
    case dms:
      def parseDMS = /^(\d{1,3})\s*(\d{2})\s*(\d{2}(\.\d+)?)\s*([NnSsEeWw])$/
      def match = input =~ parseDMS
      def degrees = match[0][1]
      def minutes = match[0][2]
      def seconds = match[0][3]
      def hemisphere = match[0][5]

      def newInput = "${degrees} ${minutes} ${seconds} ${hemisphere}"

      //println newInput


      output = new CoordinateUtility().dmsToDegrees(newInput)


      break
    case dd:
      //println "DD"
      output = input
      break
    default:
      output = null
    }

    //println "convertToDecimalDegrees <- output: ${output}"

    return output
  }

  String computePointRadiusWKT(String centerLon, String centerLat, String aoiRadius)
  {
    WktUtility wktUtil = new WktUtility()


    def centerGpt = new ossimGpt(Double.valueOf(centerLat),
            Double.valueOf(centerLon));

    double radius = Double.valueOf(aoiRadius);
    def unitType = ossimUnitType.OSSIM_METERS
    def wkt = wktUtil.toWktGeometryGivenCenterRadius(centerGpt, radius, unitType, 360, -1)

    return wkt

  }


  String convertToDms(BigDecimal degrees, String format, boolean isLat)
  {
    String dms = new CoordinateUtility().degreesToDms(degrees, format, isLat)

    return dms
  }
}
