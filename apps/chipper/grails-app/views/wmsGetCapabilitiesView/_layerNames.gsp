<h2>LayerNames</h2>
<ul>
    <g:each var="layer" in="${wmsGetCaps.layers}">
        <li>${layer.name}</li>
    </g:each>
</ul>