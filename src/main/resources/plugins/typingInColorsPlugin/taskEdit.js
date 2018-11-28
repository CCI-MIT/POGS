class TypingTaskEdit {

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

        $("#createColors").click(function () {
            this.createColorField("#000000");
        }.bind(this));
    }
    setupHtmlFromAttributeString(attribute){
        for(let k =0; k < attribute.length; k ++) {
            this.createColorField(attribute[k]);
        }
    }
    createColorField(colorValue) {
        let numberOfFields = $(".colorField").length;
        numberOfFields++;
        $("#colorFieldContainer").append(
            '<div class="form-group row colorField" >\n'
            + '      <label for="example-color-input" class="col-6 col-form-label">Color '+numberOfFields+'</label>\n'
            + '      <div class="col-4 input-group" id="colorField'+numberOfFields+'" >\n'
            + '        <input class="form-control" type="text" value="'+colorValue+'" >\n'
            + '        <span class="input-group-append">\n'
            + '         <span class="input-group-text colorpicker-input-addon"><i></i></span>\n'
            + '      </span>'
            + '      </div>\n'
            + '      <div class="col-2">\n'
            + '           <button type="button" class="btn btn-danger btn-sm remove-color" data-color-index="'+numberOfFields+'"> X </button>'
            + '      </div>\n'
            + '    </div>');

        $('#colorField'+ numberOfFields).colorpicker({format: 'auto'});
        this.setupDelete();
    }
    setupDelete(){
        $("remove-color").unbind().click(function (event) {
           $(event.target).parent().parent().remove();
        });
    }
    beforeSubmit(){
        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }
    setupAttributesFromHtml() {
        let bluePrint = [];

        let inputs = $(".colorField input");
        for(let k=0; k < inputs.length; k++){
            bluePrint.push($(inputs[k]).val());
        }
        return  {bluePrint: JSON.stringify(bluePrint)};

    }
}

pogsTaskConfigEditor.register(new TypingTaskEdit());
