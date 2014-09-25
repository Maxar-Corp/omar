<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <r:require modules = "product"/>
    <title>OMAR <g:meta name="app.version"/>: Federation Admin Page</title>
    <r:layoutResources/>
</head>
<body>
<div class="outer-center" id="ProductPageId">
    <omar:securityClassificationBanner/>
   



<form id="productFormId" method="POST"></form>

<b>Image Id's:</b> ${params.layers}

<p>

<b>Combiner Type:</b> <g:select id="combinerTypeId" name="combinerTypeId" from="${['ossimImageMosaic']}" />

<p>

<b>Output File Name:</b> <g:textField id="outputFileNameId" name="outputFileNameId" value="image" />

<p>

<b>Output Projection:</b> <g:select id="outputProjectionId" name="outputProjectionId" from="${['Geographic','Google Mercator','Scaled Mercator']}" />

<p>

<b>Output Type:</b>
<g:select id="outputTypeId" name="outputTypeId" from="${['ossim_gpkg','tiff_tiled']}" />

<p>

<b>Grid Alignment:</b> <input type="radio" id="gridAlignmentId" name="gridAlignmentId" value="ALIGN_TO_PROJECTION_GRID" checked="checked" title="foo"/>Align to Projection Grid <input type="radio" id="gridAlignmentId" name="gridAlignmentId" value="2" />Align to Image

<p>

<g:actionSubmit id="submitButtonId" value="Submit" />




    <omar:securityClassificationBanner/>
</div>
<r:layoutResources/>

<script type="text/javascript">
    function init(){
        var params = {model:new OMAR.models.Product({
          layers:"${params.layers}",
          bbox:"${params.bbox}"
        }

        )};
        var productPage = OMAR.pages.ProductPage(jQuery, params);
        productPage.render();
    }
</script>

</body>
</html>
