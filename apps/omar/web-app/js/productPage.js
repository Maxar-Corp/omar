

OMAR.models.TiffWriterProperties = Backbone.Model.extend({
    defaults:{
        // can be tiff_tiled, tiff_tiled_band_separate,
        //  tiff_strip or tiff_strip_band_separate
        //
        image_type:"tiff_tiled",

        // can be none,jpeg,packbits,deflate,or zip
        compression_type:"none",
        // if compression is JPEG then a quality can be set.
        // 100 is best quality.
        compression_quality:"100",
        create_overview:false,
        create_histogram:false,
        scale_to_eight_bit:false
        //output_tile_size_x:"256",
        //output_tile_size_y:"256"
    },
    getType:function(){
        return this.get("image_type");
    },
    initialize:function()
    {

    },
    toJson:function(){
        return this.attributes;
    }
});

OMAR.models.GeopackageWriterProperties = Backbone.Model.extend({
    defaults:{
        image_type:"ossim_gpkg",
        align_to_grid:false,
        writer_mode:"jpeg",
        tile_size_x:"256",
        tile_size_y:"256",
        create_histogram:false,
        scale_to_eight_bit:false
    },
    getType:function(){
        return this.get("image_type");
    },
    toJson:function(){
        var result = {};

        result.image_type = this.attributes.image_type;
        result.align_to_grid = this.attributes.align_to_grid;
        result.writer_mode = this.attributes.writer_mode;
        result.tile_size = "("+this.attributes.tile_size_x+","+this.attributes.tile_size_y+")";
        result.create_histogram = this.attributes.create_histogram;
        result.scale_to_eight_bit = this.attributes.scale_to_eight_bit;

        return result;
    },
    initialize:function()
    {

    }
});

OMAR.models.Product = Backbone.Model.extend({
    urlRoot: "/omar/product/submitJob",
    defaults: {
        layers: null,
        cut_wms_bbox_ll: null,
        combiner_type: null,
        output_file: null,
        resampler_filter: null,
        srs: null,
        meters:null,
        gsdMin:null,
        gsdMax:null,
        writer: null,
        writerProperties:{}
    },
    initialize: function(){

        // alert("Welcome to this world");
    },
    url:function() {
        result = this.urlRoot + "?";
        params = "";
        idx = 0;

        for (param in this.attributes) {
            if(param != "writerProperties")
            {
                if (params != "") {
                    params += "&";
                }
                if(param != null)
                {
                    params += param + "=" + this.attributes[param];
                }
                else
                {
                    params += param + "=";
                }
            }
        }

        return result + params;
    }
});

OMAR.views.ProductPageView = Backbone.View.extend({
    el:"#ProductPageId",
    initialize:function(params){
        if(params) {
            this.model = params.model
        }

        if(!this.model) {
            this.model = new OMAR.models.Product();
        }

        if(!this.model.attributes.combiner_type) {
            this.model.attributes.combiner_type = $(this.el).find("#combinerTypeId").val();
        }

        if(!this.model.attributes.output_file) {
            this.model.attributes.output_file = $(this.el).find("#outputFileId").val();
        }

        if(!this.model.attributes.srs) {
            this.model.attributes.srs = $(this.el).find("#srsId").val();
        }


        this.combinerTypeId = "#combinerTypeId";
        this.outputFileId = "#outputFileId";
        this.srsId = "#srsId";
        this.gsdId = "#gsdId";
        this.writerId = "#writerId";
        this.geopackagePropertiesDlgId="#geopackagePropertiesDlgId";
        this.tiffPropertiesDlgId="#tiffPropertiesDlgId";
        this.tiffCancelPropertiesButtonId="#tiffCancelPropertiesButtonId";
        this.tiffSavePropertiesButtonId = "#tiffSavePropertiesButtonId";
        this.gpkgCancelPropertiesButtonId="#gpkgCancelPropertiesButtonId";
        this.gpkgSavePropertiesButtonId = "#gpkgSavePropertiesButtonId";
        this.writerPropertiesButtonId="#writerPropertiesButtonId";
        this.currentDlgId = this.geopackagePropertiesDlgId;
        this.tiffWriterPropertyModel = new OMAR.models.TiffWriterProperties();
        this.geopackageWriterPropertyModel = new OMAR.models.GeopackageWriterProperties();

        this.currentWriterPropertyModel =  this.geopackageWriterPropertyModel;
        this.combinerTypeId = "#combinerTypeId";
        var thisPtr = this;
        $('form input, form select').change(function(){
            thisPtr.model.set($(this).attr("name"), $(this).val());
        });
        //$(this.el).find("input").change(function(){
        //});

        $( "#submitButtonId" ).click($.proxy(this.submit, this));
        $(this.tiffCancelPropertiesButtonId).click($.proxy(this.cancelPropertiesButtonClicked, this));
        $(this.tiffSavePropertiesButtonId).click($.proxy(this.savePropertiesButtonClicked, this));
        $(this.gpkgCancelPropertiesButtonId).click($.proxy(this.cancelPropertiesButtonClicked, this));
        $(this.gpkgSavePropertiesButtonId).click($.proxy(this.savePropertiesButtonClicked, this));
        $(this.writerPropertiesButtonId).click($.proxy(this.writerPropertiesClicked, this));

        $(this.writerId).change(function(){
           thisPtr.setCurrentWriterProperties();
        });
        // alert($(this.gsdId).get());
    },
    cancelPropertiesButtonClicked:function()
    {
        $(this.currentDlgId).dialog('close');
    },
    savePropertiesButtonClicked:function()
    {
        this.copyWriterPropertiesFromDialog();
        $(this.currentDlgId).dialog('close');
    },
    setCurrentWriterProperties:function(){
        var value = $(this.writerId).val();
        switch(value)
        {
            case "tiff":
                this.currentDlgId = this.tiffPropertiesDlgId;
                this.currentWriterPropertyModel = this.tiffWriterPropertyModel;
                break;
            case "gpkg":
                this.currentDlgId = this.geopackagePropertiesDlgId;
                this.currentWriterPropertyModel = this.geopackageWriterPropertyModel;
                break;
        }

        this.model.set("writer", this.currentWriterPropertyModel.getType());
        this.model.set("writerProperties", this.currentWriterPropertyModel.toJson());

    },
    copyWriterPropertiesToDialog:function()
    {
        var thisPtr = this;

        $(this.currentDlgId + " input[type='text'], "+this.currentDlgId+" select").each(function(idx, el){
            var name = $(el).attr("name");
            $(el).val(thisPtr.currentWriterPropertyModel.get(name));
        });
        $(this.currentDlgId + " input[type='checkbox']").each(function(idx, el){
            var name = $(el).attr("name");
            $(el).prop('checked', thisPtr.currentWriterPropertyModel.get(name));
        });
    },
    copyWriterPropertiesFromDialog:function()
    {
        var thisPtr = this;
        $(this.currentDlgId + " input[type='text'], "+this.currentDlgId+" select").each(function(idx, el){
            var name = $(el).attr("name");
            thisPtr.currentWriterPropertyModel.set(name, $(el).val());
        });
        $(this.currentDlgId + " input[type='checkbox']").each(function(idx, el){
            var name = $(el).attr("name");
            thisPtr.currentWriterPropertyModel.set(name, $(el).is(':checked'));
        });
        this.model.set("writer", this.currentWriterPropertyModel.getType());
        this.model.set("writerProperties", this.currentWriterPropertyModel.toJson());
    },
    writerPropertiesClicked:function()
    {
        var thisPtr = this;
        $(this.currentDlgId).show();
        $(this.currentDlgId).dialog('open')
                .dialog('setTitle','Edit Writer Properties');

        this.copyWriterPropertiesToDialog();
       // setTimeout($.proxy(this.copyWriterPropertiesToDialog, this), 1000);
       // alert($(this.currentDlgId ).find( "#tiffTypeId").val());
      //  setTimeout(this.testFunc, 1000);

        //alert($($( this.currentDlgId ).find( "#tiffTypeId")).val('Tiled'));
        //alert($('#tiffTypeId').get());
    },
    submit:function(){
        $( "#submitButtonId").prop('disabled', true);
        this.model.set("writer", this.currentWriterPropertyModel.getType());

        //alert(JSON.stringify(this.model.attributes));
        $.ajax({
            context:this,
            type: "post",
            contentType:"application/json; charset=utf-8",
            url: this.model.urlRoot,
            data: JSON.stringify(this.model.attributes),
            success: function (msg) {
                $( "#submitButtonId").prop('disabled', false);
                OMAR.jobPage.refresh()
            },
            error: function(msg) {
              $( "#submitButtonId").prop('disabled', false);
            }

        })
            .done(function(){
                $( "#submitButtonId").prop('disabled', false);
                OMAR.jobPage.refresh()
        });
  //      $.post(this.model.urlRoot,JSON.stringify(this.model.attributes),function(){},"json").done(function(){
  //          $( "#submitButtonId").prop('disabled', false);
  //      });


 /*
        $.get(this.model.url(), {}, function(result){
            if(OMAR.jobPage)
            {
                OMAR.jobPage.refresh();
            }
           // alert("Job submitted with ID: " + result.jobId);
        })
        .done(function() {
        })
        .fail(function() {
        })
        .always(function() {
                $( "#submitButtonId").prop('disabled', false);
        });

 */
        //alert(JSON.stringify( this.model.toJSON() ));
    },

    render:function(){
        var thisPtr = this;
        $("#gsdId").numberbox({
            value:this.model.get("meters"),
           // min:this.model.get("gsdMin"),
           // max:this.model.get("gsdMax"),
            precision:15,
            required:true,
            novalidate:true,
            name:"gsd"
        });
        $("#gsdId").attr("name", "meters");
        $(this.el).find("#combinerTypeId").val(this.model.attributes.combiner_type);
        $(this.el).find("#outputFileId").val(this.model.attributes.output_file);
        this.combinerTypeId = "#combinerTypeId";
        var thisPtr = this;
        $('form input, form select').change(function(){
            thisPtr.model.set($(this).attr("name"), $(this).val());
        });

        this.setCurrentWriterProperties();
    }
  /*
    render:function(){
        var thisPtr = this;
        $("#gsdId").numberbox({
            value:this.model.get("meters"),
            // min:this.model.get("gsdMin"),
            // max:this.model.get("gsdMax"),
            precision:15,
            required:true,
            novalidate:true,
            name:"gsd"
        });
        $("#gsdId").attr("name", "meters");
        $(this.el).find("#combinerTypeId").val(this.model.attributes.combiner_type);
        $(this.el).find("#outputFileId").val(this.model.attributes.output_file);
        $(this.el).find("#srsId").val(this.model.attributes.outputProjection);
        $(this.el).find("#combinerTypeId").val(this.model.attributes.combinerType);
        $(this.el).find("#gsdId").val(this.model.attributes.meters);
        $(this.el).find("#resamplerFilterId").val(this.model.attributes.resampler_filter);
    }
    */
});

OMAR.ProductPage = null;
OMAR.pages.ProductPage = (function($, params){
    OMAR.ProductPage = new OMAR.views.ProductPageView(params);
    return OMAR.ProductPage;
});

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    if(!OMAR.ProductPage)
    {
        init();
    }
    $(this.el).find("#srsId").val(OMAR.ProductPage.model.attributes.outputProjection);
    $(this.el).find("#combinerTypeId").val(OMAR.ProductPage.model.attributes.combinerType);
    $(this.el).find("#gsdId").val(OMAR.ProductPage.model.attributes.meters);
    $(this.el).find("#resamplerFilterId").val(OMAR.ProductPage.model.attributes.resampler_filter);
});

OMAR.ProductPage = null;
OMAR.pages.ProductPage = (function($, params){
    OMAR.ProductPage = new OMAR.views.ProductPageView(params);
    return OMAR.ProductPage;
});

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    if(!OMAR.ProductPage)
    {
        init();
    }
});