/**
 * Created by sbortman on 6/22/15.
 */

// require application.js
//= require yui/yahoo-dom-event/yahoo-dom-event.js
//= require yui/element/element-min.js
//= require yui/layout/layout-min.js
//*= require_self

(function ()
{
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    Event.onDOMReady( function ()
    {
        var layout = new YAHOO.widget.Layout( {
            units: [
                {
                    position: 'top', height: 25, body: 'top'
                },
                {position: 'center', body: 'center', scroll: true},
                {position: 'bottom', height: 25, body: 'bottom'}
            ]
        } );

        YAHOO.util.Dom.setStyle( document.body, 'visibility', 'visible' );

        layout.render();

        if ( init )
        {
            init();
        }
    } );
})();
