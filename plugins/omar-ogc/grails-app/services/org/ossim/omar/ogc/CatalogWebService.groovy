package org.ossim.omar.ogc

import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.workspace.Database
import geoscript.workspace.Workspace
import groovy.xml.StreamingMarkupBuilder

class CatalogWebService
{
  static transactional = false

  def grailsApplication
  def dataSourceUnproxied


  private static final dcTag = [
      full: 'Record',
      brief: 'BriefRecord',
      summary: 'SummaryRecord'
  ]

  private static final dcCols = [
      full: [
          'identifier',
          'type',
          'title',
          'subject',
          'relation',
          'format',
          'creator',
          'publisher',
          'contributer',
          'source',
          'language',
          'coverage',
          'rights',
          'description'
      ],
      brief: [
          'identifier',
          'type',
          'title',
      ],
      summary: [
          'identifier',
          'type',
          'title',
          'subject',
          'relation',
          'format'
      ]
  ]

  private static final dctCols = ['spatial', 'abstract', 'modified']
  private static final owsCols = ['boundingbox']


  def getCapabiltiies(CswCommand cswCommand)
  {
    def serverAddress = grailsApplication.config.omar.serverURL

    def cswCaps = {
      mkp.xmlDeclaration()
//	mkp.declareNamespace(apiso: "http://www.opengis.net/cat/csw/apiso/1.0")
//	mkp.declareNamespace(atom: "http://www.w3.org/2005/Atom")
      mkp.declareNamespace( csw: "http://www.opengis.net/cat/csw/2.0.2" )
      mkp.declareNamespace( dc: "http://purl.org/dc/elements/1.1/" )
      mkp.declareNamespace( dct: "http://purl.org/dc/terms/" )
//	mkp.declareNamespace(dif: "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/")
//	mkp.declareNamespace(fgdc: "http://www.opengis.net/cat/csw/csdgm")
//	mkp.declareNamespace(gco: "http://www.isotc211.org/2005/gco")
//	mkp.declareNamespace(georss: "http://www.georss.org/georss")
//      mkp.declareNamespace( gmd: "http://www.isotc211.org/2005/gmd" )
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
//	mkp.declareNamespace(inspire_common: "http://inspire.ec.europa.eu/schemas/common/1.0")
//	mkp.declareNamespace(inspire_ds: "http://inspire.ec.europa.eu/schemas/inspire_ds/1.0")
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )
//	mkp.declareNamespace(os: "http://a9.com/-/spec/opensearch/1.1/")
      mkp.declareNamespace( ows: "http://www.opengis.net/ows" )
//	mkp.declareNamespace(rim: "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0")
//	mkp.declareNamespace(sitemap: "http://www.sitemaps.org/schemas/sitemap/0.9")
//	mkp.declareNamespace(soapenv: "http://www.w3.org/2003/05/soap-envelope")
//	mkp.declareNamespace(wrs: "http://www.opengis.net/cat/wrs/1.0")
//	mkp.declareNamespace(srv: "http://www.isotc211.org/2005/srv")
      mkp.declareNamespace( xlink: "http://www.w3.org/1999/xlink" )
      mkp.declareNamespace( xs: "http://www.w3.org/2001/XMLSchema" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
      csw.Capabilities(
//		updateSequence: "1366821893",
          version: "2.0.2",
          'xsi:schemaLocation': "http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd"
      ) {
        ows.ServiceIdentification {
          ows.Title()
          ows.Abstract()
          ows.Keywords {
            ows.Keyword()
            ows.Keyword()
            ows.Keyword()
            ows.Type( codeSpace: "ISOTC211/19115", "theme" )
          }
          ows.ServiceType( codeSpace: "OGC", "CSW" )
          ows.ServiceTypeVersion( "2.0.2" )
          ows.Fees( "None" )
          ows.AccessConstraints( "None" )
        }
        ows.ServiceProvider {
          ows.ProviderName()
          ows.ProviderSite( 'xlink:type': "simple", 'xlink:href': "" )
          ows.ServiceContact {
            ows.IndividualName()
            ows.PositionName()
            ows.ContactInfo {
              ows.Phone {
                ows.Voice()
                ows.Facsimile()
              }
              ows.Address {
                ows.DeliveryPoint()
                ows.City()
                ows.AdministrativeArea()
                ows.PostalCode()
                ows.Country()
                ows.ElectronicMailAddress()
              }
              ows.OnlineResource( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw" )
              ows.HoursOfService()
              ows.ContactInstructions()
            }
            ows.Role( codeSpace: "ISOTC211/19115", "" )
          }
        }
        ows.OperationsMetadata {
          ows.Operation( name: "GetCapabilities" ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw" )
                ows.Post( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw" )
              }
            }
            ows.Parameter( name: "sections" ) {
              ows.Value( "ServiceIdentification" )
              ows.Value( "ServiceProvider" )
              ows.Value( "OperationsMetadata" )
              ows.Value( "Filter_Capabilities" )
            }
          }
/*
			ows.Operation(name: "GetRepositoryItem") {
      			ows.DCP {
        			ows.HTTP {
          				ows.Get( 'xlink:type': "simple",  'xlink:href':"${serverAddress}/csw")
        			}
      			}
    		}
*/
          ows.Operation( name: "DescribeRecord" ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw" )
                ows.Post( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw" )
              }
            }
            ows.Parameter( name: "typeName" ) {
              ows.Value( "csw:Record" )
/*
					ows.Value("dif:DIF")
					ows.Value("gmd:MD_Metadata")
					ows.Value("rim:RegistryObject")
					ows.Value("fgdc:metadata")
					ows.Value("atom:entry")
*/
            }
            ows.Parameter( name: "outputFormat" ) {
              ows.Value( "application/xml" )
              ows.Value( "application/json" )
            }
            ows.Parameter( name: "schemaLanguage" ) {
//					ows.Value("http://www.w3.org/XML/Schema")
              ows.Value( "http://www.w3.org/TR/xmlschema-1/" )
            }
          }
/*
		    ows.Operation( name: "GetDomain") {
		      ows.DCP {
		        ows.HTTP {
		          ows.Get( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw")
		          ows.Post( 'xlink:type': "simple", 'xlink:href': "${serverAddress}/csw")
		        }
		      }
		      ows.Parameter( name: "ParameterName") {
		        ows.Value("GetRecords.outputFormat")
		        ows.Value("GetRecords.outputSchema")
		        ows.Value("GetRecords.CONSTRAINTLANGUAGE")
		        ows.Value("GetRecords.resultType")
		        ows.Value("GetRecords.typeNames")
		        ows.Value("GetRecords.ElementSetName")
		        ows.Value("GetCapabilities.sections")
		        ows.Value("GetRecordById.outputFormat")
		        ows.Value("GetRecordById.outputSchema")
		        ows.Value("GetRecordById.ElementSetName")
		        ows.Value("DescribeRecord.schemaLanguage")
		        ows.Value("DescribeRecord.outputFormat")
		        ows.Value("DescribeRecord.typeName")
		      }
		    }
*/
          ows.Operation( name: 'GetRecords' ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:type': 'simple', 'xlink:href': "${serverAddress}/csw" )
                ows.Post( 'xlink:type': 'simple', 'xlink:href': "${serverAddress}/csw" )
              }
            }
            ows.Parameter( name: 'resultType' ) {
              ows.Value( "hits" )
              ows.Value( "results" )
              ows.Value( "validate" )
            }
            ows.Parameter( name: 'outputFormat' ) {
              ows.Value( "application/xml" )
              ows.Value( "application/json" )
            }
            ows.Parameter( name: 'outputSchema' ) {
              ows.Value( "http://www.opengis.net/cat/csw/2.0.2" )
/*
			        ows.Value("http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/")
			        ows.Value("http://www.isotc211.org/2005/gmd")
			        ows.Value("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0")
			        ows.Value("http://www.opengis.net/cat/csw/csdgm")
			        ows.Value("http://www.w3.org/2005/Atom")
*/
            }
            ows.Parameter( name: 'typeNames' ) {
              ows.Value( "csw:Record" )
/*
			        ows.Value("dif:DIF")
			        ows.Value("gmd:MD_Metadata")
			        ows.Value("rim:RegistryObject")
			        ows.Value("fgdc:metadata")
			        ows.Value("atom:entry")
*/
            }
            ows.Parameter( name: 'CONSTRAINTLANGUAGE' ) {
              ows.Value( "FILTER" )
              ows.Value( "CQL_TEXT" )
            }
/*
			      ows.Parameter(name:'ElementSetName') {
			        ows.Value("brief")
			        ows.Value("summary")
			        ows.Value("full")
			      }
			      ows.Constraint(name:'SupportedISOQueryables') {
			        ows.Value("apiso:DistanceValue")
			        ows.Value("apiso:Abstract")
			        ows.Value("apiso:RevisionDate")
			        ows.Value("apiso:Subject")
			        ows.Value("apiso:KeywordType")
			        ows.Value("apiso:Title")
			        ows.Value("apiso:CRS")
			        ows.Value("apiso:PublicationDate")
			        ows.Value("apiso:Type")
			        ows.Value("apiso:AlternateTitle")
			        ows.Value("apiso:BoundingBox")
			        ows.Value("apiso:AnyText")
			        ows.Value("apiso:ParentIdentifier")
			        ows.Value("apiso:Modified")
			        ows.Value("apiso:Operation")
			        ows.Value("apiso:Format")
			        ows.Value("apiso:TempExtent_end")
			        ows.Value("apiso:DistanceUOM")
			        ows.Value("apiso:OrganisationName")
			        ows.Value("apiso:ServiceType")
			        ows.Value("apiso:TempExtent_begin")
			        ows.Value("apiso:ResourceLanguage")
			        ows.Value("apiso:ServiceTypeVersion")
			        ows.Value("apiso:OperatesOn")
			        ows.Value("apiso:Denominator")
			        ows.Value("apiso:HasSecurityConstraints")
			        ows.Value("apiso:OperatesOnIdentifier")
			        ows.Value("apiso:GeographicDescriptionCode")
			        ows.Value("apiso:Language")
			        ows.Value("apiso:Identifier")
			        ows.Value("apiso:OperatesOnName")
			        ows.Value("apiso:TopicCategory")
			        ows.Value("apiso:CreationDate")
			        ows.Value("apiso:CouplingType")
			    }
				ows.Constraint(name:'AdditionalQueryables') {
					ows.Value("apiso:Lineage")
					ows.Value("apiso:Classification")
					ows.Value("apiso:Creator")
					ows.Value("apiso:Relation")
					ows.Value("apiso:OtherConstraints")
					ows.Value("apiso:SpecificationTitle")
					ows.Value("apiso:ResponsiblePartyRole")
					ows.Value("apiso:SpecificationDateType")
					ows.Value("apiso:Degree")
					ows.Value("apiso:Contributor")
					ows.Value("apiso:ConditionApplyingToAccessAndUse")
					ows.Value("apiso:SpecificationDate")
					ows.Value("apiso:AccessConstraints")
					ows.Value("apiso:Publisher")
				}
				ows.Constraint(name:'SupportedDIFQueryables') {
					ows.Value("dif:Keyword")
					ows.Value("dif:Data_Presentation_Form")
					ows.Value("dif:Summary")
					ows.Value("dif:Stop_Date")
					ows.Value("dif:Identifier")
					ows.Value("dif:ISO_Topic_Category")
					ows.Value("dif:Originating_Center")
					ows.Value("dif:DIF_Creation_Date")
					ows.Value("dif:Dataset_Publisher")
					ows.Value("dif:Dataset_Release_Date")
					ows.Value("dif:Related_URL")
					ows.Value("dif:AnyText")
					ows.Value("dif:Data_Set_Language")
					ows.Value("dif:Access_Constraints")
					ows.Value("dif:Spatial_Coverage")
					ows.Value("dif:Entry_Title")
					ows.Value("dif:Start_Date")
					ows.Value("dif:Dataset_Creator")
				}
				ows.Constraint(name:'SupportedDublinCoreQueryables') {
					ows.Value("dc:contributor")
					ows.Value("dc:source")
					ows.Value("dc:language")
					ows.Value("dc:title")
					ows.Value("dc:subject")
					ows.Value("dc:creator")
					ows.Value("dc:type")
					ows.Value("ows:BoundingBox")
					ows.Value("dct:modified")
					ows.Value("dct:abstract")
					ows.Value("dc:relation")
					ows.Value("dc:date")
					ows.Value("dc:identifier")
					ows.Value("dc:publisher")
					ows.Value("dc:format")
					ows.Value("csw:AnyText")
					ows.Value("dc:rights")
				}
				ows.Constraint(name:'SupportedFGDCQueryables') {
					ows.Value("fgdc:Contributor")
					ows.Value("fgdc:GeospatialPresentationForm")
					ows.Value("fgdc:Publisher")
					ows.Value("fgdc:AccessConstraints")
					ows.Value("fgdc:AnyText")
					ows.Value("fgdc:Source")
					ows.Value("fgdc:Origin")
					ows.Value("fgdc:Type")
					ows.Value("fgdc:Format")
					ows.Value("fgdc:Originator")
					ows.Value("fgdc:Modified")
					ows.Value("fgdc:Envelope")
					ows.Value("fgdc:ThemeKeywords")
					ows.Value("fgdc:PublicationDate")
					ows.Value("fgdc:Title")
					ows.Value("fgdc:Purpose")
					ows.Value("fgdc:Abstract")
					ows.Value("fgdc:Identifier")
					ows.Value("fgdc:Progress")
					ows.Value("fgdc:BeginDate")
					ows.Value("fgdc:EndDate")
					ows.Value("fgdc:Relation")
				}
				ows.Constraint(name:'SupportedAtomQueryables') {
					ows.Value("atom:updated")
					ows.Value("georss:where")
					ows.Value("atom:id")
					ows.Value("atom:rights")
					ows.Value("atom:AnyText")
					ows.Value("atom:author")
					ows.Value("atom:summary")
					ows.Value("atom:contributor")
					ows.Value("atom:source")
					ows.Value("atom:published")
					ows.Value("atom:category")
					ows.Value("atom:title")
				}
*/
          }
/*
			ows.Constraint( name:'XPathQueryables') {
				ows.Value("allowed")
			}
			ows.Constraint(name:'PostEncoding') {
				ows.Value("XML")
				ows.Value("SOAP")
			}
			inspire_ds.ExtendedCapabilities('xsi:schemaLocation': 'http://inspire.ec.europa.eu/schemas/inspire_ds/1.0 http://inspire.ec.europa.eu/schemas/inspire_ds/1.0/inspire_ds.xsd') {
      			inspire_common.ResourceLocator {
        			inspire_common.URL {
          				mkp.yieldUnescaped "<![CDATA[http://maps.opensandiego.org/catalogue/csw?service=CSW&version=2.0.2&request=GetCapabilities]]>"
					}
        			inspire_common.MediaType("application/xml")
      			}
		      	inspire_common.ResourceType("service")
		      	inspire_common.TemporalReference {
		        	inspire_common.TemporalExtent {
		          		inspire_common.IntervalOfDates {
		            		inspire_common.StartingDate("YYYY-MM-DD")
		            		inspire_common.EndDate("YYYY-MM-DD")
		          		}
		        	}
		      	}
		      	inspire_common.Conformity {
		        	inspire_common.Specification('xsi:type': 'inspire_common:citationInspireInteroperabilityRegulation_eng') {
		          		inspire_common.Title("COMMISSION REGULATION (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services")
		          		inspire_common.DateOfPublication("2010-12-08")
		         		inspire_common.URI("OJ:L:2010:323:0011:0102:EN:PDF")
		          		inspire_common.ResourceLocator {
		            		inspire_common.URL("http://eur-lex.europa.eu/LexUriServ/LexUriServ.do?uri=OJ:L:2010:323:0011:0102:EN:PDF")
		            		inspire_common.MediaType("application/pdf")
		          		}
		          	}
		        	inspire_common.Degree("notEvaluated")
		      	}
				inspire_common.MetadataPointOfContact {
					inspire_common.OrganisationName("Organization Name")
					inspire_common.EmailAddress("Email Address")
				}
				inspire_common.MetadataDate("YYYY-MM-DD")
				inspire_common.SpatialDataServiceType("discovery")
				inspire_common.MandatoryKeyword('xsi:type': 'inspire_common:classificationOfSpatialDataService') {
					inspire_common.KeywordValue("infoCatalogueService")
				}
		      	inspire_common.Keyword('xsi:type': 'inspire_common:inspireTheme_eng') {
		        	inspire_common.OriginatingControlledVocabulary {
		          		inspire_common.Title("GEMET - INSPIRE themes")
		          		inspire_common.DateOfPublication("2008-06-01")
		        	}
		        	inspire_common.KeywordValue("Utility and governmental services")
		      	}
				inspire_common.SupportedLanguages {
					inspire_common.DefaultLanguage {
						inspire_common.Language("eng")
					}
					inspire_common.SupportedLanguage {
						inspire_common.Language("eng")
					}
					inspire_common.SupportedLanguage {
						inspire_common.Language("gre")
					}
				}
				inspire_common.ResponseLanguage {
					inspire_common.Language("eng")
				}
			}
*/
          ows.Operation( name: 'GetRecordById' ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:type': 'simple', 'xlink:href': "${serverAddress}/csw" )
                ows.Post( 'xlink:type': 'simple', 'xlink:href': "${serverAddress}/csw" )
              }
            }
            ows.Parameter( name: 'outputSchema' ) {
              ows.Value( "http://www.opengis.net/cat/csw/2.0.2" )
/*
		        ows.Value("http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/")
		        ows.Value("http://www.isotc211.org/2005/gmd")
		        ows.Value("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0")
		        ows.Value("http://www.opengis.net/cat/csw/csdgm")
		        ows.Value("http://www.w3.org/2005/Atom")
*/
            }
            ows.Parameter( name: 'outputFormat' ) {
              ows.Value( "application/xml" )
              ows.Value( "application/json" )
            }
            ows.Parameter( name: 'resultType' ) {
              ows.Value( "hits" )
              ows.Value( "results" )
              ows.Value( "validate" )
            }
            ows.Parameter( name: 'ElementSetName' ) {
              ows.Value( "brief" )
              ows.Value( "summary" )
              ows.Value( "full" )
            }
          }
          ows.Parameter( name: 'service' ) {
            ows.Value( "http://www.opengis.net/cat/csw/2.0.2" )
          }
          ows.Parameter( name: 'version' ) {
            ows.Value( "2.0.2" )
          }
        }
        ogc.Filter_Capabilities {
          ogc.Spatial_Capabilities {
            ogc.GeometryOperands {
              ogc.GeometryOperand( "gml:Point" )
              ogc.GeometryOperand( "gml:LineString" )
              ogc.GeometryOperand( "gml:Polygon" )
              ogc.GeometryOperand( "gml:Envelope" )
            }
            ogc.SpatialOperators {
              ogc.SpatialOperator( name: 'BBOX' )
              ogc.SpatialOperator( name: 'Beyond' )
              ogc.SpatialOperator( name: 'Contains' )
              ogc.SpatialOperator( name: 'Crosses' )
              ogc.SpatialOperator( name: 'Disjoint' )
              ogc.SpatialOperator( name: 'DWithin' )
              ogc.SpatialOperator( name: 'Equals' )
              ogc.SpatialOperator( name: 'Intersects' )
              ogc.SpatialOperator( name: 'Overlaps' )
              ogc.SpatialOperator( name: 'Touches' )
              ogc.SpatialOperator( name: 'Within' )
            }
          }
          ogc.Scalar_Capabilities {
            ogc.LogicalOperators()
            ogc.ComparisonOperators {
              ogc.ComparisonOperator( "Between" )
              ogc.ComparisonOperator( "EqualTo" )
              ogc.ComparisonOperator( "GreaterThan" )
              ogc.ComparisonOperator( "GreaterThanEqualTo" )
              ogc.ComparisonOperator( "LessThan" )
              ogc.ComparisonOperator( "LessThanEqualTo" )
              ogc.ComparisonOperator( "Like" )
              ogc.ComparisonOperator( "NotEqualTo" )
              ogc.ComparisonOperator( "NullCheck" )
            }
            ogc.ArithmeticOperators {
              ogc.Functions {
                ogc.FunctionNames {
//                  ogc.FunctionName( nArgs: '1', 'length' )
//                  ogc.FunctionName( nArgs: '1', 'lower' )
//                  ogc.FunctionName( nArgs: '1', 'ltrim' )
//                  ogc.FunctionName( nArgs: '1', 'rtrim' )
//                  ogc.FunctionName( nArgs: '1', 'trim' )
//                  ogc.FunctionName( nArgs: '1', 'upper' )
                }
              }
            }
          }
          ogc.Id_Capabilities {
//            ogc.EID()
//            ogc.FID()
          }
        }
      }
    }

    return new StreamingMarkupBuilder( encoding: "UTF-8" ).bind( cswCaps )
  }

  private def getResults(CswCommand cswCommand)
  {
    Database workspace = createWorkspace()
    def results = null

    try
    {

      Layer layer = createLayer( workspace )

      results = getHitCount( layer, cswCommand )

      if ( cswCommand.resultType?.toLowerCase() == 'results' )
      {
        results.data = executeQuery( layer, cswCommand )
      }
    }
    finally
    {
      workspace?.close()
    }

    return results
  }

  private def executeQuery(def layer, def cswCommand)
  {
    def o = [
        max: Math.min( cswCommand.maxRecords ?: 10, 100 ),
        start: ( cswCommand?.startPosition ?: 1 ) - 1,
        filter: parseFilter( cswCommand ),
        sort: ( cswCommand?.sortBy ) ? cswCommand?.convertSortByToArray() : [['identifier', 'ASC']]
    ]

    //println o

    def c = layer?.getCursor( o )
    def records = []

    while ( c?.hasNext() )
    {
      def f = c?.next()

      records << f.attributes
    }

    c?.close()

    return records
  }

  private static String parseFilter(cswCommand)
  {
    def filter = null

    if ( cswCommand?.constraint )
    {
      def constraint = cswCommand?.constraint

      constraint = constraint.replaceAll( "(?i)(ows:)?BoundingBox", "boundingbox" )
      constraint = constraint.replaceAll( "(?i)(csw:)?AnyText", "anytext" )
      filter = constraint
    }
    else
    {
      filter = Filter.PASS
    }

    //println filter

    filter
  }


  private getHitCount(Layer layer, CswCommand cswCommand)
  {
    def numberOfRecordsMatched = null

    try
    {
      //println layer.proj
      def filter = parseFilter( cswCommand )
      //println filter
      numberOfRecordsMatched = layer.count( filter )
    }
    catch ( Exception e )
    {
      e.printStackTrace()
    }

    def numberOfRecordsReturned = Math.min( numberOfRecordsMatched ?: 0, cswCommand.maxRecords ?: 10 )

    def nextRecord = ( cswCommand.startPosition ?: 1 ) + ( cswCommand.maxRecords ?: 10 )

    [
        timestamp: new Date().toTimestamp(),
        numberOfRecordsMatched: numberOfRecordsMatched,
        numberOfRecordsReturned: numberOfRecordsReturned,
        nextRecord: nextRecord
    ]
  }

  private Layer createLayer(Database workspace)
  {
    def sql = grailsApplication.config.csw.sql
    def layer = workspace.addSqlQuery( 'csw', sql, 'boundingBox', 'Polygon', 4326, ['identifier'] )
    layer
  }

  private Database createWorkspace()
  {
    def workspace = new Database( new Workspace(
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
    )?.ds )
    workspace
  }

  def getRecords(CswCommand cswCommand)
  {
    def serverAddress = grailsApplication.config.omar.serverURL



    def params = [
        elementSet: "full"
    ]

    def results = getResults( cswCommand )

    def xml = {
      mkp.xmlDeclaration()
//      mkp.declareNamespace( atom: "http://www.w3.org/2005/Atom" )
      mkp.declareNamespace( csw: "http://www.opengis.net/cat/csw/2.0.2" )
      mkp.declareNamespace( dc: "http://purl.org/dc/elements/1.1/" )
      mkp.declareNamespace( dct: "http://purl.org/dc/terms/" )
//      mkp.declareNamespace( dif: "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/" )
//      mkp.declareNamespace( fgdc: "http://www.opengis.net/cat/csw/csdgm" )
//      mkp.declareNamespace( gmd: "http://www.isotc211.org/2005/gmd" )
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )
//      mkp.declareNamespace( os: "http://a9.com/-/spec/opensearch/1.1/" )
      mkp.declareNamespace( ows: "http://www.opengis.net/ows" )
//      mkp.declareNamespace( rdf: "http://www.w3.org/1999/02/22-rdf-syntax-ns#" )
//      mkp.declareNamespace( sitemap: "http://www.sitemaps.org/schemas/sitemap/0.9" )
//      mkp.declareNamespace( soapenv: "http://www.w3.org/2003/05/soap-envelope" )
      mkp.declareNamespace( xlink: "http://www.w3.org/1999/xlink" )
      mkp.declareNamespace( xs: "http://www.w3.org/2001/XMLSchema" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
      csw.GetRecordsResponse(
          version: "2.0.2",
          'xsi:schemaLocation': "http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd"
      ) {
        csw.SearchStatus( timestamp: formatAsString( results.timestamp ) )
        csw.SearchResults(
            nextRecord: results.nextRecord,
            numberOfRecordsMatched: results.numberOfRecordsMatched,
            numberOfRecordsReturned: results.numberOfRecordsReturned,
            recordSchema: "http://www.opengis.net/cat/csw/2.0.2",
            elementSet: params.elementSet
        ) {
          for ( def x in results?.data )
          {
            def elementSetName = cswCommand.elementSetName ?: 'full'

            csw."${dcTag[elementSetName]}" {
              for ( def y in dcCols[elementSetName] )
              {
                if ( x[y] != null )
                {
                  "dc:${y}"( formatAsString( x[y] ) )
                }
              }
              for ( def y in dctCols )
              {
                if ( x[y] != null )
                {
                  "dct:${y}"( formatAsString( x[y] ) )
                }
              }
              if ( x.boundingbox != null )
              {
                def bounds = x.boundingbox.bounds

                "ows:BoundingBox"( crs: bounds?.proj?.id ) {
                  "ows:LowerCorner"( "${bounds?.minX} ${bounds?.minY}" )
                  "ows:UpperCorner"( "${bounds?.maxX} ${bounds?.maxY}" )
                }
              }
            }
          }
        }
      }
    }

    return new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( xml )
  }

  def getRecordById(CswCommand cswCommand)
  {
    def serverAddress = grailsApplication.config.omar.serverURL


    def getRecById = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( csw: "http://www.opengis.net/cat/csw/2.0.2" )
      mkp.declareNamespace( dc: "http://purl.org/dc/elements/1.1/" )
      mkp.declareNamespace( dct: "http://purl.org/dc/terms/" )
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( ows: "http://www.opengis.net/ows" )
      //mkp.declareNamespace( rim: "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" )
      //mkp.declareNamespace( wrs: "http://www.opengis.net/cat/wrs/1.0" )
      mkp.declareNamespace( xlink: "http://www.w3.org/1999/xlink" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
      mkp.declareNamespace( xml: "http://www.w3.org/XML/1998/namespace" )
      csw.GetRecordByIdResponse(
          'xsi:schemaLocation': "http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd" ) {

        def results = getResults( cswCommand )

        //println results

        for ( def x in results?.data )
        {
          def elementSetName = cswCommand.elementSetName ?: 'full'

          csw."${dcTag[elementSetName]}" {
            for ( def y in dcCols[elementSetName] )
            {
              if ( x[y] != null )
              {
                "dc:${y}"( formatAsString( x[y] ) )
              }
            }
            for ( def y in dctCols )
            {
              if ( x[y] != null )
              {
                "dct:${y}"( formatAsString( x[y] ) )
              }
            }
            if ( x.boundingBox != null )
            {
              def bounds = x.boundingBox.bounds

              "ows:BoundingBox"( crs: bounds?.proj?.id ) {
                "ows:LowerCorner"( "${bounds?.minX} ${bounds?.minY}" )
                "ows:UpperCorner"( "${bounds?.maxX} ${bounds?.maxY}" )
              }
            }
          }
        }
      }
    }

    return new StreamingMarkupBuilder( encoding: "UTF-8" ).bind( getRecById )
  }

  def describeRecord(CswCommand cswCommand)
  {
    def serverAddress = grailsApplication.config.omar.serverURL

    def descRec = {
      mkp.xmlDeclaration()
//      mkp.declareNamespace( apiso: "http://www.opengis.net/cat/csw/apiso/1.0" )
//      mkp.declareNamespace( atom: "http://www.w3.org/2005/Atom" )
      mkp.declareNamespace( csw: "http://www.opengis.net/cat/csw/2.0.2" )
      mkp.declareNamespace( dc: "http://purl.org/dc/elements/1.1/" )
      mkp.declareNamespace( dct: "http://purl.org/dc/terms/" )
//      mkp.declareNamespace( dif: "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/" )
//      mkp.declareNamespace( fgdc: "http://www.opengis.net/cat/csw/csdgm" )
//      mkp.declareNamespace( gco: "http://www.isotc211.org/2005/gco" )
//      mkp.declareNamespace( georss: "http://www.georss.org/georss" )
//      mkp.declareNamespace( gmd: "http://www.isotc211.org/2005/gmd" )
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
//      mkp.declareNamespace( inspire_common: "http://inspire.ec.europa.eu/schemas/common/1.0" )
//      mkp.declareNamespace( inspire_ds: "http://inspire.ec.europa.eu/schemas/inspire_ds/1.0" )
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )
//      mkp.declareNamespace( os: "http://a9.com/-/spec/opensearch/1.1/" )
      mkp.declareNamespace( ows: "http://www.opengis.net/ows" )
//      mkp.declareNamespace( rim: "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" )
//      mkp.declareNamespace( sitemap: "http://www.sitemaps.org/schemas/sitemap/0.9" )
//      mkp.declareNamespace( soapenv: "http://www.w3.org/2003/05/soap-envelope" )
//      mkp.declareNamespace( srv: "http://www.isotc211.org/2005/srv" )
//      mkp.declareNamespace( wrs: "http://www.opengis.net/cat/wrs/1.0" )
      mkp.declareNamespace( xlink: "http://www.w3.org/1999/xlink" )
      mkp.declareNamespace( xs: "http://www.w3.org/2001/XMLSchema" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )

      csw.DescribeRecordResponse(
          'xsi:schemaLocation': "http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd"
      ) {
        csw.SchemaComponent( schemaLanguage: "XMLSCHEMA", targetNamespace: "http://www.opengis.net/cat/csw/2.0.2" ) {
          xs.schema( id: "csw-record", targetNamespace: "http://www.opengis.net/cat/csw/2.0.2",
              elementFormDefault: "qualified", version: "2.0.2 2010-01-22" ) {
            xs.annotation {
              xs.appinfo {
                dc.identifier( "http://schemas.opengis.net/csw/2.0.2/record.xsd</dc:identifier" )
              }
              xs.documentation( 'xml:lang': "en", "This schema defines the basic record types that must be supported by all CSW implementations. These correspond to full, summary, and brief views based on DCMI metadata terms. CSW is an OGC Standard. Copyright (c) 2004,2010 Open Geospatial Consortium, Inc. All Rights Reserved. To obtain additional rights of use, visit http://www.opengeospatial.org/legal/ ." )
            }
            xs.import( namespace: "http://purl.org/dc/terms/", schemaLocation: "rec-dcterms.xsd" )
            xs.import( namespace: "http://purl.org/dc/elements/1.1/", schemaLocation: "rec-dcmes.xsd" )
            xs.import( namespace: "http://www.opengis.net/ows", schemaLocation: "../../ows/1.0.0/owsAll.xsd" )
            xs.element( name: "AbstractRecord", id: "AbstractRecord", type: "csw:AbstractRecordType", abstract: "true" )
            xs.complexType( name: "AbstractRecordType", id: "AbstractRecordType", 'abstract': "true" )
            xs.element( name: "DCMIRecord", type: "csw:DCMIRecordType", substitutionGroup: "csw:AbstractRecord" )

            xs.complexType( name: "DCMIRecordType" ) {

              xs.annotation {
                xs.documentation( 'xml:lang': "en", "This type encapsulates all of the standard DCMI metadata terms, including the Dublin Core refinements; these terms may be mapped to the profile-specific information model." )
              }
              xs.complexContent {
                xs.extension( base: "csw:AbstractRecordType" ) {
                  xs.sequence {
                    xs.group( ref: "dct:DCMI-terms" )
                  }
                }
              }
            }
            xs.element( name: "BriefRecord", type: "csw:BriefRecordType", substitutionGroup: "csw:AbstractRecord" )
            xs.complexType( name: "BriefRecordType", 'final': "#all" ) {
              xs.annotation {
                xs.documentation( 'xml:lang': "en", "This type defines a brief representation of the common record format. It extends AbstractRecordType to include only the dc:identifier and dc:type properties." )
              }
              xs.complexContent {
                xs.extension( base: "csw:AbstractRecordType" ) {
                  xs.sequence {
                    xs.element( ref: "dc:identifier", minOccurs: "1", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:title", minOccurs: "1", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:type", minOccurs: "0" )
                    xs.element( ref: "ows:BoundingBox", minOccurs: "0", maxOccurs: "unbounded" )
                  }
                }
              }
            }
            xs.element( name: "SummaryRecord", type: "csw:SummaryRecordType", substitutionGroup: "csw:AbstractRecord" )
            xs.complexType( name: "SummaryRecordType", 'final': "#all" ) {
              xs.annotation {
                xs.documentation( 'xml:lang': "en", "This type defines a summary representation of the common record format. It extends AbstractRecordType to include the core properties." )
              }
              xs.complexContent {
                xs.extension( base: "csw:AbstractRecordType" ) {
                  xs.sequence {
                    xs.element( ref: "dc:identifier", minOccurs: "1", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:title", minOccurs: "1", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:type", minOccurs: "0" )
                    xs.element( ref: "dc:subject", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:format", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "dc:relation", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "dct:modified", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "dct:abstract", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "dct:spatial", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "ows:BoundingBox", minOccurs: "0", maxOccurs: "unbounded" )
                  }
                }
              }
            }
            xs.element( name: "Record", type: "csw:RecordType", substitutionGroup: "csw:AbstractRecord" )
            xs.complexType( name: "RecordType", 'final': "#all" ) {
              xs.annotation {
                xs.documentation( 'xml:lang': "en", "This type extends DCMIRecordType to add ows:BoundingBox; it may be used to specify a spatial envelope for the catalogued resource." )
              }
              xs.complexContent {
                xs.extension( base: "csw:DCMIRecordType" ) {
                  xs.sequence {
                    xs.element( name: "AnyText", type: "csw:EmptyType", minOccurs: "0", maxOccurs: "unbounded" )
                    xs.element( ref: "ows:BoundingBox", minOccurs: "0", maxOccurs: "unbounded" )
                  }
                }
              }
            }
            xs.complexType( name: "EmptyType" )
          }
        }
      }
    }

    return new StreamingMarkupBuilder( encoding: "UTF-8" ).bind( descRec )
  }

  private static String formatAsString(def value)
  {
    String results = null

    switch ( value?.class )
    {
    case java.sql.Timestamp:
      results = value.format( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone( 'GMT' ) )
      break
    default:
      results = "${value}"
    }

    return results
  }
}
