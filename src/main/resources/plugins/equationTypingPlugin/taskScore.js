var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);




var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = 0;

for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "gridBluePrint"){
        gridBluePrint = JSON.parse(_taskConfigurationAttributes[i].stringValue);
    }
    break;
}
var shouldReuseMember = gridBluePrint.shouldReuseMembers;
var totalSum = parseInt(gridBluePrint.totalSum);

var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};

var equations = [];


for (var i = 0; i < _completedTaskAttributes.length; i++) {
    if (_completedTaskAttributes[i].attributeName.indexOf("equationAnswer") != -1) {
        var index = parseInt(
            _completedTaskAttributes[i].attributeName.replace("equationAnswer", ""));
        var answer = _completedTaskAttributes[i].stringValue;
        equations[index] = answer;
    }
}
var lastRoundMembers = [];

var NUMERIC_REGEXP = /[-]{0,1}[\d]*[\.]{0,1}[\d]+/g;

for(var i=0 ;i < equations.length; i++) {

    _completedTaskScore.numberOfEntries++;
    _completedTaskScore.numberOfProcessedEntries++;

    var result = eval(equations[i]);
    var reusedMember = false;
    var currentEquationNumbers = [];
    if(shouldReuseMember) {
        currentEquationNumbers = equations[i].match(NUMERIC_REGEXP);
        if(i == 0) {
            lastRoundMembers = currentEquationNumbers;
            reusedMember = true;
        } else {
            if(currentEquationNumbers && currentEquationNumbers.length > 0) {
                for (var j = 0; j < currentEquationNumbers.length; j++) {
                    for(var k=0; k < lastRoundMembers.length; k++){
                        if(lastRoundMembers[k] == currentEquationNumbers[j]){
                            reusedMember = true;
                            lastRoundMembers =  currentEquationNumbers;
                            break;
                        }
                    }
                    if(reusedMember == true) {
                        break;
                    }
                    lastRoundMembers =  currentEquationNumbers;
                }
            }
        }
    } else {
        reusedMember = true;
    }

    if ((result == totalSum) && reusedMember) {
        _completedTaskScore.numberOfRightAnswers++;
        _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
    } else {
        _completedTaskScore.numberOfWrongAnswers++;
        _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
    }

}
completedTaskScore = JSON.stringify(_completedTaskScore);
