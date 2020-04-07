var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Field Index;Field type;Last subject author; Answer; Ground Truth";
var answerSheet = null;
var surveyBluePrint = null;

for(var i=0; i < _taskConfigurationAttributes.length; i++){
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = JSON.parse(_taskConfigurationAttributes[i].stringValue);
    }
    if(_taskConfigurationAttributes[i].attributeName == "surveyBluePrint"){
        surveyBluePrint = JSON.parse(_taskConfigurationAttributes[i].stringValue);
    }
}

print(JSON.stringify(surveyBluePrint));

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1) {
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer",""));
        var rightAnwser = "";
        if(answerSheet){
            rightAnwser = answerSheet[index];
            print(rightAnwser)
            print(typeof  rightAnwser);
        }
        if(_completedTaskAttributes[i].extraData == "RadioTableField"){
            var givenAnswersArray = JSON.parse(_completedTaskAttributes[i].stringValue);
            var groundTruthArray = rightAnwser;
            for(var k=0; k < givenAnswersArray.length; k ++) {
                if(givenAnswersArray[k]!="") {
                    print("(" + groundTruthArray[k] + ")");
                    _exportRecordLines.push(
                        index + "." + (k + 1) + ";" + _completedTaskAttributes[i].extraData + ";"
                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                        + givenAnswersArray[k] + ";"
                        + groundTruthArray[k])
                }
            }
        } else {
            _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"
                                    + _completedTaskAttributes[i].lastAuthorSubject + ";"
                                    + _completedTaskAttributes[i].stringValue + ";" + rightAnwser)
        }
    }
}



exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;