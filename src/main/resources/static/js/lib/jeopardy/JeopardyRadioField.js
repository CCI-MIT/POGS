class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference, questionJson, jeopardyJson) {
        super(jeopardyReference, questionJson, jeopardyJson);
        this.index = this.registerListenerAndGetFieldId(this);
        this.selectedValue = null;
        this.result = [];
        this.answers = [];
        this.isInfluencePage = false;
        this.delayToAdd = 90;
        this.isSumCorrect = false;
        this.influenceCounter = 0;
        this.areFieldsFilled = true;
        this.individualResponseSubmitted = false;
        for (var i = 0; i < questionJson.length; i++) {
            this.result.push(questions[questionJson[i] - 1]);
        }
        this.str = "";
        this.score = 0;
        this.totalTime = 120
        this.stopTime = (new Date().getTime() / 1000) + this.totalTime;
        this.questionNumber = 0;
        var probabilities = jeopardyJson;
        this.prob;
        this.localPogsPlugin = this.getPogsPlugin();
        this.teammates = this.localPogsPlugin.getTeammates();
        var currentId = this.localPogsPlugin.getSubjectId();
        for (var i = 0; i < this.teammates.length; i++) {
            if (currentId == this.teammates[i].displayName) {
                if (i == 0)
                    this.prob = parseFloat(probabilities.prob1);
                else if (i == 1)
                    this.prob = parseFloat(probabilities.prob2);
                else if (i == 2)
                    this.prob = parseFloat(probabilities.prob3);
                else
                    this.prob = parseFloat(probabilities.prob4);
            }
        }
        this.setupHTML();
        $('#jeopardyForm').append(this.str);
        this.setupHooks();
        setInterval(this.timer.bind(this),1000);
    }

    setupHTML() {
        this.isInfluencePage = false;
        while (this.answers.length !==0){
            this.answers.pop();
        }
        this.str = '<div id = "question-answer-machine">';
        this.str += '<div class="form-group fa-align-right" id="jeopardyField_' + this.index + '" style="min-width: 300px;">';
        this.str += '<div><p id = "errorMessage" class ="text-danger row">Individual answer is mandatory. You or one of your ' +
            'teammates has not provided the answer yet </p></div>';
        this.str += '<div class="text-center text-dark col-4" id="askMachine">' +
            '           <div class="form-group form-inline form-row justify-content-center">' +
            '               <button type="button" class="btn btn-default" id="askMachineButton">Ask Machine</button>' +
            '   </div>';
        $("#askMachine").bind(this);
        this.str += '</div><div class = "text-center text-dark" id = "askMachineSuggestion"></div></div>';
        if (this.questionNumber != 0){
            this.str += '<div><p id = "showAnswer" class="text-right text-dark row">Answer to previous question is &nbsp<b>' +
                this.result[this.questionNumber-1].Answer +'</b></p></div>';
        }
        $("#showAnswer").bind(this);
        $("#askMachineButton").bind(this);
        $("#messageInput").attr("disabled","true");
        this.str += '<div><p id = "jeopardyCountdown" class="text-right text-dark row"></p></div>';
        this.str += '<div><p id = "jeopardyScore" class="text-right text-dark row"></p></div>';
        this.str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">'

        this.str += '<label id="question' + this.index + '" class="text-left text-dark row">' + this.result[this.questionNumber].question + '</label>'
        this.str += '<div id="answer' + this.index + '">'
        $.each(this.result[this.questionNumber].value, function (j, choice) { // setup radio question
            this.str += '<div class="form-check form-inline row">'
            this.str += '  <label class="form-check-label text-left text-dark">'
            this.str +=
                '    <input type="radio" class="form-check-input" name="answer"' + this.index
                + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                + choice;
            this.str += '  </label> </div>'

        }.bind(this));
        this.str += ' </div> ';
        this.str += '<div class="text-center" id="submitAnswer">' +
            '                        <div class="form-group form-inline form-row justify-content-center">' +
            '                            <button type="button" class="btn btn-light" id="submitButton">Submit Answer</button>' +
            '                        </div>' +
            ' </div>';
        $("submitAnswer").bind(this);
        this.str += this.getInteractionIndicatorHTML();
        this.str += '<div id = "initialResponse" class = "row">';
        this.str += '</div>';
        $("#initialResponse").bind(this);
        this.str += '</div><br>';

        setTimeout( this.individualAnswerBroadcast.bind(this),30000);
        setTimeout(this.individualAnswerDisplay.bind(this), 31000);
    }

    setupHooks() {
        super.setupHooks();
        $("#errorMessage").hide();
        $("#initialResponse").hide();
        $("#initialResponse").delay(32000).fadeIn('fast');
        $("#askMachineButton").hide();
        $("#askMachineButton").delay(60000).fadeIn('fast');
        $('#showAnswer').delay(10000).fadeOut('fast');
        $("#submitButton").hide();
        $('#submitButton').delay(60000).fadeIn('fast');
        if (this.isInfluencePage===false){
            setTimeout(function() {
                $('#messageInput').removeAttr('disabled');
            }, 30000);
        }
        $('#answer' + this.index + ' input').on('change', this.handleRadioOnClick.bind(this));
        $('#submitAnswer').on('click', this.handleSubmitOnClick.bind(this));
        $('#askMachine').on('click', this.handleAskMachineOnClick.bind(this));
        $('#finishSurvey').on('click', this.handleSurveyFinishedOnClick.bind(this));
        for (var i = 0;i<this.teammates.length; i++)
            $('#MemberInfluence-'+i).on('input', this.sumInfluence.bind(this));
        for (var i = 0;i<this.teammates.length; i++)
            $('#AgentRating-'+i).on('input', this.sumInfluence.bind(this));
    }

    handleAskMachineOnClick(event) {
        let machSuggestion = "";
        this.score = this.score - 1;
        let randInt = Math.random();
        if (randInt <= this.prob)
            machSuggestion += this.result[this.questionNumber].Answer;
        else {
            let nonAnswers = this.removeA(this.result[this.questionNumber].value, this.result[this.questionNumber].Answer);
            machSuggestion += nonAnswers[Math.floor(Math.random() * 3)];
        }
        if ((this.stopTime - (new Date().getTime() / 1000))>=this.totalTime-59)
            return;

        this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME+"0",
            "AskMachine:"+machSuggestion, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.ASK_MACHINE);

        var element = document.getElementById("askMachineSuggestion");
        if (element) {
            document.getElementById("askMachineSuggestion").innerHTML = machSuggestion;
        }
    }

    handleSubmitOnClick(event) {
        let cellIndex = $('input[name="answer"]:checked').index();
        if (!isNaN(cellIndex)) {
            let valueTyped = $('input[name="answer"]:checked').val();
            let consensusReached = true;
            for (var i=1;i<this.teammates.length;i++){
                if (document.getElementById("individualAnswer-"+this.teammates[i].externalId) &&
                    document.getElementById("individualAnswer-"+this.teammates[i].externalId)!=
                    document.getElementById("individualAnswer-"+this.teammates[i-1].externalId)){
                    consensusReached = false;
                    break;
                }
                if (document.getElementById("individualAnswer-"+this.teammates[i].externalId)===null)
                    consensusReached = false;
            }

            if (consensusReached && valueTyped == this.result[this.questionNumber].Answer)
                this.score = this.score + 5;
            else if (valueTyped === undefined) {
                valueTyped = "Consensus Not Reached";
                this.score = this.score;
            } else {
                this.score = this.score;
            }
            if (valueTyped != null) {
                if ((this.stopTime - (new Date().getTime() / 1000) < this.totalTime-30))
                    this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex + "__" + this.localPogsPlugin.getSubjectId(),
                        valueTyped, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.SUBMIT_FIELD);
                else {
                    this.getPogsPlugin().saveCompletedTaskAttributeWithoutBroadcast(JEOPARDY_CONST.FIELD_NAME + cellIndex, valueTyped,
                        this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.INDIVIDUAL_RESPONSE);
                }
            }
        }
    }

    handleSurveyFinishedOnClick(event) {
        let cellIndex = 0;
        if (!isNaN(cellIndex)) {
            let m1 = $('input[id="MemberInfluence-0"]').val();
            let m2 = $('input[id="MemberInfluence-1"]').val();
            let m3 = $('input[id="MemberInfluence-2"]').val();
            let m4 = $('input[id="MemberInfluence-3"]').val();
            let memberInfluences = [m1, m2, m3, m4];
            let agentRatings = [$('input[id="AgentRating-0"]').val(), $('input[id="AgentRating-1"]').val(), $('input[id="AgentRating-2"]').val(), $('input[id="AgentRating-3"]').val()];

            if (agentRatings != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex + "__" + this.localPogsPlugin.getSubjectId(),
                    "Agent Ratings "+agentRatings.toString() + "Member Influences "+ memberInfluences.toString(), 0, this.score, true, JEOPARDY_CONST.INFLUENCE_MATRIX);
            }
        }
    }

    handleRadioOnClick(event) {
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {
            this.selectedValue = $(event.target).attr('value'); // value of radio button
            console.log("Value of button clicked: " + this.selectedValue);
            if(this.selectedValue != null) {
                if ((this.stopTime - (new Date().getTime() / 1000)<this.totalTime-29))
                    this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    this.selectedValue, this.result[this.questionNumber].ID, this.score, true,
                        JEOPARDY_CONST.GROUP_RADIO_RESPONSE);
                else {
                    this.individualResponseSubmitted = true;
                    this.getPogsPlugin().saveCompletedTaskAttributeWithoutBroadcast(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                        this.selectedValue, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.INDIVIDUAL_RESPONSE);
                }
            }
        }
    }

    roundTransitionHTML(event) {
        this.isInfluencePage = true;
        this.isSumCorrect = false;
        this.areFieldsFilled = true;
        $("#messageInput").attr("disabled","true");
        this.str = '<div id = "roundTransition"> ';
        if (this.questionNumber != 0){
            this.str += '<div><p id = "showAnswer" class="text-right text-dark row">Answer to previous question is &nbsp<b>' +
                this.result[this.questionNumber-1].Answer +'</b></p></div>';
        }
        $("#showAnswer").bind(this);
        this.str+= '<div><p id = "errorMessage" class ="text-danger row">Make sure that the influence adds to 100 and all fields are filled!<br> ' +
            'If your submission is correct, one of your teammates has made a mistake </p></div>'+
            '<div><p id = "jeopardyCountdown" class="text-right text-dark row"></p></div>' +
            '<p class = "text-dark"> <b>Influence of your teammates so far. <br> The numbers must add up to 100</b></p>';

        this.str += '<table class="table table-striped text-dark">'+
            '<tr>'+
            '<th>Member</th>'+
            '<th>Influence</th>'+
            '</tr>';
        for(var j = 0;j<this.teammates.length;j++){
            if(this.localPogsPlugin.getSubjectId() == this.teammates[j].externalId){
                this.str+='<tr class="text-dark">'+
                    '<td><b> You </b></td>'+
                    '<td>'+ '<input type="number" id="MemberInfluence-'+j+'" maxlength="3" size="3"/>'+ '</td>'+
                    '</tr>';
                continue;
            }
            this.str+='<tr class="text-dark">'+
                '<td>'+ this.teammates[j].displayName +'</td>'+
                '<td>'+ '<input type="number" id="MemberInfluence-'+j+'" maxlength="3" size="3"/>'+ '</td>'+
                '</tr>';
        }
        this.str += '</table>';
        this.str +='<p class = "text-dark" id="influenceSum"></p><br>';
        this.str += '<p class = "text-dark"><b> Rate the competence of the machines so far between 0 to 1</b></p>';
        this.str += '<table class="table table-striped text-dark">'+
        '<tr>'+
        '<th>Agent</th>'+
        '<th>Rating</th>'+
        '</tr>';

        for(var i = 0; i< this.teammates.length;i++){
        this.str+='<tr class="text-dark">'+
                  '<td>'+ this.teammates[i].displayName +'</td>'+
                  '<td>'+ '<input type="number" id="AgentRating-'+i+'" maxlength="2" size="2"/>'+ '</td>'+
                  '</tr>';
        }
        this.str += '</table>' + ' </div>';

        this.str += '<div class="text-center" id="finishSurvey">\n' +
            '            <br>\n' +
            '            <div class="form-group form-inline form-row justify-content-center">\n' +
            '                 <button type="button" class="btn btn-link" id="surveyFinishButton"></button>\n' +
            '            </div>\n' +
            ' </div>';
        $("finishSurvey").bind(this);
    }

    broadcastReceived(message) {
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;
        let buttonType = message.content.extraData;
        if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) && (buttonType == JEOPARDY_CONST.SUBMIT_FIELD)) {

            if (document.getElementById("machSuggestion")) {
                document.getElementById("machSuggestion").innerHTML = '<div class="text-center text-dark col-4" id="machSuggestion">\n' + '</div>';
            }

            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            // var radioButtons = $("#answer" + question_number).find("input[value='" + message.content.attributeStringValue + "']").prop("checked", true);
            this.setFinalAnswer(message.sender);
            this.questionNumber++;
            this.stopTime = (new Date().getTime() / 1000) + this.totalTime;

            var questionEl = document.getElementById("question-answer-machine");
            if (questionEl) {
                if (this.questionNumber === 1 || this.questionNumber===4 || this.questionNumber===6) {
                    console.log("round transition");
                    this.influenceCounter+=1;
                    console.log("Influence Counter "+this.influenceCounter);
                    if (this.influenceCounter===2)
                        this.delayToAdd = 45;
                    this.stopTime = (new Date().getTime() / 1000) + this.delayToAdd;
                    this.roundTransitionHTML();
                    questionEl.innerHTML = this.str;
                    //this.setupHTML();
                    this.setupHooks();
                }
                else {
                    this.setupHTML();
                    this.score = message.content.attributeIntegerValue;
                    let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>'
                    questionEl.innerHTML = this.str;
                    document.getElementById(("jeopardyScore")).innerHTML = updateScore;
                    this.setupHooks();
                }
            }
            //End of round give a message
            //End of task -> Thanks
        } else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) && (buttonType == JEOPARDY_CONST.GROUP_RADIO_RESPONSE)){
            // var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            // var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            // this.setFinalAnswer(message.sender);
            if (document.getElementById("individualAnswer-"+message.sender))
                document.getElementById("individualAnswer-"+message.sender).innerHTML = message.content.attributeStringValue;
        }
        else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.ASK_MACHINE)){
            this.score = message.content.attributeIntegerValue;
            let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>';
            document.getElementById(("jeopardyScore")).innerHTML = updateScore;
            $("#askMachineButton").attr("disabled", "true");
            let machineAnswer = message.content.attributeStringValue;
            if (document.getElementById("machineResponse-"+message.sender))
                document.getElementById("machineResponse-"+message.sender).innerHTML = machineAnswer.substring(machineAnswer.indexOf(':')+1);
        }else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.INFLUENCE_MATRIX)){
            this.nextQuestionSetup(message);
        } else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.INDIVIDUAL_SUBMISSION)){
            this.answers.push(message.content.attributeStringValue);
        } else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.INCREASE_STOPTIME)){
            this.stopTime = Number(message.content.attributeStringValue);
            $("#errorMessage").show();
        }
    }

    removeA(arr) {
        var what, a = arguments, L = a.length, ax;
        while (L > 1 && arr.length) {
            what = a[--L];
            while ((ax = arr.indexOf(what)) !== -1) {
                arr.splice(ax, 1);
            }
        }
        return arr;
    }

    timer(){
        var startTime = new Date().getTime() / 1000;
        var distance = Math.floor(this.stopTime - startTime);
        if (this.isInfluencePage === false && distance=== this.totalTime-28 && this.individualResponseSubmitted===false){
            // this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + "0",
            //     (this.stopTime + 15).toString(), this.result[this.questionNumber].ID, this.score, false, JEOPARDY_CONST.INCREASE_STOPTIME);
        } else if (this.isInfluencePage && distance===5 && (this.isSumCorrect===false || this.areFieldsFilled ===false)){
            this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + "0",
                (this.stopTime + 15).toString(), this.result[this.questionNumber].ID, this.score, false, JEOPARDY_CONST.INCREASE_STOPTIME);
        }
        if (distance < 0) {
            // clearInterval(x);
            if(document.getElementById("finishSurvey"))
                document.getElementById("finishSurvey").click();
            if(document.getElementById("submitAnswer"))
                document.getElementById("submitAnswer").click();
        }
        document.getElementById("jeopardyCountdown").innerHTML = distance + "s ";
    }

    nextQuestionSetup(message){
        if (document.getElementById("machSuggestion")) {
            document.getElementById("machSuggestion").innerHTML = '<div class="text-center text-dark col-4" id="machSuggestion">\n' + '</div>';
        }

        this.setFinalAnswer(message.sender);
        this.stopTime = (new Date().getTime() / 1000) + this.totalTime;

        var questionEl = document.getElementById("question-answer-machine");
        if (questionEl) {
            if (this.questionNumber === 63) {
                this.str = '<div id = "thankYou"> ' +
                    '<p class = "text-dark"> End of Experiment</p>' +
                    ' </div>';
                questionEl.innerHTML = this.str;
            }else {
                this.setupHTML();
                this.score = message.content.attributeIntegerValue;
                let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>';
                questionEl.innerHTML = this.str;
                document.getElementById(("jeopardyScore")).innerHTML = updateScore;
                this.setupHooks();
            }
        }
    }

    individualAnswerBroadcast(){
        let cellIndex = $('input[name="answer"]:checked').index();
        if (!isNaN(cellIndex)) {
            let valueTyped = $('input[name="answer"]:checked').val();
           if (valueTyped === undefined) {
                valueTyped = "No Answer";
            }
            if (valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    valueTyped + "__" + this.localPogsPlugin.getSubjectId(), this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.INDIVIDUAL_SUBMISSION);
            }
        }
    }

    individualAnswerDisplay(){
        let responseAnswer = '<table border="1" class="text-dark"><tr><th>Subject</th><th>Option</th></tr>';
        for(var j = 0; j<this.answers.length; j++){
            let stringValue = this.answers[j];
            responseAnswer+='<tr class="text-dark">'+
                '<td>'+ stringValue.substr(stringValue.indexOf("__")+2) +'</td>'+
                '<td id="individualAnswer-' + stringValue.substr(stringValue.indexOf("__")+2)+'">'+
                stringValue.substr(0, stringValue.indexOf("__"))+'</td>'+
                '</tr>';
        }
        responseAnswer += '</table> &nbsp; &nbsp;';
        responseAnswer += '<table border="1" class="text-dark"><tr><th>Machine</th><th>Option</th></tr>';
        for (var i =0;i<this.teammates.length;i++){
            responseAnswer+= '<tr class="text-dark">'+
                '<td>'+ this.teammates[i].externalId + " machine" +'</td>'+
                '<td id = "machineResponse-'+this.teammates[i].displayName+'">Not Used</td>'+
                '</tr>';
        }
        responseAnswer += '</table>';
        if (document.getElementById("initialResponse"))
            document.getElementById("initialResponse").innerHTML = responseAnswer;
    }

    sumInfluence(){
        var x = 0;
        var conditionMet = false;
        for (var i =0;i<this.teammates.length;i++){
            if (document.getElementById("MemberInfluence-"+i)) {
                x += Number(document.getElementById("MemberInfluence-" + i).value);
                if (document.getElementById("MemberInfluence-" + i).value.length ===0) {
                    this.areFieldsFilled = false;
                    conditionMet = true;
                }
            }
        }
        for (var i = 0; i < this.teammates.length; i++) {
            var temp = document.getElementById("AgentRating-" + i);
            if (temp && (temp.value.length === 0 || Number(temp.value) < 0 || Number(temp.value) > 1)) {
                this.areFieldsFilled = false;
                conditionMet = true;
                break;
            }
        }

        if (conditionMet===false)
            this.areFieldsFilled = true;
        if (x===100)
            this.isSumCorrect = true;
        if (document.getElementById("influenceSum"))
            document.getElementById("influenceSum").innerHTML = "<b>Sum of influences is " + x +"</b>";
    }
}