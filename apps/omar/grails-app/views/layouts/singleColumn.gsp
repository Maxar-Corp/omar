<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/12/11
  Time: 1:29 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
      resource(dir: "js", file: "application.js"),
      resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
      resource(plugin: "richui", dir: "js/datechooser", file: "datechooser.js"),
      resource(plugin: "richui", dir: "js/yui/calendar", file: "calendar-min.js"),
      resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
      resource(plugin: "richui", dir: "js/yui/tabview", file: "tabview-min.js")
  ])}'></script>

  <style>
  body {
    margin: 0;
    padding: 0; /* visibility: hidden;*/
    /*
    background-color: #f2f2f2;
    */
  }

<%--
  .banner {
    background-color: black;
  }

  .top {
    background-color: yellow;
  }

  .bottom {
    background-color: orange;
  }

  .left {
    background-color: red;
    width: 200px;
  }

  .right {
    background-color: blue;
    width: 200px;
  }

  .center {
    background-color: green;
  }
--%>
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
</head>
<body class="${pageProperty(name: 'body.class')}">
<table width='100%' height='100%'>
  <tr height="25px" width="1024">
    <td class='banner' colspan='3'>
      <omar:securityClassificationBanner/>
    </td>
  </tr>
  <tr height="50px">
    <td class='top' colspan='3'>
      <g:pageProperty name="page.top"/>
    </td>
  </tr>
  <tr>
    <td class='center' style="min-height:200px;">
      <g:pageProperty name="page.center"/>
    </td>
  </tr>
  <tr height="50px">
    <td class='bottom' colspan='3'>
      <g:pageProperty name="page.bottom"/>
    </td>
  </tr>
  <tr height="25px">
    <td class='banner' colspan='3'>
      <omar:securityClassificationBanner/>
    </td>
  </tr>
  <g:layoutBody/>
</table>
</body>
<g:javascript>
  (function()
  {
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    Event.onDOMReady( function()
    {
    } );
  })();
</g:javascript>
</html>
