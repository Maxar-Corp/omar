OMAR.models.Map = Backbone.Model.extend({
});

OMAR.views.Map = Backbone.View.extend({
    el:"#map",
     initialize:function(params){
        this.setElement(this.el);
        //this.model = new OMAR.models.Map({"div":this.el});
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
                div: "map",
                theme:"/omar/plugins/openlayers-0.12/js/theme/default/style.css",
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
            this.map.events.register("mousemove", this.map, this.setMouse);
            this.map.events.register("moveend", this.map, this.setExtent);
            this.map.events.register("moveend", this.map, this.setCenter);
        }
    },
    setMouse:function(evt)
    {
    },
    setExtent:function(evt)
    {
        alert("setExtent");
    },
    setCenter:function(evt)
    {
        alert("setCenter");
    },
    setBboxModel:function(bboxModel)
    {
        if(this.bbobxModel)
        {
            // may want to unregister change listener of old model
            this.bboxModel.off("change", this.bboxChanged);
        }
        this.bboxModel = bboxModel;
        if(this.bboxModel)
        {
            this.bboxModel.on("change", this.bboxChanged);
        }
    },
    bboxChanged:function()
    {
       alert("In MAP SEARCH AND GOT BBOX CHANGE EVENT");
    },
    render:function()
    {
       this.reset();
    }
});

