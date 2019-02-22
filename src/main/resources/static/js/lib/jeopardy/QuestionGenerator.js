var questions = [];
var usedQuestions = [];

for (var i = 0; i<4; i++){
    questions[i] = [];
    for (var j = 0; j<10; ){
        var q = Math.floor(Math.random() * 300) + 1;
        var alreadyUsed = false;
        for (var k=0;k<usedQuestions.length;k++){
            if (q==usedQuestions[k])
                alreadyUsed = true;
        }
        if (alreadyUsed == false){
            j++;
            usedQuestions.push(q);
            questions[i].push(q);
        }
    }
}
console.log(questions);