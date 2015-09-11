package org.ossim.omar.ogc

import geoscript.workspace.Workspace

import groovy.xml.StreamingMarkupBuilder

import org.geotools.factory.CommonFactoryFinder
import org.ossim.omar.ogc.wfs.ResultFormat
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class WebFeatureService implements InitializingBean, ApplicationContextAware
{
  static transactional = false

  def grailsLinkGenerator
  def grailsApplication
  def dataSourceUnproxied

  // Initialized in afterPropertiesSet
  private def serverAddress
  private def wfsConfig
  private def resultFormats

  ApplicationContext applicationContext

  private def typeMappings = [
      'Double': 'xsd:double',
      'Integer': 'xsd:int',
      'Long': 'xsd:long',
      'Polygon': 'gml:PolygonPropertyType',
      'MultiPolygon': 'gml:MultiPolygonPropertyType',
      'String': 'xsd:string',
      'java.lang.Boolean': 'xsd:boolean',
      'java.math.BigDecimal': 'xsd:decimal',
      'java.sql.Timestamp': 'xsd:dateTime',
  ]

  def getCapabilities(def wfsRequest)
  {
    initConfig()

    def x = {
      mkp.xmlDeclaration()

      // OGC Namespaces
      mkp.declareNamespace( '': "http://www.opengis.net/wfs" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )

      // Feature namespaces:
      wfsConfig.featureNamespaces.each { k, v ->
        mkp.declareNamespace( "${k}": v )
      }

      // WFS GetCapabilities Document
      WFS_Capabilities(
          version: '1.1.0',
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd"
      ) {
        def service = wfsConfig.service
        Service {
          Name( service.name )
          Title( service.title )
          Abstract( service.abstract )
          Keywords( service.keywords )
//          OnlineResource( service.onlineResource )
//          OnlineResource( grailsLinkGenerator.resource( dir: 'wfs', absolute: true ) )
          OnlineResource( "${grailsLinkGenerator.serverBaseURL}/wfs" )

          Fees( service.fees )
          AccessConstraints( service.accessContraints )
        }
        Capability {
          Request {
            wfsConfig.requestTypes.each { requestType ->
              "${requestType.name}" {
                switch ( requestType.name )
                {
                case "DescribeFeatureType":
                  SchemaDescriptionLanguage {
                    wfsConfig.schemaDescriptionLanguages.each { descLang ->
                      "${descLang}"()
                    }
                  }
                  break
                case "GetFeature":
                case "GetFeatureWithLock":
                  ResultFormat {
                    wfsConfig.resultFormats[requestType.name].each { format ->
                      "${format}"()
                    }
                  }
                  break
                }
                ['Get', 'Post'].each { method ->
                  DCPType {
                    HTTP {
//                      "${method}"( onlineResource: requestType.onlineResource[method] )
//                      "${method}"( onlineResource: grailsLinkGenerator.resource( dir: 'wfs', absolute: true ) )
                      if ( method == 'Get' )
                      {
                        "${method}"( onlineResource: "${grailsLinkGenerator.link( controller: 'wfs', params: [request: requestType.name], absolute: true )}" )
                      }
                      else
                      {

                        "${method}"( onlineResource: "${grailsLinkGenerator.serverBaseURL}/wfs" )
                      }
                    }
                  }
                }
              }
            }
          }
        }
        FeatureTypeList {
          Operations {
            wfsConfig.featureTypeOperations.each { op -> "${op}"() }
          }
          wfsConfig.featureTypes.each { featureType ->
            FeatureType {
              Name( featureType.name )
              Title( featureType.title )
              Abstract( featureType.abstract )
              Keywords( featureType.keywords )
              SRS( featureType.srs )
              LatLongBoundingBox(
                  minx: featureType.bbox.minX,
                  miny: featureType.bbox.minY,
                  maxx: featureType.bbox.maxX,
                  maxy: featureType.bbox.maxY
              )
            }
          }
        }
        ogc.Filter_Capabilities {
          ogc.Spatial_Capabilities {
            ogc.Spatial_Operators {
              wfsConfig.spatialOperators.each { op ->
                ogc."${op}"()
              }
            }
          }
          ogc.Scalar_Capabilities {
            ogc.Logical_Operators()
            ogc.Comparison_Operators {
              wfsConfig.comparisonOperators.each { op ->
                ogc."${op}"()
              }
            }
            ogc.Arithmetic_Operators {
              ogc.Simple_Arithmetic()
              ogc.Functions {
                ogc.Function_Names {
                  wfsConfig.functionNames.each { func ->
                    ogc.Function_Name( nArgs: func.nArgs, func.name )
                  }
                }
              }
            }
          }
        }
      }
    }

    def buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()
    [buffer, 'application/xml']
  }


  def describeFeatureType(def wfsRequest)
  {
    initConfig()

    def exposedLayers = 'omar:raster_entry,omar:video_data_set'
    def gmlSchema = "http://schemas.opengis.net/gml/2.1.2/feature.xsd"
    def buffer

    if ( wfsRequest.typeName )
    {
      def x = {
        //mkp.xmlDeclaration()
        mkp.declareNamespace(
            gml: 'http://www.opengis.net/gml',
            xsd: 'http://www.w3.org/2001/XMLSchema',
            omar: 'http://omar.ossim.org',
        )
        xsd.schema( elementFormDefault: 'qualified', targetNamespace: 'http://omar.ossim.org' ) {
          xsd.import( namespace: 'http://www.opengis.net/gml', schemaLocation: gmlSchema )
          ( wfsRequest.typeName ?: exposedLayers )?.split( ',' ).each { foo ->
            def (workspaceId, layerName) = foo.split( ':' )

            Workspace.withWorkspace( getWorkspace( workspaceId ) ) { workspace ->
              def layer = workspace[layerName]

//                println "${layer.name}: ${layer.count()}"

              xsd.complexType( name: "${layer.name}Type" ) {
                xsd.complexContent {
                  xsd.extension( base: 'gml:AbstractFeatureType' ) {
                    xsd.sequence {
                      for ( def field in layer.schema.fields )
                      {
                        def descr = layer.schema.featureType.getDescriptor( field.name )
                        xsd.element(
                            maxOccurs: "${descr.maxOccurs}",
                            minOccurs: "${descr.minOccurs}",
                            name: "${field.name}",
                            nillable: "${descr.nillable}",
                            type: "${typeMappings.get( field.typ, field.typ )}" )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()
    }
    else
    {
      def x = {
        mkp.xmlDeclaration()
        mkp.declareNamespace(
            omar: 'http://omar.ossim.org',
            xsd: 'http://www.w3.org/2001/XMLSchema'
        )
        xsd.schema( elementFormDefault: 'qualified', targetNamespace: 'http://omar.ossim.org' ) {
          xsd.import( namespace: 'http://omar.ossim.org',
              schemaLocation: grailsLinkGenerator.link( absolute: true, controller: 'wfs', params: [
                  typeName: exposedLayers,
                  request: 'DescribeFeatureType'
              ] ) )
        }
      }
      buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()

    }

    [buffer, 'application/xml']
  }

  def getFeature(def wfsRequest)
  {
    initConfig()

    def results, contentType
    def name = ( wfsRequest['outputFormat']?.toUpperCase() ?: "GML2" )?.toUpperCase()
    def resultFormat = resultFormats[name]?.first()

    if ( resultFormat )
    {
      (results, contentType) = resultFormat.getFeature( wfsRequest, getWorkspace( 'omar' ) )
    }
    else
    {
      results = new StreamingMarkupBuilder().bind() {
        mkp.xmlDeclaration()
        mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
        ServiceExceptionReport( version: "1.2.0", xmlns: "http://www.opengis.net/ogc",
            'xsi:schemaLocation': "http://www.opengis.net/ogc http://schemas.opengis.net/wfs/1.1.0/wfs.xsd" ) {
          ServiceException( code: "GeneralException", "Uknown outputFormat: ${wfsRequest.outputFormat}" )
        }
      }.toString()

     // println results

      contentType = 'application/xml'
    }
    //println results
    return [results, contentType]
  }

  private Workspace getWorkspace(def workspaceName)
  {
/*
    def url = grailsApplication.config.dataSource.url

    def workspace = new Workspace(
        dbtype: 'postgis',

        // All these can be blank (except for port for some reason)
        // The dataSource is provided by Hibernate.
        database: '',
        host: '',
        port: 5432,
        user: '',
        password: '',

        'Data Source': dataSourceUnproxied,
        'Expose primary keys': true,
        namespace: 'http://omar.ossim.org'
    )
*/

    def dataSourceConfig = grailsApplication.config.dataSource
    def pattern = "jdbc:postgresql:(//(.*)/)?(.*)"
    def matcher = dataSourceConfig.url =~ pattern

    def dbParams = [
        dbtype: 'postgis',
        host: matcher[0][-2] ?: 'localhost',
        port: '5432',
        database: matcher[0][-1],
        user: dataSourceConfig.username,
        password: dataSourceConfig.password,
//        'Data Source': dataSourceUnproxied,
        'Expose primary keys': true
    ]

    //println dbParams

    def workspace = Workspace.getWorkspace( dbParams )

    workspace
  }

  void afterPropertiesSet() throws Exception
  {
    resultFormats = applicationContext.getBeansOfType( ResultFormat ).values().groupBy { it.name }

  }

  private void initConfig()
  {
    serverAddress = grailsLinkGenerator.serverBaseURL

    wfsConfig = [
        service: [
            name: 'OMAR WFS',
            title: 'OMAR Web Feature Service',
            abstract: 'This is the WFS implementation for OMAR',
            keywords: 'WFS, OMAR',
            onlineResource: "${serverAddress}/wfs",
            fees: 'NONE',
            accessContraints: 'NONE'
        ],
        featureTypeOperations: ['Query'/*, 'Insert', 'Update', 'Delete', 'Lock'*/],
        spatialOperators: [
            'Disjoint',
            'Equals',
            'DWithin',
            'Beyond',
            'Intersect',
            'Touches',
            'Crosses',
            'Within',
            'Contains',
            'Overlaps',
            'BBOX'
        ],
        comparisonOperators: ['Simple_Comparisons', 'Between', 'Like', 'NullCheck'],
        functionNames: CommonFactoryFinder.getFunctionFactories().collect {
          it.functionNames
        }.flatten().sort {
          it.name.toLowerCase()
        }.groupBy { it.name }.collect { k, v ->
          [name: k, nArgs: v[0].argumentCount]
        },
        featureTypes: [
            [name: 'omar:raster_entry', title: 'raster_entry', abstract: '', keywords: 'raster_entry, features', srs: 'EPSG:4326',
                bbox: [minX: -180.0, minY: -90.0, maxX: 180.0, maxY: 90.0]],
            [name: 'omar:video_data_set', title: 'video_data_set', abstract: '', keywords: 'video_data_set, features', srs: 'EPSG:4326',
                bbox: [minX: -180.0, minY: -90.0, maxX: 180.0, maxY: 90.0]]
        ],
        requestTypes: [
            [name: 'GetCapabilities', onlineResource: [
                Get: "${serverAddress}/wfs?request=GetCapabilities",
                Post: "${serverAddress}/wfs"]],
            [name: 'DescribeFeatureType', onlineResource: [
                Get: "${serverAddress}/wfs?request=DescribeFeatureType",
                Post: "${serverAddress}/wfs"]],
            [name: 'GetFeature', onlineResource: [
                Get: "${serverAddress}/wfs?request=GetFeature",
                Post: "${serverAddress}/wfs"]]
        ],
        schemaDescriptionLanguages: ['XMLSCHEMA'],
        resultFormats: [
            GetFeature: resultFormats.keySet().sort()
        ],
        featureNamespaces: [omar: 'http://omar.ossim.org']
    ]
  }

}
