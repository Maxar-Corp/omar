<div id="FederationAdminId">
    <div>
        <div class="niceBox">
            <div  class="niceBoxHeader" ><h2>V-Card Profile</h2></div>
            <table>
                <tr>
                    <td>Nickname</td>
                    <td><input id="OmarFederationVcardNickName" type="text"></input></td>
                </tr>
                <tr>
                    <td>First Name</td>
                    <td><input id="OmarFederationVcardFirstName" type="text"></input></td>
                </tr>
                <tr>
                    <td>Last Name</td>
                    <td><input id="OmarFederationVcardLastName" type="text"></input></td>
                </tr>
            </table>
        </div>
        <div class="niceBox">
            <div  class="niceBoxHeader"><h2>Chat Server Settings</h2></div>
            <table>
                <tr>
                    <td>Ip</td>
                    <td><input id="OmarFederationServerIp" type="text"></input></td>
                </tr>
                <tr>
                    <td>Domain</td>
                    <td><input id="OmarFederationServerDomain" type="text"></input></td>
                </tr>
                <tr>
                    <td>Port</td>
                    <td><input id="OmarFederationServerPort" type="text" value="5222"></input></td>
                </tr>
                <tr>
                    <td>Username</td>
                    <td><input id="OmarFederationServerUsername" type="text"></input></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><input id="OmarFederationServerPassword" type="password"></input></td>
                </tr>
            </table>
        </div>
        <div class="niceBox">
            <div  class="niceBoxHeader"><h2>Federated Chat Room Settings</h2></div>
            <table>
                <thead>
                <tr>
                    <th>Field </th>
                    <th>Value </th>
                <tr>
                </thead>
                <tr>
                    <td>Room Id</td>
                    <td><input id="OmarFederationChatRoomId" type="text"></input></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><input id="OmarFederationChatRoomPassword" type="password"></input></td>
                </tr>
                <tr>
                    <td>Enabled</td>
                    <td><input id="OmarFederationChatRoomEnabled" type="checkbox"></input></td>
                </tr>
            </table>
        </div>
    </div>
     <div class="niceBox" style="text-align:center">
         <button id="RefreshId">Refresh</button>
         <button id="ApplyId">Apply</button>
         <!--<button id="DisconnectId">Disconnect</button>-->
     </div>
</div>