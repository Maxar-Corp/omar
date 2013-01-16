/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/16/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
OMAR.views.ViewSelector = Backbone.View.extend({
    el:"#tabView",
    initialize:function(params){
        this.setElement(this.el);
        this.tabView = $(this.el).buttonset();

        this.views = params.views;

        var thisPtr = this;

        $(this.selector() + " :radio").click(function(){
            //alert($(this).attr("value")); // this refers to current clicked radio button
           // $(this).removeClass('class_name').addClass('class_name');
            thisPtr.show($(this).attr("value"));
        });
    },
    click:function(idx)
    {
        this.hideAll();
        var children = $(this.el).children(":input");
        $(children[idx]).click();
        this.show(idx);
    },
    selector:function(){
       return "#"+$(this.el).attr("id");
    },
    show:function(idx){
        this.hideAll();
        $(this.views[idx]).css("display","block");
        this.trigger("show", idx);
    },
    setText:function(idx, text){
        //var children = $(this.el).children(":label");
        var children = $(this.el).children(":input");
        $($(children)[idx]).button({ label: text })
    },
    hideAll:function(){
        var thisPtr = this;
        $(this.views).each(function(i) {

            $(thisPtr.views[i]).css("display","none");
        });
    },
    tabViewChanged:function(e){
        //alert($(this.tabView).val());
    },
    render:function(){

    }

});