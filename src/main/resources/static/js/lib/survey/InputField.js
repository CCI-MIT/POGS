class InputField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
        this.setupHooks();

    }
    setupHTML(){
        let str = "";


        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">';
        str += '<label for="answer'+this.index+'" id="question'+this.index+'" class="text-left text-dark row">'+ this.jsonInfo.question +'</label>'
        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }

        str += '<input type="text" class="form-control row" id="answer'+this.index+'" data-cell-reference-index="'+this.index+'" autocomplete="off" placeholder="'+this.jsonInfo.placeholder+'">';
        str += this.getInteractionIndicatorHTML();

        str += '</div> <br>';
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        //$('#answer'+this.index + '').on('focusin', this.handleTextOnClick.bind(this));
        $('#answer'+this.index + '').on('change textInput input', this.handleTextOnBlur.bind(this));

    }
    handleTextOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("cell reference " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
            // console.log(valueTyped);
            if(valueTyped != null) {
                this.saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                                valueTyped, 0.0,
                                                                0, true, SURVEY_CONST.INPUT_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);

        let attrName = message.content.attributeName;


        if (attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1) {
            var cell = attrName.replace(SURVEY_CONST.FIELD_NAME, "");

            if($("#answer" + cell).attr('type') == "text"){ // sync text field
                $("#answer" + cell).val(message.content.attributeStringValue);
                this.setFinalAnswer(message.sender);
            }

        }
    }


}

class InputFieldEdit {

    constructor(question_number, question, withVideo, video_url, answer, placeholder){
        let str = "";
        this.questionNumber = question_number;

        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "text">';
        str += '<span><div class="btn btn-sm btn-warning move_toggle">Minimize</div>Input field: <span class="question_number">' + question_number + '</span></span><div class="content">';

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class="form-control col-sm-8 mandatory" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'">  <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>';

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video url: </label> <input class="form-control col-sm-8 video-input" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'">'
                   + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
        }

        //add answer field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Right Answer: </label><input type="text" class="form-control col-sm-8" id="answer'+question_number+'" placeholder = "Put answer here" value="'+answer+'"></div>';

        //add placeholder field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Placeholder: </label> <input class="form-control col-sm-8" type="text" id="placeholder'+question_number+'" placeholder = "Put placeholder here" value="'+placeholder+'"></div>';
        str += '<small id="" class="form-text text-muted">If it applies provide the right answer in the "Right Answer" field</small>';
        str += '</div>';
        str += '</div>';

        $("#survey").append(str);




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

        return question_set;

    }
    composeAnswerFromHTML(){
        let answer = "";
        answer =  $("#answer"+this.questionNumber).val();
        return answer;
    }
}