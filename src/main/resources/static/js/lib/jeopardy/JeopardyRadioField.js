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
        this.stopTime = (new Date().getTime() / 1000) + 122;
        this.questionNumber = 0;
        var probabilities = jeopardyJson;
        this.prob;
        this.localPogsPlugin = this.getPogsPlugin();
        var teammates = this.localPogsPlugin.getTeammates();
        var currentId = this.localPogsPlugin.getSubjectId();
        for (var i = 0; i < teammates.length; i++) {
            if (currentId == teammates[i].displayName) {
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
        this.str = '<div id = "question-answer-machine">';
        this.str += '<div><p id = "jeopardyCountdown" class="text-right text-dark row"></p></div>';
        this.str += '<div><p id = "jeopardyScore" class="text-right text-dark row"></p></div>';
        this.str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">'

        // let question = result.values();
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
        this.str += '<div class="text-center" id="submitAnswer">\n' +
            '                    <br>\n' +
            '                        <div class="form-group form-inline form-row justify-content-center">\n' +
            '                            <button type="button" class="btn btn-light" id="submitButton">Submit Answer</button>\n' +
            '                        </div>\n' +
            ' </div>';
        $("submitAnswer").bind(this);

        this.str += this.getInteractionIndicatorHTML();
        this.str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">';
        this.str += '<div class="row"> ';
        this.str += '<div class="text-center text-dark col-4" id="askMachine">\n' +
            '      <br>\n' +
            '           <div class="form-group form-inline form-row justify-content-center">\n' +
            '               <button type="button" class="btn btn-default" id="askMachineButton">Ask Machine</button>\n' +
            '           </div>\n' +
            '   </div>';
        $("askMachine").bind(this);
        this.str += '</div> <br></div>';
        this.str += '</div> <br>';
    }

    setupHooks() {
        super.setupHooks();
        $('#answer'+this.index+' input').on('change',this.handleRadioOnClick.bind(this));
        $('#submitAnswer').on('click', this.handleSubmitOnClick.bind(this));
        $('#askMachine').on('click', this.handleAskMachineOnClick.bind(this));
    }

    handleAskMachineOnClick(event) {
        let machSuggestion = "";
        this.score = this.score - 1;
        let randInt = Math.random();
        console.log(this.result[this.questionNumber]);
        if (randInt <= this.prob)
            machSuggestion += this.result[this.questionNumber].Answer;
        else {
            let nonAnswers = this.removeA(this.result[this.questionNumber].value, this.result[this.questionNumber].Answer);
            machSuggestion += nonAnswers[Math.floor(Math.random() * 3)];
        }
        console.log("Machine suggestion "+machSuggestion);

        this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME+"0",
            "AskMachine: "+machSuggestion, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.ASK_MACHINE);

        let machStr = "";
        machStr += '<div class="text-center text-dark col-4" id="machSuggestion">\n' +
            machSuggestion + '</div>';
        var element = document.getElementById("machSuggestion");
        if (element) {
            document.getElementById("machSuggestion").innerHTML = machStr;
        }
        else{
            $('#jeopardyForm').append(machStr);
        }
    }

    handleSubmitOnClick(event) {
        let cellIndex = $('input[name="answer"]:checked').index();
        if (!isNaN(cellIndex)) {
            let valueTyped = $('input[name="answer"]:checked').val();
            if(valueTyped == this.result[this.questionNumber].Answer)
                this.score = this.score + 2;
            else if (valueTyped === undefined)
            {
                valueTyped = "Not Answered";
                this.score = this.score -2;
            } else
            {
                this.score = this.score -2;
            }
            console.log("Typed Value: " + valueTyped);
            if (valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    valueTyped, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.SUBMIT_FIELD);
            }
        }
    }

    handleRadioOnClick(event) {
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        console.log("answer " + cellIndex);
        if(!isNaN(cellIndex)) {
            this.selectedValue = $(event.target).attr('value'); // value of radio button
            console.log("Value of button clicked: " + this.selectedValue);
            if(this.selectedValue != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    this.selectedValue, this.result[this.questionNumber].ID, this.score, true, JEOPARDY_CONST.RADIO_FIELD);
            }
        }
    }

    broadcastReceived(message) {
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;
        let buttonType = message.content.extraData;

        if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) && (buttonType == JEOPARDY_CONST.SUBMIT_FIELD)) {

            if (document.getElementById("machSuggestion")) {
                document.getElementById("machSuggestion").innerHTML = '<div class="text-center text-dark col-4" id="machSuggestion">\n' + '</div>';
            }

            //sync submit button
            console.log("Submit button message ");
            console.log(message);
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            // var radioButtons = $("#answer" + question_number).find("input[value='" + message.content.attributeStringValue + "']").prop("checked", true);
            this.setFinalAnswer(message.sender);
            this.questionNumber++;
            this.stopTime = (new Date().getTime() / 1000) + 122;
            console.log("Next question number " + this.questionNumber);

            var questionEl = document.getElementById("question-answer-machine");
            if (questionEl) {
                console.log("next");
                if (this.questionNumber === 43) {
                    this.str = '<div id = "thankYou"> ' +
                        '<p class = "text-dark"> End of Experiment</p>' +
                        ' </div>';
                    questionEl.innerHTML = this.str;
                    // $('#jeopardyForm').append(this.str);
                }
                else if (this.questionNumber === 10 || this.questionNumber===21 || this.questionNumber===32) {
                    console.log("round transition");
                    this.stopTime = (new Date().getTime() / 1000) + 20;
                    this.str = '<div id = "roundTransition"> ' +
                        '<div><p id = "jeopardyCountdown" class="text-right text-dark row"></p></div>' +
                        '<p class = "text-dark"> Going to the next round...</p>' +
                        ' </div>';
                    this.str += '<div class="text-center" id="submitAnswer">\n' +
                        '            <br>\n' +
                        '            <div class="form-group form-inline form-row justify-content-center">\n' +
                        '                 <button type="button" class="btn btn-link" id="submitButton"></button>\n' +
                        '            </div>\n' +
                        ' </div>';
                    $("submitAnswer").bind(this);
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
        } else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) && (buttonType == JEOPARDY_CONST.RADIO_FIELD)){
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            this.setFinalAnswer(message.sender);
            // console.log("Radio button clicked "+ this.selectedValue);
        }
        else if ((attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1)&& (buttonType == JEOPARDY_CONST.ASK_MACHINE)){
            console.log("Score is "+this.score);
            this.score = message.content.attributeIntegerValue;
            let updateScore = '<div><p id = "jeopardyScore" class="text-right text-dark row">'+this.score +' points</p></div>';
            document.getElementById(("jeopardyScore")).innerHTML = updateScore;
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
        console.log("TIME "+this.stopTime);
        var distance = Math.floor(this.stopTime - startTime);
        document.getElementById("jeopardyCountdown").innerHTML = distance + "s ";
        if (distance < 0) {
            // clearInterval(x);
            document.getElementById("submitAnswer").click();
        }
    }
}
