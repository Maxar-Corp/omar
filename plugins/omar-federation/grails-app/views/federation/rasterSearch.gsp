<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<r:require modules = "federationRasterSearch"/>
	<title>Nested Layouts</title>
	<r:layoutResources/>

	<style type="text/css">
	#ui-datepicker-div, .ui-datepicker{ font-size: 75%; }
	
	h1 {font-size:50%}
	h2 {font-size:50%}
	p {font-size:50%} 

	/*
	 *	NOTE: All CSS is purely cosmetic - it does not affect functionality
	 */

	/* customize borders to avoid double-borders around inner-layouts */
	.ui-layout-pane {
		border:			0; /* override layout-default-latest.css */
		border-top:		1px solid #BBB;
		border-bottom:	1px solid #BBB;
	}
	.ui-layout-pane-north ,
	.ui-layout-pane-south {
		border:			1px solid #BBB;
		overflow:		hidden;
	}
	.ui-layout-pane-west ,
	.ui-layout-pane-east {
	}
	.ui-layout-pane-center	{
		border-left:	0;
		border-right:	0;
		}
		.inner-center {
			border:		1px solid #BBB;
		}

	/* add shading to outer sidebar-panes */
	.outer-west ,
	.outer-east {
		background-color: #EEE;
	}
	.middle-west ,
	.middle-east {
		background-color: #F8F8F8;
	}

	/* remove padding & scrolling from panes that are 'containers' for nested layouts */
	.outer-center ,
	.middle-center {
		border:			0; /* cosmetic */
		padding:		0;
		overflow:		hidden;
	}

	/*
	 *	customize borders on panes/resizers to make pretty
	 */
	.ui-layout-pane-west		{ border-right:		0; }
	.ui-layout-resizer-west		{ border-left:		1px solid #BBB; }
	.ui-layout-pane-east		{ border-left:		0; }
	.ui-layout-resizer-east		{ border-right:		1px solid #BBB; }
	.ui-layout-pane-north		{ border-bottom:	0; }
	.ui-layout-resizer-north	{ border-top:		1px solid #BBB; }
	.ui-layout-pane-south		{ border-top:		0; }
	.ui-layout-resizer-south	{ border-bottom: 	1px solid #BBB; }
	/*
	 *	add borders to resizers when pane is 'closed'
	 *
	 *.ui-layout-resizer-closed	{ border:			1px solid #BBB; }
	 */
	/*
	 *	show both borders when the resizer is 'dragging'
	 */
	.ui-layout-resizer-west-dragging ,
	.ui-layout-resizer-east-dragging {
		border-left:		1px solid #BBB;
		border-right:		1px solid #BBB;
	}
	.ui-layout-resizer-north-dragging ,
	.ui-layout-resizer-south-dragging {
		border-top:		1px solid #BBB;
		border-bottom:	1px solid #BBB;
	}

	</style>

	<script type="text/javascript" src="http://layout.jquery-dev.net/lib/js/jquery-latest.js"></script>
	<script type="text/javascript" src="http://layout.jquery-dev.net/lib/js/jquery-ui-latest.js"></script>
	
	<script type="text/javascript">

	$(document).ready(function () {

		// OUTER-LAYOUT
		$('body').layout({
			center__paneSelector:	".outer-center"
		,	west__paneSelector:		".outer-west"
		,	east__paneSelector:		".outer-east"
		,	west__size:				125
		,	east__size:				125
		,	spacing_open:			8  // ALL panes
		,	spacing_closed:			12 // ALL panes
		//,	north__spacing_open:	0
		//,	south__spacing_open:	0
		,	north__maxSize:			50
		,   north__minSize:         50
		,	south__maxSize:			50
		,   south__minSize:         50

			// MIDDLE-LAYOUT (child of outer-center-pane)
		,	center__childOptions: {
				center__paneSelector:	".middle-center"
			,	west__paneSelector:		".middle-west"
			,	east__paneSelector:		".middle-east"
			,	west__size:				100
			,	east__size:				100
			,	spacing_open:			8  // ALL panes
			,	spacing_closed:			12 // ALL panes

				// INNER-LAYOUT (child of middle-center-pane)
			,	center__childOptions: {
					center__paneSelector:	".inner-center"
				,	west__paneSelector:		".inner-west"
				,	east__paneSelector:		".inner-east"
				,	west__size:				225
				,   west__minSize:          225
				,	east__size:				75
				,	spacing_open:			8  // ALL panes
				,	spacing_closed:			8  // ALL panes
				,	west__spacing_closed:	12
				,	east__spacing_closed:	12
				}
			}
		});

		var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery);
		searchPageController.render();

		$('#startDateTime').datetimepicker({
			dateFormat: "yy-mm-dd",
			timeFormat: "HH:mm:ss:lz",
			separator: 'T',
			showSecond: true,
			showMillisec: true
		});

		$('#endDateTime').datetimepicker({
			dateFormat: "yy-mm-dd",
			timeFormat: "HH:mm:ss:lz",
			separator: 'T',
			showSecond: true,
			showMillisec: true
		});
	});

	</script>

</head>
<body>

<div class="outer-center">

	<div class="middle-center">

		<div class="inner-center">Map</div>
		<div class="inner-west">
			<g:render plugin="omar-common-ui" template="/templates/boundBoxTemplate"/>
			<g:render plugin="omar-common-ui" template="/templates/dateTimeTemplate"/>

			<button name="SearchRasterId" id="SearchRasterId">Search</button>
		</div>
		<!--<div class="inner-east">Inner East</div>-->
		<div class="ui-layout-north">Menu</div>
		<div class="ui-layout-south">OMAR Server List</div>

	</div>
	<!--<div class="middle-west">Middle West</div>-->
	<!--<div class="middle-east">Middle East</div>-->

</div>

<!--<div class="outer-west">Outer West</div>-->
<!--<div class="outer-east">Outer East</div>-->

<div class="ui-layout-north"><omar:securityClassificationBanner/></div>
<div class="ui-layout-south"><omar:securityClassificationBanner/></div>

<r:layoutResources/>

</body>
</html>