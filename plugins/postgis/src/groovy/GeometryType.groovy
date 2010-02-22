/*
 * GeometryType.java
 * 
 * PostGIS extension for PostgreSQL JDBC driver - EJB3 Tutorial
 * 
 * (C) 2006  Norman Barker <norman.barker@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or visit the web at
 * http://www.gnu.org.
 * 
 * $Id: GeometryType.java 2531 2006-11-22 10:42:17Z mschaber $
 */
//package org.postgis.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * @author nbarker $date 16/8/06
 */
public class GeometryType implements UserType
{
  private static final int[] SQL_TYPES = [Types.BLOB];


  /**
   * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
   */
  public Object deepCopy(Object value) throws HibernateException
  {
    return value;
  }

  /**
   * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
   */
  public boolean equals(Object x, Object y) throws HibernateException
  {
    if ( x == y )
    {
      return true;
    }
    else if ( x == null || y == null )
    {
      return false;
    }
    else
    {
      return x.equals(y);
    }
  }

  /**
   * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
   */
  public int hashCode(Object arg0) throws HibernateException
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * @see org.hibernate.usertype.UserType#isMutable()
   */
  public boolean isMutable()
  {
    return false;
  }

  /**
   * )
   *
   * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String [], java.lang.Object)
   */
  public Object nullSafeGet(ResultSet resultSet,
                            String[] names, Object owner) throws HibernateException, SQLException
  {
    Geometry result = null;
    String geom = resultSet.getString(names[0]);
    if ( geom != null )
    {
      org.postgis.binary.BinaryParser parser = new org.postgis.binary.BinaryParser();
      result = new Geometry( geom:parser.parse(geom) );
    }
    return result;
  }

  /**
   * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
   */
  public void nullSafeSet(PreparedStatement statement,
                          Object value, int index) throws HibernateException, SQLException
  {   
    if ( value == null )
    {
      statement.setBytes(index, null);
    }
    else
    {
      org.postgis.binary.BinaryWriter bw = new org.postgis.binary.BinaryWriter();

      byte[] bytes = bw.writeBinary((org.postgis.Geometry)((Geometry)value).geom);
      statement.setBytes(index, bytes);
    }
  }

  /* (non-Javadoc)
    * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
    */

  public Object replace(Object original, Object target,
                        Object owner) throws HibernateException
  {
    return original;
  }

  /* (non-Javadoc)
    * @see org.hibernate.usertype.UserType#returnedClass()
    */

  public Class returnedClass()
  {
    return Geometry.class;
  }

  /**
   * @see org.hibernate.usertype.UserType#sqlTypes()
   */
  public int[] sqlTypes()
  {
    return GeometryType.SQL_TYPES;
  }

  /**
   * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
   */
  public Object assemble(Serializable cached, Object owner) throws HibernateException
  {
    return cached;
  }

  /**
   * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
   */
  public Serializable disassemble(Object value) throws HibernateException
  {
    return (Serializable) value;
  }

}

