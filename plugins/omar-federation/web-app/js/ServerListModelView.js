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
        count:"0"
    },
    initialize:function(params)
    {
        this.spinnerOptions =
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
      if(!this.spinner)
      {
        this.spinner = new Spinner(this.spinnerOptions);
      }
      return this.spinner;
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
            result.push(model);
        }
        return result;
    },

/*
fetch:function(){
        this.reset()
        $.ajax({
            url: this.url,
            cache:false,
            type: "GET",
            dataType: "json",
            timeout: 60000,
            scopePtr:this,
            success: function(response) {
                if(response)
                {
                    var size = response.size();
                    for(var idx=0;idx<size;++idx)
                    {
                        var model = new OMAR.models.OmarServerModel(response[idx]);
                        this.scopePtr.add(model);
                    }
                }
            },
            error: function(x, t, m) {
            }
        });
    },
    */
    initialize:function(params){
    }
});

OMAR.views.OmarServerCollectionView=Backbone.View.extend({
    el:"#omarServerCollectionId",
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
    },
    collectionAdd:function(params){
        //this.render();
        this.collectionReset(params);
    },
    collectionChanged:function(params){
        var scope = this;
       $(params).each(function(idx, obj){
             scope.updateServerView(obj);
       })
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
                $(this.el).append(this.makeServer({id:model.id,
                                                    url:model.get("url"),// this needs to be the models search params later
                                                    count:model.get("count"),
                                                    name:model.get("nickname")}));
            }
            else
            {
               this.updateServerView(model);
            }
        }
    },
    updateServerView:function(model)
    {
        var el = $(this.el).find("#"+model.id);
        var countElement = $(el).find("#omar-server-count").get();
        $(countElement).text(model.get("count"));
     },
    makeServer:function(attr)
    {
        return _.template($('#omar-server-template').html(), attr);
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
                if(model.spinner) model.spinner.stop();
                model.createSpinner();
                var el = $(this.el).children("#"+model.id)[0];
                if(el)
                {
                    model.spinner.spin(el);
                }
                else
                {
                    model.spinner.stop();
                }
            }
            else if(model.spinner)
            {
                model.spinner.stop();
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
                $(this.el).append(this.makeServer({id:model.id,
                                                   url:model.get("url"),// this needs to be the models search params later
                                                   count:model.get("count"),
                                                   name:model.get("nickname")}));
            }
        }
    }
});