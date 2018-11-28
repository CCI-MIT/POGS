class EquationTypingTaskEdit {

    init(taskConfigId, currentAttributes){
        this.taskConfigId = taskConfigId;
        let gridBluePrint = null;
        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridBluePrint") {
                gridBluePrint = currentAttributes[i];
            }
        }

        if(gridBluePrint!=null){
            this.setupHtmlFromAttributeString($.parseJSON(gridBluePrint.stringValue));
            createOrUpdateAttribute("gridBluePrint",$.parseJSON(gridBluePrint.stringValue),null,null,this.taskConfigId,0, gridBluePrint.id);
        }

    }
    setupHtmlFromAttributeString(attribute){
        $("#taskText").val(attribute.taskText);
        $("#totalSum").val(attribute.totalSum);
        $("#shouldReuseMembers").attr("checked",attribute.shouldReuseMembers);
    }

    beforeSubmit() {
        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }
    setupAttributesFromHtml() {
        let bluePrint = {};
        bluePrint.taskText = $("#taskText").val();
        bluePrint.totalSum = $("#totalSum").val();
        bluePrint.shouldReuseMembers = $("#shouldReuseMembers").is(":checked");
        return  {bluePrint: JSON.stringify(bluePrint)};
    }
}

pogsTaskConfigEditor.register(new EquationTypingTaskEdit());
