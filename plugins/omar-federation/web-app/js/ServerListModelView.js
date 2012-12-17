OMAR.models.OmarServerModel=Backbone.Model.extend({
    defauls:{
        url:"",
        alive:true,
        enabled:true
    },
    initialize:function(params)
    {

    }
});

OMAR.views.OmarServerView=Backbone.View.extend({
    initialize:function(params){
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
            this.model = new OMAR.models.OmarServerCollection();
        }
    },
    //events:{                                                                   dc
    //  "dblclick" : "render"
    //},
    makeServer:function(name)
    {
        var compiled = _.template($('#template-contact').html(),{
            "serverName":name, "count":"1000000000"});

        return compiled;
    },
    render:function()
    {
        if(this.el)
        {
            var compiled = this.makeServer(0);
            $(this.el).html("");
            var offsetX = 0;
            $(this.el).append(this.makeServer("PED 1"));
            $(this.el).append(this.makeServer("PED 2"));
            $(this.el).append(this.makeServer("PED 3"));
            $(this.el).append(this.makeServer("PED 4"));
            $(this.el).append(this.makeServer("PED 5"));
            $(this.el).append(this.makeServer("PED 6"));
            $(this.el).append(this.makeServer("PED 7"));
            $(this.el).append(this.makeServer("PED 8"));
            $(this.el).append(this.makeServer("PED 9"));
            $(this.el).append(this.makeServer("PED 10"));
            $(this.el).append(this.makeServer("PED 11"));
            $(this.el).append(this.makeServer("PED 12"));
            $(this.el).append(this.makeServer("PED 13"));
            $(this.el).append(this.makeServer("PED 14"));
            // $(this.el).layout();
            //$(this.el).children(".omar-server-container").each(function(idx, el){
            //    var containerDivRegion = YAHOO.util.Region.getRegion(el);
            //    alert(containerDivRegion);
            //});
               // function(idx, el){
               //     var containerDivRegion = YAHOO.util.Region.getRegion($(this.el).firstChild());
            //);
        }
    }
});