/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/15/13
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
OMAR.models.UnitModel = Backbone.Model.extend({
    defaults:{
        unit:""
    },
    initialize:function(params){
    }
});

OMAR.views.UnitModelView = Backbone.View.extend({
    el:"#measurementUnitViewId",
    initialize:function(params){
        this.setElement(this.el);
        this.selectorEl = $(this.el).find("#selectUnitsId")[0];
        this.model = new OMAR.models.UnitModel();
        this.model.bind("change", this.unitModelChanged, this);
        $(this.selectorEl).change($.proxy(this.unitSelectorChange, this));
    },
    unitModelChanged:function(params){
        this.render();
    },
    unitSelectorChange: function(){
        this.model.set("unit", $(this.selectorEl).val());
    },
    render:function(){
        $(this.selectorEl).val(this.model.get("unit"));
    }
})