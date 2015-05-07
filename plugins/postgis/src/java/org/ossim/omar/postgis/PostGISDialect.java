package org.ossim.omar.postgis;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 1/24/13
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostGISDialect extends PostgisDialect
{
  @Override
  protected void registerTypesAndFunctions()
  {
    super.registerTypesAndFunctions();

/*
    registerFunction("collect", new StandardSQLFunction("st_collect",
        geometryCustomType));

    registerFunction("geomfromtext", new StandardSQLFunction("st_geomfromtext",
        geometryCustomType));
*/
  }
}
