<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <r:require modules = "product"/>
    <title>OMAR <g:meta name="app.version"/>: Federation Admin Page</title>
    <r:layoutResources/>
</head>
<body class="easyui-layout" fit="true">
<div region="north" class="banner" style="overflow:hidden;">
    <omar:securityClassificationBanner/>
</div>

<div region="south" class="banner" style="overflow:hidden;">
    <omar:securityClassificationBanner/>
</div>


<div region="center" class="outer-center" id="ProductPageId">
    <div class="easyui-layout" fit="true">

        <div region="north" style="overflow:hidden;">
            <div class="easyui-panel" style="overflow:hidden;padding:5px;">
                <g:link class="easyui-linkbutton" plain="true" uri="/">Home</g:link>
            </div>
        </div>
        <div region="center" style="overflow:hidden;">
            <div class="easyui-layout" fit="true">
                <div region="north" style="overflow:hidden;">
                    <form id="productFormId" method="POST">
                        <table>
                            <tr>
                                <td>
                                    <label>ImageId's:</label>
                                </td>
                                <td>
                                    ${params.layers}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label>Combiner Type:</label>
                                </td>
                                <td>
                                    <g:select id="combinerTypeId" name="combiner_type" from="${['ossimImageMosaic']}" />
                                </td>
                                <td>
                                    <label>Output File Name:</label>
                                </td>
                                <td>
                                    <g:textField id="outputFileId" name="output_file" value="image" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label>Output Projection:</label>
                                </td>
                                <td>
                                    <g:select id="srsId" name="srs" from="${['Geographic','Google Mercator', 'Auto UTM']}" keys="${['EPSG:4326','EPSG:3857', 'AUTO:42001']}" />
                                </td>
                                <td>
                                    <label>Gsd (Meters):</label>
                                </td>
                                <td>
                                    <g:textField id="gsdId" name="meters" value="0" class="easyui-numberbox" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label>Resampler Filter:</label>
                                </td>
                                <td>
                                    <g:select id="resamplerFilterId" name="resampler_filter" from="${['nearest neighbor','bilinear', 'cubic', 'lanczos', 'catrom', 'quadratic']}" />
                                </td>
                            </tr>


                            <tr>
                                <td>
                                    <label>Output Type:</label>
                                </td>
                                <td>
                                    <g:select id="writerId" name="writer" from="${['Geo Package','Tiled Tiff']}" keys="${['ossim_gpkg','tiff_tiled']}" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label>Grid Alignment:</label>
                                </td>
                                <td>
                                    <g:select id="gridAlignmentId" name="gridAlignment" from="${['ALIGN_TO_PROJECTION_GRID','ALIGN_TO_IMAGE']}" />
                                </td>
                            </tr>
                        </table>

                    </form>
                    <g:actionSubmit id="submitButtonId" value="Submit" />
                </div>

                <div region="center" style="overflow:hidden;">
                    <table id="jobTableId" class="easyui-datagrid"
                           rownumbers="true" toolbar="#toolbarId"  pagination="true" fit="true" fitColumns="true"
                           striped="true" url="${createLink( controller:'job', action: 'getData' )}"></table>

                </div>
            </div>
        </div>
        <div id="toolbarId">
            <a id="downloadId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-save" plain="true">Download Job</a>
            <a id="removeId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true">Remove Job</a>
            <a id="reloadId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-reload" plain="true">Reload</a>
        </div>
    </div>
</div>
<r:layoutResources/>

<script type="text/javascript">
    function init(){
        var gsdRangeArray = "${params.gsdRange}".split(",");
        var gsdMin = null;
        var gsdMax = null;
        var meters = "${params.meters}";
        if((meters != null)&&(meters!=""))
        {
            meters = parseFloat(meters);
        }
        else
        {
            meters = 1.0;
        }
        if((gsdRangeArray!=null) && (gsdRangeArray.length==2))
        {
            gsdMin = parseFloat(gsdRangeArray[0]);
            gsdMax = parseFloat(gsdRangeArray[1]);
        }
        else
        {
            gsdMin = meters;
            gsdMax = meters;
        }
        var productParams = {model:new OMAR.models.Product({
            layers:"${params.layers}",
            cut_wms_bbox_ll:"${params.cut_wms_bbox_ll}",
            meters:meters,
            gsdMin:gsdMin,
            gsdMax:gsdMax

        })
        };

        var tModel = ${tableModel as grails.converters.JSON};
        var jobParams = {model:new OMAR.models.Job(),
            tableModel:tModel,
            url: "${createLink( controller: 'Job', action: 'getData' )}",
            urls:{"remove":"${createLink( controller: 'job', action: 'remove' )}",
                "download":"${createLink( controller: 'job', action: 'download' )}"
            },
            baseUrl:"${createLink(controller: 'Job', action:'')}",
            singleSelect:false

        };

        var jobPage = OMAR.pages.JobPage(jQuery, jobParams);
        var productPage = OMAR.pages.ProductPage(jQuery, productParams);


        productPage.render();
        jobPage.render();



        $("body").css("visibility","visible");
    }
</script>

</body>
</html>
