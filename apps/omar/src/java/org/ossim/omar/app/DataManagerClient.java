package org.ossim.omar.app; /**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jan 19, 2010
 * Time: 10:02:09 AM
 * To change this template use File | Settings | File Templates.
 */


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DataManagerClient
{
  public static final String ADD_RASTER = "addRaster";
  public static final String ADD_VIDEO = "addVideo";
  public static final String ADD_DATAINFO = "add";
  public static final String REMOVE_RASTER = "removeRaster";
  public static final String REMOVE_VIDEO = "removeVideo";

  private String omarInstance;

  public DataManagerClient( String omarInstance )
  {
    this.omarInstance = omarInstance;
  }

    public void addRaster( String filename )
    {
      String address = omarInstance + "/dataManager/" + ADD_RASTER;
      Map<String, String> params = new HashMap<String, String>();

      params.put( "filename", filename );

      try
      {
        String response = doPost( address, params );

        System.out.println( response );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }                            
    }
    public void addDataInfo( String filename )
    {
      String address = omarInstance + "/dataManager/" + ADD_DATAINFO;
        File file = new File("infilename");

        // Get the number of bytes in the file
        long length = file.length();
        Map<String, String> params = new HashMap<String, String>();
        String fileContents = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str;
            //read file into a string
            while ((str = in.readLine()) != null)
            {
                fileContents += str;
            }
            in.close();
        }
        catch (IOException e)
        {
        }
      params.put( "datainfo", fileContents );

      try
      {
        String response = doPost( address, params );

        System.out.println( response );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }

  public void addVideo( String filename )
  {
    String address = omarInstance + "/dataManager/" + ADD_VIDEO;
    Map<String, String> params = new HashMap<String, String>();

    params.put( "filename", filename );

    try
    {
      String response = doPost( address, params );

      System.out.println( response );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public void removeRaster( String filename )
  {
    String address = omarInstance + "/dataManager/" + REMOVE_RASTER;
    Map<String, String> params = new HashMap<String, String>();

    params.put( "filename", filename );

    try
    {
      String response = doPost( address, params );

      System.out.println( response );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public void removeVideo( String filename )
  {
    String address = omarInstance + "/dataManager/" + REMOVE_VIDEO;
    Map<String, String> params = new HashMap<String, String>();

    params.put( "filename", filename );

    try
    {
      String response = doPost( address, params );

      System.out.println( response );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }


  public static String doPost( String address, Map<String, String> params ) throws IOException
  {
    // Encode params
    String data = encodeParams( params );

    // Send data
    URL url = new URL( address );
    URLConnection conn = url.openConnection();

    conn.setDoOutput( true );

    OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );

    wr.write( data );
    wr.flush();

    // Get the response
    BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
    String line;
    StringBuffer buffer = new StringBuffer();

    while ( ( line = rd.readLine() ) != null )
    {
      buffer.append( line );
    }

    wr.close();
    rd.close();

    return buffer.toString();
  }

  public static String encodeParams( Map<String, String> dataMap )
      throws UnsupportedEncodingException
  {
    // Construct data
    StringBuffer data = new StringBuffer();

    for ( String key : dataMap.keySet() )
    {
      String value = dataMap.get( key );

      if ( data.length() > 0 )
      {
        data.append( "&" );
      }

      data.append( URLEncoder.encode( key, "UTF-8" ) );
      data.append( "=" );
      data.append( URLEncoder.encode( value, "UTF-8" ) );
    }

    return data.toString();
  }

  public static void main( String[] args )
  {
    if ( args.length >= 3 )
    {
      String omarInstance = args[0];
      DataManagerClient client = new DataManagerClient( omarInstance );
      String command = args[1];

      for ( int i = 2; i < args.length; i++ )
      {
        String filename = args[i];

          if ( command.equalsIgnoreCase( DataManagerClient.ADD_RASTER ) )
           {
             client.addRaster( filename );
           }
          if ( command.equalsIgnoreCase( DataManagerClient.ADD_DATAINFO ) )
           {
             client.addDataInfo( filename );
           }
         else if ( command.equalsIgnoreCase( DataManagerClient.ADD_VIDEO ) )
        {
          client.addVideo( filename );
        }
        else if ( command.equalsIgnoreCase( DataManagerClient.REMOVE_RASTER ) )
        {
          client.removeRaster( filename );

        }
        else if ( command.equalsIgnoreCase( DataManagerClient.REMOVE_VIDEO ) )
        {
          client.removeVideo( filename );
        }
        else
        {
          System.err.println( "Unknown command: " + command );
          break;
        }
      }
    }
    else
    {
      System.out.println( "Usage: DataMangerClient <omar instance>  <command> <filename>*" );
      System.out.println( "\t<omar instance>: URL to OMAR server (i.e. http://<server>[:port]/omar-2.0" );
      System.out.println( "\t<command>: Action to perform (i.e. " + ADD_RASTER + "|" + ADD_VIDEO + "|" + ADD_DATAINFO +"|"
          + REMOVE_RASTER + "|" + REMOVE_VIDEO);
      System.out.println( "\t<filename>*: One or more filenames to process" );

    }
  }
}
