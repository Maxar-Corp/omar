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

      <g:form controller="RunScript" action="indexFiles" method="post">
      <tr bgcolor="#cccccc">
          <td>omarRunScript</td>
          <td><g:select name="threads" from="${1..grailsApplication.config.stager.scripts.maxThreadCount}" value="${grailsApplication.config.stager.scripts.defaultThreadCount}" /></td>
          <td><g:textField  style="width:100%" name="runScriptIndexFilesArgs" value="${runScriptIndexFilesArgs}" /></td>
          <td>indexFiles</td>
          <td><g:textField  style="width:100%" name="indexFilesArgs" value="${indexFilesArgs}" /></td>
          <td><span class="button"><input type="submit" value="Index Files" /></span></td>
      </tr>   
      </g:form>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>

      <g:form controller="RunScript" action="stageRaster" method="post">
      <tr bgcolor="#cccccc">
          <td>omarRunScript</td>
          <td><g:select name="threads" from="${1..grailsApplication.config.stager.scripts.maxThreadCount}" value="${grailsApplication.config.stager.scripts.defaultThreadCount}" /></td>
          <td><g:textField  style="width:100%" name="runScriptStageRasterArgs" value="${runScriptStageRasterArgs}" /></td>
          <td>stageRaster</td>
        <td><g:textField name="stageRasterArgs" style="width:100%" value="${stageRasterArgs}" /></td>
        <td><span class="button"><input type="submit" value="Stage Raster" /></span></td>
      </tr>   
      </g:form>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
      </tr>

      <g:form controller="RunScript" action="removeRaster" method="post">
      <tr bgcolor="#cccccc">
          <td>omarRunScript</td>
          <td></td>
          <td><g:textField  style="width:100%" name="runScriptRemoveRasterArgs" value="${runScriptRemoveRasterArgs}" /></td>
          <td>removeRaster</td>
           <td><g:textField name="removeRasterArgs" style="width:100%" value="${removeRasterArgs}" /></td>
        <td><span class="button"><input type="submit" value="Remove Raster" /></span></td>
      </tr>   
      </g:form>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
      </tr>

      <g:form controller="RunScript" action="synchFiles" method="post">
      <tr bgcolor="#cccccc">
          <td>omarRunScript</td>
          <td></td>
          <td><g:textField  style="width:100%" name="runScriptSynchFilesArgs" value="${runScriptSynchFilesArgs}" /></td>
          <td>synchFiles</td>
          <td></td>
          <td><span class="button"><input type="submit" value="Synch Files" /></span></td>
      </tr>   
      </g:form>

      <tr bgcolor="#999999">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
      </tr>

      <g:form controller="RunScript" action="clearCache" method="post">
      <tr bgcolor="#cccccc">
          <td></td>
          <td></td>
          <td></td>
          <td>clearCache</td>
        <td></td>
        <td><span class="button"><input type="submit" value="Clear Cache" /></span></td>
      </tr>   
      </g:form>
    </table>

      <div id="jobTableDivId">
        <h1>Current Jobs (Updated every 5 seconds...)</h1>
      </div>
  </div>

  </content>

<r:script>
    var jobTriggers = ${jobTriggers}

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

    }
</r:script>
</body>
</html>