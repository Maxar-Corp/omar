<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <asset:stylesheet src="federationAdmin.css"/>
    <title>OMAR <g:meta name="app.version"/>: Federation Admin Page</title>
</head>

<body>
<div class="outer-center" id="federatedAdminPageId">
    <omar:securityClassificationBanner/>
    <g:render plugin="omar-common-ui" template="/templates/federationAdminMenu"/>
    <g:render plugin="omar-federation" template="/templates/federationAdmin"/>
    <omar:securityClassificationBanner/>
</div>
<asset:javascript src="federationAdmin.js"/>
<g:javascript>
    function init()
    {
        var params = {};
        var federationAdmin = OMAR.pages.FederationAdmin( jQuery, params );
        federationAdmin.render();
    }
</g:javascript>

</body>
</html>
