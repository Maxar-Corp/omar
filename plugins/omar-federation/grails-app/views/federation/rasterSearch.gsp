<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<r:require modules = "federationRasterSearch"/>
    <title>OMAR <g:meta name="app.version"/>: Federated Search</title>
    <r:layoutResources/>
    <!--

<link rel="stylesheet" href="http://openlayers.org/dev/theme/default/style.css" type="text/css">
        <link rel="stylesheet" href="http://openlayers.org/dev/examples/style.css" type="text/css">
        <style type="text/css">
            html, body, #map {
                margin: 0;
                width: 100%;
                height: 95%;
            }

            #text {
                position: absolute;
                bottom: 1em;
                left: 1em;
                width: 512px;
                z-index: 20000;
                background-color: white;
                padding: 0 0.5em 0.5em 0.5em;
            }
        </style>
            <script src="http://openlayers.org/dev/OpenLayers.js"></script>
    -->
</head>
<body>
<div class="outer-center" id="rasterSearchPageId">
    <div class="ui-layout-north"><omar:securityClassificationBanner/></div>

    <div class="middle-center">

        <div class="ui-layout-north">Menu</div>
        <div class="inner-west">

            <!--<div id="accordion">-->
           
<table width="100%"><tr><td bgcolor="dfdfdf">
                <h2>Bounding Box Search:</h2>
                <g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/>
</td></tr></table>           

<table width="100%"><tr><td bgcolor="dfdfdf">
                <h2>Point Radius Search:</h2>
                <g:render plugin="omar-common-ui" template="/templates/pointRadiusTemplate"/>
</td></tr></table>  


<table width="100%"><tr><td bgcolor="dfdfdf">
                <h2>Temporal Search:</h2>
                <g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/>
                </td></tr></table>  
            <!--</div> -->


                <button name="SearchRasterId" id="SearchRasterId">Search</button>


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
<script type="text/html" id="template-contact">
    <div class="omar-server-container">
        <div class="infoi">
            <div class="omar-server-count">${'<%=count%>'}</div>
            <!--<img src="http://icons.iconarchive.com/icons/visualpharm/hardware/256/server-icon.png" height="70" width="70"/> -->
            <img style="padding-top:24px" src="${resource(dir:'images', file:'server.gif')}"/>
        </div>
        <a href="">${'<%=serverName%>'}</a>
    </div>
</script>

<script type="text/javascript">

function init(){
    // application specific initialize that will need access to grails models
    //
    OpenLayers.ImgPath = "${resource(plugin:'openlayers', dir:'js/img')}/";
    var params = {
        map:{theme:"${resource(plugin:'openlayers', dir:'js/theme/default')}/"}
    };
    var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery, params);
    searchPageController.render();

/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////

    map.events.register("mousemove", map, setMouse);
    map.events.register("moveend", map, setExtent);
    map.events.register("moveend", map, setCenter);
}
 /*
function setMouse(evt) {
    var mouse = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));
    var ddMouse = document.getElementById("ddMouse");

    if (mouse.lat < "90" && mouse.lat > "-90" && mouse.lon < "180" && mouse.lon > "-180") {
        ddMouse.innerHTML = "<b>DD:</b> " + mouse.lat + ", " + mouse.lon;
    }
    else {
        ddMouse.innerHTML = "<b>DD:</b> Outside of geographic extent.";
    }
}

function setExtent() {
    var extent = map.getExtent();
   
    // lower left
    //alert(extent.bottom + "," + extent.left);

    // upper right
    //alert(extent.top + "," + extent.right);
    //alert("test");
}

function setCenter() {
    var center = map.getCenter();

    //alert(center.lat + "," + center.lon);
    //alert("test");
}




     */











</script>

</body>
</html>