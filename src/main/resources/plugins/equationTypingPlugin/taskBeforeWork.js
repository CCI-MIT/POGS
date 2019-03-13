var teammatez = JSON.parse(teammates);




var subjectOrder = [];
var alreadyIn = false;
while(subjectOrder.length != teammatez.length ){

    var x = Math.floor(
        Math.random() * teammatez.length);
    alreadyIn = false;
    for(var k=0; k < subjectOrder.length; k++){
        if(x == subjectOrder[k]){
            alreadyIn = true;
        }
    }
    if(!alreadyIn){
        subjectOrder.push(x);
    }
}

attributesToAddz = [{"attributeName": "gridOrder",
    "stringValue":JSON.stringify(subjectOrder) }]

completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);