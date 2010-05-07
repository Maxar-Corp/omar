<%--
  Created by IntelliJ IDEA.
  User: davelucas
  Date: Sep 9, 2009
  Time: 1:50:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>About OMAR</title>
  <meta name='layout' content='main4'/>
</head>
<body>

<div align=center>
  <img id="omarLogo" src="${resource(dir: 'images', file: 'OMAR_AboutHalf.png')}" alt="OMAR-2.0 Logo"/><br/>
</div>

<div align=center>
  <h3>
    OMAR<br/>
    Version <g:meta name="app.version"/>
  </h3>
</div>

<div align=center>
  <p>OSSIM Mapping and ARchive System (OMAR) is developed and supported by RadiantBlue Technologies.  OMAR integrates
  OSSIM, OpenLayers, MapServer, Postgres, PostGIS, GDAL, PROJ4, GeoTrans, and GRAILs to provide web based discovery
  and processing of geo-spatial and video assets.</p>

  <p>OMAR is supported by the Large Data JCTD, RROC-DEV, and the Army ZoP project.</p>
</div>

<div align=center>
  <img id="rbtLogo" src="${resource(dir: 'images', file: 'RBT.png')}" alt="OMAR-2.0 Logo"/><br/>
  <a href="http://www.radiantblue.com">Radiant Blue Technologies</a>
</div>

</body>
</html>