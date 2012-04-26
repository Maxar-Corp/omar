/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 1/30/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */


(function ()
{
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    Event.onDOMReady( function ()
    {
        var layout = new YAHOO.widget.Layout( {
            units:[
                { position:'top', height:120, body:'top1' },
                { position:'bottom', height:25, body:'bottom1' },
                //{ position:'left', header:'Left', width:200, resize:true, body:'left1', gutter:'5px', collapse:true, scroll:true },
                //{ position:'right', header:'Right', width:300, resize:true, body:'right1', gutter:'5px', collapse:true, scroll:true },
                { position:'center', body:'center1', scroll:true }
            ]
        } );
        layout.on( 'render', function ()
        {
            init();
            Dom.setStyle( document.body, 'visibility', 'visible' );
        } );
        layout.render();
    } );
})();

