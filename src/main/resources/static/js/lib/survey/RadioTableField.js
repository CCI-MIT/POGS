class RadioTableField extends Field {
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

        str += '<div id="answer'+this.index+'"><table style="color:#000000"><thead><th></th>';

        let choices = this.jsonInfo.value;

        $.each(choices.columns,function(i){

            str += '<th>'+choices.columns[i]+'</th>';
        });
        str += '</thead>';
        str += '<tbody>'

        let subindex = 0;
        $.each(choices.rows,function(i){
            str += '<tr>'
                   + '<th class="row-header" style="width:300px;min-height:60px">'+choices.rows[i]+'</th>';

            for(let j =0 ; j < choices.columns.length; j ++) {
                str += '<td style="width:300px">';
                str += '<input type="radio" class="" name="answer' + this.index + '_'+ i +'" value="'+choices.columns[j]+'" data-cell-reference-subindex="'+subindex+'" data-cell-reference-index="'+this.index+'"">';
                str += '</td>';
                subindex++;
            }
            str +=  '</tr>';
        }.bind(this));

        str += '</tbody></table>';
        str += this.getInteractionIndicatorHTML();
        str += '</div>';


        $('#surveyForm').append(str);
    }
    setupHooks(){

        super.setupHooks();
        $('#answer'+this.index+' input').on('focusin', this.handleFocusIn.bind(this));
        $('#answer'+this.index+' input').on('change', this.handleFocusOut.bind(this));
    }
    handleFocusIn(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        let subIndex = parseInt($(event.target).data( "cell-reference-subindex"));


        if(!isNaN(subIndex)) {
            this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG + this.index,
                                                            "", 0.0,
                                                            subIndex, false, null);
        }
    }
    handleFocusOut(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        let subIndex = parseInt($(event.target).data( "cell-reference-subindex"));


        let answer = [];
        let answerz = $('#answer'+this.index+' input');

        let columns = []
        $.each($('#answer'+this.index+ ' thead th'),function(j,input){
            if($(input).text()!="") {
                columns.push($(input).text());
            }
        });
        for(let i = 0; i < answerz.length; i ++) {
            if($(answerz[i]).is(':checked')) {
                answer.push(columns[i%columns.length]);
            } else {
                answer.push("");
            }
        }

        if(!isNaN(cellIndex)) {
            if(answer != null) {
                this.saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                                JSON.stringify(answer), 0.0,
                                                                0, true, SURVEY_CONST.RADIO_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;

        if(attrName.indexOf(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG) != -1){


            let cell = parseInt(message.content.attributeIntegerValue);


            let allInputs = $('#answer'+this.index+' input');
            for(let k = 0 ; k < allInputs.length; k++){
                if(parseInt($(allInputs[k]).data( "cell-reference-subindex")) == cell){
                    $(allInputs[k]).prop("checked", true);
                }
            }
            this.setFinalAnswer(message.sender);

        }else {

            if(attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1) {
                let anwsers = JSON.parse(message.content.attributeStringValue);



                let allInputs = $('#answer' + this.index + ' input');
                for (let k = 0; k < allInputs.length; k++) {
                    if ($(allInputs[k]).val() == anwsers[k]) {
                        $(allInputs[k]).prop("checked", true);
                    }
                }
            }
        }
    }
}
class RadioTableFieldEdit {


    constructor(question_number, question, withVideo, video_url, choices, answers){
        let str = "";
        this.questionNumber = question_number;
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "radiotable" >'
        str += '<span><div class="btn btn-sm btn-warning move_toggle">Minimize</div>Radio table field: <span class="question_number">' + question_number + '</span></span><div class="content">';
        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8 mandatory" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label video-input">Video url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'">'
                   + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
        }

        str += '<div id="answer'+question_number+'" class="table-container"><table class="flex-table"><thead><th></th>'
        //add button and choices field
        $.each(choices.columns,function(i){

            str += '<th><button type="button" class="btn btn-danger btn-sm remove-radio-column" data-col-index="'+i+'"> X </button><input type="text" class="mandatory" value="'+choices.columns[i]+'"></th>';
        });
        str += '<th class="btn_column"><button type="button" class="btn btn-secondary btn-sm add-column-choice'+question_number+'" id="addColumnChoice'+question_number+'">+</button></th>'
        str += '</thead>';
        str += '<tbody>'

        $.each(choices.rows,function(i){
            str += '<tr>'
                   + '<th class="row-header"><button type="button" class="btn btn-danger btn-sm remove-radio-row"> X </button><input type="text" style="width:110px" class="mandatory" value="'+choices.rows[i]+'"></th>';

            for(let j =0 ; j < choices.columns.length; j ++) {
                str += '<td>';
                console.log('answer' + question_number + '_'+ i);
                str += '<input type= "radio" class="" name="answer' + question_number + '_'+ i +'" value="'+choices.columns[j]+'">' ;
                str += '</td>';
            }
            str +=  '</tr>';
        }.bind(this));
        str+= '<tr class="btn_row"><td style="text-align: left" colspan="'+choices.columns.length+'"><button type="button" class="btn btn-secondary btn-sm add-row-choice'+question_number+'" id="addRowChoice'+question_number+'">+</button></td>'

        str += '</tbody></table><small id="" class="form-text text-muted">If it applies provide the right answer by clicking in the right radio button field</small>';
        str += '</div> '
        str += '</div>';

        $("#survey").append(str);

        $(".add-column-choice" + question_number).click(function () { //setup addRadioChoice Button
            let columns = [];
            $.each($("#answer"+question_number+" thead").find("input[type=text]"),function(j,input){
                columns.push(input.value);
            });
            let questionNum = $(this).attr('id').match(/\d+/);
            let newColTH = '<th><button type="button" class="btn btn-danger btn-sm remove-radio-column" data-col-index="'+(columns.length)+'"> X </button><input type="text" value="col '+(columns.length +1)+'"/></th>';
            $(newColTH).insertBefore("#answer"+questionNum + ' .btn_column');

            $("#answer"+questionNum + ' tbody tr').each(function(i){
                if(!$(this).hasClass("btn_row")) {
                    $(this).append(
                        '<td><input class="" type="radio"  '
                        + 'name="answer'+questionNum+'_'+(columns.length)+'" value="col '+(columns.length+1)+'"></td>');

                }
            });

            $(".remove-radio-column").unbind().click(function () { //setup removeRadio Button
                let colIndex = $(this).data("col-index");
                ($(this).parent()).remove();//remove its th
                $("#answer"+question_number+" input[type=radio]").each(function(i,k){
                    if($(k).attr("name") == "answer"+question_number+"_"+colIndex ) {
                        $($(k).parent()).remove();
                    }
                });

            });
        });

        $(".add-row-choice" + question_number).click(function () { //setup addRadioChoice Button
            let questionNum = $(this).attr('id').match(/\d+/);
            let num_of_rows = $("#answer"+questionNum + ' tbody tr').length -1 ; //to take out the + row
            let columns = [];
            $.each($("#answer"+question_number+" thead").find("input[type=text]"),function(j,input){
                columns.push(input.value);
            });

            let newTR = "<tr><th><button type='button' class='btn btn-danger btn-sm remove-radio-row' > X </button><input type='text' style='width:110px' value='row "+(num_of_rows+1)+"'/> </th>";

            for(let i = 0; i < columns.length; i++){
                newTR+= '<td><input type="radio" name="answer'+questionNum+'_'+i+'" value="'+(columns[i])+'"/></td>';
            }
            newTR += "</tr>";

            $(newTR).insertBefore("#answer"+questionNum + ' .btn_row ');


            $(".remove-radio-row").unbind().click(function () { //setup removeRadio Button
                $(($(this).parent())).parent().remove();
            });
        });


        $(".remove-radio-column").click(function () { //setup removeRadio Button
            let colIndex = $(this).data("col-index");
            ($(this).parent()).remove();//remove its th
            $("#answer"+question_number+" input[type=radio]").each(function(i,k){
                if($(k).attr("name") == "answer"+question_number+"_"+colIndex ) {
                    $($(k).parent()).remove();
                }
            });

        });
        $(".remove-radio-row").click(function () { //setup removeRadio Button
            $(($(this).parent())).parent().remove();
        });

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            let question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();

        });


        if(answers!=null) {

            for (let k = 0; k < answers.length; k++) {

                if (answers[k] != "") {
                    let row = Math.floor((k) / choices.columns.length);
                    let inputs = $(
                        '#question_set' + question_number + ' tbody tr:nth-child(' + (row
                        + 1) + ') input[type="radio"]');

                    for (let j = 0; j < inputs.length; j++) {

                        if ($(inputs[j]).val() == answers[k]) {
                            $(inputs[j]).prop("checked", true);
                        }
                    }
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

        var choices = {columns: [] , rows: []};
        $.each($("#answer"+this.questionNumber+" thead").find("input[type=text]"),function(j,input){
            choices.columns.push(input.value);
            //console.log(input.value);
        });

        $.each($("#answer"+this.questionNumber+" tbody").find("input[type=text]"),function(j,input){
            choices.rows.push(input.value);

        });
        question_set["value"] = choices;
        return question_set;
    }
    composeAnswerFromHTML(){
        let answer = [];
        let columns = []
        $.each($("#answer"+this.questionNumber+" thead").find("input[type=text]"),function(j,input){
            columns.push(input.value);
        });

        let answerz = $("#question_set"+this.questionNumber+" td input");

        for(let i = 0; i < answerz.length; i ++) {
            if($(answerz[i]).is(':checked')) {
                answer.push(columns[i%columns.length]);
            } else {
                answer.push("");
            }
        }

        return answer;
    }
}
