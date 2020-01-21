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
        setupHTMLFieldEditors()
    }

    setupHtmlFromAttributeString(attribute){
        console.log(attribute.taskText);
        $("#taskText").val(attribute.taskText);
        $("#totalSum").val(attribute.totalSum);
        $("#executionMode").val(attribute.executionMode);
        $("#shouldReuseMembers").attr("checked",attribute.shouldReuseMembers);
        $("#shouldRestrictNumbers").attr("checked",attribute.shouldRestrictNumbers);
        $("#shouldNotAllowRepetition").attr("checked",attribute.shouldNotAllowRepetition);
        $("#allowedNumbers").val(attribute.allowedNumbers);

    }

    beforeSubmit() {
        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }
    setupAttributesFromHtml() {
        let bluePrint = {};
        bluePrint.taskText = $("#taskText").val();
        bluePrint.totalSum = $("#totalSum").val();
        bluePrint.executionMode = $("#executionMode").val();
        bluePrint.shouldReuseMembers = $("#shouldReuseMembers").is(":checked");

        bluePrint.shouldRestrictNumbers = $("#shouldRestrictNumbers").is(":checked");
        bluePrint.shouldNotAllowRepetition = $("#shouldNotAllowRepetition").is(":checked");
        bluePrint.allowedNumbers = $("#allowedNumbers").val();


        return  {bluePrint: JSON.stringify(bluePrint)};
    }
}

pogsTaskConfigEditor.register(new EquationTypingTaskEdit());
