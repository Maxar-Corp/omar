package org.ossim.omar.core

import org.apache.commons.collections.map.CaseInsensitiveMap
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.WritableRaster
import javax.imageio.ImageTypeSpecifier
import java.awt.image.SampleModel
import java.awt.image.IndexColorModel
import java.awt.image.DataBuffer;
import grails.converters.JSON
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class Utility implements ApplicationContextAware
{
  ApplicationContext applicationContext

//here is the code for the method
//here is the code for the method
  static def zipDir( String dir2zip, String outputFile, String prefix = null )
  {
    def zos = new ZipOutputStream( new FileOutputStream( outputFile ) )
    Utility.zipDir( dir2zip, zos, prefix );
    zos.close();
  }

  static def zipDir( String dir2zip, ZipOutputStream zos, String prefix = null )
  {
    def result = true
    try
    {
      //create a new File object based on the directory we
      def zipDirTemp = new File( dir2zip, prefix ?: null );
      //get a listing of the directory content
      String[] dirList = zipDirTemp.list();
      byte[] readBuffer = new byte[2048];
      int bytesIn = 0;
      //loop through dirList, and zip the files
      for ( int i = 0; i < dirList.length; i++ )
      {
        File f = new File( zipDirTemp, dirList[i] );
        if ( f.isDirectory() )
        {
          //if the File object is a directory, call this
          //function again to add its content recursively
          String filePath = f.getPath();
          zipDir( filePath, zos );
          //loop again
          continue;
        }
        //if we reached here, the File object f was not a directory
        //create a FileInputStream on top of f
        FileInputStream fis = new FileInputStream( f );
        //create a new zip entry
        ZipEntry anEntry = new ZipEntry( f.getPath() );
        //place the zip entry in the ZipOutputStream object
        zos.putNextEntry( anEntry );
        //now write the content of the file to the ZipOutputStream
        while ( ( bytesIn = fis.read( readBuffer ) ) != -1 )
        {
          zos.write( readBuffer, 0, bytesIn );
        }
        //close the Stream
        fis.close();
      }
    }
    catch ( Exception e )
    {
      result = false;
    }
  }

  static void writeStreamToOutputStream( def inputStream, def output, def blockSize = 4096 )
  {
    byte[] buffer = new byte[blockSize]; // To hold file contents
    int bytes_read; // How many bytes in buffer

    while ( ( bytes_read = inputStream.read( buffer ) ) != -1 )
    {
      // Read until EOF
      output.write( buffer, 0, bytes_read ); // write
    }

  }

  static void writeFileToOutputStream( def inputFile, def output, def blockSize = 4096 )
  {
    def from = new FileInputStream( inputFile ); // Create input stream
    writeStreamToOutputStream( from, output, blockSize )
    from.close()
    from = null
  }

  static def createTypeMap( def featureClass )
  {
    def fields = featureClass.declaredFields
    def typeMap = [:]

    for ( field in fields )
    {
      if ( field.name != "metaClass" )
      {
        typeMap[field.name] = field.type
      }
    }

    return typeMap
  }

  static void removeEmptyParams( def params )
  {
    def nullMap = params?.findAll {entry -> ( entry.value == "" || entry.value == "null" )}
    nullMap?.each {params?.remove( it.key )}
  }

  static void simpleCaseInsensitiveBind( def target, def paramMap )
  {
    def keys = target.properties.keySet()
    def tempParams = new CaseInsensitiveMap( paramMap )

    keys.each {
      def value = tempParams.get( it )
      if ( value )
      {
        target.setProperty( "${it}", value )
      }
    }
  }

  static def convertToColorIndexModel( def dataBuffer, def width, def height, def transparentFlag )
  {
    ImageTypeSpecifier isp = ImageTypeSpecifier.createGrayscale( 8, DataBuffer.TYPE_BYTE, false );
    ColorModel colorModel
    SampleModel sampleModel = isp.getSampleModel( width, height )
    if ( !transparentFlag )
    {
      colorModel = isp.getColorModel();
    }
    else
    {
      int[] lut = new int[256]
      ( 0..<lut.length ).each {i ->
        lut[i] = ( ( 0xff << 24 ) | ( i << 16 ) | ( i << 8 ) | ( i ) );
      }
      lut[0] = 0xff000000
      colorModel = new IndexColorModel( 8, lut.length, lut, 0, true, 0, DataBuffer.TYPE_BYTE )
    }
    WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, dataBuffer, null )
    return new BufferedImage( colorModel, raster, false, null );

  }
  /**
   * This will extract all WMS paramters this includes GetMap, GetCapabilities, and
   * GetFeatureInfo REQUESTS.  It will do this in a case insensitive way and return
   * a new map
   */
  static Map keepOnlyWMSParams( def map )
  {
    def tempParams = new CaseInsensitiveMap()
    map.each { tempParams.put( it.key, it.value )}

    return tempParams.subMap( ["version", "request", "layers", "styles",
            "srs", "crs", "bbox", "width", "height", "format",
            "transparent", "bgcolor", "exceptions", "time",
            "elevation", "updatesequence", "query_layers",
            "info_format", "i", "j"
    ] )
  }

  static Map keepOnlyParams( def map, def params )
  {
    def tempParams = new CaseInsensitiveMap()
    map.each { tempParams.put( it.key, it.value )}
    def listOfParams = ["version", "request", "layers", "styles",
            "srs", "bbox", "width", "height", "format",
            "transparent", "bgcolor", "exceptions", "time",
            "elevation"
    ]
    if ( params )
    {
      params.each {
//        println "adding ${it}"
        listOfParams.add( it )
      }
    }

    return tempParams.subMap( listOfParams )
  }

  public static def parseSearchTag( def name, def value )
  {
    def nameValue = "${name}${value}"

    def pattern = /(.*\.)?(.*)=(.*)/
    def matcher = nameValue =~ pattern

    def results = [:]

    if ( matcher )
    {
      if ( matcher[0][1] )
      {
        results["property"] = matcher[0][1] - "."
        results["tag"] = matcher[0][2]
        results["content"] = matcher[0][3]
      }
      else
      {
        results["property"] = matcher[0][2]
        results["value"] = matcher[0][3]
      }
    }
    else
    {
      results["property"] = name
      results["value"] = value
    }

    return results
  }

  static def generateMapForOgcFilterQuery( def domainClass, def includeList = null, def excludeList = null, def overrideMap = null )
  {
    def result = [:]
    def typeList = []
    def labelList = []
    def columnNameList = []
    def nameList = []
    def defaultContraintList = []
    result = [:]
    domainClass.properties.each { property ->
      def domainType = null
      def xmlType = null
      def defaultConstraint = ""
      switch ( property.type )
      {
      case Double.class:
        domainType = "Double"
        xmlType = "xsd:double"
        defaultConstraint = "="
        break
      case Integer.class:
        domainType = "Double"
        xmlType = "xsd:int"
        defaultConstraint = "="
        break
      case Long.class:
        domainType = "Double"
        xmlType = "xsd:long"
        defaultConstraint = "="
        break
      case Date.class:
        domainType = "Date"
        xmlType = "xsd:dateTime"
        defaultConstraint = "<"
        break
      case org.joda.time.DateTime.class:
        domainType = "DateTime"
        xmlType = "xsd:dateTime"
        defaultConstraint = "<"
        break
      case String.class:
        domainType = "String"
        xmlType = "xsd:string"
        defaultConstraint = "like"
        break

      case com.vividsolutions.jts.geom.Geometry.class:
        domainType = "Geometry"
        xmlType = "gml:PolygonPropertyType"
        break
      }
      if ( xmlType )
      {

        def useProperty = true
        if ( includeList )
        {
          useProperty = property.name in includeList
        }
        else if ( excludeList )
        {
          useProperty = !( property.name in excludeList )
        }
        if ( useProperty )
        {
          def name = property.name
          def label = property.naturalName
          def description = ""
          if ( overrideMap )
          {
            if ( overrideMap."${name}" )
            {
              label = overrideMap."${name}".label ?: label
              description = overrideMap."${name}".description ?: description
            }
          }
          typeList << xmlType
          nameList << name
          name = name.replaceAll( "[a-z][A-Z]", {v -> "${v[0]}_${v[1].toLowerCase()}"} )
          columnNameList << name
          labelList << label
          defaultContraintList << defaultConstraint
//          result.PropertyNames."${name}" = [label:label,
//                                            type:xmlType,
//                                            description:description]
        }
      }
    }
    result.nameList = nameList
    result.labelList = labelList
    result.typeList = typeList
    result.columnNameList = columnNameList
    result.defaultContraintList = defaultContraintList
    result
  }

  static def generateJSONForOgcFilterQuery( def domainClass, def includeList = null, def excludeList = null, def overrideMap = null )
  {
    generateMapForOgcFilterQuery( domainClass, includeList, excludeList, overrideMap ) as JSON
  }

  void setApplicationContext( ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext
  }

  def lookupDomainInfo( def className )
  {
    def service = applicationContext.getBean( "domainClassLookupService" )

    service.createTypeMapping( className )
  }
}