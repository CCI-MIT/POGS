var questions = [];

while(questions.length < 45){
    var r = Math.floor(Math.random()*45) + 1;
    if(questions.indexOf(r) === -1) questions.push(r);
}
console.log(questions);