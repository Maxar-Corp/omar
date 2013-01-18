OMAR.models.OmarServerModel=Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:"",
        ip:"",
        url:"",
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
        this.userDefinedData.spinnerOptions =
        {
            lines: 13,
            length: 7,
            width: 4,
            radius: 10,
            corners: 1,
            rotate: 0,
            color: '#000',
            speed: 1,
            trail: 60,
            shadow: false,
            hwaccel: false,
            className: 'spinner',
            zIndex: 2e9,
            top: 'auto',
            left: 'auto'
        };
    },
    createSpinner:function(){
      if(!this.userDefinedData.spinner)
      {
        this.userDefinedData.spinner = new Spinner(this.userDefinedData.spinnerOptions);
      }
      return this.userDefinedData.spinner;
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
        this.omarServerView = new OMAR.views.OmarServerView();
        if(params.models)
        {
            this.model = new OMAR.models.OmarServerCollection(params.models);
        }
        else
        {
            this.model = new OMAR.models.OmarServerCollection();
        }
        this.model.bind('add', this.collectionAdd, this)
        this.model.bind("change", this.collectionChanged, this);
        this.model.bind("reset", this.collectionReset, this);
        this.wfsServerCount = new OMAR.models.Wfs({"resultType":"hits"});
        this.lastClickedServerId = "";

        this.wfsServerCount.bind("change", this.refreshServerCounts, this);
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
    fetchAndSetCount:function(id){
        var model = this.model.get(id);
        if(model.get("enabled"))
        {
            this.setBusy(model.id, true);
            this.wfsServerCount.attributes.url = model.get("url")+"/wfs";
            //wfs.set("url",model.get("url")+"/wfs");

            if((model.userDefinedData.ajaxCountQuery) &&
               (model.userDefinedData.ajaxCountQuery.readyState != 4)){
                model.userDefinedData.ajaxCountQuery.abort();
                model.userDefinedData.ajaxCountQuery = null;
                this.omarServerCollectionView.setBusy(model.id, false);
            }
            model.userDefinedData.ajaxCountQuery = $.ajax({
                url: this.wfsServerCount.toUrl()+"&callback=?",
                cache:false,
                type: "GET",
                crossDomain:true,
                dataType: "json",
                timeout: 60000,
                modelId:model.id,
                scopePtr:this,
                success: function(response) {
                    if(response.numberOfFeatures!=null)
                    {
                        var numberOfFeatures = response.numberOfFeatures;
                        this.scopePtr.setBusy(this.modelId, false);
                        var tempModel = this.scopePtr.model.get(this.modelId);
                        if(tempModel)
                        {
                            tempModel.set({"count":numberOfFeatures});
                        }
                    }
                },
                error: function(x, t, m) {
                    var count = "Error";
                    if(t==="timeout") {
                        count = "Timeout"
                    } else {
                        //alert(JSON.stringify(x)+ " " +t + " " + m);
                    }
                    var tempModel = this.scopePtr.model.get(this.modelId);
                    this.scopePtr.setBusy(this.modelId, false);
                    if(tempModel)
                    {
                        tempModel.set({"count":count});
                    }
                }
            });
        }
        else
        {

        }
    },
    refreshServerCounts:function(){
        for(var idx = 0; idx <this.model.size();++idx )
        {
            var model = this.model.at(idx);
            if(model.get("enabled"))
            {
                this.fetchAndSetCount(model.id);
            }
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

        for(idx = 0; idx < this.model.size();++idx)
        {
            var model = this.model.at(idx);
            var el = $(this.el).find("#"+model.id);
            if(el.size()==0)
            {
                $(this.el).append(this.makeServer(model));
            }
            else
            {
                var countElement = $(el).find("#omar-server-count").get();
                $(countElement).text(model.get("count"));
            }
        }

    },
    getCount : function(modelId){

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
            name:model.get("nickname")}


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
        this.lastClickedServerId = id;
        this.trigger("onModelClicked", id);
        //alert(id);
    },
    getLastClickedModel:function(){
      return this.model.get(this.lastClickedServerId);
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
                var url = $(serverResult).find("#omar-server-url").click($.proxy(this.modelClicked, this, modelId));
            }
        }
    }
});