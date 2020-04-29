var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Field Index;Field type;Question;Last subject author; Answer; Ground Truth";
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



for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName.indexOf("surveyAnswer") != -1) {
        var index = parseInt(_completedTaskAttributes[i].attributeName.replace("surveyAnswer",""));
        var rightAnwser = "";
        if(answerSheet){
            rightAnwser = answerSheet[index];
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
                        + cleanEntry(groundTruthArray))
                }
            }
        } else {
            if(_completedTaskAttributes[i].extraData == "CheckBoxField"){
                _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"
                                        + cleanEntry(surveyBluePrint[index].question) + ";"
                                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                                        + JSON.stringify(JSON.parse(_completedTaskAttributes[i].stringValue)) + ";"
                                        + JSON.stringify((rightAnwser)))

            } else {
                _exportRecordLines.push(index + ";" + _completedTaskAttributes[i].extraData + ";"
                                        + cleanEntry(surveyBluePrint[index].question) + ";"
                                        + _completedTaskAttributes[i].lastAuthorSubject + ";"
                                        + cleanEntry(_completedTaskAttributes[i].stringValue) + ";"
                                        + cleanEntry(rightAnwser))
            }
        }
    }
}


function cleanEntry(summaryDescription) {
    if (summaryDescription === null) return "";
    if (summaryDescription === undefined) return "";
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