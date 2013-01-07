<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<r:require modules = "federationRasterSearch"/>
    <title>OMAR <g:meta name="app.version"/>: Federated Search</title>
    <r:layoutResources/>

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
    </style>

</head>
<body>
<div class="outer-center" id="rasterSearchPageId">
    <div class="ui-layout-north"><omar:securityClassificationBanner/></div>

    <div class="middle-center">

        <div class="ui-layout-north">Menu</div>
        <div class="inner-west">

            <font size=1>
            







            <div id="accordion">
                <h3>Spatial</h3>
                <div>
                    <p><g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/></p>

                    <p><g:render plugin="omar-common-ui" template="/templates/pointRadiusTemplate"/></p>
                </div>

                <h3>Temporal</h3>
                <div><p><g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/></p></div>

                <h3>Metadata</h3>
                <div>
                    <p><g:render plugin="omar-common-ui" template="/templates/cqlTemplate"/></p>
                </div>
            </div>

             <center><button name="SearchRasterId" id="SearchRasterId">Search</button></center>













        </font>

            
        

        




               


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