<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 10:27 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Image Space - ${(rasterEntry?.filename)}</title>
  <meta content="imageSpacePageLayout" name="layout">
  <r:require modules="imageSpacePageLayout"/>
  <style type="text/css">
  #slider-brightness-bg, #slider-contrast-bg {
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
    width: 120px;
  }

  #slider-rotate-bg {
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
    width: 180px;
  }

 .olControlPanel .olControlButtonPanItemActive { 
  width:  24px;  
  height: 22px;
  background-image: url(${resource(plugin: 'openlayers', dir:'js/theme/default/img/', file:'pan_on.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonPanItemInactive { 
  width:  24px;  
  height: 22px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'js/theme/default/img/', file:'pan_off.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonSelectAOIItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis/grass', file:'mActionSelect.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonSelectAOIItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis/grass', file:'mActionSelect.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonDeleteAOIItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionDeleteSelected.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonDeleteAOIItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionDeleteSelected.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePointItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'bullseye.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePointItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'bullseye.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePathItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasure.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePathItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasure.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasureAreaItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasureArea.png')});
  background-repeat: no-repeat;
}

#hudChildDivId {
	width:50px;
}
</style>

<g:javascript dir="js" src="matrix.js"/>
<g:javascript dir="js" src="OpenLayersImageManipulator.js"/>

</head>

<body class=" yui-skin-sam">
 <g:form name="wmsFormId" method="POST"></g:form>
<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: 0}"/>
<input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: 0}"/>

<content tag="top1">
  <g:render template="imageSpaceMenu" model="${[rasterEntry: rasterEntry]}"/>
</content>

<content tag="bottom1"></content>

<content tag="left1">
  <g:render template="imageSpaceAdjustments" model="${[rasterEntry: rasterEntry, params: params]}"/>
</content>

<%--
<content tag="right1"></content>
--%>

<content tag="top2">
  <div id="toolBar" class="olControlPanel"></div>
</content>

<content tag="bottom2">

      <div id="mouseDisplayId" align="left"></div>

<!--  <table><tr>
    <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
  </tr></table>
-->
</content>

<content tag="center2">

    
 <div id="eventDivId"></div>
 <div id="hudDivId">
	<div id="hudChildDivId">
		<img src="${resource(plugin: 'omar', dir: 'images', file: 'north_arrow.png')}">
	</div>
 </div>
 <div id="map"></div>
</content>

<r:script>
var hudChildDivId = document.getElementById("hudChildDivId")

OMAR.ProjectionType = {
  PIXEL: 0,
  GEOGRAPHIC: 1
};

OMAR.ToolModeType = {
  PAN_ZOOM: "panzoom",
  ZOOM_BOX: "zoombox",
  BOX_AOI: "boxaoi",
  POINT: "point",
  LINE: "line",
  POLYGON: "polygon"
};

OMAR.OpenLayersImageManipulator = OpenLayers.Class({
  mouseDragStart:null,
  map:null,
  zoomBox:null,
  selectBox:null,
  mousePosition:null,
  eventDiv:null,
  annotationDiv:null,
  affineParams:null,
  affineM: null,
  containerDiv: null,
  containerDivRegion: null,
  projectionType: OMAR.ProjectionType.PIXEL,
  documentListenersAdded: false,
  toolMode: OMAR.ToolModeType.PAN_ZOOM,
  vectorLayer: null,
  drawControls:null,
  currentDrawControl:null,
  wheelListener:null,
  events: null,
  editMode:null,
  EVENT_TYPES:["measureAddPointFinished"],
   destroy : function() 
   {
        this.events.un ({
            "click":this.click,
            "dblclick":this.dblClick,
            "mousedown":this.mousedown,
            "mouseup":this.mouseup,
            "mousemove": this.mousemove,
            "mouseout": this.mouseout,
            "wheel": this.wheel
    });
   //   YAHOO.util.Event.removeListener(this.eventDiv, "click", this.click);
   //   YAHOO.util.Event.removeListener(this.eventDiv, "dblclick", this.dblClick);
   //   YAHOO.util.Event.removeListener(this.eventDiv, "mousedown", this.mousedown);
   //   YAHOO.util.Event.removeListener(this.eventDiv, "mouseup", this.mouseUu);
   //   YAHOO.util.Event.removeListener(this.eventDiv, "mousemove", this.mousemove);
   //   YAHOO.util.Event.removeListener(this.eventDiv, "mouseout", this.mouseout);    
   //   YAHOO.util.Event.removeListener(this.eventDiv, "mousewheel", this.mousewheel);    
   //   YAHOO.util.Event.removeListener(window,   "DOMMouseScroll", this.mousewheel);
   //   YAHOO.util.Event.removeListener(window,   "mousewheel", this.mousewheel);
   //   YAHOO.util.Event.removeListener(document, "mousewheel", this.mousewheel);
   },
   initialize : function()
   {
    this.affineParams =  new OmarAffineParams();
    this.affineM = new OmarMatrix3x3();
   },
   setup : function(containerDiv, mapObj, annDiv, topDiv){
    this.map           = mapObj;
    this.eventDiv      = this.getDivElement(topDiv);
    this.annotationDiv = this.getDivElement(annDiv);
    this.containerDiv  = this.getDivElement(containerDiv);
    this.containerDivRegion = YAHOO.util.Region.getRegion(this.containerDiv);
    this.vectorLayer = new OpenLayers.Layer.Vector();
    this.map.addLayer(this.vectorLayer);
    this.events = new OpenLayers.Events(this, this.eventDiv, this.EVENT_TYPES, true);

    this.drawControls = {
                    point: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        OpenLayers.Handler.Point),
                    line: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        OpenLayers.Handler.Path),
                    polygon: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        OpenLayers.Handler.Polygon)//,
                 };
    this.map.addControl(this.drawControls.line);
    this.map.addControl(this.drawControls.polygon);
    if(this.containerDiv)
    {
      if(!this.annotationDiv)
      {
        this.annotationDiv = document.createElement("div");
        this.annotationDiv.setAttribute('id', 'hudDiv');
        this.annotationDiv.style.position='absolute';
        this.annotationDiv.style.zIndex = '5';
        this.containerDiv.insertBefore(this.annotationDiv, this.map.div);
      }
      if(!this.eventDiv)
      {
        this.eventDiv = document.createElement("div");     
        this.eventDiv.setAttribute('id', 'eventDiv');
        this.eventDiv.style.backgroundColor ='#fff';
        this.eventDiv.style.opacity=0;
        this.eventDiv.style.filter='alpha(opacity=1)';
        this.eventDiv.style.position='absolute';
        this.eventDiv.style.zIndex= '6';
        this.containerDiv.insertBefore(this.eventDiv, this.annotationDiv);
      }
    }
    this.events.on ({
        "click":this.click,
        "dblclick":this.dblClick,
        "mousedown":this.mousedown,
        "mouseup":this.mouseup,
        "mousemove": this.mousemove,
        "mouseout": this.mouseout,
        "wheel": this.wheel,
        "scope":this
    });
    this.wheelListener = OpenLayers.Function.bindAsEventListener(this.wheel, this);

    OpenLayers.Event.observe(window,   "DOMMouseScroll", this.wheelListener);
    OpenLayers.Event.observe(window,   "mousewheel",     this.wheelListener);
    OpenLayers.Event.observe(document, "mousewheel",     this.wheelListener);
    this.containerResized();
  },
   setToolMode: function(mode)
   {
        var stateChangedFlag = (mode != this.toolMode);

        // need to add clearing the state information when changing states.
        this.toolMode = mode;

        if(stateChangedFlag)
        {
            this.toolModeChanged();
        }
   },
   toolModeChanged: function()
   {
        if(this.zoomBox)
        {
            this.zoomBoxEnd();
        }
        if(this.documentListenersAdded)
        {
            YAHOO.util.Event.removeListener(document, "mousemove", this.mousemove);
            YAHOO.util.Event.removeListener(document, "mouseUp", this.mouseup);
            this.documentListenersAdded = false;
        }
        if(this.currentDrawControl) this.currentDrawControl.deactivate();
        this.currentDrawControl = null;

        switch(this.toolMode)
        {
            case OMAR.ToolModeType.LINE:
            {
                this.vectorLayer.destroyFeatures();
                this.currentDrawControl = this.drawControls.line;
                this.currentDrawControl.activate();
                break;
            }
            case OMAR.ToolModeType.POLYGON:
            {
                this.currentDrawControl = this.drawControls.polygon;
                this.currentDrawControl.activate();
                this.vectorLayer.destroyFeatures();
                break;
            }
        }
    },
   adaptOpenLayersXY : function(evt, pt){

    evt.xy={x:pt.x,y:pt.y};
    evt.xy.equals = function(xy){
        return ((this.x == xy.x)&&(this.y == xy.y));
    }
    return evt;
   },
  getAffineParams : function(){
    return this.affineParams;
  },
  applyRotate : function(rot){
    this.affineParams.rotate = rot;
    this.updateTransform();
    this.transformDiv();
  },
  regionsEqual : function(r1, r2){
    return ((r1.top == r2.top)&&
            (r1.right == r2.right)&&
            (r1.bottom==r2.bottom)&&
            (r1.left==r2.left)&&
            (r1.width==r2.width) &&
            (r1.height==r2.height));
  },
  setChildDivDimensions: function(div){
      var wRad = 0.0;//Math.round(this.containerDivRegion.width*0.5*Math.sqrt(2));
      var hRad = 0.0;//Math.round(this.containerDivRegion.height*0.5*Math.sqrt(2));

      var newW = this.containerDivRegion.width+wRad*2; //Math.round(max*1.5);
      var newH = this.containerDivRegion.height+hRad*2; //Math.round(max*1.5);

      // now center about container
      var shiftLeft = -Math.round(wRad);//Math.round((containerCenterX-centerX)/2);
      var shiftTop  = -Math.round(hRad);//Math.round((containerCenterY-centerY)/2);
      
      div.style.left   = shiftLeft + "px";
      div.style.top    = shiftTop + "px";
      div.style.width  = newW+"px";
      div.style.height = newH+"px";

     },
  containerResized: function(){
    var center = this.map.getCenter();
      var region = YAHOO.util.Region.getRegion(this.containerDiv);
      this.containerDivRegion = region;
      //alert(region);
      this.setChildDivDimensions(this.map.div);
      this.setChildDivDimensions(this.eventDiv);
      this.setChildDivDimensions(this.annotationDiv);

      this.updateTransform();
      this.map.updateSize();
      this.map.setCenter(center, this.map.getZoom());
  },  
  checkResize: function(){
    var region = YAHOO.util.Region.getRegion(this.containerDiv);
    if(!this.regionsEqual(region,this.containerDivRegion))
    {
        this.containerResized();
     }
  },
  updateTransform: function(){
    if(!this.annotationDiv) return;
      var region = YAHOO.util.Region.getRegion(this.annotationDiv);
      var centerPx = new OmarPoint((region.width)/2 , (region.height)/2 );
      this.affineParams.pivot.x = centerPx.x;
      this.affineParams.pivot.y = centerPx.y;
      this.affineParams.scale.x = 1;
      this.affineParams.scale.y = 1;
      this.affineParams.translate.x = -region.left;
      this.affineParams.translate.y = -region.top;

      this.affineM = this.affineParams.toMatrix();
 
  },
  generateOssimFullImageTransform: function(){
    var affine =  new OmarAffineParams();
    var res = OMAR.imageManipulator.map.getResolution();
    var scale = 1.0/res;
 
    var center =  this.getCenterLocal();
    affine.rotate  = -this.affineParams.rotate;
    affine.scale.x = scale;
    affine.scale.y = scale;
    affine.pivot.x = center.x;
    affine.pivot.y = center.y;
    return affine.toMatrix();
  },
  transformDiv: function(){
      if(this.map && this.map.div)
      {
        cssSandpaper.setTransform(this.map.div,
                                  "rotate(" + this.affineParams.rotate+"deg)");

		cssSandpaper.setTransform(hudChildDivId,
		 						  "rotate(" + this.affineParams.rotate+"deg)");
      }
  },
  getDivElement: function(div){
      if(typeof div == "string")
      {
        return document.getElementById(div);
      }
      return div;
  },
  mouseToPoint: function(evt){
      var mouseXY = YAHOO.util.Event.getXY(evt);
      return new OmarPoint(mouseXY[0], mouseXY[1]);
  },
  annotationPointToPoint: function(pt){
    var region = YAHOO.util.Region.getRegion(this.annotationDiv);
    return {x:(pt.x + region.left), y:(pt.y + region.top)};
  },
  pointToTransformPoint: function(pt){
    return this.affineM.transform(pt);
  },
  pointToMap: function(pt){
    var endPt     = this.pointToTransformPoint(pt);
    return this.map.getLonLatFromViewPortPx(endPt);
  },
  pointReflect: function(pt){
      var result = new OmarPoint(pt.x, pt.y);
      if(this.projectionType == OMAR.ProjectionType.PIXEL)
      {
        if(this.map.maxExtent.top > this.map.maxExtent.bottom)
        {
            //reflect
            result.y = this.map.maxExtent.top - result.y;
        }
      }
      return result;
  },
  pointToLocal: function(pt){
      var mapPt = this.pointToMap(pt);
      return this.pointReflect(new OmarPoint(mapPt.lon, mapPt.lat));

   },
   getCenterLocal: function()
   {
     var mapPt = this.map.getCenter();
     return this.pointReflect(new OmarPoint(mapPt.lon, mapPt.lat));
   },
   withinDiv: function(xy, el){
    var region = YAHOO.util.Dom.getRegion(el);
    var top    = region.top;
    var left   = region.left;
    var bottom = region.bottom;
    var right  = region.right;
    var mX     = xy.x;
    var mY     = xy.y;
    
    return (mX > left && mX < right && mY > top && mY < bottom);
  },
  wheel: function(evt){
    if(!this.withinDiv(this.mouseToPoint(evt), this.eventDiv.parentNode))
    {
      return;
    }
    if (evt.wheelDelta) 
    {
      delta = evt.wheelDelta / 120;
      if (window.opera && window.opera.version() < 9.2) {
        delta = -delta;
      }
    } 
    else if (evt.detail) 
    {
      delta = -evt.detail / 3;
    }
    if (delta) 
    {
      if (delta < 0) 
      {
        this.wheelDown(evt);
      } 
      else 
      {
        this.wheelUp(evt);
      }
    }
    YAHOO.util.Event.stopEvent(evt);
  },
  wheelUp: function(evt){
      if (this.map.getZoom() < this.map.getNumZoomLevels()) 
      {
         this.map.setCenter(this.map.getCenter(), this.map.getZoom() + 1);
      }
  },
  wheelDown: function(evt){
      if (this.map.getZoom() > 0) 
      {
         this.map.setCenter(this.map.getCenter(), this.map.getZoom() - 1);
      }
  },
  click: function(evt)
  {
    
  },
  dblClick: function(evt){

     switch(this.toolMode)
     {
        case OMAR.ToolModeType.PAN_ZOOM:
        case OMAR.ToolModeType.ZOOM_BOX:
        {
             var endPt     = this.affineM.transform(this.mouseToPoint(evt));
             var newCenter = this.map.getLonLatFromViewPortPx(endPt);

             this.map.setCenter(newCenter, this.map.zoom + 1);
             OpenLayers.Event.stop(evt);  
             break;
        }
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        {
            var wasDrawing = this.currentDrawControl.handler.drawing;
            this.currentDrawControl.handler.dblclick(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
            if(wasDrawing&&!this.currentDrawControl.handler.drawing)
            {
                this.events.triggerEvent("measureAddPointFinished",{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
            }
            OpenLayers.Event.stop(evt);  
           break;
        }
     }
   },
   mouseout: function(evt){
    /*
      if (this.mouseDragStart != null && OpenLayers.Util.mouseLeft(evt, this.map.div))
      {
         if (this.zoomBox) 
         {
            this.removeZoomBox();
         }
         this.mouseDragStart = null;
      }
      */
   },
   mousedown: function(evt){
      this.mousePosition = this.mouseToPoint(evt);
      this.editMode = false;

      switch(this.toolMode)
      {
        case OMAR.ToolModeType.PAN_ZOOM:
        case OMAR.ToolModeType.ZOOM_BOX:
        {
            this.documentListenersAdded = false;
            this.mouseDragStart = null;
            this.performedDrag = false;
            if (OMAR.ToolModeType.PAN_ZOOM&&(!OpenLayers.Event.isLeftClick(evt))) {
             return;
            }
            /** This simulates a capture mouse events from the entire document window so we
            * can continue dragging even if the mouse goes outside the DIV.
            *
            * We may want a utility method for this.
            */

            this.mouseDragStart = this.mouseToPoint(evt);
            if (evt.shiftKey||(this.toolMode==OMAR.ToolModeType.ZOOM_BOX)) 
            {
                var region = YAHOO.util.Region.getRegion(this.annotationDiv);
                this.eventDiv.style.cursor = "crosshair";
                this.zoomBox = OpenLayers.Util.createDiv('zoomBox', 
                                                         new OmarPoint(-region.left +this.mouseDragStart.x,
                                                                       -region.top +this.mouseDragStart.y), 
                                                         null, null, "absolute", "2px solid red");
                this.zoomBox.id="zoomBox";
                this.zoomBox.style.backgroundColor = "white";
                this.zoomBox.style.filter = "alpha(opacity=50)";
                this.zoomBox.style.opacity = "0.50";
                this.zoomBox.style.fontSize = "1px";
                this.zoomBox.style.zIndex = this.map.Z_INDEX_BASE["Popup"] - 1;

                if(this.annotationDiv!=null) this.annotationDiv.appendChild(this.zoomBox);
                OpenLayers.Event.stop(evt);  
            }
            else
            {
                this.map.div.style.cursor = "move";
            }
            document.onselectstart = OpenLayers.Function.False;
            break;
        }
        case OMAR.ToolModeType.BOX_AOI:
        {
           this.mouseDragStart = this.mouseToPoint(evt);
           if(!evt.shiftKey)
           {
                this.removeSelectionBox();
                if(!this.selectionBox)
                {

                    var region = YAHOO.util.Region.getRegion(this.annotationDiv);
                    this.selectionBox = OpenLayers.Util.createDiv('selectionBox', 
                                                             new OmarPoint(-region.left +this.mouseDragStart.x,
                                                                           -region.top +this.mouseDragStart.y), 
                                                             null, null, "absolute", "");
                    this.selectionBox.id="selectionBox";
                    this.selectionBox.style.backgroundColor = "orange";
                    this.selectionBox.style.filter = "alpha(opacity=50)";
                    this.selectionBox.style.opacity = "0.50";
                    this.selectionBox.style.fontSize = "1px";
                    this.selectionBox.style.zIndex = this.map.Z_INDEX_BASE["Popup"] - 1;

                    if(this.annotationDiv!=null) this.annotationDiv.appendChild(this.selectionBox);
                }
            }
            else
            {
                if(this.withinDiv(this.mousePosition, this.selectionBox))
                {
                    this.editMode = true;
                }
            }
 
            OpenLayers.Event.stop(evt);  
            document.onselectstart = OpenLayers.Function.False;
         break;
        }
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        {
            if(!this.currentDrawControl.handler.drawing)
                 this.vectorLayer.destroyFeatures();

            this.currentDrawControl.handler.mousedown(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
            OpenLayers.Event.stop(evt); 
            document.onselectstart = OpenLayers.Function.False;
           break;
        }
      }
      if(this.withinDiv(this.mouseToPoint(evt), this.eventDiv))
      {
          this.documentListenersAdded = true;
          YAHOO.util.Event.addListener(document, "mousemove", this.mousemove, null, this);
          YAHOO.util.Event.addListener(document, "mouseup", this.mouseup, null, this);
      }
    },
    mouseup: function(evt){
       this.editMode = false;
       if(this.documentListenersAdded)
       {
           YAHOO.util.Event.removeListener(document, "mousemove", this.mousemove);
           YAHOO.util.Event.removeListener(document, "mouseup", this.mouseup);
           this.documentListenersAdded = false;
       }
       switch(this.toolMode)
       {
        case OMAR.ToolModeType.BOX_AOI:
        {
           if(this.selectionBox&&this.mouseDragStart)
           {
              OpenLayers.Event.stop(evt);  
           }
           document.onselectstart = null;
           this.mouseDragStart = null;
           this.map.div.style.cursor = ""; 

           break; 
        } 
        case OMAR.ToolModeType.PAN_ZOOM:
        case OMAR.ToolModeType.ZOOM_BOX:
        {

          if (OMAR.ToolModeType.PAN_ZOOM&&(!OpenLayers.Event.isLeftClick(evt))) {
             return;
          }
          if (this.zoomBox) 
          {
            this.zoomBoxEnd(evt);
            OpenLayers.Event.stop(evt);  
            this.eventDiv.style.cursor = "";
          } 
          else 
          {
             if (this.performedDrag) 
             {
                this.map.setCenter(this.map.center);
                OpenLayers.Event.stop(evt);  
             }
          }
          document.onselectstart = null;
          this.mouseDragStart = null;
          this.map.div.style.cursor = "";  
          break;
        }
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        {
           if(this.currentDrawControl.handler.drawing)
           {
                this.currentDrawControl.handler.mouseup(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
                OpenLayers.Event.stop(evt); 

                // check for triggering finished 
                if(!this.currentDrawControl.handler.drawing)
                {
                 this.events.triggerEvent("measureAddPointFinished",{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
                }
           }
           document.onselectstart = null;
           break;
        }

    }
  },
  mousemove: function(evt){
     var updateBox = function(evt, box, scopePtr, translateOnly) {

            var region = YAHOO.util.Region.getRegion(scopePtr.annotationDiv);
            var deltaX = (scopePtr.mousePosition.x - scopePtr.mouseDragStart.x);
            var deltaY = (scopePtr.mousePosition.y - scopePtr.mouseDragStart.y);
            if(translateOnly)
            {
                var left = parseInt(box.style.left);
                var top  = parseInt(box.style.top);
                left += deltaX;
                top  += deltaY;

                box.style.left = left + "px";
                box.style.top  = top  + "px";
            }
            else
            {
                deltaX = Math.abs(deltaX);
                deltaY = Math.abs(deltaY);
                 var w = Math.max(1, deltaX);
                var h = Math.max(1, deltaY);
                box.style.width  = w + "px";
                box.style.height = h + "px";

                var x =  scopePtr.mouseDragStart.x;
                var y =  scopePtr.mouseDragStart.y;
                if(x > scopePtr.mousePosition.x) x = scopePtr.mousePosition.x;
                if(y > scopePtr.mousePosition.y) y = scopePtr.mousePosition.y;

                box.style.left = (-region.left + x) + "px";
                box.style.top  = (-region.top + y) + "px";
            }
        };
     this.mousePosition = this.mouseToPoint(evt);
     switch(this.toolMode)
      {
        case OMAR.ToolModeType.BOX_AOI:
        {
           if (this.mouseDragStart != null) 
           {
             if(this.selectionBox)
             {
                if(this.editMode)
                {
                    updateBox(evt, this.selectionBox, this, true);
                }
                else if(!evt.shiftKey)
                {
                    updateBox(evt, this.selectionBox, this, false);
                }
            }
            OpenLayers.Event.stop(evt);  
           }
           break;
        }
        case OMAR.ToolModeType.PAN_ZOOM:
        case OMAR.ToolModeType.ZOOM_BOX:
        {
          if (this.mouseDragStart != null) 
          {

             //var region = YAHOO.util.Region.getRegion(this.annotationDiv);
             if (this.zoomBox) 
             {
                updateBox(evt, this.zoomBox, this);
                OpenLayers.Event.stop(evt);  
             } 
             else if(this.mouseDragStart)
             {
                var startPt   = this.affineM.transform(this.mouseDragStart);
                var endPt     = this.affineM.transform(this.mousePosition);
                var deltaPt   = startPt.sub(endPt);
                var size      = this.map.getSize();
                var newXY     = new OpenLayers.Pixel(size.w / 2 + deltaPt.x, size.h / 2 + deltaPt.y);
                var newCenter = this.map.getLonLatFromViewPortPx(newXY);
                this.map.setCenter(newCenter, null, true);
                this.mouseDragStart       = this.mouseToPoint(evt);
                this.map.div.style.cursor = "move";
                 OpenLayers.Event.stop(evt);  
            }
             this.performedDrag = true;
          }
          break;
       }
      case OMAR.ToolModeType.LINE:
      case OMAR.ToolModeType.POLYGON:
      {
        if(this.currentDrawControl.handler.drawing)
        {
            this.currentDrawControl.handler.mousemove(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
            OpenLayers.Event.stop(evt);  
        }  
        break;
     }
    }
    if(this.editMode)
    {
        this.mouseDragStart = this.mouseToPoint(evt);
    }
   },
   zoomBoxEnd:function(evt){
      var currentPoint = this.mouseToPoint(evt);
      if (this.mouseDragStart != null) {
         if (Math.abs(this.mouseDragStart.x - currentPoint.x) > 5 || 
                      Math.abs(this.mouseDragStart.y - currentPoint.y) > 5) {
            
            var startPt    =  this.affineM.transform(this.mouseDragStart);
            var endPt      =  this.affineM.transform(this.mouseToPoint(evt));
            var start              = this.map.getLonLatFromViewPortPx(startPt);
            var end                = this.map.getLonLatFromViewPortPx(endPt);
            var top = Math.max(start.lat, end.lat);
            var bottom = Math.min(start.lat, end.lat);
            var left = Math.min(start.lon, end.lon);
            var right = Math.max(start.lon, end.lon);
            var bounds = new OpenLayers.Bounds(left, bottom, right, top);

            this.map.zoomToExtent(bounds);
         } 
         else 
         {
            var end = this.map.getLonLatFromViewPortPx(this.pointToTransformPoint(this.mouseToPoint(evt)));
            this.map.setCenter(new OpenLayers.LonLat((end.lon), (end.lat)), this.map.getZoom());
         }
         this.removeZoomBox();
      }
   },
   removeSelectionBox: function(){
    if(this.selectionBox)
    {
        this.annotationDiv.removeChild(this.selectionBox);
        this.selectionBox = null;
    }
   },
   removeZoomBox: function(){
      this.annotationDiv.removeChild(this.zoomBox);
      this.zoomBox = null;
   },
   CLASS_NAME: "OMAR.OpenLayersImageManipulator"
});

            var northAngle;
            var upIsUpRotation;
            var brightnessSlider;
            var compassImage;
            var compassMap;
            var compassVectorLayer;
            var contrastSlider;
            var currentMapCenterX;
            var currentMapCenterY;
            var format;
            var image;
            var imageBounds;
            var imageURL;
            var initFlag;
            var layer;
            var map;
            var omarImageSpaceOpenLayersParams;
            var resLevels;
            var rotateSlider;
            var rotationAngle;
            var zoomInButton;

        function resetRotate()
        {
           rotateSlider.setRealValue(0.0);
        }
        function rotateUpIsUp()
        {
           rotateSlider.setRealValue(upIsUpRotation);
        }
        function rotateNorthUp()
        {
           rotateSlider.setRealValue(northAngle);
        }
            function changeBandsOpts()
            {
                var bands = $("bands").value;
                layer.mergeNewParams({bands:bands});

               // updateImage();
            }

            function changeHistoOpts()
            {
                var stretch_mode = $("stretch_mode").value;
                var stretch_mode_region = $("stretch_mode_region").value;
                layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
                //updateImage(); ////////////////////
            }

            function changeMapSize( mapWidth, mapHeight )
            {
                if(mapWidth && mapHeight)
                {
                    var Dom = YAHOO.util.Dom;
                    var mapDiv = Dom.get( "map" );
                    if(mapDiv)
                    {
                        mapDiv.style.width  = mapWidth + "px";
                        mapDiv.style.height = mapHeight + "px";
                    }
                }
                map.updateSize();
            }

            function changeSharpenOpts()
            {
                var sharpen_mode = $("sharpen_mode").value;
                layer.mergeNewParams({sharpen_mode:sharpen_mode});

                //updateImage();
            }

            function changeToSingleLayer()
            {
                var url = "${createLink(controller: 'mapView', action: 'index')}";
                var wmsFormElement = $("wmsFormId");
                if(wmsFormElement)
                {
                    var imageAdjustmentParams = new OmarWmsParams();
                    imageAdjustmentParams.setProperties(document);
                    imageAdjustmentParams.layers = "${rasterEntry.indexId}";
                    wmsFormElement.action = url + "?"+imageAdjustmentParams.toUrlParams();
                    wmsFormElement.method = "POST";
                    wmsFormElement.submit();
                }
            }

            function chgInterpolation()
            {
	            var interpolation = $("interpolation").value;
	            layer.mergeNewParams({interpolation:interpolation});

	            //updateImage();
            }

            function chipImage(format)
            {
                var res = OMAR.imageManipulator.map.getResolution();
                var scale = 1.0/res;
                var url = "${createLink(controller: 'imageSpace', action: 'getTile')}";
                var x = null;
                var y = null;
                var w = null;
                var h = null;
                var pivot = null;
                var affineM      = OMAR.imageManipulator.generateOssimFullImageTransform();


                if(OMAR.imageManipulator.selectionBox)
                {
                    x = parseInt(OMAR.imageManipulator.selectionBox.style.left);
                    y = parseInt(OMAR.imageManipulator.selectionBox.style.top);
                    w = parseInt(OMAR.imageManipulator.selectionBox.style.width);
                    h = parseInt(OMAR.imageManipulator.selectionBox.style.height);
                    var centerChip = OMAR.imageManipulator.pointToLocal(OMAR.imageManipulator.annotationPointToPoint({x:x+w/2,y:y+h/2}));
                    var center =  OMAR.imageManipulator.getCenterLocal();
                    pivot = Math.round(center.x) + "," + Math.round(center.y);
 
                    var centerView = affineM.transform(centerChip);

                    x = Math.round(centerView.x-w/2);
                    y = Math.round(centerView.y-h/2);
                }
                else
                {
                    // we will chip the viewport only
                    //
                    var w = Math.abs(OMAR.imageManipulator.containerDivRegion.right - OMAR.imageManipulator.containerDivRegion.left) + 1;
                    var h = Math.abs(OMAR.imageManipulator.containerDivRegion.top - OMAR.imageManipulator.containerDivRegion.bottom) + 1;
                    var center =  OMAR.imageManipulator.getCenterLocal();
                    pivot = Math.round(center.x) + "," + Math.round(center.y);
                    var centerView = affineM.transform(center);
                    x = Math.round(centerView.x-w/2);
                    y = Math.round(centerView.y-h/2);
               }

                //var size = bounds.getSize();

                var z = this.map.getZoom();
                var params = new OmarImageSpaceGetTileParams();

                params.setProperties(document);
                params.setProperties(
                {
                                    'x': x,
                                    'y': y,
                                    'scale':scale,
                                    'rotate': -parseFloat(${"rotateAngle"}.value),
                                    'width':w,
                                    'height':h,
                                    'format':"image/"+format,
                                    'id' : "${rasterEntry?.id}",
                                    'pivot' : pivot
                });

                 document.location.href = "${createLink(controller: 'imageSpace', action: 'getTile')}" + "?" + params.toUrlParams();
            }

            function get_my_url (bounds)
            {
                var width  = parseFloat("${rasterEntry.width}");
                var height = parseFloat("${rasterEntry.height}");
                var res = this.map.getResolution();
                var scale = 1.0/res;
                var size = bounds.getSize();
                var x = ((map.getCenter().lon - 
                          size.w/2.0)*scale);
                var y = ((height-map.getCenter().lat - 
                          size.h/2.0)*scale);

                var z = this.map.getZoom();
                var params = new OmarImageSpaceGetTileParams();

                params.setProperties(document);
                params.setProperties(
                {
                                    'x': x,
                                    'y': y,
                                    'scale':scale,
                                    'width':this.tileSize.w,
                                    'height':this.tileSize.h,
                                    'id' : "${rasterEntry?.id}"
                });

                var path = "?"+params.toUrlParams();
                var url = this.url;

                if (url instanceof Array) { url = this.selectUrl(path, url); }

                return url + path;
            }

            function init(mapWidth, mapHeight)
            {
                OMAR.imageManipulator = new OMAR.OpenLayersImageManipulator();
                OMAR.imageManipulator.click = function(evt)
                {
                   getCoordinates(OMAR.imageManipulator.pointToLocal(this.mouseToPoint(evt)));
                }
               northAngle = parseFloat("${rasterEntry.azimuthAngle}");
                upIsUpRotation   =  parseFloat("${upIsUpRotation}");
                brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
                contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
                omarImageSpaceOpenLayersParams = new  OmarImageSpaceOpenLayersParams();
                format = "image/jpeg";
                resLevels = parseFloat("${rasterEntry.numberOfResLevels}");
                initFlag = 1;
                rotateSlider = YAHOO.widget.Slider.getHorizSlider("slider-rotate-bg",  "slider-rotate-thumb", 0, 180, 1);
                rotationAngle = ${"rotateAngle"}.value;

                OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";

                var width  = parseFloat("${rasterEntry.width}");
                var height = parseFloat("${rasterEntry.height}");
                //var url = "${createLink(controller: 'imageSpace', action: 'getTileOpenLayers')}";
                var url = "${createLink(controller: 'imageSpace', action: 'getTile')}";
                var bounds = new OpenLayers.Bounds(0, 0, width, height);
                map = new OpenLayers.Map("map", { controls:[], theme: null, maxExtent:bounds, maxResolution: 16, numZoomLevels:(resLevels+5) });
                var options = {
                     controls: [],
                     maxExtent: bounds,
                     getURL: get_my_url,
                     isBaseLayer: true,
                     maxResolution: (width) / map.getTileSize().w,
                     transitionEffect: "resize",
                     units:'pixel',
                     singleTile:true,
                     format: format
                 };
 
	            var oMenu = new YAHOO.widget.MenuBar("rasterMenu",
	            {
	                autosubmenudisplay: true,
                    showdelay: 0,
                    hidedelay: 750,
                    lazyload: true
                });
	            oMenu.render();


             //map.events.register('zoomend', null, theMapHasZoomed);
             //map.events.register("moveend", null, theMapHasMoved);

               layer = new OpenLayers.Layer.TMS( "Image Space Viewer", url, options);
              layer.getURL = get_my_url;
               layer.singleTile = true;
                map.addLayer(layer);
                //map.addControl(new OpenLayers.Control.MouseDefaults());
                map.addControl(new OpenLayers.Control.KeyboardDefaults());
                map.setBaseLayer(layer);
                //changeMapSize(mapWidth, mapHeight);
               map.zoomToMaxExtent();


                setupToolbar();
                /*
                var isiPad = navigator.userAgent.match( /iPad/i ) != null;
                if ( isiPad )
                {
                    this.touchhandler = new TouchHandler( map, 4 );
                }
                */

                omarImageSpaceOpenLayersParams.setProperties(document);

                brightnessSlider.animate = false;
	            brightnessSlider.getRealValue = function() { return ((this.getValue() - 50)/50.0); }
                brightnessSlider.setRealValue = function(value) { this.setValue((value + 1) * 50); }
                brightnessSlider.subscribe("change", function(offsetFromStart)
                {
    	            $("brightness").value = this.getRealValue();
    	            $("brightnessTextField").value = this.getRealValue();
                });
                brightnessSlider.subscribe("slideEnd", function()
                {
		            if(layer)
		            {
			            layer.mergeNewParams({brightness:this.getRealValue()});
                        //updateImage();
		            }
                });
                brightnessSlider.setRealValue(${params.brightness ?: 0});

                contrastSlider.getRealValue = function()
                {
                    var value = (this.getValue()/100.0)*2.0;
                    return value;
                }
                contrastSlider.setRealValue = function(value) { this.setValue(value*50); }
	            contrastSlider.subscribe("change", function(offsetFromStart)
	            {
    	            $("contrast").value = this.getRealValue();
    	            $("contrastTextField").value = this.getRealValue();
                });
	            contrastSlider.subscribe("slideEnd", function()
	            {
		            if(layer)
		            {
			            layer.mergeNewParams({contrast:this.getRealValue()});
                        //updateImage();
		            }
                });
	            contrastSlider.setRealValue(${params.contrast ?: 1});

	            rotateSlider.animate = false;
	            rotateSlider.getRealValue = function() { return this.getValue() * 2; }
                rotateSlider.setRealValue = function(value) { this.setValue(Math.ceil((value%360.0) / 2)); }
                 rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });
                rotateSlider.setRealValue(rotationAngle);

	            // set the initialization flag so the moveend and zoomend code can execute
	            initFlag = 0;

                var offsetX = width/2.0;
                var offsetY = height/2.0;
                map.setCenter(new  OpenLayers.LonLat(0.0,0.0), 0);
                map.zoomToMaxExtent();
                // map.zoomIn();
                // initialize the zoom level variable used to determine zoom in and out in the MapHasZoomed ////////////////////

               OMAR.imageManipulator.affineParams.rotate = parseFloat(${"rotateAngle"}.value);

               OMAR.imageManipulator.setup("center2", map, "hudDivId", "eventDivId");
               // add these just in case there were settings passed to the GSP 
               // but we only want to apply them once the page is finished with setup
               OMAR.imageManipulator.updateTransform(); 
               OMAR.imageManipulator.transformDiv();
               // OMAR.iamgeManipulator.setToolMode(OMAR.ToolModeType.PAN_ZOOM);
               //alert(map.getMaxExtents());

               OMAR.imageManipulator.events.on({
                        "measureAddPointFinished": measureFinished
                });
           }

            function measureFinished(evt){
                if(evt&&evt.feature)
                {
                    var url = "/omar/imageSpace/measure"; 
                    var request = OpenLayers.Request.POST({
                         url: url,
                         //params: {id:${rasterEntry.id}, feature:jsonText.toString()},
                         data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id}, 
                                                          feature:{wkt:evt.feature.geometry.toString()}
                                                         }),
                         callback: function (transport){
                            var temp = YAHOO.lang.JSON.parse(transport.responseText);
                            alert(transport.responseText);
                        }
                    });

                }
            }
            function resetBrightnessContrast()
            {
              brightnessSlider.setRealValue(0);
              contrastSlider.setRealValue(1.0);

                //updateImage();
            }

            function rotateTextFieldChange(angle)
            {
                sliderRotate(angle);
                rotateSlider.unsubscribe("change");
                rotateSlider.setRealValue(angle);
                rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });
                OMAR.imageManipulator.applyRotate(angle);
            }

            function setupCompassMap()
            {

                compassMap = new OpenLayers.Map('compassMap', {controls: new OpenLayers.Control.Navigation({autoActivate: false}), theme: null});

              var baseLayer = new OpenLayers.Layer("Empty", {isBaseLayer: true});
              compassMap.addLayer(baseLayer);


              compassMap.setCenter(new OpenLayers.LonLat(0,0), 0);


              var compassImageURL = "${resource(plugin: 'omar', dir: 'images', file: 'north_arrow.png')}";

                // define a vector layer to add markers to
	            compassVectorLayer = new OpenLayers.Layer.Vector("Compass Layer",
	            {
		            styleMap: new OpenLayers.StyleMap
		            ({
			            "default":
			            {
				            externalGraphic : compassImageURL,
				            graphicWidth : 40,
                            graphicHeight : 40,
			                rotation : <%=' "${angle}" '%>
            }
})
});

compassMap.addLayer(compassVectorLayer);



// define the marker for the image to sit on
//compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0,0), {angle: -northAngle});
//compassVectorLayer.addFeatures([compassImage]);
}

function setupToolbar()
{

 //  var panButton = new OpenLayers.Control.MouseDefaults({title:'Click pan button to activate. Once activated click the map and drag the mouse to pan.'});
   
    panButton = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                id: "PAN",
                                                trigger: panMode,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonPan"});

   var selectAoi = new OpenLayers.Control.Button({title: "Click to select an AOI with an upright rectangle.", 
                                                   id: "SELECT_AOI",
                                                trigger: selectAoiClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonSelectAOI"});

  var deleteAoi = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                   id: "DELETE_AOI",
                                                trigger: deleteAoiClicked,
                                                type:OpenLayers.Control.TYPE_BUTTON,
                                                displayClass: "olControlButtonDeleteAOI"});

    var pointButton = new OpenLayers.Control.Button({title: "Click to drop a point", 
                                                   id: "POINT_BUTTON",
                                                trigger: pointModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasurePoint"});
  
    var measurePathButton = new OpenLayers.Control.Button({title: "Click to measure a path", 
                                                   id: "MEASURE_PATH",
                                                trigger: measurePathModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasurePath"});
    var measureAreaButton = new OpenLayers.Control.Button({title: "Click to measure a path", 
                                                    id: "MEASURE_AREA",
                                               trigger: measureAreaModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasureArea"});

    var zoomBoxButton = new OpenLayers.Control.ZoomBox({title:"Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into.",id:"ZOOM_BOX", trigger:zoomBoxClicked});
    zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                 id:"ZOOM_IN",
                                                 displayClass: "olControlZoomIn", 
                                                 trigger: zoomIn});
    var zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.", displayClass: "olControlZoomToLayer", id:"ZOOM_IN_FULL", trigger: zoomInFullRes});
    var zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: zoomOut,
                                                      id:"ZOOM_OUT"});
    var container = $("toolBar");

    var panel = new OpenLayers.Control.Panel(
    {
       div: container,
       defaultControl: panButton,
      'displayClass': 'olControlPanel',
      activateControl: controlActivated
      });

    panel.addControls(
    [
      panButton,
      zoomBoxButton,
      zoomInButton,
      zoomOutButton,
      zoomInFullResButton,
      new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent."}),
      selectAoi,
      deleteAoi,
      pointButton,
      measurePathButton,
      measureAreaButton
    ]);
    map.addControl(panel);
}

function sliderRotate(sliderValue)
{
            rotationAngle = 360 - parseInt(sliderValue)
            ${"rotateAngle"}.value = sliderValue;
            OMAR.imageManipulator.applyRotate(sliderValue);
}

            function theMapHasMoved()
            {
                if (initFlag == 0) { 
                    //updateImage(); 
                }
            }

            function theMapHasZoomed()
            {
                if (initFlag == 0)
                {
	            }
            }

            function zoomIn()
            {
	            map.zoomIn();
	            if(map.getZoom() >= map.getZoomForResolution(1.0, true))
	            {
		            zoomInButton.displayClass = "olControlFoo";
	            }
            }
            function zoomBoxClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.ZOOM_BOX);
            }
            function panMode()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.PAN_ZOOM);
            }
            function measurePathModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.LINE);
            }
            function measureAreaModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.POLYGON);
            }
            function pointModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.POINT);
            }
            function controlActivated(control)
            {
               if (!this.active) {
                     return false;
               }
               if (control.type == OpenLayers.Control.TYPE_BUTTON) 
               {
                 if(control.trigger) control.trigger();
                 this.redraw();
                 return;
               }
               if (control.type == OpenLayers.Control.TYPE_TOGGLE) 
               {
                 if (control.active) {
                    control.deactivate();
                 } else {
                    control.activate();
                 }
                 this.redraw();
                 return;
               }
               for (var i = 0, len = this.controls.length; i < len; i++) {
                 if (this.controls[i] != control) 
                 {
                    if (this.controls[i].type != OpenLayers.Control.TYPE_TOGGLE) 
                    {
                       this.controls[i].deactivate();
                    }
                 }
              }
              control.activate();
              if(control.trigger) control.trigger();
            }

            function deleteAoiClicked()
            {
                OMAR.imageManipulator.removeSelectionBox();
            }
            function selectAoiClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.BOX_AOI);
            }
            function zoomInFullRes()
            {
                // we are image space so set to a 1:1 scale
                var zoom = map.getZoomForResolution(1.0, true)
                map.zoomTo(zoom)
	            zoomInButton.displayClass = "olControlFoo";
            }

            function zoomOut()
            {
                map.zoomOut();
	              if(map.getZoom() < map.getZoomForResolution(1.0, true)) { zoomInButton.displayClass = "olControlZoomIn"; }
            }

			function rotateCompass()
			{
			//cssSandpaper.setTransform(ex.div, "rotate(" + this.affineParams.180+"deg)");
				}





          function getCoordinates(ipt)
          {
                 var jsonText = YAHOO.lang.JSON.stringify([
                                  {"x":Math.round(ipt.x), "y":Math.round(ipt.y)}
                                  ]
                                );
                 //var url = "/omar/imageSpace/imageToGround?id=${rasterEntry.id}&x="+Math.round(ipt.x)+"&y="+Math.round(ipt.y)
                 var url = "/omar/imageSpace/imageToGround?id=${rasterEntry.id}&imagePoints="+jsonText.toString();

                 new OpenLayers.Ajax.Request(url, {
                      onSuccess: function(transport) {
                      var temp = YAHOO.lang.JSON.parse(transport.responseText);
                      var out = document.getElementById("mouseDisplayId");
                      if(out)
                      {
                        if(temp.length>0)
                        {
                          out.innerHTML = "<table><tr><td width='10%'>x: " + temp[0].x + "</td>" +"<td width='10%'>y: " + temp[0].y +
                                          "</td>" + "<td width='20%'>lat: " + temp[0].lat + "</td><td width='20%'>lon: " + temp[0].lon + "</td>" + 
                                          "<td width='20%'>hgt: " + temp[0].hgt + " m </td>";
                        }
                        else
                        {
                          out.innerHTML = "";
                        }
                      }
                 }

                });
            }


 </r:script>
</body>
</html>