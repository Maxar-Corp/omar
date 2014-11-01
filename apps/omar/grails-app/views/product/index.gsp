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
                                    <g:select id="writerId" name="writer" from="${['Geo Package','Tiff']}" keys="${['gpkg','tiff']}" />
                                    <button type="button" id="writerPropertiesButtonId">Properties</button>
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


    <div id="geopackagePropertiesDlgId" class="easyui-dialog" closed="true" style="width:400px;height:280px;padding:10px 20px"
         closed="true" buttons="#gpkgWriterPropertiesDldButtonsId">
        <table>
            <tr>
                <td>
                    <label>Writer Mode:</label>
                </td>
                <td>
                    <g:select name="writer_mode" id="writerModeIdId" value="none" from="${['jpeg','png', 'pnga']}" keys="${['jpeg','png', 'pnga']}"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Align To Grid:</label>
                </td>
                <td>
                    <input type="checkbox" name="align_to_grid"  id="gpkgAlignToGridId"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Create Histogram:</label>
                </td>
                <td>
                    <input type="checkbox" name="create_histogram"  id="gpkgCreateHistogramId"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Scale To Eight Bit:</label>
                </td>
                <td>
                    <input type="checkbox" name="scale_to_eight_bit"  id="gpkgScaleToEightBitId"/>
                </td>
            </tr>
        </table>
    </div>
    <div id="tiffPropertiesDlgId" class="easyui-dialog" closed="true" style="width:400px;height:280px;padding:10px 20px"
         closed="true" buttons="#tiffWriterPropertiesDldButtonsId">
        <table>
            <tr>
                <td>
                    <label>Image Type:</label>
                </td>
                <td>
                    <g:select name="image_type" id="tiffTypeId" value="tiff_tiled" from="${['Tiled','Tiled Band Separate','Strip', 'Strip Band Separate']}" keys="${['tiff_tiled','tiff_tiled_band_separate','tiff_strip','tiff_strip_band_separate']}"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Compression:</label>
                </td>
                <td>
                     <g:select name="compression_type" id="tiffCompressionTypeId" value="none" from="${['None','JPEG', 'Packbits', 'Deflate', 'Zip']}" keys="${['none','jpeg','packbits','deflate','zip']}"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Compression Quality:</label>
                </td>
                <td>
                    <input type="text" name="compression_quality"  id="tiffCompressQualityId" class="easyui-textbox"/>
                    <label>%</label>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Create Overview:</label>
                </td>
                <td>
                    <input type="checkbox" name="create_overview"  id="tiffCreateOverviewId"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Create Histogram:</label>
                </td>
                <td>
                    <input type="checkbox" name="create_histogram"  id="tiffCreateHistogramId"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Scale To Eight Bit:</label>
                </td>
                <td>
                    <input type="checkbox" name="scale_to_eight_bit"  id="tiffScaleToEightBitId"/>
                </td>
            </tr>
        </table>
    </div>

    <div id="tiffWriterPropertiesDldButtonsId">
        <a id="tiffSavePropertiesButtonId" href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" style="width:90px">Save</a>
        <a id="tiffCancelPropertiesButtonId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" style="width:90px">Cancel</a>
    </div>
    <div id="gpkgWriterPropertiesDldButtonsId">
        <a id="gpkgSavePropertiesButtonId" href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" style="width:90px">Save</a>
        <a id="gpkgCancelPropertiesButtonId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" style="width:90px">Cancel</a>
    </div>
    -->

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
