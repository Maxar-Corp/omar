<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/19/14
  Time: 11:49 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Download Raster</title>
    <meta name="layout" content="generatedViews"/>

</head>

<body>
<content tag="content">
    <div class="nav">
        <ul>
            <li class="menuButton"><a class="home" href="${createLink( uri: '/' )}"><g:message
                    code="default.home.label"/></a></li>
        </ul>
    </div>

    <div class="body">
        <br/>

        <h1>Download Raster</h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <div class="list">
            <table>
                <thead>
                <tr>
                    <th>Filename</th>
                    <th>Size</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${fileList}" status="i" var="file">
                    <tr class="${( i % 2 ) == 0 ? 'even' : 'odd'}">
                        <td>
                            <g:set var="downloadURL"
                                   value="http://${grailsApplication.config.omar.serverIP}/${file.name}"/>
                            <a href="#" onclick="javascript:window.open( '${downloadURL}' )">${file.name}</a>
                        </td>
                        <td>${file.size}</td>
                        <td>${file.date}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
</content>
</body>
</html>