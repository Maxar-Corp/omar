var acquisitionDateTextColor;
var countryMap;
var descriptionTextColor;
var dom = YAHOO.util.Dom;
var locationTextColor;
var securityClassificationTextColor;
var titleTextColor;

function changeLogo(logo)
{
	dom.get("logoImage").src = document.getElementById("imageResourceLocation").innerHTML + logo + "ForWeb.png";
}

function changeCountry(country)
{
	countryMap = country;
	updateCountryMaps();
}

function exportPreview()
{
	exportPreviewSetup();
	getTextColors();
	var previewUrl = dom.get("exportPreviewUrl").innerHTML;
		var countryCode = dom.get("country").options.selectedIndex;
	previewUrl += "?country=" + dom.get("country").options[countryCode].value;
	previewUrl += "&imageURL=" + (dom.get("imageURL").value).replace(/&/g,"%26");
	previewUrl += "&includeOutlineMap=null";

	if (dom.get("includeOverviewMap").checked)
	{
		previewUrl += "&includeOverviewMap=on";
	}
	else
	{
		previewUrl += "&includeOverviewMap=null";
	}
		var logoId = dom.get("logo").options.selectedIndex;
	previewUrl += "&logo=" + dom.get("logo").options[logoId].value;
	previewUrl += "&title=" + dom.get("title").value;
	previewUrl += "&titleTextColor=" + titleTextColor;
	previewUrl += "&description=" + dom.get("description").value;
	previewUrl += "&descriptionTextColor=" + descriptionTextColor;
	previewUrl += "&location=" + dom.get("location").value;
	previewUrl += "&locationTextColor=" + locationTextColor;
	previewUrl += "&acquisitionDate=" + dom.get("acquisitionDate").value;
	previewUrl += "&acquisitionDateTextColor=" + acquisitionDateTextColor; 
	previewUrl += "&securityClassification=" + dom.get("securityClassification").value;
	previewUrl += "&securityClassificationTextColor=" + securityClassificationTextColor;
	previewUrl += "&northArrowAngle=" + dom.get("northArrowAngle").value;
	previewUrl += "&gradientColorTop=" + dom.get("gradientColorTop").value;
	previewUrl += "&gradientColorBottom=" + dom.get("gradientColorBottom").value;
	dom.get("preview").onload = function() { exportPreviewCleanup(); }
	dom.get("preview").src = previewUrl;
}

function exportPreviewCleanup()
{
	dom.get("templateStatus").style.display = "none";
	dom.get("loading").style.display = "none";
	dom.get("preview").style.display = "block";
}

function exportPreviewSetup()
{
	dom.get("templateStatus").style.display = "block";
	dom.get("loading").src = dom.get("imageResourceLocation").innerHTML + "imageLoading.gif";
	dom.get("loading").style.display = "block";
	dom.get("preview").style.display = "none";
}

function getTextColors()
{
	acquisitionDateTextColor = dom.get("acquisitionDateTextColor").value;
	descriptionTextColor = dom.get("descriptionTextColor").value;
	locationTextColor = dom.get("locationTextColor").value;
	securityClassificationTextColor = dom.get("securityClassificationTextColor").value;
	titleTextColor = dom.get("titleTextColor").value;
}

function gradientGenerator()
{
	var gradientGeneratorUrl = dom.get("gradientGeneratorUrl").innerHTML;
	gradientGeneratorUrl += "?gradientColorTop=" + dom.get("gradientColorTop").value;
	gradientGeneratorUrl += "&gradientColorBottom=" + dom.get("gradientColorBottom").value;
	gradientGeneratorUrl += "&gradientHeight=" + dom.get("headerFooterGradient").offsetHeight;
	dom.get("headerFooterGradient").style.backgroundImage = "url('" + gradientGeneratorUrl + "')"; 
}

function init()
{
	countryMap = "aa";
	var countryCode = dom.get("countryCode").innerHTML.toLowerCase();
	for (var i = 0; i < dom.get("country").options.length; i++)
	{
		if (countryCode == dom.get("country").options[i].value)
		{
			dom.get("country").options.selectedIndex = i;
			countryMap = dom.get("country").options[i].value;
			dom.get("includeOverviewMap").checked = true;
		}
	}
	updateCountryMaps();
}

function resetColors()
{
	dom.get("titleTextColor").color.fromString("FFFF00");
	dom.get("descriptionTextColor").color.fromString("FFFFFF");
	dom.get("locationTextColor").color.fromString("ADD8E6");
	dom.get("acquisitionDateTextColor").color.fromString("ADD8E6");
	dom.get("securityClassificationTextColor").color.fromString("ADD8E6");
	dom.get("gradientColorBottom").color.fromString("000000");	
	dom.get("gradientColorTop").color.fromString("595454");
	gradientGenerator();
}

function submitTemplate()
{
	dom.get("templateStatus").style.display = "block";
}

function updateCountryMaps()
{
	if (dom.get("includeOverviewMap").checked)
	{
		dom.get("overviewMap").style.visibility = "visible";
		var image = dom.get("imageResourceLocation").innerHTML + "overviewMaps/" + countryMap + ".gif";
		dom.get("overviewMap").innerHTML = "<img src = '" + image + "' width = '100'/>";
	}
	else
	{
		dom.get("overviewMap").style.visibility = "hidden";
	}
}

(function ()
{
	Event = YAHOO.util.Event;
	Event.onDOMReady( function ()
	{
		var layout = new YAHOO.widget.Layout( 
		{
			units:[
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
	});
	gradientGenerator();
})();
