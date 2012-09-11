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

if(window.console&&window.console.log)
{
  //OpenLayers.Util.extend(OpenLayers.Console, window.console);
}

/*
* MouseEvent
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

/*
(function(window) {
var MouseEvent = function(type, clientX, clientY, target, nativeEvent) {
  this.initialize(type, clientX, clientY, target, nativeEvent);
}
var p = MouseEvent.prototype;
  p.clientX = 0;
  p.clientY = 0;
  p.type = null;
  p.nativeEvent = null;
  p.target = null;
//  p.screenX = 0;
//  p.screenY = 0;
  p.ctrlKey = null;
  p.shiftKey = null;
  p.altKey = null;
  p.button = null;
  p.metaKey = null;
  p.preventDefault = function(){return this};
  p.stopPropagation = function(){return this};

  p.initialize = function(type, clientX, clientY, target, nativeEvent) {
    this.type = type;
    this.nativeEvent = nativeEvent;
    var tempString = "";

//alert(target);
   if(this.nativeEvent)
   {

//    var tempClone = OMAR.getCloneOfObject(this.nativeEvent);
//    for(x in tempClone)
 //   {
 //     tempString = (tempString + x + "\n");
 //   }
  //    OpenLayers.Util.extend(this, nativeEvent);
      for( key in this.nativeEvent)
      {
        if(typeof this.nativeEvent[key] == "function")
        {

          //this[key].this = this;
        }
      }
    //  for(key in this.nativeEvent)
    //  { 
    //    if(!key in ["preventDefault","stopPropagation","clientX","clientY","x","y","target"])
    //      this[key] = this.nativeEvent[key];
      this.ctrlKey = this.nativeEvent.ctrlKey;
      this.shiftKey = this.nativeEvent.shiftKey;
      this.altKey = this.nativeEvent.altKey;
      this.metaKey = this.nativeEvent.metaKey;
      this.button = this.nativeEvent.button;
      this.cancelable = this.nativeEvent.cancelable;
      this.cancelBubble = this.nativeEvent.cancelBubble;
      this.button = this.nativeEvent.button;

    //  }
    // this.preventDefault = this.nativeEvent.preventDefault;
    }

    this.x = clientX;
    this.y = clientY;
    this.clientX = clientX;
    this.clientY = clientY;
    this.target = target;
    this.preventDefault = null;
    this.stopPropagation = null;
    //this.xy={x:clientX, y:clientY};
  }

  p.clone = function() {
    return new MouseEvent(this.type, this.clientX, this.clientY, this.target, this.nativeEvent);
  }

  p.toString = function() {
    return "";
  }

window.MouseEvent = MouseEvent;
}(window));
*/

OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
        defaultHandlerOptions: {
            'single': true,
            'double': false,
            'pixelTolerance': 0,
            'stopSingle': false,
            'stopDouble': false
        },

        initialize: function(options) {
            this.handlerOptions = OpenLayers.Util.extend(
                {}, this.defaultHandlerOptions
            );
            OpenLayers.Control.prototype.initialize.apply(
                this, arguments
            ); 
            this.handler = new OpenLayers.Handler.Click(
                this, {
                    'click': this.trigger
                }, 
                this.handlerOptions
            );
        }, 

        trigger: function(e) {
          //  var lonlat = map.getLonLatFromViewPortPx(e.xy);
          //  alert("You clicked near " + lonlat.lat + " N, " +
           //                           + lonlat.lon + " E");
        }

    });
OMAR.OpenLayersImageManipulator = OpenLayers.Class({
  mouseDragStart:null,
  map:null,
  mapEventsDiv:null,
  zoomBox:null,
  selectBox:null,
  mousePosition:null,
  eventDiv:null,
  annotationDiv:null,
  compassDiv:null,
  metersPerPixelFullRes:0.0,
  affineParams:null,
  affineM: null,
  eventDivToMapDivM: null,
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
  outScaleAOI:null,
  lastTick:null,

  EVENT_TYPES:["onDragFinished",
               "onScaleChanged",
               "onAoiFinished",
               "onAoiModified",
               "onToolModeChanged",
               "onFeatureDone",
               "onFeatureRemoved"],
   destroy : function() 
   {
    this.events.un ({
        "click":this.click,
        "dblclick":this.dblClick,
        "mousedown":this.mousedown,
        "mouseup":this.mouseup,
        "mousemove": this.mousemove,
        "mouseout": this.mouseout,
        "wheel": this.wheel,
        "mouseover":this.mouseover,
        "touchstart":this.touchstart, 
        "touchmove":this.touchmove,
        "touchend": this.touchend,
       "scope":this
    });
   },
   initialize : function(options)
   {
    this.affineParams =  new OmarAffineParams();
    this.affineM = new OmarMatrix3x3();
    this.eventDivToMapDivM = new OmarMatrix3x3();
    this.metersPerPixelFullRes = options.metersPerPixelFullRes?options.metersPerPixelFullRes:0.0;
    this.lastTick = (new Date()).getTime();

    //[i for(i in document)].filter(function(i){return i.substring(0,2)=='on'&&(document[i]==null||typeof document[i]=='function');})
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
    //this.map.events.includeXY = false;

       var crossStyle =  new OpenLayers.Style({"default": {fillColor: "#000000"},
           "temporary": {fillColor: "#000000", graphicName: "cross"}});

       this.drawControls = {
                    point: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        OpenLayers.Handler.Point, {handlerOptions:{style:crossStyle}}),
                    line: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                       OpenLayers.Handler.Path),
                    polygon: new OpenLayers.Control.DrawFeature(this.vectorLayer,
                        OpenLayers.Handler.Polygon)
                  };
       var isIE=document.all;
       if (isIE)
           this.eventDiv.style.cursor = "url('../images/hand.cur'),pointer";
       else
           this.eventDiv.style.cursor = "url('../images/hand.cur') 24 24,pointer";

// now setup overrides
//
     this.vectorLayer.events.on({
             "beforefeaturesadded": function(){
                  switch(this.toolMode)
                  {
                    case OMAR.ToolModeType.LINE:
                    case OMAR.ToolModeType.POLYGON:
                    {
                      this.vectorLayer.destroyFeatures();
                      break;
                    }
                  }
             },
             scope:this
           });
   for(var key in this.drawControls) {
        this.map.addControl(this.drawControls[key]);
        if(this.drawControls[key]&&this.drawControls[key].events)
        {
          this.drawControls[key].events.includeXY = false;
        }
        if(this.drawControls[key]&&this.drawControls[key].handler)
        {
          this.drawControls[key].handler.imageManipulator = this;



          this.drawControls[key].handler.mousemove = function(evt){
                  return this.move(this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt))));
          };
          this.drawControls[key].handler.mousedown = function(evt){
                  return this.down(this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt))));
          };
          this.drawControls[key].handler.mouseup = function(evt){
                  return this.up(this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt))));
          };

          this.drawControls[key].handler.touchstart= function(evt) {
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
              this.lastTouchPx = evt.xy;
              return this.down(this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt))));
          };
          this.drawControls[key].handler.touchmove= function(evt) {
            var modifiedEvt = this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt)));
            this.lastTouchPx = modifiedEvt.xy;
            return this.move(modifiedEvt);
          };
          this.drawControls[key].handler.touchend= function(evt) {
           var modifiedEvt = this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt)));
            //evt.xy = this.lastTouchPx;
            return this.up(modifiedEvt);
          }          
          this.drawControls[key].handler.featureDone = function(geom){
            this.drawFeature(geom);
            //OpenLayers.Console.info(this.layer.features[this.layer.features.length-1]);
            this.handler.imageManipulator.events.triggerEvent("onFeatureDone", {feature:this.layer.features[this.layer.features.length-1]});
          }
          this.drawControls[key].handler.callbacks.done = this.drawControls[key].handler.featureDone;
       }
        this.drawControls.line.handler.dblclick = function(evt){
            var evtAdapted = this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt)));
            if (!this.freehandMode(evtAdapted)) {
               this.finishGeometry();
            }
            return false;
        }
        this.drawControls.polygon.handler.dblclick = function(evt){
            var evtAdapted = this.imageManipulator.adaptOpenLayersXY(evt,
                                        this.imageManipulator.pointToTransformPoint(this.imageManipulator.mouseToPoint(evt)));
            if (!this.freehandMode(evtAdapted)) {
               this.finishGeometry();
            }
            return false;
        }
    }
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
        "mouseover":this.mouseover,
        "touchstart":this.touchstart, 
        "touchmove":this.touchmove,
        "touchend": this.touchend,
       "scope":this
    });
    this.wheelListener = OpenLayers.Function.bindAsEventListener(this.wheel, this);
    //this.eventHandler = OpenLayers.Function.bindAsEventListener(this.handleBrowserEvent, this);

    OpenLayers.Event.observe(window,   "DOMMouseScroll", this.wheelListener);
    OpenLayers.Event.observe(window,   "mousewheel",     this.wheelListener);
    OpenLayers.Event.observe(document, "mousewheel",     this.wheelListener);
    this.mapEventsDiv = OpenLayers.Util.getElement(this.map.id + "_events");
    this.containerResized(true);
  },
//handleBrowserEvent: function(evt)
//{

//}
   calculateMetersPerPixel: function()
   {
       var result = 0.0;
       switch(this.projectionType)
       {
           case OMAR.ProjectionType.PIXEL:
           {
               result = this.metersPerPixelFullRes*this.map.getResolution();
               break;
           }
           default:
           {
               result = (OpenLayers.INCHES_PER_UNIT[this.map.units]*
                         this.map.getResolution()*
                         OpenLayers.METERS_PER_INCH);
               break;
           }
       }
       return result;
   },
   calculateAzimuth: function()
   {
       var rot = (this.northxAngle + this.affineParams.rotate);
       //if(rot < 0.0) rot += 360.0;
       return rot%360;
   },
   setToolMode: function(mode)
   {
        var stateChangedFlag = (mode != this.toolMode);


        // need to add clearing the state information when changing states.
        this.toolMode = mode;

        if(stateChangedFlag)
        {
            this.toolModeChanged();
            this.events.triggerEvent("onToolModeChanged");
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
        this.eventDiv.style.cursor = "";

       var pqeBox = document.getElementById("pqeDivId");
       pqeBox.innerHTML = ' ';

       switch(this.toolMode)
        {
            case OMAR.ToolModeType.PAN_ZOOM:
            {
                var isIE=document.all;
                if (isIE)
                    this.eventDiv.style.cursor = "url('../images/hand.cur'),pointer";
                else
                    this.eventDiv.style.cursor = "url('../images/hand.cur') 24 24,pointer";
                break;
            }
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
                var isIE=document.all;
                if (isIE)
                    this.eventDiv.style.cursor = "url('../images/mens.cur'),crosshair";
                else
                    this.eventDiv.style.cursor = "url('../images/mens.cur') 17 17,crosshair";
                break;
            }
        }
        if(removedMeasurements) this.events.triggerEvent("onFeatureRemoved");
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
    setChildDivDimensions: function(div, initializing, oldRegion){
      if(oldRegion&&(OpenLayers.BROWSER_NAME == "msie")&&!initializing)
      {
          /*
          try{
              var shiftW = -(this.containerDivRegion.width-oldRegion.width)/2;
              var shiftH = -(this.containerDivRegion.height-oldRegion.height)/2;
              var x = parseInt(div.style.left);
              var y = parseInt(div.style.top);

              if(!isNaN(shiftW) && !isNaN(shiftH)&&!isNaN(x)&&!isNaN(y))
              {
                  //div.style.left = x-shiftW+"px";
                  //div.style.top  = y-shiftH+"px";
                  // alert("modify: " + x +", " +shiftW +", "+y+", "+shiftH);
                  //OpenLayers.Util.modifyDOMElement(div, null, {x:(x-shiftW),y:(y-shiftH)});
                  OpenLayers.Util.modifyDOMElement(div, null, {x:x,y:(y)});
              }
          }
        catch(e)
          {
          }
          */
          return;
      }
      var tempAffine    = new OmarAffineParams();

      var w = this.containerDivRegion.width;
      var h = this.containerDivRegion.height;
      var w2 = w*0.5;
      var h2 = h*0.5;
      var WH = Math.max(w,h);   // extend out by about 10 percent
      var WHMin = Math.min(w,h);   // extend out by about 10 percent
      var ratio = WH/WHMin;
      var multiplier = 1.0;
      if(ratio < 1.4){multiplier = 1.4/ratio;}


      WH=WH*multiplier;

      //WH = WHMin*2.0;

      var WH2 = WH*0.5;
      w2 = WH2;
      h2 = WH2;
      tempAffine.rotate = 0;//this.affineParams.rotate;
      tempAffine.pivot  = new OmarPoint(w*0.5, h*0.5);
      var center = tempAffine.pivot
      if(!this.fillAreaFlag)//||(OpenLayers.BROWSER_NAME == "msie"))
      {
        extraW = 0.0;
        extraH = 0.0;
        tempAffine.rotate = 0;
      }
      var p1 = new OmarPoint(center.x-w2, center.y-h2);
      var p2 = new OmarPoint(center.x + w2, center.y-h2);
      var p3 = new OmarPoint(center.x+w2, center.y+h2);
      var p4 = new OmarPoint(center.x-w2, center.y+h2);
      var m  = tempAffine.toMatrix();
      p1  = m.transform(p1);
      p2  = m.transform(p2);
      p3  = m.transform(p3);
      p4  = m.transform(p4);
      var minX   = Math.min(Math.min(Math.min(p1.x, p2.x), p3.x), p4.x);
      var maxX   = Math.max(Math.max(Math.max(p1.x, p2.x), p3.x), p4.x);
      var minY   = Math.min(Math.min(Math.min(p1.y, p2.y), p3.y), p4.y);
      var maxY   = Math.max(Math.max(Math.max(p1.y, p2.y), p3.y), p4.y);
      var width  = Math.abs(Math.round(maxX-minX));
      var height = Math.abs(Math.round(maxY-minY));
      var left   = Math.round(minX);
      var top    = Math.round(minY);

       //alert(left +", " + top + ", " + width + ", "+height + "\n" + w +","+h);
    
      OpenLayers.Util.modifyDOMElement(div, null, {x:left,y:top}, {w:(width),h:(height)});
   },
  containerResized: function(initializing){
      var center = this.map.getCenter();
      var region = YAHOO.util.Region.getRegion(this.containerDiv);
      var oldRegion = this.containerDivRegion;
      this.containerDivRegion = region;
      this.setChildDivDimensions(this.map.div, initializing, oldRegion);
      this.setChildDivDimensions(this.eventDiv, initializing, oldRegion);
      this.setChildDivDimensions(this.annotationDiv, initializing, oldRegion);
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

    region = YAHOO.util.Region.getRegion(this.annotationDiv);
    var centerPx = new OmarPoint(region.width*0.5, region.height*0.5);
    this.affineParams.pivot.x = centerPx.x;
    this.affineParams.pivot.y = centerPx.y;
    this.affineParams.scale.x = 1;
    this.affineParams.scale.y = 1;
    this.affineParams.translate.x = -region.x;
    this.affineParams.translate.y = -region.y;
    this.affineM = this.affineParams.toMatrix();
    if(this.eventDiv)
    {
        var eventDivRegion = YAHOO.util.Region.getRegion(this.eventDiv);
        var mapViewportRegion = YAHOO.util.Region.getRegion(this.map.eventsDiv);
        var mapViewportDivRegion = YAHOO.util.Region.getRegion(this.map.viewPortDiv);

         var centerEvent    = new OmarPoint((eventDivRegion.width)*0.5,
                                           (eventDivRegion.height)*0.5);
        var centerMap    = new OmarPoint((mapViewportRegion.width)*0.5,
                                           (mapViewportRegion.height)*0.5);

        //var centerEvent    = new OmarPoint(Math.abs(eventDivRegion.right-eventDivRegion.left)*0.5,
        //                                   Math.abs(eventDivRegion.bottom-eventDivRegion.top)*0.5);
        var rotate       = new OmarAffineParams();
        rotate.rotate    = this.affineParams.rotate;
        var transEvent   = new OmarMatrix3x3();
        var transEvent2  = new OmarMatrix3x3();
         transEvent.makeTranslate(-centerEvent.x, -centerEvent.y);
        transEvent2.makeTranslate(-centerMap.x, centerMap.y);
        //transEvent3.makeTranslate(-eventDivRegion.left, -eventDivRegion.top);
        this.eventDivToMapDivM = transEvent2.transform(rotate.toMatrix().transform(transEvent));
    }
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
        // let's make sure openlayers offsets are initialize
        //
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
      //return new OmarPoint(evt.clientX, evt.clientY);//YAHOO.util.Event.getXY(evt);
  },
  generateAnnotationPointToFullImage:function(){
      var region = YAHOO.util.Region.getRegion(this.annotationDiv);
      var annotationPointToPointM = new OmarMatrix3x3();
      annotationPointToPointM.makeTranslate(region.left, region.top);
      var fullImageTrans = this.generateOssimFullImageTransform();

      var tempAffine = new OmarAffineParams();
      tempAffine.scale.y = -1.0;
      tempAffine.scale.x = 1.0;
      tempAffine.translate.y = this.map.maxExtent.top;
      var tempAffineM = tempAffine.toMatrix();
      return fullImageTrans.transform((this.affineM.transform(annotationPointToPointM)));
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
  findZoomForMetersPerPixel: function(mpp){
      var zoom = null;
      if(this.projectionType == OMAR.ProjectionType.PIXEL)
      {
          var idx = 0;
          var ratio = mpp/this.metersPerPixelFullRes;
          for(idx=0;idx < this.map.getNumZoomLevels();++idx)
          {
              if(this.map.layers[0].resolutions[idx]<ratio)
              {
                  if(idx > 0) --idx;
                  break;
              }
          }
          if(idx >= this.map.getNumZoomLevels()) idx =  this.map.getNumZoomLevels() -1;
          zoom = idx;
      }
      else
      {
          resolution = (1.0/OpenLayers.INCHES_PER_UNIT["degrees"])*
                       (OpenLayers.INCHES_PER_UNIT["m"]*viewParam.mpp);
          zoom =this.map.getZoomForResolution(resolution);
      }

      return zoom
  },
  setCenterGivenImagePoint: function(pt, zoom){
    if(this.map)
    {
        if((this.projectionType == OMAR.ProjectionType.PIXEL)&&
           this.localImageBounds)
        {
            var newCenter = new OpenLayers.LonLat(pt.x, (this.localImageBounds.top - pt.y ) );
            this.map.setCenter(newCenter, zoom);
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
  mouseEventToMapEvent: function(evt, type){
    /*
    var mapPt = this.map.getViewPortPxFromLayerPx(this.map.getLayerPxFromLonLat(this.pointToMap(new OmarPoint(evt.clientX, evt.clientY))));
    var tempPt = new OmarPoint(mapPt.x , mapPt.y);
        var mapDivRegion = YAHOO.util.Region.getRegion(this.map.div);
        var viewPortDivRegion = YAHOO.util.Region.getRegion(this.map.viewPortDiv);
        var mapEventsDivRegion = YAHOO.util.Region.getRegion(this.mapEventsDiv);
    var t  = type?type:evt.type;
    var event = new window.MouseEvent(t, tempPt.x, tempPt.y, this.mapEventsDiv, evt);
     return event;
     */
     return evt;
  },
  wheel: function(evt){
    if(!this.withinDiv(this.mouseToPoint(evt), this.eventDiv.parentNode))
    {
      return;
    }
    var currentTick = (new Date()).getTime();

    if((currentTick - this.lastTick) < 500)
    {
        YAHOO.util.Event.stopEvent(evt);
        return;
    }
    this.lastTick = currentTick;
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

          this.events.triggerEvent("onScaleChanged");
          // Remove selection box until code is added to scale it with zoom
          //if (this.outScaleAOI == 'Image')
          //{
              //this.removeSelectionBox();
          //}

      }
  },
  wheelDown: function(evt){
      if (this.map.getZoom() > 0) 
      {
          this.map.setCenter(this.map.getCenter(), this.map.getZoom() - 1);

          this.events.triggerEvent("onScaleChanged");
          // Remove selection box until code is added to scale it with zoom
          //if (this.outScaleAOI == 'Image')
          //{
          //    this.removeSelectionBox();
          //}
      }
  },
  click: function(evt)
  {
    if(this.currentDrawControl) this.currentDrawControl.handler.click(evt);

  },
  mouseover: function(evt){
  },
  touchstart: function(evt){
  }, 
  touchmove: function(evt){
  }, 
  touchend: function(evt){
     switch(this.toolMode)
     {
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        case OMAR.ToolModeType.POINT:
        {
          this.currentDrawControl.handler.touchend(evt);
          OpenLayers.Event.stop(evt);  
          break;
        }
     }
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
            this.events.triggerEvent("onScaleChanged");
             break;
        }
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        case OMAR.ToolModeType.POINT:
        {
          this.currentDrawControl.handler.dblclick(evt);
          OpenLayers.Event.stop(evt);  
          break;
        }
    }
    //this.map.events.handleBrowserEvent(event);
    //document.onselectstart = OpenLayers.Function.False;
   },
   mouseout: function(evt){

   },
   mousedown: function(evt){
     this.mousePosition = this.mouseToPoint(evt);
      this.editMode = false;
      this.mouseDragStart = null;
       this.mouseDragStart = this.mouseToPoint(evt);
       switch(this.toolMode)
      {
        case OMAR.ToolModeType.PAN_ZOOM:
        case OMAR.ToolModeType.ZOOM_BOX:
        {
            this.documentListenersAdded = false;
            this.performedDrag = false;
            if (OMAR.ToolModeType.PAN_ZOOM&&(!OpenLayers.Event.isLeftClick(evt))) {
             return;
            }
            /** This simulates a capture mouse events from the entire document window so we
            * can continue dragging even if the mouse goes outside the DIV.
            *
            * We may want a utility method for this.
            */

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
          document.onselectstart = OpenLayers.Function.False;
          if(this.currentDrawControl) this.currentDrawControl.handler.mousedown(evt);
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
          if ((this.mouseDragStart != null) && (!evt.shiftKey))
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
          if(this.currentDrawControl) this.currentDrawControl.handler.mousemove(evt);
          break;
     }
    }
    if(this.editMode)
    {
        this.mouseDragStart = this.mouseToPoint(evt);
    }
    //this.map.events.handleBrowserEvent(event);
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
           if(this.editMode)
           {
               this.events.triggerEvent("onAoiModified");
           }
           else
           {
               this.events.triggerEvent("onAoiFinished");
           }

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
                 this.events.triggerEvent("onDragFinished");
             }
          }
          document.onselectstart = null;
          this.map.div.style.cursor = "";
          break;
        }
        case OMAR.ToolModeType.POINT:
        case OMAR.ToolModeType.LINE:
        case OMAR.ToolModeType.POLYGON:
        {
          if(this.currentDrawControl) this.currentDrawControl.handler.mouseup(evt);
          document.onselectstart = null;
          if(this.performedDrag){
              setCenterForLayers(this);
              this.events.triggerEvent("onDragFinished");
          }

            break;
        }

       }
       this.mouseDragStart = null;
       this.editMode = false;
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

         // Remove selection box until code is added to scale it with zoom
        // if (this.outScaleAOI == 'Image')
        // {
        //   this.removeSelectionBox();
        // }
        this.events.triggerEvent("onScaleChanged");
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
