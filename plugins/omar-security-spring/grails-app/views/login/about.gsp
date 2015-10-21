<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR <g:meta name="app.version"/>: About</title>
</head>

<body>
<content tag="content">
  <div class="nav" role="navigation">
    <ul><li><g:link class="home" uri="/">OMAR™ Home</g:link><li></li></ul>
  </div>

  <div class="body">
    <h1>OMAR Version <g:meta name="app.version"/></h1>
    <br>

    <p>
       ossim library info: <label id="version"></label> <label id="build_date"></label><label id="revision"></label>
    </p>
    <br>
    <h2>
      OSSIM Mapping and ARchive System (OMAR) is developed and supported by RadiantBlue Technologies.  OMAR integrates
      OSSIM, OpenLayers, MapServer, Postgres, PostGIS, GDAL, PROJ4, GeoTrans, and GRAILs to provide web based discovery
      and processing of geo-spatial and video assets.  OMAR is supported by the Large Data JCTD, RROC-DEV, CSTARS, and the
      Army ZoP project.
    </h2>

    <div align="center">
      <p><asset:image src='RBT.png'
              alt="RadiantBlue Technologies"/></p>

      <p><a href="http://www.radiantblue.com">RadiantBlue Technologies</a></p>
    </div>
  </div>
</content>

<asset:javascript src = "jquery.js"/>
<asset:script>

$(document).ready(function (){

   $.get( "${createLink(controller:'versionInfo')}", 
      function( data ) { 
         $("#version").html(data.ossim.version);
         $("#build_date").html(data.ossim.build_date);
         $("#revision").html(data.ossim.revision);
      }
   );
});

</asset:script>
<asset:deferredScripts/>

</body>
</html>

