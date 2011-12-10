/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codehaus.groovy.grails.plugins.filter

import org.codehaus.groovy.grails.plugins.filter.FilterType as Type
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Respresents a filter
 * @author maxwell
 */
class Filter
{

  // domain class
  Class filterBean

  // the field class
  Class filterFieldClass

  // field name
  String filterField

  // criteria type
  Type filterCriteria

  // raw values from view
  String filterValue
  String filterValue2

  // formatted filter value
  def filterValueObj
  def filterValue2Obj

  // paginate
  int max
  int offset

  // sort and order
  String order
  String sort

  /**
   * Create and return a filter query, to be used with Criteria.list() method
   *
   */
  public def list()
  {

    String[] fields = filterField.split("[.]")

    def c = filterBean.createCriteria()

    def _query = ""

    if ( sort )
      _query += "order(\'${sort}\',\'${order}\')\n"

    _query += "maxResults(${max})\n"
    _query += "firstResult(${offset})\n"

    int close = 0
    for ( int x = 0; x < fields.length; x++ )
    {

      if ( x + 1 == fields.length )
      {

        switch ( filterCriteria )
        {
        case FilterType.EQUALS:
          _query += "eq(\'${fields[x]}\',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.LIKE:
          _query += "ilike('${fields[x]}',"
          _query += "'%${filterValueObj}%'"
          _query += ")\n"
          break
        case FilterType.GREATER:
          _query += "ge('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.LESS:
          _query += "le('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.BETWEEN:
          _query += "between('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ","
          _query += (filterFieldClass == String) ? "'${filterValue2Obj}'" : "${filterValue2Obj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        }


      }
      else
      {
        _query += "${fields[x]} {\n"
        close++
      }
    }

    for ( int x = 0; x < close; x++ )
    {
      _query += "}\n"
    }

    GroovyShell gs = new GroovyShell()
    return gs.evaluate("return { ${_query} }")

  }

  /**
   * Create and return a filter to get total count for this filter
   */
  public def count()
  {

    String[] fields = filterField.split("[.]")

    def c = filterBean.createCriteria()

    def _query = ""

    int close = 0
    for ( int x = 0; x < fields.length; x++ )
    {

      if ( x + 1 == fields.length )
      {

        switch ( filterCriteria )
        {
        case FilterType.EQUALS:
          _query += "eq(\'${fields[x]}\',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.LIKE:
          _query += "ilike('${fields[x]}',"
          _query += "'%${filterValueObj}%'"
          _query += ")\n"
          break
        case FilterType.GREATER:
          _query += "ge('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.LESS:
          _query += "le('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        case FilterType.BETWEEN:
          _query += "between('${fields[x]}',"
          _query += (filterFieldClass == String) ? "'${filterValueObj}'" : "${filterValueObj} as ${filterFieldClass.name}"
          _query += ","
          _query += (filterFieldClass == String) ? "'${filterValue2Obj}'" : "${filterValue2Obj} as ${filterFieldClass.name}"
          _query += ")\n"
          break
        }

      }
      else
      {
        _query += "${fields[x]} { \n"
        close++
      }
    }

    for ( int x = 0; x < close; x++ )
    {
      _query += "}\n"
    }

    _query += " projections { \n rowCount() \n } \n "

    GroovyShell gs = new GroovyShell()
    return gs.evaluate("return { ${_query} }")

  }

}

