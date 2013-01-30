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
        "click #ExportGeoJsonId": "exportGeoJson",
        "click #ExportGml2Id": "exportGml2",
        "click #ExportCsvId": "exportCsv",
    },
    exportKmlQuery:function() {
        alert("export kml query");
    },
    exportGeoJson:function() {
        alert("export geo json");
    },
    exportGml2:function() {
        alert("export gml2");
    },
    exportCsv:function() {
        alert("export csv");
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