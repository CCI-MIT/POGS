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











