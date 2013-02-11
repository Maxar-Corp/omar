OMAR.models.MenuModel = Backbone.Model.extend(
{

}
);

OMAR.views.MenuView = Backbone.View.extend({
    el:"#federatedSearchMenuId",
    initialize: function(params)
    {
        
    },
    events: {
        "click #ExportKmlQueryId": "exportKmlQuery",
        "click #ExportKmlId": "exportKml",
        "click #ExportGeoJsonId": "exportGeoJson",
        "click #ExportGml2Id": "exportGml2",
        "click #ExportCsvId": "exportCsv"
    },
    exportKmlQuery:function() {
        //OMAR.federatedRasterSearch.setupExports("kmlQuery");
        this.trigger("onKmlQueryClicked");
    },
    exportKml:function() {
        //OMAR.federatedRasterSearch.setupExports("kmlQuery");
        this.trigger("onKmlClicked");
    },
    exportGeoJson:function() {
        this.trigger("onGeoJsonClicked")
    },
    exportGml2:function() {
        this.trigger("onGml2Clicked")
    },
    exportCsv:function() {
        this.trigger("onCsvClicked")
    },
    render:function()
    {
        $("#federatedSearchMenuId").jMenu({
            openClick: false,
            ulWidth: 100,
            effects: {
                effectSpeedOpen: 200,
                effectSpeedClose: 200,
                effectTypeOpen: 'slide',
                effectTypeClose: 'hide',
                effectOpen: 'linear',
                effectClose: 'linear'
            },
            TimeBeforeOpening: 100,
            TimeBeforeClosing: 100,
            animatedText: true,
            paddingLeft: 10
        });
    }
});