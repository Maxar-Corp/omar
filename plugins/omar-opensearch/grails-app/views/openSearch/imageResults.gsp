<%--
  Created by IntelliJ IDEA.
  User: gpotts
  Date: 8/11/15
  Time: 7:25 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <asset:stylesheet src="imageResults.css"/>
</head>

<body>
<div class="ui-layout-north"><omar:securityClassificationBanner/></div>
<div class="ui-layout-center">
    <div id="ResultsView">
        <table id="DataTable" cellspacing="0" >
        </table>
    </div>
</div>
<div class="ui-layout-south"><omar:securityClassificationBanner/></div>



<asset:javascript src="imageResults.js"/>

<g:javascript>

function init()
{
/*
12356N 1234567E

12356.00N 1234567.00E

12°34'56N 123°45'67E

12°34'56.00 N 123°45'67.00 E

12°34'56.00N 3°45'67.00E

12 34 56N 123 45 67E

12:34:56 N 123:45:67 E

12:34:56.00N 123:45:67.00E

34°36'57.0"S 58°25'60.0"W (Buenos Aires)

35°32'20.2"N 82°33'55.5"W (Asheville, NC)

18S UJ 23480 06470 (Washington D.C.)
*/
//var dms1String = "12356N 1234567E, 444.555";
//var dms2String = "34°36'57.0\"S 58°25'60.0\"W";
//var d = "99.454N,110.1234,45";
   var initParams = ${raw(initParams.toString())};
//   var coordinateConversion = new CoordinateConversion();
//   var mgrs = coordinateConversion.ddToMgrs(45,45)+",40000.30";
//   console.log(mgrs);
//   console.log(mgrs.match(OMAR.regexp.mgrsrRegExp));
//   console.log(dms1String.match(OMAR.regexp.dmsrRegExp));
//   console.log(dms2String.match(OMAR.regexp.dmsRegExp));
//   console.log(d.match(OMAR.regexp.ddrRegExp));

   var imageSearch = new OMAR.pages.imageSearch( jQuery, initParams );
   imageSearch.render();
}

</g:javascript>

</body>
</html>