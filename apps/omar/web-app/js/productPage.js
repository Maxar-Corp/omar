OMAR.models.Product = Backbone.Model.extend({
	urlRoot: "/omar/product/submitJob",
	defaults: {
		layers: null,
      	bbox: null,
      	combinerType: null,
      	outputFileName: null,
      	outputProjection: null,
      	outputType: null,
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

        if(!this.model.attributes.combinerType) {
        	this.model.attributes.combinerType = $(this.el).find("#combinerTypeId").val();
        }

        if(!this.model.attributes.outputFileName) {
        	this.model.attributes.outputFileName = $(this.el).find("#outputFileNameId").val();
        }

        if(!this.model.attributes.outputProjection) {
        	this.model.attributes.outputProjection = $(this.el).find("#outputProjectionId").val();
        }

        if(!this.model.attributes.outputType) {
        	this.model.attributes.outputType = $(this.el).find("#outputTypeId").val();
        }

        if(!this.model.attributes.gridAlignment) {
        	this.model.attributes.gridAlignment = $(this.el).find("#gridAlignmentId").val();
        }

        $( "#submitButtonId" ).click($.proxy(this.submit, this));
    },

    submit:function(){

    	//alert(this.model.url());
    	$.get(this.model.url(), {}, function(result){/*alert(JSON.stringify(result));*/});


    	//alert(JSON.stringify( this.model.toJSON() ));
    },
    
    render:function(){
    	$(this.el).find("#combinerTypeId").val(this.model.attributes.combinerType);
		$(this.el).find("#outputFileNameId").val(this.model.attributes.outputFileName);
		$(this.el).find("#outputProjectionId").val(this.model.attributes.outputProjection);
		$(this.el).find("#outputTypeId").val(this.model.attributes.outputType);
		$(this.el).find("#combinerTypeId").val(this.model.attributes.combinerType);
		$(this.el).find("#gridAlignmentId").val(this.model.attributes.gridAlignment);
    }
});

OMAR.ProductPage = null;
OMAR.pages.ProductPage = (function($, params){
    OMAR.ProductPage = new OMAR.views.ProductPageView(params);
    return OMAR.ProductPage;
});

$(document).ready(function () {
    //$.ajaxSetup({ cache: false });
    init();
});