class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference, questionJson, jeopardyJson) {
        super(jeopardyReference, questionJson, jeopardyJson);
        this.index = this.registerListenerAndGetFieldId(this);

        this.result = [];
        for (var i = 0; i < questionJson.length; i++) {
            for (var j = 0; j < questionJson[i].length; j++) {
                this.result.push(questions[questionJson[i][j] - 1]);
            }
        }
        this.str = "";
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
                + choice
            this.str += '  </label> </div>'

        }.bind(this));
        this.str += ' </div> ';
        this.str += '<div class="text-center" id="submitAnswer">\n' +
            '                    <br>\n' +
            '                        <div class="form-group form-inline form-row justify-content-center">\n' +
            '                            <button type="button" class="btn btn-light" id="submitButton">Submit Answer</button>\n' +
            '                        </div>\n' +
            ' </div>'
        $("submitAnswer").bind(this);

        this.str += this.getInteractionIndicatorHTML();
        this.str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">'
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
        $('#submitAnswer').on('click', this.handleSubmitOnClick.bind(this));
        $('#askMachine').on('click', this.handleAskMachineOnClick.bind(this));
    }

    handleAskMachineOnClick(event) {
        let machSuggestion = "";
        let randInt = Math.random();
        console.log(this.result[0]);
        if (randInt <= this.prob)
            machSuggestion += this.result[0].Answer;
        else {
            let nonAnswers = this.removeA(this.result[0].value, this.result[0].Answer);
            machSuggestion += nonAnswers[Math.floor(Math.random() * 3)];
        }
        console.log(machSuggestion);
        let machStr = "";
        machStr += '<div class="text-center text-dark col-4" id="machSuggestion">\n' +
            machSuggestion + '</div>';
        var element = document.getElementById("machSuggestion");
        if (element) {
            element.innerHTML = machStr;
        }
        else
            $('#jeopardyForm').append(machStr);
    }

    handleSubmitOnClick(event) {
        let cellIndex = $('input[name="answer"]:checked').index();
        console.log("answer " + cellIndex);
        if (!isNaN(cellIndex)) {
            let valueTyped = $('input[name="answer"]:checked').val();
            if (valueTyped === undefined)
                valueTyped = "Not Answered";
            console.log("Typed Value: " + valueTyped);
            if (valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(JEOPARDY_CONST.FIELD_NAME + cellIndex,
                    valueTyped, 0.0,
                    0, true, JEOPARDY_CONST.RADIO_FIELD);
            }
        }
    }

    broadcastReceived(message) {
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;

        if (attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) { //sync radio button
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            // var radioButtons = $("#answer" + question_number).find("input[value='" + message.content.attributeStringValue + "']").prop("checked", true);
            this.setFinalAnswer(message.sender);
            this.questionNumber++;
            this.stopTime = (new Date().getTime() / 1000) + 122;
            console.log("Next question number " + this.questionNumber);

            var questionEl = document.getElementById("question-answer-machine");
            if (questionEl) {
                console.log("next");
                if (this.questionNumber === 40) {
                    this.str = '<div id = "thankYou"> ' +
                        '<p class = "text-dark"> End of Experiment</p>' +
                        ' </div>';
                    $('#jeopardyForm').append(this.str);
                }
                else if ((this.questionNumber + 1) % 10 === 0) {
                    this.str = '<div id = "roundTransition"> ' +
                        '<p class = "text-dark"> Going to the next round...</p>' +
                        ' </div>';
                    questionEl.innerHTML = this.str;
                    // this.sleep(50000);
                    // this.setupHTML();
                    // questionEl.innerHTML = this.str;
                    // this.setupHooks();
                }
                else {
                    this.setupHTML();
                    questionEl.innerHTML = this.str;
                    this.setupHooks();
                }
            }
            //End of round give a message
            //End of task -> Thanks
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