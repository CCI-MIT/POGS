var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);


var gridBluePrint;

var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = 0;


for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "gridBluePrint"){
        gridBluePrint = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
    }
}

var paymentStructure = gridBluePrint.paymentStructure;
var availableNumbers = parseInt(gridBluePrint.availableNumbers);
var numberOfRounds = parseInt(gridBluePrint.numberOfRounds);

function calculatePayout(minimalNumber, userOwn) {
    if(userOwn == 0 ){
        return '0';
    }
    return paymentStructure[(availableNumbers - (userOwn))][(availableNumbers - (minimalNumber))];
}


var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : {}
};

var rounds = [];
var userByRounds = [];
var roundsAnswers = [];

for(var k =0 ; k < numberOfRounds; k ++ ) {
    roundsAnswers[k] = [];
    rounds[k] = {"roundNumber": (k+1), "subjects" : [] , "roundMinimal":0, "totalRoundPayout": 0 };

}


var userArray = [];
for(var k =0 ; k < _teammates.length; k ++ ) {
    userArray[_teammates[k].externalId] = { "user_external_id": _teammates[k].externalId,
        "roundPayouts": [], "roundOptions": []};
    for(var h =0 ; h < numberOfRounds; h ++ ) {
        userArray[_teammates[k].externalId].roundPayouts[h] = 0;
        userArray[_teammates[k].externalId].roundOptions[h] = 0;
    }
}


for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if(_completedTaskAttributes[i].attributeName.indexOf("roundAnswer_") != -1){
        var arr = _completedTaskAttributes[i].attributeName.replace("roundAnswer_", "").split("|");
        var roundNumber = parseInt(arr[0]);


        var userId = arr[1];
        userArray[userId].roundOptions[roundNumber] = parseInt(_completedTaskAttributes[i].integerValue);
        roundsAnswers[roundNumber].push(parseInt(_completedTaskAttributes[i].integerValue))
    }
}
var roundsMinimals = [];
for(var k=0 ;k < roundsAnswers.length; k++){
    roundsMinimals[k] = 9999999;
}
for(var k=0 ;k < roundsAnswers.length; k++){
    for(var z=0; z < roundsAnswers[k].length; z++){
        if(roundsAnswers[k][z] < roundsMinimals[k]){
            roundsMinimals[k] = roundsAnswers[k][z];
        }
    }
}

for (var subject in userArray){
    var su = userArray[subject]
    for(var k =0 ; k < numberOfRounds; k ++ ) {
        su.roundPayouts[k]= parseInt(calculatePayout(roundsMinimals[k],su.roundOptions[k]))
    }
}


var totalScore = 0;
for(var k =0 ; k < numberOfRounds; k ++ ) {
    rounds[k].roundMinimal = roundsMinimals[k];
    var roundPayoutSum = 0;
    var subjects = [];
    for (var subject in userArray){
        var su = userArray[subject];
        roundPayoutSum+=su.roundPayouts[k];

        subjects.push({'externalId': su.user_external_id,
                          'chosenOption': su.roundOptions[k],
                          'payout': su.roundPayouts[k] })
    }
    rounds[k].totalRoundPayout = roundPayoutSum;
    rounds[k].subjects = subjects;
    totalScore += roundPayoutSum;
}
_completedTaskScore.totalScore = (totalScore/numberOfRounds);
_completedTaskScore.scoringData = {rounds: rounds};

_completedTaskScore.scoringData = JSON.stringify(_completedTaskScore.scoringData);

completedTaskScore = JSON.stringify(_completedTaskScore);

// {"rounds":[{"roundNumber":1,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":7,"payout":30},{"externalId":"syn1inter1_1594320293582","chosenOption":2,"payout":80},{"externalId":"syn1inter1_1594320288694","chosenOption":3,"payout":70}],"roundMinimal":2,"totalRoundPayout":180},{"roundNumber":2,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":5,"payout":110},{"externalId":"syn1inter1_1594320293582","chosenOption":7,"payout":90},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":110}],"roundMinimal":5,"totalRoundPayout":310},{"roundNumber":3,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":6,"payout":120},{"externalId":"syn1inter1_1594320293582","chosenOption":7,"payout":110},{"externalId":"syn1inter1_1594320288694","chosenOption":6,"payout":120}],"roundMinimal":6,"totalRoundPayout":350},{"roundNumber":4,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":4,"payout":40},{"externalId":"syn1inter1_1594320293582","chosenOption":1,"payout":70},{"externalId":"syn1inter1_1594320288694","chosenOption":6,"payout":20}],"roundMinimal":1,"totalRoundPayout":130},{"roundNumber":5,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":6,"payout":80},{"externalId":"syn1inter1_1594320293582","chosenOption":4,"payout":100},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":90}],"roundMinimal":4,"totalRoundPayout":270},{"roundNumber":6,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":7,"payout":90},{"externalId":"syn1inter1_1594320293582","chosenOption":6,"payout":100},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":110}],"roundMinimal":5,"totalRoundPayout":300},{"roundNumber":7,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":4,"payout":100},{"externalId":"syn1inter1_1594320293582","chosenOption":5,"payout":90},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":90}],"roundMinimal":4,"totalRoundPayout":280},{"roundNumber":8,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":4,"payout":100},{"externalId":"syn1inter1_1594320293582","chosenOption":7,"payout":70},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":90}],"roundMinimal":4,"totalRoundPayout":260},{"roundNumber":9,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":3,"payout":70},{"externalId":"syn1inter1_1594320293582","chosenOption":2,"payout":80},{"externalId":"syn1inter1_1594320288694","chosenOption":0,"payout":0}],"roundMinimal":2,"totalRoundPayout":150},{"roundNumber":10,"subjects":[{"externalId":"syn1inter1_1594320289546","chosenOption":4,"payout":40},{"externalId":"syn1inter1_1594320293582","chosenOption":1,"payout":70},{"externalId":"syn1inter1_1594320288694","chosenOption":5,"payout":30}],"roundMinimal":1,"totalRoundPayout":140}]}

var _individualSubjectScore = {};
for(var i=0;i<_teammates.length; i++){
    _individualSubjectScore[_teammates[i].externalId] = {
        "subjectExternalId" :  _teammates[i].externalId,
        "individualScore" : 0.0,
        "scoringData" : ""
    };
}
for(var j=0; j < rounds.length; j++){
    for(var k=0; k < rounds[j].subjects.length; k++){
        _individualSubjectScore[rounds[j].subjects[k].externalId].individualScore+= rounds[j].subjects[k].payout
    }
}
var _indScor = [];
for(var iss in _individualSubjectScore){
    _indScor.push(_individualSubjectScore[iss]);
}
individualSubjectScores = JSON.stringify(_indScor);
