<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/12/11
  Time: 1:29 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
<style>
body {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow-y: hidden;
  overflow-x: hidden;
}

#content {
  height: 100%;
  min-height: 100%;
  margin-bottom: -20px
}

#header {
  position: relative;
  width: 100%;
  height: 20px;
  top: 0px;
}

center {
  position: relative;
  width: 100%;
}

footer {
  position: absolute;
  width: 100%;
  bottom: 0px;
  height: 20px;
}
</style>
<title><g:layoutTitle default="Grails"/></title>
<g:layoutHead/>
<r:require modules="singleColumn"/>
<r:layoutResources/>

</head>

<body class="${pageProperty(name: 'body.class')} onresize="bodyOnResize();" onload="${pageProperty(name: 'body.onload')};bodyOnResize();">
<div id="content">

  <div id="header">
    <omar:securityClassificationBanner/>
  </div>

  <div id="top">
    <g:pageProperty name="page.top"/>
  </div>

  <div id="center">
    <g:pageProperty name="page.center"/>
  </div>
</div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>
</table>
<g:layoutBody/>
<r:script>
  //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
  var bodyOnResize = function ()
  {
    var Dom = YAHOO.util.Dom;
    var top = Dom.get( "top" );
    var header = Dom.get( "header" );
    var footer = Dom.get( "footer" );
    var center = Dom.get( "center" );

    center.style.height = Math.abs( footer.offsetTop - (top.offsetTop + top.offsetHeight) );

  }
</r:script>
<r:layoutResources/>

</body>
</html>
