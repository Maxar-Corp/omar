OMAR.models.WfsTypeNameModel = Backbone.Model.extend({
    idAttribute:"typeName",
    defaults:{
        typeName:"omar:raster_entry"
    },
    initialize:function(params)
    {
    }
});

OMAR.views.WfsTypeNameView = Backbone.View.extend({
    el:"#wfsTypeNameId",
    initialize:function(params){
        this.setElement(this.el);
        if(params)
        {
            if(params.model)
            {
                this.model = params.model;
            }
        }
        if(!this.model)
        {
            this.model = new OMAR.models.WfsTypeNameModel();
        }
        this.model.bind("change", this.modelChanged, this);
    },
    modelChanged:function(){
        this.render();
    },
    render:function(){

        this.wfsTypeNameVideoDataSetEl = $(this.el).find("#WfsTypeNameVideoDataSetId")[0];
        this.wfsTypeNameRasterEntryEl  = $(this.el).find("#WfsTypeNameRasterEntryId")[0];

        if(this.model)
        {
            if(this.model.get("typeName").search("raster_entry") > -1)
            {
                $(this.wfsTypeNameRasterEntryEl).click();
            }
            else
            {
                $(this.wfsTypeNameVideoDataSetEl).click();
            }
        }

        $(this.el).delegate("#WfsTypeNameVideoDataSetId", "click", this.videoDataSetClicked.bind(this));
        $(this.el).delegate("#WfsTypeNameRasterEntryId", "click", this.rasterEntryClicked.bind(this));

    },
    videoDataSetClicked:function(){
        this.model.unbind("change", this.modelChanged, this);
        this.model.set({typeName:"omar:video_data_set"});
        this.model.bind("change", this.modelChanged, this);
    },
    rasterEntryClicked:function(){
        this.model.unbind("change", this.modelChanged, this);
        this.model.set({typeName:"omar:raster_entry"});
        this.model.bind("change", this.modelChanged, this);
    }
});

OMAR.models.FederatedRasterSearchModel = Backbone.Model.extend({
    defaults:{
        mapCriteriaDirtyFlag:false
       ,dataTableCriteriaDirtyFlag:false
       ,spatialSearchType:"bbox"
       ,useSpatialFlag:true
        ,mapModel:null
        ,activeTab:0
       ,cqlModel:null
       ,displayUnitModel:null
       ,bboxModel:null
       ,pointModel:null
       ,menuModel:null
       ,measurementUnitModel:null
       ,wfsServerCountModel:null
       ,dateTimeRangeModel:null
       ,wfsTypeNameModel:null
        ,footprintLegendModelView:null
       ,userRoles:[]
    },
    initialize:function(params)
    {
        if(params)
        {
            if(params.mapCriteriaDirtyFlag)
            {
                this.attributes.mapCriteriaDirtyFlag = params.mapCriteriaDirtyFlag;
            }
            if(params.userRoles)
            {
                this.attributes.userRoles = params.userRoles
            }
            if(params.dataTableCriteriaDirtyFlag)
            {
                this.attributes.dataTableCriteriaDirtyFlag = params.dataTableCriteriaDirtyFlag;
            }
            if(params.spatialSearchType)
            {
                this.attributes.spatialSearchType = params.spatialSearchType;
            }
            if(params.useSpatialFlag)
            {
                this.attributes.useSpatialFlag = params.useSpatialFlag;
            }
            if(params.activeTab)
            {
                this.attributes.activeTab = params.activeTab;
            }
            if(params.cqlModel)
            {
                this.attributes.cqlModel = params.cqlModel;
            }
            if(params.displayUnitModel)
            {
                this.attributes.displayUnitModel = params.displayUnitModel;
            }
            if(params.bboxModel)
            {
                this.attributes.bboxModel = params.bboxModel;
            }
            if(params.pointModel)
            {
                this.attributes.pointModel = params.pointModel;
            }
            if(params.menuModel)
            {
                this.attributes.menuModel = params.menuModel;
            }
            if(params.measurementUnitModel)
            {
                this.attributes.measurementUnitModel = params.measurementUnitModel;
            }
            if(params.wfsServerCountModel)
            {
                this.attributes.wfsServerCountModel = params.wfsServerCountModel;
            }
            if(params.dateTimeRangeModel)
            {
                this.attributes.dateTimeRangeModel = params.dateTimeRangeModel;
            }
            if(params.wfsTypeNameModel)
            {
                this.attributes.wfsTypeNameModel = params.wfsTypeNameModel;
            }
        }

        if(!this.cqlModel)
        {
            this.attributes.cqlModel = new OMAR.models.CqlModel();
        }
        if(!this.displayUnitModel)
        {
            this.attributes.displayUnitModel = new OMAR.models.DisplayUnitModel();
        }
        if(!this.bboxModel)
        {
            this.attributes.bboxModel = new OMAR.models.BBOX();
        }
        if(!this.pointModel)
        {
            this.attributes.pointModel = new OMAR.models.PointModel();
        }
        if(!this.menuModel)
        {
            this.attributes.menuModel = new OMAR.models.MenuModel();
        }
        if(!this.measurementUnitModel)
        {
            this.attributes.measurementUnitModel = new OMAR.models.UnitModel();
        }
        if(!this.wfsServerCountModel)
        {
            this.attributes.wfsServerCountModel = new OMAR.models.WfsModel({"resultType":"hits"});
        }
        if(!this.dateTimeRangeModel)
        {
            this.attributes.dateTimeRangeModel = new OMAR.models.SimpleDateRangeModel();
        }
        if(!this.wfsTypeNameModel)
        {
            this.attributes.wfsTypeNameModel = new OMAR.models.WfsTypeNameModel();
        }
    }
});

OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){
        this.initializing = true;
        var thisPtr = this;
        this.model = new OMAR.models.FederatedRasterSearchModel(params);

        this.footprintLegendModelView = new OMAR.views.FootprintLegendView(params?params.legend:null);

        this.wfsTypeNameView = new OMAR.views.WfsTypeNameView({model:this.model.get("wfsTypeNameModel")});
        this.displayUnitView = new OMAR.views.DisplayUnitModelView();
        this.model.set("displayUnitModel",this.displayUnitView.model);

        this.bboxView = new OMAR.views.BBOX({displayUnitModel:this.model.get("displayUnitModel")});
        this.model.set("bboxModel",this.bboxView.model);

        this.pointView = new OMAR.views.PointView({displayUnitModel:this.model.get("displayUnitModel")});
        this.model.set("pointModel",this.pointView.model);
/*
    The Menu model is a place holder for now.  We need to figure out how to
    Use backbone as a true model for a menu and build the menu up dynamically.
    Currently its just a place holder and we will build it here.
*/
        this.menuView = new OMAR.views.SearchMenuView();
        this.model.set("menuModel",this.menuView.model);

        this.menuView.bind("onKmlQueryClicked", $.proxy(this.kmlQueryClicked, this, false));
        this.menuView.bind("onKmlQueryFloatBboxClicked", $.proxy(this.kmlQueryClicked, this, true));//this.kmlQueryFloatBboxClicked, this);
        this.menuView.bind("onKmlClicked", this.kmlClicked, this);
        this.menuView.bind("onGeoJsonClicked", this.geoJsonClicked, this);
        this.menuView.bind("onGml2Clicked", this.gml2Clicked, this);
        this.menuView.bind("onCsvClicked", this.csvClicked, this);
        this.menuView.bind("onTimeLapseClicked", this.timeLapseClicked, this);
        this.menuView.bind("onGeoCellClicked", this.gclClicked, this);
        this.menuView.bind("onDownloadFilesClicked", this.downloadFilesClicked, this);

        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.model.attributes.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.model.set("wfsServerCountModel",new OMAR.models.WfsModel({"resultType":"hits"}));

        this.omarServerCollectionView = new OMAR.views.OmarServerCollectionView(
            {"model":new OMAR.models.OmarServerCollection(),
             "wfsServerCountModel":this.model.get("wfsServerCountModel"),
             "wfsTypeNameModel":this.model.get("wfsTypeNameModel"),
             "userRoles":this.model.get("userRoles")
            }
        );
        this.measurementUnitView = new OMAR.views.UnitModelView({el:"#measurementUnitViewId"});
        this.model.set("measurementUnitModel", this.measurementUnitView.model);

        var mapParams = params.map;
        mapParams.unitModelView = this.measurementUnitView;
        this.mapView = new OMAR.views.Map(mapParams);
        this.model.set("mapModel", this.mapView.model);
        this.mapView.setSearchType(this.model.get("wfsTypeNameModel"));
        this.mapView.setBboxModel(this.model.get("bboxModel"));
        this.mapView.setPointModel(this.model.get("pointModel"));
        this.model.attributes.measurementUnitModel.set("unit", "meters");
        this.mapView.setUnitModelView(this.measurementUnitView);

        this.mapView.setServerCollection(this.omarServerCollectionView.model);
        this.setElement(this.el);

        // construct with a shared wfsTypeName model
        this.dataModelView = new OMAR.views.DataModelView({wfsTypeNameModel:this.model.attributes.wfsTypeNameModel});

        var cqlViewParams = params.cql;
        cqlViewParams.wfsTypeNameModel = this.model.get("wfsTypeNameModel");
        this.cqlView = new OMAR.views.CqlView(cqlViewParams);
        this.model.attributes.cqlModel = this.cqlView.model;
        this.model.attributes.dateTimeRangeModel.bind('change',this.setCriteriaDirty, this);
        this.model.get("bboxModel").bind('change', this.bboxModelChanged, this);
        this.model.get("pointModel").bind('change', this.pointModelChanged, this);
        this.model.attributes.cqlModel.bind('change', this.setCriteriaDirty, this);

        this.omarServerCollectionView.bind("onModelClicked", this.serverClicked, this);
        this.omarServerCollectionView.activeServerModel.bind("change", this.serverClicked, this);

        this.viewSelector = new OMAR.views.ViewSelector({el:"#tabView",
                                                         views:["#CustomQueryView",
                                                                "#MapView",
                                                                "#ResultsView"]});
        this.viewSelector.bind("show", this.showTab, this);
        this.model.attributes.wfsTypeNameModel.bind("change", this.wfsTypeNameChanged, this);
        this.model.get("cqlModel").bind("change", this.cqlModelChanged, this);
        this.useSpatialFlag = $('#spatialSearchFlag').is(":checked");
        $('#spatialSearchFlag').click(function() {
            thisPtr.useSpatialFlag = $(this).is(':checked');
            thisPtr.setCriteriaDirty();
            //thisPtr.search();
        });
        $('#bboxRadioButton').click(function(){
            thisPtr.model.set("spatialSearchType", "bbox");
            thisPtr.setCriteriaDirty();
        });
        $('#pointRadioButton').click(function(){
            thisPtr.model.set("spatialSearchType", "point");
            thisPtr.setCriteriaDirty();
            thisPtr.mapView.clearBoundBox();
        });
        this.model.get("mapModel").bind("onSelectBbox", this.selectBbox, this);
        this.pointView.bind("onCenterChanged", this.pointViewEdited, this);
        this.pointView.bind("onRadiusChanged", this.pointViewEdited, this);
        this.bboxView.bind("onLlChanged", this.bboxViewEdited, this);
        this.bboxView.bind("onUrChanged", this.bboxViewEdited, this);
        this.omarServerCollectionView.bind("onServersAdded", this.omarServerCollectionChanged, this);
        this.initializing = false;
    },
    events: {
        "click #SearchId": "search"
    },
    showTab:function(idx){
        this.model.set("activeTab", idx);
        switch(idx.toString())
        {
            case "0":
                break;
            case "1":
                if(this.model.get("mapCriteriaDirtyFlag"))
                {
                    this.updateFootprintCql();
                    this.model.set("mapCriteriaDirtyFlag",false);
                }
                this.centerResize();
                break;
            case "2":
                this.dataModelView.resizeView();
                if(this.model.get("dataTableCriteriaDirtyFlag"))
                {
                    this.updateDataTable();
                    this.model.set("dataTableCriteriaDirtyFlag",false);
                }
                break;
        }
    },
    selectBbox:function(bboxModel){
        $('#bboxRadioButton').click();
    },
    bboxViewEdited:function(pointModel)
    {
        $('#bboxRadioButton').click();
    },
    pointViewEdited:function(pointModel)
    {
        $('#pointRadioButton').click();
    },
    serverClicked:function(){
        var model =  this.omarServerCollectionView.model.get(
                               this.omarServerCollectionView.activeServerModel.get("id"));
        if(model)
        {
            this.viewSelector.click(2);
            this.viewSelector.setText(2, model.get("nickname"));
        }
        this.updateDataTable();
    },
    timeLapseClicked:function(){
        var currentSelection = this.dataModelView.getCurrentSelection();
        var wfsModel = this.dataModelView.wfsModel.clone();
        var location = wfsModel.attributes.url;
        // for now let's just use the wfs model and get URL and
        // replace the wfs path
        //
        if(location)
        {
            location = location.replace("/wfs","");
        }
        var bbox = null;
        if(currentSelection.size() > 1) {
            var layerList = null;
            for(var idx=0; idx < currentSelection.size(); idx++){
                var item = currentSelection.at(idx);
                var modelRecord = this.dataModelView.model.get(item.id);
                if(modelRecord)
                {
                    var minLatLon = modelRecord.get("min_lat_lon");
                    var maxLatLon = modelRecord.get("max_lat_lon");
                    var minLatLonArray = minLatLon.split(",");
                    var maxLatLonArray = maxLatLon.split(",");
                    var tempBbox = new OMAR.models.BBOX({minx:parseFloat(minLatLonArray[1]),
                        miny:parseFloat(minLatLonArray[0]),
                        maxx:parseFloat(maxLatLonArray[1]),
                        maxy:parseFloat(maxLatLonArray[0])
                    });
                    if(!layerList) layerList = item.id;
                    else layerList += (","+item.id);
                    if(!bbox)
                    {
                        bbox = tempBbox;

                    }
                    else if(bbox.intersect(tempBbox))
                    {
                        bbox = tempBbox.intersect(bbox);
                    }
                    else
                    {
                        alert("Unable to perform request.  Not all images selected intersect");
                        return;
                    }
                }
                //alert(modelRecord.id);
            }
            var urlTemp = location +"/timeLapse/viewer?layer=" + layerList + "&bbox="+bbox.toWmsString();

            window.open(urlTemp, "");
        }
        else
        {
            alert("Please select at least 2 images in the results for timeLapse output");
        }
    },
    kmlQueryClicked:function(forceFloatBbox){

        // alert(this.mapView.hasBBOXSelection());
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");

        var currentSelection = this.dataModelView.getCurrentSelection();

        wfsModel.set({
            outputFormat:"kmlquery"
            ,resultType:""
        });
        if(forceFloatBbox)
        {
            var saveSpatial = this.useSpatialFlag;
            this.useSpatialFlag = false;
            cqlFilter = this.toCql();
            this.useSpatialFlag = saveSpatial;
        }
        else
        {
            cqlFilter = this.toCql();
        }

        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });
        }
        else
        {
            wfsModel.set({
                filter:cqlFilter
            });

        }
        //alert(wfsModel.toUrl());
        window.open(wfsModel.toUrl(),"_parent");
    },
    kmlClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"kml"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });
        }

        window.open(wfsModel.toUrl(),"_parent");
    },

    geoJsonClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"json"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        window.open(wfsModel.toUrl(),"_parent");
    },
    gml2Clicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"gml2"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        //alert(wfsModel.toUrl());
        window.open(wfsModel.toUrl(),"_parent");
    },
    csvClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");

        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"csv"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        window.open(wfsModel.toUrl(),"_parent");
    },
    downloadFilesClicked:function(){
        var fileNames = "";
        var classNames = "";
        if(!this.omarServerCollectionView.isFirstSelected())
        {
           alert("Please select the first server.  Currently, you can only export geocell " +
               "projects from the first server.  " +
               "Sorry We do not federate geocell " +
               "project exports at this time.");

            return;
        }
        var currentSelection = this.dataModelView.getCurrentSelection();
        if(this.model.attributes.userRoles.indexOf("ROLE_DOWNLOAD") >=0)
        {
            if(currentSelection.size() > 0) {

                // Build file name and type parameter strings
                for(var idx=0; idx < currentSelection.size(); idx++){
                    var item = currentSelection.at(idx);

                    fileNames += item.id.toString();
                    //var modelRecord = this.dataModelView.model.get(item.id);
                    //if(modelRecord)
                    //{
                    //    fileNames += modelRecord.get("filename");
                    //    classNames += modelRecord.get("class_name");
                    //}
                    if(idx < currentSelection.size()-1)
                    {
                        fileNames += ",";
                        //  classNames += ",";
                    }
                }

                // Initialize with controller string
                exportURL = "/omar/rasterEntryExport/exportGclProject";

                // Add image file name and type parameters
                exportURL += "?filenames=" + fileNames + "&classnames=" + classNames;
                exportURL += "&rootPathName=omar-download&includeGeocellProject=false"
                //alert("Project export initiated - this may take awhile.\nClick OK and wait for download prompt...");

                window.open(exportURL, "_parent");
            }

            else {
                alert("No images were selected for project export...");
            }
        }
        else
        {
            alert("You currently do not have download privileges.")
        }
    },
    gclClicked:function(){
        var fileNames = "";
        var classNames = "";
        if(!this.omarServerCollectionView.isFirstSelected())
        {
           alert("Please select the first server.  Currently, you can only export geocell " +
               "projects from the first server.  " +
               "Sorry We do not federate geocell " +
               "project exports at this time.");

            return;
        }
        var currentSelection = this.dataModelView.getCurrentSelection();
        if(this.model.attributes.userRoles.indexOf("ROLE_DOWNLOAD") >=0)
        {
            if(currentSelection.size() > 0) {

                // Build file name and type parameter strings
                for(var idx=0; idx < currentSelection.size(); idx++){
                    var item = currentSelection.at(idx);

                    fileNames += item.id.toString();
                    //var modelRecord = this.dataModelView.model.get(item.id);
                    //if(modelRecord)
                    //{
                    //    fileNames += modelRecord.get("filename");
                    //    classNames += modelRecord.get("class_name");
                    //}
                    if(idx < currentSelection.size()-1)
                    {
                        fileNames += ",";
                        //  classNames += ",";
                    }
                }

                // Initialize with controller string
                exportURL = "/omar/rasterEntryExport/exportGclProject";

                // Add image file name and type parameters
                exportURL += "?filenames=" + fileNames + "&classnames=" + classNames;

                //alert("Project export initiated - this may take awhile.\nClick OK and wait for download prompt...");

                window.open(exportURL, "_parent");
            }

            else {
                alert("No images were selected for project export...");
            }
        }
        else
        {
            alert("You currently do not have download privileges.")
        }
    },
    wfsTypeNameChanged:function()
    {
        var wfsTypeName = this.model.attributes.wfsTypeNameModel.get("typeName");

        if(wfsTypeName.contains("video"))
        {
            $("#TimeLapseId").attr("class","ui-state-disabled");
            $("#ExportGeoCellId").attr("class","ui-state-disabled");
            $("#DownloadId").attr("class","ui-state-disabled");
            this.menuView.unbind("onTimeLapseClicked", this.timeLapseClicked, this);
            this.menuView.unbind("onGeoCellClicked", this.gclClicked, this);
            this.menuView.unbind("onDownloadFilesClicked", this.downloadFilesClicked, this);
        }
        else
        {
            $("#TimeLapseId").attr("class","ui-state-enabled");
            $("#ExportGeoCellId").attr("class","ui-state-enabled");
            $("#DownloadId").attr("class","ui-state-disabled");
            this.menuView.bind("onTimeLapseClicked", this.timeLapseClicked, this);
            this.menuView.bind("onGeoCellClicked", this.gclClicked, this);
            this.menuView.bind("onDownloadFilesClicked", this.downloadFilesClicked, this);
        }

        this.updateLegend();
        this.search();
    },
    bboxModelChanged:function()
    {
        if(this.model.get("spatialSearchType") == "bbox")
        {
            this.setCriteriaDirty();
        }
    },
    pointModelChanged:function()
    {
        if(this.model.get("spatialSearchType") == "point")
        {
            this.setCriteriaDirty();
        }
    },
    setCriteriaDirty:function()
    {
        switch(this.model.get("activeTab").toString())
        {
            case "0": // cql
                this.model.set("dataTableCriteriaDirtyFlag",true);
                this.model.set("mapCriteriaDirtyFlag",true);
                break;
            case "1": // map
                this.model.set("dataTableCriteriaDirtyFlag",true);
                this.updateFootprintCql();
                break;
            case "2": // data table
                this.model.set("mapCriteriaDirtyFlag",true);
                this.updateDataTable();
                break;
        }
        this.updateCounts();
    },
    cqlModelChanged:function()
    {
        this.setCriteriaDirty();
    },
    render:function(){
        this.footprintLegendModelView.render();
        if(this.wfsTypeNameView)
        {
            this.wfsTypeNameView.render();
        }
        if(this.bboxView)
        {
            this.bboxView.render();
        }
        if(this.pointView)
        { 
            this.pointView.render();
        }

        if(this.menuView)
        { 
            this.menuView.render();
        }

        if(this.dateTimeRangeView)
        {
            this.dateTimeRangeView.render();
        }

        if(this.mapView)
        {
            this.mapView.render();
        }
        if(this.cqlView)
        {
            this.cqlView.render();
        }
        if(this.dataModelView)
        {
            this.dataModelView.render();
        }
        if(this.measurementUnitView)
        {
            this.measurementUnitView.render();
        }
        // lets make sure that that the map object exists
        // before we start the AJAX calls for fetching the server lists
        //
        if(this.omarServerCollectionView)
        {
            var collection =  this.omarServerCollectionView;

            collection.model.fetch({success:function(){collection.render()},
                update: true, remove: false});
           // window.setTimeout(this.updateServers.bind(this), 5000);
        }

        if(this.mapView) this.mapView.setCqlFilterToFootprintLayers(this.toCql());//this.toFootprintCql());

        // we must render everything first and fully initialize before we set a selected view
        //
        this.viewSelector.click(1);
    },
    updateFootprintCql:function(){
        this.updateCounts();
        if(this.mapView) this.mapView.setCqlFilterToFootprintLayers(this.toCql());//this.toFootprintCql());
    },
    updateServers:function(){
        var collection =  this.omarServerCollectionView;
        collection.model.fetch({success:function(){},
            update: true, remove: false});
       // window.setTimeout(this.updateServers.bind(this),5000);
    },
    omarServerCollectionChanged:function(model, newModelIdList)
    {
        var firstModel = model.at(0);

        this.updateLegend();
        //alert("Added models");
    },
    updateLegend:function(){
        var model = this.omarServerCollectionView.getFirstModel();

        if(model)
        {
            var wfsTypeName = this.model.attributes.wfsTypeNameModel.get("typeName");

            var settings = null;
            if(wfsTypeName.contains("video"))
            {
                settings = model.getVideoFootprintSettings();
            }
            else
            {
                settings = model.getRasterFootprintSettings();
            }

            if(settings&&settings.params)
            {
                if(settings.params.styles)
                {
                    this.footprintLegendModelView.footprintStyle.set({style:settings.params.styles});
                }
            }
        }

    },
    toCql:function(){
        var result = "";
        var timeQueryCql = null;
        var wfsTypeName = this.model.attributes.wfsTypeNameModel.get("typeName");
        var customQueryFilter = this.model.attributes.cqlModel.toCql();

        if(wfsTypeName.search("raster_entry") > -1)
        {
            timeQueryCql = this.model.attributes.dateTimeRangeModel.toCql("acquisition_date");
        }
        else if(wfsTypeName.search("video_data_set")>-1)
        {
            timeQueryCql = this.model.attributes.dateTimeRangeModel.toCql("start_date", "end_date");
        }
        var spatialQueryCql;

        if (this.useSpatialFlag) {
            switch(this.model.get("spatialSearchType"))
            {
                case "bbox":
                    spatialQueryCql = this.model.get("bboxModel").toCql("ground_geom");
                    break;
                case "point":
                    spatialQueryCql = this.model.get("pointModel").toCql("ground_geom");
                    break;
            }
        }

        if(timeQueryCql&&spatialQueryCql)
        {
            result = "(("+spatialQueryCql+")AND(" +timeQueryCql+"))";
        }
        else if(spatialQueryCql)
        {
            result=spatialQueryCql;
        }
        else
        {
            result = timeQueryCql;
        }
        if(customQueryFilter!="")
        {
            if(result) result += "AND";
            result += customQueryFilter;
        }
        return result;
    },
    centerResize:function(){
        //alert($("#tabView").height() + ", " + $(".inner-center").height()+","+$(".tabViewContainer").height());
        //var h = $(".inner-center").height();
        //$("#tabView").height(h-110);
        if(this.dataModelView) this.dataModelView.resizeView();
        if(this.mapView)       this.mapView.resizeView();
    },
    updateCounts:function(){
        var cqlFilter = this.toCql();
        this.model.get("wfsServerCountModel").attributes.filter = cqlFilter;
        this.model.attributes.wfsServerCountModel.trigger("change");
    },
    updateDataTable:function(){
        var cqlFilter = this.toCql();
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        if(model)
        {
            this.dataModelView.wfsModel.attributes.url      = model.get("url")+"/wfs";
            this.dataModelView.wfsModel.attributes.filter   = cqlFilter;
            this.dataModelView.wfsModel.attributes.typeName = this.model.attributes.wfsTypeNameModel.get("typeName");
            //      this.dataModelView.wfsModel.set(
            //      {"url":model.get("url")+"/wfs",
            //       "filter":cqlFilter,
            //       "typeName":this.model.attributes.wfsTypeNameModel.get("typeName")}
            //  );
            this.dataModelView.wfsModel.trigger("change");
        }
    },
    search:function(){
        this.updateCounts();
        this.updateDataTable();
    }
});

OMAR.federatedRasterSearch = null;
OMAR.pages.FederatedRasterSearch = (function($, params){
    if(!OMAR.federatedRasterSearch)
    {
        OMAR.federatedRasterSearch = new OMAR.views.FederatedRasterSearch(params);
    }
    else
    {

    }
    return OMAR.federatedRasterSearch;
});

$(document).ready(function () {
    $.ajaxSetup({ cache: false }); // turn cache off for ajax
    // OUTER-LAYOUT

    $('body').layout({
            center__paneSelector:   ".outer-center"
        ,   west__paneSelector:     ".outer-west"
        ,   east__paneSelector:     ".outer-east"
        ,   west__size:             125
        ,   east__size:             125
        ,   spacing_open:           8  // ALL panes
        ,   spacing_closed:         12 // ALL panes
        //, north__spacing_open:    0
        //, south__spacing_open:    0
        ,   north__maxSize:         200
        ,   south__maxSize:         200

            // MIDDLE-LAYOUT (child of outer-center-pane)
        ,   center__childOptions: {
                center__paneSelector:   ".middle-center"
            ,   west__paneSelector:     ".middle-west"
            ,   east__paneSelector:     ".middle-east"
            ,   west__size:             100
            ,   east__size:             100
            ,   spacing_open:           0  // ALL panes
            ,   spacing_closed:         0 // ALL panes

                // INNER-LAYOUT (child of middle-center-pane)
            ,   center__childOptions: {
                    center__paneSelector:   ".inner-center"
                ,   north__paneSelector:     ".inner-north"
                ,   west__paneSelector:     ".inner-west"
                ,   east__paneSelector:     ".inner-east"
                ,   west__size:             225
                ,   west__minSize:          225
                ,   east__size:             175
                ,   north__size:             25
                ,   north__minSize:             25
                ,   north__maxSize:             25
                ,   spacing_open:           8  // ALL panes
                ,   spacing_closed:         8  // ALL panes
                ,   west__spacing_closed:   12
                ,   east__spacing_closed:   12
                , onresize_end:function(){if(OMAR.federatedRasterSearch) OMAR.federatedRasterSearch.centerResize();}
                }
            }
        });
    // initialize one time the html parsing for datatable
    if(!OMAR.federatedRasterSearch)
    {
        init();
        OMAR.federatedRasterSearch.centerResize();
    }
    //$( "#accordion" ).accordion();
});






