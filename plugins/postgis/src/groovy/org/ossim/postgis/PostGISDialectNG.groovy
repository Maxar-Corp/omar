package org.ossim.postgis
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 29, 2009
 * Time: 2:34:53 PM
 * To change this template use File | Settings | File Templates.
 */
class PostGISDialectNG extends PostGISDialect
{
  public boolean supportsInsertSelectIdentity()
  {
    return true;
  }

  public String appendIdentitySelectToInsert(String insertString)
  {
    return insertString + " RETURNING id";
  }
}
