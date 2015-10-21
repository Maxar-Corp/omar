<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <asset:stylesheet src="federationSearch.css"/>
    <title>OMAR <g:meta name="app.version"/>: Federated Search</title>
    <style type="text/css">
    .body {
        text-align: left;
    }
    </style>
    <piwik:trackPageview/>
    <opensearch:descriptors/>
    <!--<link rel="search" type="application/opensearchdescription+xml" href="/omar/openSearch/settings" title="OMAR Search" /> -->
</head>

<body class="body">
<div class="outer-center" id="rasterSearchPageId">

    <div class="ui-layout-north">
        <div style="display: inline;">
            <omar:securityClassificationBanner/>
            <div style="float: left;">
                <g:render plugin="omar-common-ui" template="/templates/federatedSearchMenu"/>
            </div>

            <div style="margin-left: 45%; margin-right: auto;">
                <g:render template="/templates/searchTabView"/>
            </div>
        </div>
    </div>

    <div class="middle-center">

        <div class="inner-west">

            <div id='displayUnitId'>
                <p>Units <g:select styles="z-index:-1" name="displayUnit" from="${['DD', 'DMS', 'MGRS']}"/></p>
            </div>

            <g:render plugin="omar-common-ui" template="/templates/wfsTypeNameTemplate"/>
            <p><g:checkBox name="spatialSearchFlag" value="${true}"/> Use Spatial</p>

            <form>
                <p><input type="radio" id="bboxRadioButton" name="spatialSearchType" value="bbox"
                          checked="checked">Use Bound Box</p>

                <p><g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/></p>

                <p><input type="radio" id="pointRadioButton" name="spatialSearchType" value="point">Use Point Radius
                </p>
            </form>

            <p><g:render plugin="omar-common-ui" template="/templates/pointRadiusTemplate"/></p>

            <p><g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/></p>

            <center>
                <table><tr>
                    <td><button name="Search" id="SearchId">Search OMAR™</button></td>
                    <td><button name="ClearSelectedRows" id="ClearSelectedRowsId">Clear Selected Rows</button></td>
                </tr></table>
            </center>

        </div>

        <div class="inner-center">
            <div id="CustomQueryView">
                <br/>
                <g:render plugin="omar-common-ui" template="/templates/cqlTemplate"/>
            </div>

            <div id="GeneralQueryView">
                <g:render plugin="omar-common-ui" template="/templates/generalQueryTemplate"/>
                <br/>
            </div>

            <div id="MapView">
                <g:render plugin="omar-common-ui" template="/templates/mapTemplate"/>
            </div>

            <div id="ResultsView">
                <br/>
                <table id="DataTable" cellspacing="0px" width="100%">
                </table>
            </div>
        </div>

        <div class="inner-east">
            <g:render plugin="omar-common-ui" template="/templates/measurementTemplate"/>

            <p>

            <div class="niceBox">
                <div class="niceBoxHeader">Map Layers:</div>

                <div class="niceBoxBody">
                    <div id="layerSwitcher" class="layerSwitcher"></div>
                </div>
            </div></p>

            <g:render plugin="omar-common-ui" template="/templates/footprintLegendTemplate"
                      model="${[style: footprintStyle]}"/>
        </div>

        <div class="ui-layout-south">
            <div id="omarServerCollectionId" class="omar-server-collection"></div>
        </div>
    </div>


    <div class="ui-layout-south">
        <omar:securityClassificationBanner/>
    </div>
</div>

<script type="text/html" id="omar-server-template">
<div class="omar-server" id="{{id}}">
    <table>
        <tr><td>
            <div id="omar-server-enabled" class="omar-server-enabled">
                <%--
                <input id="omar-server-enabled-checkbox"
                      type="checkbox" class="omar-server-enabled-checkbox1"
                      checked='{{enabled}}'>
               </input>
                --%>
                <label id="omar-server-count" class="omar-server-count">{{count}}
                </label>
            </div>
        </td>
        </tr>
        <tr>
            <td>
                <asset:image class="omar-server-image" id="omar-server-image-{{id}}"
                             src='server.gif'/>
            </td>
        </tr>
        <tr><td><label class="omar-server-link-cursor" id="omar-server-name-{{id}}">{{name}}</label>
        </td>
        </tr>
    </table>

</div>
</script>
<asset:javascript src="federationSearch.js"/>
<g:javascript>
    function init()
    {
        var userRoles = ${raw( roles?.toString() )};
        var wmsConfig = ${raw( wmsBaseLayers?.toString() )};
        var styles = ${raw( styles.toString() )};
        //var maxInputs=
        // application specific initialize that will need access to grails models
        //
        OpenLayers.ImgPath = "${raw( resource( plugin: 'openlayers', dir: 'js/img' ) )}/";
        var params = {
            map: {theme: "${raw( asset.assetPath( src: 'theme/default/style.css' ) )}",
                baseLayers: wmsConfig.base.layers
            },
            cql: {resourceImages: {
                remove: "${raw( asset.assetPath( src: 'remove.gif' ) )}",
                add: "${raw( asset.assetPath( src: 'add.gif' ) )}"
            }
            },
            userRoles: userRoles,
            legend: {
                styles: styles
            },
            maxMosaicSize:${raw( maxInputs?.toString() )},
            jobQueueEnabled:${raw( jobQueueEnabled?.toString() )}
    };
    var searchPageController = new OMAR.pages.FederatedRasterSearch( jQuery, params );
    searchPageController.render();
}
</g:javascript>
</body>
</html>