class SelectField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
        this.setupHooks();
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">'
        str += '<label for="answer'+this.index+'" id="question'+this.index+'" class="text-left text-dark row">'+this.jsonInfo.question+'</label>'

        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }

        str += '<select class="form-control row" id="answer'+this.index+'" data-cell-reference-index="'+this.index+'">'
        str += '<option value="" disabled selected>Please select an option...</option>'
        $.each(this.jsonInfo.value, function(j, option) {
            str += '<option value="'+option+'">'+option+'</option>'
        });
        str += '</select>';
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>'
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        $("#answer"+this.index).on('change', this.handleSelectOnChange.bind(this))
    }
    handleSelectOnChange(event) {
        let target = $(event.target);

        let cellIndex = parseInt(target.data( "cell-reference-index"));
        //console.log("Select change + " + cellIndex);
        let option = target.val();
        if(!isNaN(cellIndex) && option != null) {
            this.saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                            option, 0.0,
                                                            0, true, SURVEY_CONST.SELECT_FIELD);
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;
        if (attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1) {
            var cell = attrName.replace(SURVEY_CONST.FIELD_NAME, "");
            $("#answer" + cell).val(message.content.attributeStringValue);
            this.setFinalAnswer(message.sender);
        }
    }
}
class SelectFieldEdit {

    //addSelectQuestion
    //this.fieldList.push(new SelectFieldEdit(
    constructor(question_number, question, withVideo, video_url, choices, answer) {
        let str = "";
        this.questionNumber = question_number;
        str +=
            '<div class="container question_set" id="question_set' + question_number
            + '" data-question-type = "select" >'
        str += '<span><div class="btn btn-sm btn-warning move_toggle">Minimize</div>Select field: <span class="question_number">' + question_number + '</span></span><div class="content">';
        //add question field
        str +=
            '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8" type="text" id="question'
            + question_number + '" placeholder = "Put question here" value="' + question
            + '"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion'
            + question_number + '">remove</button> </div>'

        if (withVideo) { //add field for video url
            str +=
                '<div class="form-group row"><label class="col-sm-2 col-form-label">Video url: </label> <input class="form-control col-sm-8 video-input" type="text" id="video_url'
                + question_number + '" placeholder = "Put video url" value="' + video_url + '">'
                + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
        }

        //add button and choices field
        str +=
            '<div class="form-group row"> <div class="col-sm-2">Right Answer:</div> <select class="col-sm-8" id="answer'
            + question_number + '"> </select></div>'

        str += '<div id="answerChoices' + question_number + '">'
        $.each(choices, function (i) {
            str += '<div class="form-check form-inline row">'
            str += '<div class="col-sm-2"> </div>'
            str +=
                '<input type="text" class="form-control col-sm-8 mandatory" placeholder="put select choices here" value="'
                + choices[i] + '"> '
            str +=
                '<button type="button" class="btn btn-danger btn-sm remove-select-choice"> X </button>'
            str += '</div>'
        });
        str += '</div>'

        str +=
            '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-select-choice" id="addSelectChoice'
            + question_number + '">+</button> </div>'

        str +=
            '<small id="" class="form-text text-muted">If it applies provide the right answer in the "Right Answer" Field </small>';
        str += '</div> '
        str += '</div>';

        $("#survey").append(str);

        $.each(choices, function (i) {
            $("#answer" + question_number)
                .append('<option value="' + choices[i] + '">' + choices[i] + '</option>');
        });

        $("#addSelectChoice" + question_number).click(function () { //setup addSelectChoice Button
            var questionNum = $(this).attr('id').match(/\d+/);
            var newSelectChoice = '<div class="form-check form-inline row"><div class="col-sm-2"> </div><input  class="form-control col-sm-8" type="text" placeholder="put select label here"> <button type="button" class="btn btn-danger btn-sm remove-select-choice"> X </button></div>'
            $("#answerChoices" + questionNum).append(newSelectChoice);

            $(".remove-select-choice").click(function () {
                var valueOfDeletedField = $(this).siblings('input').val();
                if (valueOfDeletedField != "") {
                    ($("#answer" + questionNum + " option[value=" + escapeValueStringsInQuotes(
                        valueOfDeletedField) + "]")).remove();
                }
                ($(this).parent()).remove();
            });

            $("#answerChoices" + questionNum).find("input").on("blur", function () {
                $("#answer" + questionNum).empty();
                $.each($("#answerChoices" + questionNum).find("input"), function (i, e) {
                    if (e.value != "") {
                        $("#answer" + questionNum).append(
                            '<option value="' + escapeValueStringsInQuotes(e.value) + '">'
                            + e.value + '</option>');
                    }
                });
            });
        });

        $(".remove-select-choice").click(function () {
            var questionNum = $(this).parents("div[id*=question_set]").attr("id").match(/\d+/);
            var valueOfDeletedField = $(this).siblings('input').val();
            if (valueOfDeletedField != "") {
                ($("#answer" + questionNum + " option[value=" + escapeValueStringsInQuotes(
                    valueOfDeletedField) + "]")).remove();
            }
            ($(this).parent()).remove();
        });

        $("#answerChoices" + question_number).find("input").on("blur", function () {
            var questionNum = $(this).parents("div[id*=question_set]").attr("id").match(/\d+/);
            $("#answer" + questionNum).empty();
            $.each($("#answerChoices" + questionNum).find("input"), function (i, e) {
                if (e.value != "") {
                    $("#answer" + questionNum).append(
                        '<option value="' + escapeValueStringsInQuotes(e.value) + '">'
                        + e.value + '</option>');
                }
            });
        });


        if(answer!=null) {
            //select answer for checkbox question
            let inputs = $('#question_set' + question_number + ' div select option');
            for (let j = 0; j < inputs.length; j++) {
                if ($(inputs[j]).val() == answer) {
                    $(inputs[j]).prop("selected", true);
                }
            }
        }

    }

    composeFieldFromHTML(){
        let question_set = {};

        question_set["question"] = $("#question"+this.questionNumber).val();
        question_set["type"] = $('#question_set' + this.questionNumber).attr('data-question-type');
        if($("#video_url"+this.questionNumber).val() != undefined){ // if question contains video_url add it
            question_set["video_url"] = $("#video_url"+this.questionNumber).val();
        }

        if($("#placeholder"+this.questionNumber)){ // if question contains placeholder add it
            question_set["placeholder"] = $("#placeholder"+this.questionNumber).val();
        }

        let choices = [];
        $.each($("#answerChoices"+this.questionNumber).find("input[type=text]"),function(j,input){
            choices.push(input.value);
        });
        question_set["value"] = choices;

        return question_set;
    }
    composeAnswerFromHTML(){
        let answer = "";
        answer = $("#question_set"+this.questionNumber+" option:checked").val();
        return answer;
    }
}
function escapeValueStringsInQuotes(string) {
    return string.replace(/"/g, '\\"').replace(/'/g, '\\\'');
}