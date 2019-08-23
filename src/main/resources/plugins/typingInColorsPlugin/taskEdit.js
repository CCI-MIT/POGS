class TypingTaskEdit {

    init(taskConfigId, currentAttributes){
        this.taskConfigId = taskConfigId;

        this.dictionaries={};
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

        $("#dictionaryId").change(this.setupSectionsFromDictionary.bind(this));
    }
    getDictJson(dictId, callBack){

        $.getJSON("/dictionaries/" + dictId + '/full',null, function(emp) {

            let dictionaries = emp.dictionaryEntryList;

            for(var i=0 ; i< dictionaries.length; i++ ) {
                this.dictionaries[dictionaries[i].id]= dictionaries[i];
            }
            callBack();
        }.bind(this));
    }
    setupSectionsFromDictionary(ref) {
        this.getDictJson($(ref.target).val(), function(){
            for(var i=0 ; i< this.dictionaries.length; i ++){
                this.createColorField("#000000",this.dictionaries[i].id);
            }
            this.refreshPreview();
        }.bind(this));
    }
    setupHtmlFromAttributeString(attributez){
        if($("#dictionaryId").val()!="") {
            this.getDictJson($("#dictionaryId").val(), function () {

                for (let k = 0; k < attributez.length; k++) {
                    this.createColorField(attributez[k].color, attributez[k].text);
                }
                this.refreshPreview();

            }.bind(this));
        }



    }
    refreshPreview(){
        let inputsTxt = $(".colorField .colorText");
        let inputsColors = $(".colorField .colorPick");

        let texts = [];
        for(let k=0; k < inputsTxt.length; k++) {
            let newColor = $(inputsColors[k]).val();

            texts.push({textContent:this.dictionaries[$(inputsTxt[k]).val()].entryValue,
                    backgroundColor:newColor, fontColor:
                    generateFontColorBasedOnBackgroundColor(newColor)});
        }
        $("#livePreviewContainer canvas").remove();
        this.canvasImage = new CanvasTextToImage(texts,"livePreviewContainer", 318);

    }
    createColorField(colorValue, textValue) {
        if(colorValue === undefined) colorValue= "#000000";
        let numberOfFields = $(".colorField").length;
        numberOfFields++;
        $("#colorFieldContainer").append(
              '    <div class="form-group row colorField"  class="col-12">\n'
            + '      <div class="col-6 row">'
            + '         <label for="example-color-input" class="col-3 col-form-label"># '+numberOfFields+'</label>\n'
            + '         <input type="hidden" class="form-control  col-9 colorText" rows="1" id="colorField'+numberOfFields+'Text" />'
            + '         <div  class="form-control  col-9 " rows="1" id="colorField'+numberOfFields+'TextLabel"></div>'
            + '      </div>\n'
            + '      <div class="col-5 row">'
            + '         <label for="example-color-input" class="col-4 col-form-label">Color '+numberOfFields+'</label>\n'
            + '         <div class="col-8 input-group" id="colorField'+numberOfFields+'" >\n'
            + '           <input class="form-control colorPick" type="text" value="'+colorValue+'" >\n'
            + '           <span class="input-group-append" style="height: 38px;">\n'
            + '            <span class="input-group-text colorpicker-input-addon"><i></i></span>\n'
            + '           </span>'
            + '          </div>'
            + '      </div>\n'
            //+ '      <div class="col-1">\n'
            //+ '           <button type="button" class="btn btn-danger btn-sm remove-color" data-color-index="'+numberOfFields+'"> X </button>'
            //+ '      </div>\n'
            + '    </div>');

        $('#colorField'+numberOfFields+'TextLabel').text(this.dictionaries[textValue].entryCategory);
        $('#colorField'+numberOfFields+'Text').val(this.dictionaries[textValue].id);

        $('#colorField'+ numberOfFields).colorpicker({format: 'auto'});

        $('#colorField'+ numberOfFields).on('change',this.refreshPreview.bind(this));
        $('#colorField'+numberOfFields+'Text').on('change',this.refreshPreview.bind(this));

        //this.setupDelete();
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
function replaceNewLinesForBrs(st){
    if(st === undefined) return "";
    return st.replace(/\n/g,"<br/>");
}
function generateFontColorBasedOnBackgroundColor(colorz) {
    let color = parseColor(colorz);
    let r = color[0];
    let g = color[1];
    let b = color[2];
    let yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
    return (yiq >= 128) ? "#000000" : "#FFFFFF";
}
function parseColor(input) {
    let  m = input.match(/^#([0-9a-f]{6})$/i)[1];
    if( m) {
        return [
            parseInt(m.substr(0,2),16),
            parseInt(m.substr(2,2),16),
            parseInt(m.substr(4,2),16)
        ];
    }
}
pogsTaskConfigEditor.register(new TypingTaskEdit());
