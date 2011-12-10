package org.codehaus.groovy.grails.plugins.filter

import org.codehaus.groovy.grails.plugins.filter.Filter
import org.codehaus.groovy.grails.plugins.filter.FieldType
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.filter.FilterType
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler


/**
 *
 * @author maxwell
 */
class FilterUtils {

    public static final String TEXT = "text"

    public static final String NUMERIC = "numeric"

    public static final String BOOLEAN ="boolean"

    public static final String DATE = "date"

    /**
     * Parse params and create a Filter object
     */
    public static Filter parseParams(def params,GrailsDomainClass domainClass) {

        if(!params.max) params.max = 10
        if(!params.offset) params.offset = 0
        if(!params.order) params.order = "asc"

        Filter filter = new Filter()

        filter.max = params.max.toInteger()
        filter.offset = params.offset.toInteger()
        filter.order = params.order
        filter.sort = params.sort

        filter.filterBean = domainClass.clazz
        filter.filterField = params.filterField
        filter.filterValue = params.filterValue
        filter.filterValue2 = params.filterValue2

        filter.filterCriteria = params.filterCriteria ? FilterType.valueOf(params.filterCriteria) : null

        if(filter.filterCriteria && filter.filterValue) {

            filter.filterFieldClass  = resolveFieldClass(filter.filterBean,filter.filterField)

            parseFilterValues(filter)

        }

        return filter
    }

    /**
     * Get type of filterField
     */
    public static Class resolveFieldClass(Class filterBean,String filterField) {

        String[] fields = filterField.split("[.]")

        if(fields.length == 1) {
            return filterBean.getDeclaredField(filterField).getType()

        } else {

            Class clazz = filterBean

            for(int x = 0;x < fields.length;x++) {
                if(x+1 == fields.length)
                    return clazz.getDeclaredField(fields[x]).getType()
                else
                    clazz = clazz.getDeclaredField(fields[x]).getType()

            }

        }

    }

    /**
     * Get field simple type, that can be: text, numeric, date or boolean.
     */
    public static String resolveFieldType(Class filterBean,String filterField) {
        Class clazz = resolveFieldClass(filterBean,filterField)

        switch (clazz) {
            case String:
                return TEXT

            case Date:
                return DATE

            case Boolean:
                return BOOLEAN

            case boolean:
                return BOOLEAN

            default:
                return NUMERIC

        }

    }

    /**
     * Get all fields that can be filtrate in domain class
     */
    public static List<String> resolveFields(GrailsDomainClass domainClass) {

        List<String> fields = []

        domainClass.persistentProperties.each {
            if (it.isAssociation()) {
                fields.addAll(resolveFields(it.domainClass,it.name))
            } else {
                fields.add(it.name)
            }
        }

        return fields
    }

    /**
     *  Get all fields that can be filtrate in domain class
     *
     */
    public static List<String> resolveFields(GrailsDomainClass domainClass,String fieldName) {

        GrailsDomainClass fieldClass = domainClass.getPropertyByName(fieldName).referencedDomainClass
        List<String> fields = []

        fieldClass.persistentProperties.each {
            fields.add("${fieldName}.${it.name}")
        }

        return fields

    }

    /**
     * Parse primitive values
     */
    private static void parseFilterValues(Filter filter) {

        Class filterFieldClass = filter.filterFieldClass

        try {

            if(filterFieldClass == String) {

                filter.filterValueObj = filter.filterValue
                filter.filterValue2Obj = filter.filterValue2

                return

            } else if(filterFieldClass == int || filterFieldClass == Integer) {

                filter.filterValueObj = filter.filterValue.toInteger()
                if(filter.filterValue2 != null && filter.filterValue2 != "")
                    filter.filterValue2Obj = filter.filterValue2.toInteger()

            } else if(filterFieldClass == long || filterFieldClass == Long) {

                filter.filterValueObj = filter.filterValue.toLong()
                if(filter.filterValue2 != null && filter.filterValue2 != "")
                    filter.filterValue2Obj = filter.filterValue2.toLong()

            }  else if(filterFieldClass == double || filterFieldClass == Double) {

                filter.filterValueObj = filter.filterValue.toDouble()
                if(filter.filterValue2 != null && filter.filterValue2 != "")
                    filter.filterValue2Obj = filter.filterValue2.toDouble()

            } else if(filterFieldClass == float || filterFieldClass == Float) {

                filter.filterValueObj = filter.filterValue.toFloat()
                if(filter.filterValue2 != null && filter.filterValue2 != "")
                    filter.filterValue2Obj = filter.filterValue2.toFloat()

            } else if(filterFieldClass == boolean || filterFieldClass == Boolean) {

                filter.filterValueObj = filter.filterValue.toBoolean()
                filter.filterCriteria = FilterType.EQUALS
            }

            if( filter.filterCriteria == FilterType.LIKE )
                filter.filterCriteria = FilterType.EQUALS


        } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid.${filterFieldClass.simpleName.toLowerCase()}");
        }

    }

}