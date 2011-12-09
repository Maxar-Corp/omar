<html>
    <head>
        <title>OMAR <g:meta name="app.version"/>: Image Sharing</title>
        <style>
            body
            {
                background: #2f2f2f;
                color: #ffffff;
            }
        </style>
        <g:javascript>
            function init()
            {
                var url = window.opener.document.getElementById("shareLink").innerHTML;
                document.getElementById("url").innerHTML = "<a href='" + url + "' target = '_new'><FONT COLOR = yellow> Link</FONT COLOR></a>";
            }
        </g:javascript>
    </head>
    <body onload = "init()">
        <h1>OMAR Image Sharing</h1>
        Share this image with someone by sending them the link below. To copy, simply right click on the link and copy the shortcut.
        <div id = "url"></div>
    </body>
</html>