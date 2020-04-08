var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Field Index;Field type;Question;Last subject author answer 1; Answer1;Last subject author answer 2; Answer2; Ground Truth";
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
var newanswerSheet = [];
for(var i = 0; i < answerSheet.length; i++){

    newanswerSheet.push(answerSheet[i]);
    newanswerSheet.push(answerSheet[i]);
}
answerSheet = newanswerSheet;

var newsurveyBluePrint = [];
for(var i = 0; i < surveyBluePrint.length; i++){

    newsurveyBluePrint.push(surveyBluePrint[i]);
    newsurveyBluePrint.push(surveyBluePrint[i]);
}
surveyBluePrint = newsurveyBluePrint;



var secondAnswerMap = {};
for(var i=0; i < _completedTaskAttributes.length; i++) {

    if (_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1 && _completedTaskAttributes[i].integerValue != -1) {
        secondAnswerMap[_completedTaskAttributes[i].integerValue] = _completedTaskAttributes[i];
    }
}

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1 && _completedTaskAttributes[i].integerValue == -1) {
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer",""));
        var rightAnwser = "";
        var secondAnswerAuthor = "";
        var secondAnswer = "";

        if(answerSheet){
            rightAnwser = answerSheet[index];
        }
        if(secondAnswerMap[index]!=null){
            secondAnswerAuthor = secondAnswerMap[index].lastAuthorSubject;
            secondAnswer = secondAnswerMap[index].stringValue;
        }

        if(_completedTaskAttributes[i].extraData == "RadioTableField"){


            var givenAnswersArray = JSON.parse(_completedTaskAttributes[i].stringValue);

            var numberOfColumns = surveyBluePrint[index].value.columns.length;


            for(var k=0; k < givenAnswersArray.length; k ++) {

                if(givenAnswersArray[k]!="") {

                    var groundTruthArray = getValueFromArrayRange(rightAnwser,k,numberOfColumns);


                    _exportRecordLines.push(
                        index + "." + (k + 1) + ";" + _completedTaskAttributes[i].extraData + ";"
                        + cleanEntry(getRowValueFromArrayRange(index,k,numberOfColumns) )+ ";"
                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                        + cleanEntry(givenAnswersArray[k]) + ";"
                        + secondAnswerAuthor + ";"
                        + cleanEntry(secondAnswer) + ";"

                        + cleanEntry(groundTruthArray))
                }
            }
        } else {
            if(_completedTaskAttributes[i].extraData == "CheckBoxField"){
                _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"
                                        + cleanEntry(surveyBluePrint[index].question) + ";"
                                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                                        + JSON.stringify(JSON.parse(_completedTaskAttributes[i].stringValue)) + ";"
                                        + secondAnswerAuthor + ";"
                                        + JSON.stringify(JSON.parse(secondAnswer)) + ";"
                                        + JSON.stringify((rightAnwser)))

            } else {

                _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"
                                        + cleanEntry(surveyBluePrint[index].question) + ";"

                                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                                        + cleanEntry(_completedTaskAttributes[i].stringValue) + ";"
                                        + secondAnswerAuthor + ";"
                                        + secondAnswer + ";"
                                        + cleanEntry(rightAnwser))
            }
        }
    }
}


function cleanEntry(summaryDescription) {
    if (summaryDescription === undefined) return "";
    if (summaryDescription === null) return "";
    summaryDescription = summaryDescription.replace(/\n/g, "¶");
    summaryDescription = summaryDescription.replace(/\r/g, "");
    summaryDescription = summaryDescription.replace(/,/g, "|");
    summaryDescription = summaryDescription.replace(/;/g, "|");
    return summaryDescription;
}


function getRowValueFromArrayRange(fieldIndex, index, numberOfColumns){
    var subIndex = parseInt(index/numberOfColumns);
    if(surveyBluePrint[fieldIndex].value.rows.length >= subIndex) {
        return surveyBluePrint[fieldIndex].value.rows[subIndex];
    }

    return "";


}
function getValueFromArrayRange(array, index, numberOfColumns){
    var subIndex = parseInt(index/numberOfColumns);
    var startIndex=subIndex*numberOfColumns;
    var stopIndex= (subIndex + 1)*numberOfColumns;



    if(array.length > startIndex && stopIndex <= array.length ) {
        for (var i = startIndex; i < stopIndex; i++) {

            if (array[i] != "") {
                return array[i];
            }
        }
    }
    return "";
}


exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;