var dom = YAHOO.util.Dom;
var map;

$(document).ready(
	function () 
	{ 
		Event = YAHOO.util.Event;
		Event.onDOMReady( 
			function ()
			{
				var layout = new YAHOO.widget.Layout(
				{
					units:
					[
						{ position:'top', height:25, body:'top1' },
						{ position:'bottom', height:25, body:'bottom1' },
						{ position:'center', body:'center1', scroll:true }
					]
				});
				layout.on( 'render', function ()
				{
					dom.setStyle( document.body, 'visibility', 'visible' );
				});
				layout.render();
			}
		);
		mapSetup();
		initializeButtons();
	}
);

function mapSetup()
{
	var mapHeight = 0.8 * $("#pageContainer").height();
	$("#map").css("height", mapHeight);

	var mapWidth = 0.8 * $("#pageContainer").width();
	$("#map").css("width", mapWidth);

	map = new OpenLayers.Map("map");
}

function initializeButtons()
{
	$("#fastForwardButton").button(
	{
		icons: {primary: "ui-icon-seek-next"},
		text: false
	});
	$("#playForwardButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-e"},
		text: false
	});
	$("#playReverseButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-w"},
		text: false
	});
	$("#rewindButton").button(
	{
		icons: {primary: "ui-icon-seek-prev"},
		text: false
	});
	$("#stopButton").button(
	{
		icons: {primary: "ui-icon-stop"},
		text: false
	});
}
