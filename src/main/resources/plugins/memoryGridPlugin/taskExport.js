var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";


_headerColumns = "Cell Index;Last subject author; Answer;Ground Truth";

for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
    }
}

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("memoryGridAnswer") != -1) {
        var index = _completedTaskAttributes[i].attributeName.replace("memoryGridAnswer","");
        _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].lastAuthorSubject + ";" + _completedTaskAttributes[i].stringValue + ";" +answerSheet[index])
    }
}
exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;