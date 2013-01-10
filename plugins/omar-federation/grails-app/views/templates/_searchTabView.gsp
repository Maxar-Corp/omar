<div id="tabView" class="tabs">
    <ul>
        <li><a href="#customQuery">Custom Query</a></li>
        <li><a href="#MapView">Map</a></li>
        <li><a href="#Results">Results</a></li>
    </ul>
    <div id="customQuery">Custom Query</div>
    <div id="MapView">
        <g:render plugin="omar-common-ui" template="/templates/mapTemplate"/>
    </div>
    <div id="Results">
        <table id="DataTable">
            <thead>
            <tr>
                <th>Id</th>
                <th>Width</th>
                <th>Height</th>
                <th>Image Id</th>
                <th>Sensor Id</th>
            </tr>
            </thead>
            <tr>
                <td>1</td>
                <td>1000</td>
                <td>1000</td>
                <td>ABCDEFG1</td>
                <td>CoolSensor</td>
            </tr>
            <tfoot>
            <tr>
                <th>Id</th>
                <th>Width</th>
                <th>Height</th>
                <th>Image Id</th>
                <th>Sensor Id</th>
            </tr>
            </tfoot>

        </table>
    </div>

</div>