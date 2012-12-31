var dom = YAHOO.util.Dom;
var headerHeight;
var headerSecurityClassificationTextHeight;
var headerTitleTextHeight;
var logoHeight;
var logoOffset;
var logoWidth;
var northArrowSpinner;

$(document).ready
(
	function () 
	{ 
		Event = YAHOO.util.Event;
		Event.onDOMReady( 
			function ()
			{
				var layout = new YAHOO.widget.Layout
				({
					units:
					[
						{ position:'top', height:25, body:'top1' },
						{ position:'bottom', height:25, body:'bottom1' },
						{ position:'center', body:'center1', scroll:true }
					]
				});
				layout.on('render', function () { dom.setStyle(document.body, 'visibility', 'visible'); });
				layout.render();
			}
		);

		generateLoadingDialogPopup();
		setupDialogs();
		var oMenu = new YAHOO.widget.MenuBar
		(
			"templateExportMenu",
			{ autosubmenudisplay: true, hidedelay: 750, showdelay: 0, lazyload: true, zIndex:9999 }
		);
		oMenu.render();
		$(window).resize(function() { windowResize(); });
	}
);

function changeColorGradient() { $("#changeColorGradientPopup").dialog("open"); }
function changeFooterAcquisitionDateText() { $("#changeFooterAcquisitionDateTextPopup").dialog("open"); }
function changeFooterLocationText() { $("#changeFooterLocationTextPopup").dialog("open"); }
function changeFooterSecurityClassificationText() { $("#changeFooterSecurityClassificationTextPopup").dialog("open"); }
function changeHeaderDescriptionText() { $("#changeHeaderDescriptionTextPopup").dialog("open"); }
function changeHeaderSecurityClassificationText() { $("#changeHeaderSecurityClassificationTextPopup").dialog("open"); }
function changeHeaderTitleText() { $("#changeHeaderTitleTextPopup").dialog("open"); }
function changeLogo() { $("#changeLogoPopup").dialog("open"); }
function changeNorthArrow() { $("#changeNorthArrowPopup").dialog("open"); }
function changeOverviewMap() { $("#changeOverviewMapPopup").dialog("open"); }

function downloadImage()
{
	var exportUrlParams = "?country=" + $("#overviewMapCountry").val();
	exportUrlParams += "&footerAcquisitionDateText=" + $("#footerAcquisitionDateTextInput").val();
	exportUrlParams += "&footerAcquisitionDateTextColor=" + $("#footerAcquisitionDateTextColor").val();
	exportUrlParams += "&footerLocationText=" + $("#footerLocationTextInput").val();
	exportUrlParams += "&footerLocationTextColor=" + $("#footerLocationTextColor").val();
	exportUrlParams += "&footerSecurityClassificationText=" + $("#footerSecurityClassificationTextInput").val();
	exportUrlParams += "&footerSecurityClassificationTextColor=" + $("#footerSecurityClassificationTextColor").val();
	exportUrlParams += "&gradientColorBottom=" + $("#gradientColorBottom").val();
	exportUrlParams += "&gradientColorTop=" + $("#gradientColorTop").val();
	exportUrlParams += "&headerDescriptionText=" + $("#headerDescriptionTextInput").val();
	exportUrlParams += "&headerDescriptionTextColor=" + $("#headerDescriptionTextColor").val();
	exportUrlParams += "&headerSecurityClassificationText=" + $("#headerSecurityClassificationTextInput").val();
        exportUrlParams += "&headerSecurityClassificationTextColor=" + $("#headerSecurityClassificationTextColor").val();
	exportUrlParams += "&headerTitleText=" + $("#headerTitleTextInput").val();
	exportUrlParams += "&headerTitleTextColor=" + $("#headerTitleTextColor").val();
	exportUrlParams += "&imageUrl=" + $("#previewImage").get(0).src.replace(/&/g,"%26");
	exportUrlParams += "&imageHeight=" + $("#previewImage").height();
	exportUrlParams += "&imageWidth=" + $("#previewImage").width();
	exportUrlParams += "&includeOverviewMap=" + $("#includeOverviewMapCheckbox").get(0).checked; 
	exportUrlParams += "&logo=" + $("#logo").val();
	exportUrlParams += "&northArrowAngle=" + $("#northAngleInput").val();
	exportUrlParams += "&northArrowBackgroundColor=" + $("#northArrowBackgroundColor").val();
	exportUrlParams += "&northArrowColor=" + $("#northArrowColor").val();
	exportUrlParams += "&northArrowSize=" + $("#northArrowImage").height();
	
	if (markers.length > 1)
	{
		exportUrlParams += "&markers=" + markers.join(",");
	}

	$("#downloadForm").get(0).action = formActionUrl + exportUrlParams;
	$("#downloadForm").get(0).submit();

	$("#downloadDialogPopup").dialog("open");   
}

function fontSize(text, desiredSizeHeight, desiredSizeWidth)
{
	var textSize;
	var resizer = $("#fontSize");
	resizer.html(text);
	resizer.css("font-size", headerHeight);

	while(resizer.height() > desiredSizeHeight) 
	{
  		textSize = parseInt(resizer.css("font-size"), 10);
  		resizer.css("font-size", textSize - 1);
	}

	return (textSize - 1); 
}

function footerGradientUrlGenerator(topColor, bottomColor, height)
{
        var gradientGeneratorUrl = footerGradientGeneratorUrl;
        gradientGeneratorUrl += "?gradientColorTop=" + topColor;
        gradientGeneratorUrl += "&gradientColorBottom=" + bottomColor;
        gradientGeneratorUrl += "&gradientHeight=" + height;
        return gradientGeneratorUrl;
}

function generateFooter()
{
	var footerHeight = 0.035 * $("#previewImage").height();
	var footerWidth = $("#previewImage").width();
	$("#footer").css("height", footerHeight);
	$("#footer").css("width", footerWidth);

	var gradientColorBottom = $("#gradientColorBottom").val();
	var gradientColorTop = $("#gradientColorTop").val();
	$("#footer").css("backgroundImage", "url('" + footerGradientUrlGenerator(gradientColorTop, gradientColorBottom, footerHeight) + "')");	
}

function generateFooterAcquisitionDateText()
{
	var footerAcquisitionDateTextHeight = 0.035 * $("#previewImage").height();
	var footerAcquisitionDateTextWidth = $("#previewImage").width() / 3;

	$("#footerAcquisitionDateTextContainer").css("height", footerAcquisitionDateTextHeight);
	$("#footerAcquisitionDateTextContainer").css("width", footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextContainer").css("color", "#" + $("#footerAcquisitionDateTextColor").get(0).value);
	$("#footerAcquisitionDateTextContainer").html($("#footerAcquisitionDateTextInput").val());
	var textSize = fontSize($("#footerAcquisitionDateTextInput").val(), footerAcquisitionDateTextHeight, footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextContainer").css("fontSize", textSize);
}

function generateFooterLocationText()
{
	var footerLocationTextHeight = 0.035 * $("#previewImage").height();
	var footerLocationTextWidth = $("#previewImage").width() / 2;

	$("#footerLocationTextContainer").css("height", footerLocationTextHeight);
	$("#footerLocationTextContainer").css("width", footerLocationTextWidth);
	$("#footerLocationTextContainer").css("color", "#" + $("#footerLocationTextColor").val());
	$("#footerLocationTextContainer").html($("#footerLocationTextInput").val());
	var textSize = fontSize($("#footerLocationTextInput").val(), footerLocationTextHeight, footerLocationTextWidth);
	$("#footerLocationTextContainer").css("font-size", textSize);
	$("#footerLocationTextContainer").css("textAlign", "center");
}

function generateFooterSecurityClassificationText()
{
	var footerSecurityClassificationTextHeight = 0.035 * $("#previewImage").height();
	var footerSecurityClassificationTextWidth = $("#previewImage").width() / 3;

	$("#footerSecurityClassificationTextContainer").css("height", footerSecurityClassificationTextHeight);
	$("#footerSecurityClassificationTextContainer").css("width", footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextContainer").css("color", "#" + $("#footerSecurityClassificationTextColor").get(0).value);
        $("#footerSecurityClassificationTextContainer").html($("#footerSecurityClassificationTextInput").val());
	var textSize = fontSize($("#footerSecurityClassificationTextInput").val(), footerSecurityClassificationTextHeight, footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextContainer").css("font-size", textSize);
}

function generateHeader()
{
	headerHeight = 0.1 * $("#previewImage").height();
	var headerWidth = $("#previewImage").width();

	$("#header").css("height", headerHeight);
	$("#header").css("width", headerWidth);

	var gradientColorBottom = $("#gradientColorBottom").val();
	var gradientColorTop = $("#gradientColorTop").val();
	$("#header").css("backgroundImage", "url('" + headerGradientUrlGenerator(gradientColorTop, gradientColorBottom, headerHeight) + "')");
}

function generateHeaderDescriptionText()
{
	var headerDescriptionTextHeight = 0.32 * logoHeight;
	var headerDescriptionTextWidth = $("#previewImage").width();

	$("#headerDescriptionTextContainer").css("height", headerDescriptionTextHeight);
	$("#headerDescriptionTextContainer").css("width", headerDescriptionTextWidth);
	$("#headerDescriptionTextContainer").css("color", "#" + $("#headerDescriptionTextColor").get(0).value);
	$("#headerDescriptionTextContainer").html($("#headerDescriptionTextInput").val());
	var textSize = fontSize($("#headerDescriptionTextInput").val(), headerDescriptionTextHeight, headerDescriptionTextWidth);
	$("#headerDescriptionTextContainer").css("fontSize", textSize);
}

function generateHeaderSecurityClassificationText()
{
	headerSecurityClassificationTextHeight = 0.25 * logoHeight;
	var headerSecurityClassificationTextWidth = $("#previewImage").width();
	
	$("#headerSecurityClassificationTextContainer").css("height", headerSecurityClassificationTextHeight);
	$("#headerSecurityClassificationTextContainer").css("width", headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextContainer").css("color", "#" + $("#headerSecurityClassificationTextColor").get(0).value);
	
	$("#headerSecurityClassificationTextContainer").html($("#headerSecurityClassificationTextInput").val());
	var textSize = fontSize($("#headerSecurityClassificationTextInput").val(), headerSecurityClassificationTextHeight, headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextContainer").css("fontSize", textSize);
}

function generateHeaderTitleText()
{
	headerTitleTextHeight = 0.43 * logoHeight;
	var headerTitleTextWidth = $("#previewImage").width();
	
	$("#headerTitleTextContainer").css("height", headerTitleTextHeight);
        $("#headerTitleTextContainer").css("width", headerTitleTextWidth); 
	$("#headerTitleTextContainer").css("color", "#" + $("#headerTitleTextColor").get(0).value);
	
	$("#headerTitleTextContainer").html($("#headerTitleTextInput").val());
	var textSize = fontSize($("#headerTitleTextInput").val(), headerTitleTextHeight, headerTitleTextWidth);
	$("#headerTitleTextContainer").css("fontSize", textSize);
}

function generateLogo()
{
	logoHeight = 0.8 * $("#header").height();
	logoWidth = logoHeight;
	$("#logoImage").css("height", logoHeight);
	$("#logoImage").css("width", logoWidth);

	$("#logoImage").attr("src", logoImagesDirectory + "ciaLogoForWeb.png");

	logoOffset = ($("#header").height() - logoHeight) / 2;
}

function generateLoadingDialogPopup()
{
	var spinnerOptions = 
	{
		lines: 13,
		length: 7,
		width: 4, 
		radius: 10, 
		corners: 1,
		rotate: 0,
		color: '#000',
		speed: 1,
		trail: 60, 
		shadow: false,
		hwaccel: false, 
		className: 'spinner', 
		zIndex: 2e9,
		top: 'auto',
		left: 'auto'
	};
	var target = document.getElementById("spinner");
	var spinner = new Spinner(spinnerOptions).spin(target);

	$("#loadingDialogPopup").dialog
	({
		modal: true,
		width: "auto"
	});
	$("#loadingDialogPopup").parent().find('a.ui-dialog-titlebar-close').remove();
}

function generateNorthArrow()
{
	var northArrowHeight = logoHeight;
	var northArrowWidth = logoWidth;
	$("#northArrowImage").css("height", northArrowHeight);
	$("#northArrowImage").css("width", northArrowWidth);

	var northArrowColor = $("#northArrowColor").val();
	var northArrowBackgroundColor = $("#northArrowBackgroundColor").val();

	var northArrowUrl = northArrowGeneratorUrl;
	northArrowUrl += "?northArrowSize=" + northArrowHeight;
	northArrowUrl += "&northAngle=" + $("#northAngleInput").val();
	northArrowUrl += "&northArrowColor=" + northArrowColor;
	northArrowUrl += "&northArrowBackgroundColor=" + northArrowBackgroundColor;
	$("#northArrowImage").attr("src", northArrowUrl);

	var target = document.getElementById("northArrowSpinnerDiv");

	if (northArrowSpinner) { northArrowSpinner.spin(target); }
	else
	{
		var options =
		{
			className: "spinner", color: "#ffffff", corners: 1, hwaccel: false, left: "auto", lines: 13,
			radius: 10, rotate: 0, shadow: false, speed: 1, top: "auto", trail: 60, width: 4, zIndex: 2e9
		};
                northArrowSpinner = new Spinner(options).spin(target);
        }
}

function generateOverviewMap()
{
	var overviewMapCountry = "yy";
	for (var i = 0; i < $("#overviewMapCountry").get(0).options.length; i++)
	{
		if (countryCode == $("#overviewMapCountry").get(0).options[i].value)
		{
			$("#overviewMapCountry").get(0).options.selectedIndex = i;
			overviewMapCountry = $("#overviewMapCountry").get(0).options[i].value;
			$("#includeOverviewMapCheckbox").get(0).checked = true;
		}
	}

	var overviewMapImageHeight = 0.2 * $("#previewImage").height();
	$("#overviewMapImage").css("height", overviewMapImageHeight);

	$("#overviewMapImage").attr("src", overviewMapImagesDirectory + overviewMapCountry + ".gif");
}

function headerGradientUrlGenerator(topColor, bottomColor, height)
{
        var gradientGeneratorUrl = headerGradientGeneratorUrl;
        gradientGeneratorUrl += "?gradientColorTop=" + topColor;
        gradientGeneratorUrl += "&gradientColorBottom=" + bottomColor;
        gradientGeneratorUrl += "&gradientHeight=" + height;
        return gradientGeneratorUrl;
}

function init()
{
	generateHeader();
	generateLogo();
	generateHeaderSecurityClassificationText();
	generateHeaderTitleText();
	generateHeaderDescriptionText();
	generateOverviewMap();
	generateNorthArrow();
	generateFooter();
	generateFooterSecurityClassificationText();
	generateFooterLocationText();
	generateFooterAcquisitionDateText();
	setupMarkers();
	windowResize();
	$("#loadingDialogPopup").dialog("close");
}

function positionNorthArrow()
{
	var northArrowOffsetHeight = logoOffset;
	var northArrowOffsetWidth = logoOffset;

	$("#northArrowImage").position
	({
		my: "right top",
		at: "left top",
		of: $("#overviewMapImage"),
		offset: "-" + logoOffset + " 0",
		collision: "none"
	});

	$("#northArrowSpinnerDiv").position
	({
		my: "middle center",
		at: "middle center",
		of: $("#northArrowImage"),
		offset: "0 0",
		collision: "none"
	});
}

function positionOverviewMapImage()
{
	var overviewMapImageOffsetHeight = logoOffset;
	var overviewMapImageOffsetWidth = 2 * logoOffset;
	$("#overviewMapImage").position
	({
		my: "right top",
		at: "right top",
		of: $("#header"),
		offset: "-" + overviewMapImageOffsetWidth + " " + overviewMapImageOffsetHeight,
		collision: "none"
	});	
}

function removeNorthArrowSpinner() { northArrowSpinner.stop(); }

function setupDialogs()
{
	$("#changeColorGradientPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				generateHeader();
				generateFooter();		
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeColorGradientPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeFooterAcquisitionDateTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerAcquisitionDateTextInput").get(0).value;
				$("#footerAcquisitionDateTextContainer").html(text);
				var color = $("#footerAcquisitionDateTextColor").get(0).value;
				$("#footerAcquisitionDateTextContainer").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterAcquisitionDateTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeFooterLocationTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerLocationTextInput").val();
				$("#footerLocationTextContainer").html(text);
				var color = $("#footerLocationTextColor").val();
				$("#footerLocationTextContainer").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterLocationTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeFooterSecurityClassificationTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerSecurityClassificationTextInput").val();
				$("#footerSecurityClassificationTextContainer").html(text);
				var color = $("#footerSecurityClassificationTextColor").val();
				$("#footerSecurityClassificationTextContainer").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterSecurityClassificationTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeHeaderDescriptionTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerDescriptionTextInput").val();
				$("#headerDescriptionTextContainer").html(text);
				var color = $("#headerDescriptionTextColor").val();
				$("#headerDescriptionTextContainer").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderDescriptionTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeHeaderSecurityClassificationTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerSecurityClassificationTextInput").val();
				$("#headerSecurityClassificationTextContainer").html(text);
				var color = $("#headerSecurityClassificationTextColor").val();
				$("#headerSecurityClassificationTextContainer").css("color", "#" + color);				
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderSecurityClassificationTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeHeaderTitleTextPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerTitleTextInput").val();
				$("#headerTitleTextContainer").html(text);
				var color = $("#headerTitleTextColor").val();
				$("#headerTitleTextContainer").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderTitleTextPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeLogoPopup").dialog
	({
        	autoOpen: false,
		buttons: 
		{
			"OK": function() 
			{
				$(this).dialog("close");
				var logo = $("#logo").val();
				$("#logoImage").attr("src", logoImagesDirectory + logo + "ForWeb.png");	
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeLogoPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeNorthArrowPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				generateNorthArrow();
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeNorthArrowPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#changeOverviewMapPopup").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var overviewMapCountry = $("#overviewMapCountry").val();
				$("#overviewMapImage").attr("src", overviewMapImagesDirectory + overviewMapCountry + ".gif");
				if ($("#includeOverviewMapCheckbox").get(0).checked) { $("#overviewMapImage").fadeTo("fast", 1); }
				else { $("#overviewMapImage").fadeTo("fast", 0.5); }
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});	
	$("#changeOverviewMapPopup").parent().find('a.ui-dialog-titlebar-close').remove();

	$("#downloadDialogPopup").dialog
	({
		autoOpen: false,
		buttons:
		{
			"OK": function() { $(this).dialog("close"); }
		},
		modal: true,
		width: "auto"
	});
	$("#downloadDialogPopup").parent().find('a.ui-dialog-titlebar-close').remove();
}

function setupMarkers()
{
	if (markers.length > 1)
	{
		var numberOfMarkers = parseInt(markers.length / 2);
		for (var i = 0; i < numberOfMarkers; i++)
		{
			$("#markerDiv").append("<img id = 'marker" + i + "' src = '" + markerIcon + "'/>");
			$("#" + "marker" + i).position
			({
				my: "left top",
				at: "left top",
				of: $("#previewImage"),
				offset: markers[2 * i] + " " + markers[2 * i + 1],
				collision: "none"
			});
		}
	}
}

function windowResize()
{
	var headerPositionHorizontal;
	if ($("#header").width() > $(window).width()) { headerPositionHorizontal = "left";}
	else { headerPositionHorizontal = "middle"; }
	$("#header").position
	({
		my: headerPositionHorizontal + " top",
		at: headerPositionHorizontal + " bottom",
		of: $("#templateExportMenu"),
		offset: "0 0",
		collision: "none"
	});

	$("#previewImage").position
	({
		my: "left top",
		at: "left bottom",
		of: $("#header"),
		offset: "0 0",
		collision: "none"
	});

	$("#logoImage").position
	({
		my: "left center",
		at: "left center",
		of: $("#header"),
		offset: logoOffset + " 0",
		collision: "none"
	});

	var headerSecurityClassificationTextOffsetHeight = logoOffset;
	var headerSecurityClassificationTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerSecurityClassificationTextContainer").position
	({
		my: "left top",
		at: "left top",	
		of: $("#header"),
		offset: headerSecurityClassificationTextOffsetWidth + " " + headerSecurityClassificationTextOffsetHeight,
		collision: "none"
	});

	var headerTitleTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight;
	var headerTitleTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerTitleTextContainer").position
	({
		my: "left top",
		at: "left top",
		of: $("#header"),
		offset: headerTitleTextOffsetWidth + " " + headerTitleTextOffsetHeight,
		collision: "none"
        });

	var headerDescriptionTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight + headerTitleTextHeight;
	var headerDescriptionTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerDescriptionTextContainer").position
	({
		of: $("#header"),
		my: "left top",
		at: "left top",
		offset: headerDescriptionTextOffsetWidth + " " + headerDescriptionTextOffsetHeight,
		collision: "none"
	});

	$("#northArrowImage").position
	({
		my: "right top",
		at: "left top",
		of: $("#overviewMapImage"),
		offset: "-" + logoOffset + " 0",
		collision: "none"
	});
	$("#northArrowSpinnerDiv").position
	({
		my: "middle center",
		at: "middle center",
		of: $("#northArrowImage"),
		offset: "0 0",
		collision: "none"
	});

	var overviewMapImageOffsetHeight = logoOffset;
	var overviewMapImageOffsetWidth = 2 * logoOffset;
	$("#overviewMapImage").position
	({
		my: "right top",
		at: "right top",
		of: $("#header"),
		offset: "-" + overviewMapImageOffsetWidth + " " + overviewMapImageOffsetHeight,
		collision: "none"
	});


	$("#footer").position
	({
		my: "left top",
		at: "left bottom",
		of: $("#previewImage"),
		offset: "0 0",
		collision: "none"
	});

	var footerSecurityClassificationTextOffsetHeight = 0;
	var footerSecurityClassificationTextOffsetWidth = logoOffset;
	$("#footerSecurityClassificationTextContainer").position
	({
		my: "left top",
		at: "left top",
		of: $("#footer"),
		offset: footerSecurityClassificationTextOffsetWidth + " " + footerSecurityClassificationTextOffsetHeight,
		collision: "none"
	});

	$("#footerLocationTextContainer").position
	({
		my: "center top",
		at: "center top",
		of: $("#footer"),
		offset: "0 0",
		collision: "none"
	});

	$("#footerAcquisitionDateTextContainer").position
	({
		my: "right top",
		at: "right top",
		of: $("#footer"),
		offset: "0 0",
		collision: "none"
	});
}
