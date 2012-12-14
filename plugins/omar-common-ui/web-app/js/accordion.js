OMAR.views.AccordionView=Backbone.View.extend({
    el:"#accordion-container",
    initialize:function(params){
        this.setElement(this.el);
        if(this.el)
        {
            //Add Inactive Class To All Accordion Headers
            $('.accordion-header').toggleClass('inactive-header');
            //Set The Accordion Content Width
            var contentwidth = $('.accordion-header').width();
            $('.accordion-content').css({'width' : contentwidth });

            //Open The First Accordion Section When Page Loads
            $('.accordion-header').first().toggleClass('active-header').toggleClass('inactive-header');
            $('.accordion-content').first().slideDown().toggleClass('open-content');


            var effectEvent = params?params.effectEvent:"click";
            if(effectEvent == "hover")
            {
                // The Accordion Effect
                $('.accordion-header').hover(function () {
                    if($(this).is('.inactive-header')) {
                        $('.active-header').toggleClass('active-header').toggleClass('inactive-header').next().slideToggle().toggleClass('open-content');
                        $(this).toggleClass('active-header').toggleClass('inactive-header');
                        $(this).next().slideToggle().toggleClass('open-content');
                    }
                    else {
                        $(this).toggleClass('active-header').toggleClass('inactive-header');
                        $(this).next().slideToggle().toggleClass('open-content');
                    }
                });
            }
            else
            {
                $('.accordion-header').click(function () {
                    if($(this).is('.inactive-header')) {
                        $('.active-header').toggleClass('active-header').toggleClass('inactive-header').next().slideToggle().toggleClass('open-content');
                        $(this).toggleClass('active-header').toggleClass('inactive-header');
                        $(this).next().slideToggle().toggleClass('open-content');
                    }
                    else {
                        $(this).toggleClass('active-header').toggleClass('inactive-header');
                        $(this).next().slideToggle().toggleClass('open-content');
                    }
                });
            }
        }
    }
})