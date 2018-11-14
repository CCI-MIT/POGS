const SURVEY_CONST = {
    FIELD_NAME: "surveyAnswer",
    INPUT_FIELD: "InputField",
    RADIO_FIELD: "RadioField",
    SELECT_FIELD: "SelectField",
    CHECKBOX_FIELD: "CheckBoxField",
    CHECKBOX_FIELD_SELECT: "checkboxSelect",
    CHECKBOX_FIELD_UNSELECT: "checkboxSelect",
};
const SURVEY_TRANSIENT = {
    CLICK_RADIO_NOT_LOG : "clickInRadio",
    FOCUS_IN_CELL: "focusInCell",
    CLICK_CHECKBOX_NOT_LOG : "clickInCheckbox",
    MOUSE_OVER_FIELD: "mouseOverField",
    MOUSE_OUT_OF_FIELD: "mouseOutOfField"
}
const SURVEY_FIELDS = {
    TEXT_FIELD : "text",
    RADIO_FIELD : "radio",
    SELECT_FIELD : "select",
    CHECKBOX_FIELD : "checkbox",

}

class Field {
    constructor(surveyRefence,jsoninfo){
        this.surveyRefence = surveyRefence;
        this.jsonInfo = jsoninfo;
        this.finalAnswerSubject = null;

    }
    broadcastReceived(message){
        let attrName = message.content.attributeName;

        if(message.sender != this.getPogsPlugin().getSubjectId()) {
            if ((attrName.indexOf(SURVEY_TRANSIENT.MOUSE_OVER_FIELD) > -1)) {
                this.addSubjectInteraction(message.sender)
            }
        }
        if(message.sender != this.getPogsPlugin().getSubjectId()) {
            if ((attrName.indexOf(SURVEY_TRANSIENT.MOUSE_OUT_OF_FIELD) > -1)) {
                this.removeSubjectInteraction(message.sender);
            }
        }


    }
    getPogsPlugin(){
        return this.surveyRefence.getPogsPlugin();
    }
    registerListenerAndGetFieldId(ref){
       return this.surveyRefence.registerListenerAndGetFieldId(ref);
    }
    getInteractionIndicatorHTML(){
        return '<small class="interaction-indicator form-text " style="font-size: 60%;text-align: left">Working on this field:<span class="subjectpool"></span> </small>'
               + '<small class="finalanswer-indicator form-text " style="font-size: 60%;text-align: left">Latest answer :<span class="finalsubjectanswer"></span> </small>'
    }
    setFinalAnswer(subjectId){
        this.finalAnswerSubject = subjectId;
        let sub = this.getPogsPlugin().getSubjectByExternalId(subjectId);
        $("#surveyField_" + this.index + " .finalsubjectanswer").empty();

        $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
          + '</span>')
            .appendTo("#surveyField_" + this.index + " .finalsubjectanswer");

        $("#surveyField_" + this.index + " .finalanswer-indicator").addClass('text-muted');
    }
    handleOnMouseOverField(event){
            this.getPogsPlugin()
                .saveCompletedTaskAttribute(SURVEY_TRANSIENT.MOUSE_OVER_FIELD+this.index,
                                                            "", 0.0,
                                                            this.index, false);
    }
    handleOnMouseOutOfField(event){
        this.getPogsPlugin()
            .saveCompletedTaskAttribute(SURVEY_TRANSIENT.MOUSE_OUT_OF_FIELD+this.index,
                                                        "", 0.0,
                                                        this.index, false);
    }
    addSubjectInteraction(subjectId){
        let sub = this.getPogsPlugin().getSubjectByExternalId(subjectId);
        if($("#surveyField_"+this.index+" .subjectpool ."+sub.externalId+'_color').length == 0) {

            $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
              + '</span>')
                .appendTo("#surveyField_" + this.index + " .subjectpool");
            $("#surveyField_" + this.index + " .interaction-indicator").addClass("text-muted");

        }
    }
    removeSubjectInteraction(subjectId){
        let sub = this.getPogsPlugin().getSubjectByExternalId(subjectId);
        if($("#surveyField_"+this.index+" .subjectpool ."+sub.externalId+'_color').length>=0) {

            $("#surveyField_"+this.index+" .subjectpool ."+sub.externalId+'_color').remove();
        }
        if($("#surveyField_"+this.index+" .subjectpool .badge").length ==0){
            $("#surveyField_" + this.index + " .interaction-indicator").removeClass("text-muted");
        }
    }
    setupHooks(){
        $('#surveyField_'+this.index ).on('mouseenter',this.handleOnMouseOverField.bind(this))
        $('#surveyField_'+this.index ).on('mouseleave',this.handleOnMouseOutOfField.bind(this))
    }
}

class VideoInformation {
    constructor(videoURL){
        this.videoURL = videoURL;
    }
    getHTML(){
        return ' <div class="embed-responsive embed-responsive-16by9 row"><iframe '
               + 'class="embed-responsive-item" src="'+this.videoURL+'" '
               + 'frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>';
    }
}


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
        str += '<input type="text" class="form-control row" id="answer'+this.index+'" data-cell-reference-index="'+this.index+'" placeholder="'+this.jsonInfo.placeholder+'">';
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>';
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        //$('#answer'+this.index + '').on('focusin', this.handleTextOnClick.bind(this));
        $('#answer'+this.index + '').on('change', this.handleTextOnBlur.bind(this));

    }
    // handleTextOnClick(event){
    //     var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
    //     if(!isNaN(cellIndex)) {
    //         this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_TRANSIENT.FOCUS_IN_CELL+this.index,
    //                                                    "", 0.0,
    //                                                    cellIndex, false);
    //     }
    // }
    handleTextOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("cell reference " + cellIndex);
        if(!isNaN(cellIndex)) {
           // console.log($(event.target))
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
           // console.log(valueTyped);
            if(valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
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
        $.each(this.jsonInfo.value,function(j, choice){ // setup radio question
            str += '<div class="form-check form-inline row">'
            str += '  <label class="form-check-label text-left text-dark">'
            str += '    <input type="radio" class="form-check-input" name="answer'+this.index+'" value="'+choice+'" data-cell-reference-index="'+this.index+'">' + choice
            str += '  </label> </div>'

        }.bind(this));

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
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("answer " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).attr('value'); // value of radio button
            // console.log("Typed Value: " + valueTyped);
            if(valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
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

class RadioTableField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        //console.log("RadioTableField - Constructor");
        this.index = this.registerListenerAndGetFieldId(this);
        //console.log("RadioTableField index field " + this.index);
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

        //$.each(this.jsonInfo.value,function(j, choice){ // setup radio question
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
        //$('#answer'+this.index+' input').on('change',this.handleRadioOnClick.bind(this));
        //console.log('#answer'+this.index+' input');
        super.setupHooks();
        $('#answer'+this.index+' input').on('focusin', this.handleFocusIn.bind(this));
        $('#answer'+this.index+' input').on('change', this.handleFocusOut.bind(this));
    }
    handleFocusIn(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        let subIndex = parseInt($(event.target).data( "cell-reference-subindex"));

        //console.log("Sending clickInRadio handleFocusIn subIndex: " + subIndex);

        //sends the click to all but does not save in the database. Save all values on focus out.
        if(!isNaN(subIndex)) {
            this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG + this.index,
                                                       "", 0.0,
                                                            subIndex, false, null);
        }
    }
    handleFocusOut(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        let subIndex = parseInt($(event.target).data( "cell-reference-subindex"));

        //console.log("Sending all answers handleFocusOut subindex: " + subIndex);

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
            // console.log($(event.target))
            if(answer != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                           JSON.stringify(answer), 0.0,
                                                           0, true, SURVEY_CONST.RADIO_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;

        if(attrName.indexOf(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG) != -1){
            // handle on focus - add class for bg change

            let cell = parseInt(message.content.attributeIntegerValue);
            //console.log("Click In Radio received: " + cell);
            //console.log("This index value: " + this.index);

            let allInputs = $('#answer'+this.index+' input');
            for(let k = 0 ; k < allInputs.length; k++){
                //console.log("Data field for input: " + $(allInputs[k]).data( "cell-reference-subindex"))
                if(parseInt($(allInputs[k]).data( "cell-reference-subindex")) == cell){
                    $(allInputs[k]).prop("checked", true);
                }
            }
            this.setFinalAnswer(message.sender);

        }else {
            let anwsers = JSON.parse(message.content.attributeStringValue);

            //console.log("Save attribute received " + attrName)
            //console.log("This.index " + this.index)
            //console.log(anwsers);

            let allInputs = $('#answer'+this.index+' input');
            for(let k = 0 ; k < allInputs.length; k++){
                if($(allInputs[k]).val() == anwsers[k]){
                    $(allInputs[k]).prop("checked", true);
                }
            }
        }
    }
}


class OrderableField extends Field {
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
        str += '<input type="text" class="form-control row" id="answer'+this.index+'" data-cell-reference-index="'+this.index+'" placeholder="'+this.jsonInfo.placeholder+'">';
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>';
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        $('#answer'+this.index + '').on('change', this.handleTextOnBlur.bind(this));

    }

    handleTextOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("cell reference " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
            // console.log(valueTyped);
            if(valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
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
            this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
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

class InformationField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" style="min-width: 300px;">';
        str += '<label class="control-label text-dark text-left row question-intro">'+this.jsonInfo.question+'</label>';

        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }
        str += '</div>';
        $('#surveyForm').append(str);
    }
}

class Survey {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.replacements  = [];
        this.replacements.push(this.pogsPlugin.getTeammatesDisplayNames());
        this.replacements.push(this.pogsPlugin.getOtherTeammates());
        this.replacements.push(this.pogsPlugin.getLastTask());
        this.replacements.push(this.pogsPlugin.getTaskList());
        this.replacements.push(this.pogsPlugin.getOtherTasks());
        this.replacements.push(this.pogsPlugin.getSessionName());

        this.fields = [];
        this.globalFieldIndex = 0;
    }
    resolveVariablesForNetworkQuestions(surveyItem){
        var regex = new RegExp(/\${.*}/gi);
        var allVariables = ['\\${allTeammates}','\\${otherTeamates}', '\\${lastTaskName}',
                            '\\${allTasksNames}','\\${otherTasksNames}', '\\${sessionName}'];

        var replacements = this.replacements;

        // [['m01', 'm02', 'm03'],
        //     ['m02', 'm03'],
        //     "Last task name",
        //     ["tast 1", "task 2","task 3"],
        //     ["task 2","task3"],
        //     "session one"]

        if(surveyItem.question.match(regex)) {

            for(var i=0; i < allVariables.length; i ++) {

                var replacer = "";
                if(surveyItem.question.match(new RegExp(allVariables[i] ,'gi'))) {
                    if(replacements[i].constructor === Array) {

                       for(var j =0 ; j < replacements[i].length; j ++) {
                           replacer += replacements[i][j];
                           if(j + 1 != replacements[i].length){
                               replacer += ", ";
                           }
                       }

                    } else {
                        replacer = replacements[i];
                    }
                    surveyItem.question =
                        surveyItem.question.replace( new RegExp( allVariables[i] ,'gi'), replacer);
                }
            }
        }
        if(!surveyItem.value){
            return surveyItem;
        }

        if (surveyItem.value.constructor === Array) {
            console.log("value is array");
            if (surveyItem.value !== undefined && surveyItem.value.length > 0) {
                for (var i = 0; i < surveyItem.value.length; i++) {

                    if (surveyItem.value[i].match(new RegExp(regex, 'gi'))) {

                        for (var j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {
                                    surveyItem.value = [];

                                    for (var k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {
                                    surveyItem.value[i] =
                                        surveyItem.value.replace(new RegExp(allVariables[j], 'gi'),
                                                                 replacements[j]);
                                }
                            }
                        }
                    }

                }
            }
        } else {

            if (surveyItem.value.constructor === Object) {
                console.log("value is object");
                for (let i = 0; i < surveyItem.value.columns.length; i++) {

                    if (surveyItem.value.columns[i].match(new RegExp(regex, 'gi'))) {

                        for (let j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value.columns[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {
                                    surveyItem.value.columns = [];

                                    for (let k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.columns.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {
                                    surveyItem.value.columns[i] =
                                        surveyItem.value.columns.replace(new RegExp(allVariables[j], 'gi'),
                                                                 replacements[j]);
                                }
                            }
                        }
                    }
                }
                for (let i = 0; i < surveyItem.value.rows.length; i++) {

                    if (surveyItem.value.rows[i].match(new RegExp(regex, 'gi'))) {

                        for (let j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value.rows[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {
                                    surveyItem.value.rows = [];

                                    for (let k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.rows.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {
                                    surveyItem.value.rows[i] =
                                        surveyItem.value.rows.replace(new RegExp(allVariables[j], 'gi'),
                                                                         replacements[j]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return surveyItem;
    }
    getPogsPlugin(){
        return this.pogsPlugin;
    }
    registerListenerAndGetFieldId(fieldImpl){

        let field = this.globalFieldIndex;
        //console.log("Field: --------" + this.globalFieldIndex);
        this.fields[field] = fieldImpl;
        this.globalFieldIndex = this.globalFieldIndex + 1;
        return field;
    }
    setupSurvey(surveyBluePrint){
    console.info("starting survey setup...");
        /*
            JSON format
            [
              {"question":"Survey question1?",
                "type": "text",
                "placeholder":"question1 placeholder",
                "default": "whatever"
              },
              {"question":"Survey question2?",
                "type": "radio/check",
                "placeholder":"question2 placeholder"
                "options": [...],
                "default": "whatever"
              },
              {"question":"Survey question3?",
                "type": "select",
                "placeholder":"question3 placeholder",
                "options": [...],
                "default": "whatever"
              }
            ]

        */
        var surveyValues = $.parseJSON(surveyBluePrint);
        var self = this;

        var str = '';
        $.each(surveyValues,function(i,e){
            console.log(e);
            e = this.resolveVariablesForNetworkQuestions(e);

            if(e.type == "text"){ // setup text question
                this.fields.push(new InputField(this,e));
            }
            else if(e.type == "radio"){ // setup radio question
                this.fields.push(new RadioField(this,e));
            }
            else if(e.type == "select") {
                this.fields.push(new SelectField(this,e));
            }
            else if(e.type == "checkbox") {
                this.fields.push(new CheckboxField(this,e));
            }
            if(e.type == "introduction") {
                this.fields.push(new InformationField(this,e));
            }else if(e.type == "radiotable"){ // setup radio question
                this.fields.push(new RadioTableField(this,e));
            }

            //console.log(i + '----'+ JSON.stringify(e));
        }.bind(this));

    }

    broadcastReceived(message){
        let attrName = message.content.attributeName;
        let index = attrName
            .replace(SURVEY_CONST.FIELD_NAME, "")
            .replace(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG,"")
            .replace(SURVEY_TRANSIENT.FOCUS_IN_CELL,"")
            .replace(SURVEY_TRANSIENT.CLICK_CHECKBOX_NOT_LOG,"")
            .replace(SURVEY_TRANSIENT.MOUSE_OVER_FIELD,"")
            .replace(SURVEY_TRANSIENT.MOUSE_OUT_OF_FIELD,"");

        if(this.fields.length > index) {
            if(message.sender != this.pogsPlugin.subjectId) {
                this.fields[index].broadcastReceived(message);
            }
        }
    }

}

var surveyPlugin = pogs.createPlugin('surveyTaskPlugin',function(){

    console.info("Survey Plugin Loaded");

    var survey = new Survey(this);
    // get config attributes from task plugin
    survey.setupSurvey(this.getStringAttribute("surveyBluePrint"));
    this.subscribeTaskAttributeBroadcast(survey.broadcastReceived.bind(survey))

});