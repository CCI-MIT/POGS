/*
The class that will handle the editing.
init(taskConfigId, currentAttributes) will be called once the editor is done loading all dependencies.
* */
class TicTacToeTaskEdit {

    init(taskConfigId, currentAttributes){

        this.taskConfigId = taskConfigId;
        var gridSize = null;

        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridSize") {
                gridSize = currentAttributes[i];
            }
        }

        if(gridSize!=null){
            this.setupHtmlFromAttributeString(gridSize);
            createOrUpdateAttribute("gridSize",gridSize.stringValue,null,null,this.taskConfigId,0, gridSize.id);
        }
    }
    setupHtmlFromAttributeString(gridSize){
        $("#tictactoegridsize").val(gridSize);
    }
    setupAttributesFromHtml(){
        let gridsize = $("#tictactoegridsize").val();
        console.log("gridsize: "+ gridsize)
        return gridsize;
    }
    beforeSubmit(){
        let attribute = this.setupAttributesFromHtml();
        createOrUpdateAttribute("gridSize",attribute,null,null,this.taskConfigId,0, "");

    }


}
pogsTaskConfigEditor.register(new TicTacToeTaskEdit());

