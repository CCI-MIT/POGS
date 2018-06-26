'use strict';

class Pogs {
    constructor(){
        window.pogs = this;
        this.handlers = {};  // observers
        this.plugins = [];  // observers
        this.task = null;  // observers
        this.team = null;  // observers
    }
    createPlugin(pluginName, initFunc) {
        var pl = new PogsPlugin(pluginName, initFunc, this);
        this.plugins.push(pl);
        this.subscribe('onReady', pl.initFunc.bind(pl));
        return pl;
    }
    setup (config) {

        this.stompClient = null;
        this.sessionId = config.sessionId;
        this.subjectId = config.subjectId;
        this.completedTaskId = config.completedTaskId;
        this.teammates = config.teammates;

        this.hasCollaboration = config.hasCollaboration;

        this.hasChat = config.hasChat;
        this.communicationType = config.communicationType;


        this.nextUrl = config.nextUrl+ "/"+ this.subjectId;
        this.secondsRemainingCurrentUrl = (new Date().getTime() +
                                           parseInt(config.secondsRemainingCurrentUrl));
        this.taskConfigurationAttributes = config.taskConfigurationAttributes;

        this.taskConfigurationAttributesMap = new Map();
        if(typeof(this.taskConfigurationAttributes) != "undefined") {
            for (var i = 0; i < this.taskConfigurationAttributes.length; i++) {
                this.taskConfigurationAttributesMap.set(
                    this.taskConfigurationAttributes[i].attributeName,
                    this.taskConfigurationAttributes[i]);
            }
        }


        this.initializeWebSockets();

        this.countDown = new Countdown(this.secondsRemainingCurrentUrl, "countdown",
                                       function () {
                                           window.location = this.nextUrl
                                       }.bind(this))

        this.subscribe('flowBroadcast', this.onFlowBroadcastReceived.bind(this));

        this.setupCommunication();
        this.setupCollaboration();

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
        this.fire(null, 'onReady', this);
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

        //schedule check-in ping

        // Tell your username to the server
//        this.stompClient.send("/app/chat.addUser",
//                         {},
//                         JSON.stringify({sender: username, type: 'JOIN'})
//        );
    }
    onError(error) {
        this.fire(null, 'onReady', this);
    }
    onFlowBroadcastReceived(message) {

        this.nextUrl = message.content.nextUrl;
        if(message.content.nextUrl.indexOf("http") == -1) {
            this.nextUrl = this.nextUrl + '/' + this.subjectId;
        }
        var finalDate = (new Date().getTime() + parseInt(
            message.content.secondsRemainingCurrentUrl));
        this.countDown.updateCountDownDate(finalDate)

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
        event.preventDefault();
    }
}

//TODO: put it in an external file
class Countdown{
    constructor(countDownDate, htmlReference, finalFunction){
    // Update the count down every 1 second
        this.countDownDate = countDownDate;
        this.htmlReference = htmlReference;
        this.finalFunction = finalFunction;

        function trailingZeros(val) {
            if (val < 10) {
                return '0' + val;
            } else {
                return val;
            }
        }

        var x = setInterval(function () {

            // Get todays date and time
            var now = new Date().getTime();

            // Find the distance between now an the count down date
            var distance = this.countDownDate - now;

            // Time calculations for days, hours, minutes and seconds
            var days = Math.floor(distance / (1000 * 60 * 60 * 24));
            var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((distance % (1000 * 60)) / 1000);

            if (isNaN(days)) {
                days = 0;
            }
            if (isNaN(hours)) {
                hours = 0;
            }
            if (isNaN(minutes)) {
                minutes = 0;
            }
            if (isNaN(seconds)) {
                seconds = 0;
            }

    //
            //trailingZeros
            if (distance > 0) {
                if (document.getElementById(this.htmlReference) != null) {
                    document.getElementById(this.htmlReference).innerHTML =
                        ((minutes > 0) ? (trailingZeros(minutes.toString()) + ':' ) : (''))
                        + trailingZeros(seconds.toString())
                        + ((minutes > 0) ? (' minutes')
                        : (' seconds'));
                }
            } else {
                if (document.getElementById(this.htmlReference) != null) {
                    document.getElementById(this.htmlReference).innerHTML = "Redirecting ...";
                }
            }

            // If the count down is finished, write some text
            if (distance < 0) {
                clearInterval(x);
                this.finalFunction.call(pogs);
            }
        }.bind(this), 1000);
    }
    updateCountDownDate(countDownDate) {
        this.countDownDate = countDownDate;
    }
}

new Pogs();