class RadioField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
        this.setupHooks();
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">'
        str += '<label id="question'+this.index+'" class="text-left text-dark row">'+ this.jsonInfo.question +'</label>'

        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }

        str += '<div id="answer'+this.index+'">'
        if(this.jsonInfo.orientation == "vertical") {
            $.each(this.jsonInfo.value, function (j, choice) { // setup radio question
                str += '<div class="form-check form-inline row" >'
                str += '  <label class="form-check-label text-left text-dark">'
                str +=
                    '    <input type="radio" class="form-check-input" name="answer' + this.index
                    + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                    + choice
                str += '  </label> </div>'

            }.bind(this));
        } else {
            str += '<div class="form-check form-inline row" style="justify-content: center;">'
            $.each(this.jsonInfo.value, function (j, choice) { // setup radio question

                str += '  <label class="form-check-label text-left text-dark" style="margin-right: 26px;">'
                str +=
                    '    <input type="radio" class="form-check-input" name="answer' + this.index
                    + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                    + choice
                str += '  </label>'

            }.bind(this));
            str += '</div>';
        }

        str += ' </div> ';
        str += this.getInteractionIndicatorHTML();
        str+= '</div> <br>';

        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        $('#answer'+this.index+' input').on('change',this.handleRadioOnClick.bind(this));
    }
    handleRadioOnClick(event){
        console.log("Radio on change called");
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("answer " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).attr('value'); // value of radio button
            // console.log("Typed Value: " + valueTyped);
            if(valueTyped != null) {
                this.saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                                valueTyped, 0.0,
                                                                0, true, SURVEY_CONST.RADIO_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;
        if(attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1){ //sync radio button
            var question_number = attrName.replace(SURVEY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            this.setFinalAnswer(message.sender);
        }
    }
}
class RadioFieldEdit {

    //addRadioQuestion
    //this.fieldList.push(new RadioFieldEdit(
    constructor(question_number, question, withVideo, video_url, choices,answer, orientation, shouldRandomize){
            let str = "";
            this.questionNumber = question_number;
            str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "radio" >'
            str += '<span><div class="btn btn-sm btn-warning move_toggle">Minimize</div>Radio field: <span class="question_number">' + question_number + '</span></span><div class="content">';

            //add should be randomized field
            str += '<div class="form-group row"><input class="" style="margin-top: 5px" type="checkbox" id="randomize' + question_number + '" '+((shouldRandomize)?("checked"):(""))+'/><label class="col-sm-6 col-form-label">Should be randomized: </label></div>';


            //add question field
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Options orientation: </label>';
            str += '<select class="form-control row col-sm-4" id="orientation' + question_number +'" ><option value="vertical">Vertical</option><option value="horizontal">Horizontal</option></select></div>';

            if(withVideo){ //add field for video url
                str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video url: </label> '
                       + '<input class="form-control col-sm-8 video-input" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'">'
                       + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
            }

            str += '<div id="answer'+question_number+'">'
            //add button and choices field
            $.each(choices,function(i){
                str += '<div class="form-check form-inline row">'
                str += '<div class="col-sm-1"> </div>'
                str += '<input class="col-sm-1" type="radio" name="answer' + question_number + '"> '
                str += '<input type="text" class="form-control col-sm-8 mandatory" placeholder="put radio label here" value="'+choices[i]+'"> '
                str += '<button type="button" class="btn btn-danger btn-sm remove-radio-choice"> X </button>'
                str += '</div>'
            });
            str += '</div>'

            str += '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-radio-choice" id="addRadioChoice' + question_number + '">+</button> </div>'

            str += '<small id="" class="form-text text-muted">If it applies, provide the answer key by checking the radio button field next to the correct answer.</small>';
            str += '</div> '
            str += '</div>';
            $("#survey").append(str);

            $("#addRadioChoice" + question_number).click(function () { //setup addRadioChoice Button
                let questionNum = $(this).attr('id').match(/\d+/);
                let newRadio = '<div class="form-check form-inline row"><div class="col-sm-1"> </div><input class="col-sm-1" type="radio" name="answer' + questionNum + '" > <input  class="form-control col-sm-8" type="text" placeholder="put radio label here"> <button type="button" class="btn btn-danger btn-sm remove-radio-choice"> X </button></div>'
                $("#answer"+questionNum).append(newRadio);

                $(".remove-radio-choice").click(function () {
                    ($(this).parent()).remove();
                });
            });

            $(".remove-radio-choice").click(function () { //setup removeRadio Button
                ($(this).parent()).remove();
            });


            if(answer!=null) {
                //select answer for radio question
                let inputs = $('#question_set' + question_number + ' div input');
                for (let j = 0; j < inputs.length; j++) {
                    if ($(inputs[j]).val() == answer) {
                        $(inputs[j]).parent().find('input[type=radio]').prop("checked", true);
                    }
                }
            }
            if(orientation) {
                $("#orientation" + question_number).val(orientation);
            }

    }
    composeFieldFromHTML(){
        let question_set = {};

        question_set["question"] = $("#question"+this.questionNumber).val();
        question_set["type"] = $('#question_set' + this.questionNumber).attr('data-question-type');
        question_set["shouldRandomize"] = $("#randomize"+this.questionNumber).is(':checked')

        if($("#video_url"+this.questionNumber).val() != undefined){ // if question contains video_url add it
            question_set["video_url"] = $("#video_url"+this.questionNumber).val();
        }

        if($("#placeholder"+this.questionNumber)){ // if question contains placeholder add it
            question_set["placeholder"] = $("#placeholder"+this.questionNumber).val();
        }
        question_set["orientation"]= $("#orientation" + this.questionNumber).val();
        console.log($("#orientation" + this.questionNumber).val());

        let choices = [];
        $.each($("#answer"+this.questionNumber).find("input[type=text]"),function(j,input){
            choices.push(input.value);
        });
        question_set["value"] = choices;

        return question_set;

    }
    composeAnswerFromHTML(){
        let answer = "";
        answer = $("#question_set"+this.questionNumber+" div input:checked").siblings("input[type=text]").val();
        return answer;
    }
}