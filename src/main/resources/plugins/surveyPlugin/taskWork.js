
var surveyPlugin = pogs.createPlugin('surveyTaskPlugin',function(){

    console.info("Survey Plugin Loaded");

    var survey = new Survey(this);
    // get config attributes from task plugin
    survey.setupSurvey(this.getStringAttribute("surveyBluePrint"),this.getCompletedTaskStringAttribute("customVariables"));
    this.subscribeTaskAttributeBroadcast(survey.broadcastReceived.bind(survey))

});