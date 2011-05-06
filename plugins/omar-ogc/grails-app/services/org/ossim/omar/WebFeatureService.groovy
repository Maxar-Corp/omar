package org.ossim.omar

import net.opengis.wfs.WfsFactory

import org.geotools.xml.Encoder
import org.geotools.wfs.v1_0.WFSConfiguration
import org.geotools.wfs.v1_0.WFS
import org.geotools.data.postgis.PostgisNGDataStoreFactory
import org.geotools.data.Query
import org.opengis.filter.sort.SortOrder
import org.geotools.factory.CommonFactoryFinder


import geoscript.workspace.Database
import geoscript.filter.Filter
import geoscript.workspace.PostGIS

import groovy.xml.StreamingMarkupBuilder

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 3/16/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
class WebFeatureService
{
  def dataSource
  def grailsApplication


  def featureTypes = [
          [name: "raster_entry", title: "raster_entry", description: "Available Imagery", srs: "EPSG:4326", bbox: [minx: "-180", miny: "-90", maxx: "180", maxy: "90"]],
          [name: "video_data_set", title: "video_data_set", description: "Available Videos", srs: "EPSG:4326", bbox: [minx: "-180", miny: "-90", maxx: "180", maxy: "90"]]
  ]

  def getCapabilities()
  {
    def workspace = createWorkspace()
    def wfsURL = "${grailsApplication.config.grails.serverURL}/wfs"

    def name = "OMAR WFS"
    def title = "OMAR Data"
    def xml = null

    try
    {
      xml = new StreamingMarkupBuilder().bind {

        mkp.xmlDeclaration()
        mkp.declareNamespace('': "http://www.opengis.net/wfs")
        mkp.declareNamespace('ogc': 'http://www.opengis.net/ogc')
        mkp.declareNamespace('xsi': "http://www.w3.org/2001/XMLSchema-instance")

        WFS_Capabilities('xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-capabilities.xsd", version: "1.0.0", updateSequence: "0") {
          Service {
            Name(name)
            Title(title)
            OnlineResource {
              mkp.yieldUnescaped("<![CDATA[${wfsURL}]]>")
            }
          }
          Capability {
            Request {
              GetCapabilities {
                DCPType {
                  HTTP {
                    Get(onlineResource: wfsURL)
                  }
                }
                DCPType {
                  HTTP {
                    Post(onlineResource: wfsURL)
                  }
                }
              }
              DescribeFeatureType {
                SchemaDescriptionLanguage {
                  XMLSCHEMA()
                }
                DCPType {
                  HTTP {
                    Get(onlineResource: wfsURL)
                  }
                }
                DCPType {
                  HTTP {
                    Post(onlineResource: wfsURL)
                  }
                }
              }
              GetFeature {
                ResultFormat {
                  GML2()
                }
                DCPType {
                  HTTP {
                    Get(onlineResource: wfsURL)
                  }
                }
                DCPType {
                  HTTP {
                    Post(onlineResource: wfsURL)
                  }
                }
              }
            }
          }
          FeatureTypeList {
            Operations {
              Query()
            }
            featureTypes.each { featureType ->
              FeatureType {
                Name(featureType.name)
                Title(featureType.title)
                Abstract(featureType.description)
                SRS(featureType.srs)
                LatLongBoundingBox(minx: featureType.bbox.minx, miny: featureType.bbox.miny, maxx: featureType.bbox.maxx, maxy: featureType.bbox.maxy)
              }
            }
          }
          ogc.Filter_Capabilities {
            ogc.Spatial_Capabilities {
              ogc.Spatial_Operators {
                ogc.Equals()
                ogc.Disjoint()
                ogc.Touches()
                ogc.Within()
                ogc.Overlaps()
                ogc.Crosses()
                ogc.Intersect()
                ogc.Contains()
                ogc.DWithin()
                ogc.BBOX()
              }
            }
            ogc.Scalar_Capabilities {
              ogc.Logical_Operators {
                ogc.Comparison_Operators {
                  ogc.Simple_Comparisons()
                  ogc.Like()
                  ogc.Between()
                }
              }
            }
          }
        }
      }
    }
    finally
    {
      workspace.close()
    }

    return xml?.toString()
  }

  def getFeature(def typeName, def filter, def pagination)
  {
    def out = new ByteArrayOutputStream()
    def workspace = createWorkspace()
    try
    {

      def layer = workspace[typeName]
      def query = new Query(typeName, new Filter(filter).filter)


      query.startIndex = pagination.offset
      query.maxFeatures = (pagination.max <= 100) ? pagination.max : 100

      if ( pagination.sort )
      {
        def filterFactory = CommonFactoryFinder.getFilterFactory(null)
        def order = null
        switch ( pagination.order.toString().toLowerCase() )
        {
        case "asc":
          order = SortOrder.ASCENDING
          break
        case "desc":
          order = SortOrder.DESCENDING
          break
        default:
          order = SortOrder.ASCENDING
        }
        query.sortBy = [filterFactory.sort(pagination.sort, order)]
      }




      def features = layer.fs.getFeatures(query)
      def fc = WfsFactory.eINSTANCE.createFeatureCollectionType()

      fc.feature.add(features)

      def e = new Encoder(new WFSConfiguration())
      def uri = (layer.fs.name.namespaceURI == null) ? new URI("http://omar.ossim.org") : new URI(layer.fs.name.namespaceURI)
      String prefix = "omar"
      e.namespaces.declarePrefix(prefix, uri.toString())
      e.indenting = true
      e.encode(fc, WFS.FeatureCollection, out)
    }
    catch (Exception e)
    {
      e.printStackTrace()
    }
    finally
    {
      workspace.close()
    }

    return out.toString()
  }

  def describeFeatureType(def typeName)
  {
    def workspace = createWorkspace()
    def xml = null

    try
    {
      def layer = workspace[typeName]

      xml = new StreamingMarkupBuilder().bind {

        mkp.xmlDeclaration()
        mkp.declareNamespace('': "http://www.opengis.net/wfs")
        mkp.declareNamespace('ogc': 'http://www.opengis.net/ogc')
        mkp.declareNamespace('xsi': "http://www.w3.org/2001/XMLSchema-instance")

        mkp.declareNamespace(xsd: "http://www.w3.org/2001/XMLSchema")

        xsd.schema {
          xsd.complexType(name: "${typeName}Type") {
            xsd.complexContent {
              xsd.extension(base: "gml:AbstractFeatureType") {
                xsd.sequence {
                  layer.schema.featureType.attributeDescriptors.each {
                    def dataType = null

                    switch ( it.type.binding )
                    {
                    case String:
                      dataType = "xsd:string"
                      break
                    case Long:
                      dataType = "xsd:long"
                      break
                    case java.sql.Timestamp:
                      dataType = "xsd:dateTime"
                      break
                    case Double:
                      dataType = "xsd:double"
                      break
                    case Integer:
                      dataType = "xsd:integer"
                      break
                    case Boolean:
                      dataType = "xsd:boolean"
                      break
                    case BigDecimal:
                      dataType = "xsd:decimal"
                      break
                    case com.vividsolutions.jts.geom.Polygon:
                      dataType = "gml:PolygonPropertyType"
                      break
                    case com.vividsolutions.jts.geom.MultiPolygon:
                      dataType = "gml:MultiPolygonPropertyType"
                      break
                    default:
                      dataType = it.type.binding
                    }

                    xsd.element(maxOccurs: it.maxOccurs, minOccurs: it.minOccurs, name: it.localName, nillable: it.nillable, type: dataType)
                  }
                }
              }
            }
          }

          xsd.element(name: typeName, substitutionGroup: "gml:_Feature", type: "omar:${typeName}Type")
        }
      }
    }
    finally
    {
      workspace.close()
    }

    return xml?.toString()
  }

  def createWorkspace(def flag = true)
  {
    def workspace = null

    if ( flag )
    {
      def jdbcParams = grailsApplication.config.dataSource

      def dbParams = [
              dbtype: "postgis",           //must be postgis
              user: jdbcParams.username,   //the user to connect with
              passwd: jdbcParams.password, //the password of the user.
              schema: "public"
      ]


      def pattern1 = "jdbc:(.*)://(.*):(.*)/(.*)"
      def pattern2 = "jdbc:(.*)://(.*)/(.*)"
      def pattern3 = "jdbc:(.*):(.*)"


      switch ( jdbcParams.url )
      {
      case ~pattern1:
        def matcher = (jdbcParams.url) =~ pattern1
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = matcher[0][3]
        dbParams['database'] = matcher[0][4]
        break
      case ~pattern2:
        def matcher = (jdbcParams.url) =~ pattern2
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][3]
        break
      case ~pattern3:
        def matcher = (jdbcParams.url) =~ pattern3
        dbParams['host'] = "localhost"
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][2]
        break
      }

      workspace = new PostGIS(
              dbParams['database'],
              dbParams['host'],
              dbParams['port'],
              dbParams['schema'],
              dbParams['user'],
              dbParams['passwd']
      )
    }
    else
    {
      def getDbParams = [(PostgisNGDataStoreFactory.DATASOURCE.key): dataSource]
      def dataStore = new PostgisNGDataStoreFactory().createDataStore(getDbParams)

      workspace = new Database(dataStore)
    }

    return workspace
  }
}
