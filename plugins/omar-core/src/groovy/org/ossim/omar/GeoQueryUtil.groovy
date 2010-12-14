package org.ossim.omar

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: Dec 14, 2010
 * Time: 2:43:04 PM
 * To change this template use File | Settings | File Templates.
 */
class GeoQueryUtil {
  static def createClauseFromOgcFilter(def classVariable, String ogcFilter) {
    def result = null
    def fieldTypeMap = org.ossim.omar.Utility.createTypeMap(classVariable)
    try {
      def gtFilter = new geoscript.filter.Filter(ogcFilter).filter
      result = createClauseFromGeotoolsFilter(fieldTypeMap, gtFilter)
    }
    catch (Exception e) {
      println e
      result = null
    }

    result
  }

  static def createClauseFromGeotoolsFilter(def fieldTypeMap, def filter) {
    def result = null
    if (filter instanceof org.geotools.filter.LogicFilter) {
      if (filter instanceof org.geotools.filter.AndImpl) {
        result = new org.hibernate.criterion.Conjunction()
        def temp = filter as org.geotools.filter.BinaryLogicAbstract
        temp.children.each {
          def tempResult = createClauseFromGeotoolsFilter(fieldTypeMap, it)
          if (tempResult) {
            result.add(tempResult)
          }
        }
      }
      else if (filter instanceof org.geotools.filter.OrImpl) {
        def temp = filter as org.geotools.filter.OrImpl
        result = new org.hibernate.criterion.Disjunction()
        temp.children.each {
          def tempResult = createClauseFromGeotoolsFilter(fieldTypeMap, it)
          if (tempResult) {
            result.add(tempResult)
          }
        }
      }
      else if (filter instanceof org.geotools.filter.NotImpl) {
        def notFilter = filter as org.geotools.filter.NotImpl
        def notClause = createClauseFromGeotoolsFilter(fieldTypeMap, notFilter.filter);
        result = org.hibernate.criterion.Restrictions.not(notClause);
      }
    }
    else if (filter instanceof org.geotools.filter.GeometryFilter) {
      if (filter instanceof org.geotools.filter.spatial.BBOXImpl) {
        def bbox = filter as org.geotools.filter.spatial.BBOXImpl
        def srs = bbox.SRS ?: "4326"

        def bounds = new com.vividsolutions.jts.io.WKTReader().read(bbox.expression2.toString())
        bounds?.setSRID(Integer.parseInt(srs))

        result = new org.hibernatespatial.criterion.SpatialFilter(bbox.expression1.toString(), bounds)
      }
    }
    else if (filter instanceof org.geotools.filter.CompareFilter) {
      if (filter instanceof org.geotools.filter.BetweenFilterImpl) {
        def compare = filter as org.geotools.filter.BetweenFilterImpl

        def type = fieldTypeMap."${compare.expression}"?.simpleName
        if (!type) {
          throw new Exception("invalid field name ${compare.expression} for between clause")
        }
        result = org.hibernate.criterion.Restrictions.between(compare.expression.toString(),
                compare.lowerBoundary()."to${type}"(),
                compare.upperBoundary()."to${type}"())
      }
      else if (filter instanceof org.geotools.filter.IsNotEqualToImpl) {
        def compare = filter as org.geotools.filter.IsNotEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} <> ${compare.rightValue}")
          // need to throw exception if type not found
        }
        result = org.hibernate.criterion.Restrictions.ne(paramsFix.leftValue,
                paramsFix.rightValue."to${type}"());
      }
      else if (filter instanceof org.geotools.filter.IsEqualsToImpl) {
        def compare = filter as org.geotools.filter.IsEqualsToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} == ${compare.rightValue}")
          // need to throw exception if type not found
        }
        result = org.hibernate.criterion.Restrictions.eq(paramsFix.leftValue,
                paramsFix.rightValue."to${type}"());
      }
      else if (filter instanceof org.geotools.filter.IsGreaterThanImpl) {
        def compare = filter as org.geotools.filter.IsGreaterThanImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} > ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if (paramsFix.swap == true) {
          result = org.hibernate.criterion.Restrictions.lt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else {
          result = org.hibernate.criterion.Restrictions.gt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
      else if (filter instanceof org.geotools.filter.IsGreaterThanOrEqualToImpl) {
        def compare = filter as org.geotools.filter.IsGreaterThanOrEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} <= ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if (paramsFix.swap == true) {
          result = org.hibernate.criterion.Restrictions.lt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else {
          result = org.hibernate.criterion.Restrictions.gt(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }

      }
      else if (filter instanceof org.geotools.filter.IsLessThenImpl) {
        def compare = filter as org.geotools.filter.IsLessThenImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} < ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if (paramsFix.swap == true) {
          result = org.hibernate.criterion.Restrictions.ge(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
        else {
          result = org.hibernate.criterion.Restrictions.le(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
      else if (filter instanceof org.geotools.filter.IsLessThenOrEqualToImpl) {
        def compare = filter as org.geotools.filter.IsLessThenOrEqualToImpl
        def paramsFix = fixBinaryExpression(fieldTypeMap, compare.leftValue.toString(), compare.rightValue.toString());
        def propSwap = true
        def type = paramsFix.type
        if (!type) {
          throw new Exception("invalid field name in expression ${compare.leftValue} <= ${compare.rightValue}")
          // need to throw exception if type not found
        }
        if (paramsFix.swap == true) {
          result = org.hibernate.criterion.Restrictions.ge(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"())
        }
        else {
          result = org.hibernate.criterion.Restrictions.le(paramsFix.leftValue,
                  paramsFix.rightValue."to${type}"());
        }
      }
    }
    else if (filter instanceof org.geotools.filter.LikeFilterImpl) {
      def likeFilter = filter as org.geotools.filter.LikeFilterImpl
      def type = fieldTypeMap."${likeFilter.expression}"?.simpleName
      if (!type) {
        throw new Exception("invalid field name in expression ${likeFilter.expression} like ${likeFilter.literal}")
      }


      result = org.hibernate.criterion.Restrictions.like(likeFilter.expression.toString(), likeFilter.literal)
    }

    return result
  }

  static def fixBinaryExpression(def fieldMap, def leftValue, def rightValue) {
    def result = [:]

    result.swap = false;
    if (fieldMap."${leftValue}") {
      result.type = fieldMap."${leftValue}"?.simpleName
      result.leftValue = leftValue;
      result.rightValue = rightValue;
    }
    else if (fieldMap."${rightValue}") {
      result.swap = true;
      result.type = fieldMap."${rightValue}"?.simpleName
      result.leftValue = rightValue;
      result.rightValue = leftValue;
    }

    result
  }
}
