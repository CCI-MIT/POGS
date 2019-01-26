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
//Picking random questions for the game
var questions = [];
var usedQuestions = [];

for (var i = 0; i<4; i++){
    questions[i] = [];
    for (var j = 0; j<20; ){
        var q = Math.floor(Math.random() * 6) + 1;
        if (usedQuestions.includes(q) == false){
            j++;
            usedQuestions.push(q);
            questions[i].push(q);
        }
    }
}

attributesToAddz = [{"attributeName": "gridOrder", "stringValue":JSON.stringify(subjectOrder) },
                   {"attributeName": "questionOrder", "stringValue":JSON.stringify(questions) }]

attributesToAdd = JSON.stringify(attributesToAddz);