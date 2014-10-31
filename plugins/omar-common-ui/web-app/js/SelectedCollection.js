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
    toArrayOfIds:function(maxCount){
        var selectedItem = [];
        if(!maxCount) maxCount = this.length;
        else if(maxCount > this.length) maxCount = this.length;
        for(var idx=0; idx < maxCount; idx++){
            var item = this.at(idx);
            selectedItem.push(item.id);
        }
        return selectedItem;
    },
    toStringOfIds:function(seperator, maxCount){
        if(!seperator) {
            seperator = ",";
        }

        return this.toArrayOfIds(maxCount).join(seperator);
    },
    toArrayOfIdsReverse:function(maxCount){
        var selectedItem = [];
        if(!maxCount) maxCount = this.length;
        else if(maxCount > this.length) maxCount = this.length;
        for(var idx=0; idx < maxCount; idx++){
            var item = this.at(idx);
            selectedItem.unshift(item.id);
        }
        return selectedItem;
    },
    toStringOfIdsReverse:function(seperator, maxCount){
        if(!seperator) {
            seperator = ",";
        }

        return this.toArrayOfIdsReverse(maxCount).join(seperator);
    },
});
