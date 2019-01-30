class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference, questionJson, jeopardyJson) {
        super(jeopardyReference, questionJson, jeopardyJson);
        //todo
        this.index = this.registerListenerAndGetFieldId(this);
        //open file and keep the questions in a variable

        this.questionSetJson = questions;
        this.probabilities = jeopardyJson;

        this.localPogsPlugin = this.getPogsPlugin();
        var teammates = this.localPogsPlugin.getTeammates();
        var currentId = this.localPogsPlugin.getSubjectId();
        console.log("Current ID "+ currentId);
        for (var i = 0; i < teammates.length; i++) {
            if (currentId == teammates[i]) {
                if (i == 0)
                    this.prob = parseFloat(this.probabilities.prob1);
                else if (i==1)
                    this.prob = parseFloat(this.probabilities.prob2);
                else if (i==2)
                    this.prob = parseFloat(this.probabilities.prob3);
                else
                    this.prob = parseFloat(this.probabilities.prob4);
            }
        }
        console.log("This.prob "+ parseFloat(this.probabilities.prob1));
        this.setupHTML();
        this.setupHooks();
    }

    setupHTML() {
        let str = "";
        for (let j = 1; j < 3; j++) {
            this.result = this.questionSetJson.filter(obj => (obj.ID === j));
            // console.log(this.result);
            str += '<div id="question-answer-machine"><label id="question' + this.index + '" class="text-left text-dark row">' + this.result[0].question + '</label>'
            str += '<div id="answer' + this.index + '" class="col-8">'
            $.each(this.result[0].value, function (j, choice) { // setup radio question
                str += '<div class="form-check form-inline row">'
                str += '  <label class="form-check-label text-left text-dark">'
                str +=
                    '    <input type="radio" class="form-check-input" name="answer"' + this.index
                    + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                    + choice
                str += '  </label> </div>'

            }.bind(this));
            str += ' </div>';
            str += '<div class="text-center" id="submitAnswer">\n' +
                '        <p>Please click the button below when you are ready</p>\n' +
                '        <br>\n' +
                '        <div class="form-group form-inline form-row justify-content-center">\n' +
                '             <button type="button" class="btn btn-light" id="submitButton">Submit Answer</button>\n' +
                '        </div>\n' +
                ' </div>';
            $("submitAnswer").bind(this);
            str += this.getInteractionIndicatorHTML();


            str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">'
            str+= '<div class="row"> ';
            str += '<div class="text-center text-dark col-4" id="askMachine">\n' +
                '      <p>Click the button below to ask the Machine. Remember that asking the machine subtracts one point from your score.</p>\n' +
                '      <br>\n' +
                '           <div class="form-group form-inline form-row justify-content-center">\n' +
                '               <button type="button" class="btn btn-default" id="askMachineButton">Ask Machine</button>\n' +
                '           </div>\n' +
                '   </div>';
            $("askMachine").bind(this);
            str += '</div> <br></div>';
        }
    }

    setupHooks() {
        super.setupHooks();
        $('#submitAnswer').on('click',this.handleSubmitOnClick.bind(this));
        $('#askMachine').on('click',this.handleAskMachineOnClick.bind(this));
    }

    handleAskMachineOnClick(event){
        let machSuggestion = "";
        let randInt = Math.random();
        console.log(this.result[0]);
        if(randInt <= this.prob)
            machSuggestion += this.result[0].Answer;
        else
        {
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
        // console.log("event.target " + $(event.target))
        // let cellIndex = parseInt($(event.target).data("data-cell-reference-index"));
        let cellIndex = $('input[name="answer"]:checked').index();
        console.log("answer " + cellIndex);
        if (!isNaN(cellIndex)) {
            // console.log($(event.target))
            // var valueTyped = $(event.target).attr('value'); // value of radio button
            let valueTyped = $('input[name="answer"]:checked').val();
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

        if(attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1){ //sync radio button
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            this.setFinalAnswer(message.sender);
            //Show next questions
            //End of round give a message
            //End of task -> Thanks
        }
    }

    removeA(arr) {
        var what, a = arguments, L = a.length, ax;
        while (L > 1 && arr.length) {
            what = a[--L];
            while ((ax= arr.indexOf(what)) !== -1) {
                arr.splice(ax, 1);
            }
        }
        return arr;
    }

}