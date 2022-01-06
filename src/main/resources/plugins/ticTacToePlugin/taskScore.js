var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);



var TIED_GAME_GROUP_REWARD = 0;
var NOT_TIED_GAME_GROUP_REWARD = 1;


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
    if(_completedTaskAttributes[i].attributeName.indexOf("ticTacToeAnswer_") != -1){
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("ticTacToeAnswer_",""));
        var answer = _completedTaskAttributes[i].stringValue;
        answerKeyMap[index] = answer;
    }
}


var X_CLASS = "fa-times";
var O_CLASS = "fa-circle";
var isCellX = [];
var isCellY = [];
var gameOver = false;

for(var i=0 ;i < answerKeyMap.length; i++) {
    isCellX.push((answerKeyMap[i]==X_CLASS));
    isCellY.push((answerKeyMap[i]==O_CLASS));
}
//horizontal scenarios
var winnerClass = null;

if(isCellX[0]&&isCellX[1]&&isCellX[2] ||
   isCellX[3]&&isCellX[4]&&isCellX[5] ||
   isCellX[6]&&isCellX[7]&&isCellX[8]){
    winnerClass = X_CLASS;
    gameOver = true;
}

if(isCellX[0]&&isCellX[3]&&isCellX[6] ||
   isCellX[1]&&isCellX[4]&&isCellX[7] ||
   isCellX[2]&&isCellX[5]&&isCellX[8]){
    winnerClass = X_CLASS;
    gameOver = true;
}
if(isCellX[0]&&isCellX[4]&&isCellX[8] ||
   isCellX[2]&&isCellX[4]&&isCellX[6] ){
    winnerClass = X_CLASS;
    gameOver = true;
}


if(isCellY[0]&&isCellY[1]&&isCellY[2] ||
   isCellY[3]&&isCellY[4]&&isCellY[5] ||
   isCellX[6]&&isCellY[7]&&isCellY[8]){
    winnerClass = O_CLASS;
    gameOver = true;
}

if(isCellY[0]&&isCellY[3]&&isCellY[6] ||
   isCellY[1]&&isCellY[4]&&isCellY[7] ||
   isCellY[2]&&isCellY[5]&&isCellY[8]){
    winnerClass = O_CLASS;
    gameOver = true;
}
if(isCellY[0]&&isCellY[4]&&isCellY[8] ||
   isCellY[2]&&isCellY[4]&&isCellY[6] ){
    winnerClass = O_CLASS;
    gameOver = true;
}

_completedTaskScore.numberOfEntries=1;
_completedTaskScore.numberOfProcessedEntries=1;

if(gameOver){
    _completedTaskScore.numberOfRightAnswers++;
    _completedTaskScore.totalScore += NOT_TIED_GAME_GROUP_REWARD;
} else {
    _completedTaskScore.numberOfWrongAnswers++;
    _completedTaskScore.totalScore += TIED_GAME_GROUP_REWARD;
}

completedTaskScore = JSON.stringify(_completedTaskScore);