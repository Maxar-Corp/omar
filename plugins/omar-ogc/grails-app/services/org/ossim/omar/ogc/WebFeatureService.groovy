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
          version: '1.0.0',
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"
      ) {
        def service = wfsConfig.service
        Service {
          Name( service.name )
          Title( service.title )
          Abstract( service.abstract )
          Keywords( service.keywords )
          OnlineResource( service.onlineResource )
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
                      "${method}"( onlineResource: requestType.onlineResource[method] )
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
    def (workspaceName, layerName) = wfsRequest?.typeName?.split( ':' )
    def workspace = getWorkspace( workspaceName )
    def layer = workspace[layerName]

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( xsd: "http://www.w3.org/2001/XMLSchema" )
      mkp.declareNamespace( "${workspaceName}": layer.schema.uri )
      xsd.schema( elementFormDefault: "qualified", targetNamespace: layer.schema.uri ) {
        xsd.import( namespace: "http://www.opengis.net/gml", schemaLocation: "http://schemas.opengis.net/gml/2.1.2/feature.xsd" )
        xsd.complexType( name: "${layerName}Type" ) {
          xsd.complexContent {
            xsd.extension( base: "gml:AbstractFeatureType" ) {
              xsd.sequence {
                for ( def field in layer.schema.fields )
                {
                  def descr = layer.schema.featureType.getDescriptor( field.name )
                  xsd.element(
                      maxOccurs: "${ descr.maxOccurs }",
                      minOccurs: "${ descr.minOccurs }",
                      name: "${ field.name }",
                      nillable: "${ descr.nillable }",
                      type: "${ typeMappings.get( field.typ, field.typ ) }" )
                }
              }
            }
          }
        }
        xsd.element( name: layerName, substitutionGroup: "gml:_Feature", type: "${workspaceName}:${layerName}Type" )
      }
    }

    workspace.close()

    def buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()

    [buffer, 'application/xml']
  }

  def getFeature(def wfsRequest)
  {
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
            'xsi:schemaLocation': "http://www.opengis.net/ogc http://schemas.opengis.net/wfs/1.0.0/OGC-exception.xsd" ) {
          ServiceException( code: "GeneralException", "Uknown outputFormat: ${wfsRequest.outputFormat}" )
        }
      }.toString()

      println results

      contentType = 'application/xml'
    }

    return [results, contentType]
  }

  private Workspace getWorkspace(def workspaceName)
  {

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

    workspace
  }

  void afterPropertiesSet() throws Exception
  {
    serverAddress = grailsApplication.config.omar.serverURL
    resultFormats = applicationContext.getBeansOfType( ResultFormat ).values().groupBy { it.name }

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
                Post: '${serverAddress}/wfs']]
        ],
        schemaDescriptionLanguages: ['XMLSCHEMA'],
        resultFormats: [
            GetFeature: ['GML2'/*, 'GML3', 'SHAPE-ZIP'*/, 'GEOJSON', 'CSV', 'KML', 'KMLQUERY']
        ],
        featureNamespaces: [omar: 'http://omar.ossim.org']
    ]
  }
}
