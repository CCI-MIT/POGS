'use strict';

class Pogs {
    constructor(){
        window.pogs = this;
        this.handlers = {};  // observers
        this.plugins = [];  // observers
        this.task = null;  // observers
        this.team = null;  // observers
    }
    createPlugin(pluginName, initFunc, destroyFunct) {
        var pl = new PogsPlugin(pluginName, initFunc, this,destroyFunct);
        this.plugins.push(pl);
        this.subscribe('onReady', pl.initFunc.bind(pl));

        this.subscribe("onUnload", pl.destroyFunc.bind(pl));
        return pl;
    }
    setupSubjectColors(){

        var subjectColorMap = [];

        if(!this.teammates) return;

        for(var i=0; i < this.teammates.length; i++) {

            var backgroundColor = null;
            var fontColor = null;
            for (var j = 0; j < this.teammates[i].attributes.length; j++) {

                if (this.teammates[i].attributes[j].attributeName
                    == "SUBJECT_DEFAULT_BACKGROUND_COLOR") {
                    backgroundColor = this.teammates[i].attributes[j].stringValue;
                }
                if (this.teammates[i].attributes[j].attributeName == "SUBJECT_DEFAULT_FONT_COLOR") {
                    fontColor = this.teammates[i].attributes[j].stringValue;
                }
            }

            if (fontColor && backgroundColor) {
                subjectColorMap.push(
                    {
                        externalId: this.teammates[i].externalId,
                        backgroundColor: backgroundColor,
                        fontColor: fontColor
                    });
            }
        }




        var rule  = '';
        for (let i = 0; i < subjectColorMap.length; i++) {
            rule += `.${subjectColorMap[i].externalId}_color, `
                + `[data-author=subject-${subjectColorMap[i].externalId}] {`
                + `background-color: ${subjectColorMap[i].backgroundColor};`
                + `color: ${subjectColorMap[i].fontColor};`
                + '}';
            rule += `.${subjectColorMap[i].externalId}_activecolor {`
                    + `color: ${subjectColorMap[i].backgroundColor};`
                    + '}';
        }

        var css = document.createElement('style'); // Creates <style></style>
        css.type = 'text/css'; // Specifies the type
        css.id="subject_colors";
        if (css.styleSheet) css.styleSheet.cssText = rule; // Support for IE
        else css.appendChild(document.createTextNode(rule)); // Support for the rest
        document.getElementsByTagName("head")[0].appendChild(css); // Specifies where to place the css

    }
    setup (config) {


        this.stompClient = null;
        this.sessionId = config.sessionId;
        this.subjectId = config.subjectId;
        this.completedTaskId = config.completedTaskId;
        this.teammates = config.teammates;
        this.taskList = config.taskList;
        this.task = config.task;
        this.lastTask = config.lastTask;
        this.sessionName = config.sessionName;
        this.chatBotName = config.chatBotName;
        this.sessionIsPerpetual = ((config.sessionIsPerpetual)?(config.sessionIsPerpetual):(false));
        this.waitingRoomExpireTime = config.waitingRoomExpireTime;
        this.doneUrlParameter = config.doneUrlParameter;
        this.taskIsSolo = config.taskIsSolo;
        this.setupSubjectColors();

        this.hasCollaborationVotingWidget = config.hasCollaborationVotingWidget;
        this.hasCollaborationFeedbackWidget = config.hasCollaborationFeedbackWidget;
        this.hasCollaborationTodoListEnabled = config.hasCollaborationTodoListEnabled;

        this.hasCollaboration = this.hasCollaborationVotingWidget||
                                this.hasCollaborationFeedbackWidget||
                                this.hasCollaborationTodoListEnabled;



        this.subjectCanTalkTo = config.subjectCanTalkTo;

        this.channelSubjectIsIn = config.channelSubjectIsIn;

        this.hasCollaboration = this.hasCollaborationVotingWidget ||
                                this.hasCollaborationFeedbackWidget||
                                this.hasCollaborationTodoListEnabled;
        this.hasChat = config.hasChat;
        this.communicationType = config.communicationType;


        this.nextUrl =  this.validateFinalUrl(config.nextUrl);
        this.secondsRemainingCurrentUrl = (new Date().getTime() +
                                           parseInt(config.secondsRemainingCurrentUrl));
        this.taskConfigurationAttributes = config.taskConfigurationAttributes;

        this.completedTaskAttributes = config.completedTaskAttributes;
        this.dictionary = config.dictionary;

        this.eventsUntilNow = config.eventsUntilNow;
        this.taskConfigurationAttributesMap = new Map();
        if(typeof(this.taskConfigurationAttributes) != "undefined") {
            for (var i = 0; i < this.taskConfigurationAttributes.length; i++) {
                this.taskConfigurationAttributesMap.set(
                    this.taskConfigurationAttributes[i].attributeName,
                    this.taskConfigurationAttributes[i]);
            }
        }
        this.completedTaskAttributesMap = new Map();
        if(typeof(this.completedTaskAttributes) != "undefined") {
            for (var i = 0; i < this.completedTaskAttributes.length; i++) {
                this.completedTaskAttributesMap.set(
                    this.completedTaskAttributes[i].attributeName,
                    this.completedTaskAttributes[i]);
            }
        }



        this.initializeWebSockets();
        //window.location.href.indexOf("/w/")>=0
        if(!(window.location.href.indexOf("/waiting_room")>=0)&&!(window.location.href.indexOf("/sessions/start/")>=0)) {
            $('<div/>', {
                id: "countdown",
                'class': "float-right",
            }).appendTo(".header");
        }

        this.countDown = new Countdown(this.secondsRemainingCurrentUrl,
                                       "countdown",
                                       this.onCountdownEnd.bind(this))

        this.subscribe('flowBroadcast', this.onFlowBroadcastReceived.bind(this));

        this.setupCommunication();
        this.setupCollaboration();


        if(this.eventsUntilNow) {
            if (this.eventsUntilNow.length > 0) {
                this.subscribe('onReady', this.processOldEventsUntilNow.bind(this));
            }
        }

    }
    getDictionary(){
        if(this.dictionary){
            return this.dictionary;
        }
    }
    getDictionaryEntry(id, callback){
        if(this.dictionary){
            $.getJSON("/dictionaries/" + this.dictionary.id + '/dictionaryentries/' + id,null, function(emp) {
                if(emp) {
                    return callback(emp);
                } else {
                    return callback(null);
                }
            });
            return;
        }
        callback(null);
    }
    onCountdownEnd(){
        this.subscribe("onUnload", function(){
            window.location = this.nextUrl;
        }.bind(this))
        this.fire(null, 'onUnload', this);

    }
    processOldEventsUntilNow(){
        for(var i=0; i< this.eventsUntilNow.length ; i++) {

            var event = this.eventsUntilNow[i];
            if(event.type == "TASK_ATTRIBUTE"){
                this.fireOldEvent(event,'taskAttributeBroadcast')
            }

            if(event.type == "COMMUNICATION_MESSAGE"){
                this.fireOldEvent(event,'communicationMessage');
            }

            if(event.type == "COLLABORATION_MESSAGE"){
                this.fireOldEvent(event,'collaborationMessage');
            }
        }
    }
    fireOldEvent(message, eventName){
        if (!(typeof message.content === 'object')) {
            var messageContent = JSON.parse(message.content);
            message.content = messageContent;
        }

        this.fire(message, eventName, this);

    }
    setupCommunication() {
      if(this.hasChat){
          var pl = new CommunicationPlugin(this);
          this.plugins.push(pl);
          this.subscribe('onReady', pl.initFunc.bind(pl));
      }
    }
    setupCollaboration () {
        if(this.hasCollaboration){
            var pl = new CollaborationPlugin(this);
            this.plugins.push(pl);
            this.subscribe('onReady', pl.initFunc.bind(pl));
        }
    }
    initializeWebSockets () {

        var socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {};

        this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
    }
    subscribe(eventName, fn) {
        if (!(eventName in this.handlers)) {
            this.handlers[eventName] = [];
        }
        this.handlers[eventName].push(fn);
    }
    fire(o, eventName, thisObj) {
        var scope = thisObj || window;
        if (eventName in this.handlers) {
            this.handlers[eventName].forEach(function (item) {
                item.call(scope, o);
            });
        }
    }
    subscribeTopicAndFireEvent(topicUrl, messageType, eventName) {

        this.stompClient.subscribe(topicUrl,
                                   function (payload) {
                                       var message = JSON.parse(payload.body);
                                       if (!(typeof message.content === 'object')) {
                                           var messageContent = JSON.parse(message.content);
                                           message.content = messageContent;
                                       }

                                       if (message.type === messageType) {
                                           this.fire(message, eventName, this);
                                       }
                                   }.bind(this));
    }
    onConnected() {

        this.subscribeTopicAndFireEvent(('/topic/public/flow/' + this.sessionId),
                                        'FLOW_BROADCAST', 'flowBroadcast');

        if (this.completedTaskId != null) {

            this.subscribeTopicAndFireEvent(
                '/topic/public/task/' + this.completedTaskId
                + '/work',
                'TASK_ATTRIBUTE', 'taskAttributeBroadcast');

            if (this.hasChat) {
                this.subscribeTopicAndFireEvent(
                    '/topic/public/task/' + this.completedTaskId
                    + '/communication',
                    'COMMUNICATION_MESSAGE', 'communicationMessage');
            }
            //if collaboration subscribe to collab
            if (this.hasCollaboration) {
                this.subscribeTopicAndFireEvent(
                    '/topic/public/task/' + this.completedTaskId
                    + '/collaboration',
                    'COLLABORATION_MESSAGE', 'collaborationMessage');
            }
        }

        var x = setInterval(function () {

            this.sendCheckInMessage();

        }.bind(this), 5000);// 5 seconds

        this.fire(null, 'onReady', this);
    }
    onError(error) {
        this.fire(null, 'onError', this);
    }
    sendCheckInMessage(){
        this.sendMessage("/pogsapp/checkIn.sendMessage", "CHECK_IN",
                         {message: window.location.pathname, type: "CHECK_IN"},
                                 this.subjectId, null, this.completedTaskId,
                                 this.sessionId);
    }
    validateFinalUrl(newUrlToBeSet) {
        //check if there is HTTPS or HTTP in the URL.
        if ((newUrlToBeSet.indexOf("http") == -1)&&(newUrlToBeSet.indexOf("https")==-1)) {
            return newUrlToBeSet + '/' + this.subjectId;
        } else {
            if((newUrlToBeSet.indexOf("/sessions/")!=-1) && this.doneUrlParameter){
                return newUrlToBeSet + '?externalId=' + this.subjectId;
            }
            return newUrlToBeSet;
        }


    }
    onFlowBroadcastReceived(message) {

        if((message.content.currentUrl + "/" +this.subjectId == window.location.pathname)) {
            this.nextUrl = message.content.nextUrl;
            this.nextUrl = this.validateFinalUrl(this.nextUrl);
            var finalDate = (new Date().getTime() + parseInt(
                message.content.secondsRemainingCurrentUrl));
            this.countDown.updateCountDownDate(finalDate)
        }
        console.log(JSON.parse(message.content.perpetualSubjectsChosen));
        if(this.sessionIsPerpetual) {
            console.log(JSON.parse(message.content.perpetualSubjectsChosen));
            this.perpetualSubjectsChosen = JSON.parse(message.content.perpetualSubjectsChosen);
            for(var i = 0; i < this.perpetualSubjectsChosen.length ; i ++){
                if(this.subjectId == this.perpetualSubjectsChosen[i]) {
                    window.location.href = "/check_in?externalId=" + this.subjectId;
                }
            }

        }
        //HANDLE EXPIRED WAITING ROOM.
        if(this.sessionIsPerpetual && window.location.pathname.indexOf("/sessions/start/") >=0 ){
            let now = new Date();
            if(parseInt(this.waitingRoomExpireTime) < now){
                window.location.href = "/expired?externalId=" + this.subjectId;
            }
        }


    }
    sendMessage(url, type, messageContent, sender, receiver, completedTaskId,
                           sessionId) {

        if (messageContent && this.stompClient) {
            var chatMessage = {
                sender: sender,
                content: messageContent,
                type: type,
                receiver: receiver,
                completedTaskId: completedTaskId,
                sessionId: sessionId

            };
            this.stompClient.send(url, {}, JSON.stringify(chatMessage));
        }
        if (typeof event !== 'undefined') {
            event.preventDefault()
        }
    }
}



new Pogs();
