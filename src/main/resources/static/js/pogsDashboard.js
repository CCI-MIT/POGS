'use strict';

const COLLABORATION_TYPE = {
    TODO_LIST: "TODO_LIST",
    VOTING_LIST: "VOTING_LIST",
    FEEDBACK_BAR: "FEEDBACK_BAR"
};
const VOTING_TYPE = {
    CREATE_VOTING_POOL: "CREATE_VOTING_POOL",
    CAST_VOTE: "CAST_VOTE",
    DELETE_VOTING_POOL: "DELETE_VOTING_POOL",
    CREATE_OPTION: "CREATE_OPTION",
    DELETE_OPTION: "DELETE_OPTION"
};

const TODO_TYPE = {

    CREATE_TODO: "CREATE_TODO",
    ASSIGN_ME: "ASSIGN_ME",
    UNASSIGN_ME: "UNASSIGN_ME",
    DELETE_TODO: "DELETE_TODO",
    MARK_DONE: "MARK_DONE",
    MARK_UNDONE: "MARK_UNDONE"

};

class PogsDashboard {
    constructor() {
        window.pogs = this;
        this.handlers = {};  // observers
        this.plugins = [];  // observers
        this.task = null;
        this.taskCommunicationCount = null;
        this.team = null;
        this.completedTasksByTeam = null;
        this.subjectColorMap = {};
    }
    generateRandomColor(){
       var colorStr= ["#FFB300",    // Vivid Yellow
        "#803E75",    // Strong Purple
        "#FF6800",    // Vivid Orange
        "#A6BDD7",    // Very Light Blue
        "#C10020",    // Vivid Red
        "#CEA262",    // Grayish Yellow
        "#817066",    // Medium Gray
        "#007D34",    // Vivid Green
        "#F6768E",    // Strong Purplish Pink
        "#00538A",    // Strong Blue
        "#FF7A5C",    // Strong Yellowish Pink
        "#53377A",    // Strong Violet
        "#FF8E00",    // Vivid Orange Yellow
        "#B32851",    // Strong Purplish Red
        "#F4C800",    // Vivid Greenish Yellow
        "#7F180D",    // Strong Reddish Brown
        "#93AA00",    // Vivid Yellowish Green
        "#593315",    // Deep Yellowish Brown
        "#F13A13",    // Vivid Reddish Orange
        "#232C16" ];   // Dark Olive Green

       return colorStr[this.getRandomInt(colorStr.length)]
    }
    getRandomInt(max) {
        var min = Math.ceil(0);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
    }
    setup(config){
        this.stompClient = null;
        this.sessionId = config.sessionId;
        this.eventsUntilNow = config.eventsUntilNow;
        this.completedTasksByTeam = config.completedTasksByTeam;
        this.initializeWebSockets();
        this.subscribe('flowBroadcast', this.onFlowBroadcastReceived.bind(this));
        this.subscribe('checkIn', this.onCheckInReceived.bind(this));
        this.countDown = null
    }
    initializeWebSockets () {

        var socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {};

        this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
    }
    onConnected() {

        this.subscribeTopicAndFireEvent(('/topic/public/flow/' + this.sessionId),
                                        'FLOW_BROADCAST', 'flowBroadcast');

        this.subscribeTopicAndFireEvent(
            '/topic/public/checkin/' + this.sessionId,
            'CHECK_IN', 'checkIn');
    }
    subscribe(eventName, fn) {
        if (!(eventName in this.handlers)) {
            this.handlers[eventName] = [];
        }
        this.handlers[eventName].push(fn);
    }
    purgeSubscribers(eventName){
        delete this.handlers[eventName];
    }
    fire(o, eventName, thisObj) {
        var scope = thisObj || window;
        if (eventName in this.handlers) {
            this.handlers[eventName].forEach(function (item) {
                item.call(scope, o);
            });
        }
    }
    onNewTaskStarted(){

        this.taskCommunicationCount = [];
        var teamsCompTasks = this.completedTasksByTeam[this.task];
        //console.log("Listening to  task "+this.task+" has : " + teamsCompTasks.length);
        for(var i =0; i < teamsCompTasks.length ; i++) {
            var completedTask = teamsCompTasks[i].completedTaskId;
            //console.log("Completed task Id: " + completedTask);
            this.taskCommunicationCount[completedTask] = 0;
            this.subscribeTopicAndFireEvent(
                '/topic/public/task/' + completedTask
                + '/feedback',
                'FEEDBACK_MESSAGE', 'feedbackBroadcastMessage');


            this.subscribeTopicAndFireEvent(
                '/topic/public/task/' + completedTask
                + '/communication',
                'COMMUNICATION_MESSAGE', 'communicationMessage');


            this.subscribeTopicAndFireEvent(
                '/topic/public/task/' + completedTask
                + '/collaboration',
                'COLLABORATION_MESSAGE', 'collaborationMessage');

        }
        this.purgeSubscribers("communicationMessage");
        this.purgeSubscribers("collaborationMessage");

        this.subscribe("communicationMessage", this.onCommunicationMessageReceived.bind(this))
        this.subscribe("collaborationMessage", this.onCollaborationMessageReceived.bind(this))
    }
    onCollaborationMessageReceived(message){
        if(message.content.collaborationType == COLLABORATION_TYPE.TODO_LIST){
            if (message.content.triggeredBy != TODO_TYPE.DELETE_TODO &&
                message.content.triggeredBy != TODO_TYPE.UNASSIGN_ME) {
                var todoEntries = message.content.todoEntries;
                var teamId = this.getTeamByCompletedTask(message.completedTaskId);
                console.log("Team id" + teamId + "   -  completedTaskId: " + message.completedTaskId);
                if(teamId != null) {
                    $("#" +this.lastReference + teamId + "_collab_todo").text(todoEntries.length);
                }
            }

            return;
        }
        if(message.content.collaborationType == COLLABORATION_TYPE.VOTING_LIST){
            var votingPools = message.content.votingPools;
            console.log("Voting pools: " + votingPools.length);

            if (message.content.triggeredBy != VOTING_TYPE.DELETE_OPTION ||
                message.content.triggeredBy != VOTING_TYPE.DELETE_VOTING_POOL) {

                    var votingPools = message.content.votingPools;
                    var teamId = this.getTeamByCompletedTask(message.completedTaskId);
                    if(teamId != null) {
                        $("#" +this.lastReference + teamId + "_collab_voting").text(votingPools.length);
                    }
                }
            return;
        }
        if (message.content.collaborationType == COLLABORATION_TYPE.FEEDBACK_BAR) {

            for (var i = 0; i < message.content.completedTasks.length; i++) {

                    var subjectFeedbacks = message.content.completedTasks[i].subjectFeedbacks;
                    var totalInteractions = message.content.completedTasks[i].totalInteractions;
                    var teamId = this.getTeamByCompletedTask(message.content.completedTasks[i].completedTaskId);
                    for (var j = 0; j < subjectFeedbacks.length; j++) {
                        var externalId = subjectFeedbacks[j].externalId;
                        var interaction = subjectFeedbacks[j].interaction;
                        var percentage = subjectFeedbacks[j].percentage;
                        var taskurl = this.lastReference;
                        if ($("#" +teamId +taskurl+ externalId + '_feedback_progressbar').length > 0) {
                            this.updateSubjectEntry(teamId,taskurl,externalId, interaction, percentage);
                        } else {
                            this.createSubjectEntry(teamId,taskurl,externalId, interaction, percentage);
                        }
                    }
                    $("#"+teamId +taskurl +'totalInteractions').text(totalInteractions);
                }
        }

    }

    updateSubjectEntry(teamId,taskurl, externalId, value, percentage) {
        $("#" +teamId+taskurl+externalId +'_feedback_progressbar').width(percentage + "%");
        $("#" +teamId+taskurl+externalId +'_feedback_progressbar').attr("aria-valuenow",percentage);
    }
    createSubjectEntry(teamId,taskurl, externalId, value, percentage) {
        $('<div id="'+teamId+taskurl+externalId+'_feedback_progressbar" class="progress-bar" role="progressbar" '
          + 'style="width: '+percentage+'%;background-color:'+this.generateRandomColor()+'" aria-valuenow="'+percentage+'" aria-valuemin="0" '
          + 'aria-valuemax="100">'+
          externalId
          +'</div>').appendTo("#"+teamId + this.lastReference + "progress .progress");

    }

    updateCommunicationCount(completedTask){

        this.taskCommunicationCount[completedTask]++;

        var teamId = this.getTeamByCompletedTask(completedTask);
        if(teamId != null) {
            $("#" + this.lastReference+ teamId + "_communication").text(this.taskCommunicationCount[completedTask]);
        }
    }
    getTeamByCompletedTask(completedTask) {
        var teamsCompTasks = this.completedTasksByTeam[this.task];
        if(teamsCompTasks)
        for(var i =0; i < teamsCompTasks.length ; i++) {
            var completedTaskTemp = teamsCompTasks[i].completedTaskId;
            if(completedTaskTemp == completedTask) {
                return teamsCompTasks[i].teamId;
            }
        }
        return null;
    }
    onCommunicationMessageReceived(message){

        if (message.content.type == "MESSAGE") {

            if (message.content.message != "") {
                console.log("Completed Task: " + message.completedTaskId);
                console.log("Are we counting twice? Why would we receive the event 2 times? two listeners?");
                console.log("Message sender: " + message.sender);
                this.updateCommunicationCount(message.completedTaskId);
            }
        }
    }
    onCheckInReceived(message){


        var urlRef = "#" + message.content.message.replace("/"+message.sender,"").replace(/\//g,"_");

        var subjectRef = $(urlRef + " .subjectContainer ." + message.sender + "_pill");
        if(!subjectRef.length){

            $(urlRef + " .subjectContainer ").append(
            '<span class="badge-pill badge-success subjectpill ' +message.sender+'_pill">'
            +message.sender+'</span>');
        } else {
            if(!subjectRef.hasClass("badge-success")){
                subjectRef.removeClass("badge-dark");
                subjectRef.addClass("badge-success");
            }
        }
    }
    onFlowBroadcastReceived(message) {

        //console.log(" URL: " + message.content.nextUrl)
        //console.log(" ID" + message.content.currentUrl.replace(/\//g,"_"));
        //console.log($("#" +message.content.currentUrl.replace(/\//g,"_")));
        var currentUrl = message.content.currentUrl.replace(/\//g,"_");

        $("#" + currentUrl + ' .card-body').addClass("cardLive");
        this.secondsRemainingCurrentUrl = (new Date().getTime() + parseInt(
            message.content.secondsRemainingCurrentUrl));

        if(this.lastReference == null){
            this.lastReference = currentUrl;
        }

        if(this.countDown == null){
            this.createCountDown(currentUrl);
        }
        if(!(currentUrl.indexOf("_w") > 0)){
            this.task == null;
            console.log("Not a work task page");
        }

        if(currentUrl.indexOf("_w") > 0 && this.task == null){

            var end = currentUrl.indexOf("_w");
            var start = currentUrl.indexOf("_task_");
            var taskId = currentUrl.substring(start + 6,end);
            this.task = taskId;
            this.onNewTaskStarted();
            console.log("Registering listeners to TASK ID: " + taskId);
        }

        if( this.lastReference != currentUrl){
            //console.log("Flow changed URL prev:" + this.lastReference + " - current: " + currentUrl);

            if(this.lastReference.indexOf("_waiting_room")>=0) {
                location.reload();
            }

            $("#" +this.lastReference +" .card-body")
                .removeClass("cardLive").addClass("cardPassed");

            this.lastReference = currentUrl;

            //console.log("Creating countdown with reference: " + currentUrl+"countdown");
            this.createCountDown(currentUrl);


        }

    }
    createCountDown(currentUrl){
        this.countDown = new Countdown(this.secondsRemainingCurrentUrl, currentUrl+"countdown",
                                       function () {
                                           this.countDown == null;
                                           this.task = null;

                                           console.log("Last reference was waiting room :" + this.lastReference);


                                           //$("#" +this.lastReference +" .card-body")
                                           //    .removeClass("cardLive").addClass("cardPassed");
                                       }.bind(this));
        this.countDown.updateFinalMessage("DONE");
    }
    onError(error) {
        this.fire(null, 'onError', this);
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
};
window.pogsDashboard = new PogsDashboard();