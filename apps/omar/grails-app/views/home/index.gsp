<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Welcome to OMAR <g:meta name="app.version"/></title>
    <meta name="layout" content="homePageLayout"/>
    <asset:stylesheet src="homePage.css"/>
</head>

<body class="yui-skin-sam">
<content tag="top">
    <div align="center">
        <g:link class="home" uri="/">
            <asset:image src="omarLogo.png" alt="OMAR Logo"/>
        </g:link>
    </div>

</content>

<content tag="center">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div>
        <h1 style="font-size:150%" align="left">Search:</h1>
        <table>
            <tr>
                <td width="120px">
                    <a href="${createLink( controller: 'federation', action: 'search' )}">
                        <asset:image src="discover.gif"/>
                    </a>
                </td>
                <td align="center">
                    <g:link plugin="omar-federation" controller="federation" action="search">Search</g:link>
                </td>
            </tr>
            <tr>
                <td width="120px">
                    <a href="${createLink( controller: 'imageList', action: index )}">
                        <asset:image src="discover.gif"/>
                    </a>
                </td>
                <td align="center">
                    <g:link plugin="omar-chipper" controller="imageList" action="index">Product Builder</g:link>
                </td>
            </tr>

            <g:each in="${grailsApplication.config.views?.home?.search?.additionalLinks}" var="searchLink">
                <tr>
                    <td width="120px">
                        <g:if test="${searchLink.url}">
                            <a href="${searchLink.url}">
                                <g:if test="${searchLink.img}">
                                    <img src="${searchLink.img}" alt="">
                                </g:if>
                                <g:else>
                                    <asset:image src="discover.gif"/>
                                </g:else>
                            </a>
                        </g:if>
                    </td>
                    <td align="center">
                        <a href="${searchLink.url}">${searchLink.label}</a>
                    </td>
                </tr>

            </g:each>
        </table>
    </div>
    <!--
    <g:if test="${grailsApplication.config.views?.home?.browseEnabled}">
        <div>
          <h1 style="font-size:150%" align="left">Browse:</h1>
          <table>
            <tr>
              <td width="120px">
                <asset:image src="globe_128.gif" width="96" height="96"/>
            </td>
        <td>
          <ol>
            <li><g:link controller="rasterEntry" action="results">Imagery</g:link></li>
              <li><g:link controller="videoDataSet" action="results">Video</g:link></li>
            </ol>
          </td>
        </tr>
      </table>
    </div>
    </g:if>
    -->
    <div>
        <h1 style="font-size:150%" align="left">KML Network Links:</h1>
        <table>
            <tr>
                <td width="120px">
                    <asset:image src="GoogleEarth_1.gif" width="96" height="96"/>
                </td>
                <td>
                    <ol>
                        <li>
                            <g:form name="imageView"
                                    url="[action: 'topImages', controller: 'rasterKmlQuery', params: [stretch_mode: 'linear_auto_min_max', stretch_mode_region: 'global']]">
                                <g:textField name="maximages" size="2"
                                             value="${grailsApplication.config.kml.defaultImages}"/>
                                <g:submitButton name="submit" value="Most Recent Images for View"/>
                            </g:form>
                        </li>
                        <li>
                            <g:form name="videoView" url="[controller: 'videoKmlQuery', action: 'topVideos']">
                                <g:textField name="maxvideos" size="2"
                                             value="${grailsApplication.config.kml.defaultVideos}"/>
                                <g:submitButton name="submit" value="Most Recent Videos for View"/>
                            </g:form>
                        </li>
                        <li>
                            <g:form name="imageFootprints"
                                    url="[controller: 'rasterKmlQuery', action: 'imageFootprints']">
                                <g:textField name="imagedays" size="2"
                                             value="${grailsApplication.config.kml.daysCoverage}"/>
                                <g:submitButton name="submit" value="Most Recent Days Imagery Coverage"/>
                            </g:form>
                        </li>
                        <li>
                            <g:form name="videoFootprints"
                                    url="[controller: 'videoKmlQuery', action: 'videoFootprints']">
                                <g:textField name="videodays" size="2"
                                             value="${grailsApplication.config.kml.daysCoverage}"/>
                                <g:submitButton name="submit" value="Most Recent Days Video Coverage"/>
                            </g:form>
                        </li>
                    </ol>
                </td>
            </tr>
        </table>
    </div>

    <div>
        <h1 style="font-size:150%" align="left">GeoRSS Links:</h1>
        <table>
            <tr>
                <td width="120px">
                    <asset:image src="georss-1.gif" width="96" height="96"/>
                </td>
                <td>
                    <ol>
                        <li>
                            <g:form name="ccRss" method="GET"
                                    url="[plugin: 'omar-rss', controller: 'rssFeed', action: 'georss']">
                                <g:submitButton name="submit" value="By Country Code"/>
                                <g:textField name="cc" size="2"/>
                            </g:form>
                        </li>
                        <li>
                            <g:form name="beRss" method="GET"
                                    url="[plugin: 'omar-rss', controller: 'rssFeed', action: 'georss']">
                                <g:submitButton name="submit" value="By BE Number"/>
                                <g:textField name="be" size="10"/>
                            </g:form>
                        </li>
                    </ol>
                </td>
            </tr>
        </table>
    </div>


    <sec:ifNotGranted roles="ROLE_ADMIN">
        <div>
            <h1 style="font-size:150%" align="left">User Preferences:</h1>
            <table>
                <tr>
                    <td width="120px">
                        <asset:image src="use.gif" width="96" height="96"/>
                    </td>
                    <td>
                        <ol>
                            <li><g:link controller="userPreferences" action="editProfile"
                                        id="${user?.id}">Edit Profile</g:link></li>
                            <li><g:link controller="userPreferences" action="changePassword"
                                        id="${user?.id}">Change Password</g:link></li>
                        </ol>
                    </td>
                </tr>
            </table>
        </div>
    </sec:ifNotGranted>
    <div>
        <h1 style="font-size:150%" align="left">Report:</h1>
        <table>
            <tr>
                <td width="120px">
                    <asset:image src="report.gif" width="96" height="96"/>
                </td>
                <td>
                    <ol>
                        <g:if test="${feedback.enabled}">
                            <li>
                                <a href="mailto:${feedback.mailto}?subject=${feedback.subject}&body=">User Feedback</a>

                            </li>
                        </g:if>

                    <%--<sec:ifAllGranted roles="ROLE_ADMIN">--%>
                        <li>
                            <g:link plugin="omar-raster" controller="GetTileLog"
                                    action="list">View Image Space Log</g:link>
                        </li>
                        <li>
                            <g:link plugin="omar-ogc" controller="WmsLog" action="list">View WMS Log</g:link>
                        </li>
                        <li>
                            <g:link controller="Job" action="index">View Jobs</g:link>
                        </li>
                        <%--</sec:ifAllGranted>--%>
                    </ol>
                </td>
            </tr>
        </table>
    </div>

    <div>
        <h1 style="font-size:150%" align="left">Logout</h1>
        <table>
            <tr>
                <td>
                    <a href="${createLink( controller: 'logout' )}">
                        <asset:image src="logout.gif" width="96" height="96"/>
                    </a>
                </td>
            </tr>
        </table>
    </div>

    <sec:ifAllGranted roles="ROLE_ADMIN">
        <div>
            <h1 style="font-size:150%" align="left">User Management:</h1>
            <table>
                <tr>
                    <td width="120px">
                        <asset:image src="use.gif" width="96" height="96"/>
                    </td>
                    <td>
                        <ol>
                            <li><g:link controller="secUser" action="index">User</g:link></li>
                            <li><g:link controller="secRole" action="index">Roles</g:link></li>
                            <li><g:link controller="requestmap" action="index">Permissions</g:link></li>
                        </ol>
                    </td>
                </tr>
            </table>
        </div>

        <div>
            <h1 style="font-size:150%" align="left">Admin:</h1>
            <table>
                <tr>
                    <td width="120px">
                        <asset:image src="extend.gif" width="96" height="96"/>
                    </td>
                    <td>
                        <ol>
                            <li><g:link controller="Federation" action="admin">Federation</g:link></li>
                            <li><g:link controller="RunScript" action="scripts">Scripts</g:link></li>
                            <li><g:link controller="ChipFormat" action="list">Chip Formats</g:link></li>
                            <li><g:link controller="DiskCache" action="index">View Disk Cache Locations</g:link></li>
                            <br/>
                            <g:each var="c" in="${editableControllers}">
                                <g:if test="${!c.name.equals( 'ConfigSettings' ) && !c.name.equals( 'DiskCache' ) && !c.name.equals( 'ChipFormat' )}">
                                    <li><g:link controller="${c.path}">${c.name}</g:link></li>
                                </g:if>
                            </g:each>
                        </ol>
                    </td>
                </tr>
            </table>
        </div>
    </sec:ifAllGranted>
</content>
<asset:javascript src="homePage.js"/>
</body>
</html>
