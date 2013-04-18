/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/15/13
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
/**
 *
 * DD, MGRS, DMS
 */
OMAR.models.DisplayUnitModel = Backbone.Model.extend({
    defaults:{
        unit:""
    },
    initialize:function(params){
    }
});

OMAR.views.DisplayUnitModelView = Backbone.View.extend({
    el:"#displayUnitId",
    initialize:function(params){
        this.setElement(this.el);
        this.model = new OMAR.models.DisplayUnitModel();
        this.displayUnitEl = $("#displayUnit");
       // this.model.on('change', this.unitSelectorChange, this);
    },
    events:{
        "change #displayUnit": "displayUnitOnChange"
    },

    displayUnitOnChange:function(params){
        this.model.set({unit:$(this.displayUnitEl).val()});
      // this.render();
    },
    unitSelectorChange: function(){
        //this.model.set("unit", $(this.selectorEl).val());
    },
    render:function(){
        //$(this.selectorEl).val(this.model.get("unit"));
    }
})