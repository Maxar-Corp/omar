OMAR.models.FederationReconnectModel = Backbone.Model.extend({
   url:"/omar/federation/reconnect",
    defaults:{
        connected:"false"
    }
});

OMAR.models.FederationSettingsModel = Backbone.Model.extend({
    url:"/omar/configSettings/federation",
    idAttribute:"name",
    defaults:{
        name:"",
        settings:{
            "vcard":{nickName:"", firstName:"",lastName:""},
            "server":{ip:"",port:"5222",username:"",password:""},
            "chatRoom":{id:"", password:"", enabled:false}
        }
    },
    parse:function(jsonResponse){
        var result = {name:jsonResponse.name};
        result.settings = JSON.parse(jsonResponse.settings);
        if(!result.settings)
        {
            result.settings = {};
        }
        if(!result.settings.vcard)
        {
            result.settings.vcard = {nickName:"", firstName:"",lastName:""};
        }
        if(!result.settings.server)
        {
            result.settings.server = {ip:"",port:"5222",username:"",password:""};
        }
        if(!result.settings.chatRoom)
        {
            result.settings.chatRoom = {id:"", password:"", enabled:false};
        }
        return result;
    }
});

OMAR.views.FederationAdmin = Backbone.View.extend({
    el:"#federatedAdminPageId",
    initialize:function(params){
        if(params)
        {
            this.model=params.model
        }
        if(!this.model){
            this.model = new  OMAR.models.FederationSettingsModel();
        }

        this.model.bind("change", this.modelChanged, this);
        this.omarFederationVcardNickName  = $(this.el).find("#OmarFederationVcardNickName");
        this.omarFederationVcardFirstName      = $(this.el).find("#OmarFederationVcardFirstName");
        this.omarFederationVcardLastName       = $(this.el).find("#OmarFederationVcardLastName");
        this.omarFederationServerIp            = $(this.el).find("#OmarFederationServerIp");
        this.omarFederationServerPort          = $(this.el).find("#OmarFederationServerPort");
        this.omarFederationServerAdminUsername = $(this.el).find("#OmarFederationServerAdminUsername");
        this.omarFederationServerAdminPassword = $(this.el).find("#OmarFederationServerAdminPassword");
        this.omarFederationChatRoomId          = $(this.el).find("#OmarFederationChatRoomId");
        this.omarFederationChatRoomPassword    = $(this.el).find("#OmarFederationChatRoomPassword");
        this.omarFederationChatRoomEnabled     = $(this.el).find("#OmarFederationChatRoomEnabled");

        this.refreshButton                      = $(this.el).find("#RefreshId");
        this.applyButton                     = $(this.el).find("#ApplyId");
       // this.disconnectButton                  = $(this.el).find("#DisconnectId");

        this.dirty = false;

        $(this.omarFederationVcardNickName).change(this.nickNameChanged.bind(this));
        $(this.omarFederationVcardFirstName).change(this.firstNameChanged.bind(this));
        $(this.omarFederationVcardLastName).change(this.lastNameChanged.bind(this));

        $(this.omarFederationServerIp).change(this.serverIpChanged.bind(this));
        $(this.omarFederationServerPort).change(this.serverPortChanged.bind(this));
        $(this.omarFederationServerAdminUsername).change(this.serverUsernameChanged.bind(this));
        $(this.omarFederationServerAdminPassword).change(this.serverPasswordChanged.bind(this));

        $(this.omarFederationChatRoomId).change(this.chatRoomIdChanged.bind(this));
        $(this.omarFederationChatRoomPassword).change(this.chatRoomPasswordChanged.bind(this));
        $(this.omarFederationChatRoomEnabled).click(this.chatRoomEnabledChanged.bind(this));

        $(this.refreshButton).click(this.reloadClicked.bind(this));
        $(this.applyButton).click(this.applyClicked.bind(this));
        //$(this.disconnectButton).click(this.disconnectClicked.bind(this));

        // force a reload from server on reload of page
        this.reloadClicked();
    },
    nickNameChanged:function(){
        var settings = this.model.get("settings");
        //alert("DOING NICK CHANGE " + JSON.stringify(settings));
        settings.vcard.nickName = $(this.omarFederationVcardNickName).val();
        this.model.set("settings", settings);
    },
    firstNameChanged:function(){
        var settings = this.model.get("settings");

        settings.vcard.firstName = $(this.omarFederationVcardFirstName).val();
        this.model.set("settings", settings);
    },
    lastNameChanged:function(){
        var settings = this.model.get("settings");

        settings.vcard.lastName = $(this.omarFederationVcardLastName).val();
        this.model.set("settings", settings);
    },
    serverIpChanged:function(){
        var settings = this.model.get("settings");
        settings.server.ip = $(this.omarFederationServerIp).val();
        this.model.set("settings", settings);
    },
    serverPortChanged:function(){
        var settings = this.model.get("settings");
        settings.server.port = $(this.omarFederationServerPort).val();
        this.model.set("settings", settings);
    },
    serverUsernameChanged:function(){
        var settings = this.model.get("settings");
        settings.server.username = $(this.omarFederationServerAdminUsername).val();
        this.model.set("settings", settings);
    },
    serverPasswordChanged:function(){
        var settings = this.model.get("settings");
        settings.server.password = $(this.omarFederationServerAdminPassword).val();
        this.model.set("settings", settings);
    },
    chatRoomIdChanged:function(){
        var settings = this.model.get("settings");
        settings.chatRoom.id = $(this.omarFederationChatRoomId).val();
        this.model.set("settings", settings);
    },
    chatRoomPasswordChanged:function(){
        var settings = this.model.get("settings");
        settings.chatRoom.password = $(this.omarFederationChatRoomPassword).val();
        this.model.set("settings", settings);
    },
    chatRoomEnabledChanged:function(){
        var checked = $(this.omarFederationChatRoomEnabled).attr("checked")=="checked";
        var settings = this.model.get("settings");
        settings.chatRoom.enabled = checked;
        this.model.set("settings", settings);
    },
    reloadClicked:function(){
        this.model.fetch({success:function(){},
            update: true, remove: false,date:{cache:false}});
        //alert("reloadClicked");
    },
    applyClicked:function(){
        if(this.dirty)
        {
            var spinner = new Spinner(OMAR.defaultSpinnerOptions);

            spinner.spin($(this.el)[0]);
            this.model.save(this.model.attributes,
                {
                    success:function(){
                        federationReconnectModel = new OMAR.models.FederationReconnectModel();

                        federationReconnectModel.fetch({
                            success:function(){
                                spinner.stop();
                                if(federationReconnectModel.get("connected"))
                                {
                                    alert("New settings applied and Server connected");
                                }
                                else
                                {
                                    alert("New settings applied and server not connect");
                                }
                            },
                            error:function(){
                                spinner.stop();
                                alert("New settings could not be applied");
                            },
                                                update: true,
                                                remove: false,
                                                date:{cache:false}});
                    },
                    error:function(){alert("New settings could not be applied");spinner.stop();},
                    update: true, remove: false,date:{cache:false}
                });
            this.dirty = false;
        }
       // alert("connectClicked");
    },
    disconnectClicked:function(){
        alert("disconnectClicked");
    },
    modelChanged:function(){
        this.dirty = true;
        this.render();
    },
    applyChanges:function(){

    },
    render:function(){
        var settings = this.model.get("settings");
        // alert("------------- " + JSON.stringify(settings));
        $(this.omarFederationVcardNickName).val(settings.vcard.nickName);
        $(this.omarFederationVcardFirstName).val(settings.vcard.firstName);
        $(this.omarFederationVcardLastName).val(settings.vcard.lastName);
        $(this.omarFederationServerIp).val(settings.server.ip);
        $(this.omarFederationServerPort).val(settings.server.port);
        $(this.omarFederationServerAdminUsername).val(settings.server.username);
        $(this.omarFederationServerAdminPassword).val(settings.server.password);
        $(this.omarFederationChatRoomId).val(settings.chatRoom.id);
        $(this.omarFederationChatRoomPassword).val(settings.chatRoom.password);

        $(this.omarFederationChatRoomEnabled).attr('checked',settings.chatRoom.enabled);
    }
});


OMAR.FederationAdminPage = null;
OMAR.pages.FederationAdmin = (function($, params){
    OMAR.FederationAdminPage = new OMAR.views.FederationAdmin(params);
    return OMAR.FederationAdminPage;
});

$(document).ready(function () {
    init();
});