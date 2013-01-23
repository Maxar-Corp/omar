<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <r:require modules = "federationAdmin"/>
    <title>OMAR <g:meta name="app.version"/>: Federation Admin Page</title>
    <r:layoutResources/>
</head>
<body>
<div class="outer-center" id="federatedAdminPageId">
        <g:render plugin="omar-federation" template="/templates/federationAdmin"/>
</div>
<r:layoutResources/>

<script type="text/javascript">
    function init(){
        var params = {};
        var federationAdmin = OMAR.pages.FederationAdmin(jQuery, params);
        federationAdmin.render();
    }
</script>

</body>
</html>
