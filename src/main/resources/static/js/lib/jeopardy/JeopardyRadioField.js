class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference, questionJson, jeopardyJson) {
        super(jeopardyReference, questionJson, jeopardyJson);
        this.index = this.registerListenerAndGetFieldId(this);
        this.selectedValue = null;
        this.result = [];
        for (var i = 0; i < questionJson.length; i++) {
            for (var j = 0; j < questionJson[i].length; j++) {
                this.result.push(questions[questionJson[i][j] - 1]);
            }
        }
        this.str = "";
        this.score = 0;
        this.stopTime = (new Date().getTime() / 1000) + 92;
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
        this.reachedConsensus = false;
    }

    setupHTML() {
        this.reachedConsensus = false;
        this.str = '<div id = "question-answer-machine">';
        this.str += '<div class="form-group fa-align-right" id="jeopardyField_' + this.index + '" style="min-width: 300px;">';
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
        this.str += '<div><table border="1" class="text-dark" id = "initialResponse"><tr><th>Subject</th><th>Option</th></tr>';
        for(var j = 0; j<this.teammates.length;j++){
            this.str+='<tr class="text-dark">'+
                '<td>'+ this.teammates[j].displayName +'</td>'+
                '<td>Answer'+ '</td>'+
                '</tr>';
        }
        this.str += '</table></div>';
        $("#initialResponse").bind(this);
        this.str += '</div> <br>';
    }

    setupHooks() {
        super.setupHooks();
        $("#initialResponse").hide();
        $("#initialResponse").delay(30000).fadeIn('fast');
        $("#askMachineButton").hide();
        $("#askMachineButton").delay(60000).fadeIn('fast');
        $('#showAnswer').delay(3000).fadeOut('fast');
        $("#submitButton").hide();
        $('#submitButton').delay(60000).fadeIn('fast');
        setTimeout(function() {
            $('#messageInput').removeAttr('disabled');
        }, 30000);

        $('#answer' + this.index + ' input').on('change', this.handleRadioOnClick.bind(this));
        $('#submitAnswer').on('click', this.handleSubmitOnClick.bind(this));
        $('#askMachine').on('click', this.handleAskMachineOnClick.bind(this));
        $('#finishSurvey').on('click', this.handleSurveyFinishedOnClick.bind(this));
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
        if ((this.stopTime - (new Date().getTime() / 1000))>=61)
            return;

        this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME+"0",
            "AskMachine: "+machSuggestion, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.ASK_MACHINE);

        var element = document.getElementById("askMachineSuggestion");
        if (element) {
            document.getElementById("askMachineSuggestion").innerHTML = machSuggestion;
        }
    }

    handleSubmitOnClick(event) {
        let cellIndex = $('input[name="answer"]:checked').index();
        if (!isNaN(cellIndex)) {
            let valueTyped = $('input[name="answer"]:checked').val();
            if (valueTyped == this.result[this.questionNumber].Answer)
                this.score = this.score + 5;
            else if (valueTyped === undefined) {
                valueTyped = "Not Answered";
                this.score = this.score;
            } else {
                this.score = this.score;
            }
            if (valueTyped != null) {
                if ((this.stopTime - (new Date().getTime() / 1000) < 61))
                    this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex, valueTyped, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.SUBMIT_FIELD);
                else {
                    this.getPogsPlugin().saveCompletedTaskAttributeWithoutBroadcast(JEOPARDY_CONST.FIELD_NAME + cellIndex, valueTyped, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.INDIVIDUAL_RESPONSE);
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
                if ((this.stopTime - (new Date().getTime() / 1000)<61))
                    this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    this.selectedValue, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.GROUP_RADIO_RESPONSE);
                else
                    this.getPogsPlugin().saveCompletedTaskAttributeWithoutBroadcast(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                        this.selectedValue, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.INDIVIDUAL_RESPONSE);
            }
        }
    }

    roundTransitionHTML(event)
    {
        this.str = '<div id = "roundTransition"> ' +
            '<div><p id = "jeopardyCountdown" class="text-right text-dark row"></p></div>' +
            '<p class = "text-dark"> <b>Influence of your teammates so far. <br> The numbers must add up to 100</b></p>';
        this.str += '<table class="table table-striped text-dark">'+
            '<tr>'+
            '<th>Member</th>'+
            '<th>Influence</th>'+
            '</tr>';
        for(var j = 0;j<this.teammates.length;j++){
            if(this.localPogsPlugin.getSubjectId() == this.teammates[j].externalId){
                continue;
            }
            this.str+='<tr class="text-dark">'+
                '<td>'+ this.teammates[j].displayName +'</td>'+
                '<td>'+ '<input type="number" id="MemberInfluence-'+j+'" maxlength="3" size="3"/>'+ '</td>'+
                '</tr>';
        }
        this.str += '</table>';
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
            this.stopTime = (new Date().getTime() / 1000) + 92;

            var questionEl = document.getElementById("question-answer-machine");
            if (questionEl) {
                if (this.questionNumber === 1 || this.questionNumber===4 || this.questionNumber===6) {
                    console.log("round transition");
                    this.stopTime = (new Date().getTime() / 1000) + 20;
                    this.roundTransitionHTML();
                    questionEl.innerHTML = this.str;
                    this.setupHTML();
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
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            this.setFinalAnswer(message.sender);
            this.reachedConsensus = true;
        }
        else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.ASK_MACHINE)){
            this.score = message.content.attributeIntegerValue;
            let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>';
            document.getElementById(("jeopardyScore")).innerHTML = updateScore;
            $("#askMachineButton").attr("disabled", "true");
        }else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.INFLUENCE_MATRIX)){
            this.nextQuestionSetup(message);
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
        this.stopTime = (new Date().getTime() / 1000) + 91;

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
                let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>'
                questionEl.innerHTML = this.str;
                document.getElementById(("jeopardyScore")).innerHTML = updateScore;
                this.setupHooks();
            }
        }
    }

}