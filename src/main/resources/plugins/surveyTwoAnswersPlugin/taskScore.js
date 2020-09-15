var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);


var answerSheet;

var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = -3;

for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "answerSheet"){
        answerSheet = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
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

var newanswerSheet = [];
for(var i = 0; i < answerSheet.length; i++){

    newanswerSheet.push(answerSheet[i]);
    newanswerSheet.push(answerSheet[i]);
}
answerSheet = newanswerSheet;
var answerAuthorMap = [];

for(var i=0;i<_teammates.length; i++){
    _individualSubjectScore[_teammates[i].externalId] = {
        "subjectExternalId" :  _teammates[i].externalId,
        "individualScore" : 0.0,
        "scoringData" : ""
    };
}

var answerSheetAndKey = {};
for(var j=0; j < answerSheet.length; j = j +2){
        answerSheetAndKey[("surveyAnswer" + j)] = [];
        if (answerSheet[j] instanceof Array) {
            var emptyArrayStr = "[";
            for (var i = 0; i < answerSheet[j].length; i++) {
                emptyArrayStr += "\"\"";
                if (i + 1 != answerSheet[j].length) {
                    emptyArrayStr += ",";
                }
            }
            emptyArrayStr += "]";

            answerSheetAndKey[("surveyAnswer" + j)][0] = emptyArrayStr;
            answerSheetAndKey[("surveyAnswer" + j)][1] = emptyArrayStr;
            answerSheetAndKey[("surveyAnswer" + j)][2] = answerSheet[j];
            answerSheetAndKey[("surveyAnswer" + j)][3] = "";
            answerSheetAndKey[("surveyAnswer" + j)][4] = "";
        } else {
            answerSheetAndKey[("surveyAnswer" + j)][0] = "";
            answerSheetAndKey[("surveyAnswer" + j)][1] = "";
            answerSheetAndKey[("surveyAnswer" + j)][2] = answerSheet[j];
            answerSheetAndKey[("surveyAnswer" + j)][3] = "";
            answerSheetAndKey[("surveyAnswer" + j)][4] = "";
        }

}
for(var i=0 ; i < _completedTaskAttributes.length; i ++) {
    if (_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1) {
        var fieldKind = _completedTaskAttributes[i].integerValue;

        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer", ""));
        var answer = _completedTaskAttributes[i].stringValue;

        if(fieldKind == -1 ){
            answerSheetAndKey[_completedTaskAttributes[i].attributeName][0] = answer;
            answerSheetAndKey[_completedTaskAttributes[i].attributeName][3] = _completedTaskAttributes[i].lastAuthorSubject;
        } else {
            var indet = "surveyAnswer"+fieldKind;
            answerSheetAndKey[indet][1] = answer;
            answerSheetAndKey[indet][4] = _completedTaskAttributes[i].lastAuthorSubject;
        }

    }
}

for(var answerKey in answerSheetAndKey) {
    var anArray = answerSheetAndKey[answerKey];

    if(anArray[2] == "") {
        //Information field ignore.
        continue;
    }

    _completedTaskScore.numberOfEntries++;
    _completedTaskScore.numberOfProcessedEntries++;

    if(anArray[2] instanceof Array ) {
        var firstAnswer = JSON.parse(anArray[0]);
        var secondAnswer = JSON.parse(anArray[1]);
        var rightAnswer = (anArray[2]);

        var isRight = true;
        var isFirstRight = true;
        var isSecondRight = true;

        for (var j = 0; j < rightAnswer.length; j++) {
            if(!((rightAnswer[j] == firstAnswer[j]) &&
                 (rightAnswer[j] == secondAnswer[j]))){
                isRight = false;
            }
            if(!((rightAnswer[j] == firstAnswer[j]))){
                isFirstRight = false;
            }
            if(!(
                 (rightAnswer[j] == secondAnswer[j]))){
                isSecondRight = false;
            }
        }
        if(isRight){
            _completedTaskScore.numberOfRightAnswers++;
            _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
        } else {
            _completedTaskScore.numberOfWrongAnswers++;
            _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
        }
        //individual
        if(anArray[3]!= "") {
            if(isFirstRight) {
                _individualSubjectScore[anArray[3]].individualScore += RIGHT_ANSWER_REWARD
            } else {
                _individualSubjectScore[anArray[3]].individualScore += WRONG_ANSWER_REWARD
            }
        }
        if(anArray[4]!= "") {

            if(isSecondRight) {
                _individualSubjectScore[anArray[4]].individualScore += RIGHT_ANSWER_REWARD
            } else {
                _individualSubjectScore[anArray[4]].individualScore += WRONG_ANSWER_REWARD
            }
        }
    } else {
        if ((anArray[1] == anArray[2]) && (anArray[0] == anArray[2])) {
            _completedTaskScore.numberOfRightAnswers++;
            _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;
        } else {

            if ((anArray[1] == anArray[2]) && (anArray[0] == anArray[2])) {
                _completedTaskScore.numberOfWrongAnswers++;
                _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
            } else {

            }
        }

        if(anArray[3]!= "") {
            if((anArray[0] == anArray[2])) {
                _individualSubjectScore[anArray[3]].individualScore += RIGHT_ANSWER_REWARD
            } else {
                _individualSubjectScore[anArray[3]].individualScore += WRONG_ANSWER_REWARD
            }
        }
        if(anArray[4]!= "") {

            if((anArray[1] == anArray[2])) {
                _individualSubjectScore[anArray[4]].individualScore += RIGHT_ANSWER_REWARD
            } else {
                _individualSubjectScore[anArray[4]].individualScore += WRONG_ANSWER_REWARD
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