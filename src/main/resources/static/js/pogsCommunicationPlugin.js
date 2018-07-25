
const COMMUNICATION_TYPE ={
    GROUP_CHAT: 'G',
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
            console.log("Init config : " + this.pogsRef.communicationType);
            if (this.pogsRef.communicationType == COMMUNICATION_TYPE.GROUP_CHAT) {
                var gcm = new GroupChatManager(this);
                gcm.changeChannelTo(CHAT_BODY.GROUP.htmlRef, CHAT_BODY.GROUP.displayString, null);
            }
            if (this.pogsRef.communicationType == COMMUNICATION_TYPE.DYADIC) {
                var dcm = new DyadicChatManager(this);
                dcm.changeChannelTo(CHAT_BODY.INSTRUCTIONS.htmlRef, CHAT_BODY.INSTRUCTIONS.displayString,null);

            }
            //TODO: handle other kinds of chats
            // - Communication matrix

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

        this.sendJoinedMessage();
        this.setupHTML();
        this.communicationPluginReference.
            subscribeCommunicationBroadcast(this.onCommunicationBroadcastReceived.bind(this));

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
                        message.content.message, this.resolveSubjectDisplayName(message.sender), new Date()));
                    this.adjustScroll();
                }

            }
        }

    }
    setupSubjectPanel() {
        for(var i = 0; i < this.subjectsInChannel.length; i ++) {

            $("#friend-list").append(
            '                           <li class="list-group-item p-1 hover-bg-lightgray">\n'
            + '                            <span class="d-xs-none username">'+this.subjectsInChannel[i].displayName+'</span>\n'
            + '                        </li>\n');
            //handle onclick to change CHAT channel

        }
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
            return "";
        }
    }
    triggerSendMessage() {
        var message = $("#messageInput").val();
        $("#messageInput").val("");

        this.channelBodyRef.append(
            this.createOwnMessageHTML(message,
                                      this.resolveSubjectDisplayName(
                                          this.communicationPluginReference.getSubjectId()),
                                      new Date()));
        this.sendRegularMessage(message);

        this.adjustScroll();
    }
    sendRegularMessage(message) {
        this.communicationPluginReference.sendMessage(message, this.channel, CHAT_TYPE.MESSAGE, this.channelReceiver);
    }

    createOwnMessageHTML(message,creator, timeStamp){

     return '<div class="row justify-content-end" >'
            +'<div class="card message-card m-1">'
               + '<div class="card-body p-2">'
                    +'<span class="float-left mx-1"><b>'+creator+'</b></span>'
                    +'<span class="mx-2">'+message+'</span>'
                    +'<span class="float-right mx-1"><small>'+minuteAndSecond(timeStamp)+'</small></span>'
                +'</div>'
            +'</div>'
        +'</div>';
    }
    createReceivedMessageHTML(message, creator, timestamp) {
        return '<div class="row ">\n'
               + '                            <div class="card message-card bg-lightblue m-1">\n'
               + '                                <div class="card-body p-2">\n'
               + '                                    <span class="float-left mx-1"><b>'+creator+'</b></span>\n'
               + '                                    <span class="mx-2">'+message+'</span>\n'
               + '                                    <span class="float-right mx-1"><small>'+minuteAndSecond(timestamp)+'</span>\n'
               + '                                </div>\n'
               + '                            </div>\n'
               + '                        </div>';
    }
}


class DyadicChatManager extends GroupChatManager {
    constructor(communicationPluginReference) {
        super(communicationPluginReference);
        this.requestSent = null;
        this.requestReceived = null;
        this.inChatWith = null;
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
            console.log("Request sent from: " + this.communicationPluginReference.getSubjectId() +
                        " to :" + $(event.target).data("externalid"));
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
                    '                           <li class="list-group-item p-1 hover-bg-lightgray" id="'+this.subjectsInChannel[i].externalId+'_subject_ref">\n'
                    + '                            <span class="d-xs-none username">'
                    + this.subjectsInChannel[i].displayName + '</span>\n'
                    + '                             <i class="fa fa-phone-square chatRequest chatRequestAvailable" data-status="AVAILABLE" data-externalId="'
                    + this.subjectsInChannel[i].externalId + '"style=""></i> <span class="badge badge-primary status-text-desc statusAvailable">available</span>  \n'
                    + '                        </li>\n');
            }
            //handle onclick to change CHAT channel
        }
        $(".chatRequest").click(this.handleSendRequestCommunication.bind(this));
        this.toggleSubjectList(null);
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
    var hours = Math.floor((timestamp % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    var minutes = Math.floor((timestamp % (1000 * 60 * 60)) / (1000 * 60));

    return trailingZeros(hours.toString()) + ':' + trailingZeros(minutes.toString());
}