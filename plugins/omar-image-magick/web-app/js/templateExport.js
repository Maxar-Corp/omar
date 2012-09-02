var countryMap;
var dom = YAHOO.util.Dom;

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
	previewUrl += "&description=" + dom.get("description").value;
	previewUrl += "&location=" + dom.get("location").value;
	previewUrl += "&acquisitionDate=" + dom.get("acquisitionDate").value;
	previewUrl += "&securityClassification=" + dom.get("securityClassification").value;
	previewUrl += "&northArrowAngle=" + dom.get("northArrowAngle").value;
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
})();
