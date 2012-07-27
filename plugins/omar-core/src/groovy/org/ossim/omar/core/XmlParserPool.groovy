package org.ossim.omar.core

import org.apache.commons.pool.impl.GenericObjectPool

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 7/27/12
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlParserPool
{
  private final GenericObjectPool pool;

  public XmlParserPool( int maxActive )
  {
    pool = new GenericObjectPool( new XmlParserPoolableObjectFactory(), maxActive,
            GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 0 );
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