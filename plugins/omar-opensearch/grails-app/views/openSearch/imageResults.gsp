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
<div id="ResultsView">
    <table id="DataTable" cellspacing="0px">
    </table>
</div>

<asset:javascript src="imageResults.js"/>

<g:javascript>

function init()
{
   var initParams = ${raw(initParams.toString())};

   var imageSearch = new OMAR.pages.imageSearch( jQuery, initParams );
   imageSearch.render();
}

</g:javascript>

</body>
</html>