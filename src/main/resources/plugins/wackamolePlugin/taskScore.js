var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);

var totalOfRounds = 0;

for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if(_completedTaskAttributes[i].attributeName == "totalOfRounds"){
        totalOfRounds = parseInt(_completedTaskAttributes[i].integerValue);
    }
}

var rounds = [];
function newRound(){
    return {
        "teamScoreRound" : 0,
        "totalTargetsAppearedRound" : 0,
        "subjectScoreRound" : [],
        "subjectNumberOfClicksRound" : []

    };
}
var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};

for(var k = 0; k <= totalOfRounds; k++) {
    var round = newRound();
    var roundIndex = k +1;
    //print(" - " + k + " - " + roundIndex)
    for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
        if(_completedTaskAttributes[i].attributeName == ("teamScoreRound"+roundIndex)){
            round.teamScoreRound = parseInt(_completedTaskAttributes[i].integerValue);

            //print(" ---- " + _completedTaskAttributes[i].integerValue);
        }
        if(_completedTaskAttributes[i].attributeName == ("totalTargetsAppearedRound"+roundIndex)){
            round.totalTargetsAppearedRound = parseInt(_completedTaskAttributes[i].integerValue);

        }
        if(_completedTaskAttributes[i].attributeName == ("subjectScoreRound"+roundIndex)){
            var su = {"external_id": _completedTaskAttributes[i].extraData, subject_score: parseInt(_completedTaskAttributes[i].integerValue)}
            round.subjectScoreRound.push(su);
        }
        if(_completedTaskAttributes[i].attributeName == ("subjectNumberOfClicksRound"+roundIndex)){
            var su = {"external_id": _completedTaskAttributes[i].extraData, subject_score: parseInt(_completedTaskAttributes[i].integerValue)}
            round.subjectNumberOfClicksRound.push(su);
        }
    }
    _completedTaskScore.totalScore+=round.teamScoreRound;
    _completedTaskScore.numberOfEntries+=round.totalTargetsAppearedRound;
    rounds.push(round);
}

//print("Newly calculated SCORE: " + _completedTaskScore.totalScore)
_completedTaskScore.scoringData = JSON.stringify(rounds);

completedTaskScore = JSON.stringify(_completedTaskScore);