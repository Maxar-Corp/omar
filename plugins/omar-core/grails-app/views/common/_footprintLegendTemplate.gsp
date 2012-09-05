<div class="niceBox">
    <div class="niceBoxHd">Footprint Legend:</div>

    <div class="niceBoxBody">
        <table>
            <g:each var="entry" in="${style?.outlineLookupTable}">
                <tr>
                    <td bgcolor="${entry?.value?.encodeAsHexColor()}">&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>${entry?.key}</td>
                </tr>
            </g:each>
        </table>
    </div>
</div>