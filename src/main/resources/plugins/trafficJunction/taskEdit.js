/*
The class that will handle the editing.
init(taskConfigId, currentAttributes) will be called once the editor is done loading all dependencies.
* */
class QualtrixPluginTaskEdit {

    init(taskConfigId, currentAttributes){

        this.taskConfigId = taskConfigId;
        var gridBluePrint = null;
        var answerSheet = null;
        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridBluePrint") {
                gridBluePrint = (currentAttributes[i]);
            }
            if(currentAttributes[i].attributeName == "answerSheet") {
                answerSheet = (currentAttributes[i]);
            }
        }

        if(gridBluePrint!=null &&answerSheet!=null ){

            this.setupHtmlFromAttributeString(JSON.parse(gridBluePrint.stringValue),JSON.parse(answerSheet.stringValue));
            createOrUpdateAttribute("gridBluePrint", gridBluePrint.stringValue,null,null,this.taskConfigId,0, gridBluePrint.id);
            createOrUpdateAttribute("answerSheet",answerSheet.stringValue,null,null,this.taskConfigId,1, answerSheet.id);
        }


        $("#createSudoku").click(function () {
            var bluePrintAnswerSheet = generateNewSodukuGame($("#sudokuDifculty").val());
            this.setupHtmlFromAttributeString(bluePrintAnswerSheet.bluePrint,bluePrintAnswerSheet.answerSheet);
        }.bind(this));

    }
    setupHtmlFromAttributeString(bluePrint, answerSheet){

        $("#qualtrixLink").val(bluePrint.qualtrixLink);

    }
    setupAttributesFromHtml(){
        let qualtrixLinkVar = $("#qualtrixLink").val();

        return  {bluePrint: {qualtrixLink: qualtrixLinkVar}, answerSheet: {}};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all cells for non int numbers

        var attr = this.setupAttributesFromHtml();



        createOrUpdateAttribute("gridBluePrint",JSON.stringify(attr.bluePrint),null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("answerSheet",JSON.stringify(attr.answerSheet),null,null,this.taskConfigId,1, "");
    }


}
pogsTaskConfigEditor.register(new QualtrixPluginTaskEdit());

