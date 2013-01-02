OMAR.models.OmarServerModel=Backbone.Model.extend({
    defaults:{
        id:"",
        url:"",
        name:"",
        params:"",
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
    defaults:{
        model:OMAR.models.OmarServerModel
    },
    initialize:function(params){
    }
});

OMAR.views.OmarServerCollectionView=Backbone.View.extend({
    el:"#omarServerCollectionId",
    initialize:function(params){
        //this.setElement(this.el);
        this.omarServerView = new OMAR.views.OmarServerView();
        if(params.models)
        {
            this.model = new OMAR.models.OmarServerCollection(params.models);
        }
        else
        {
            this.model = new OMAR.models.OmarServerCollection([
                new OMAR.models.OmarServerModel(
                    {
                        id:1,
                        url:"http://10.0.10.23",
                        name:"Potts"
                    }),

                new OMAR.models.OmarServerModel(
                    {
                        id:0,
                        url:"http://10.0.10.31",
                        name:"Scottie"
                    }),
                new OMAR.models.OmarServerModel(
                    {
                        id:2,
                        url:"http://10.0.10.37",
                        name:"Dave L."
                    })
                //,
                //new OMAR.models.OmarServerModel(
                //    {
                //        id:3,
                //        url:"http://localhost",
                //        name:"BIG DATA 3"
                //    })

            ]);
        }

        for(var idx = 0; idx < this.model.size(); ++idx)
        {
            var model = this.model.at(idx);
            //model.on("change", this.modelChanged, this);
        }
        this.model.on("change", this.collectionChanged, this);
    //events:{                                                                   dc
    //  "dblclick" : "render"
    //},
    },
    modelChanged:function(params){
        this.updateServerView(params);
    },
    collectionChanged:function(params){

        var scope = this;
       $(params).each(function(idx, obj){
             scope.updateServerView(obj);
           // alert(obj.get("id") + " = " + obj.get("name"));
       })
    },
    updateServerView:function(model)
    {
        var el = $(this.el).find("#"+model.get("id"));
        var countElement = $(el).find("#omar-server-count").get();
        $(countElement).text(model.get("count"));
     },
    makeServer:function(model)
    {
        return _.template($('#omar-server-template').html(), model.attributes);
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
       // var el = $(this.el).children("#"+idx.toString())[0];
       // var spinner = new Spinner(this.spinnerOptions).spin(el);
    },
    setBusy:function(idx, flag)
    {
        if((idx!=null)&&this.model)
        {
            var model = this.model.at(idx);
            if(flag)
            {
                model.createSpinner();
                var el = $(this.el).children("#"+model.get("id"))[0];
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
                $(this.el).append(this.makeServer(model));
            }
        }
    }
});