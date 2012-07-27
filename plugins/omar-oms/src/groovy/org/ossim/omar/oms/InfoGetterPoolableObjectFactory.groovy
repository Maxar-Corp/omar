package org.ossim.omar.oms

import org.apache.commons.pool.PoolableObjectFactory

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 7/27/12
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
class InfoGetterPoolableObjectFactory implements PoolableObjectFactory
{
  public void destroyObject(Object infoGetter)
  {
    infoGetter?.cleanup()
    infoGetter = null
  }

  public void activateObject(Object infoGetter)
  {
  }

  public void passivateObject(Object infoGetter)
  {
  }

  public Object makeObject()
  {
    return new InfoGetter()
  }

  public boolean validateObject(Object infoGetter)
  {
    return true
  }
}
