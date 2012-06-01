<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>

<form action="/omar/rasterEntry/index" method="post" name="paginateForm" id="paginateForm" ></form>

<div class="pagination">
  <g:paginate event="testing('tabView');" controller="rasterEntry" action="results" total="${totalCount ?: 0}"
              max="${params.max}" offset="${params.offset}" params="${params}"/>

  <g:if test="${totalCount == 0}">
  </g:if>
  <g:else>
    <input type="text" id="pageOffset" size="3" onchange="updateOffset();"/>
    <button type="button" onclick="javascript:updateOffset();">Go to Page</button>
    <label for="max">Max:</label>
    <input type="text" id="max" name="max" value="${params.max}" onChange="updateMaxCount()"/>
    <button type="button" onclick="javascript:updateMaxCount();">Set</button>
  </g:else>
</div>


<g:hiddenField id="offset" name="offset" value="${params.offset}"/>
<g:hiddenField id="totalCount" name="totalCount" value="${totalCount ?: 0}"/>
<g:hiddenField name="order" value="${params.order}"/>
<g:hiddenField name="sort" value="${params.sort}"/>

<g:hiddenField name="queryParams" value="${queryParams?.toMap()}"/>
