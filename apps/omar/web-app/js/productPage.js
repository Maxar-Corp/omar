

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
        output_tile_size:"256"
    },
    getType:function(){
        return this.get("image_type");
    },
    initialize:function()
    {

    },
    validate:function(attrs){
       if(attrs)
       {
           if(attrs.compression_quality)
           {

           }
       }
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
        create_histogram:false
    },
    getType:function(){
        return this.get("image_type");
    },
    validate:function(attrs){

    },
    toJson:function(){
        var result = {};

        result.image_type = this.attributes.image_type;
        result.align_to_grid = this.attributes.align_to_grid;
        result.writer_mode = this.attributes.writer_mode;
        result.tile_size = "("+this.attributes.tile_size_x+","+this.attributes.tile_size_y+")";
        result.create_histogram = this.attributes.create_histogram;

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
        scale_2_8_bit:true,
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
    toJson:function(){
        var result = {}
        for (param in this.attributes) {
            if(this.attributes[param] != null)
            {
                result[param] = this.attributes[param];
            }
        }
        return result
    },
    /**
     * We will do a custom validate for I do not want the backbone validate to execute.  We will call
     * this method something other than validate
     */
    validate: function(attrs) {
        var result = null;
        if(!attrs) attrs = this.attributes;
        if(attrs.writer&&attrs.srs)
        {
            var writerType = attrs.writer.toLowerCase();
            var projectionType = attrs.srs.toLowerCase();

            if(writerType.indexOf("gpkg")>-1)
            {
                if(projectionType.indexOf("42001")>-1)
                {
                    result = "Geopackage output can only support Google Mercator or Geographic";
                }
            }
        }
        else
        {
            result = "Output type and projection must be set";
        }

        return result;
    },
    url:function() {
        result = this.urlRoot + "?";
        params = "";
        idx = 0;

        for (param in this.attributes) {
            if(param != "writerProperties")
            {
                if(this.attributes[param] != null)
                {
                    params += param + "=" + this.attributes[param]+"&";
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
        this.scaleToEightBitId = "#scaleToEightBitId";
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
        $('form :text, form select').change(function(){
            thisPtr.model.set($(this).attr("name"), $(this).val());

            if(thisPtr.model.get($(this).attr("name")) != $(this).val())
            {
                $(this).val(thisPtr.model.get($(this).attr("name")));
            }
        });
        $('form :checkbox').change(function(){
            thisPtr.model.attributes[$(this).attr("name")] = $(this).is(':checked');
            if(thisPtr.model.get($(this).attr("name")) != $(this).is(':checked'))
            {
                $(this).prop('checked', hisPtr.model.attributes[$(this).attr("name")] == true);
            }
        });

        $( "#submitButtonId" ).click($.proxy(this.submit, this));
        $(this.tiffCancelPropertiesButtonId).click($.proxy(this.cancelPropertiesButtonClicked, this));
        $(this.tiffSavePropertiesButtonId).click($.proxy(this.savePropertiesButtonClicked, this));
        $(this.gpkgCancelPropertiesButtonId).click($.proxy(this.cancelPropertiesButtonClicked, this));
        $(this.gpkgSavePropertiesButtonId).click($.proxy(this.savePropertiesButtonClicked, this));
        $(this.writerPropertiesButtonId).click($.proxy(this.writerPropertiesClicked, this));

        $(this.writerId).change(function(){
           thisPtr.setCurrentWriterProperties();
        });
        this.model.on("error",function(model,err){
            $.messager.alert("Warning", err);
        });
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
    copyFormToModel:function(){
        var thisPtr = this;
        $('form :text, form select').each(function(k,v){
            thisPtr.model.set($(v).attr("name"), $(v).val());
        });
        $('form :checkbox').each(function(k,v){
            thisPtr.model.set($(v).attr("name"), $(v).is(':checked'));
        });
    },
    submit:function(){

        // For some reason I have to do this.  The change event on the checkbox seems to work when
        // I do alerts on the value but when submit occurs the checkbox seems to always be true
        // once set.  No clue.  Forcing a refresh on the model seems to fix it.
        this.copyFormToModel();
        //$( "#submitButtonId").prop('disabled', true);
        this.model.set("writer", this.currentWriterPropertyModel.getType());
        this.model.set("scale_2_8_bit", $('form :checkbox[name="scale_2_8_bit"]').is(":checked"))

       if(this.model.isValid())
       {
           alert("VALID");
           /*
            $.ajax({
                context:this,
                type: "post",
                contentType:"application/json; charset=utf-8",
                url: this.model.urlRoot,
                data: JSON.stringify(this.model.toJson()),
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
            */

       }
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
       // $('form input, form select').change(function(){
       //     thisPtr.model.set($(this).attr("name"), $(this).val());
       // });

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
    $(this.el).find("#scaleToEightBitId").prop('checked', OMAR.ProductPage.model.attributes.scale_2_8_bit == true);
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