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
        this.tabView = $(this.el).buttonset();                          ""

        this.views = params.views;

        var thisPtr = this;

        //alert($(this.selector() + " :radio")[0]);

        $(this.selector() + " :radio").click(function(){
            //alert($(this).attr("value")); // this refers to current clicked radio button
           // $(this).removeClass('class_name').addClass('class_name');
            thisPtr.show($(this).attr("value"));
        });
    },

    getIndex:function(value)
    {
        var children = $(this.el).children(":radio");

        var result = -1
        $(children).each(function(idx, el){
            if($(el).val() == value)
            {
                result = idx;

                return false;
            }
        })

        return result;
    },
    click:function(idx)
    {
        this.hideAll();
        if( typeof idx  === "string")
        {
            idx = this.getIndex(idx);
        }


        var children = $(this.el).children(":radio");
        //alert($(children).size());
        //var children = $(this.el).children(":input");
        //$(children[idx]).click();
        //$(children[idx]).prop('checked', true);
        //$(this.selector() + " :radio").prop('checked', true);
        $(children[idx]).attr('checked','checked').button("refresh");

        this.show(idx);
    },
    selector:function(){
       return "#"+$(this.el).attr("id");
    },
    show:function(idx){
        this.hideAll();
        if( typeof idx  === "string")
        {
            idx = this.getIndex(idx);
        }

        $(this.views[idx]).css("display","block");
        this.trigger("show", idx);
    },
    setText:function(idx, text){
        //var children = $(this.el).children(":label");
        if( typeof idx  === "string")
        {
            idx = this.getIndex(idx);
        }
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