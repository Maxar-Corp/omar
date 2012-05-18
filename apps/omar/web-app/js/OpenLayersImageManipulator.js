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


OMAR.OpenLayersFeatureHandler = OpenLayers.Class(OpenLayers.Handler.Feature, {

    touchstart: function(evt) {
    var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
    if (!this.touch) {
         this.touch = true;
         this.map.events.un({
            mousedown: this.mousedown,
            mouseup: this.mouseup,
            mousemove: this.mousemove,
            click: this.click,
            dblclick: this.dblclick,
            scope: this
         });
      }
      return OpenLayers.Event.isMultiTouch(adaptedEvt) ? true : this.mousedown(evt);
   },
   touchmove: function(evt) {
     var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
     OpenLayers.Event.stop(adaptedEvt);
   },
   mousedown: function(evt) {
      var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
      this.down = adaptedEvt.xy;
      return this.handle(adaptedEvt) ? !this.stopDown : true;
   },
   mouseup: function(evt) {
     var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
      this.up = adaptedEvt.xy;
      return this.handle(adaptedEvt) ? !this.stopUp : true;
   },
   click: function(evt) {
     var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
     return this.handle(adaptedEvt) ? !this.stopClick : true;
   },
   mousemove: function(evt) {
    var adaptedEvt = this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt)))
      return this.move();
      if (!this.callbacks['over'] && !this.callbacks['out']) {
         return true;
      }
      this.handle(adaptedEvt);
      return true;
   },

   CLASS_NAME: "OMAR.OpenLayersFeatureHandler"
});

/**
* This is temporarily used to filter coordinates for different features.
* I could not figure out a generic way of doing it so the code is duplicated 
* for each feature type we need (Point, Line, Polygon).
* 
*/
OMAR.OpenLayersPointHandler = OpenLayers.Class(OpenLayers.Handler.Point,{
  EVENT_TYPES:["featureDone"],
  customEvents:null,
   initialize : function(control, callbacks, options){
   // this.manipulator = options.manipulator;
    OpenLayers.Handler.Path.prototype.initialize.apply(this, arguments);
    this.customEvents = new OpenLayers.Events(this, null, this.EVENT_TYPES, true);
    this.callbacks.done = this.featureDone;

   },
   mousemove:function(evt){
    return this.move(this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mouseup:function(evt){
    return this.up(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mousedown:function(evt){
    return this.down(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   featureDone: function(geom){
    this.drawFeature(geom);
    // pass the added object to the layer not the object we are drawing
    this.handler.customEvents.triggerEvent("featureDone", {feature:this.layer.features[this.layer.features.length-1]});
   },
   CLASS_NAME: "OMAR.OpenLayersPointHandler"
});


OMAR.OpenLayersPathHandler = OpenLayers.Class(OpenLayers.Handler.Path,{
  EVENT_TYPES:["featureDone"],
  customEvents:null,
   initialize : function(control, callbacks, options){
   // this.manipulator = options.manipulator;
    OpenLayers.Handler.Path.prototype.initialize.apply(this, arguments);
    this.customEvents = new OpenLayers.Events(this, null, this.EVENT_TYPES, true);
    this.callbacks.done = this.featureDone;

   },
   mousemove:function(evt){
    return this.move(this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mouseup:function(evt){
    return this.up(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mousedown:function(evt){
    return this.down(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   featureDone: function(geom){
    this.drawFeature(geom);
    // pass the added object to the layer not the object we are drawing
    this.handler.customEvents.triggerEvent("featureDone", {feature:this.layer.features[this.layer.features.length-1]});
   },
   CLASS_NAME: "OMAR.OpenLayersPathHandler"
});

OMAR.OpenLayersPolygonHandler = OpenLayers.Class(OpenLayers.Handler.Polygon,{
  EVENT_TYPES:["featureDone"],
  customEvents:null,
   initialize : function(control, callbacks, options){
   // this.manipulator = options.manipulator;
    OpenLayers.Handler.Path.prototype.initialize.apply(this, arguments);
    this.customEvents = new OpenLayers.Events(this, null, this.EVENT_TYPES, true);
    this.callbacks.done = this.featureDone;

   },
   mousemove:function(evt){
    return this.move(this.manipulator.adaptOpenLayersXY(evt, this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mouseup:function(evt){
    return this.up(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   mousedown:function(evt){
    return this.down(this.manipulator.adaptOpenLayersXY(evt, 
                                      this.manipulator.pointToTransformPoint(this.manipulator.mouseToPoint(evt))));
   },
   featureDone: function(geom){
    this.drawFeature(geom);
    // pass the added object to the layer not the object we are drawing
    this.handler.customEvents.triggerEvent("featureDone", {feature:this.layer.features[this.layer.features.length-1]});
   },
   CLASS_NAME: "OMAR.OpenLayersPolygonHandler"
});

OMAR.OpenLayersImageManipulator = OpenLayers.Class({
  mouseDragStart:null,
  map:null,
  zoomBox:null,
  selectBox:null,
  mousePosition:null,
  eventDiv:null,
  annotationDiv:null,
  compassDiv:null,
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
  localImageBounds:null,
  upIsUpAngle:null,
  northAngle:null,
  fillAreaFlag:true,

  EVENT_TYPES:["featureDone", "featureRemoved"],
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
   },
   initialize : function()
   {
    this.affineParams =  new OmarAffineParams();
    this.affineM = new OmarMatrix3x3();

   },
   setup : function(containerDiv, mapObj, annDiv, topDiv, compass){
    this.map           = mapObj;
    this.eventDiv      = this.getDivElement(topDiv);
    this.annotationDiv = this.getDivElement(annDiv);
    this.containerDiv  = this.getDivElement(containerDiv);
    this.compassDiv    = this.getDivElement(compass);
    this.containerDivRegion = YAHOO.util.Region.getRegion(this.containerDiv);
    this.vectorLayer = new OpenLayers.Layer.Vector();
    this.map.addLayer(this.vectorLayer);
    this.events = new OpenLayers.Events(this, this.eventDiv, this.EVENT_TYPES, true);
    this.drawControls = {
                    point: 
                    new OpenLayers.Control.SelectFeature(this.vectorLayer,
                        //OpenLayers.Handler.Point) ,
                        OMAR.OpenLayersFeatureHandler, {
                          handlerOptions:{
                            manipulator:this
                          }
                        }),
                    line: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                       //OpenLayers.Handler.Path),
                        OMAR.OpenLayersPathHandler, {
                          handlerOptions:{
                            manipulator: this
                          }
                        }),
                    polygon: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        //OpenLayers.Handler.Polygon)//,
                        OMAR.OpenLayersPolygonHandler, {
                          handlerOptions:{
                            manipulator:this
                          }
                        })
                 };

         this.vectorLayer.events.on({
            "beforefeaturesadded": function(){
                //this.events.triggerEvent("measureAddPointFinished");
                if(!OMAR.ToolModeType.POINT)
                  this.vectorLayer.destroyFeatures();
            },
            scope:this
          });
//          this.drawControls.line.events.on({
//            //"featureadded": function (){alert("HERE")},
//            scope: this
//          });
    this.map.addControl(this.drawControls.point);
    this.map.addControl(this.drawControls.line);
    this.map.addControl(this.drawControls.polygon);

    this.drawControls.line.handler.customEvents.on({
      "featureDone": function(feature){
        this.events.triggerEvent("featureDone", feature);//{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
      },
      scope:this
    });
    this.drawControls.polygon.handler.customEvents.on({
      "featureDone": function(feature){
        this.events.triggerEvent("featureDone", feature);//{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
      },
      scope:this
    });
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

        var removedMeasurements = this.vectorLayer.features.length > 0;
        this.vectorLayer.destroyFeatures();
        switch(this.toolMode)
        {
            case OMAR.ToolModeType.LINE:
            {
                this.currentDrawControl = this.drawControls.line;
                this.currentDrawControl.activate();
                removedMeasurements = true;
                break;
            }
            case OMAR.ToolModeType.POLYGON:
            {
                this.currentDrawControl = this.drawControls.polygon;
                this.currentDrawControl.activate();
                removedMeasurements = true;
                break;
            }
            case OMAR.ToolModeType.POINT:
            {
                this.currentDrawControl = this.drawControls.point;
                this.currentDrawControl.activate();
                removedMeasurements = true;
                break;
            }
        }
        if(removedMeasurements) this.events.triggerEvent("featureRemoved");
    },
   adaptOpenLayersXY : function(evt, pt){

      evt.xy = this.events.getMousePosition(evt);
      evt.xy.x = pt.x;
      evt.xy.y = pt.y;
//    evt.xy={x:pt.x,y:pt.y};
//    evt.xy.equals = function(xy){
//        return ((this.x == xy.x)&&(this.y == xy.y));
//    }
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
      var tempAffine    = new OmarAffineParams();

      var w = this.containerDivRegion.width;
      var h = this.containerDivRegion.height;
      var maxWH = Math.max(w,h);
      var extraW = 0.5*(maxWH -w);//w*0.5*(Math.sqrt(2)- 1.0);
      var extraH = 0.5*(maxWH -h);//h*0.5*(Math.sqrt(2)- 1.0);

      tempAffine.rotate = 45;//this.affineParams.rotate;
      tempAffine.pivot  = new OmarPoint(w*0.5, h*0.5);

      if(!this.fillAreaFlag||(OpenLayers.BROWSER_NAME == "msie"))
      {
        extraW = 0.0;
        extraH = 0.0;
        tempAffine.rotate = 0;
      }
      var p1 = new OmarPoint(-extraW, -extraH);
      var p2 = new OmarPoint(w+extraW, -extraH);
      var p3 = new OmarPoint(w+extraW, h+extraH);
      var p4 = new OmarPoint(-extraW,h+extraH);
      var m  = tempAffine.toMatrix();
      p1  = m.transform(p1);
      p2  = m.transform(p2);
      p3  = m.transform(p3);
      p4  = m.transform(p4);
      var minX = Math.min(Math.min(Math.min(p1.x, p2.x), p3.x), p4.x);
      var maxX = Math.max(Math.max(Math.max(p1.x, p2.x), p3.x), p4.x);
      var minY = Math.min(Math.min(Math.min(p1.y, p2.y), p3.y), p4.y);
      var maxY = Math.max(Math.max(Math.max(p1.y, p2.y), p3.y), p4.y);
      var width    = Math.abs(Math.round(maxX-minX));
      var height    = Math.abs(Math.round(maxY-minY));
      var left = Math.round(minX);
      var top  = Math.round(minY);
    
      OpenLayers.Util.modifyDOMElement(div, null, {x:left,y:top}, {w:(width),h:(height)});
   },
  containerResized: function(){
      var center = this.map.getCenter();
      var region = YAHOO.util.Region.getRegion(this.containerDiv);
      this.containerDivRegion = region;
      this.setChildDivDimensions(this.map.div);
      this.setChildDivDimensions(this.eventDiv);
      this.setChildDivDimensions(this.annotationDiv);

      this.updateTransform();
      this.map.updateSize(); // tell the map to adjust itself
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

        if(this.compassDiv)
        {
          var rotate =  (this.affineParams.rotate-this.northAngle)%360;
          cssSandpaper.setTransform(this.compassDiv,
                                  "rotate(" + rotate+"deg)");
        }
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
  moveToCenter: function(pt){
    if(this.localImageBounds)
    {
      if(this.projectionType == OMAR.ProjectionType.PIXEL)
      {
        var w = Math.abs(this.localImageBounds.right-this.localImageBounds.left);
        var h = Math.abs(this.localImageBounds.bottom-this.localImageBounds.top);
        this.setCenterGivenImagePoint(new OmarPoint(this.localImageBounds.left + w*0.5,
                                                    this.localImageBounds.bottom + h*0.5));
      }
    }
  },
  setCenterGivenImagePoint: function(pt){
    if(this.map)
    {
        if((this.projectionType == OMAR.ProjectionType.PIXEL)&&
           this.localImageBounds)
        {
            var newCenter = new OpenLayers.LonLat(pt.x, (this.localImageBounds.top - pt.y ) );
            this.map.setCenter(newCenter);
        }
        else
        {
            alert("We only have support for setCenterGiveImagePoint for image space operations.");
        }
    }
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
            //var wasDrawing = this.currentDrawControl.handler.drawing;
            if(this.currentDrawControl) this.currentDrawControl.handler.dblclick(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
            //if(wasDrawing&&!this.currentDrawControl.handler.drawing)
            //{
            //    this.events.triggerEvent("measureAddPointFinished",{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
            //}
            OpenLayers.Event.stop(evt);  
           break;
        }
        case OMAR.ToolModeType.POINT:
        {
          if(this.currentDrawControl) this.currentDrawControl.handler.dblclick(evt);
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
            this.map.div.style.cursor = "move";
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
        case OMAR.ToolModeType.POINT:
       {
            //if(!this.currentDrawControl.handler.drawing)
             //    this.vectorLayer.destroyFeatures();
             this.currentDrawControl.handler.mousedown(evt);
            //this.currentDrawControl.handler.mousedown(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
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
             this.map.div.style.cursor = "move";
//           OpenLayers.Event.stop(evt);  
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
      case OMAR.ToolModeType.POINT:
      {
          this.currentDrawControl.handler.mousemove(evt);
          //OpenLayers.Event.stop(evt);  
        break;
     }
    }
    if(this.editMode)
    {
        this.mouseDragStart = this.mouseToPoint(evt);
    }
   },
   mouseup: function(evt){
       var setCenterForLayers = function(scopePtr){
          var extent = scopePtr.map.getExtent();
          var idx = 0;
          for(idx = 0; idx < scopePtr.map.layers.length; ++idx)
          {
            scopePtr.map.layers[idx].moveTo(extent);
          }

       }
       this.mousePosition = this.mouseToPoint(evt);
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
                 // now lets make sure all layers are refreshed to the current
                 // extents when drag finished
                 setCenterForLayers(this);
                 
                 //this.map.setCenter(this.map.center);

                 //this.map.baseLayer.moveTo(this.map.getExtent());

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
          // if(this.currentDrawControl.handler.drawing)
           //{

                this.currentDrawControl.handler.mouseup(evt);
                //this.currentDrawControl.handler.mouseup(this.adaptOpenLayersXY(evt, this.pointToTransformPoint(this.mouseToPoint(evt))));
                OpenLayers.Event.stop(evt); 

                // check for triggering finished 
               // if(!this.currentDrawControl.handler.drawing)
               // {
                // this.events.triggerEvent("measureAddPointFinished",{feature:this.vectorLayer.features[this.vectorLayer.features.length-1]});
               // }
           //}
           document.onselectstart = null;
           break;
        }

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
