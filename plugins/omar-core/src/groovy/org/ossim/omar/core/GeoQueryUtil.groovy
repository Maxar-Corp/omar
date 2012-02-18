package org.ossim.omar.core

import org.hibernatespatial.SpatialRelation
/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: Dec 14, 2010
 * Time: 2:43:04 PM
 * To change this template use File | Settings | File Templates.
 */
class GeoQueryUtil
{
  /**
   *
   * @param fieldTypeMap Is in the format given by org.ossim.omar.Utility.createTypeMap(classVariable)
   * @param ogcFilter ogcFilter pass in the Filter string defined in the WFS OGC specification
   * @return hibernate criteria filter restrictions
   */
  static def createClauseFromOgcFilter(Map fieldTypeMap, String ogcFilter)
  {
    def result = null
//    try {
    def gtFilter = new geoscript.filter.Filter(ogcFilter).filter
    result = createClauseFromGeotoolsFilter(fieldTypeMap, gtFilter)
//    }
    //    catch (Exception e) {
    //      println e
    //      result = null
    //    }

    result
  }
  /**
   *
   * @param classVariable pass a class variable in, ex. Foo.class
   * @param ogcFilter pass in the Filter string defined in the WFS OGC specification
   * @return hibernate criteria filter restrictions
   */
  static def createClauseFromOgcFilter(def classVariable, String ogcFilter)
  {
    def result = null
    def fieldTypeMap = Utility.createTypeMap(classVariable)
//    try {
    def gtFilter = new geoscript.filter.Filter(ogcFilter).filter
    result = createClauseFromGeotoolsFilter(fieldTypeMap, gtFilter)
//    }
    //    catch (Exception e) {
    //      println e
    //      result = null
    //    }

    result
  }

  static def createClauseFromGeotoolsFilter(def fieldTypeMap, def filter, def caseInsensitiveFlag = true)
  {
    def result = null
    if ( filter instanceof org.geotools.filter.LogicFilter )
    {
      if ( filter instanceof org.geotools.filter.AndImpl )
      {
        result = new org.hibernate.criterion.Conjunction()
        def temp = filter as org.geotools.filter.BinaryLogicAbstract
        temp.children.each {
          def tempResult = createClauseFromGeotoolsFilter(fieldTypeMap, it)
          if ( tempResult )
          {
            result.add(tempResult)
          }
        }
      }
      else if ( filter instanceof org.geotools.filter.OrImpl )
      {
        def temp = filter as org.geotools.filter.OrImpl
        result = new org.hibernate.criterion.Disjunction()
        temp.children.each {
          def tempResult = createClauseFromGeotoolsFilter(fieldTypeMap, it)
          if ( tempResult )
          {
            result.add(tempResult)
          }
        }
      }
      else if ( filter instanceof org.geotools.filter.NotImpl )
      {
        def notFilter = filter as org.geotools.filter.NotImpl
        def notClause = createClauseFromGeotoolsFilter(fieldTypeMap, notFilter.filter);
        result = org.hibernate.criterion.Restrictions.not(notClause);
      }
      else
      {
        throw new Exception("Unsupported Logic filter ${filter}")
      }
    }
    else if ( filter instanceof org.geotools.filter.GeometryFilter )
    {
      def srs = "4326"
      if ( filter instanceof org.geotools.filter.spatial.BBOXImpl )
      {
        def bbox = filter as org.geotools.filter.spatial.BBOXImpl
        srs = bbox.SRS ?: "4326"

        def geom = new com.vividsolutions.jts.io.WKTReader().read(bbox.expression2.toString())
        geom?.setSRID(Integer.parseInt(srs))

        result = new org.hibernatespatial.criterion.SpatialFilter(fixField(bbox.expression1.toString()), geom)
      }
      else if ( filter instanceof org.geotools.filter.spatial.EqualsImpl )
      {
        def equalsFilter = filter as org.geotools.filter.spatial.EqualsImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, equalsFilter.expression1.toString(),
                equalsFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.EQUALS)
      }
      else if ( filter instanceof org.geotools.filter.spatial.WithinImpl )
      {
        def withinFilter = filter as org.geotools.filter.spatial.WithinImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, withinFilter.expression1.toString(),
                withinFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.WITHIN)
      }
      else if ( filter instanceof org.geotools.filter.spatial.ContainsImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.ContainsImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.CONTAINS)
      }
      else if ( filter instanceof org.geotools.filter.spatial.CrossesImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.CrossesImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.CROSSES)
      }
      else if ( filter instanceof org.geotools.filter.spatial.DisjointImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.DisjointImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                null,
                geom,
                SpatialRelation.DISJOINT)
      }
      else if ( filter instanceof org.geotools.filter.spatial.IntersectsImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.IntersectsImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.INTERSECTS)
      }
      else if ( filter instanceof org.geotools.filter.spatial.OverlapsImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.OverlapsImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.OVERLAPS)
      }
      else if ( filter instanceof org.geotools.filter.spatial.TouchesImpl )
      {
        def tempFilter = filter as org.geotools.filter.spatial.OverlapsImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, tempFilter.expression1.toString(),
                tempFilter.expression2.toString());
        def geom = new com.vividsolutions.jts.io.WKTReader().read(paramsFix.rightValue)
        geom?.setSRID(Integer.parseInt(srs))
        result = new org.hibernatespatial.criterion.SpatialRelateExpression(paramsFix.leftValue,
                geom,
                geom,
                SpatialRelation.TOUCHES)
      }
      else
      {
        throw new Exception("Unsupported Geometry filter ${filter}")
      }
    }
    else if ( filter instanceof org.geotools.filter.CompareFilter )
    {
      if ( filter instanceof org.geotools.filter.BetweenFilterImpl )
      {
        def compare = filter as org.geotools.filter.BetweenFilterImpl
        def paramFix = fixField(compare.expression.toString())
        def type = fieldTypeMap."${paramFix}"?.simpleName
        if ( !type )
        {
          throw new Exception("invalid field name ${compare.expression} for between clause")
        }
        result = org.hibernate.criterion.Restrictions.between(paramFix,
                compare.lowerBoundary().toString()."to${type}"(),
                compare.upperBoundary().toString()."to${type}"())
      }
      else if ( filter instanceof org.geotools.filter.IsBetweenImpl )
      {
        def compare = filter as org.geotools.filter.IsBetweenImpl
        def paramFix = fixField(compare.expression.toString())
        def type = fieldTypeMap."${paramFix}"?.simpleName
        if ( !type )
        {
          throw new Exception("invalid field name ${compare.expression} for between clause")
        }
        result = org.hibernate.criterion.Restrictions.between(paramFix,
                compare.getLowerBoundary().toString()."to${type}"(),
                compare.getUpperBoundary().toString()."to${type}"())
      }
      else if ( filter instanceof org.geotools.filter.IsNotEqualToImpl )
      {
        def compare = filter as org.geotools.filter.IsNotEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} <> ${compare.rightValue}")
          // need to throw exception if type not found
        }
        result = org.hibernate.criterion.Restrictions.ne(paramsFix.leftValue,
                paramsFix.rightValue."to${type}"());
      }
      else if ( filter instanceof org.geotools.filter.IsEqualsToImpl )
      {
        def compare = filter as org.geotools.filter.IsEqualsToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} == ${compare.rightValue}")
          // need to throw exception if type not found
        }
        result = org.hibernate.criterion.Restrictions.eq(paramsFix.leftValue,
                paramsFix.rightValue."to${type}"());
      }
      else if ( filter instanceof org.geotools.filter.IsGreaterThanImpl )
      {
        def compare = filter as org.geotools.filter.IsGreaterThanImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} > ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if ( paramsFix.swap == true )
        {
          result = org.hibernate.criterion.Restrictions.lt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else
        {
          result = org.hibernate.criterion.Restrictions.gt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
      else if ( filter instanceof org.geotools.filter.IsGreaterThanOrEqualToImpl )
      {
        def compare = filter as org.geotools.filter.IsGreaterThanOrEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} <= ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if ( paramsFix.swap == true )
        {
          result = org.hibernate.criterion.Restrictions.le(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else
        {
          result = org.hibernate.criterion.Restrictions.ge(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }

      }
      else if ( filter instanceof org.geotools.filter.IsLessThenImpl )
      {
        def compare = filter as org.geotools.filter.IsLessThenImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} < ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if ( paramsFix.swap == true )
        {
          result = org.hibernate.criterion.Restrictions.gt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else
        {
          result = org.hibernate.criterion.Restrictions.lt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
      else if ( filter instanceof org.geotools.filter.IsLessThenOrEqualToImpl )
      {
        def compare = filter as org.geotools.filter.IsLessThenOrEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def propSwap = true
        def type = paramsFix.type
        if ( !type )
        {
          throw new Exception("invalid field name in expression ${compare.leftValue} <= ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if ( paramsFix.swap == true )
        {
          result = org.hibernate.criterion.Restrictions.ge(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"())
        }
        else
        {
          result = org.hibernate.criterion.Restrictions.le(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
      else if ( filter instanceof org.geotools.filter.IsNullImpl )
      {
        def nullFilter = filter as org.geotools.filter.IsNullImpl
        result = org.hibernate.criterion.Restrictions.isNull(fixField(nullFilter.expression.toString()))
      }
      else
      {
        throw new Exception("Unsupported Compare filter ${filter} of class type ${filter.class}")
      }
    }
    else if ( filter instanceof org.geotools.filter.LikeFilterImpl )
    {
      def likeFilter = filter as org.geotools.filter.LikeFilterImpl
      def adjustedField = fixField(likeFilter.expression.toString())
      def type = fieldTypeMap."${adjustedField}"?.simpleName
      if ( !type )
      {
        throw new Exception("invalid field name in expression ${likeFilter.expression} like ${likeFilter.literal}")
      }

      result = caseInsensitiveFlag ? org.hibernate.criterion.Restrictions.ilike(adjustedField, likeFilter.literal) :
        org.hibernate.criterion.Restrictions.like(adjustedField, likeFilter.literal)
    }

    else
    {
      throw new Exception("Unsupported filter ${filter}")
    }


    return result
  }

  static def fixField(def field)
  {
    def result = field.toLowerCase()
    // convert to standard column naming convention firs
    result = result.replaceAll("_[a-zA-Z]", { value -> value[1].toUpperCase()})
    result
  }

  static def fixBinaryExpression(def fieldMap, def leftValue, def rightValue)
  {
    def result = [:]
    result.swap = false;
    def tempLeft = fixField(leftValue)
    def tempRight = fixField(rightValue)

    if ( fieldMap."${tempLeft}" )
    {
      result.type = fieldMap."${tempLeft}"?.simpleName
      result.leftValue = tempLeft;
      result.rightValue = rightValue;
    }
    else if ( fieldMap."${tempRight}" )
    {
      result.swap = true;
      result.type = fieldMap."${tempRight}"?.simpleName
      result.leftValue = tempRight;
      result.rightValue = leftValue;
    }

    result
  }
}
