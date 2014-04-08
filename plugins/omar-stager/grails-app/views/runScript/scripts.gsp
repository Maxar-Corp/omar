<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="scriptsPageLayout"/>
    <r:require modules="scriptsPageLayout"/>
    <title>OMAR: Scripts</title>
</head>
<body>
<content tag="content">
    <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link controller="Repository" class="create" action="create">Create Repository</g:link></li>
          <li class="menuButton"><g:link class="list" controller="Repository" action="list">Repository List</g:link></li>
      </ul>
  </div>
  <div class="body">
<g:form name="formPostId" controller="RunScript" action="" method="post"></g:form>

<h1>OMAR: Staging Scripts</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>

    <table border="1"  cellpadding="10">
      <tr bgcolor="#666666">
          <td><font color="#ffffff"><b>Run Script</b></font></td>
          <td><font color="#ffffff"><b>Number of Threads</b></font></td>
          <td><font color="#ffffff"><b>Run Script Args</b></font></td>
          <td><font color="#ffffff"><b>Script</b></font></td>
          <td><font color="#ffffff"><b>Script Args</b></font></td>
          <td><font color="#ffffff"><b>Execute</b></font></td>
      </tr>

      <tr bgcolor="#cccccc">
              <td>omarRunScript</td>
              <td><g:select id="indexFilesThreadCountId" name="threads" from="${1..grailsApplication.config.stager.scripts.maxThreadCount}" value="${grailsApplication.config.stager.scripts.defaultThreadCount}" /></td>
              <td><g:textField  style="width:100%" id="runScriptIndexFilesArgsId" name="runScriptIndexFilesArgs" value="${runScriptIndexFilesArgs}" /></td>
              <td>indexFiles</td>
              <td><g:textField  style="width:100%" id="indexFilesArgsId" name="indexFilesArgs" value="${indexFilesArgs}" /></td>
              <td><span class="button"><input type="button" onclick="submitIndexFiles()" value="Index Files" /></span></td>
      </tr>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr bgcolor="#cccccc">
            <td>omarRunScript</td>
            <td><g:select name="stageRasterThreadCountId" from="${1..grailsApplication.config.stager.scripts.maxThreadCount}" value="${grailsApplication.config.stager.scripts.defaultThreadCount}" /></td>
            <td><g:textField  style="width:100%" id="runScriptStageRasterArgsId" name="runScriptStageRasterArgs" value="${runScriptStageRasterArgs}" /></td>
            <td>stageRaster</td>
            <td><g:textField id="stageRasterArgsId" name="stageRasterArgs" style="width:100%" value="${stageRasterArgs}" /></td>
            <td><span class="button"><input type="button" onclick="submitStageRaster()" value="Stage Raster" /></span></td>
      </tr>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
      </tr>

    <tr bgcolor="#cccccc">
            <td>omarRunScript</td>
            <td></td>
            <td><g:textField  style="width:100%" id="runScriptRemoveRasterArgsId" name="runScriptRemoveRasterArgs" value="${runScriptRemoveRasterArgs}" /></td>
            <td>removeRaster</td>
            <td><g:textField id="removeRasterArgsId" name="removeRasterArgs" style="width:100%" value="${removeRasterArgs}" /></td>
            <td><span class="button"><input type="button" onclick="submitRemoveRaster()" value="Remove Raster" /></span></td>
    </tr>

        <tr bgcolor="#999999">
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
            <tr bgcolor="#cccccc">
                    <td>omarRunScript</td>
                    <td></td>
                    <td><g:textField  style="width:100%" id="runScriptRemoveVideoArgsId" name="runScriptRemoveVideoArgs" value="${runScriptRemoveVideoArgs}" /></td>
                    <td>removeVideo</td>
                    <td><g:textField id="removeVideoArgsId" name="removeVideoArgs" style="width:100%" value="${removeVideoArgs}" /></td>
                    <td><span class="button"><input type="button" onclick="submitRemoveVideo()" value="Remove Video" /></span></td>
            </tr>

        <tr bgcolor="#999999">
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>

      <tr bgcolor="#cccccc">
          <td>omarRunScript</td>
          <td></td>
          <td><g:textField  style="width:100%" id="runScriptSynchFilesArgsId" name="runScriptSynchFilesArgs" value="${runScriptSynchFilesArgs}" /></td>
          <td>synchFiles</td>
          <td></td>
          <td><span class="button"><input type="button" onclick="submitSynchFiles()" value="Synch Files" /></span></td>
      </tr>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
      </tr>

      <tr bgcolor="#cccccc">
          <td></td>
          <td></td>
          <td></td>
          <td>clearCache</td>
        <td></td>
        <td><span class="button"><input type="button" onclick="submitClearCache()" value="Clear Cache" /></span></td>
      </tr>
    </table>
      <div id="jobTableDivId">
        <h1>Current Jobs (Updated every 5 seconds...)</h1>
      </div>
  </div>

  </content>

<r:script>
    var jobTriggers = ${jobTriggers}

    function submitIndexFiles(){
        var formPost = document.getElementById("formPostId");

        var threads = document.getElementById("indexFilesThreadCountId");
        var runScriptIndexFilesArgs = document.getElementById("runScriptIndexFilesArgsId");
        var indexFilesArgs = document.getElementById("indexFilesArgsId");
        formPost.action="/omar/RunScript/indexFiles?threads="+threads.value+
                              "&runScriptIndexFilesArgs="+runScriptIndexFilesArgs.value +
                              "&indexFilesArgs="+indexFilesArgs.value;
        formPost.submit();
    }
    function submitStageRaster(){
        var formPost = document.getElementById("formPostId");
        var threads = document.getElementById("stageRasterThreadCountId");
        var runScriptStageRasterArgs = document.getElementById("runScriptStageRasterArgsId");
        var stageRasterArgs = document.getElementById("stageRasterArgsId");
        formPost.action="/omar/RunScript/stageRaster?threads="+threads.value+
                              "&runScriptStageRasterArgs="+runScriptStageRasterArgs.value +
                              "&stageRasterArgs="+stageRasterArgs.value;
        formPost.submit();
    }
    function submitRemoveRaster(){
        var formPost = document.getElementById("formPostId");
        var runScriptRemoveRasterArgs = document.getElementById("runScriptRemoveRasterArgsId");
        var removeRasterArgs = document.getElementById("removeRasterArgsId");
        formPost.action="/omar/RunScript/removeRaster?runScriptRemoveRasterArgs="+runScriptRemoveRasterArgs.value +
                              "&removeRasterArgs="+removeRasterArgs.value;
        formPost.submit();

    }
    function submitRemoveVideo(){
        var formPost = document.getElementById("formPostId");
        var runScriptRemoveVideoArgs = document.getElementById("runScriptRemoveVideoArgsId");
        var removeVideoArgs = document.getElementById("removeVideoArgsId");
        formPost.action="/omar/RunScript/removeVideo?runScriptRemoveVideoArgs="+runScriptRemoveVideoArgs.value +
                              "&removeVideoArgs="+removeVideoArgs.value;
        formPost.submit();

    }
    function submitSynchFiles(){
        var formPost = document.getElementById("formPostId");
        var runScriptSynchFilesArgs = document.getElementById("runScriptSynchFilesArgsId");
        formPost.action="/omar/RunScript/synchFiles?runScriptSynchFilesArgs="+runScriptSynchFilesArgs.value;
        formPost.submit();
    }
    function submitClearCache(){
        var formPost = document.getElementById("formPostId");
        formPost.action="/omar/RunScript/clearCache";
        formPost.submit();
    }
    function clearTable(tableId)
    {
        var el = YAHOO.util.Dom.get(tableId);
        if(el)
        {
             for(var i = el.rows.length; i > 0;i--)
             {
                  el.deleteRow(i - 1);
             }
        }
        return (el!=null);
    }
    function deleteTable(tableId)
    {
        var el = YAHOO.util.Dom.get(tableId);

        if(el)
        {
            el.parentNode.removeChild(el);
        }
        return (el!=null);
    }
    function renderTable(jobs)
    {
        deleteTable("jobTableId");
        if(jobs&&jobs.labels)
        {
            var tableDiv = YAHOO.util.Dom.get(jobTableDivId);
            var jobTable=document.createElement('table');
            var jobTableBody=document.createElement('tbody');
            var row, cell;

            jobTable.id="jobTableId";
            row=document.createElement('tr');
            var idx = 0;
            for(idx = 0; idx <jobs.labels.length;++idx)
            {
                cell=document.createElement('td');
                cell.appendChild(document.createTextNode(jobs.labels[idx]));
                row.appendChild(cell);
            }
            jobTableBody.appendChild(row);
            for(idx = 0; idx <jobs.rows.length;++idx)
            {
                row=document.createElement('tr');
                var cellIdx = 0;
                for(cellIdx = 0; cellIdx <jobs.labels.length;++cellIdx)
                {
                    cell=document.createElement('td');
                    cell.appendChild(document.createTextNode(jobs.rows[idx][cellIdx]));
                    row.appendChild(cell);
                    jobTableBody.appendChild(row);
                }
            }
            jobTable.appendChild(jobTableBody);
            tableDiv.appendChild(jobTable);
        }
    }
    function getJobs()
    {
        var link = "${createLink(action:'jobs', params: [time: new Date().time])}";
        new Ajax.Request(link, {
            method: 'get',
            onSuccess: function(transport) {
                 renderTable(YAHOO.lang.JSON.parse(transport.responseText));
                 setTimeout(getJobs,5000);
            }
        });
    }
    function init()
    {
       renderTable(jobTriggers);
       setTimeout(getJobs,5000);

       var tableDiv = YAHOO.util.Dom.get(jobTableDivId);
    }
</r:script>
</body>
</html>