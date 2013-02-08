/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 2/8/13
 * Time: 7:19 AM
 * To change this template use File | Settings | File Templates.
 */
OMAR.models.Selection = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:""
        ,description:""
    },
    initialize:function(params){

    }
});

OMAR.models.SelectedCollection = Backbone.Collection.extend({
    url:"",
    model:OMAR.models.Selection,
    initialize:function(){
    },
    toArrayOfIds:function(){
        var selectedItem = [];
        for(var idx=0; idx < this.size(); idx++){
            var item = this.at(idx);
            selectedItem.push(item.id);
        }
        return selectedItem;
    },
    toStringOfIds:function(seperator){
        if(!seperator) {
            seperator = ",";
        }

        return this.toArrayOfIds().join(seperator);
    }
});
