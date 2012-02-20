package org.ossim.omar

import java.text.SimpleDateFormat

import joms.oms.ossimGpt
import org.ossim.omar.raster.RasterDataSet

import org.ossim.omar.video.VideoDataSet

import geoscript.geom.Bounds
import org.ossim.omar.core.DateUtil
import org.ossim.omar.core.Repository
import org.ossim.omar.stager.OmsInfoParser

class DataInjectorTests extends GroovyTestCase
{
  boolean transactional = false
  def backgroundService
  def bgThreadManager
  int seedValue = 0

  def dateUtil = new DateUtil()

  static final IMAGE_CATEGORIES = [
      VIS: "Visible Imagery MONO, RGB, If geo-referenced, presence of",
      SL: "Side-Looking Radar RGB/LUT,YCbCr601, spatial location and positional",
      TI: "Thermal Infrared MULTI accuracy is recommended.",
      FL: "Forward Looking Infrared",
      RD: "Radar",
      EO: "Electro-optical",
      OP: "Optical",
      HR: "High Resolution Radar",
      HS: "Hyperspectral",
      CP: "Color Frame Photography",
      BP: "Black/White Frame Photography",
      SAR: "Synthetic Aperture Radar",
      SARIQ: "SAR Radio Hologram",
      IR: "Infrared",
      MS: "Multispectral",
      FP: "Fingerprints",
      MRI: "Magnetic Resonance Imagery",
      XRAY: "X-rays",
      CAT: "CAT Scans",
      VD: "Video"

  ]

  def buildRasterXml(def mapping)
  {
    def result = """<oms>
   <dataSets>
      <RasterDataSet>
         <fileObjects>
            <RasterFile type="main" format=\"${mapping.file_type}\">
                <name>${mapping.file}</name>
            </RasterFile>
         </fileObjects>
         <rasterEntries>
            <RasterEntry>
               <fileObjects>
                  <RasterEntryFile type="overview">
                     <name>${mapping.file_noext}.ovr</name>
                  </RasterEntryFile>
                  <RasterEntryFile type="histogram">
                     <name>${mapping.file_noext}.his</name>
                  </RasterEntryFile>
                  <RasterEntryFile type="omd">
                     <name>${mapping.file_noext}.omd</name>
                  </RasterEntryFile>
               </fileObjects>
               <entryId>0</entryId>
               <width>${mapping.width}</width>
               <height>${mapping.height}</height>
               <numberOfBands>4</numberOfBands>
               <numberOfResLevels>7</numberOfResLevels>
               <bitDepth>11</bitDepth>
               <dataType>uint</dataType>
               <gsd unit=\"${mapping.gsd_unit}\" dx=\"${mapping.gsd_dx}\" dy=\"${mapping.gsd_dy}\"/>
               <groundGeom srs=\"${mapping.srs}\">${mapping.geom}</groundGeom>
               <TimeStamp>
                  <when>${mapping.iso_start_date}</when>
               </TimeStamp>
               <metadata>
                  <filepath>${mapping.path}</filepath>
                  <file_type>${mapping.file_type}</file_type>
                  <class_name>ossimNitfTileSource</class_name>
                  <tgtid>${mapping.tgtid}</tgtid>
                  <enabled>1</enabled>
                  <entry>0</entry>
                  <enable_cache>0</enable_cache>
                  <version>02.00</version>
                  <clevel>04</clevel>
                  <ostaid>DG</ostaid>
                  <ftitle>${mapping.title}</ftitle>
                  <fsclas>U</fsclas>
                  <fscop>00000</fscop>
                  <fscpys>00000</fscpys>
                  <encryp>0</encryp>
                  <oname>~~~DigitalGlobe</oname>
                  <ophone>+1(800)496-1225</ophone>
                  <RPC00B>
                     <ERR_BIAS>0082.22</ERR_BIAS>
                     <ERR_RAND>0000.48</ERR_RAND>
                     <LINE_OFF>004472</LINE_OFF>
                     <SAMP_OFF>04114</SAMP_OFF>
                     <LAT_OFF>+33.4988</LAT_OFF>
                     <LONG_OFF>+044.4176</LONG_OFF>
                     <HEIGHT_OFF>+0032</HEIGHT_OFF>
                     <LINE_SCALE>004585</LINE_SCALE>
                     <SAMP_SCALE>04167</SAMP_SCALE>
                     <LAT_SCALE>+00.0972</LAT_SCALE>
                     <LONG_SCALE>+000.1072</LONG_SCALE>
                     <HEIGHT_SCALE>+0500</HEIGHT_SCALE>
                     <LINE_DEN>1=+0.100000E+1, 10=+0.867430E-7, 11=-0.275673E-6, 12=+0.219225E-5, 13=-0.126615E-7, 14=-0.375694E-7, 15=+0.191249E-5, 16=+0.122368E-5, 17=+0.843962E-6, 18=-0.213671E-5, 19=+0.494775E-6, 2=+0.458927E-5, 20=-0.551516E-5, 3=+0.524360E-3, 4=+0.161583E-3, 5=+0.337472E-6, 6=-0.583822E-6, 7=-0.291401E-5, 8=-0.883754E-6, 9=+0.183139E-6</LINE_DEN>
                     <LINE_NUM>1=+0.106668E-2, 10=-0.143257E-5, 11=-0.110788E-5, 12=-0.501320E-5, 13=-0.379273E-6, 14=-0.198992E-6, 15=-0.110620E-5, 16=-0.131710E-5, 17=-0.493314E-6, 18=+0.106012E-4, 19=-0.208982E-5, 2=+0.507019E-2, 20=+0.106966E-5, 3=-0.979318E+0, 4=+0.255963E-1, 5=+0.358801E-5, 6=-0.169075E-3, 7=+0.190863E-3, 8=-0.472821E-3, 9=-0.524737E-3</LINE_NUM>
                     <SAMP_DEN>1=+0.100000E+1, 10=-0.389501E-6, 11=-0.759956E-6, 12=+0.875117E-5, 13=+0.431554E-6, 14=-0.292017E-5, 15=+0.112437E-6, 16=-0.137879E-7, 17=-0.559828E-7, 18=-0.182168E-4, 19=-0.343586E-7, 2=+0.683582E-4, 20=-0.258935E-7, 3=+0.554846E-3, 4=-0.607273E-3, 5=-0.699471E-7, 6=+0.987560E-6, 7=-0.151731E-6, 8=-0.116104E-5, 9=+0.108728E-5</SAMP_DEN>
                     <SAMP_NUM>1=-0.133797E-3, 10=+0.957945E-6, 11=+0.685068E-6, 12=+0.253484E-6, 13=-0.970141E-6, 14=+0.212355E-6, 15=+0.153085E-6, 16=+0.265919E-6, 17=+0.000000E+0, 18=-0.218076E-5, 19=+0.102823E-5, 2=+0.995763E+0, 20=-0.245140E-6, 3=+0.602371E-2, 4=-0.102660E-1, 5=-0.559988E-3, 6=+0.618222E-3, 7=-0.343211E-3, 8=+0.737314E-4, 9=+0.115797E-4</SAMP_NUM>
                  </RPC00B>
                  <STDIDC>
                     <ACQDATE>${mapping.aqdate}</ACQDATE>
                     <MISSION>QB02</MISSION>
                     <PASS>06</PASS>
                     <OPNUM>000</OPNUM>
                     <STARTSEGMENT>AA</STARTSEGMENT>
                     <REPRONUM>00</REPRONUM>
                     <REPLAYREGEN>000</REPLAYREGEN>
                     <STARTCOLUMN>001</STARTCOLUMN>
                     <STARTROW>00001</STARTROW>
                     <ENDSEGMENT>AA</ENDSEGMENT>
                     <ENDCOLUMN>009</ENDCOLUMN>
                     <ENDROW>00009</ENDROW>
                     <LOCATION>3336N04425E</LOCATION>
                  </STDIDC>
                  <USE00A>
                     <ANGLETONORTH>270</ANGLETONORTH>
                     <MEANGSD>094.5</MEANGSD>
                     <DYNAMICRANGE>02047</DYNAMICRANGE>
                     <OBLANG>28.89</OBLANG>
                     <ROLLANG>-11.75</ROLLANG>
                     <NREF>00</NREF>
                     <REVNUM>07994</REVNUM>
                     <NSEG>001</NSEG>
                     <MAXLPSEG>008933</MAXLPSEG>
                     <SUNEL>+51.1</SUNEL>
                     <SUNAZ>143.1</SUNAZ>
                  </USE00A>
                  <ICHIPB>
                     <XFRM_FLAG>00</XFRM_FLAG>
                     <SCALE_FACTOR>0001.00000</SCALE_FACTOR>
                     <ANAMRPH_CORR>00</ANAMRPH_CORR>
                     <SCANBLK_NUM>00</SCANBLK_NUM>
                     <OP_ROW_11>00000000.500</OP_ROW_11>
                     <OP_COL_11>00000000.500</OP_COL_11>
                     <OP_ROW_12>00000000.500</OP_ROW_12>
                     <OP_COL_12>00004090.500</OP_COL_12>
                     <OP_ROW_21>00004095.500</OP_ROW_21>
                     <OP_COL_21>00000000.500</OP_COL_21>
                     <OP_ROW_22>00004095.500</OP_ROW_22>
                     <OP_COL_22>00004090.500</OP_COL_22>
                     <FI_ROW_11>00004096.500</FI_ROW_11>
                     <FI_COL_11>00004096.500</FI_COL_11>
                     <FI_ROW_12>00004096.500</FI_ROW_12>
                     <FI_COL_12>00008186.500</FI_COL_12>
                     <FI_ROW_21>00008191.500</FI_ROW_21>
                     <FI_COL_21>00004096.500</FI_COL_21>
                     <FI_ROW_22>00008191.500</FI_ROW_22>
                     <FI_COL_22>00008186.500</FI_COL_22>
                     <FI_ROW>00008933</FI_ROW>
                     <FI_COL>00008245</FI_COL>
                  </ICHIPB>
                  <iid1>${mapping.iid1}</iid1>
                  <idatim>${mapping.aqdate}</idatim>
                  <iid2>${mapping.iid1}</iid2>
                  <isclas>U</isclas>
                  <encryp>0</encryp>
                  <isorce>${mapping.mission}</isorce>
                  <pvtype>INT</pvtype>
                  <irep>MULTI</irep>
                  <icat>${mapping.icat}</icat>
                  <abpp>11</abpp>
                  <pjust>R</pjust>
                  <icords>U</icords>
                  <igeolo></igeolo>
                  <nicom>5</nicom>
                  <ic>NC</ic>
                  <isync>0</isync>
                  <imode>S</imode>
                  <nbpr>0004</nbpr>
                  <nbpc>0004</nbpc>
                  <nppbh>1024</nppbh>
                  <nppbv>1024</nppbv>
                  <nbpp>16</nbpp>
                  <idlvl>001</idlvl>
                  <ialvl>000</ialvl>
                  <iloc>0000000000</iloc>
                  <imag>1.0</imag>
                  <source>QB02</source>
                  <image_date>${mapping.image_date}</image_date>
                  <image_title>${mapping.title}</image_title>
               </metadata>
            </RasterEntry>
         </rasterEntries>
      </RasterDataSet>
   </dataSets>
</oms>"""

    return result
  }

  def buildVideoXml(def mapping)
  {
    def result = """<oms>
   <dataSets>
      <VideoDataSet>
         <fileObjects>
            <VideoFile type="main" format=\"${mapping.file_type}\">
                <name>${mapping.file}</name>
            </VideoFile>
         </fileObjects>
         <width>720</width>
         <height>480</height>
         <groundGeom srs=\"${mapping.srs}\">${mapping.geom}</groundGeom>
         <TimeSpan>
            <begin>${mapping.iso_start_date}</begin>
            <end>${mapping.iso_end_date}</end>
         </TimeSpan>
         <metadata>
            <organizational_program_number>+4</organizational_program_number>
            <security_classification>UNCLASSIFIED//</security_classification>
            <release_instructions>US</release_instructions>
            <security_caveats>FOUO</security_caveats>
            <classification_comment>SIC=#</classification_comment>
            <original_producer_name>sa</original_producer_name>
            <image_source_sensor>EO Spotter</image_source_sensor>
         </metadata>
      </VideoDataSet>
   </dataSets>
</oms>"""

    return result;
  }

  synchronized def findRepositoryForFile(def file)
  {
    def repositories = (Repository.list()?.sort { it.baseDir.size() })?.reverse()
    def repository = null

    if ( repositories )
    {
      def filename = file?.absolutePath

      for ( it in repositories )
      {
        if ( filename?.startsWith(it.baseDir) )
        {
          repository = it
          break
        }
      }
    }

    if ( !repository )
    {
      repository = new Repository(baseDir: file?.parentFile?.absolutePath)
      repository.save(flush: true)
    }

    return repository
  }

  def buildMissionMap(int numberOfMissions)
  {
    def missionMap = [:]
    int idx = 0;
    for ( idx = 0; idx < numberOfMissions; ++idx )
    {
      missionMap.put(idx, "Mission${idx}")
    }

    return missionMap;
  }

  void addRaster(def mapping)
  {
    mapping.file = mapping.file_noext + ".ntf"
    mapping.file_type = "nitf"
    def xml = buildRasterXml(mapping)
    def oms = new XmlSlurper().parseText(xml)
    def repository = findRepositoryForFile(new File("/"))
    def omsInfoParser = new OmsInfoParser()

    def rasterDataSets = omsInfoParser.processRasterDataSets(oms, repository)
    rasterDataSets.each {rasterDataSet ->
      RasterDataSet result = rasterDataSet.save()
      //       assertNotNull("Object not created: " + rasterDataSet, result)
    }
  }

  void addVideo(def mapping)
  {
    mapping.file = mapping.file_noext + ".mpeg"
    mapping.file_type = "mpeg"
    def xml = buildVideoXml(mapping)
    def oms = new XmlSlurper().parseText(xml)
    def omsInfoParser = new OmsInfoParser()

    def repository = findRepositoryForFile(new File("/"))

    def dataSets = omsInfoParser.processVideoDataSets(oms, repository)
    dataSets.each {videoDataSet ->
      VideoDataSet result = videoDataSet.save()
//        assertNotNull("Object not created: " + videoDataSet, result)
    }
  }

  void addRasters(int count)
  {
    def missions = buildMissionMap(100)
    def random = new Random()
    def gpt = new ossimGpt(0.0, 0.0);
    def mpd = gpt.metersPerDegree();

    SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    SimpleDateFormat imageDate = new SimpleDateFormat("dd-MM-yyyy")
    SimpleDateFormat acqDate = new SimpleDateFormat("yyyyMMddhhmmss")
    def icatList = IMAGE_CATEGORIES.keySet().asList()

    count.times {cellId ->

      int missionKey = random.nextInt(missions.size());
      Date date = dateUtil.createDateBetweenYears(2000, 2010);
      double latCenter = -89.5 + 179 * random.nextFloat()
      double lonCenter = -179.5 + 359.5 * random.nextFloat()
      double latDelta = random.nextFloat() * 0.5;
      double lonDelta = 0.0;

      if ( latDelta < 0.01 )
      {
        latDelta = 0.1;
      }

      lonDelta = latDelta

      double minLat = latCenter - latDelta;
      double minLon = lonCenter - lonDelta;
      double maxLat = latCenter + latDelta;
      double maxLon = lonCenter + lonDelta;
      int pixelsWide = 4096;
      int pixelsHigh = 4096;

      def mapping = [file: "",
          file_noext: "/data/test_${cellId}",
          geom: new Bounds(minLon, minLat, maxLon, maxLat).polygon.wkt,
          srs: "4326",
          title: "Test for Cell ${cellId}",
          tgtid: "Cell_${cellId}",
          iid1: "Cell_${cellId}",
          icat: icatList.get(random.nextInt(IMAGE_CATEGORIES.size())),
          mission: missions.get(missionKey) as String,
          iso_start_date: isoDate.format(date),
          iso_end_date: isoDate.format(date),
          image_date: imageDate.format(date),
          aqdate: acqDate.format(date),
          path: "/data",
          file_type: "nitf",
          width: pixelsWide as String,
          height: pixelsHigh as String,
          gsd_unit: "meters",
          gsd_dx: (((maxLon - minLon) / pixelsWide) * mpd.x) as String,
          gsd_dy: (((maxLat - minLat) / pixelsHigh) * mpd.y) as String
      ]

      backgroundService.execute("ImageInsert", {
        addRaster(mapping)
      })
    }

  }

  void addVideos(int count)
  {
    def missions = buildMissionMap(100)
    def random = new Random()

    SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    count.times {cellId ->
      int missionKey = random.nextInt(missions.size());
      double latCenter = -89.5 + 179 * random.nextFloat()
      double lonCenter = -179.5 + 359.5 * random.nextFloat()
      double latDelta = random.nextFloat() * 0.5;
      double lonDelta = random.nextFloat() * 0.5;
      if ( latDelta < 0.01 )
      {
        latDelta = 0.1;
      }
      lonDelta = latDelta
      double minLat = latCenter - latDelta;
      double minLon = lonCenter - lonDelta;
      double maxLat = latCenter + latDelta;
      double maxLon = lonCenter + lonDelta;
      int pixelsWide = 720;
      int pixelsHigh = 480;

      def calendar = Calendar.instance
      def startDate = dateUtil.createDateBetweenYears(2000, 2010)

      calendar.time = startDate.clone()
      calendar.roll(Calendar.MINUTE, true)

      def endDate = calendar.time

      def mapping = [file: "",
          file_noext: "/data/test_${cellId}",
          geom: new Bounds(minLon, minLat, maxLon, maxLat).polygon.wkt,
          srs: "4326",
          iso_start_date: isoDate.format(startDate),
          iso_end_date: isoDate.format(endDate),
          path: "/data",
          file_type: "mpeg",
          width: pixelsWide as String,
          height: pixelsHigh as String,
      ]

      backgroundService.execute("VideoInsert", {
        addVideo(mapping)
      })
    }

  }

  void testSomething()
  {
    int rasterCount = 1000
    int videoCount = 1000
    addVideos(videoCount);
    while ( VideoDataSet.count() < videoCount )
    {
      Thread.sleep(100);
    }
    addRasters(rasterCount);

    while ( RasterDataSet.count() < rasterCount )
    {
      Thread.sleep(100);
    }
  }


}
