class Survey {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    setupHTML(surveyBluePrint){


    }
    broadcastReceived(message){
        var attrName = message.content.attributeName;
        if(message.sender != this.pogsPlugin.subjectId) {
            if (attrName == "focusInCell") {

            }

        }
        //All attributes sync(send all set attributes)
    }

    handleOnBlur(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {

            var valueTyped = $(event.target).text().replace(/\r?\n?/g, '').trim();
            console.log("Typed value : " + valueTyped);
            if(valueTyped != null && ! isNaN(valueTyped)) {
                this.pogsPlugin.saveCompletedTaskAttribute('sudokuAnswer_' + cellIndex,
                    valueTyped, 0.0,
                    0, true);
            }
        }
    }
}

var surveyPlugin = pogs.createPlugin('surveyTaskPlugin',function(){

    console.log("Survey Plugin Loaded");

    var survey = new Survey(this);
    // get config attributes from task plugin
    survey.setupHTML(this.getStringAttribute("surveyBluePrint"));
    this.subscribeTaskAttributeBroadcast(survey.broadcastReceived.bind(survey))

});