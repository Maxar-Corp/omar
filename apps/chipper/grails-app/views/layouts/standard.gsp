<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/4/13
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:layoutTitle/></title>
    <r:require modules="standard"/>
    <g:layoutHead/>
    <r:layoutResources/>
    <style type="text/css">
    #north {
        height: 75px;
    }

    #south {
        height: 100px;
    }

    #east {
        width: 200px;
    }

    #west {
        width: 200px;
    }

    #center {
        padding: 5px;
        background: #eee;
    }
    </style>
</head>

<body class="easyui-layout">

<div id="north" data-options="region:'north',title:'North Title',split:true">
    <g:pageProperty name="page.north"/>
</div>

<div id="south" data-options="region:'south',title:'South Title',split:true,collapsed:true">
    <g:pageProperty name="page.south"/>

</div>

<div id="east" data-options="region:'east',title:'East',split:true,collapsed:false">
    <g:pageProperty name="page.east"/>

</div>

<div id="west" data-options="region:'west',title:'West',split:true">
    <g:pageProperty name="page.west"/>

</div>

<div id="center" data-options="region:'center',title:'center title'">
    <g:pageProperty name="page.center"/>
</div>
<g:layoutBody/>
<r:layoutResources/>
</body>
</html>