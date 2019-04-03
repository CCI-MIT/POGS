'use strict';

class PogsPlugin {
    constructor(pluginName, initFunc, pogsRef){
        this.pluginName = pluginName;
        this.initFunc = initFunc;
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
            if(teammates[i].externalId == this.pogsRef.subjectId){
                teamm.push(teammates[i].displayName);
            }
        }
        return teamm;
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
    saveCompletedTaskAttribute(attributeName, stringValue, floatValue, intValue,
                                          loggable, extraData) {
        var messageContent = {
            attributeName: attributeName,
            attributeStringValue: stringValue,
            attributeDoubleValue: floatValue,
            attributeIntegerValue: intValue,
            loggableAttribute: loggable,
            mustCreateNewAttribute: false,
            broadcastableAttribute: true,
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


