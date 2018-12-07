class MemoryGridTaskEdit {

    init(taskConfigId, currentAttributes){

        this.taskConfigId = taskConfigId;
        let gridBluePrint = null;
        let answerSheet = null;
        for(let i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridBluePrint") {
                gridBluePrint = currentAttributes[i];
            }
            if(currentAttributes[i].attributeName == "answerSheet") {
                answerSheet = currentAttributes[i];
            }
        }

        if(gridBluePrint!=null && answerSheet!=null ){

            this.setupHtmlFromAttributeString($.parseJSON(gridBluePrint.stringValue),$.parseJSON(answerSheet.stringValue));//.replace(/,/g,'')

            createOrUpdateAttribute("gridBluePrint",gridBluePrint.stringValue,null,null,this.taskConfigId,0, gridBluePrint.id);
            createOrUpdateAttribute("answerSheet",answerSheet.stringValue,null,null,this.taskConfigId,1, answerSheet.id);
        }


        $("#createGrid").click(function () {

            let arr = [];
            for(let i=0;i<$("#rowsSize").val();i++){
                for(let j = 0 ; j< $("#colsSize").val(); j++){
                    arr.push('');
                }
            }

            let bluePrintAnswerSheet = {bluePrint: {colsSize:$("#colsSize").val(),rowsSize: $("#rowsSize").val()},
            answerSheet:arr};
            this.setupHtmlFromAttributeString(bluePrintAnswerSheet.bluePrint,bluePrintAnswerSheet.answerSheet);
        }.bind(this));

    }
    setupHtmlFromAttributeString(bluePrint, answerSheet){

        $("#gridTable").empty();

            let headerRow = $('<tr/>');
            for (let j = 0; j < bluePrint.colsSize; j++) {
                let td = $('<th/>');
                let color = "#000000";
                if(bluePrint.columnColors) {
                    color = bluePrint.columnColors[j];
                }
                td.append(
                    '      <div class="col-12 input-group colorField" id="colorField' + j + '" >\n'
                    + '        <input class="form-control" type="text" value="'
                    + color + '" >\n'
                    + '        <span class="input-group-append">\n'
                    + '         <span class="input-group-text colorpicker-input-addon"><i></i></span>\n'
                    + '      </span>'
                    + '      </div>\n');

                headerRow.append(td);
            }
            $("#gridTable").append(headerRow);


        for (let j = 0; j < bluePrint.colsSize; j++) {
            $('#colorField' + j).colorpicker({format: 'auto'});
        }

        $("#taskText").val(bluePrint.taskText);

        let total = 0;
        for(let i=0;i<bluePrint.rowsSize;i++){
            let tableRow = $('<tr/>');

            for(let j = 0 ; j< bluePrint.colsSize; j++) {
                let td = $('<td/>');
                td.append(
                    $('<input/>', {
                        'class' : 'wordsInput',
                        'cell-reference-index': total,
                        value: answerSheet[total]
                    })
                );
                total++;
                tableRow.append(td);
            }
            $("#gridTable").append(tableRow);
        }
        $("#gridTable").data('colsSize',bluePrint.colsSize);
        $("#gridTable").data('rowsSize',bluePrint.rowsSize);
    }
    setupAttributesFromHtml(){
        let bluePrint = {}, answerSheet = [];

        bluePrint.colsSize = parseInt($("#gridTable").data('colsSize'));
        bluePrint.rowsSize = parseInt($("#gridTable").data('rowsSize'));

        bluePrint.columnColors = [];

        for(let i = 0; i < (bluePrint.colsSize);i ++){
            bluePrint.columnColors.push($("#colorField"+ i + " input").val());
        }


        bluePrint.taskText = $("#taskText").val();

        for (let i = 0; i < (bluePrint.rowsSize * bluePrint.colsSize) ; i ++ ){
            let currCel = $("#gridTable .wordsInput")[i];
            let cellData = $(currCel).val();
            answerSheet.push(cellData);
        }

        return  {bluePrint: JSON.stringify(bluePrint), answerSheet: JSON.stringify(answerSheet)};
    }
    beforeSubmit(){

        let attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("answerSheet",attr.answerSheet,null,null,this.taskConfigId,1, "");

    }


}
pogsTaskConfigEditor.register(new MemoryGridTaskEdit());
