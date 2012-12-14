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
    <div class="ui-layout-north"><omar:securityClassificationBanner/></div>

    <div class="middle-center">

        <div class="ui-layout-north">Menu</div>
        <div class="inner-west">
            <div id="accordion-container">
                <h2 class="accordion-header">Bounding Box Search</h2>

                <div class="accordion-content">
                    <g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/>
                </div>

                <h2 class="accordion-header">Temporal Search</h2>
                <div class="accordion-content">
                    <g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/>
                </div>

                <button name="SearchRasterId" id="SearchRasterId">Search</button>

             </div>
         </div>
        <div class="inner-center">Map</div>
		<div class="ui-layout-south">OMAR Server List</div>

	</div>
    <div class="ui-layout-south"><omar:securityClassificationBanner/></div>

</div>

<r:layoutResources/>
<script type="text/javascript">
function init(){
    // application specific initialize that will need access to grails models
    //
    var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery);
    searchPageController.render();
}

</script>

</body>
</html>