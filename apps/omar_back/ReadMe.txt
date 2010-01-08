OMAR 2.0 Install Notes:

1.	Install Grails:

	1.	Obtain Grails 1.1.1 from:  http://www.grails.org/Download.  Tar/GZ or
		Zip depending on target platform.

	2.	Extract to desired location (archive contains grails-1.1.1 relative
		path internally)

	3.	Set GRAILS_HOME environment variable (i.e. <path to grails installation
		directory>/grails-1.1.1)

	4.	Add GRAILS_HOME/bin to PATH 

	5.	Test install by typing “grails help” to make sure all is ok.  Should
		see a list of commands

2.	Install Groovy (I think this is optional, but helpful for debugging or quick
	tests):

	1.	Obtain Groovy 1.6.3 from:  http://groovy.codehaus.org/Download  Tar/GZ
		or Zip depending on target platform.

	2.	Extract to desired location (archive contains groovy-1.6.3 relative
		path internally)

	3.	Set GROOVY_HOME environment variable (i.e. <path to grails installation
		directory>/groovy-1.6.3)
	
	4.	Add GROOVY_HOME/bin to PATH 

	5.	Test install by typing “groovy --version” to make sure all is ok. 
		Should see version info. 

3.	Setup OMAR:

	1.	Obtain OMAR-2.0 source distribution from the following SVN:  

		svn.osgeo.org/ossim/omar

	2.	Copy joms.jar from oms/lib tree into omar-2.0/lib (should be there
		already but just to make sure it matches the OMS on the system)

	3.	Unzip the MapServer support files contained in
		omar-2.0/mapserver/bmng.zip to a directory.  The preferred directory is
 		/data (or c:/data under windows).  If you choose a different place, you
		 will have to change the corresponding config variable as described
 		below.

	4.	The following files MAY need to be “tweaked” to match the local install
		(need a better way to do this):

		1.	omar-2.0/grails-app/conf/DataSource.groovy

			1.	If the username/password for PostgreSQL is different,
				change it to the correct values

			2.	If the database instance resides on a different machine,
				change the jdbc url (for each environment) to the correct
				values using one of the following formats:  

    				jdbc:postgresql:database
    				jdbc:postgresql://host/database
    				jdbc:postgresql://host:port/database

		2.	omar-2.0/grails-app/conf/Config.groovy

			1.	If the MapServer CGI instance resides on different host,
				change the wms.serverAddress variable to match the location
 				of MapServer CGI

			2.	If the MapServer support data was not unzipped to the /data
 				directory (or if you wish to use a different mapFile),
	 			change the wms.mapFile variable to match the location of
				the MapServer map file.


	5.	Setup the OMAR 2.0 database – from the omar-2.0 directory, do the
		following commands. If there is a port conflict with the default
		(8080), you can override with -Dserver.port=<port number> argument.

		1.	Install each of the plugins contained in the plugin directory.
			For example, to install the postgis plugin, execute the
			following:

                        grails install-plugin plugins/grails-postgis-plugin.zip

		2.	Type:  “grails prod create-postgis-database”  This will create
			the PostgreSQL database and load the PostGIS extensions into it. 
			If all goes well, you should see a whole bunch of inserts on the
			console.

		3.	Type: “grails prod run-app” to start the app which will create
			the application database tables for OMAR 2.0.  If all goes well,
			there will be a “Server running. Browse to 
	
			http://localhost:8080/omar-2.0” on the console.    

			Once this is complete, you can Ctrl-C to exit.

		4.	Type: “grails prod add-geometry-column”

			1.	When prompted for table:  type “raster_entry” (w/o quotes
				off course, this is true for the rest as well) and hit
				return.

			2.	When prompted for column:  type “ground_geom” and hit
				return

			3.	When prompted for srid:  type “4326”  and hit return

			4.	When prompted for type name: type “POLYGON”  (yes,
				uppercase) and hit return.

			5.	When prompted for number of dimensions:  type”2” and hit
				return.

		4.	Repeat step 3 for the “video_data_set” table.  All other values
			are the same.

	6.	Run OMAR-2.0:

		1.	To run using standalone Jetty server, again type:  
			“grails prod run-app”  or

		2.	To create a war file deployable to Tomcat, type:  
			“grails prod war”  If all goes well, you should have see
			omar-2.0-0.1.war in the omar-2.0 directory.  I think the context
			path will be /omar-2.0-0.1 when deployed to Tomcat.
	
	7.	Stage some data:

		1.	Log in as admin/admin (or whatever you may have changed the those
			values to)

		2.	Find the Settings link called “Respository” (look under “Edit
 			Tables”) and click it

		3.	Press the “Create a new repository button”.
  
		4.	Add a path and submit

		5.	Press the run stager button to kick off the ingest.

		6.	It actually writes out logs/repository-xxx.txt log files for the
			staging process.  Not sure where they'll end up when running as
			Tomcat, might need to fix that.  (Doh!)

		7.	The start date will show up and if all goes well (meaning stager
 			doesn't crash), you'll eventually get an end date (runs as a
 			background thread so page doesn't refresh)

	8.	Search for data:

		1.	As admin or user, you can search for either imagery or video
			(combined search is coming).

		2.	Search page brings up OpenLayers map viewer.  Zoom/Pan as normal

		3.	When read to set AOI, make sure to switch mode w/radio button.

		4.	You can also specify optional date range (MM/DD/YYYY format for
			now, need something better).  Should work with either start, end,
			both, or neither.  Also can specify date only w/o AOI.  Metadata
			search coming soon.

		5.	Clicking the search button should show the results page.
			Hopefully thumbnails on the right. Clicking a thumbnail should
			popup a larger view.  These are currently set to 128 and 512 I 
			think.  Look in the Thumbnail controller to change 'em.
  
		6.	Some columns are sortable (the others are derived columns and
			aren't yet.  Soon...)

		7.	Query results are 10 at a time and paginated.  This is adjustable
			as well.  Just look in the appropriate controller to change 'em.
  			Look for params.max.

7.	CAVEATS:

	1.	This install assumes that OSSIM, OMS, MapServer, PostgreSQL/PostGIS and
		all their dependencies are installed and  configured correctly.

	2.	The OMS/OSSIM stuff needs to be on LD_LIBRARY_PATH (or PATH for
		Windows)

	3.	The map file might need to be “tweaked” for base layer.  The map file
		used during development is included as a reference in the mapserver
		directory.

	4.	If additional layers are to be added to the map.  The changes can be
		made to the rasterEntry or videoDataSet contoller and their respective
		search.gsp files.  (Good Luck)

	5.	Probably tons more that I haven't even thought of yet.... 
