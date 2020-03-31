var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";


_headerColumns = "Cell Index;Last subject author; Answer; Ground Truth";
var answerSheet = null;

for(var i=0; i < _taskConfigurationAttributes.length; i++){
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = _taskConfigurationAttributes[i].stringValue.split(",");
        break;
    }
}

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("sudokuAnswer_") != -1) {
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("sudokuAnswer_",""));
        var rightAnwser = "";
        if(answerSheet){
            rightAnwser = answerSheet[index];
        }
        _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].lastAuthorSubject + ";" + _completedTaskAttributes[i].stringValue + ";" + rightAnwser)
    }
}
exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;