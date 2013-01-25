<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <r:require modules = "federationRasterSearch"/>
    <title>OMAR <g:meta name="app.version"/>: Federated Search</title>
    <r:layoutResources/>
</head>
<body>
    <div class="outer-center" id="rasterSearchPageId">

        <div class="ui-layout-north">
            <omar:securityClassificationBanner/>
            <g:render plugin="omar-federation" template="/templates/searchTabView"/>
            <g:render plugin="omar-common-ui" template="/templates/federatedSearchMenu"/>
        </div>

        <div class="middle-center">

            <div class="inner-west">
                <p>Units<g:select styles="z-index:-1" name="displayUnit" from="${['DD', 'DMS', 'MGRS']}"/></p>
                <div id=SearchTypeNameId></div>
                <p><g:checkBox name="spatialSearch" value="" checked="true"/> Use Spatial</p>

                <form>
                    <p><input type="radio" id="bbox" name="spatialSearchType" value="bbox" checked="checked">Use Bound Box</p>
                    <p><g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/></p>

                    <p><input type="radio" id="point" name="spatialSearchType" value="point">Use Point Radius</p>
                </form>
                <p><g:render plugin="omar-common-ui" template="/templates/pointRadiusTemplate"/></p>

                <p><g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/></p>

                <center><button name="SearchRasterId" id="SearchRasterId">Search OMAR™</button></center>
            </div>

            <div class="inner-center">

                <div id="CustomQueryView">Custom Query</div>
                <div id="MapView">
                    <g:render plugin="omar-common-ui" template="/templates/mapTemplate"/>
                </div>
                <div id="ResultsView" >
                    <table id="DataTable" cellspacing="0px" width="100%">
                    </table>
                </div>
            </div>

            <div class="inner-east">
                <g:render plugin="omar-federation" template="/templates/measurementTemplate"/>

                <p><div class="niceBox">
                    <div class="niceBoxHeader">Layer Switcher:</div>
                    <div class="niceBoxBody">
                        <div id="layerSwitcher" class="layerSwitcher"></div>
                    </div>
                </div></p>
            </div>

            <div class="ui-layout-south">
                <div id="omarServerCollectionId"></div>
            </div>
        </div>


        <div class="ui-layout-south">
            <omar:securityClassificationBanner/></div>
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
            <img class="omar-server-image" id="omar-server-image" style="padding-top:24px" src="${resource(dir:'images', file:'server.gif')}"/>
        </div>
        <label  class="link_cursor" id="omar-server-name-${'<%=id%>'}">${'<%=name%>'}</label>
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

    }
</script>

</body>
</html>