var dom = YAHOO.util.Dom;
var headerHeight;
var headerSecurityClassificationTextHeight;
var headerTitleTextHeight;
var logoHeight;
var logoOffset;
var logoWidth;

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
		generateLoadingDialogPopup();
	}
);

function addAnArrow(arrowType)
{
	$("#pageContainer").append('<div id = "arrowDiv" style = "border: .2em dotted #900"><img id = "arrowImage" src = ' + $("#logoImagesDirectory").get(0).innerHTML + 'annotationArrow.png height = "500"/></div>');
	//$("#pageContainer").append('<div id = "arrow">Cheese</div>');
	$("#arrowImage").resizable();
	$("#arrowDiv").draggable();
	$("#arrowDiv").position({
		my: "left bottom",
		at: "left bottom",
		of: $("#previewImage")
	});
	
}

function changeColorGradient()
{
	$("#changeColorGradientPopup").dialog
	({
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				generateHeader();
				generateFooter();		
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeFooterAcquisitionDateText()
{
	$("#changeFooterAcquisitionDateTextPopup").dialog(
	{
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
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeFooterLocationText()
{
	$("#changeFooterLocationTextPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerLocationTextInput").get(0).value;
				$("#footerLocationTextContainer").html(text);
				var color = $("#footerLocationTextColor").get(0).value;
				$("#footerLocationTextContainer").css("color", "#" + color);
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeFooterSecurityClassificationText()
{
	$("#changeFooterSecurityClassificationTextPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerSecurityClassificationTextInput").get(0).value;
				$("#footerSecurityClassificationTextContainer").get(0).innerHTML = text;
				var color = $("#footerSecurityClassificationTextColor").get(0).value;
				$("#footerSecurityClassificationTextContainer").css("color", "#" + color);
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeHeaderDescriptionText()
{
	$("#changeHeaderDescriptionTextPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerDescriptionTextInput").get(0).value;
				$("#headerDescriptionTextContainer").get(0).innerHTML = text;
				var color = $("#headerDescriptionTextColor").get(0).value;
				$("#headerDescriptionTextContainer").css("color", "#" + color);
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeHeaderSecurityClassificationText()
{
	$("#changeHeaderSecurityClassificationTextPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerSecurityClassificationTextInput").get(0).value;
				$("#headerSecurityClassificationTextContainer").get(0).innerHTML = text;
				var color = $("#headerSecurityClassificationTextColor").get(0).value;
				$("#headerSecurityClassificationTextContainer").css("color", "#" + color);				
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeHeaderTitleText()
{
	$("#changeHeaderTitleTextPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerTitleTextInput").get(0).value;
				$("#headerTitleTextContainer").get(0).innerHTML = text;
				var color = $("#headerTitleTextColor").get(0).value;
				$("#headerTitleTextContainer").css("color", "#" + color);
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeLogo()
{
	$( "#changeLogoPopup" ).dialog(
	{
		buttons: 
		{
			"OK": function() 
			{
				$(this).dialog("close");
				var logo = $("#logo").get(0).value;
				$("#logoImage").get(0).src = $("#logoImagesDirectory").get(0).innerHTML + logo + "ForWeb.png";	
			},
			Cancel: function() 
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeNorthArrow()
{
	$("#changeNorthArrowPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				generateNorthArrow();
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});
}

function changeOverviewMap()
{
	$("#changeOverviewMapPopup").dialog(
	{
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var overviewMapCountry = $("#overviewMapCountry").val();
				$("#overviewMapImage").get(0).src = $("#overviewMapImagesDirectory").get(0).innerHTML + overviewMapCountry + ".gif";
				if ( $("#includeOverviewMapCheckbox").get(0).checked )
				{
					$("#overviewMapImage").fadeTo("fast", 1);
				}
				else
				{
					$("#overviewMapImage").fadeTo("fast", 0.5);
				}
			},
			Cancel: function()
			{
				$(this).dialog("close");
			}
		},
		modal: true,
		width: "auto"
	});	
}

function displayTemplateButtons()
{
	$("#downloadButton").add("#upArrowButton").fadeTo("fast", 1);
	//$("#upArrowButton").fadeTo("fast", 1);
}

function downloadImage()
{
	var exportUrlParams = "?country=" + $("#overviewMapCountry").val();
	exportUrlParams += "&footerAcquisitionDateText=" + $("#footerAcquisitionDateTextInput").get(0).value;
	exportUrlParams += "&footerAcquisitionDateTextColor=" + $("#footerAcquisitionDateTextColor").get(0).value;
	exportUrlParams += "&footerLocationText=" + $("#footerLocationTextInput").get(0).value;
	exportUrlParams += "&footerLocationTextColor=" + $("#footerLocationTextColor").get(0).value;
	exportUrlParams += "&footerSecurityClassificationText=" + $("#footerSecurityClassificationTextInput").get(0).value;
	exportUrlParams += "&footerSecurityClassificationTextColor=" + $("#footerSecurityClassificationTextColor").get(0).value;
	exportUrlParams += "&gradientColorBottom=" + $("#gradientColorBottom").get(0).value;
	exportUrlParams += "&gradientColorTop=" + $("#gradientColorTop").get(0).value;
	exportUrlParams += "&headerDescriptionText=" + $("#headerDescriptionTextInput").get(0).value;
	exportUrlParams += "&headerDescriptionTextColor=" + $("#headerDescriptionTextColor").get(0).value;
	exportUrlParams += "&headerSecurityClassificationText=" + $("#headerSecurityClassificationTextInput").get(0).value;
        exportUrlParams += "&headerSecurityClassificationTextColor=" + $("#headerSecurityClassificationTextColor").get(0).value;
	exportUrlParams += "&headerTitleText=" + $("#headerTitleTextInput").get(0).value;
	exportUrlParams += "&headerTitleTextColor=" + $("#headerTitleTextColor").get(0).value;
	exportUrlParams += "&imageUrl=" + $("#previewImage").get(0).src.replace(/&/g,"%26");
	exportUrlParams += "&imageHeight=" + $("#previewImage").height();
	exportUrlParams += "&imageWidth=" + $("#previewImage").width();
	exportUrlParams += "&includeOverviewMap=" + $("#includeOverviewMapCheckbox").get(0).checked; 
	exportUrlParams += "&logo=" + $("#logo").get(0).value;
	exportUrlParams += "&northArrowAngle=" + $("#northAngleInput").get(0).value;
	exportUrlParams += "&northArrowBackgroundColor=" + $("#northArrowBackgroundColor").get(0).value;
	exportUrlParams += "&northArrowColor=" + $("#northArrowColor").get(0).value;
	exportUrlParams += "&northArrowSize=" + $("#northArrowImage").height();
	
	$("#downloadForm").get(0).action = $("#formActionUrl").get(0).innerHTML + exportUrlParams;
	$("#downloadForm").get(0).submit();

	$("#downloadDialogPopup").dialog(
	{
                buttons:
                {
                        "OK": function()
                        {
				$(this).dialog("close"); 
			}
		},
		modal: true,
		width: "auto"
	});   
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
        var gradientGeneratorUrl = $("#footerGradientGeneratorUrl").get(0).innerHTML;
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

	var gradientColorBottom = $("#gradientColorBottom").get(0).value;
	var gradientColorTop = $("#gradientColorTop").get(0).value;
	$("#footer").css("backgroundImage", "url('" + footerGradientUrlGenerator(gradientColorTop, gradientColorBottom, footerHeight) + "')");	

	$("#footer").position({
		my: "left top",
		at: "left bottom",
		of: $("#previewImage")
	});
}

function generateFooterAcquisitionDateText()
{
	var footerAcquisitionDateTextHeight = 0.035 * $("#previewImage").height();
	var footerAcquisitionDateTextWidth = $("#previewImage").width() / 3;

	$("#footerAcquisitionDateTextContainer").css("height", footerAcquisitionDateTextHeight);
	$("#footerAcquisitionDateTextContainer").css("width", footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextContainer").css("color", "#" + $("#footerAcquisitionDateTextColor").get(0).value);
	$("#footerAcquisitionDateTextContainer").get(0).innerHTML = $("#footerAcquisitionDateTextInput").get(0).value;
	var textSize = fontSize($("#footerAcquisitionDateTextInput").get(0).value, footerAcquisitionDateTextHeight, footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextContainer").get(0).style.fontSize = textSize;
	
	var footerAcquisitionDateTextOffsetHeight = 0;
	var footerAcquisitionDateTextOffsetWidth = 0;;
	$("#footerAcquisitionDateTextContainer").position({
		of: $("#footer"),
		my: "right top",
		at: "right top",
		offset: footerAcquisitionDateTextOffsetWidth + " " + footerAcquisitionDateTextOffsetHeight
	});
}

function generateFooterLocationText()
{
	var footerLocationTextHeight = 0.035 * $("#previewImage").height();
	var footerLocationTextWidth = $("#previewImage").width() / 2;

	$("#footerLocationTextContainer").css("height", footerLocationTextHeight);
	$("#footerLocationTextContainer").css("width", footerLocationTextWidth);
	$("#footerLocationTextContainer").css("color", "#" + $("#footerLocationTextColor").get(0).value);
	$("#footerLocationTextContainer").get(0).innerHTML = $("#footerLocationTextInput").get(0).value;
	var textSize = fontSize($("#footerLocationTextInput").get(0).value, footerLocationTextHeight, footerLocationTextWidth);
	$("#footerLocationTextContainer").css("font-size", textSize);
	$("#footerLocationTextContainer").css("textAlign", "center");

	var footerLocationTextOffsetHeight = 0;
	var footerLocationTextOffsetWidth = 0;
	$("#footerLocationTextContainer").position({
		of: $("#footer"),
		my: "center top",
		at: "center top",
		offset: footerLocationTextOffsetWidth + " " + footerLocationTextOffsetHeight
	});
}

function generateFooterSecurityClassificationText()
{
	var footerSecurityClassificationTextHeight = 0.035 * $("#previewImage").height();
	var footerSecurityClassificationTextWidth = $("#previewImage").width() / 3;

	$("#footerSecurityClassificationTextContainer").css("height", footerSecurityClassificationTextHeight);
	$("#footerSecurityClassificationTextContainer").css("width", footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextContainer").css("color", "#" + $("#footerSecurityClassificationTextColor").get(0).value);
        $("#footerSecurityClassificationTextContainer").get(0).innerHTML = $("#footerSecurityClassificationTextInput").get(0).value;
	var textSize = fontSize($("#footerSecurityClassificationTextInput").get(0).value, footerSecurityClassificationTextHeight, footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextContainer").css("font-size", textSize);

	var footerSecurityClassificationTextOffsetHeight = 0;
	var footerSecurityClassificationTextOffsetWidth = logoOffset;
	$("#footerSecurityClassificationTextContainer").position({
		of: $("#footer"),
		my: "left top",
		at: "left top",
		offset: footerSecurityClassificationTextOffsetWidth + " " + footerSecurityClassificationTextOffsetHeight
	});
}

function generateHeader()
{
	headerHeight = 0.1 * $("#previewImage").height();
	var headerWidth = $("#previewImage").width();

	$("#header").css("height", headerHeight);
	$("#header").css("width", headerWidth);

	//var windowWidth = $(window).width();
	//var headerOffsetWidth= (windowWidth - headerWidth) / 8;	

	$("#header").position({
		my: "center top",
		at: "center top",
		of: $("#pageContainer")
	});

	$("#previewImage").position({
		my: "left top",
		at: "left bottom",
		of: $("#header")
	});

	var gradientColorBottom = $("#gradientColorBottom").get(0).value;
	var gradientColorTop = $("#gradientColorTop").get(0).value;
	$("#header").css("backgroundImage", "url('" + headerGradientUrlGenerator(gradientColorTop, gradientColorBottom, headerHeight) + "')");
}

function generateHeaderDescriptionText()
{
	var headerDescriptionTextHeight = 0.32 * logoHeight;
	var headerDescriptionTextWidth = $("#previewImage").width();

	$("#headerDescriptionTextContainer").css("height", headerDescriptionTextHeight);
	$("#headerDescriptionTextContainer").css("width", headerDescriptionTextWidth);
	$("#headerDescriptionTextContainer").css("color", "#" + $("#headerDescriptionTextColor").get(0).value);
	$("#headerDescriptionTextContainer").get(0).innerHTML = $("#headerDescriptionTextInput").get(0).value;
	var textSize = fontSize($("#headerDescriptionTextInput").get(0).value, headerDescriptionTextHeight, headerDescriptionTextWidth);
	$("#headerDescriptionTextContainer").get(0).style.fontSize = textSize;

	var headerDescriptionTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight + headerTitleTextHeight;
	var headerDescriptionTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerDescriptionTextContainer").position({
		of: $("#header"),
		my: "left top",
		at: "left top",
		offset: headerDescriptionTextOffsetWidth + " " + headerDescriptionTextOffsetHeight
	});
}

function generateHeaderSecurityClassificationText()
{
	headerSecurityClassificationTextHeight = 0.25 * logoHeight;
	var headerSecurityClassificationTextWidth = $("#previewImage").width();
	
	$("#headerSecurityClassificationTextContainer").css("height", headerSecurityClassificationTextHeight);
	$("#headerSecurityClassificationTextContainer").css("width", headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextContainer").css("color", "#" + $("#headerSecurityClassificationTextColor").get(0).value);
	
	$("#headerSecurityClassificationTextContainer").get(0).innerHTML = $("#headerSecurityClassificationTextInput").get(0).value;
	var textSize = fontSize($("#headerSecurityClassificationTextInput").get(0).value, headerSecurityClassificationTextHeight, headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextContainer").get(0).style.fontSize = textSize;

	var headerSecurityClassificationTextOffsetHeight = logoOffset;
	var headerSecurityClassificationTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerSecurityClassificationTextContainer").position({
		of: $("#header"),
		my: "left top",
		at: "left top",
		offset: headerSecurityClassificationTextOffsetWidth + " " + headerSecurityClassificationTextOffsetHeight
	});
}

function generateHeaderTitleText()
{
	headerTitleTextHeight = 0.43 * logoHeight;
	var headerTitleTextWidth = $("#previewImage").width();
	
	$("#headerTitleTextContainer").css("height", headerTitleTextHeight);
        $("#headerTitleTextContainer").css("width", headerTitleTextWidth); 
	$("#headerTitleTextContainer").css("color", "#" + $("#headerTitleTextColor").get(0).value);
	$("#headerTitleTextContainer").get(0).innerHTML = $("#headerTitleTextInput").get(0).value;
	var textSize = fontSize($("#headerTitleTextInput").get(0).value, headerTitleTextHeight, headerTitleTextWidth);
	$("#headerTitleTextContainer").get(0).style.fontSize = textSize;

	var headerTitleTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight;
	var headerTitleTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerTitleTextContainer").position({
		my: "left top",
		at: "left top",
		of: $("#header"),
		offset: headerTitleTextOffsetWidth + " " + headerTitleTextOffsetHeight
	});
}

function generateLogo()
{
	logoHeight = 0.8 * $("#header").height();
	logoWidth = logoHeight;
	$("#logoImage").css("height", logoHeight);
	$("#logoImage").css("width", logoWidth);

	logoOffset = ($("#header").height() - logoHeight) / 2;

	$("#logoImage").position
	({
		my: "left center",
		at: "left center",
		of: $("#header"),
		offset: logoOffset + " 0"
	});

	$("#logoImage").get(0).src = $("#logoImagesDirectory").get(0).innerHTML + "ciaLogoForWeb.png";
}

function generateLoadingDialogPopup()
{
	var spinnerOptions = 
	{
		lines: 13, // The number of lines to draw
		length: 7, // The length of each line
		width: 4, // The line thickness
		radius: 10, // The radius of the inner circle
		corners: 1, // Corner roundness (0..1)
		rotate: 0, // The rotation offset
		color: '#000', // #rgb or #rrggbb
		speed: 1, // Rounds per second
		trail: 60, // Afterglow percentage
		shadow: false, // Whether to render a shadow
		hwaccel: false, // Whether to use hardware acceleration
		className: 'spinner', // The CSS class to assign to the spinner
		zIndex: 2e9, // The z-index (defaults to 2000000000)
		top: 'auto', // Top position relative to parent in px
		left: 'auto' // Left position relative to parent in px
	};
	var target = $("#spinner").get(0);
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

	var northArrowColor = $("#northArrowColor").get(0).value;
	var northArrowBackgroundColor = $("#northArrowBackgroundColor").get(0).value;

	var northArrowUrl = $("#northArrowGeneratorUrl").get(0).innerHTML;
	northArrowUrl += "?northArrowSize=" + northArrowHeight;
	northArrowUrl += "&northAngle=" + $("#northAngleInput").get(0).value;
	northArrowUrl += "&northArrowColor=" + northArrowColor;
	northArrowUrl += "&northArrowBackgroundColor=" + northArrowBackgroundColor;
	$("#northArrowImage").get(0).src =  northArrowUrl;
}

function generateOverviewMap()
{
	var overviewMapCountry = "yy";
        var countryCode = $("#countryCode").get(0).innerHTML.toLowerCase();
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

	$("#overviewMapImage").get(0).src = $("#overviewMapImagesDirectory").get(0).innerHTML + overviewMapCountry + ".gif";
}

function generateTemplateButtons()
{
	$("#downloadButton").button
	({
		icons: { primary: "ui-icon-arrowthickstop-1-s" },	
		text: false
		}).click(
			function() 
			{
				downloadImage();
			}
	);

	$("#downloadButton").position
	({
		my: "left top",
		at: "left top",
		of: $("#previewImage"),
		offset: "5 5"
	});

	$("#upArrowButton").button
	({
		icons: { primary: "ui-icon-arrowthick-1-n" },
		text: false
		}).click(
			function()
			{
				addAnArrow("up");
			}
	);

	$("#upArrowButton").position
	({
		my: "left top",
		at: "left bottom",
		of: $("#downloadButton"),
		offset: "0 5"
	});
}

function headerGradientUrlGenerator(topColor, bottomColor, height)
{
        var gradientGeneratorUrl = $("#headerGradientGeneratorUrl").html();
        gradientGeneratorUrl += "?gradientColorTop=" + topColor;
        gradientGeneratorUrl += "&gradientColorBottom=" + bottomColor;
        gradientGeneratorUrl += "&gradientHeight=" + height;
        return gradientGeneratorUrl;
}

function hideTemplateButtons()
{
	$("#downloadButton").add("#upArrowButton").fadeTo("fast", 0);
	//$("#upArrowButton").fadeTo("fast", 0);
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
	generateTemplateButtons();
	setupPreviewImageEvents();
	$("#loadingDialogPopup").dialog("close");
	$("#pageContainer").css("visibility", "visible");
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
		offset: "-" + northArrowOffsetWidth + " 0"
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
		offset: "-" + overviewMapImageOffsetWidth + " " + overviewMapImageOffsetHeight
	});	
}

function setupPreviewImageEvents()
{
	$("#previewImage").mousemove
	(
		function () { displayTemplateButtons(); }
	);

	$("#previewImage").mousestop
	(
		//function () { hideTemplateButtons(); }
	);
}
