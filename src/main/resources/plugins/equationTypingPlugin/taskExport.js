var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";


_headerColumns = "Equation Index;Last subject author; Answer";
for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("equationAnswer") != -1) {
        var index = _completedTaskAttributes[i].attributeName.replace("equationAnswer","");
        _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].lastAuthorSubject + ";" + _completedTaskAttributes[i].stringValue)
    }
}
exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;