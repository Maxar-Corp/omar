<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<r:require modules = "federationRasterSearch"/>
    <title>OMAR <g:meta name="app.version"/>: Federated Search</title>
    <r:layoutResources/>
<style type="text/css">
ul {
    font-family: Arial, Verdana;
    font-size: 14px;
    margin: 0;
    padding: 0;
    list-style: none;
}
ul li {
    display: block;
    position: relative;
    float: left;
}
li ul {
    display: none;
}
ul li a {
    display: block;
    text-decoration: none;
    color: #ffffff;
    border-top: 1px solid #ffffff;
    padding: 5px 15px 5px 15px;
    background: #1e7c9a;
    margin-left: 1px;
    white-space: nowrap;
}
ul li a:hover {
background: #3b3b3b;
}
li:hover ul {
    display: block;
    position: absolute;
}
li:hover li {
    float: none;
    font-size: 11px;
}
li:hover a { background: #3b3b3b; }
li:hover li a:hover {
    background: #1e7c9a;
}

</style>
    <style>
  .box
{
margin: 0 0 0px; 
padding: 0 0 0px; 

border: 1px solid #d8d8d8; 
background: #f9f9f9;
 
-moz-border-radius: 4px; 
-webkit-border-radius: 4px; 
border-radius: 4px; 
-moz-box-shadow: inset 0 0 0 1px #fff;
-webkit-box-shadow: inset 0 0 0 1px #fff;
box-shadow: inset 0 0 0 1px #fff; 
 }
 .ui-layout-pane {
  padding: 0px !important;
}
#navbar {
    margin: 0;
    padding: 0;
    height: 1em;  }
#navbar li {
    list-style: none;
    float: left; }
#navbar li a {
    display: block;
    padding: 3px 8px;
    background-color: #5e8ce9;
    color: #fff;
    text-decoration: none; }






#navbar li ul {
    display: none; 
    width: 10em; /* Width to help Opera out */
    background-color: #69f;}







    #navbar li:hover ul {
    display: block;
    position: absolute;
    margin: 0;
    padding: 0; }
#navbar li:hover li {
    float: none; }
#navbar li:hover li a {
    background-color: #69f;
    border-bottom: 1px solid #fff;
    color: #000;}
#navbar li li a:hover {
    background-color: #8db3ff; }




    </style>

</head>
<body>
<div class="outer-center" id="rasterSearchPageId">
    <div class="ui-layout-north"><omar:securityClassificationBanner/>


    </div>

    <div class="middle-center">

        <div class="ui-layout-north">

<div style="position:relative">
<ul id="navbar">
    <li><a href="#">OMAR™</a><ul>
        <li><a href="#">About</a></li>
        <li><a href="#">Home</a></li>
        <li><a href="#">Log Out</a></li></ul>
    </li>


 <li><a href="#">Export</a><ul>
        <li><a href="#">Kml Query</a></li></ul>
    </li>


    
 <li><a href="#">View</a><ul>
        <li><a href="#">Refresh Footprints</a></li>
        <li><a href="#">Search</a></li></ul>
    </li>
    
  
</ul>
 
</div>
        </div>
        








        <div class="inner-west">



<p>Display Unit: <g:select name="displayUnit" from="${['DD', 'DMS', 'MGRS']}"/></p>



<div id="accordion">
  <h3>Spatial</h3>
  <div>
   <p><g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/></p>
   <p><g:render plugin="omar-common-ui" template="/templates/pointRadiusTemplate"/></p>
  </div>
 
  <h3>Tempotral</h3>
  <div>
   <p><g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/></p>

  </div>
  <h3>Metadata</h3>
  <div>
   
  </div>
</div>














<center><button name="SearchRasterId" id="SearchRasterId">Search</button></center>













        




               


         </div>
        <div class="inner-center">
















            <g:render plugin="omar-common-ui" template="/templates/mapTemplate"/>
        </div>
		<div class="ui-layout-south">
            <div id="omarServerCollectionId">
            </div>
        </div>

	</div>
    <div class="ui-layout-south"><omar:securityClassificationBanner/></div>

</div>

 <script>
   //  OpenLayers.ImgPath = "${resource(plugin:'openlayers', dir:'js/img')}/";
     // alert("${resource(plugin:'openlayers', dir:'js/theme/default')}/");

 </script>
<r:layoutResources/>
<script type="text/html" id="omar-server-template">
    <div class="omar-server-container" id="${'<%=id%>'}">
        <div class="omar-server-info">
            <div id="omar-server-count" class="omar-server-count">${'<%=count%>'}</div>
            <img style="padding-top:24px" src="${resource(dir:'images', file:'server.gif')}"/>
        </div>
        <a href="${'<%=url%>'}" id="omar-server-url">${'<%=name%>'}</a>
    </div>
</script>

<script type="text/javascript">

function init(){
    // application specific initialize that will need access to grails models
    //
    OpenLayers.ImgPath = "${resource(plugin:'openlayers', dir:'js/img')}/";
    var params = {
        map:{theme:"${resource(plugin:'openlayers', dir:'js/theme/default', file:'style.css')}"}
    };
    var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery, params);
    searchPageController.render();

    $( "#accordion" ).accordion();

}
</script>

</body>
</html>