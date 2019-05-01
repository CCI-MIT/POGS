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
        gridBluePrint = _taskConfigurationAttributes[i].stringValue.split(",");
        break;
    }
}

var paymentStructure = gridBluePrint.paymentStructure;
var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};

let userArray = [];

for(var k =0 ; k < _teammates.length; k ++ ) {
    userArray[_teammates[k].externalId]= {"user_external_id": _teammates[k].externalId, "roundPayouts": [], "roundOptions": []};
}

/*"users": [
    {
        "user": "externalId",
        "roundPayouts": [
            {"roundNumber": 1, roundPayout: "130"},
            {"roundNumber": 2, roundPayout: "120"},
            {"roundNumber": 3, roundPayout: "29"},
            {"roundNumber": 4, roundPayout: "20"},
            {"roundNumber": 5, roundPayout: "92"},
        ]
    }
*/
/*
var roundsAnswers = [];

for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if(_completedTaskAttributes[i].attributeName.indexOf("roundAnswer_") != -1){
        let arr = _completedTaskAttributes[i].attributeName.replace("roundAnswer_", "").split("|");
        let roundNumber = parseInt(arr[0]);
        let userId = arr[1];
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

for (var us in userArray){
    for(var k = 0; k < userArray[us].roundOptions.length; k++){
        if(paymentStructure[userArray[us].roundOptions[k])
    }
}

*/

completedTaskScore = JSON.stringify(_completedTaskScore);