'use strict';

class PogsPlugin {

    constructor(pluginName, initFunc, pogsRef, destroyFunc){
        this.pluginName = pluginName;
        this.initFunc = initFunc;
        if(destroyFunc) {
            this.destroyFunc = destroyFunc;
        } else {
            this.destroyFunc = function(){ console.log("Plug in destroy method")};
        }
        this.pogsRef = pogsRef;

    }
    getSubjectId() {
        return this.pogsRef.subjectId;
    }
    getSessionName(){
        return this.pogsRef.sessionName;
    }

    getTaskList(){
        var taskList = []
        for(var i =0; i< this.pogsRef.taskList.length; i ++){
            taskList.push(this.pogsRef.taskList[i].taskName);
        }
        return taskList;
    }
    getOtherTasks(){
        var taskList = []
        for(var i =0; i< this.pogsRef.taskList.length; i ++){
            if(this.pogsRef.taskList[i].id != this.pogsRef.task) {
                taskList.push(this.pogsRef.taskList[i].taskName);
            }
        }
        return taskList;
    }
    getLastTask(){
        return this.pogsRef.lastTask;
    }
    getTeammatesDisplayNames() {
        var teamm = [];
        var teammates = this.getTeammates();
        for(var i = 0; i < teammates.length; i ++) {
                teamm.push(teammates[i].displayName);
        }
        return teamm;
    }
    getOtherTeammates() {
        var teamm = [];
        var teammates = this.getTeammates();
        for(var i = 0; i < teammates.length; i ++) {
            if(teammates[i].externalId != this.pogsRef.subjectId){
                teamm.push(teammates[i].displayName);
            }
        }
        return teamm;
    }
    getCurrentSubjectVideoChatCredential() {
        let attr = this.getTeammateAttribute(this.getSubjectId(),"JITSI_JWT_TOKEN");
        return (attr)?(attr.stringValue):(null);
    }
    getCurrentSubjectShouldStartRecording() {
        return (this.getTeammateAttribute(this.getSubjectId(),"JITSI_JWT_START_REC")!=null);
    }
    getTeammateAttribute(subjectId, propertyName){
        if(!this.getTeammates()) return;

        let subject = this.getSubjectByExternalId(subjectId);
        if(subject.attributes){
            for (var j = 0; j < subject.attributes.length; j++) {
                if (subject.attributes[j].attributeName == propertyName) {
                    return subject.attributes[j];
                }
            }
        }
        return null;

    }
    getTeammates(){
        return this.pogsRef.teammates;
    }
    getCompletedTaskId(){
        return this.pogsRef.completedTaskId;
    }
    getSessionId(){
        return this.pogsRef.sessionId;
    }
    sendMessage(url, type, messageContent, sender, receiver, completedTaskId,
                           sessionId) {
        this.pogsRef.sendMessage(url, type, messageContent, sender, receiver, completedTaskId,
                                 sessionId);
    }
    sendOperation(operation) {
        this.sendMessage('/pogsapp/ot.operations.submit', 'OPERATION', JSON.stringify(operation),
            this.pogsRef.subjectId, null, this.pogsRef.completedTaskId, this.pogsRef.sessionId);
    }
    isSoloTask(){
        return this.pogsRef.taskIsSolo;
    }
    getSubjectByExternalId(externalId){
        var teammates = this.getTeammates();
        for(var i = 0; i < teammates.length; i ++) {
            if(teammates[i].externalId == externalId){
                return teammates[i];
            }
        }
        return null;
    }



    subscribeTaskAttributeBroadcast (funct) {
        this.pogsRef.subscribe('taskAttributeBroadcast', funct);
    }

    /*
        This method has a usage very similar to that of the saveCompletedTaskAttribute(...) method with the exception
        that  each call to this method with the same attributeName value would not cause an override of the value.
        An example can be when we want to score different participants in a joint task - calls from the client of all the
        subject would have the same attributeName but, we would store them as individual entries in the database with
        each entry representing the score of each client.
    */
    saveCompletedTaskAttributeMustCreateNew(attributeName, stringValue, floatValue, intValue,
                               loggable, extraData,summaryDescription) {
        var messageContent = {
            attributeName: attributeName,
            attributeStringValue: stringValue,
            attributeDoubleValue: floatValue,
            attributeIntegerValue: intValue,
            loggableAttribute: ((this.pogsRef.recordSessionSaveEphemeralEvents)?(true):(loggable)),
            mustCreateNewAttribute: true,
            broadcastableAttribute: true,
            shouldUpdateExistingAttribute: true,
            summaryDescription: summaryDescription,
            extraData: extraData
        };

        this.pogsRef.sendMessage("/pogsapp/task.saveAttribute", "TASK_ATTRIBUTE", messageContent,
                                 this.getSubjectId(), null, this.getCompletedTaskId(),
                                 this.getSessionId());
    }

    /*
        This method broadcasts a completed task attribute to all the users attempting the task. Depending on the
        arguments passed to the method, it may also save the completed task attribute and log it in the event log.
        // TODO: e.g. hover -> only broadcast, no persistence and survey answer response -> broadcast + persistence

        attributeName: this is a unique identifier for the completed task attribute. Two different executions of a
        (completed) task may use the same unique identifier without overriding the value stored against the completed
        task attribute. For a task running in solo mode, calls to this method would only override the value stored against
        a completed task attribute if they are received from the same client. The method calls with the same completed
        task attribute received from two different clients will be treated as separate entries. For a task NOT running in solo
        mode, every call to this method with a given attributeName would overwrite the entry regardless of the client that
        it is received from.
        stringValue: a string value stored with the attributeName as a key
        floatValue: a float value stored with the attributeName as a key
        intValue: an integer value stored with the attributeName as a key
        loggable: boolean; we could potentially have a change-log to be able to record overwrites;
                  if recordSessionSaveEphemeralEvents is set, this argument will be disregarded and all events will be
                  recorded
                  recordSessionSaveEphemeralEvents is fixed at run-time
        extraData: an additional string value that can be used as metadata for adding any additional business logic
        summaryDescription: human-readable description of the event (meant only for logging?)
    */
    saveCompletedTaskAttribute(attributeName, stringValue, floatValue, intValue,
                                          loggable, extraData,summaryDescription) {
        var messageContent = {
            attributeName: attributeName,
            attributeStringValue: stringValue,
            attributeDoubleValue: floatValue,
            attributeIntegerValue: intValue,
            loggableAttribute: ((this.pogsRef.recordSessionSaveEphemeralEvents)?(true):(loggable)),
            mustCreateNewAttribute: false,
            broadcastableAttribute: true,
            shouldUpdateExistingAttribute: true,
            summaryDescription: summaryDescription,
            extraData: extraData
        };

        this.pogsRef.sendMessage("/pogsapp/task.saveAttribute", "TASK_ATTRIBUTE", messageContent,
                                 this.getSubjectId(), null, this.getCompletedTaskId(),
                                 this.getSessionId());
    }

    /*
        This method has a usage very similar to that of the saveCompletedTaskAttribute(...) method with the exception that
        the value against a given attributeName is not overridden. One example of this is a timed sub-task in which we can have
        only one "winner" e.g. choosing a number from 0-9 to start a task. In this case all the clients would attempt to call this
        method but, only the first one to make the call would be able to store the value against the attributeName and all the other
        calls would simply do nothing. In the saveCompletedTaskAttribute(...) method the last call to the method would override the
        value stored against the given attributeName
    */
    saveCompletedTaskAttributeWithoutOverride(attributeName, stringValue, floatValue, intValue,
                               loggable, extraData,summaryDescription) {
        var messageContent = {
            attributeName: attributeName,
            attributeStringValue: stringValue,
            attributeDoubleValue: floatValue,
            attributeIntegerValue: intValue,
            loggableAttribute: ((this.pogsRef.recordSessionSaveEphemeralEvents)?(true):(loggable)),
            mustCreateNewAttribute: false,
            broadcastableAttribute: true,
            shouldUpdateExistingAttribute: false,
            summaryDescription: summaryDescription,
            extraData: extraData
        };

        this.pogsRef.sendMessage("/pogsapp/task.saveAttribute", "TASK_ATTRIBUTE", messageContent,
                                 this.getSubjectId(), null, this.getCompletedTaskId(),
                                 this.getSessionId());
    }

    /*
        This method has a usage very similar to that of the saveCompletedTaskAttribute(...) method with the exception
        that the value for a given attributeName received from one client is not broadcast to the rest of the clients.
        One example of this is group tasks such as a Jeopardy game in which we may not want every participants responses
        to be broadcast to the clients of other participants.
    */
    saveCompletedTaskAttributeWithoutBroadcast(attributeName, stringValue, floatValue, intValue,
                               loggable, extraData, summaryDescription) {
        var messageContent = {
            attributeName: attributeName,
            attributeStringValue: stringValue,
            attributeDoubleValue: floatValue,
            attributeIntegerValue: intValue,
            loggableAttribute: ((this.pogsRef.recordSessionSaveEphemeralEvents)?(true):(loggable)),
            mustCreateNewAttribute: false,
            broadcastableAttribute: false,
            summaryDescription : summaryDescription,
            extraData: extraData
        };

        this.pogsRef.sendMessage("/pogsapp/task.saveAttribute", "TASK_ATTRIBUTE", messageContent,
            this.getSubjectId(), null, this.getCompletedTaskId(),
            this.getSessionId());
    }


    getConfigurationAttributes() {
        return this.pogsRef.taskConfigurationAttributesMap;
    }
    getStringAttribute(attributeName) {

        if (this.pogsRef.taskConfigurationAttributesMap.has(attributeName)) {
            return String(
                this.pogsRef.taskConfigurationAttributesMap.get(attributeName).stringValue);
        } else {
            return null;
        }
    }
    getFloatAttribute(attributeName) {
        if (this.pogsRef.taskConfigurationAttributesMap.has(attributeName)) {
            return parseFloat(
                this.pogsRef.taskConfigurationAttributesMap.get(attributeName).doubleValue);
        } else {
            return null;
        }
    }

    getIntAttribute(attributeName) {
        if (this.pogsRef.taskConfigurationAttributesMap.has(attributeName)) {
            return parseInt(
                this.pogsRef.taskConfigurationAttributesMap.get(attributeName).integerValue);
        } else {
            return null;
        }
    }
    getBooleanAttribute(attributeName) {
        if (this.pogsRef.taskConfigurationAttributesMap.has(attributeName)) {
            return parseInt(
                this.pogsRef.taskConfigurationAttributesMap.get(attributeName).integerValue) == 1;
        } else {
            return false;
        }
    }

    getCompletedTaskStringAttribute(attributeName) {

        if (this.pogsRef.completedTaskAttributesMap.has(attributeName)) {
            return String(
                this.pogsRef.completedTaskAttributesMap.get(attributeName).stringValue);
        } else {
            return null;
        }
    }
    getCompletedTaskFloatAttribute(attributeName) {
        if (this.pogsRef.completedTaskAttributesMap.has(attributeName)) {
            return parseFloat(
                this.pogsRef.completedTaskAttributesMap.get(attributeName).doubleValue);
        } else {
            return null;
        }
    }

    getCompletedTaskIntAttribute(attributeName) {
        if (this.pogsRef.completedTaskAttributesMap.has(attributeName)) {
            return parseInt(
                this.pogsRef.completedTaskAttributesMap.get(attributeName).integerValue);
        } else {
            return null;
        }
    }
    getCompletedTaskBooleanAttribute(attributeName) {
        if (this.pogsRef.completedTaskAttributesMap.has(attributeName)) {
            return parseInt(
                this.pogsRef.completedTaskAttributesMap.get(attributeName).integerValue) == 1;
        } else {
            return false;
        }
    }
}

const MAXLENGH = 10;

function trimDisplayName(str){
    return str.substring(0, MAXLENGH);
}

