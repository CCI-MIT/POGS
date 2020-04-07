var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Field Index;Field type;Last subject author; Answer; Ground Truth";
var answerSheet = null;

for(var i=0; i < _taskConfigurationAttributes.length; i++){
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
    }
}

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1) {
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer",""));
        var rightAnwser = "";
        if(answerSheet){
            rightAnwser = answerSheet[index];
        }
        _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"+_completedTaskAttributes[i].lastAuthorSubject + ";" + _completedTaskAttributes[i].stringValue + ";" + rightAnwser)
    }
}



exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;