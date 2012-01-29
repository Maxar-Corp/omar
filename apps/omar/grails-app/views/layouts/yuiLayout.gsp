<html>
<head>
  <style type="text/css">
  body {
    visibility: hidden;
  }

  .yui-skin-sam .yui-layout .yui-layout-unit-top,
  .yui-skin-sam .yui-layout .yui-layout-unit-top div.yui-layout-bd-nohd {
    overflow: visible;
  }

    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */
  body {
    margin: 0;
    padding: 0;
    visibility: hidden;
    overflow: hidden;
  }

  #middle1 {
    height: 100%;
    width: 100%;
  }

  #bottom2 {
    font-size: 10px;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>

  <g:layoutHead/>
  <r:require modules="yuiLayout"/>
  <r:layoutResources/>

</head>

<body class="yui-skin-sam">
<div id="top1" class="top1">
  <g:pageProperty name="page.top"/>
</div>

<div id="middle1" class="middle1">
  <div id="left1" class="left1">
    <g:pageProperty name="page.left"/>
  </div>

  <div id="center1" class="center1">
    <g:pageProperty name="page.center"/>
  </div>

  <div id="right1" class="right1">
    <g:pageProperty name="page.right"/>
  </div>
</div>

<div id="bottom1" class="bottom1">
  <g:pageProperty name="page.bottom"/>
</div>


<r:script>
  (function ()
  {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady( function ()
    {
      var layout = new YAHOO.widget.Layout( {
        units:[
          { position:"top", height:50, body:"top1", zIndex:2},
          { position:"left", width:200, body:"left1", collapse:true, scroll:true},
          { position:"center", body:"center1" },
          { position:"right", width:175, collapse:true, scroll:true, body:"right1" },
          { position:"bottom", height:25, body:"bottom1" }
        ]
      } );

      layout.on( "render", function ()
      {
        bodyOnResize();
        init();
        Dom.setStyle( document.body, "visibility", "visible" );
      } );
      layout.render();

      layout.getUnitByPosition( "left" ).on( "collapse", function ()
      {
        bodyOnResize();
      } );

      layout.getUnitByPosition( "left" ).on( "expand", function ()
      {
        bodyOnResize();
      } );

      layout.getUnitByPosition( "right" ).on( "collapse", function ()
      {
        bodyOnResize();
      } );

      layout.getUnitByPosition( "right" ).on( "expand", function ()
      {
        bodyOnResize();
      } );
    } );
  })();

  var bodyOnResize = function ()
  {
    var Dom = YAHOO.util.Dom;

    var top1 = Dom.get( "top1" );
    var middle1 = Dom.get( "middle1" );
    var left1 = Dom.get( "left1" );
    var top2 = Dom.get( "top2" );
    var mapDiv = Dom.get( "map" );
    var bottom2 = Dom.get( "bottom2" );
    var right1 = Dom.get( "right1" );
    var bottom1 = Dom.get( "bottom1" );

    var mapWidth = middle1.offsetWidth - (left1.offsetWidth + right1.offsetWidth);
    var mapHeight = middle1.offsetHeight - 7 - (top1.offsetHeight + bottom1.offsetHeight + top2.offsetHeight + bottom2.offsetHeight);


    if ( mapWidth < 0 )
    {
      mapWidth = -mapWidth;
    }

    Dom.setStyle( mapDiv, 'width', mapWidth + "px" );
    Dom.setStyle( mapDiv, 'height', mapHeight + "px" );
  };
</r:script>
<g:layoutBody/>
<r:layoutResources/>
</body>
</html>