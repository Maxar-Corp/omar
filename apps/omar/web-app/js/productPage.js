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
        gridAlignment: null
    },
    initialize: function(){

        // alert("Welcome to this world");
    },
    url:function() {
        result = this.urlRoot + "?";
        params = "";
        idx = 0;

        for (param in this.attributes) {
            if (params != "") {
                params += "&";
            }
            params += param + "=" + this.attributes[param];
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

        if(!this.model.attributes.writer) {
            this.model.attributes.writer = $(this.el).find("#writerId").val();
        }

        if(!this.model.attributes.gridAlignment) {
            this.model.attributes.gridAlignment = $(this.el).find("#gridAlignmentId").val();
        }

        this.combinerTypeId = "#combinerTypeId";
        this.outputFileId = "#outputFileId";
        this.srsId = "#srsId";
        this.gsdId = "#gsdId";
        this.writerId = "#writerId";
        this.combinerTypeId = "#combinerTypeId";
        var thisPtr = this;
        $('form input, form select').change(function(){
            thisPtr.model.set($(this).attr("name"), $(this).val());
        });
        //$(this.el).find("input").change(function(){
        //});

        $( "#submitButtonId" ).click($.proxy(this.submit, this));
       // alert($(this.gsdId).get());
    },
    submit:function(){
        $( "#submitButtonId").prop('disabled', true);

        //alert(this.model.url());
        $.get(this.model.url(), {}, function(result){
            if(OMAR.jobPage)
            {
                OMAR.jobPage.refresh();
            }
           // alert("Job submitted with ID: " + result.jobId);
        })
        .done(function() {
            //$( "#submitButtonId").prop('disabled', false);

            //alert( "second success" );
             //   window.open()
        })
        .fail(function() {
               // alert( "error" );
        })
        .always(function() {
                $( "#submitButtonId").prop('disabled', false);
              //  alert( "finished" );
        });


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
        $(this.el).find("#srsId").val(this.model.attributes.outputProjection);
        $(this.el).find("#writerId").val(this.model.attributes.writer);
        $(this.el).find("#combinerTypeId").val(this.model.attributes.combinerType);
        $(this.el).find("#gridAlignmentId").val(this.model.attributes.gridAlignment);
        $(this.el).find("#gsdId").val(this.model.attributes.meters);
        $(this.el).find("#resamplerFilterId").val(this.model.attributes.resampler_filter);
   }
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