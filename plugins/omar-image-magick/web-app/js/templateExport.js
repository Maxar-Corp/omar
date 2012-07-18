//var countryMap;

//function changeLogo(logo)
//{
//	${"logoImage"}.src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}" + logo + ".png?_debugResources=y";
//}

//function changeCountry(country)
//{
//	countryMap = country;
//	updateCountryMaps();
//}

//function exportPreview()
//{
//	exportPreviewSetup();
//	var previewUrl = "${createLink(action: 'exportPreview')}";
//	var countryCode = ${"country"}.options.selectedIndex;
//	previewUrl += "?country=" + ${"country"}.options[countryCode].value;
//	previewUrl += "&imageURL=" + (${"imageURL"}.value).replace(/&/g,"%26");
//	previewUrl += "&includeOutlineMap=null";
//	if (${"includeOverviewMap"}.checked)
//	{
//		previewUrl += "&includeOverviewMap=on";
//	}
//	else
//	{
//		previewUrl += "&includeOverviewMap=null";
//	}
//	var logoId = ${"logo"}.options.selectedIndex;
//	previewUrl += "&logo=" + ${"logo"}.options[logoId].value;
//	previewUrl += "&line1=" + ${"line1"}.value;
//	previewUrl += "&line2=" + ${"line2"}.value;
//	previewUrl += "&line3=" + ${"line3"}.value;
//	previewUrl += "&northArrowAngle=" + ${"northArrowAngle"}.value;
//	previewUrl += "&securityClassification=" + ${"securityClassification"}.value;
//	${"preview"}.onload = function() { exportPreviewCleanup(); }
//	${"preview"}.src = previewUrl;
//}

//function exportPreviewCleanup()
//{
//	${"templateStatus"}.style.display = "none";
//	${"loading"}.style.display = "none";
//	${"preview"}.style.display = "block";
//}

//function exportPreviewSetup()
//{
//	${"templateStatus"}.style.display = "block";
//	${"loading"}.src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}imageLoading.gif?_debugResources=y";
//	${"loading"}.style.display = "block";
//	${"preview"}.style.display = "none";
//}

//function init()
//{
//	${"templateStatus"}.style.display = "none";
//	countryMap = "aa";
//	var countryCode = "${countryCode}".toLowerCase();
//	for (var i = 0; i < ${"country"}.options.length; i++)
//	{
//		if (countryCode == ${"country"}.options[i].value)
//		{
//			${"country"}.options.selectedIndex = i;
//			countryMap = ${"country"}.options[i].value;
//		}
//	}
//	updateCountryMaps();
//	changeLogo("ciaLogo");
//}

//function submitTemplate()
//{
//	${"templateStatus"}.style.display = "block";
//}

//function updateCountryMaps()
//{
//	if (${"includeOverviewMap"}.checked)
//	{
//		${"overviewMap"}.style.visibility = "visible";
//		var image = "${resource(dir: 'images/overviewMaps/', plugin: 'omar-image-magick')}" + countryMap + ".gif?_debugResources=y";
//		${"overviewMap"}.innerHTML = "<img src = '" + image + "' width = '100'/>";
//	}
//	else
//	{
//		${"overviewMap"}.style.visibility = "hidden";
//	}
//}
