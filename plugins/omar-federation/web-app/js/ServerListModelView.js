OMAR.models.OmarActiveServerModel=Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:""
    }
    ,initialize:function(params){

    }
});
OMAR.models.OmarServerListConnectModel=Backbone.Model.extend({
    url:"/omar/federation/reconnect",
    idAttribute:"id",
    defaults:{
        id:"",
        user:"",
        connected:null
    }
});

OMAR.models.OmarServerModel=Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:"",
        ip:"",
        url:"",
        config:"",
        phone:"",
        firstName:"",
        lastName:"",
        nickname:"",
        organization:"",
        alive:true,
        enabled:true,
        count:"0"  // This as an attribute so we can get callback notification
        // on changes
    },
    initialize:function(params)
    {
        this.userDefinedData = {}
        this.userDefinedData.spinnerOptions = OMAR.defaultSpinnerOptions;

    },
    createSpinner:function(){
        if(!this.userDefinedData.spinner)
        {
            this.userDefinedData.spinner = new Spinner(this.userDefinedData.spinnerOptions);
        }
        return this.userDefinedData.spinner;
    },
    getConfigAsJson:function(){
        return JSON.parse(this.get("config"));
    },
    getWmsConfig:function(){
        var config = this.getConfigAsJson();
        var result = null;
        if(config)
        {
           result = config.wms;
        }

        return result;
    },
    getVideoFootprintSettings:function(){
        var wmsConfig = this.getWmsConfig();
        var result = null;
        if(wmsConfig)
        {
            result = wmsConfig.data.video;
        }
        return result;
    },
    getRasterFootprintSettings:function(){
        var wmsConfig = this.getWmsConfig();
        var result = null;
        if(wmsConfig)
        {
            result = wmsConfig.data.raster;
        }
        return result;
    }
});

OMAR.views.OmarServerView=Backbone.View.extend({
    initialize:function(params){
    },
    render:function(){
    }
});

OMAR.models.OmarServerCollection=Backbone.Collection.extend({
    url: '/omar/federation/serverList',
    defaults:{
        model:OMAR.models.OmarServerModel
    },
    parse:function(response){
        var result = new Array();
        var size = response.size();
        for(var idx=0;idx<size;++idx)
        {
            var model = new OMAR.models.OmarServerModel(response[idx]);
            model.id = model.id;
            var tempM = this.get(model.id);
            // make sure we copy any existing user defined data or counts to
            // the copy of the model.
            //
            if(tempM)
            {
                model.userDefinedData = tempM.userDefinedData;
                model.attributes.count = tempM.attributes.count;
            }
            result.push(model);
        }
        return result;
    },

    initialize:function(params){
    }
});

OMAR.views.OmarServerCollectionView=Backbone.View.extend({
    el:"#omarServerCollectionId",
    dummy:function(){

    },
    initialize:function(params){
        var thisPtr = this;

        this.refreshServerList = [];
        this.omarServerView = new OMAR.views.OmarServerView();
        var wfsServerCountModel;
        if(params)
        {
            if(params.models)
            {
                this.model = new OMAR.models.OmarServerCollection(params.models);
            }
            if(params.wfsServerCountModel)
            {
                wfsServerCountModel = params.wfsServerCountModel;
            }
            if(params.wfsTypeNameModel)
            {
                this.setWfsTypeNameModel(params.wfsTypeNameModel);
            }
            if(params.userRoles)
            {
                this.userRoles = params.userRoles;
            }
            else
            {
                this.userRoles = []
            }
        }
        if(!this.model)
        {
            this.model = new OMAR.models.OmarServerCollection();
        }
        this.activeServerModel = new OMAR.models.OmarActiveServerModel();
        this.activeServerModel.bind("change",  this.activeServerModelChanged, this);
        this.model.bind('add',    this.collectionAdd,     this)
        this.model.bind("change", this.collectionChanged, this);
        this.model.bind("reset",  this.collectionReset,   this);
        if(this.wfsTypeName)
        {
            this.model.bind("change", this.wfsTypeNameChange, this);
        }
        if(!wfsServerCountModel)
        {
            this.setWfsServerCountModel(new OMAR.models.WfsModel({"resultType":"hits"}));
        }
        else
        {
            this.setWfsServerCountModel(wfsServerCountModel);
        }

       // $(this.el).bind("contextmenu", function(event) {

            var menu1 = [
                {'Reconnect':function(menuItem,menu) {
                    var isAdmin = thisPtr.userRoles.indexOf("ROLE_ADMIN")>=0;
                    if(isAdmin)
                    {
                        var m = new OMAR.models.OmarServerListConnectModel();
                        if(thisPtr.reconnectAjax&&(thisPtr.reconnectAjax.readState !=4))
                        {
                            thisPtr.reconnectAjax.abort();
                        }
                        thisPtr.reconnectAjax = m.fetch({success: function(model, response) {
                            if(model.get("connected") == true)
                            {
                                alert("Successful reconnect to federation server.");
                            }
                            else
                            {
                                alert("Unable to reconnect to federation server.");
                            }
                        }});
                    }
                    else
                    {
                        alert("In order to force a federation reconnect\nYou must be logged into the OMAR server with an Admin role");
                    }

                } },
                //$.contextMenu.separator,
                {'Goto Login':function(menuItem,menu) {
                    var activeServerModel =  thisPtr.model.get(thisPtr.activeServerModel.get("id"))
                    window.open(activeServerModel.get("url")+"/login");
                } }
            ];
            $(this.el).contextMenu(menu1,{theme:'vista'});

//            alert("DOING MENU");
//            event.preventDefault();
       // });
    },
    setWfsTypeNameModel:function(wfsTypeNameModel)
    {
        if(this.wfsTypeNameModel)
        {
            this.wfsTypeNameModel.unbind("change", this.wfsTypeNameModelChange, this);
        }
        this.wfsTypeNameModel = wfsTypeNameModel;
        if(this.wfsTypeNameModel)
        {
            this.wfsTypeNameModel.bind("change", this.wfsTypeNameModelChange, this);
        }
    },
    wfsTypeNameModelChange:function(){
      if(this.wfsTypeNameModel)
      {
          this.wfsServerCountModel.set({typeName:this.wfsTypeNameModel.get("typeName")});
      }
    },
    setWfsServerCountModel:function(wfsServerCountModel)
    {
        if(this.wfsServerCountModel)
        {
            this.wfsServerCountModel.unbind("change", this.refreshServerCounts, this);
        }
        this.wfsServerCountModel = wfsServerCountModel;
        if(this.wfsServerCountModel)
        {
            this.wfsServerCountModel.bind("change", this.refreshServerCounts, this);
        }
    },
    collectionAdd:function(params){
        //this.render();
        this.collectionReset(params);
    },
    collectionChanged:function(params){
        var scope = this;
        $(params).each(function(idx, obj){
            scope.updateServerView(obj);
        });
    },
    fetchAndSetCount:function(id, callback){
        var model = this.model.get(id);
        if(model&&model.userDefinedData&&model.userDefinedData.ajaxCountQuery &&
            (model.userDefinedData.ajaxCountQuery.readyState != 4))
        {
            model.userDefinedData.ajaxCountQuery.abort();
            model.userDefinedData.ajaxCountQuery = null;
            if(this.omarServerCollectionView)
            {
                this.omarServerCollectionView.setBusy(model.id, false);
            }
        }
        if(model&&model.get("enabled"))
        {
            var modelId = model.id;
            var thisPtr = this;;
            this.setBusy(model.id, true);

            var cloneWfsServerCountModel = this.wfsServerCountModel.clone();
            cloneWfsServerCountModel.attributes.url = model.get("url")+"/wfs";
           // this.wfsServerCountModel.attributes.url = model.get("url")+"/wfs";
            //wfs.set("url",model.get("url")+"/wfs");
            model.userDefinedData.ajaxCountQuery = $.ajax({
                url: cloneWfsServerCountModel.toUrl()+"&callback=?",
                cache:false,
                type: "GET",
                crossDomain:true,
                dataType: "json",
                timeout: 60000,
                success: function(response) {
                    if(response&&(response.numberOfFeatures!=null))
                    {
                        var numberOfFeatures = response.numberOfFeatures;
                        thisPtr.setBusy(modelId, false);
                        var tempModel = thisPtr.model.get(modelId);
                        if(tempModel)
                        {
                            tempModel.set({"count":numberOfFeatures});
                        }
                    }
                    if(callback)
                    {
                        callback(id);
                    }
                },
                error: function(x, t, m) {
                    var count = "Error";
                    if(t==="timeout") {
                        count = "Timeout"
                    } else {
                        //alert(JSON.stringify(x)+ " " +t + " " + m);
                    }
                    var tempModel = thisPtr.model.get(thisPtr.modelId);
                    thisPtr.setBusy(thisPtr.modelId, false);
                    if(tempModel)
                    {
                        tempModel.set({"count":count});
                    }
                    if(callback)
                    {
                        callback(id);
                    }

                }
            });
        }
        else
        {

        }
    },
    /**
     * refreshServerCounts:
     *
     *  We will do this as an asynchronous loop
     *  so we do not flood the browsers with hundreds
     *  of background threads
     */
    refreshServerCounts:function(appendCurrent){
        var idx = 0;
        var thisPtr = this;
        function nextServer (id){
            if(thisPtr.refreshServerList.size()>0)
            {
                var modelId = thisPtr.refreshServerList.pop();
                thisPtr.fetchAndSetCount(modelId, nextServer);
            }
        }
        if(appendCurrent)
        {
            this.refreshServerList.concat(appendCurrent);
        }
        var n = this.model.size();
        if(n>0)
        {
            for(idx = n-1; idx >= 0;+--idx)
            {
                var model = this.model.at(idx);
                if(model&&model.get("enabled"))
                {
                    this.refreshServerList.push(this.model.at(idx));
                }
            }
        }
        // we will only do about 5 at a time so
        // we don't flood the browser
        var maxCount = this.refreshServerList.size()>5?5:this.refreshServerList.size();
        for(idx = 0; idx < maxCount; ++idx)
        {
            nextServer();
        }
     },
    collectionReset:function(params){
        // remove any elements that don't belong
        //
        var children = $(this.el).children();
        var scope = this;
        var childrenToDelete = [];

        $(children).each(function(idx, el)
        {
            if(!scope.model.get(el.id))
            {
                childrenToDelete.push(el);
            }
        });

        var idx = 0;
        for(idx = 0;idx<childrenToDelete.size();++idx)
        {
            $(childrenToDelete[idx]).remove();
        }
        var appendCurrent = [];
        for(idx = 0; idx < this.model.size();++idx)
        {
            var model = this.model.at(idx);
            var el = $(this.el).find("#"+model.id);
            if(el.size()==0)
            {
                var thisPtr = this;
                var server = this.makeServer(model);
                $(server).appendTo(this.el);
                var modelId = model.id;
                $(this.el).delegate("#omar-server-name-"+modelId,
                    "click",
                    $.proxy(this.modelClicked,this, model.id));
                $(this.el).delegate("#omar-server-image-"+modelId,
                    "click",
                    $.proxy(this.modelClicked,this, model.id));

                $(this.el).delegate("#omar-server-name-"+modelId,
                    "dblclick",function(){});
                appendCurrent.push(modelId);
                //})
              //  appendCurrent.push(modelId);
            }
            else
            {
                var countElement = $(el).find("#omar-server-count").get();
                $(countElement).text(model.get("count"));
                $("#omar-server-name-"+model.id).text(model.get("nickname"));
            }
            if(appendCurrent.size() > 0)
            {
               this.refreshServerCounts(appendCurrent);
               this.trigger("onServersAdded", this.model, appendCurrent);
            }
        }

    },
    updateServerView:function(model)
    {
        var el = $(this.el).find("#"+model.id);
        var countElement = $(el).find("#omar-server-count").get();
        $(countElement).text(model.get("count"));
    },
    makeServer:function(model)
    {
        var attr = {id:model.id,
            enabled:model.get("enabled"),
            //url:model.get("url"),// this needs to be the models search params later
            count:model.get("count"),
            name:model.get("nickname")};

        var result = _.template($('#omar-server-template').html(), attr);

        var checkChild = $(result).find("#omar-server-enabled-checkbox");
        if(checkChild.size()>0)
        {
            if(attr&&(attr.id!=null))
            {
                $(checkChild).value = attr.enabled;
            }
        }
        return result;
    },
    modelClicked:function(id)
    {
        this.activeServerModel.set("id", id);
        this.trigger("onModelClicked", id);
    },
    activeServerModelChanged:function()
    {
        var id = this.activeServerModel.get("id");
        var selectedServer = $(this.el).find(".omar-server-selected");
        if(selectedServer.size()>0)
        {
            $(selectedServer).attr("class","omar-server");
        }

        $($(this.el).find("#"+id)).attr("class","omar-server-selected");//.class(".omar-server-selected");

    },
    isFirstSelected:function(){
        return $($(this.el).children()[0]).attr("class") == "omar-server-selected";
    },
    getFirstModel:function(){
        if(this.model.size() > 0)
        {
            return this.model.at(0);
        }

        return null;
    },
    setAllBusy:function(flag)
    {
        var children = $(this.el).children();
        var scope = this;
        $(children).each(function(idx, el)
        {
            var model = scope.model.at(idx);
            model.createSpinner();
            if(flag)
            {
                model.spinner.spin(el);
            }
            else
            {
                model.spinner.stop();
            }
        });
    },
    setBusy:function(id, flag)
    {
        if(id&&this.model)
        {

            var model = this.model.get(id);
            if(!model) return;
            if(flag)
            {
                if(model.userDefinedData.spinner) model.userDefinedData.spinner.stop();
                model.createSpinner();
                var el = $(this.el).children("#"+model.id)[0];
                if(el)
                {
                    model.userDefinedData.spinner.spin(el);
                }
                else
                {
                    model.userDefinedData.spinner.stop();
                }
            }
            else if(model.userDefinedData.spinner)
            {
                model.userDefinedData.spinner.stop();
            }
        }
    },
    render:function()
    {
        if(this.el)
        {
            $(this.el).html("");
            for(var idx = 0; idx < this.model.size(); ++idx)
            {
                var model = this.model.at(idx);
                var server = this.makeServer(model);
                var serverResult = $(server).appendTo(this.el);
                var modelId = model.id;
                $(this.el).delegate("#omar-server-name-"+modelId, "click", $.proxy(this.modelClicked,
                    this, modelId));
            }
            var selectedServer = $(this.el).find(".omar-server-selected");
            if(this.model&&(selectedServer.size()<1) &&(this.model.size()>0))
            {
                var modelId = this.model.at(0).get("id");
                this.activeServerModel.set("id", modelId);
            }
        }
    }
});
