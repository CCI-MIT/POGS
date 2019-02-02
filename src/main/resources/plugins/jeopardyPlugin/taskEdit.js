class JeopardyTaskEdit {

    init(taskConfigId, currentAttributes){
        this.taskConfigId = taskConfigId;
        let jeopardyBluePrint = null;

        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "jeopardyBluePrint") {
                jeopardyBluePrint = currentAttributes[i];
            }
        }

        if(jeopardyBluePrint!=null){
            this.setupHtmlFromAttributeString($.parseJSON(jeopardyBluePrint.stringValue));
            createOrUpdateAttribute("jeopardyBluePrint",$.parseJSON(jeopardyBluePrint.stringValue),null,null,this.taskConfigId,0, jeopardyBluePrint.id);
        }
        setupHTMLFieldEditors()
    }

    setupHtmlFromAttributeString(attribute){
        $("#prob1").val(attribute.prob1);
        $("#prob2").val(attribute.prob2);
        $("#prob3").val(attribute.prob3);
        $("#prob4").val(attribute.prob4);
    }

    beforeSubmit() {
        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("jeopardyBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }
    setupAttributesFromHtml() {
        let bluePrint = {};
        bluePrint.prob1 = $("#prob1").val();
        bluePrint.prob2 = $("#prob2").val();
        bluePrint.prob3 = $("#prob3").val();
        bluePrint.prob4 = $("#prob4").val();
        
        return  {bluePrint: JSON.stringify(bluePrint)};
    }
}

pogsTaskConfigEditor.register(new JeopardyTaskEdit());
