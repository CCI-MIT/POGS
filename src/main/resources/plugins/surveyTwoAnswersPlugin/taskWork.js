
class TwoAnwserSurvey extends Survey {

    setupSurvey(surveyBluePrint){
        console.info("starting survey setup...");

        let surveyValues = $.parseJSON(surveyBluePrint);
        let self = this;

        let str = '';
        $.each(surveyValues,function(i,e){

            e = this.resolveVariablesForNetworkQuestions(e);

            if(e.type == "text"){ // setup text question

                let originalField = new InputField(this,e)
                let clonedField = new InputField(this,e);
                clonedField.setFieldHTMLAsSecondAnswer();
                originalField.setSecondAnswerField(clonedField);

                this.fields.push(originalField);
                this.fields.push(clonedField);
            }
            else if(e.type == "radio"){ // setup radio question

                let originalField = new RadioField(this,e)
                let clonedField = new RadioField(this,e);
                clonedField.setFieldHTMLAsSecondAnswer();
                originalField.setSecondAnswerField(clonedField);

                this.fields.push(originalField);
                this.fields.push(clonedField);

            }
            else if(e.type == "select") {

                let originalField = new SelectField(this,e)
                let clonedField = new SelectField(this,e);
                clonedField.setFieldHTMLAsSecondAnswer();
                originalField.setSecondAnswerField(clonedField);

                this.fields.push(originalField);
                this.fields.push(clonedField);
            }
            else if(e.type == "checkbox") {

                let originalField = new CheckboxField(this,e)
                let clonedField = new CheckboxField(this,e);
                clonedField.setFieldHTMLAsSecondAnswer();
                originalField.setSecondAnswerField(clonedField);

                this.fields.push(originalField);
                this.fields.push(clonedField);
            }
            if(e.type == "introduction") {
                this.fields.push(new InformationField(this,e));
            }else if(e.type == "radiotable"){ // setup radio question

                let originalField = new RadioTableField(this,e)
                let clonedField = new RadioTableField(this,e);
                clonedField.setFieldHTMLAsSecondAnswer();
                originalField.setSecondAnswerField(clonedField);

                this.fields.push(originalField);
                this.fields.push(clonedField);
            }

            //console.log(i + '----'+ JSON.stringify(e));
        }.bind(this));

    }
}

var surveyPlugin = pogs.createPlugin('surveyTaskPlugin',function(){

    console.info("Survey Plugin Loaded");

    var survey = new TwoAnwserSurvey(this);
    // get config attributes from task plugin
    survey.setupSurvey(this.getStringAttribute("surveyBluePrint"));
    this.subscribeTaskAttributeBroadcast(survey.broadcastReceived.bind(survey))

});