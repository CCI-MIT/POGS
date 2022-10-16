var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);

var _exportRecordLines = []
var _headerColumns = "";



var totalOfRounds = 0;

for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if(_completedTaskAttributes[i].attributeName == "totalOfRounds"){
        totalOfRounds = parseInt(_completedTaskAttributes[i].integerValue);
    }
}

var rounds = [];

var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};
function newRound(){
    return {
        "teamScoreRound" : 0,
        "totalTargetsAppearedRound" : 0,
        "subjectScoreRound" : [],
        "subjectNumberOfClicksRound" : []

    };
}
var subjects = {};
for(var k = 0; k <= totalOfRounds; k++) {
    var round = newRound();
    var roundIndex = k +1;
    for(var i=0 ; i < _completedTaskAttributes.length; i ++) {

        if(_completedTaskAttributes[i].attributeName == ("totalTargetsAppearedRound"+roundIndex)){
            round.totalTargetsAppearedRound = parseInt(_completedTaskAttributes[i].integerValue);
            _completedTaskScore.numberOfEntries+=round.totalTargetsAppearedRound;
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
    rounds.push(round);
}


//print(JSON.stringify(rounds));

_headerColumns = "RoundIndex;Subject author; NumberOfClicks;HitsInMoles;TotalTargetsAppeared";
//_exportRecordLines = ["a;b;c;d"];
for(var i =0 ; i < rounds.length; i++){
    var round = rounds[i];
    for(var j=0; j < round.subjectScoreRound.length ; j ++){
        var su = round.subjectScoreRound[j];
        _exportRecordLines.push(i + ";" + su.external_id + ";" + getNumberOfClicks(round,su.external_id) + ";" +su.subject_score+ ";" +  round.totalTargetsAppearedRound)

    }
}
function getNumberOfClicks(round, externalId){
    for(var k=0; k < round.subjectNumberOfClicksRound.length; k ++){
        if(round.subjectNumberOfClicksRound[k].external_id == externalId){
            return round.subjectNumberOfClicksRound[k].subject_score;
        }
    }
    return 0;
}
/*
_headerColumns = "Cell Index;Last subject author; Answer;Ground Truth";


for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("memoryGridAnswer") != -1) {
        var index = _completedTaskAttributes[i].attributeName.replace("memoryGridAnswer","");
        _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].lastAuthorSubject + ";" + _completedTaskAttributes[i].stringValue + ";" +answerSheet[index])
    }
}
*/
exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;