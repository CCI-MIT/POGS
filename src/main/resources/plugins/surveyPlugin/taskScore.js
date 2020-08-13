var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);


var answerSheet;

var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = 0;

for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
    }
}
var _individualSubjectScore = {};
for(var i=0;i<_teammates.length; i++){
    _individualSubjectScore[_teammates[i].externalId] = {
        "subjectExternalId" :  _teammates[i].externalId,
        "individualScore" : 0.0,
        "scoringData" : ""
    };
}
var answerAuthorMap = [];

var answerKeyMap = [];


for(var k =0 ; k < answerSheet.length; k ++ ) {
    answerKeyMap[k] = "";
    answerAuthorMap[k] = ""
}

var _completedTaskScore = {
    "totalScore" : 0,
    "numberOfRightAnswers" : 0,
    "numberOfWrongAnswers" : 0,
    "numberOfEntries" : 0,
    "numberOfProcessedEntries" : 0,
    "scoringData" : ""
};


for(var i=0 ; i < _completedTaskAttributes.length; i++) {
    if(_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1){
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer",""));
        if(answerSheet.length >= index) {

            var answer = _completedTaskAttributes[i].stringValue;
            answerKeyMap[index] = answer;
            answerAuthorMap[index] = _completedTaskAttributes[i].lastAuthorSubject;
            //print("£££ field index (" + index+") - " + _completedTaskAttributes[i].lastAuthorSubject)

        }
    }
}

for(var i=0 ;i < answerSheet.length; i++) {
    _completedTaskScore.numberOfEntries++;
    _completedTaskScore.numberOfProcessedEntries++;

    if (answerSheet[i] instanceof Array) {

        answer = JSON.parse(answerKeyMap[i]);

        var isRight = true;
        for (var j = 0; j < answer.length; j++) {
            if (!(answerSheet[i][j] == answer[j])) {
                isRight = false;
            }
        }
        if(isRight) {
            _completedTaskScore.numberOfRightAnswers++;
            _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
            if(answerAuthorMap[i]!= "") {
                _individualSubjectScore[answerAuthorMap[i]].individualScore += RIGHT_ANSWER_REWARD
            }
        }else {
            _completedTaskScore.numberOfWrongAnswers++;
            _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
            if(answerAuthorMap[i]!= "") {
                _individualSubjectScore[answerAuthorMap[i]].individualScore += WRONG_ANSWER_REWARD
            }
        }
    } else {

        if(answerKeyMap[i]!="") {
            //print("GIVEN ANS:" + answerKeyMap[i] + "RIGHT ANS:" + answerSheet[i] + " - " + answerAuthorMap[i])
            if (answerKeyMap[i] == answerSheet[i]) {
                _completedTaskScore.numberOfRightAnswers++;
                _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
                if (answerAuthorMap[i] != "") {
                    _individualSubjectScore[answerAuthorMap[i]].individualScore +=
                        RIGHT_ANSWER_REWARD
                }
            } else {
                _completedTaskScore.numberOfWrongAnswers++;
                _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
                if (answerAuthorMap[i] != "") {
                    _individualSubjectScore[answerAuthorMap[i]].individualScore +=
                        WRONG_ANSWER_REWARD
                }
            }
        }
    }
}

completedTaskScore = JSON.stringify(_completedTaskScore);

var _indScor = [];
for(var iss in _individualSubjectScore){
    //print("£££ "+JSON.stringify(_individualSubjectScore[iss]));
    _indScor.push(_individualSubjectScore[iss]);
}
individualSubjectScores = JSON.stringify(_indScor);