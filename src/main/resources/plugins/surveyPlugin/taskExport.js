var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _exportRecordLines = []
var _headerColumns = "";


if(!_subject) {
    _subject = {subjectExternalId: '', subjectDisplayName : ''}
}
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
var replacements =[];

replacements.push(getTeammatesDisplayNames());
replacements.push(getOtherTeammates());
replacements.push(getLastTask());
replacements.push(getTaskList());
replacements.push(getOtherTasks());
replacements.push(getSessionName());

function resolveVariablesForNetworkQuestions(surveyItem){
    //print("     --------   SURVEY RESOLVING VARIABLES ------------")
    //print(surveyItem.question + " - " + surveyItem.type)
    var regex = new RegExp(/\${.*}/gi);
    var allVariables = ['\\${allTeammates}','\\${otherTeamates}', '\\${lastTaskName}',
                        '\\${allTasksNames}','\\${otherTasksNames}', '\\${sessionName}'];

    //var replacements = replacements;


    // [['m01', 'm02', 'm03'],
    //     ['m02', 'm03'],
    //     "Last task name",
    //     ["tast 1", "task 2","task 3"],
    //     ["task 2","task3"],
    //     "session one"]

    //print("checking if question has any variables" );
    if(surveyItem.question.match(regex)) {

        for(var i=0; i < allVariables.length; i ++) {

            var replacer = "";
            //print("Question has " + allVariables[i] );
            if(surveyItem.question.match(new RegExp(allVariables[i] ,'gi'))) {
                if(replacements[i].constructor === Array) {

                    for(var j =0 ; j < replacements[i].length; j ++) {
                        replacer += replacements[i][j];
                        if(j + 1 != replacements[i].length){
                            replacer += ", ";
                        }
                    }

                } else {
                    replacer = replacements[i];
                }
                surveyItem.question =
                    surveyItem.question.replace( new RegExp( allVariables[i] ,'gi'), replacer);
            }
        }
    }
    if(!surveyItem.value){
        return surveyItem;
    }
    //print("Value is Array :" + (surveyItem.value.constructor === Array) );
    if (surveyItem.value.constructor === Array) {
        //console.log("value is array");
        if (surveyItem.value !== undefined && surveyItem.value.length > 0) {
            for (var i = 0; i < surveyItem.value.length; i++) {

                if (surveyItem.value[i].match(regex)) {

                    for (var j = 0; j < allVariables.length; j++) {
                        if (surveyItem.value[i].match(new RegExp(allVariables[j], 'gi'))) {

                            if (replacements[i].constructor === Array) {
                                surveyItem.value = [];

                                for (var k = 0; k < replacements[j].length; k++) {
                                    surveyItem.value.push(replacements[j][k]);
                                }
                                return surveyItem;
                            } else {
                                surveyItem.value[i] =
                                    surveyItem.value.replace(new RegExp(allVariables[j], 'gi'),
                                                             replacements[j]);
                            }
                        }
                    }
                }

            }
        }
    } else {
       // print("Value is Object :" + (surveyItem.value.constructor === Object) );
        if (surveyItem.value.constructor === Object) {
            //console.log("value is object");
            //print("Value is in column? ");
            for (var i = 0; i < surveyItem.value.columns.length; i++) {

                if (surveyItem.value.columns[i].match(regex)) {

                    for (var j = 0; j < allVariables.length; j++) {
                        if (surveyItem.value.columns[i].match(new RegExp(allVariables[j], 'gi'))) {

                            if (replacements[i].constructor === Array) {
                                surveyItem.value.columns = [];

                                for (var k = 0; k < replacements[j].length; k++) {
                                    surveyItem.value.columns.push(replacements[j][k]);
                                }
                                return surveyItem;
                            } else {
                                surveyItem.value.columns[i] =
                                    surveyItem.value.columns.replace(new RegExp(allVariables[j], 'gi'),
                                                                     replacements[j]);
                            }
                        }
                    }
                }
            }
            //print("Value is in rows? ");
            for (var i = 0; i < surveyItem.value.rows.length; i++) {
                //print(" Value row: " + i + "has variable")
                if (surveyItem.value.rows[i].match(regex)) {

                    for (var j = 0; j < allVariables.length; j++) {
                        if (surveyItem.value.rows[i].match(new RegExp(allVariables[j], 'gi'))) {
                            //print(" Value row: " + i + "has variable - " +allVariables[i] );
                            //print( "Replacements length: "  + replacements[i].length);

                            if (replacements[i].constructor === Array) {

                                surveyItem.value.rows = [];

                                for (var k = 0; k < replacements[j].length; k++) {
                                    surveyItem.value.rows.push(replacements[j][k]);
                                }
                                return surveyItem;
                            } else {

                                surveyItem.value.rows[i] =
                                    surveyItem.value.rows.replace(new RegExp(allVariables[j], 'gi'),
                                                                  replacements[j]);
                            }
                        }
                    }
                }
            }
        }
    }
    return surveyItem;
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
                    //_headerColumns = "Field Index;Field type;Question;Last subject author; Answer; Ground Truth";

                    _exportRecordLines.push(
                        index + "." +  parseInt((k/numberOfColumns)) + ";" + _completedTaskAttributes[i].extraData + ";"
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
    var resolved = resolveVariablesForNetworkQuestions(surveyBluePrint[fieldIndex]);
    // if(surveyBluePrint[fieldIndex].value.rows &&
    //    surveyBluePrint[fieldIndex].value.rows.length > 0 &&
    //     surveyBluePrint[fieldIndex].value.rows[0].indexOf("${")!= -1) {
    //
        //print("-------------")
        //print(JSON.stringify(resolved.value.rows))
        //print("-------------")

        if(resolved && resolved.value.rows){
            var numberOfRows = resolved.value.rows.length;
            var inexInAnswe = parseInt(subIndex/numberOfRows)

            //print("Index of answer: " + index + " - " + "devided by number of columns: " + subIndex + " devided by the number of rows: " + inexInAnswe);

            if(inexInAnswe < resolved.value.rows.length)
                return resolved.question + ": " + resolved.value.rows[subIndex];
        }
    //}else {
        //if (surveyBluePrint[fieldIndex].value.rows.length >= subIndex) {
        //    return surveyBluePrint[fieldIndex].value.rows[subIndex];
       // }
    //}

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
function getTeammates(){
    return _teammates;
}
function getTeammatesDisplayNames() {
    var teamm = [];
    var teammates = getTeammates();
    for(var i = 0; i < teammates.length; i ++) {
        teamm.push(teammates[i].displayName);
    }
    return teamm;
}
function getOtherTeammates() {
    var teamm = [];
    var teammates = getTeammates();
    for(var i = 0; i < teammates.length; i ++) {
        if(teammates[i].externalId != _subject.subjectExternalId){
            teamm.push(teammates[i].displayName);
        }
    }
    return teamm;
}
function getLastTask(){
    //return this.pogsRef.lastTask;
}
function getTaskList(){
    /*var taskList = [];
    for(var i =0; i< this.pogsRef.taskList.length; i ++){
        taskList.push(this.pogsRef.taskList[i].taskName);
    }
    return taskList;*/
}
function getOtherTasks(){
    /*var taskList = []
    for(var i =0; i< this.pogsRef.taskList.length; i ++){
        if(this.pogsRef.taskList[i].id != this.pogsRef.task) {
            taskList.push(this.pogsRef.taskList[i].taskName);
        }
    }
    return taskList;*/
}
function getSessionName(){
    /*return this.pogsRef.sessionName;*/
}

exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;