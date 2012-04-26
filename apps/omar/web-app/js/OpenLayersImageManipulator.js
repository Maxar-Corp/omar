OMAR.ProjectionType = {
  PIXEL: 0,
  GEOGRAPHIC: 1
};

OMAR.OpenLayersImageManipulator = OpenLayers.Class({
  mouseDragStart:null,
  map:null,
  zoomBox:null,
  mousePosition:null,
  eventDiv:null,
  annotationDiv:null,
  affineParams:null,
  affineM: null,
  containerDiv: null,
  containerDivRegion: null,
  projectionType: OMAR.ProjectionType.PIXEL,
  documentListenersAdded: false,
  //projectionSpace: OMAR.OpenLayersProjectionSpace.PIXEL,
/*
   this.destroy = function() 
   {
      YAHOO.util.Event.removeListener(this.eventDiv, "click", this.mouseClick);
      YAHOO.util.Event.removeListener(this.eventDiv, "dblclick", this.mouseDblClick);
      YAHOO.util.Event.removeListener(this.eventDiv, "mousedown", this.mouseDown);
      YAHOO.util.Event.removeListener(this.eventDiv, "mouseup", this.mouseUp);
      YAHOO.util.Event.removeListener(this.eventDiv, "mousemove", this.mouseMove);
      YAHOO.util.Event.removeListener(this.eventDiv, "mouseout", this.mouseOut);    
      YAHOO.util.Event.removeListener(this.eventDiv, "mousewheel", this.mouseWheel);    

   }
   */
   initialize : function()
   {
    this.affineParams =  new OmarAffineParams();
    this.affineM = new OmarMatrix3x3();
   },
   setup : function(containerDiv, mapObj, annDiv, topDiv){
    alert("adfadsfasdfasdfasdfasd");
    this.map           = mapObj;
    this.eventDiv      = this.getDivElement(topDiv);
    this.annotationDiv = this.getDivElement(annDiv);
    this.containerDiv  = this.getDivElement(containerDiv);
    this.containerDivRegion = YAHOO.util.Region.getRegion(this.containerDiv);
    if(this.containerDiv)
    {
      if(!this.annotationDiv)
      {
        this.annotationDiv = document.createElement("div");//OpenLayers.Util.createDiv('annotationDiv', null,  null, null, "absolute", "");
        this.annotationDiv.setAttribute('id', 'hudDiv');
        this.annotationDiv.style.position='absolute';
        this.annotationDiv.style.zIndex = '5';
        this.containerDiv.insertBefore(this.annotationDiv, this.map.div);
      }
      if(!this.eventDiv)
      {
        this.eventDiv = document.createElement("div");//OpenLayers.Util.createDiv('eventDiv', null,  null, null, "absolute", "");        
        this.eventDiv.setAttribute('id', 'eventDiv');
        this.eventDiv.style.backgroundColor ='#fff';
        this.eventDiv.style.opacity=0;
        this.eventDiv.style.filter='alpha(opacity=1)';
        this.eventDiv.style.position='absolute';
        this.eventDiv.style.zIndex= '6';
        this.containerDiv.insertBefore(this.eventDiv, this.annotationDiv);
      }
    }

    YAHOO.util.Event.addListener(this.eventDiv, "click", this.mouseClick , null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "dblclick", this.mouseDblClick , null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "mousedown", this.mouseDown, null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "mouseup", this.mouseUp , null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "mousemove", this.mouseMove , null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "mouseout", this.mouseOut , null, this);
    YAHOO.util.Event.addListener(this.eventDiv, "mousewheel", this.mouseWheel, null, this);
    YAHOO.util.Event.addListener(window,   "DOMMouseScroll", this.mouseWheel,null, this);
    YAHOO.util.Event.addListener(window,   "mousewheel", this.mouseWheel, null, this);
    YAHOO.util.Event.addListener(document, "mousewheel", this.mouseWheel, null, this);

    this.containerResized();
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
      
      div.style.left   = shiftLeft + "px";//Math.round(region.left-(newW * 0.25)) + "px";
      div.style.top    = shiftTop + "px";//Math.round(region.top-(newH * 0.25)) + "px";
      div.style.width  = newW+"px";//newW + "px";
      div.style.height = newH+"px";//newH + "px";

    /*
      var radius = Math.max(this.containerDivRegion.width, this.containerDivRegion.height);

      var maxHalf = Math.round(radius*Math.sqrt(2));
      var newW = this.containerDivRegion.width+maxHalf; //Math.round(max*1.5);
      var newH = this.containerDivRegion.height+maxHalf; //Math.round(max*1.5);

      // now center about container
      var shiftLeft = -Math.round(maxHalf/2.0);//Math.round((containerCenterX-centerX)/2);
      var shiftTop  = -Math.round(maxHalf/2.0);//Math.round((containerCenterY-centerY)/2);
      
      div.style.left   = shiftLeft + "px";//Math.round(region.left-(newW * 0.25)) + "px";
      div.style.top    = shiftTop + "px";//Math.round(region.top-(newH * 0.25)) + "px";
      div.style.width  = newW+"px";//newW + "px";
      div.style.height = newH+"px";//newH + "px";
      */
  },
  containerResized: function(){
    var center = this.map.getCenter();
      var region = YAHOO.util.Region.getRegion(this.containerDiv);
      this.containerDivRegion = region;
      //alert(region);
      this.setChildDivDimensions(this.map.div);
      this.setChildDivDimensions(this.eventDiv);
      this.setChildDivDimensions(this.annotationDiv);
        //    alert(YAHOO.util.Region.getRegion(this.map.div) + "\n" +
        //          YAHOO.util.Region.getRegion(document.window));

      //var region2 = YAHOO.util.Region.getRegion(this.map.div);

    //alert((region.left) +" == " +(region2.left));
      this.updateTransform();

  //var currentCenter = this.map.getCenter();
      this.map.updateSize();
  
      this.map.setCenter(center, this.map.getZoom());
     // this.map.setCenter(currentCenter ,this.map.getZoom());
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
  transformDiv: function(){
      if(this.map && this.map.div)
      {
        cssSandpaper.setTransform(this.map.div,
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
  mouseToMap: function(evt){
    var endPt     = this.affineM.transform(this.mouseToPoint(evt));
    return this.map.getLonLatFromViewPortPx(endPt);
  },
  mouseToLocal: function(evt){
      var mapPt = this.mouseToMap(evt);
      var result = new OmarPoint(mapPt.lon, mapPt.lat);

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
  mouseWheel: function(evt){
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
  mouseClick: function(evt)
  {
    
  },
  mouseDblClick: function(evt){
      var endPt     = this.affineM.transform(this.mouseToPoint(evt));
      var newCenter = this.map.getLonLatFromViewPortPx(endPt);
  //alert("AFFINE: " + affineM +"\nMOUSE POS = " + this.mouseToPoint(evt)+"\nTRANSFORMED: " + endPt + "\nMAP CENTER: " + mapCenter);

      this.map.setCenter(newCenter, this.map.zoom + 1);
      return true;
   },
   mouseOut: function(evt){
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
   mouseDown: function(evt){
      documentListenersAdded = false;
      this.mouseDragStart = null;
      this.performedDrag = false;
      if (!OpenLayers.Event.isLeftClick(evt)) {
         return;
      }
      /** This simulates a capture mouse events from the entire document window so we
        * can continue dragging even if the mouse goes outside the DIV.
        *
        * We may want a utility method for this.
        */
      if(this.withinDiv(this.mouseToPoint(evt), this.eventDiv))
      {
        this.documentListenersAdded = true;
         YAHOO.util.Event.addListener(document, "mousemove", this.mouseMove, null, this);
         YAHOO.util.Event.addListener(document, "mouseup", this.mouseUp, null, this);
      }

      this.mouseDragStart = this.mouseToPoint(evt);
      if (evt.shiftKey) 
      {
        var region = YAHOO.util.Region.getRegion(this.annotationDiv);
         this.map.div.style.cursor = "crosshair";
         this.zoomBox = OpenLayers.Util.createDiv('zoomBox', new OmarPoint(-region.left +this.mouseDragStart.x,
                                                                           -region.top +this.mouseDragStart.y), 
                                                  null, null, "absolute", "2px solid red");
         this.zoomBox.id="zoomBox";
         this.zoomBox.style.backgroundColor = "white";
         this.zoomBox.style.filter = "alpha(opacity=50)";
         this.zoomBox.style.opacity = "0.50";
         this.zoomBox.style.fontSize = "1px";
         this.zoomBox.style.zIndex = this.map.Z_INDEX_BASE["Popup"] - 1;

         //this.zoomBox.event.event.stopPropagation();
         if(this.annotationDiv!=null) this.annotationDiv.appendChild(this.zoomBox);
      }
      document.onselectstart = OpenLayers.Function.False;
      OpenLayers.Event.stop(evt);  
    },
    mouseUp: function(evt){
      if (!OpenLayers.Event.isLeftClick(evt)) {
         return;
      }
      if(this.documentListenersAdded)
      {
          YAHOO.util.Event.removeListener(document, "mousemove", this.mouseMove);
          YAHOO.util.Event.removeListener(document, "mouseUp", this.mouseUp);
      }
      //if(this.withinDiv(this.mouseToPoint(evt), this.eventDiv))
     // {
     //    YAHOO.util.Event.removeListener(document, "mousemove", this.mouseMove);
     //    YAHOO.util.Event.removeListener(document, "mouseUp", this.mouseUp);
     // }
      if (this.zoomBox) 
      {
        this.zoomBoxEnd(evt);
      } 
      else 
      {
         if (this.performedDrag) 
         {
            this.map.setCenter(this.map.center);
         }
      }
      document.onselectstart = null;
      this.mouseDragStart = null;
      this.map.div.style.cursor = "";  
      OpenLayers.Event.stop(evt);  
  },
  mouseMove: function(evt){
      this.mousePosition = this.mouseToPoint(evt);
      if (this.mouseDragStart != null) 
      {
         var region = YAHOO.util.Region.getRegion(this.annotationDiv);
        if (this.zoomBox) 
         {
            var deltaX = Math.abs(this.mouseDragStart.x - this.mousePosition.x);
            var deltaY = Math.abs(this.mouseDragStart.y - this.mousePosition.y);
            var w = Math.max(1, deltaX) + "px";
            var h = Math.max(1, deltaY) + "px";
            this.zoomBox.style.width = Math.max(1, deltaX) + "px";
            this.zoomBox.style.height = Math.max(1, deltaY) + "px";
            
            if (this.mousePosition.x < this.mouseDragStart.x) {
               this.zoomBox.style.left = -region.left + this.mousePosition.x + "px";
            }
            if (this.mousePosition.y < this.mouseDragStart.y) {
               this.zoomBox.style.top =  -region.top + this.mousePosition.y + "px";
            }
         } 
         else 
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
         }
         this.performedDrag = true;
      }
       OpenLayers.Event.stop(evt);  
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
            var end = this.map.getLonLatFromViewPortPx(this.mouseToPoint(evt));
            this.map.setCenter(new OpenLayers.LonLat((end.lon), (end.lat)), this.map.getZoom() + 1);
         }
         this.removeZoomBox();
      }
   },
   removeZoomBox: function(){
      this.annotationDiv.removeChild(this.zoomBox);
      this.zoomBox = null;
   },
   CLASS_NAME: "OMAR.OpenLayersImageManipulator"
});
