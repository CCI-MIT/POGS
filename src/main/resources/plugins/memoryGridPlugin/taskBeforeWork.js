/*
*
*
* task_subjects
* task_execution_attributes
*
* */


var teammatez = JSON.parse(teammates);
var taskConfigAttr = JSON.parse(taskConfigurationAttributes);

var gridBluePrint = null;
for(var o = 0; o < taskConfigAttr.length; o ++) {

    if( taskConfigAttr[o].attributeName== "gridBluePrint"){
        gridBluePrint = JSON.parse(taskConfigAttr[o].stringValue);
    }
}

colorHasSubjects = [];
subjectsHasColumn = [] ;
if(gridBluePrint.columnColors){

    var allSubjectsHaveAtLeastOneColor = false;
    var allColorsHaveAtLeastOneSubject = false;


    for(var k = 0 ; k < gridBluePrint.columnColors.length; k ++){
        colorHasSubjects[k] = [];
    }
    for(var k = 0; k < teammatez.length; k ++){
        subjectsHasColumn[k] = [];
    }

    while(!(allColorsHaveAtLeastOneSubject && allSubjectsHaveAtLeastOneColor)) {

        for(var k = 0 ; k < subjectsHasColumn.length; k ++){
            if(subjectsHasColumn[k].length == 0){
                allSubjectsHaveAtLeastOneColor = false;
            }
        }

        if(!allSubjectsHaveAtLeastOneColor) {
            for (var i = 0; i < teammatez.length; i++) {
                if(subjectsHasColumn[i].length == 0 ) {
                    var x = Math.floor(
                        Math.random() * gridBluePrint.columnColors.length);
                    subjectsHasColumn[i].push(x);
                    colorHasSubjects[x].push(i);
                }
            }
            allSubjectsHaveAtLeastOneColor = true;
        }
        for(var p = 0 ; p < colorHasSubjects.length; p ++){
            if(colorHasSubjects[p].length == 0){
                allColorsHaveAtLeastOneSubject = false;
            }
        }
        if(!allColorsHaveAtLeastOneSubject) {
            for( var j=0; j < gridBluePrint.columnColors.length; j++){
                if(colorHasSubjects[j].length == 0 ){
                    var x2 = Math.floor(
                        Math.random() * teammatez.length);
                    subjectsHasColumn[x2].push(j);
                    colorHasSubjects[j].push(x2);
                }
            }
            allColorsHaveAtLeastOneSubject = true;
        }

    }
    var attributesToAddz = [{"attributeName": "colorHasSubjects", "stringValue":JSON.stringify(colorHasSubjects) },
        {"attributeName": "subjectsHasColumns", "stringValue":JSON.stringify(subjectsHasColumn)}]

    attributesToAdd = JSON.stringify(attributesToAddz);
}
