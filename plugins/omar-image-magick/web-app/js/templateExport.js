var countryMap;

function changeLogo(logo)
{
	document.getElementById("logoImage").src = document.getElementById("imageResourceLocation").innerHTML + logo + ".png";
}

function changeCountry(country)
{
	countryMap = country;
	updateCountryMaps();
}

function exportPreview()
{
	exportPreviewSetup();
	var previewUrl = document.getElementById("exportPreviewUrl").innerHTML;
		var countryCode = document.getElementById("country").options.selectedIndex;
	previewUrl += "?country=" + document.getElementById("country").options[countryCode].value;
	previewUrl += "&imageURL=" + (document.getElementById("imageURL").value).replace(/&/g,"%26");
	previewUrl += "&includeOutlineMap=null";

	if (document.getElementById("includeOverviewMap").checked)
	{
		previewUrl += "&includeOverviewMap=on";
	}
	else
	{
		previewUrl += "&includeOverviewMap=null";
	}
		var logoId = document.getElementById("logo").options.selectedIndex;
	previewUrl += "&logo=" + document.getElementById("logo").options[logoId].value;
	previewUrl += "&line1=" + document.getElementById("line1").value;
	previewUrl += "&line2=" + document.getElementById("line2").value;
	previewUrl += "&line3=" + document.getElementById("line3").value;
	previewUrl += "&northArrowAngle=" + document.getElementById("northArrowAngle").value;
	previewUrl += "&securityClassification=" + document.getElementById("securityClassification").value;
	document.getElementById("preview").onload = function() { exportPreviewCleanup(); }
	document.getElementById("preview").src = previewUrl;
}

function exportPreviewCleanup()
{
	document.getElementById("templateStatus").style.display = "none";
	document.getElementById("loading").style.display = "none";
	document.getElementById("preview").style.display = "block";
}

function exportPreviewSetup()
{
	document.getElementById("templateStatus").style.display = "block";
	document.getElementById("loading").src = document.getElementById("imageResourceLocation").innerHTML + "imageLoading.gif";
	document.getElementById("loading").style.display = "block";
	document.getElementById("preview").style.display = "none";
}

function init()
{
	countryMap = "aa";
	var countryCode = document.getElementById("countryCode").innerHTML.toLowerCase();
	for (var i = 0; i < document.getElementById("country").options.length; i++)
	{
		if (countryCode == document.getElementById("country").options[i].value)
		{
			document.getElementById("country").options.selectedIndex = i;
			countryMap = document.getElementById("country").options[i].value;
			document.getElementById("includeOverviewMap").checked = true;
		}
	}
	updateCountryMaps();
}

function submitTemplate()
{
	document.getElementById("templateStatus").style.display = "block";
}

function updateCountryMaps()
{
	if (document.getElementById("includeOverviewMap").checked)
	{
		document.getElementById("overviewMap").style.visibility = "visible";
		var image = document.getElementById("imageResourceLocation").innerHTML + "overviewMaps/" + countryMap + ".gif";
		document.getElementById("overviewMap").innerHTML = "<img src = '" + image + "' width = '100'/>";
	}
	else
	{
		document.getElementById("overviewMap").style.visibility = "hidden";
	}
}

(function ()
{
	var Dom = YAHOO.util.Dom,
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
			Dom.setStyle( document.body, 'visibility', 'visible' );
		});
		layout.render();
	});
})();
