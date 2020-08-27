
const COMMUNICATION_TYPE ={
    GROUP_CHAT: 'G',
    MATRIX_CHAT: 'M',
    DYADIC : 'D'
}

const CHAT_TYPE = {
    JOINED: "JOINED",
    MESSAGE : "MESSAGE",
    IS_TYPING: "IS_TYPING",
    STATUS: "STATUS",
    REQUEST_CHAT : "REQUEST_CHAT",
    HANG_UP_CHAT: "HANG_UP_CHAT",
    ACCEPT_CHAT: "ACCEPT_CHAT",

};
const STATUS = {
    AVAILABLE: "AVAILABLE",
    BUSY: "BUSY",
    UNAVAILABLE: "UNAVAILABLE"
}

const CHAT_BODY = {
    GROUP: {htmlRef: "group", displayString: "Group chat"},
    INSTRUCTIONS: {htmlRef: "instructions", displayString: "Instructions"},

    REQUEST_SENT: {htmlRef: "requestsent", displayString: "Waiting ..."},
    REQUEST_RECEIVED: {htmlRef: "requestreceived", displayString: "Incoming request"},


}


class CommunicationPlugin extends PogsPlugin {
    constructor(pogsRef){
        super('communicationPlugin', null, pogsRef);
        this.initFunc = this.init;
    }
    init(){
            if (this.pogsRef.communicationType == COMMUNICATION_TYPE.GROUP_CHAT) {
                var gcm = new GroupChatManager(this);
                gcm.changeChannelTo(CHAT_BODY.GROUP.htmlRef, CHAT_BODY.GROUP.displayString, null);
            }
            if (this.pogsRef.communicationType == COMMUNICATION_TYPE.DYADIC) {
                var dcm = new DyadicChatManager(this);
                dcm.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef, CHAT_BODY.INSTRUCTIONS.displayString,null);

            }
            if(this.pogsRef.communicationType == COMMUNICATION_TYPE.MATRIX_CHAT) {
                var mtm = new MatrixChatManager(this);
                mtm.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef, CHAT_BODY.INSTRUCTIONS.displayString,null);
            }


    }
    getChatBotName(){
        return this.pogsRef.chatBotName;
    }
    getChannelsSubjectIsIn(){
        return this.pogsRef.channelSubjectIsIn;
    }
    getSubjectCanTalkToSubjects(){
        return this.pogsRef.subjectCanTalkTo;
    }
    subscribeCommunicationBroadcast(funct) {
            this.pogsRef.subscribe('communicationMessage', funct);
    }
    sendMessage(message, channel, type, receiver) {

            var messageContent = {
                message: message,
                type: type,
                channel: channel
            };

            this.pogsRef.sendMessage("/pogsapp/communication.sendMessage", "COMMUNICATION_MESSAGE",
                                     messageContent,
                                     this.getSubjectId(), receiver, this.getCompletedTaskId(),
                                     this.getSessionId());
        }
}


class GroupChatManager {
    constructor(communicationPluginReference) {
        this.channelReceiver = null;
        this.communicationPluginReference = communicationPluginReference;
        this.subjectsInChannel = communicationPluginReference.getTeammates();
        this.setupSubjectPanel();

        this.lastMessageAuthor = null;
        this.shouldHideAuthorInConsecutive = true;
        this.sendJoinedMessage();
        this.setupHTML();
        this.communicationPluginReference.
            subscribeCommunicationBroadcast(this.onCommunicationBroadcastReceived.bind(this));

    }
    setupSubjectPopovers() {

        for(var i = 0; i < this.subjectsInChannel.length; i ++) {
            var properties = "";

            for (let j = 0; j < this.subjectsInChannel[i].attributes.length; j++) {

                if(!this.subjectsInChannel[i].attributes[j].internalAttribute) {
                    properties +=
                        "<h5><b>" + this.subjectsInChannel[i].attributes[j].attributeName + ": </b>"
                        + this.subjectsInChannel[i].attributes[j].stringValue + "</h5>"
                }
            }

            if(properties != "") {
                $('.' + this.subjectsInChannel[i].externalId + '_popover').popover(
                    {
                        title: "<h4><strong class='" + this.subjectsInChannel[i].externalId
                               + "_color'>" +
                               this.subjectsInChannel[i].displayName + "</strong> properties</h4>",
                        content: properties, html: true, placement: "bottom",

                        trigger: "hover", offset: 9000, flip: false, container: 'body'
                    });
            }


        }

    }
    changeChannelTo(channelName, displayName, receiver){
        this.channel = channelName;
        this.channelBodyRef = this.getOrCreateChannelHTML(this.channel);
        $(".channelBody").addClass("d-none")
        this.channelBodyRef.removeClass("d-none");
        this.channelBodyRef.show();
        receiver = receiver || null;
        this.channelReceiver = receiver;
        displayName = displayName || '';
        $("#channelDisplayName").text(displayName);

    }
    onCommunicationBroadcastReceived(message) {

        if (message.sender != this.communicationPluginReference.getSubjectId()) {
            if (message.content.type == CHAT_TYPE.MESSAGE) {

                if (message.content.message != "") {


                    this.channelBodyRef.append(this.createReceivedMessageHTML(
                        message.content.message, this.resolveSubjectDisplayName(message.sender),message.sender,
                        new Date()));
                    this.adjustScroll();
                }

            }
        }else{
            if (message.content.message != "") {
                this.channelBodyRef.append(
                    this.createOwnMessageHTML(message.content.message,
                                              this.resolveSubjectDisplayName(
                                                  this.communicationPluginReference.getSubjectId()),
                                              new Date()));
                this.adjustScroll();
            }
        }

    }
    setupSubjectPanel() {
        for(var i = 0; i < this.subjectsInChannel.length; i ++) {

            $("#friend-list").append(
            '                           <li class="list-group-item p-1 hover-bg-lightgray '+this.subjectsInChannel[i].externalId+'_popover" data-toggle="popover">\n'
            + '                            <span class="badge '+ this.subjectsInChannel[i].externalId+'_color username">'+trimDisplayName(this.subjectsInChannel[i].displayName)+'</span>\n'
            + '                        </li>\n');
            //handle onclick to change CHAT channel

        }
        this.setupSubjectPopovers()
    }
    getOrCreateChannelHTML(channelName) {
        if($("#channelBody_" + channelName).length){
            return $("#channelBody_" + channelName);
        }else {
            $('<div/>', {
                id: "channelBody_" + channelName,
                'class': "container-fluid message-scroll channelBody",
                style: 'display:none',
            }).appendTo("#channelMessageBody");

            return $("#channelBody_" + channelName);
        }
    }
    toggleSubjectList() {

        $("#sidebar-content")
            .removeClass("w-100")
            .width($("#sidebar").width());
        $("#subjectContainer").css({"flex": "none"});
        $("#subjectContainer").animate({
                                           width: "toggle"
                                       }, 300, function () {
            $("#subjectContainer").css({"flex": '', "width": ''});
            $("#sidebar-content")
                .css("width", "")
                .addClass("w-100");
        });
        this.adjustScroll();
    }
    setupHTML() {


        $("#toggleTrigger").click(this.toggleSubjectList.bind(this));

        //register form enter and button click
        $("#messageInput").keypress(function (e) {
            if (e.which == 13) {
                this.triggerSendMessage();
            } else{
                //send typing status with threshold
            }
        }.bind(this));

        $("#messageSubmitButton").click(function () {
            this.triggerSendMessage();
        }.bind(this));

        var height = $("#channelMessageBody").height();
        $("#channelMessageBody").css({height: height, overflowY: 'scroll'});

        this.adjustScroll();
    }
    adjustScroll(){
        document.getElementById("channelMessageBody").scrollTop = document.getElementById("channelMessageBody").scrollHeight;
    }
    sendJoinedMessage() {
        this.communicationPluginReference.sendMessage("", this.channel, CHAT_TYPE.JOINED, null)
    }
    sendMessage(message, channel, type, receiver) {
        this.communicationPluginReference.sendMessage(message, channel, type, receiver);
    }
    resolveSubjectDisplayName(subjectId){
        var sub = this.communicationPluginReference.getSubjectByExternalId(subjectId);
        if (sub){
            return sub.displayName;
        }else{
            var chatBotName = this.communicationPluginReference.getChatBotName();
            if(chatBotName!= ''){
                return chatBotName
            }else {
                return "";
            }
        }
    }
    triggerSendMessage() {
        var message = $("#messageInput").val();
        $("#messageInput").val("");

        this.sendRegularMessage(message);

        this.adjustScroll();
    }
    sendRegularMessage(message) {
        this.communicationPluginReference.sendMessage(message, this.channel, CHAT_TYPE.MESSAGE, this.channelReceiver);
    }

    createOwnMessageHTML(message,creator, timeStamp){
        var hideAuthor = false;
        if(this.shouldHideAuthorInConsecutive){
            if(this.lastMessageAuthor == null){
                this.lastMessageAuthor = this.communicationPluginReference.getSubjectId();
            }else{
                if(this.lastMessageAuthor == this.communicationPluginReference.getSubjectId()){
                    hideAuthor = true;
                }else{
                    this.lastMessageAuthor = this.communicationPluginReference.getSubjectId()
                    hideAuthor = false;
                }
            }
        }
     return '<div class="row justify-content-end" >'
            +'<div class="card message-card m-1 '+this.communicationPluginReference.getSubjectId()+'_color ">'
               + '<div class="card-body p-1">'
                    +((hideAuthor)?(''):('<span class="float-left mx-1"><b>'+trimDisplayName(creator)+'</b></span>'))
                    +'<span class="mx-2">'+message+'</span>'
                    +'<span class="float-right mx-1 chat_time"><small style="font-size: 10px">'+minuteAndSecond(timeStamp)+'</small></span>'
                +'</div>'
            +'</div>'
        +'</div>';
    }
    createReceivedMessageHTML(message, creator, externalId, timestamp) {

        var hideAuthor = false;
        if(this.shouldHideAuthorInConsecutive){
            if(this.lastMessageAuthor == null){
                this.lastMessageAuthor = externalId;
            }else{
                if(this.lastMessageAuthor == externalId){
                    hideAuthor = true;
                } else {
                    this.lastMessageAuthor = externalId;
                    hideAuthor = false;
                }
            }
        }
        return '<div class="row ">\n'
               + '                            <div class="card message-card bg-lightblue m-1 '+externalId+'_color">\n'
               + '                                <div class="card-body p-1">\n'
               + ((hideAuthor)?(''):('                                    <span class="float-left mx-1 "><b>'+trimDisplayName(creator)+'</b></span>\n'))
               + '                                    <span class="mx-2">'+message+'</span>\n'
               + '                                    <span class="float-right mx-1 chat_time"><small style="font-size: 10px">'+minuteAndSecond(timestamp)+'</span>\n'
               + '                                </div>\n'
               + '                            </div>\n'
               + '                        </div>';
    }
}


class MatrixChatManager extends GroupChatManager {
    constructor(communicationPluginReference) {
        super(communicationPluginReference);
        this.shouldHideAuthorInConsecutive = true;
        this.showChannelsList();
        this.subjectCanTalkTo = this.communicationPluginReference.getSubjectCanTalkToSubjects();
        this.channelsSubjectIsIn = this.communicationPluginReference.getChannelsSubjectIsIn();

        this.setupChannelsPanel();
    }
    showChannelsList(){
        $("#channelDivision").removeClass("d-none");
        $("#list-group2").removeClass("d-none")
    }
    onCommunicationBroadcastReceived(message) {
        var isOwnMessage = false;

        if (message.sender == this.communicationPluginReference.getSubjectId()) {
            isOwnMessage = true;
        }
        var isToCurrentSubject = false;
        if (message.receiver == this.communicationPluginReference.getSubjectId()) {
            isToCurrentSubject = true;
        }
        if (message.content.type == CHAT_TYPE.MESSAGE) {

            if (message.content.message != "") {
                var originChatName = "";

                if(message.content.channel.indexOf("chat_")>=0){


                    if(!isOwnMessage) {
                        if(isToCurrentSubject) {
                            originChatName = "chat_" + message.sender;

                            $("#channelBody_" + originChatName)
                                .append(this.createReceivedMessageHTML(
                                    message.content.message,
                                    this.resolveSubjectDisplayName(message.sender),message.sender,
                                    new Date()));
                        }
                    }else {

                        originChatName = "chat_" + message.receiver;
                        $("#channelBody_" + originChatName).append(
                            this.createOwnMessageHTML(message.content.message,
                                                      this.resolveSubjectDisplayName(
                                                          this.communicationPluginReference.getSubjectId()),
                                                      new Date()));

                    }

                } else{
                    originChatName = message.content.channel;

                    if(!isOwnMessage) {
                        isToCurrentSubject = true;
                        $("#channelBody_" + message.content.channel)
                            .append(this.createReceivedMessageHTML(
                                message.content.message,
                                this.resolveSubjectDisplayName(message.sender),message.sender,
                                new Date()));
                    }else{

                        $("#channelBody_" + message.content.channel).append(
                            this.createOwnMessageHTML(message.content.message,
                                                      this.resolveSubjectDisplayName(
                                                          this.communicationPluginReference.getSubjectId()),
                                                      new Date()));
                    }
                }

                if(this.channel == originChatName) {
                    //message is for the current channel

                    this.adjustScroll();
                }else{
                   //add it to the body and trigger notification icon
                    if(isToCurrentSubject) {
                        this.addNotification(originChatName);
                    }
                }
            }

        }


    }
    clearNotification(chatName) {
        if($("#"+chatName  + " .notification").length >= 0 ){
            $("#"+chatName  + " .notification").remove();
        }
    }
    addNotification(chatName) {
        //check if there is a notification badge already

        if($("#"+chatName  + " .notification").length > 0 ){
            var totalNot = parseInt($("#"+chatName  + " .notification").text());
            totalNot ++;
            $("#"+chatName  + " .notification").text(totalNot);
        }else{
            $("#"+chatName).append('<span class="notification badge badge-danger">1</span>\n');
        }
    }
    changeChannelTo(channel, channelDisplayName,receiver) {
        this.clearNotification(channel);
        super.changeChannelTo(channel, channelDisplayName, receiver);


        if (channel == CHAT_BODY.INSTRUCTIONS.htmlRef) {
            $("#closeCommunication").addClass("d-none");
            $("#sendForm").addClass("d-none");

            $(this.channelBodyRef).html('<div class="text-center"><br><br><br>' +
                                        'Click on the left menu to open the<br><br>' +
                                        'chat window with the subject\'s name you would like to chat with!'+
                                        '<br><br> You can also chat in the channels that you are associated with!</div>');
            return;
        }else{
            $("#closeCommunication").removeClass("d-none");
            $("#sendForm").removeClass("d-none");
        }
    }
    setupSubjectPanel() {
        //use matrix to either display or not the current user
        this.subjectCanTalkTo = this.communicationPluginReference.getSubjectCanTalkToSubjects();
        for(var i = 0; i < this.subjectsInChannel.length; i ++) {
            for(var j=0 ; j < this.subjectCanTalkTo.length; j++) {
                if (this.subjectCanTalkTo[j] == this.subjectsInChannel[i].externalId) {
                    $("#friend-list").append(
                        '                           <li class="list-group-item p-1 hover-bg-lightgray '
                        + 'username '+this.subjectsInChannel[i].externalId+'_popover" data-external-id="'+ this.subjectsInChannel[i].externalId + '"'
                        + ' id="chat_'+ this.subjectsInChannel[i].externalId +'" data-toggle="popover">\n'
                        + '                            <span class="badge d-xs-none '+this.subjectsInChannel[i].externalId+'_color">'
                        + this.subjectsInChannel[i].displayName + '</span>\n'
                        + '                        </li>\n');
                    this.getOrCreateChannelHTML('chat_'+ this.subjectsInChannel[i].externalId);
                }
            }
        }
        $(".username").click(function(event){
            var externalId = $(event.target).data("external-id");
            if(externalId == null) {
                externalId = $($(event.target).parent()).data("external-id");
            }
            var subject = this.communicationPluginReference.getSubjectByExternalId(externalId);
            this.changeChannelTo('chat_' + externalId, "Chat with: "+ subject.displayName, externalId);
            $("#messageInput").focus();

        }.bind(this));
        this.setupSubjectPopovers();
    }
    setupChannelsPanel() {
        for(var i = 0; i < this.channelsSubjectIsIn.length; i ++) {
            $("#channel-list").append(
                '                           <li class="list-group-item p-1 hover-bg-lightgray  channelName" \n'
                + '                 data-channel-name="'+this.channelsSubjectIsIn[i]+'"'
                + ' id="channel_'+this.channelsSubjectIsIn[i]+'">\n'
                + '                            <span class="d-xs-none">'
                +this.channelsSubjectIsIn[i]+'</span>\n'
                + '                        </li>\n');
            this.getOrCreateChannelHTML("channel_"+this.channelsSubjectIsIn[i]);
        }
        $(".channelName").click(function(event){
            var chatName = $(event.target).data("channel-name");
            if(chatName == null){
                chatName = $($(event.target).parent()).data("channel-name");
            };
            this.changeChannelTo('channel_' + chatName, "#"+ chatName, null);
            $("#messageInput").focus();
        }.bind(this));
    }
}
class DyadicChatManager extends GroupChatManager {
    constructor(communicationPluginReference) {
        super(communicationPluginReference);
        this.requestSent = null;
        this.requestReceived = null;
        this.inChatWith = null;
        this.shouldHideAuthorInConsecutive = true;
        this.setAvailableStatus();
    }
    setupHTML() {
        super.setupHTML();
        //add close communication button at the end of the window.
        $('<div/>', {
            'id': 'closeCommunication',
            'class': "text-center",
            'style': 'padding-top:5px'
        }).append(
        $('<button/>',{
            'class': "btn btn-danger border-0 ",
            type: "button",
            id: "endChat",
            text: 'Close Communication'
        })).appendTo("#chatContainer");
        $("#endChat").click(this.handleCloseCommunication.bind(this));


    }
    onCommunicationBroadcastReceived(message) {

        if (message.sender != this.communicationPluginReference.getSubjectId()) {
            if (message.receiver == this.communicationPluginReference.getSubjectId()) {
                if (message.content.type == CHAT_TYPE.REQUEST_CHAT) {

                    if (this.status != STATUS.BUSY) {
                        this.requestReceived = message.sender;
                        this.changeChannelTo(CHAT_BODY.REQUEST_RECEIVED.htmlRef,
                                             CHAT_BODY.REQUEST_RECEIVED.displayString,
                                             this.requestReceived)
                    }

                }
                if (message.content.type == CHAT_TYPE.ACCEPT_CHAT) {
                    this.initChatWith(message.sender);
                }
                if (message.content.type == CHAT_TYPE.MESSAGE) {
                    super.onCommunicationBroadcastReceived(message);
                }

                if (message.content.type == CHAT_TYPE.HANG_UP_CHAT) {
                    alert("This chat was ended by the other party!");
                    this.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef,
                                         CHAT_BODY.INSTRUCTIONS.displayString, null);
                    this.setAvailableStatus();
                }
            }
            if (message.content.type == CHAT_TYPE.STATUS) {
                this.updateTeamSubjectStatus(message.sender, message.content.message);
            }
        } else {
            if (message.content.type == CHAT_TYPE.MESSAGE) {
                super.onCommunicationBroadcastReceived(message);
            }
        }




    }
    handleCloseCommunication(){
        this.sendMessage("", this.channel, CHAT_TYPE.HANG_UP_CHAT, this.inChatWith);
        this.setAvailableStatus();
        this.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef,CHAT_BODY.INSTRUCTIONS.displayString);
    }
    handleAcceptRequest(){
        var senderId = $(event.target).data("externalid");
        this.sendMessage("", this.channel, CHAT_TYPE.ACCEPT_CHAT, senderId);
        this.initChatWith(senderId);
    }
    initChatWith(subjectId){
        this.setBusyStatus();
        //create channel
        this.inChatWith = subjectId;
        var channelChat = this.communicationPluginReference.getSubjectId() + "_" + subjectId;
        this.changeChannelTo(channelChat,"Chat with : " + this.resolveSubjectDisplayName(subjectId),this.inChatWith);
    }
    handleSendHangUpCommunication(event){
        var senderToId = $(event.target).data("externalid");
        this.sendMessage("", this.channel, CHAT_TYPE.HANG_UP_CHAT, senderToId);
        this.setAvailableStatus();
        this.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef,CHAT_BODY.INSTRUCTIONS.displayString);

    }
    handleSendRequestCommunication(event){
        var subjStatus = $(event.target).data("status");

        if(this.status == STATUS.AVAILABLE) {

            if(subjStatus != STATUS.AVAILABLE){
                alert("You can't request a call with a busy subject!")
                return;
            }
            this.requestSent = $(event.target).data("externalid");

            this.setBusyStatus();

            this.changeChannelTo("requestsent", "Waiting ...", this.requestSent);
            this.sendMessage("", this.channel, CHAT_TYPE.REQUEST_CHAT, this.requestSent);
        }else{
            alert("You must end the current call before requesting a new one!")
        }

    }
    updateTeamSubjectStatus(subject, status){
        var iconRef = "#" + subject + "_subject_ref .chatRequest";
        var textDescRef = "#" + subject + "_subject_ref .status-text-desc";
        if(status == STATUS.AVAILABLE) {
            $(iconRef).removeClass("chatRequestBusy");
            $(iconRef).removeClass("chatRequestNotAvailable");
            $(iconRef).addClass("chatRequestAvailable");
            $(iconRef).data("status", status);
            $(textDescRef).text("available");

            $(textDescRef).removeClass("statusBusy");
            $(textDescRef).removeClass("statusNotAvailable");
            $(textDescRef).addClass("statusAvailable");

            return;
        }
        if(status == STATUS.BUSY) {
            $(iconRef).removeClass("chatRequestAvailable");
            $(iconRef).removeClass("chatRequestNotAvailable");
            $(iconRef).addClass("chatRequestBusy");
            $(iconRef).data("status", status);

            $(textDescRef).text("busy");
            $(textDescRef).removeClass("statusAvailable");
            $(textDescRef).removeClass("statusNotAvailable");
            $(textDescRef).addClass("statusBusy");
            return;
        }
        if(status == STATUS.UNAVAILABLE) {
            $(iconRef).removeClass("chatRequestBusy");
            $(iconRef).removeClass("chatRequestAvailable");
            $(iconRef).addClass("chatRequestNotAvailable");
            $(iconRef).data("status", status);

            $(textDescRef).text("unavailable");
            $(textDescRef).removeClass("statusBusy");
            $(textDescRef).removeClass("statusAvailable");
            $(textDescRef).addClass("statusNotAvailable");
            return;
        }
    }
    setBusyStatus() {
        this.status = STATUS.BUSY;
        this.sendMessage(this.status, this.channel, CHAT_TYPE.STATUS,null);
    }

    setAvailableStatus() {
        this.requestSent = null;
        this.requestReceived = null;
        this.status = STATUS.AVAILABLE;
        this.sendMessage(this.status, this.channel, CHAT_TYPE.STATUS,null);
    }

    changeChannelTo(channel, channelDisplayName,receiver){
        super.changeChannelTo(channel,channelDisplayName,receiver);
        if(channel == CHAT_BODY.INSTRUCTIONS.htmlRef){
            $("#closeCommunication").addClass("d-none");
            $("#sendForm").addClass("d-none");

            $(this.channelBodyRef).html('<div class="text-center"><br><br><br>' +
            'Click on the green telephone<br><br>'+
            '<i class="fa fa-phone-square" style="font-size: 60px;color:#00dd1c"></i><br><br> next to the subject\'s name you would like to chat with!'+
            '<br><br> If he accepts the request the chat window will be displayed <b>here</b>!</div>');
            return;
        }
        if(channel == CHAT_BODY.REQUEST_SENT.htmlRef){
            $("#closeCommunication").addClass("d-none");
            $("#sendForm").addClass("d-none");

            $(this.channelBodyRef).html('<div class="text-center"><br><br><br>' +
                                        'Waiting for <b>'+this.resolveSubjectDisplayName(this.requestSent)+'</b><br><br>'+
                                        '<i class="fa fa-phone-square chatRequestIconRing" style="font-size: 60px;color:#00dd1c"></i><br><br> chat request approval!'+
                                        '<br><br><button class="btn btn-danger" id="cancel_request" data-externalid="'+this.requestSent+'">Cancel request</button></div>');
            $("#cancel_request").click(this.handleSendHangUpCommunication.bind(this));
            return;
        }
        if(channel == CHAT_BODY.REQUEST_RECEIVED.htmlRef){
            $("#closeCommunication").addClass("d-none");
            $("#sendForm").addClass("d-none");

            $(this.channelBodyRef).html('<div class="text-center"><br><br><br>' +
                                        'Chat request from <b>'+this.resolveSubjectDisplayName(this.requestReceived)+'</b><br><br>'+
                                        '<i class="fa fa-phone-square chatRequestIconRing" style="font-size: 60px;color:#00dd1c"></i><br><br> '+
                                        '<br><br><button class="btn btn-success" data-externalid="'+this.requestReceived+'" id="accept_request">Accept request</button>'+
                                        '<br><br><button class="btn btn-danger" data-externalid="'+this.requestReceived+'" id="deny_request">Deny request</button></div>');
            $("#deny_request").click(this.handleSendHangUpCommunication.bind(this));
            $("#accept_request").click(this.handleAcceptRequest.bind(this));
            return;
        }
        $("#closeCommunication").removeClass("d-none");
        $("#sendForm").removeClass("d-none");

    }
    setupSubjectPanel(){
        for(var i = 0; i < this.subjectsInChannel.length; i ++) {
            if(this.subjectsInChannel[i].externalId != this.communicationPluginReference.getSubjectId()) {
                $("#friend-list").append(
                    '                           <li class="list-group-item p-1 hover-bg-lightgray '+this.subjectsInChannel[i].externalId+'_popover" data-toggle="popover" id="'+this.subjectsInChannel[i].externalId+'_subject_ref">\n'
                    + '                            <span class="badge d-xs-none '+this.subjectsInChannel[i].externalId+'_color username">'
                    + this.subjectsInChannel[i].displayName + '</span>\n'
                    + '                             <i class="fa fa-phone-square chatRequest chatRequestAvailable" data-status="AVAILABLE" data-externalId="'
                    + this.subjectsInChannel[i].externalId + '"style=""></i> <span class="badge badge-primary status-text-desc statusAvailable">available</span>  \n'
                    + '                        </li>\n');
            }
            //handle onclick to change CHAT channel
        }
        $(".chatRequest").click(this.handleSendRequestCommunication.bind(this));
        //this.toggleSubjectList(null);
        this.setupSubjectPopovers();
    }

}



function minuteAndSecond(timestamp){
    function trailingZeros(val) {
        if (val < 10) {
            return '0' + val;
        } else {
            return val;
        }
    }

    var date = new Date(timestamp);
    var hours = date.getHours();
    var minutes = date.getMinutes();

    return trailingZeros(hours.toString()) + ':' + trailingZeros(minutes.toString());
}