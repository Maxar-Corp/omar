package org.codehaus.groovy.grails.plugins.filter.service



import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.plugins.filter.Filter
import org.codehaus.groovy.grails.plugins.filter.FilterType
import org.apache.commons.lang.StringUtils as SU
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.filter.FilterUtils

/**
 * FilterService
 * @author maxwell
 */
class FilterService
{

  def grailsApplication

  /**
   *
   * Does a filter with specified values in request params.
   * Returns a map with filter options, model and total count, in format 'classTotal' for count, and 'class'List for model,
   * for example to domain class Book the map values are: [bookTotal:TOTAL_VALUE,bookList:LIST_OBJECT]
   *
   */
  def filter(def params)
  {

    //TODO move to FilterUtils
    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, params.filterBean);

    //TODO Filter filter = FilterUtils.parseParams(params)
    Filter filter = FilterUtils.parseParams(params, domainClass)

    if ( !filter.filterValue )
      return list(filter, params)

    String clazzName = domainClass.name //filter.filterBean.name
    String list = "${SU.uncapitalize(clazzName)}InstanceList".toString()
    String count = "${SU.uncapitalize(clazzName)}InstanceTotal".toString()

    def result = [:]

    def query = filter.filterBean.createCriteria()
    def countQuery = filter.filterBean.createCriteria()


    def foo1 = filter.list()
    def foo2 = query.list(foo1)
    result.put(list, foo2)

    result.put(count, countQuery.get(filter.count()))
    result.putAll(params)

    return result

  }

  /**
   * Simple list without criteria, the same of DomainClass.list.
   * Return a map with total with key in the format 'class'Total and the model with key: 'class'List,
   * for example to domain class Book the map values are: [bookTotal:TOTAL_VALUE,bookList:LIST_OBJECT]
   */
  private def list(Filter filter, def params)
  {

    Class clazz = filter.filterBean
    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, clazz.name);
    String clazzName = domainClass.propertyName //filter.filterBean.name

    def result = [:]

    String total = "${SU.uncapitalize(clazzName)}InstanceTotal".toString()
    result.put(total, clazz.count())

    String list = "${SU.uncapitalize(clazzName)}InstanceList".toString()
    result.put(list, clazz.list(params))

    return result

  }

}
