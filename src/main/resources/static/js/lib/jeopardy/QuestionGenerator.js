var questions = [];

while(questions.length < 54){
    var r = Math.floor(Math.random()*54) + 1;
    if(questions.indexOf(r) === -1) questions.push(r);
}
console.log(questions);