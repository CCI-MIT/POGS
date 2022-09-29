var teammatez = JSON.parse(teammates);
var taskConfigAttr = JSON.parse(taskConfigurationAttributes);

var surveyBluePrint = null;

print("STARTING SCRIPT --------")
for(var o = 0; o < taskConfigAttr.length; o++) {

    if( taskConfigAttr[o].attributeName== "surveyBluePrint"){
        surveyBluePrint = JSON.parse(taskConfigAttr[o].stringValue);
    }
}


var fieldPositionsBasedOnTheirIndexes = [];

var toBeRandomizedGroup = [];


for(var i = 0; i < surveyBluePrint.length; i++) {
    if(surveyBluePrint[i].shouldRandomize){
        toBeRandomizedGroup.push(i);
    } else {
        fieldPositionsBasedOnTheirIndexes.push(i);
    }
}

function shuffle(array) {
    for (var i = array.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    return array;
}

if(toBeRandomizedGroup.length > 0) {
    var randomized = shuffle(toBeRandomizedGroup);
    for (var j = 0; j < randomized.length; j++) {
        fieldPositionsBasedOnTheirIndexes.push(randomized[j]);
    }
}
print(fieldPositionsBasedOnTheirIndexes);



var attributesToAddz = [{"attributeName": "finalDisplayOrder",
    "stringValue":JSON.stringify(fieldPositionsBasedOnTheirIndexes) }]

//console.log(attributesToAddz);
completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);
