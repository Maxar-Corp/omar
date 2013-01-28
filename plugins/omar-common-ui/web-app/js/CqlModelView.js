OMAR.models.CqlModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:"",
        name:"",
        cql:"",
        cqlState:""
    }
});

OMAR.models.CqlCollectionModel = Backbone.Collection.extend({
    url:"",
    model:OMAR.models.CqlModel,
    initialize:function(params){
    }
});
