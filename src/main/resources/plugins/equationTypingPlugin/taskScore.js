var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);

var RIGHT_ANSWER_REWARD = 1;
var WRONG_ANSWER_REWARD = 0;

for (var i = 0; i < _taskConfigurationAttributes.length; i++) {
    if (_taskConfigurationAttributes[i].attributeName == "gridBluePrint") {
        gridBluePrint = JSON.parse(_taskConfigurationAttributes[i].stringValue);
    }
    break;
}
var shouldReuseMember = (gridBluePrint.shouldReuseMembers)?(gridBluePrint.shouldReuseMembers):(false);
var totalSum = parseInt(gridBluePrint.totalSum);
var shouldRestrictNumbers = (gridBluePrint.shouldRestrictNumbers != null)
                            ? (gridBluePrint.shouldRestrictNumbers) : (false);
var shouldNotAllowRepetition = (gridBluePrint.shouldNotAllowRepetition != null)
                               ? (gridBluePrint.shouldNotAllowRepetition) : (false);
var allowedNumbers = (gridBluePrint.allowedNumbers != null) ? (gridBluePrint.allowedNumbers.split(
    ",")) : ([]);


var _individualSubjectScore = {};
for(var i=0;i<_teammates.length; i++){
    _individualSubjectScore[_teammates[i].externalId] = {
        "subjectExternalId" :  _teammates[i].externalId,
        "individualScore" : 0.0,
        "scoringData" : ""
    };
}


var previous_Equations_Permutations = [];
var _completedTaskScore = {
    "totalScore": 0,
    "numberOfRightAnswers": 0,
    "numberOfWrongAnswers": 0,
    "numberOfEntries": 0,
    "numberOfProcessedEntries": 0,
    "scoringData": []
};

var equations = [];

var answerAuthorMap = [];

for (var i = 0; i < _completedTaskAttributes.length; i++) {
    if (_completedTaskAttributes[i].attributeName.indexOf("equationAnswer") != -1) {
        var index = parseInt(
            _completedTaskAttributes[i].attributeName.replace("equationAnswer", ""));
        var answer = _completedTaskAttributes[i].stringValue;
        equations[index] = answer;
        answerAuthorMap[index] = _completedTaskAttributes[i].lastAuthorSubject;
    }
}
var lastRoundMembers = [];

var NUMERIC_REGEXP = /[\d]*[\.]{0,1}[\d]+/g; //[-]{0,1}

for (var i = 0; i < equations.length; i++) {

    var current_equation = equations[i].replace(/\s/g, "");



    _completedTaskScore.numberOfEntries++;
    _completedTaskScore.numberOfProcessedEntries++;

    var result = 0;
    try{
        result = eval(current_equation);
        //print("Result : " + result);
    }catch (number_format) {
        _completedTaskScore.scoringData.push("Eq. ("+i+") Invalid equation: "+ current_equation);
    }

    var reusedMember = false;
    var usedOnlyAllowedNumbers = true;
    var repeatedNumber = false;

    var currentEquationNumbers = [];

    currentEquationNumbers = current_equation.match(NUMERIC_REGEXP);



    if (shouldReuseMember) {

        if (i == 0) {
            reusedMember = true;
        } else {
            if (currentEquationNumbers && currentEquationNumbers.length > 0) {
                for (var j = 0; j < currentEquationNumbers.length; j++) {
                    for (var k = 0; k < lastRoundMembers.length; k++) {
                        if (lastRoundMembers[k] == currentEquationNumbers[j]) {
                            reusedMember = true;
                            break;
                        }
                    }
                    if (reusedMember == true) {
                        break;
                    }
                }
            }
        }
        lastRoundMembers = currentEquationNumbers;
    }

    if (shouldRestrictNumbers) {
        if (currentEquationNumbers && currentEquationNumbers.length > 0) {
            for (var j = 0; j < currentEquationNumbers.length; j++) {
                var numberIsAValidOne = false;
                for (var z = 0; z < allowedNumbers.length; z++) {

                    if (currentEquationNumbers[j] == allowedNumbers[z]) {
                        numberIsAValidOne = true;

                    }
                }
                if (!numberIsAValidOne) {
                    usedOnlyAllowedNumbers = false;
                    break;
                }
            }
        } else {
            usedOnlyAllowedNumbers = false;
        }
    }
    if (shouldNotAllowRepetition) {
        var numberMap = {};
        if (currentEquationNumbers && currentEquationNumbers.length > 0) {
            for (var j = 0; j < currentEquationNumbers.length; j++) {
                if (numberMap[currentEquationNumbers[j]] == null) {
                    numberMap[currentEquationNumbers[j]] = [];
                } else {
                    repeatedNumber = true;
                    break;
                }

            }
        } else {
            repeatedNumber = false;
        }

    }

    if ((shouldReuseMember && !reusedMember) ||
        (shouldRestrictNumbers && !usedOnlyAllowedNumbers) ||
        (shouldNotAllowRepetition && repeatedNumber) ||
        (result != totalSum)) {

        _completedTaskScore.numberOfWrongAnswers++;
        _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
        _individualSubjectScore[answerAuthorMap[i]].individualScore += WRONG_ANSWER_REWARD

        if((shouldRestrictNumbers && !usedOnlyAllowedNumbers)){
            _completedTaskScore.scoringData.push("Eq. ("+i+") Not allowed numbers used : "+ current_equation );
        }
        if((shouldNotAllowRepetition && repeatedNumber)){
            _completedTaskScore.scoringData.push("Eq. ("+i+") Repetition of numbers : "+ current_equation);
        }
        if((shouldReuseMember && !reusedMember)){
            _completedTaskScore.scoringData.push("Eq. ("+i+") Member not reused : "+ current_equation);
        }
        if((result != totalSum)){
            if(result!=0){
                _completedTaskScore.scoringData.push("Eq. ("+i+") Incorrect total sum : "+ current_equation);
            }
        }
    } else {
        var isPermutation = isPermutationOfOtherAnswer(current_equation);
        if(isPermutation.length == 0) {
            _completedTaskScore.numberOfRightAnswers++;
            _completedTaskScore.totalScore += RIGHT_ANSWER_REWARD;

            _individualSubjectScore[answerAuthorMap[i]].individualScore += RIGHT_ANSWER_REWARD

            var permuts = generateValidEquationPermutations(current_equation, totalSum);
            previous_Equations_Permutations.push(permuts);
        } else {
            _completedTaskScore.numberOfWrongAnswers++;
            _completedTaskScore.totalScore += WRONG_ANSWER_REWARD;
            _individualSubjectScore[answerAuthorMap[i]].individualScore += WRONG_ANSWER_REWARD

            _completedTaskScore.scoringData.push("Eq. ("+i+") Equation is a permutation of other answer : "+ current_equation + " -- " + isPermutation[0]);
        }
    }

}
function isPermutationOfOtherAnswer(equation){
    //print(previous_Equations_Permutations);

    for(var i = 0; i < previous_Equations_Permutations.length; i ++){
        for(var j=0 ;j < previous_Equations_Permutations[i].length; j ++){
            if(equation == previous_Equations_Permutations[i][j]){
                var ret = [];
                ret.push(previous_Equations_Permutations[i][j] + "");
                return ret;
            }
        }
    }
    return [];
}

// Create test array and load into 'results' as first entry
function generateValidEquationPermutations(equation, totalSum){

    var array = equation.split(''); //["(","1", "+", "3",")" + "+", "2","+" ,"4"] ;


    var results = [];
    var total = totalSum;

    results.push(array.join('').toString());

    // Utility to switch two array members at positions x & y
    Array.prototype.swap = function (x, y) {
        var t = this[x];
        this[x] = this[y];
        this[y] = t;
        return this
    };

    // Find permutations recursively via Heap's Algorithm
    function permutations(len, ary) {
        var i = 0, l = len - 1;
        if (len === 1) return;
        else {
            for (i; i < l; i++) {
                permutations(l, ary);
                l % 2 ? ary.swap(i, l) : ary.swap(0, l); // even-odd check for alternating swaps
                results.push(ary.join('').toString());
            }
            permutations(l, ary);
        }
        return results.sort();
    }

    permutations(array.length, array);




    var mathValidArray = [];
    var mathMapUniqueEquations = {};
    for(var i = 0; i < results.length; i++){
        try{
            if(eval(results[i]) == totalSum){
                mathValidArray.push(results[i]);
                mathMapUniqueEquations[results[i]] = results[i]
                //console.log(" VALID: " + results[i])
            }
        }catch(ignored){

        }
    }

    var finalValidPermutations = [];
    for( var x in mathMapUniqueEquations){
        //console.log(mathMapUniqueEquations[x]);
        finalValidPermutations.push(mathMapUniqueEquations[x])
    }
    return finalValidPermutations;

}
_completedTaskScore.scoringData = JSON.stringify(_completedTaskScore.scoringData);
completedTaskScore = JSON.stringify(_completedTaskScore);

var _indScor = [];
for(var iss in _individualSubjectScore){
    _indScor.push(_individualSubjectScore[iss]);
}
individualSubjectScores = JSON.stringify(_indScor);