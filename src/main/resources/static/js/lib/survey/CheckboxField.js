class CheckboxField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
        this.setupHooks()
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">'
        str += '<label id="question'+this.index+'" class="text-left text-dark row">'+ this.jsonInfo.question +'</label>'

        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }

        str += '<div id="answer'+this.index+'">'
        $.each(this.jsonInfo.value, function(j, choice){
            str += '<div class="form-check form-inline row">'
            str += '<label class="form-check-label text-dark">'
            str += '<input type="checkbox" class="form-check-input" name="answer'+this.index+'" value="'+choice+'" data-cell-reference-index="'+this.index+'" data-cell-reference-subindex="'+j+'">' + choice
            str += '</label> </div>'
        }.bind(this));
        str += '</div>';
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>'
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        //$('#answer'+this.index+' input').on('change',this.handleCheckboxOnClick.bind(this));
        $('#answer'+this.index+' input').on('click', this.handleFocusIn.bind(this));
        $('#answer'+this.index+' input').on('change', this.handleFocusOut.bind(this));
    }
    handleFocusIn(event) {
        //console.log("checkbox clicked");
        let target = $(event.target);
        let questionIndex = parseInt(target.data("cell-reference-index"));
        let subIndex = parseInt(target.data( "cell-reference-subindex"));
        let option = target.val();
        if(!isNaN(questionIndex) && option != null) {
            //console.log("sending check clicked: " + subIndex);
            this.getPogsPlugin().saveCompletedTaskAttribute((SURVEY_TRANSIENT.CLICK_CHECKBOX_NOT_LOG +""+ questionIndex),
                                                            subIndex, 0.0,
                                                            0, false,target.is(":checked") );
            //this is because the value is not yet set in the item when event is called, so the opposite will be real value
        }
    }
    handleFocusOut(event){
        // get all data from all inputs and compose final answer.
        let answer = [];
        let answerz = $('#answer'+this.index+' input');
        let target = $(event.target);
        let cellIndex = parseInt(target.data("cell-reference-index"));
        for(let i = 0; i < answerz.length; i ++) {
            if($(answerz[i]).is(':checked')) {
                answer.push($(answerz[i]).val());
            } else {
                answer.push("");
            }
        }
        //console.log("sending check answer: " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            if(answer != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                                JSON.stringify(answer), 0.0,
                                                                0, true, SURVEY_CONST.CHECKBOX_FIELD);
            }
        }
    }
    broadcastReceived(message) {
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;

        if(attrName.indexOf(SURVEY_CONST.FIELD_NAME ) != -1) {
            let fieldAnswers = JSON.parse(message.content.attributeStringValue);
            //console.log("Received checkbox answer: " + attrName);
            let answerz = $('#answer'+this.index+' input');
            for(let i= 0; i < fieldAnswers.length; i++){
                //console.log("Answer: " + fieldAnswers[i]);
                if(fieldAnswers[i] == ""){
                    $(answerz[i]).prop("checked", false);
                }else{
                    $(answerz[i]).prop("checked", true);
                }

            }
        }
        if(attrName.indexOf(SURVEY_TRANSIENT.CLICK_CHECKBOX_NOT_LOG) != -1) {
            let subIndex = parseInt(message.content.attributeStringValue);
            let checkedOfNot = (message.content.extraData === 'true');
            //console.log("Received check click : " + subIndex + " - " + checkedOfNot)
            let answerz = $('#answer'+this.index+' input');
            for(let i= 0; i < answerz.length; i++){
                let targ = $(answerz[i]);
                if(parseInt(targ.data( "cell-reference-subindex")) == subIndex){
                    targ.prop("checked", checkedOfNot);
                }
            }
            this.setFinalAnswer(message.sender);
        }
    }
}

class CheckboxFieldEdit {

    //addCheckboxQuestion
    //this.fieldList.push(new CheckboxFieldEdit(
    constructor(question_number, question, withVideo, video_url, choices, answers){
        let str = "";
        this.questionNumber = question_number;
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "checkbox" >'

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8 mandatory" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video url: </label> <input class="form-control col-sm-8 video-input" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'">'
                   + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
        }

        //add button and choices field
        str += '<div id="answer'+question_number+'">'
        $.each(choices,function(i){
            str += '<div class="form-check form-inline row">'
            str += '<div class="col-sm-1"> </div>'
            str += '<input class="col-sm-1" type="checkbox" name="answer' + question_number + '"> '
            str += '<input type="text" class="form-control col-sm-8 mandatory" placeholder="put checkbox label here" value="'+choices[i]+'"> '
            str += '<button type="button" class="btn btn-danger btn-sm remove-checkbox-choice"> X </button>'
            str += '</div>'
        });
        str += '</div>'


        str += '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-checkbox-choice" id="addCheckboxChoice' + question_number + '">+</button> </div>'

        str += '<small id="" class="form-text text-muted">If it applies provide the right answer(s) by clicking in the right checkbox(es)</small>';
        str += '</div> '

        $("#survey").append(str);

        $("#addCheckboxChoice" + question_number).click(function () { //setup addCheckboxChoice Button
            var questionNum = $(this).attr('id').match(/\d+/);
            var newCheckbox = '<div class="form-inline form-check row"><div class="col-sm-1"> </div><input class="col-sm-1 mandatory" type="checkbox" name="answer' + questionNum + '" > <input  class="form-control col-sm-8" type="text" placeholder="put checkbox label here"> <button type="button" class="btn btn-danger btn-sm remove-checkbox-choice"> X </button></div>'
            $("#answer"+questionNum).append(newCheckbox);

            $(".remove-checkbox-choice").click(function () {
                ($(this).parent()).remove();
            });
        });

        $(".remove-checkbox-choice").click(function () { //setup remove checkbox button
            ($(this).parent()).remove();
        });

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();

        });

        if(answers!=null) {
            //select answer for checkbox question
            let inputs = $('#question_set' + question_number + ' div input');
            for (let z = 0; z < answers.length; z++) {
                let e = answers[i][z];
                for (let j = 0; j < inputs.length; j++) {
                    if ($(inputs[j]).val() == answers[z]) {
                        $(inputs[j]).parent().find('input[type=checkbox]').prop("checked", true);
                    }

                }
            }
        }
    }
    composeFieldFromHTML() {
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
        $.each($("#answer"+this.questionNumber).find("input[type=text]"),function(j,input){
            choices.push(input.value);
        });
        question_set["value"] = choices;

        return question_set;

    }
    composeAnswerFromHTML(){
        let answer = [];
        $.each($("#question_set"+this.questionNumber+" div input:checked"), function(){
            answer.push($(this).siblings("input[type=text]").val());
        });
        return answer;
    }

}