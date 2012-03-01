package org.ossim.omar.ogc

import org.geotools.data.DefaultTransaction
import org.geotools.data.simple.SimpleFeatureStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.geotools.feature.FeatureCollections
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import org.geotools.graph.util.ZipUtil
import org.ossim.omar.core.Utility

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 12/10/10
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
class ExportUtils
{

  static def createZipFileSet(newFile)
  {
    def srcDir = newFile.parentFile
    def prefix = FilenameUtils.getBaseName(newFile.name)
    def zipfile = new File(srcDir.parentFile, "${prefix}.zip")
    def filenames = srcDir.listFiles()?.absolutePath
    def archFilenames = filenames.collect { "${prefix}${it - srcDir.absolutePath}" }

    ZipUtil.zip(zipfile.absolutePath, filenames as String[], archFilenames as String[])
    FileUtils.deleteDirectory(newFile.parentFile)

    return zipfile
  }

  static def createTempDir(def prefix, def suffix, def directory)
  {
    def tempDir = File.createTempFile(prefix, suffix, directory)

    tempDir.delete()
    tempDir.mkdirs()

    return tempDir
  }


  static def addFeatures(def newDataStore, collection)
  {
    def transaction = new DefaultTransaction("create");
    def typeName = newDataStore.getTypeNames()[0];
    def featureSource = newDataStore.getFeatureSource(typeName);

    if ( featureSource instanceof SimpleFeatureStore )
    {
      def featureStore = featureSource;

      featureStore.setTransaction(transaction);
      try
      {
        featureStore.addFeatures(collection);
        transaction.commit();

      }
      catch (Exception problem)
      {
        problem.printStackTrace();
        transaction.rollback();

      }
      finally
      {
        transaction.close();
      }
    }
    else
    {
      System.out.println(typeName + " does not support read/write access");
    }
  }

  static def createFeatures(def featureType, def objects, def attributes, def labels)
  {
    def collection = FeatureCollections.newCollection();
    def featureBuilder = new SimpleFeatureBuilder(featureType);

    for ( object in objects )
    {
      for ( attribute in attributes )
      {
        featureBuilder.add(object[attribute]);
      }

      def feature = featureBuilder.buildFeature(null);

      collection.add(feature);
    }

    return collection
  }



  static def createShapefile(def newFile, def featureType)
  {
    def dataStoreFactory = new ShapefileDataStoreFactory();

    def params = [
            url: newFile.toURI().toURL(),
            "create spatial index": true
    ]

    def newDataStore = dataStoreFactory.createNewDataStore(params);

    newDataStore.createSchema(featureType);

    return newDataStore
  }


  static def createFeatureType(def featureClass, def attributes, def labels)
  {

    def builder = new SimpleFeatureTypeBuilder();

    builder.name = featureClass.name
    builder.CRS = DefaultGeographicCRS.WGS84  // <- Coordinate reference system

    // add attributes in order
    //builder.length(15).add("acquisition_date", Date.class); // <- 15 chars width for name field

    def typeMap = Utility.createTypeMap(featureClass)

    for ( i in 0..<attributes.size() )
    {
      def atttributeClass = typeMap[attributes[i]]

      if ( atttributeClass )
      {
        builder.add(labels[i], atttributeClass);
      }
      else {
        println attributes[i]
      }
    }

    // build the type
    def featureType = builder.buildFeatureType();

    return featureType;
  }
}
