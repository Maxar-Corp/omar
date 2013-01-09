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
#header, .ui-layout-north { z-index: 3 !important; } 
#header, .ui-layout-north { overflow: visible !important; } 
</style>
</head>
<body>
<div class="outer-center" id="rasterSearchPageId">

    <div class="ui-layout-north"><omar:securityClassificationBanner/></div>

    <div class="middle-center">

        <div class="ui-layout-north">

            <div style="position:relative">
                <p><g:render plugin="omar-common-ui" template="/templates/federatedSearchMenu"/></p>
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

                <h3>Temporal</h3>
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
            <g:render plugin="omar-federation" template="/templates/searchTabView"/>
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
            <div id="omar-server-count-container">
                <div id="omar-server-enabled"><input id="omar-server-enabled-checkbox" type="checkbox" checked=${'<%=enabled%>'}></input></div>
                <div id="omar-server-count" class="omar-server-count">${'<%=count%>'}</div>
            </div>
            <img style="padding-top:24px" src="${resource(dir:'images', file:'server.gif')}"/>
        </div>
        <a href="${'<%=url%>'}" id="omar-server-url">${'<%=name%>'}</a>
    </div>
</script>

<script type="text/javascript">
    function init(){
        var wmsConfig = ${wmsBaseLayers}
            // application specific initialize that will need access to grails models
            //
                OpenLayers.ImgPath = "${resource(plugin:'openlayers', dir:'js/img')}/";
        var params = {
            map:{theme:"${resource(plugin:'openlayers', dir:'js/theme/default', file:'style.css')}",
                baseLayers:wmsConfig.base.layers
            }
        };
        var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery, params);
        searchPageController.render();

        $( "#tabView" ).tabs({active:1});

    }
</script>

</body>
</html>