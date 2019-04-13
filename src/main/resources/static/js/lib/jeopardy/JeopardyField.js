const JEOPARDY_CONST = {
    FIELD_NAME: "jeopardyAnswer",
    RADIO_FIELD: "RadioField",
    SUBMIT_FIELD: "SubmitButtonField",
    ASK_MACHINE: "AskedMachine",
    ROUND_TRANSITION_SURVEY: "RoundTransitionSurvey",
    INDIVIDUAL_RESPONSE: "IndividualResponse",
    GROUP_RADIO_RESPONSE: "GroupRadioResponse",
    INFLUENCE_MATRIX: "InfluenceMatrix"
};

const JEOPARDY_TRANSIENT = {
    CLICK_RADIO_NOT_LOG : "clickInRadio",
    FOCUS_IN_CELL: "focusInCell",
    MOUSE_OVER_FIELD: "mouseOverField",
    MOUSE_OUT_OF_FIELD: "mouseOutOfField"
}
class JeopardyField {
    constructor(jeopardyReference,questionJson,probabilityJson){
        this.jeopardyReference = jeopardyReference;
        this.questionJson = questionJson;
        this.probabilityJson = probabilityJson;
        this.finalAnswerSubject = null;

    }
    broadcastReceived(message){
        let attrName = message.content.attributeName;

        if(message.sender != this.getPogsPlugin().getSubjectId()) {
            if ((attrName.indexOf(JEOPARDY_TRANSIENT.MOUSE_OVER_FIELD) > -1)) {
                this.addSubjectInteraction(message.sender)
            }
        }
        if(message.sender != this.getPogsPlugin().getSubjectId()) {
            if ((attrName.indexOf(JEOPARDY_TRANSIENT.MOUSE_OUT_OF_FIELD) > -1)) {
                this.removeSubjectInteraction(message.sender);
            }
        }


    }
    getPogsPlugin(){
        return this.jeopardyReference.getPogsPlugin();
    }
    registerListenerAndGetFieldId(ref){
        // return 1;
        return this.jeopardyReference.registerListenerAndGetFieldId(ref);
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