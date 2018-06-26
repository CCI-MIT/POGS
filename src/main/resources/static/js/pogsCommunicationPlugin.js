class CommunicationPlugin extends PogsPlugin {
    constructor(pogsRef){
        super('communicationPlugin', null, pogsRef);
        this.initFunc = this.init;
    }
    init(){
            console.log("Init config : " + this.pogsRef.communicationType);
            if (this.pogsRef.communicationType == 'G') {
                new GroupChatManager(this);
            }
            //TODO: handle other kinds of chats
            // - Communication matrix
            // - Dyadic comm.
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
        this.subjectsInChannel = communicationPluginReference.getTeammates();
        this.setupSubjectPannel();
        this.channel = "group";

        this.channelBodyRef = this.createChannelHTML(this.channel);

        this.communicationPluginReference = communicationPluginReference;

        this.sendJoinedMessage();
        this.setupHTML();
        this.communicationPluginReference.
            subscribeCommunicationBroadcast(this.onCommunicationBroadcastReceived.bind(this));

        this.channelBodyRef.show();
    }
    onCommunicationBroadcastReceived(message) {

        if (message.sender != this.communicationPluginReference.getSubjectId()) {
            if (message.content.type == "MESSAGE") {

                if (message.content.message != "") {
                    this.channelBodyRef.append(this.createReceivedMessageHTML(
                        message.content.message, this.communicationPluginReference.getSubjectByExternalId(message.sender).displayName, new Date()));
                }

            }
        }

    }
    setupSubjectPannel(){
        for(var i = 0; i < this.subjectsInChannel.length; i ++) {
            $("#friend-list").append(
            '                           <li class="list-group-item p-1 hover-bg-lightgray">\n'
            + '                            <span class="d-xs-none username">'+this.subjectsInChannel[i].displayName+'</span>\n'
            + '                        </li>\n');
            //handle onclick to change CHAT channel

        }
    }
    createChannelHTML(channelName) {

        $('<div/>', {
            id: "channelBody_"+channelName,
            'class': "container-fluid message-scroll",
            style: 'display:none',
        }).appendTo("#channelMessageBody");

        return $("#channelBody_" + channelName);
    }
    setupHTML() {
        //register toggle button to subject list
        $("#toggleTrigger").click(function (event) {
            $("#chatContainer").hide();
            $("#subjectContainer").show();
        });
        //register back to chat window
        $("#toggleBackTrigger").click(function () {
            $("#subjectContainer").hide();
            $("#chatContainer").show();
        });
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
    }
    sendJoinedMessage() {
        this.communicationPluginReference.sendMessage("", this.channel, "JOINED", null)
    }
    triggerSendMessage() {
        //get message content from input
        var message = $("#messageInput").val();
        // erase it from the input
        $("#messageInput").val("");
        // append it to current message body.

        this.channelBodyRef.append(
            this.createOwnMessageHTML(message,
                                      this.communicationPluginReference.getSubjectByExternalId(
                                          this.communicationPluginReference.getSubjectId()),
                                      new Date()));
        // send the message using ws
        this.sendRegularMessage(message);
    }
    sendRegularMessage(message) {
        this.communicationPluginReference.sendMessage(message, this.channel, "MESSAGE", null);
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