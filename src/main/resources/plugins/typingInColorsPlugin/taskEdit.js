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
            this.createColorField("#000000","");
        }.bind(this));
    }
    setupHtmlFromAttributeString(attributez){
        for(let k =0; k < attributez.length; k ++) {
            this.createColorField(attributez[k].color, attributez[k].text);
        }
    }
    createColorField(colorValue, textValue) {
        if(colorValue === undefined) colorValue= "#000000";
        let numberOfFields = $(".colorField").length;
        numberOfFields++;
        $("#colorFieldContainer").append(
              '    <div class="form-group row colorField"  class="col-12">\n'
            + '      <div class="col-6 row">'
            + '         <label for="example-color-input" class="col-2 col-form-label">Text '+numberOfFields+'</label>\n'
            + '         <textarea class="form-control  col-10 colorText" rows="1" id="colorField'+numberOfFields+'Text"></textarea>'
            + '      </div>\n'
            + '      <div class="col-4 row">'
            + '         <label for="example-color-input" class="col-3 col-form-label">Color '+numberOfFields+'</label>\n'
            + '         <div class="col-9 input-group" id="colorField'+numberOfFields+'" >\n'
            + '           <input class="form-control colorPick" type="text" value="'+colorValue+'" >\n'
            + '           <span class="input-group-append" style="height: 38px;">\n'
            + '            <span class="input-group-text colorpicker-input-addon"><i></i></span>\n'
            + '           </span>'
            + '          </div>'
            + '      </div>\n'
            + '      <div class="col-2">\n'
            + '           <button type="button" class="btn btn-danger btn-sm remove-color" data-color-index="'+numberOfFields+'"> X </button>'
            + '      </div>\n'
            + '    </div>');

        $('#colorField'+numberOfFields+'Text').val(textValue);

        $('#colorField'+ numberOfFields).colorpicker({format: 'auto'});

        this.setupDelete();
    }
    setupDelete(){
        $(".remove-color").unbind().click(function (event) {
           $(event.target).parent().parent().remove();
        });
    }
    beforeSubmit(){
        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }
    setupAttributesFromHtml() {
        let bluePrint = [];

        let inputsTxt = $(".colorField .colorText");
        let inputsColors = $(".colorField .colorPick");
        for(let k=0; k < inputsTxt.length; k++){
            bluePrint.push({
                    text: $(inputsTxt[k]).val(),
                    color: $(inputsColors[k]).val()
            });
        }
        return  {bluePrint: JSON.stringify(bluePrint)};

    }
}

pogsTaskConfigEditor.register(new TypingTaskEdit());
