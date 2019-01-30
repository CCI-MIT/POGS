class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference, questionJson, jeopardyJson) {
        super(jeopardyReference, questionJson, jeopardyJson);
        this.index = this.registerListenerAndGetFieldId(this);

        this.result = [];
        for (var i=0;i<questionJson.length;i++){
            for (var j = 0; j<questionJson[i].length;j++) {
                this.result.push(questions[questionJson[i][j]-1]);
            }
        }
        console.log(this.result);
        this.questionNumber = 0;
        this.localPogsPlugin = this.getPogsPlugin();
        var teammates = this.localPogsPlugin.getTeammates();
        var currentId = this.localPogsPlugin.getSubjectId();
        for (var i = 0; i < teammates.length; i++) {
            if (currentId == teammates[i]) {
                if (i == 0)
                    this.prob = jeopardyJson.prob1;
                else if (i == 1)
                    this.prob = jeopardyJson.prob2;
                else if (i == 2)
                    this.prob = jeopardyJson.prob3;
                else
                    this.prob = jeopardyJson.prob4;
            }
        }
        this.setupHTML();
        this.setupHooks();
    }

    setupHTML() {
        let str = "";
        str += '<div class="form-group" id="jeopardyField_' + this.index + '" style="min-width: 300px;">'
        // let question = result.values();
        str += '<label id="question' + this.index + '" class="text-left text-dark row">' + this.result[this.questionNumber].question + '</label>'
        str += '<div id="answer' + this.index + '">'
        $.each(this.result[this.questionNumber].value, function (j, choice) { // setup radio question
            str += '<div class="form-check form-inline row">'
             str += '  <label class="form-check-label text-left text-dark">'
            str +=
                '    <input type="radio" class="form-check-input" name="answer"' + this.index
                + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                + choice
            str += '  </label> </div>'

        }.bind(this));
        str += ' </div> ';
        str += '<div class="text-center" id="submitAnswer">\n' +
            '                    <br>\n' +
            '                        <div class="form-group form-inline form-row justify-content-center">\n' +
            '                            <button type="button" class="btn btn-light" id="submitButton">Submit Answer</button>\n' +
            '                        </div>\n' +
            ' </div>'
        $("submitAnswer").bind(this);
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>';
        $('#jeopardyForm').append(str);
    }

    setupHooks() {
        super.setupHooks();
        $('#submitAnswer').on('click', this.handleSubmitOnClick.bind(this));
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

        if (attrName.indexOf(JEOPARDY_CONST.FIELD_NAME) != -1) { //sync radio button
            var question_number = attrName.replace(JEOPARDY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer" + question_number).find("input[value='" + message.content.attributeStringValue + "']").prop("checked", true);
            this.setFinalAnswer(message.sender);
            // this.questionNumber++;
            //Show next questions
            //End of round give a message
            //End of task -> Thanks
        }
    }

}