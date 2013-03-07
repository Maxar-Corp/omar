var currentLayer = 0;
var dom = YAHOO.util.Dom;
var flipBookChipFileNameArray = [];
var flipBookChipReadinessArray = ["Not Ready"];
var headerHeight;
var headerSecurityClassificationTextHeight;
var headerTitleTextHeight;
var logoHeight;
var logoOffset;
var logoWidth;
var northArrowSpinner;
var previewImageHeight;
var previewImageWidth;

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

		$.ajax({ cache: false });
		setupDialogs();
		generateLoadingDialog();

		$("#previewImageDiv").append("<img id = 'previewImage0' src = '" + imageUrlArray[0].replace(/%26/g, "&") + "'/>");
		$("#previewImage0").load
		(
			function() 
			{ 
				previewImageHeight = $("#previewImage0").height();
				previewImageWidth = $("#previewImage0").width();
				$("#previewImageDiv").css("height", previewImageHeight);
				$("#previewImageDiv").css("width", previewImageWidth);	

				for (var i = 1; i < imageUrlArray.length; i++)
				{
					$("#previewImageDiv").append("<img id = 'previewImage" + i + "' src = '" + imageUrlArray[i].replace(/%26/g, "&") + "'/>");
					$("#previewImage" + i).hide();
					flipBookChipReadinessArray[i] = "Not Ready";
				}
				init(); 
			}
		);
		
		var oMenu = new YAHOO.widget.MenuBar
		(
			"templateExportMenu",
			{ autosubmenudisplay: true, hidedelay: 750, showdelay: 0, lazyload: true, zIndex:9999 }
		);
		oMenu.render();

		$("#headerSecurityClassificationTextInput").val(headerSecurityClassificationTextArray[0]);
		$("#headerTitleTextInput").val(headerTitleTextArray[0]);
		$("#headerDescriptionTextInput").val(headerDescriptionTextArray[0]);
		$("#northAngleInput").val(northAngleArray[0]);
		$("#footerSecurityClassificationTextInput").val(footerSecurityClassificationTextArray[0]);
		$("#footerLocationTextInput").val(footerLocationTextArray[0]);
		$("#footerAcquisitionDateTextInput").val(footerAcquisitionDateTextArray[0]);
				
		//$(window).resize(function() { windowResize(); });
		$("#previousImageButton").attr("disabled", true);
		$("#previousImageButton").css("opacity", 0.3);
		$("#nextImageButton").attr("disabled", true);
		$("#nextImageButton").css("opacity", 0.3);
	}
);

function changeColorGradient() { $("#changeColorGradientDialog").dialog("open"); $(".ui-dialog: input").blur(); }
function changeFooterAcquisitionDateText() { $("#changeFooterAcquisitionDateTextDialog").dialog("open"); }
function changeFooterLocationText() { $("#changeFooterLocationTextDialog").dialog("open"); }
function changeFooterSecurityClassificationText() { $("#changeFooterSecurityClassificationTextDialog").dialog("open"); }
function changeHeaderDescriptionText() { $("#changeHeaderDescriptionTextDialog").dialog("open"); }
function changeHeaderSecurityClassificationText() { $("#changeHeaderSecurityClassificationTextDialog").dialog("open"); }
function changeHeaderTitleText() { $("#changeHeaderTitleTextDialog").dialog("open"); }
function changeLogo() { $("#changeLogoDialog").dialog("open"); }
function changeNorthArrow() { $("#changeNorthArrowDialog").dialog("open"); }
function changeOverviewMap() { $("#changeOverviewMapDialog").dialog("open"); }

function download()
{
	if (imageUrlArray.length == 1)
	{
		$.ajax
		({
			async: true,
			data: getExportUrlParams(currentLayer),
			dataType: "text",
			success: function(data) 
			{ 
				$("#currentProductProgressDiv").html("100%");
				$("#productLinkDiv").html
				( 
					"<a " + 
						"href = '" + viewProductUrl + "?fileName=" + data + "' " + 
						"onclick = 'javascript:$(\"#productGenerationStatusDialog\").dialog(\"close\")' " +
						"style = 'color: blue'" +
						"target = '_blank'><u>Link</u>" +
					"</a>"
				);
			},
			type: "POST",
			url: exportImageFormUrl
		});
	}
	else if (imageUrlArray.length > 1)
	{
		for (var i = 0; i < flipBookChipReadinessArray.length; i++)
		{
			if (flipBookChipReadinessArray[i] != "Ready")
			{
				$.ajax
				({
					async: true,
					data: getExportUrlParams(i),
					dataType: "text",
					success: function(data) 
					{
						flipBookChipReadinessArray[i] = "Ready";
						flipBookChipFileNameArray[i] = data;
						isTheFlipBookFinished();
					},
					url: exportImageFormUrl
				});
				break;
			}
		}
	}
	$("#productGenerationProgressDialog").dialog("open");
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
	var footerHeight = 0.035 * previewImageHeight;
	var footerWidth = previewImageWidth;
	$("#footerDiv").css("height", footerHeight);
	$("#footerDiv").css("width", footerWidth);

	var gradientColorBottom = $("#gradientColorBottomInput").val();
	var gradientColorTop = $("#gradientColorTopInput").val();
	$("#footerDiv").css("backgroundImage", "url('" + footerGradientUrlGenerator(gradientColorTop, gradientColorBottom, footerHeight) + "')");	
}

function generateFooterAcquisitionDateText()
{
	var footerAcquisitionDateTextHeight = 0.035 * previewImageHeight;
	var footerAcquisitionDateTextWidth = previewImageWidth / 3;

	$("#footerAcquisitionDateTextDiv").css("height", footerAcquisitionDateTextHeight);
	$("#footerAcquisitionDateTextDiv").css("width", footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextDiv").css("color", "#" + $("#footerAcquisitionDateTextColorInput").val());
	$("#footerAcquisitionDateTextDiv").html($("#footerAcquisitionDateTextInput").val());
	var textSize = fontSize($("#footerAcquisitionDateTextInput").val(), footerAcquisitionDateTextHeight, footerAcquisitionDateTextWidth);
	$("#footerAcquisitionDateTextDiv").css("fontSize", textSize);
	$("#footerAcquisitionDateTextDiv").css("textAlign", "right");
}

function generateFooterLocationText()
{
	var footerLocationTextHeight = 0.035 * previewImageHeight;
	var footerLocationTextWidth = previewImageWidth / 2;

	$("#footerLocationTextDiv").css("height", footerLocationTextHeight);
	$("#footerLocationTextDiv").css("width", footerLocationTextWidth);
	$("#footerLocationTextDiv").css("color", "#" + $("#footerLocationTextColorInput").val());
	$("#footerLocationTextDiv").html($("#footerLocationTextInput").val());
	var textSize = fontSize($("#footerLocationTextInput").val(), footerLocationTextHeight, footerLocationTextWidth);
	$("#footerLocationTextDiv").css("font-size", textSize);
	$("#footerLocationTextDiv").css("textAlign", "center");
}

function generateFooterSecurityClassificationText()
{
	var footerSecurityClassificationTextHeight = 0.035 * previewImageHeight;
	var footerSecurityClassificationTextWidth = previewImageWidth / 3;

	$("#footerSecurityClassificationTextDiv").css("height", footerSecurityClassificationTextHeight);
	$("#footerSecurityClassificationTextDiv").css("width", footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextDiv").css("color", "#" + $("#footerSecurityClassificationTextColorInput").val());
        $("#footerSecurityClassificationTextDiv").html($("#footerSecurityClassificationTextInput").val());
	var textSize = fontSize($("#footerSecurityClassificationTextInput").val(), footerSecurityClassificationTextHeight, footerSecurityClassificationTextWidth);
	$("#footerSecurityClassificationTextDiv").css("font-size", textSize);
}

function generateHeader()
{
	headerHeight = 0.1 * previewImageHeight;
	var headerWidth = previewImageWidth;

	$("#headerDiv").css("height", headerHeight);
	$("#headerDiv").css("width", headerWidth);

	var gradientColorBottom = $("#gradientColorBottomInput").val();
	var gradientColorTop = $("#gradientColorTopInput").val();
	$("#headerDiv").css("backgroundImage", "url('" + headerGradientUrlGenerator(gradientColorTop, gradientColorBottom, headerHeight) + "')");
}

function generateHeaderDescriptionText()
{
	var headerDescriptionTextHeight = 0.32 * logoHeight;
	var headerDescriptionTextWidth = previewImageWidth;

	$("#headerDescriptionTextDiv").css("height", headerDescriptionTextHeight);
	$("#headerDescriptionTextDiv").css("width", headerDescriptionTextWidth);
	$("#headerDescriptionTextDiv").css("color", "#" + $("#headerDescriptionTextColorInput").val());
	$("#headerDescriptionTextDiv").html($("#headerDescriptionTextInput").val());
	var textSize = fontSize($("#headerDescriptionTextInput").val(), headerDescriptionTextHeight, headerDescriptionTextWidth);
	$("#headerDescriptionTextDiv").css("fontSize", textSize);
}

function generateHeaderSecurityClassificationText()
{
	headerSecurityClassificationTextHeight = 0.25 * logoHeight;
	var headerSecurityClassificationTextWidth = previewImageWidth;
	
	$("#headerSecurityClassificationTextDiv").css("height", headerSecurityClassificationTextHeight);
	$("#headerSecurityClassificationTextDiv").css("width", headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextDiv").css("color", "#" + $("#headerSecurityClassificationTextColorInput").val());
	
	$("#headerSecurityClassificationTextDiv").html($("#headerSecurityClassificationTextInput").val());
	var textSize = fontSize($("#headerSecurityClassificationTextInput").val(), headerSecurityClassificationTextHeight, headerSecurityClassificationTextWidth);
	$("#headerSecurityClassificationTextDiv").css("fontSize", textSize);
}

function generateHeaderTitleText()
{
	headerTitleTextHeight = 0.43 * logoHeight;
	var headerTitleTextWidth = previewImageWidth;
	
	$("#headerTitleTextDiv").css("height", headerTitleTextHeight);
        $("#headerTitleTextDiv").css("width", headerTitleTextWidth); 
	$("#headerTitleTextDiv").css("color", "#" + $("#headerTitleTextColorInput").val());
	
	$("#headerTitleTextDiv").html($("#headerTitleTextInput").val());
	var textSize = fontSize($("#headerTitleTextInput").val(), headerTitleTextHeight, headerTitleTextWidth);
	$("#headerTitleTextDiv").css("fontSize", textSize);
}

function generateLogo()
{
	logoHeight = 0.8 * $("#headerDiv").height();
	logoWidth = logoHeight;

	$("#logoImage").css("height", logoHeight);
	$("#logoImage").css("width", logoWidth);

	$("#logoImage").attr("src", logoImagesDirectory + "ngaLogoForWeb.png");

	logoOffset = ($("#headerDiv").height() - logoHeight) / 2;
}

function generateLoadingDialog()
{
	var spinnerOptions = 
	{
		lines: 13, length: 7, width: 4, radius: 10, corners: 1, rotate: 0,color: '#000', speed: 1, 
		trail: 60, shadow: false, hwaccel: false, className: 'spinner', zIndex: 2e9, top: 'auto', left: 'auto'
	};
	var target = document.getElementById("spinner");
	var spinner = new Spinner(spinnerOptions).spin(target);
}

function generateNorthArrow()
{
	var northArrowHeight = logoHeight;
	var northArrowWidth = logoWidth;
	$("#northArrowImage").css("height", northArrowHeight);
	$("#northArrowImage").css("width", northArrowWidth);

	var northArrowColor = $("#northArrowColorInput").val();
	var northArrowBackgroundColor = $("#northArrowBackgroundColorInput").val();

	var northArrowUrl = northArrowGeneratorUrl;
	northArrowUrl += "?northArrowSize=" + northArrowHeight;
	northArrowUrl += "&northAngle=" + $("#northAngleInput").val();;
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
	$("#overviewMapCountry option[value='yy']").attr("selected", true);
	for (var i = 0; i < $("#overviewMapCountry")[0].options.length; i++)
	{
		if (countryCode == $("#overviewMapCountry")[0].options[i].value)
		{
			$("#overviewMapCountry")[0].options.selectedIndex = i;
			overviewMapCountry = $("#overviewMapCountry")[0].options[i].value;
			$("#includeOverviewMapCheckbox").prop("checked", true);
		}
	}

	var overviewMapImageHeight = 0.2 * previewImageHeight;
	$("#overviewMapImage").css("height", overviewMapImageHeight);
	$("#overviewMapImage").attr("src", overviewMapImagesDirectory + overviewMapCountry + ".gif");
	if (!$("#includeOverviewMapCheckbox").prop("checked")) { $("#overviewMapImage").fadeTo("fast", 0.5); }
}

function getExportUrlParams(layerIndex)
{
	var exportUrlParams = "";
	exportUrlParams += "country=" + $("#overviewMapCountry").val();
        exportUrlParams += "&footerAcquisitionDateText=" + footerAcquisitionDateTextArray[layerIndex];
        exportUrlParams += "&footerAcquisitionDateTextColor=" + $("#footerAcquisitionDateTextColorInput").val();
        exportUrlParams += "&footerLocationText=" + footerLocationTextArray[layerIndex];
        exportUrlParams += "&footerLocationTextColor=" + $("#footerLocationTextColorInput").val();
        exportUrlParams += "&footerSecurityClassificationText=" + footerSecurityClassificationTextArray[layerIndex];
        exportUrlParams += "&footerSecurityClassificationTextColor=" + $("#footerSecurityClassificationTextColorInput").val();
        exportUrlParams += "&format=" + format;
	exportUrlParams += "&gradientColorBottom=" + $("#gradientColorBottomInput").val();
        exportUrlParams += "&gradientColorTop=" + $("#gradientColorTopInput").val();
        exportUrlParams += "&headerDescriptionText=" + headerDescriptionTextArray[layerIndex];
        exportUrlParams += "&headerDescriptionTextColor=" + $("#headerDescriptionTextColorInput").val();
        exportUrlParams += "&headerSecurityClassificationText=" + headerSecurityClassificationTextArray[layerIndex];
        exportUrlParams += "&headerSecurityClassificationTextColor=" + $("#headerSecurityClassificationTextColorInput").val();
        exportUrlParams += "&headerTitleText=" + headerTitleTextArray[layerIndex];
        exportUrlParams += "&headerTitleTextColor=" + $("#headerTitleTextColorInput").val();
        exportUrlParams += "&imageUrl=" + imageUrlArray[layerIndex];
        exportUrlParams += "&imageHeight=" + previewImageHeight;
        exportUrlParams += "&imageWidth=" + previewImageWidth;
        exportUrlParams += "&includeOverviewMap=" + $("#includeOverviewMapCheckbox")[0].checked;
        exportUrlParams += "&logo=" + $("#logo").val();
        exportUrlParams += "&northArrowAngle=" + northAngleArray[layerIndex];
        exportUrlParams += "&northArrowBackgroundColor=" + $("#northArrowBackgroundColorInput").val();
        exportUrlParams += "&northArrowColor=" + $("#northArrowColorInput").val();
        exportUrlParams += "&northArrowSize=" + $("#northArrowImage").height();

	return exportUrlParams;
}

function goToNextImage()
{
	currentLayer++;
	if (currentLayer >= imageUrlArray.length - 1)
	{
		currentLayer = imageUrlArray.length - 1;
		$("#nextImageButton").attr("disabled", true);
		$("#nextImageButton").fadeTo("fast", 0.3);
	}
	$("#previousImageButton").attr("disabled", false);
	$("#previousImageButton").fadeTo("fast", 1);

	$("#previewImage" + (currentLayer - 1)).hide();
	$("#previewImage" + currentLayer).show();

	updateTemplate();
}

function goToPreviousImage()
{
	currentLayer--;
	if (currentLayer <= 0)
	{
		currentLayer = 0;
		$("#previousImageButton").attr("disabled", true);
		$("#previousImageButton").fadeTo("fast", 0.3);
	}
	$("#nextImageButton").attr("disabled", false);
	$("#nextImageButton").fadeTo("fast", 1);

	$("#previewImage" + (currentLayer + 1)).hide();
	$("#previewImage" + currentLayer).show();

	updateTemplate();
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
	updateTemplate();
	generateHeaderSecurityClassificationText();
	generateHeaderTitleText();
	generateHeaderDescriptionText();
	generateOverviewMap();
	generateNorthArrow();
	generateFooter();
	generateFooterSecurityClassificationText();
	generateFooterLocationText();
	generateFooterAcquisitionDateText();
	windowResize();
	$("#loadingDialog").dialog("close");

	if (imageUrlArray.length > 1) { $("#nextImageButton").attr("disabled", false); $("#nextImageButton").fadeTo("fast", 1); }
}

function isTheFlipBookFinished()
{
	var howManyFlipBookChipsHaveBeenMade = 0;
	$.each(flipBookChipReadinessArray, function(i, x) { if (x == "Ready") { howManyFlipBookChipsHaveBeenMade++; } });

	if (howManyFlipBookChipsHaveBeenMade == flipBookChipReadinessArray.length)
	{
		$.ajax
		({
			async: true,
			data: "fileNames=" + flipBookChipFileNameArray.join(">") + "&format=" + format,
			dataType: "text",
			success: function(data)
			{
				$("#currentProductProgressDiv").html("100%");
				$("#productLinkDiv").html
				(
					"<a " +
						"href = '" + viewProductUrl + "?fileName=" + data + "' " +
						"onclick = 'javascript:$(\"#productGenerationStatusDialog\").dialog(\"close\")' " +
						"style = 'color: blue'" +
						"target = '_blank'><u>Link</u>" +
					"</a>"
				);
			},
			url: exportAnimationFormUrl
		});	
	}
	else 
	{ 
		$("#currentProductProgressDiv").html(parseInt(howManyFlipBookChipsHaveBeenMade / (flipBookChipReadinessArray.length + 1) * 100) + "%"); 
		download();
	}
}

function positionNorthArrow()
{
	var northArrowOffsetHeight = logoOffset;
	var northArrowOffsetWidth = logoOffset;

	$("#northArrowImage").position({ my: "right top", at: "left top", of: $("#overviewMapImage"), offset: "-" + logoOffset + " 0", collision: "none" });

	$("#northArrowSpinnerDiv").position({ my: "middle center", at: "middle center", of: $("#northArrowImage"), offset: "0 0", collision: "none" });
}

function positionOverviewMapImage()
{
	var overviewMapImageOffsetHeight = logoOffset;
	var overviewMapImageOffsetWidth = 2 * logoOffset;
	$("#overviewMapImage").position({ my: "right top", at: "right top", of: $("#headerDiv"), offset: "-" + overviewMapImageOffsetWidth + " " + overviewMapImageOffsetHeight, collision: "none" });	
}

function removeNorthArrowSpinner() { northArrowSpinner.stop(); }

function setupDialogs()
{
	$("#changeColorGradientDialog").dialog
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
	$("#changeColorGradientDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeColorGradientDialog").css("textAlign", "left");

	$("#changeFooterAcquisitionDateTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerAcquisitionDateTextInput").val();
				$("#footerAcquisitionDateTextDiv").html(text);
				footerAcquisitionDateTextArray[currentLayer] = text;

				var color = $("#footerAcquisitionDateTextColorInput").val();
				$("#footerAcquisitionDateTextDiv").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterAcquisitionDateTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeFooterAcquisitionDateTextDialog").css("textAlign", "left");

	$("#changeFooterLocationTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerLocationTextInput").val();
				$("#footerLocationTextDiv").html(text);
				footerLocationTextArray[currentLayer] = text;

				var color = $("#footerLocationTextColorInput").val();
				$("#footerLocationTextDiv").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterLocationTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeFooterLocationTextDialog").css("textAlign", "left");

	$("#changeFooterSecurityClassificationTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#footerSecurityClassificationTextInput").val();
				$("#footerSecurityClassificationTextDiv").html(text);
				footerSecurityClassificationTextArray[currentLayer] = text;

				var color = $("#footerSecurityClassificationTextColorInput").val();
				$("#footerSecurityClassificationTextDiv").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeFooterSecurityClassificationTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeFooterSecurityClassificationTextDialog").css("textAlign", "left");

	$("#changeHeaderDescriptionTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerDescriptionTextInput").val();
				$("#headerDescriptionTextDiv").html(text);
				headerDescriptionTextArray[currentLayer] = text;

				var color = $("#headerDescriptionTextColorInput").val();
				$("#headerDescriptionTextDiv").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderDescriptionTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeHeaderDescriptionTextDialog").css("textAlign", "left");

	$("#changeHeaderSecurityClassificationTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerSecurityClassificationTextInput").val();
				$("#headerSecurityClassificationTextDiv").html(text);
				headerSecurityClassificationTextArray[currentLayer] = text;

				var color = $("#headerSecurityClassificationTextColorInput").val();
				$("#headerSecurityClassificationTextDiv").css("color", "#" + color);				
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderSecurityClassificationTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeHeaderSecurityClassificationTextDialog").css("textAlign", "left");

	$("#changeHeaderTitleTextDialog").dialog
	({
        	autoOpen: false,
		buttons:
		{
			"OK": function()
			{
				$(this).dialog("close");
				var text = $("#headerTitleTextInput").val();
				$("#headerTitleTextDiv").html(text);
				headerTitleTextArray[currentLayer] = text;

				var color = $("#headerTitleTextColorInput").val();
				$("#headerTitleTextDiv").css("color", "#" + color);
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
	$("#changeHeaderTitleTextDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeHeaderTitleTextDialog").css("textAlign", "left");

	$("#changeLogoDialog").dialog
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
	$("#changeLogoDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeLogoDialog").css("textAlign", "left");

	$("#changeNorthArrowDialog").dialog
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
	$("#changeNorthArrowDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeNorthArrowDialog").css("textAlign", "left");

	$("#changeOverviewMapDialog").dialog
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
	$("#changeOverviewMapDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#changeOverviewMapDialog").css("textAlign", "left");

	$("#downloadDialog").dialog
	({
		autoOpen: false,
		buttons:
		{
			"OK": function() { $(this).dialog("close"); }
		},
		modal: true,
		width: "auto"
	});
	$("#downloadDialog").parent().find('a.ui-dialog-titlebar-close').remove();
	$("#downloadDialog").css("textAlign", "left");

	$("#loadingDialog").dialog({draggable: false, modal: true, resizable: false, width: "auto"});
        $("#loadingDialog").parent().find("a.ui-dialog-titlebar-close").remove();
	
	$("#productGenerationProgressDialog").dialog({ autoOpen: false, draggable: false, modal: true, resizable: false, width: "auto" });
	$("#productGenerationProgressDialog").parent().find("a.ui-dialog-titlebar-close").remove();
}

function updateTemplate()
{
	$("#footerAcquisitionDateTextInput").val(footerAcquisitionDateTextArray[currentLayer]);
	generateFooterAcquisitionDateText();

	$("#footerLocationTextArrayInput").val(footerLocationTextArray[currentLayer]);
	generateFooterLocationText();

	$("#footerSecurityClassificationTextArrayInput").val(footerSecurityClassificationTextArray[currentLayer]);
	generateFooterSecurityClassificationText();

	$("#headerDescriptionTextArrayInput").val(headerDescriptionTextArray[currentLayer]);
	generateHeaderDescriptionText();

	$("#headerSecurityClassificationTextInput").val(headerDescriptionTextArray[currentLayer]);
	generateHeaderSecurityClassificationText();

	$("#headerTitleTextInput").val(headerTitleTextArray[currentLayer]);
	generateHeaderTitleText();
}

function windowResize()
{
	var headerPositionHorizontal;
	if ($("#headerDiv").width() > $(window).width()) { headerPositionHorizontal = "left"; }
	else { headerPositionHorizontal = "middle"; }
	$("#headerDiv").position({ my: headerPositionHorizontal + " top", at: headerPositionHorizontal + " bottom", of: $("#templateExportMenu"), offset: "0 0", collision: "none" });

	$("#previewImageDiv").position({ my: "left top", at: "left bottom", of: $("#headerDiv"), offset: "0 0", collision: "none" });

	$("#logoImage").position({ my: "left center", at: "left center", of: $("#headerDiv"), offset: logoOffset + " 0", collision: "none" });

	var headerSecurityClassificationTextOffsetHeight = logoOffset;
	var headerSecurityClassificationTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerSecurityClassificationTextDiv").position({ my: "left top", at: "left top", of: $("#headerDiv"), offset: headerSecurityClassificationTextOffsetWidth + " " + headerSecurityClassificationTextOffsetHeight, collision: "none" });

	var headerTitleTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight;
	var headerTitleTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerTitleTextDiv").position({ my: "left top", at: "left top", of: $("#headerDiv"), offset: headerTitleTextOffsetWidth + " " + headerTitleTextOffsetHeight, collision: "none" });

	var headerDescriptionTextOffsetHeight = logoOffset + headerSecurityClassificationTextHeight + headerTitleTextHeight;
	var headerDescriptionTextOffsetWidth = logoOffset + logoWidth + logoOffset;
	$("#headerDescriptionTextDiv").position({ my: "left top", at: "left top", of: $("#headerDiv"), offset: headerDescriptionTextOffsetWidth + " " + headerDescriptionTextOffsetHeight, collision: "none" });

	$("#northArrowImage").position({ my: "right top", at: "left top", of: $("#overviewMapImage"), offset: "-" + logoOffset + " 0", collision: "none" });

	$("#northArrowSpinnerDiv").position({ my: "middle center", at: "middle center", of: $("#northArrowImage"), offset: "0 0", collision: "none" });

	var overviewMapImageOffsetHeight = logoOffset;
	var overviewMapImageOffsetWidth = 2 * logoOffset;
	$("#overviewMapImage").position({ my: "right top", at: "right top", of: $("#headerDiv"), offset: "-" + overviewMapImageOffsetWidth + " " + overviewMapImageOffsetHeight, collision: "none" });

	$("#footerDiv").position({ my: "left top", at: "left bottom", of: $("#previewImageDiv"), offset: "0 0", collision: "none" });

	var footerSecurityClassificationTextOffsetHeight = 0;
	var footerSecurityClassificationTextOffsetWidth = logoOffset;
	$("#footerSecurityClassificationTextDiv").position({ my: "left top", at: "left top", of: $("#footerDiv"), offset: footerSecurityClassificationTextOffsetWidth + " " + footerSecurityClassificationTextOffsetHeight, collision: "none" });

	$("#footerLocationTextDiv").position({ my: "center top", at: "center top", of: $("#footerDiv"),voffset: "0 0", collision: "none" });

	$("#footerAcquisitionDateTextDiv").position({ my: "right top", at: "right top", of: $("#footerDiv"), offset: "0 0", collision: "none" });
}
