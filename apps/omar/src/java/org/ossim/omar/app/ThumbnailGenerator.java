package org.ossim.omar.app;

import joms.oms.*;

public class ThumbnailGenerator
{
  static public boolean writeImageSpaceThumbnail( String inputFile,
                                                  String entryId,
                                                  String outFile,
                                                  String writerType,
                                                  int xRes,
                                                  int yRes,
                                                  String histogramFile,
                                                  String stretchType,
                                                  boolean keepAspectFlag )
  {
    boolean status = false;
    /*
    ossimImageHandler handler = ossimImageHandlerRegistry.instance().open( inputFile );
    ossimImageFileWriter writer = ossimImageWriterFactoryRegistry.instance().createWriter( writerType );

    if ( ( handler != null ) && ( writer != null ) )
    {
      if ( handler.setCurrentEntry( Integer.valueOf( entryId ) ) )
      {

        String histFile = histogramFile;
        if ( ( histFile.length() == 0 ) && ( stretchType.length() != 0 ) )
        {
          histFile = handler.createDefaultHistogramFilename().toString();
        }
        ossimImageSource chain = Util.newEightBitImageSpaceThumbnailChain( handler, xRes, yRes, histFile, stretchType, keepAspectFlag );
        if ( chain != null )
        {
          writer.connectMyInputTo( chain );
          writer.setFilename( new ossimFilename( outFile ) );
          writer.setWriteExternalGeometryFlag( false );
          return writer.execute();
        }
      }

    }
    */

    status = Util.writeImageSpaceThumbnail( inputFile, Integer.parseInt( entryId ), outFile, writerType,
        xRes, yRes, histogramFile, stretchType, keepAspectFlag );
    
    return status;
  }

  
}
