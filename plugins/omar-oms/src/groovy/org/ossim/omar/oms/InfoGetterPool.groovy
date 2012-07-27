package org.ossim.omar.oms

import org.apache.commons.pool.impl.GenericObjectPool

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 7/27/12
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
class InfoGetterPool
{
  private final GenericObjectPool pool;

  public InfoGetterPool( int maxActive )
  {
    pool = new GenericObjectPool(
            new InfoGetterPoolableObjectFactory(), maxActive, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 0 );
  }

  public Object borrowObject( ) throws Exception
  {
    return pool.borrowObject();
  }

  public void returnObject( Object obj ) throws Exception
  {
    pool.returnObject( obj );
  }
}
