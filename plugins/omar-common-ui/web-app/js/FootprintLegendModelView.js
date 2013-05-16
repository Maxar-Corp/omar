/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 5/16/13
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */

OMAR.models.FootprintStyleName = Backbone.Model.extend({
    idAttribute:"style",
    defaults:{
        style:""
    }
});

OMAR.models.FootprintLegendStyleModel = Backbone.Model.extend({
    idAttribute:"styleName",
    defaults:{
        styleName:"",
        colorTable:[]
    },
    initialize:function(params){

    }
});

OMAR.models.FootprintLegendStyleCollectionModel = Backbone.Collection.extend({
   model:OMAR.models.FootprintLegendStyleModel,
    initialize:function(params)
    {
        if(params)
        {
            if(params.styles)
            {
            }
        }
    }
});

OMAR.views.FootprintLegendView = Backbone.View.extend({
   el:"#footprintLegendId",
   footprintLegendTableEl:null,
   model:null,
    footprintStyle:null,
   initialize:function(params)
   {
       if(params)
       {
           this.model = new OMAR.models.FootprintLegendStyleCollectionModel();
           if(params.styles)
           {
               for(var idx = 0; idx < params.styles.size();++idx)
               {
                   this.model.add(
                       {
                           styleName:params.styles[idx].styleName,
                           colorTable:params.styles[idx].colorTable
                       }
                   );
               }
           }
       }
       if($(this.el).size()>0)
       {
           this.footprintLegendTableEl = $(this.el).find("#footprintLegendTableId");
       }
       this.footprintStyle = new OMAR.models.FootprintStyleName({style:"byFileType"});

       this.footprintStyle.bind("change", this.render, this);
   },
   render:function(){
       var result = "";
       var style = null;
       if(this.model)
       {
          style = this.model.get(this.footprintStyle.get("style"));
       }
       if(style)
       {
           result += "<table>";
           $.each( style.attributes.colorTable, function( key, value ) {
               result += "<tr>";
               result += "<td bgcolor='"+value+"'>&nbsp;</td>";
               result += "<td>&nbsp;</td>";
               result += "<td>"+key+"</td>";
               result += "</tr>"
           });
           result += "</table>";
       }
       $(this.el).html(result);
   }
});