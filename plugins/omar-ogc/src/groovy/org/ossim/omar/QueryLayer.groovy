package org.ossim.omar

import geoscript.filter.Filter

import org.geotools.data.Query
import org.geotools.feature.FeatureCollection

import org.geotools.data.Transaction
import org.geotools.data.DefaultTransaction
import geoscript.workspace.Database
import geoscript.style.Style

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 3/11/11
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
class QueryLayer
{
  Query query
  Database workspace
  String typeName
  Style style

  QueryLayer(Database workspace, String typeName, Filter filter)
  {
    this.workspace = workspace
    this.typeName = typeName
    query = new Query(typeName, filter.filter)
  }

  FeatureCollection getResults()
  {
//    Transaction t = new DefaultTransaction("getResults")
    def results = null

    try
    {
      //def featureSource = workspace.ds.getFeatureSource(typeName, t)
      def featureSource = workspace.ds.getFeatureSource(typeName)
      //featureSource.transaction = t
      results = featureSource.getFeatures(query)
      //t.commit()
    }
    //println results.size()
    catch (Exception e)
    {
      e.printStackTrace()
      //t.rollback()
    }
    finally
    {
//      t.close()
      //featureSource.transaction = Transaction.AUTO_COMMIT
    }

    return results
  }
}