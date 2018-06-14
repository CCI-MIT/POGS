function Pogs(){
    window.pogs = this;
    this.handlers = {};  // observers
    this.plugins = [];  // observers
    this.task = null;  // observers
    this.team = null;  // observers

}
new Pogs();

Pogs.prototype = {

    createPlugin : function (pluginName,initFunc) {
        var pl = new PogsPlugin(pluginName,initFunc, this);
        this.plugins.push(pl);
        this.subscribe('onLoad',pl.initFunc.bind(pl));

        return pl;
    },
    setup : function(config) {


            this.stompClient = null;
            this.sessionId = config.sessionId;
            this.subjectId = config.subjectId;
            this.completedTaskId = config.completedTaskId;
            this.hasCollaboration = config.hasCollaboration;
            this.hasChat = config.hasChat;
            this.nextUrl = null;
            this.secondsRemainingCurrentUrl = config.secondsRemainingCurrentUrl;
            this.initializeWebSockets();

            this.countDown = new Countdown(finalDate, "countdown",
                                           function () {
                                               window.location = this.nextUrl
                                           }.bind(this))

            this.subscribe('flowBroadcast', this.onFlowBroadcastReceived.bind(this));


            this.fire(null, 'onLoad', this);
    },
    initializeWebSockets: function () {

        var socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
    },
    subscribe: function (eventName, fn) {
        if (!(eventName in this.handlers)) {
            this.handlers[eventName] = [];
        }
        this.handlers[eventName].push(fn);
    },
    fire: function (o, eventName, thisObj) {
        var scope = thisObj || window;
        if (eventName in this.handlers) {
            this.handlers[eventName].forEach(function (item) {
                item.call(scope, o);
            });
        }
    },
    subscribeTopicAndFireEvent: function (topicUrl, messageType, eventName) {
        this.stompClient.subscribe(topicUrl,
                                   function (payload) {
                                       var message = JSON.parse(payload.body);
                                       var messageContent = JSON.parse(message.content);
                                       message.content = messageContent;
                                       if (message.type === messageType) {
                                           this.fire(message, eventName, this);
                                       }
                                   }.bind(this));
    },
    onConnected: function () {

        this.subscribeTopicAndFireEvent('/topic/public/flow/' + this.sessionId,
            'FLOW_BROADCAST', 'flowBroadcast');

        if (this.completedTaskId != null) {

            this.subscribeTopicAndFireEvent(
                '/topic/public/task/' + this.completedTaskId
                + '/work/',
                'TASK_ATTRIBUTE', 'taskAttributeBroadcast');

            if (this.hasChat) {
                this.subscribeTopicAndFireEvent(
                    '/topic/public/task/' + this.completedTaskId
                    + '/communication/',
                    'COMMUNICATION_MESSAGE', 'communicationMessage');
            }
            //if collaboration subscribe to collab
            if (this.hasCollaboration) {
                this.subscribeTopicAndFireEvent(
                    '/topic/public/task/' + this.completedTaskId
                    + '/collaboration/',
                    'COLLABORATION_MESSAGE', 'collaborationMessage');
            }
        }

        //schedule check-in ping

        // Tell your username to the server
//        this.stompClient.send("/app/chat.addUser",
//                         {},
//                         JSON.stringify({sender: username, type: 'JOIN'})
//        );
    },
    onError: function (error) {

    },
    onFlowBroadcastReceived: function (message) {
        this.nextUrl = message.content.nextUrl + '/' + this.subjectId;
        var finalDate = (new Date().getTime() + parseInt(
            message.content.secondsRemainingCurrentUrl));

            this.countDown.updateCountDownDate(finalDate)


    },
    sendMessage: function (url, type, messageContent, sender, receiver, completedTaskId,
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
function Countdown(countDownDate, htmlReference, finalFunction) {
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

        if(isNaN(days)) days = 0;
        if(isNaN(hours)) hours = 0;
        if(isNaN(minutes)) minutes = 0;
        if(isNaN(seconds)) seconds = 0;

//
        //trailingZeros
        document.getElementById(this.htmlReference).innerHTML =
            ((minutes > 0) ? (trailingZeros(minutes.toString()) + ':' ) : (''))
            + trailingZeros(seconds.toString())
            + ((minutes > 0) ? (' minutes')
            : (' seconds'));

        // If the count down is finished, write some text
        if (distance < 0) {
            clearInterval(x);
            this.finalFunction.call(pogs);
        }
    }.bind(this), 1000);
}
Countdown.prototype = {
    updateCountDownDate: function (countDownDate) {
        this.countDownDate = countDownDate;
    }
}