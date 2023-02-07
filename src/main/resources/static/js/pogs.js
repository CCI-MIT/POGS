'use strict';

class Pogs {
    constructor(){
        window.pogs = this;
        this.handlers = {};  // observers
        this.plugins = [];  // observers
        this.task = null;  // observers
        this.team = null;  // observers
        this.firstLoad = true;
        this.lastCheckInDate = new Date().getTime();
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
        this.isGlobalChatPage = (config.isGlobalChatPage)?(config.isGlobalChatPage):(false);
        this.isGlobalChatInitialized = false;
        this.teammates = config.teammates;
        this.videoProviderAppId = config.videoProviderAppId;
        this.recordSessionSaveEphemeralEvents = config.recordSessionSaveEphemeralEvents;
        this.taskList = config.taskList;
        this.task = config.task;
        this.lastTask = config.lastTask;
        this.sessionName = config.sessionName;
        this.chatBotName = config.chatBotName;
        this.sessionIsPerpetual = ((config.sessionIsPerpetual)?(config.sessionIsPerpetual):(false));
        this.sessionExecutionMode = ((config.sessionExecutionMode)?(config.sessionExecutionMode): (false));
        this.waitingRoomExpireTime = config.waitingRoomExpireTime;
        this.doneUrlParameter = config.doneUrlParameter;
        this.videoChatShouldRecord = (config.videoChatShouldRecord)?(config.videoChatShouldRecord):(false);
        this.taskIsSolo = config.taskIsSolo;
        this.triggerTaskForVideoChat = (config.triggerTaskForVideoChat
                                        && config.triggerTaskForVideoChat != "")?
                                       (config.triggerTaskForVideoChat):("/task/");
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
            }).appendTo(".headers");
        }

        this.countDown = new Countdown(this.secondsRemainingCurrentUrl,
                                       "countdown",
                                       this.onCountdownEnd.bind(this))

        this.subscribe('flowBroadcast', this.onFlowBroadcastReceived.bind(this));

        if(!this.isGlobalChatPage) {
            this.setupCommunication();
        } else {
            console.log("IS GLOBAL CHAT PAGE")
        }
        this.setupCollaboration();


        if(this.eventsUntilNow) {
            if (this.eventsUntilNow.length > 0) {
                this.subscribe('onReady', this.processOldEventsUntilNow.bind(this));
            }
        }


        var x = setInterval(function () {

            this.checkForFlowSyncIssue();

        }.bind(this), 15000);// 15 seconds

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

            let url = this.nextUrl;
            let isExternalFinalPage =
                ((url.indexOf("http") != -1||url.indexOf("https")!=-1))
                && (url.indexOf("/sessions/")==-1) ;
            console.log("isGlobalChat ("+this.isGlobalChatPage + ") inside the onUnload call is external page: " + isExternalFinalPage);
            if(!this.isGlobalChatPage){

                console.log("isGlobalChat ("+this.isGlobalChatPage + ") inside the onUnload call parent location: " + window.parent.location);
                console.log("isGlobalChat ("+this.isGlobalChatPage + ") inside the onUnload call parent location: "+ window.location)
                if ( window.location !== window.parent.location ) {

                    if(!isExternalFinalPage){
                        window.location = this.nextUrl;

                    }
                } else {
                    window.location = this.nextUrl;
                }
            } else {
                this.countDown = null;

                if(isExternalFinalPage){
                    window.location = this.nextUrl;
                }
                if(url.indexOf("/scoring/")!=-1){
                    window.location = this.nextUrl;
                }
            }

            //console.log("Linha 206")
        }.bind(this))
        this.fire(null, 'onUnload', this);

    }
    processOldEventsUntilNow(){
        for(var i=0; i< this.eventsUntilNow.length ; i++) {

            var event = this.eventsUntilNow[i];
            //console.log("OLD EVENT" )
            //console.log(JSON.stringify(event));
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
        try {
            var socket = new SockJS('/ws');
            this.stompClient = Stomp.over(socket);
            this.stompClient.debug = () => {
            };

            this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
        } catch (error){
            console.log(error);
            //TODOFIX: this.handleSocketError("Error in socket initialization, STOMP exception:" + error);
        }
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
    handleSocketError(msg) {
        $.getJSON('/log/' +this.sessionId + "?externalId="+ this.subjectId+"&errorMessage="+ msg + "&url"+window.location.href);
        //TODO FIX: location.reload();
        console.log("LINHA 298")
    }

    handleBlockingError(msg) {
        $.getJSON('/log/' +this.sessionId + "?externalId="+ this.subjectId+"&errorMessage="+ msg + "&url"+window.location.href);

        //TODO FIX: location.reload();
        console.log("LINHA 304")
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
        this.handleSocketError(error);
    }
    sendCheckInMessage(){
        if(new Date().getTime() - this.lastCheckInDate >= 1000*60){
            this.firstLoad = true;
        }

        this.sendMessage("/pogsapp/checkIn.sendMessage", "CHECK_IN",
                         {message: window.location.pathname, type: "CHECK_IN", channel: this.firstLoad},
                                 this.subjectId, null, this.completedTaskId,
                                 this.sessionId);
        this.firstLoad = true;

    }
    getURLRedirectAttributes(){
        let url = "";
        let workerId = this.getSubjectAttribute("workerId");
        if(workerId){
            url = url + "&workerId="+workerId;
        }
        let assignmentId = this.getSubjectAttribute("assignmentId");
        if(assignmentId){
            url = url + "&assignmentId="+assignmentId;
        }
        let hitId = this.getSubjectAttribute("hitId");
        if(hitId){
            url = url + "&hitId="+hitId;
        }
        let PROLIFIC_PID = this.getSubjectAttribute("PROLIFIC_PID");
        if(PROLIFIC_PID){
            url = url + "&PROLIFIC_PID="+PROLIFIC_PID;
        }
        let STUDY_ID = this.getSubjectAttribute("STUDY_ID");
        if(STUDY_ID){
            url = url + "&STUDY_ID="+STUDY_ID;
        }
        let SESSION_ID = this.getSubjectAttribute("SESSION_ID");
        if(SESSION_ID){
            url = url + "&SESSION_ID="+SESSION_ID;
        }
        return url;
    }
    getOverrideURLIfSet(newUrlToBeSet){
        let overrideUrl = this.getSubjectAttribute("SESSION_DONE_REDIRECT_URL");
        let urlParameter = this.getURLRedirectAttributes();
        if(overrideUrl){
            if(urlParameter){
                if(overrideUrl.indexOf("?")!==-1){
                    overrideUrl = overrideUrl + urlParameter;
                } else {
                    overrideUrl = overrideUrl + "?" + urlParameter.substring(1,urlParameter.length);
                }
            }
            return overrideUrl;
        }
        return newUrlToBeSet;
    }
    getSubjectAttribute(attributeName){
        let subject = null;
        for(var i=0; i < this.teammates.length; i++) {
            if(this.teammates[i].externalId == this.subjectId){
                subject = this.teammates[i];
                break;
            }
        }
        if(subject == null) return null;
        for(var j=0 ; j < subject.attributes.length; j ++){
            if(subject.attributes[j].attributeName == attributeName) {
                //print("Attribute found" + attributeName);
                return subject.attributes[j].stringValue;
            }
        }
        return null;
    }
    validateFinalUrl(newUrlToBeSet) {
        //check if there is HTTPS or HTTP in the URL.
        if ((newUrlToBeSet.indexOf("http") == -1)&&(newUrlToBeSet.indexOf("https")==-1)) {
            return newUrlToBeSet + '/' + this.subjectId;
        } else {
            if((newUrlToBeSet.indexOf("/sessions/")!=-1) && this.doneUrlParameter){
                return newUrlToBeSet + '?externalId=' + this.subjectId;
            } else {
                newUrlToBeSet = this.getOverrideURLIfSet(newUrlToBeSet);
            }
            return newUrlToBeSet;
        }


    }
    checkForFlowSyncIssue(){
        let now = new Date().getTime();
        if((now - this.latestFlowMessage) > 1000*10 ){
            this.handleSocketError("Have not received a flow in 10 seconds");
        }
    }
    onFlowBroadcastReceived(message) {

        this.latestFlowMessage = new Date().getTime();
        //console.log("MESSAGE RECEIVED: " + JSON.stringify(message));
       console.log("this.isGlobalChatPage: " + this.isGlobalChatPage)
        if(this.isGlobalChatPage){


            if(message.content.currentUrl.indexOf(this.triggerTaskForVideoChat)!=-1) {
                if(!this.isGlobalChatInitialized){
                    this.isGlobalChatInitialized = true;
                    $("#communication_to_show").show();
                    this.setupCommunication();
                    this.fire(null, 'onReady', this);
                }
            }
            if( this.countDown == null){
                this.nextUrl = message.content.nextUrl;
                this.nextUrl = this.validateFinalUrl(this.nextUrl);
                var finalDate = (new Date().getTime() + parseInt(
                    message.content.secondsRemainingCurrentUrl));
                this.countDown = new Countdown(finalDate,
                                               "countdown",
                                               this.onCountdownEnd.bind(this))
            }
            return;
        }
        if(!this.sessionIsPerpetual) {
            if ((message.content.currentUrl + "/" + this.subjectId == window.location.pathname)) {
                this.nextUrl = message.content.nextUrl;
                this.nextUrl = this.validateFinalUrl(this.nextUrl);
                var finalDate = (new Date().getTime() + parseInt(
                    message.content.secondsRemainingCurrentUrl));
                this.countDown.updateCountDownDate(finalDate)
            } else {
                if(!this.sessionExecutionMode) {
                    //console.log(message.content.currentUrl + "/" + this.subjectId)
                    if(!this.isGlobalChatPage) {
                        window.location = message.content.currentUrl + "/"
                            + this.subjectId;
                    }
                } else {
                    var finalDate = (new Date().getTime() + parseInt(
                        message.content.secondsRemainingCurrentUrl));
                    this.countDown.updateCountDownDate(finalDate)
                }
            }
        }


        if(this.sessionIsPerpetual) {
            console.log(JSON.parse(message.content.perpetualSubjectsChosen));
            this.perpetualSubjectsChosen = JSON.parse(message.content.perpetualSubjectsChosen);
            for(var i = 0; i < this.perpetualSubjectsChosen.length ; i ++){
                if(this.subjectId == this.perpetualSubjectsChosen[i]) {
                    window.location.href = "/check_in?externalId=" + this.subjectId;
                    //console.log("Linha 415")
                }
            }

        }
        //HANDLE EXPIRED WAITING ROOM.
        if(this.sessionIsPerpetual && window.location.pathname.indexOf("/sessions/start/") >=0 ){
            let now = new Date();
            if(parseInt(this.waitingRoomExpireTime) < now){
                 window.location.href = "/expired?externalId=" + this.subjectId;
                //console.log("Linha 425");
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
            try {
                this.stompClient.send(url, {}, JSON.stringify(chatMessage));
            } catch (exp){
                this.handleSocketError("Error in sending websocket message: " + exp);
            }
        }
        if (typeof event !== 'undefined') {
            event.preventDefault()
        }
    }
}



new Pogs();
console.log("Version 1.11.9");