OMAR.models.Map = Backbone.Model.extend({
});

OMAR.views.Map = Backbone.View.extend({
    el:"#map",
     initialize:function(params){
        this.setElement(this.el);
         if(params.theme)
         {
             this.theme = params.theme;
         }
         else
         {
             this.theme = null;
         }
     },
    reset:function()
    {
        if(this.map)
        {
            this.map.destroy();
            this.map = null;
        }
        if(this.el)
        {
            this.map = new OpenLayers.Map({
                div: this.el,
                theme:this.theme,
                layers: [
                    new OpenLayers.Layer.WMS( "OpenLayers WMS",
                        "http://vmap0.tiles.osgeo.org/wms/vmap0",
                        {layers: 'basic'} )
                ],
                controls: [
                    new OpenLayers.Control.Navigation({
                        dragPanOptions: {
                            enableKinetic: true
                        }
                    }),
                    new OpenLayers.Control.PanZoom(),
                    new OpenLayers.Control.Attribution()
                ],
                center: [0, 0],
                zoom: 3
            });

            this.map.addControl(new OpenLayers.Control.LayerSwitcher());
            //this.map.events.on({
            //    "mousemove": this.setMouse,
            //    "moveend":this.setExtent,
            //        "moveend":this.setCenter,
             //       scope:this
             //   }

            //);
            this.map.events.register("mousemove", this, this.setMouse);
            this.map.events.register("moveend", this, this.setExtent);
            this.map.events.register("moveend", this, this.setCenter);
            this.map.setCenter(this.map.getCenter());
            this.map.zoomToMaxExtent();
        }
    },
    setMouse:function(evt)
    {
    },
    setExtent:function(evt)
    {
        if(this.bboxModel)
        {
            this.bboxModel.off("change", this.bboxMapChanged);
            var extent = this.map.getExtent();
            this.bboxModel.set({"minx":extent.left, "miny":extent.bottom,
                                "maxx":extent.right, "maxy":extent.top});
            this.bboxModel.on("change", this.bboxMapChanged, this);
        }
    },
    setCenter:function(evt)
    {

        //alert("setCenter");
    },
    setBboxModel:function(bboxModel)
    {
        if(this.bboxModel)
        {
            // may want to unregister change listener of old model
            this.bboxModel.off("change", this.bboxMapChanged,this);
        }
        this.bboxModel = bboxModel;
        if(this.bboxModel)
        {
            this.bboxModel.on("change", this.bboxMapChanged, this);
        }
    },
    bboxMapChanged:function()
    {

        //alert("CHANGED!!!");
    },
    render:function()
    {
       this.reset();
    }
});

