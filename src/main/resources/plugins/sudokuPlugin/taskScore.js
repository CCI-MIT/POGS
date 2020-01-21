var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);


var answerSheet;
var bluePrint;

var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = 0;


for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = _taskConfigurationAttributes[i].stringValue.split(",");
    }
    if(_taskConfigurationAttributes[i].attributeName == "gridBluePrint"){
        bluePrint = _taskConfigurationAttributes[i].stringValue.split(",");
    }
}

var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};

var answerKeyMap = [];


for(var k =0 ; k < answerSheet.length; k ++ ) {
    answerKeyMap[k] = "";
}

for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if(_completedTaskAttributes[i].attributeName.indexOf("sudokuAnswer_") != -1){
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("sudokuAnswer_",""));
        var answer = _completedTaskAttributes[i].stringValue;
        answerKeyMap[index] = answer;
    }
}

for(var i=0 ;i < answerSheet.length; i++) {

    if(bluePrint[i] == 0) {
        _completedTaskScore.numberOfEntries++;
        _completedTaskScore.numberOfProcessedEntries++;
        if (answerSheet[i] == answerKeyMap[i]) {
            _completedTaskScore.numberOfRightAnswers++;
            _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
        } else {
            _completedTaskScore.numberOfWrongAnswers++;
            _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
        }
    }
}


completedTaskScore = JSON.stringify(_completedTaskScore);