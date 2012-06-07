package org.ossim.omar.core

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class DomainClassLookupService
{
  def sessionFactory
  def grailsApplication

  def createTypeMapping( def className )
  {
    def mappingList = []

    //get the class object named the SampleDomainClass domain class
    def domainClass = grailsApplication.getClassForName( className )
    //get hibernage meta data object
    def hibernateMetaClass = sessionFactory.getClassMetadata( domainClass )
    //get the table name mapped to the SampleDomainClass domain class
    def tableName = hibernateMetaClass.getTableName()
    //println("table name=$tableName")

    //creaate a new GrailsDomainClass object for the SampleDomainClass
    def grailsDomainClass = new DefaultGrailsDomainClass( domainClass )
    //get the domain properties which keeps the domain class properties defined in Domain Class
    //grailsDomainClass.getProperties() is returned the GrailsDomainClassProperty[] objects
    //please refer to the javadoc
    //http://www.grails.org/doc/1.0.x/api/index.html?org/codehaus/groovy/grails/commons/DefaultGrailsDomainClass.html
    def domainProps = grailsDomainClass.getProperties()
    domainProps.each {prop ->
      //get the property's name
      def propName = prop.getName()
      //get the database column name mapped to the domain property name
      //getPropertyColumnNames is returned the String array object
      //please refer to the hibernate javadoc
      //http://www.hibernate.org/hib_docs/v3/api/org/hibernate/persister/entity/AbstractEntityPersister.html
      try
      {
        def columnProps = hibernateMetaClass.getPropertyColumnNames( propName )
        if ( columnProps && columnProps.length > 0 )
        {
          //get the column name, which is stored into the first array
          def columnName = columnProps[0]
          def info = [
                  propertyName: propName,
                  propertyType: prop.type.name,
                  columnName: columnName,
                  columnType: prop.typePropertyName
          ]
          mappingList << info
        }
      }
      catch ( Exception e )
      {}

    }
    return mappingList
  }

}
