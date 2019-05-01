/*
The class that will handle the editing.
init(taskConfigId, currentAttributes) will be called once the editor is done loading all dependencies.
* */
class MinimumEffortTaskEdit {

    init(taskConfigId, currentAttributes){

        this.taskConfigId = taskConfigId;
        var gridBluePrint = null;
        var answerSheet = null;
        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridBluePrint") {
                gridBluePrint = currentAttributes[i];
            }

        }

        if(gridBluePrint!=null){

            this.setupHtmlFromAttributeString($.parseJSON(gridBluePrint.stringValue));
            createOrUpdateAttribute("gridBluePrint",gridBluePrint.stringValue,null,null,this.taskConfigId,0, gridBluePrint.id);

        }


        $("#createGrid").click(function () {
            generateNewPaymentStructure($("#availableNumbers").val());

        }.bind(this));
        setupHTMLFieldEditors();
    }
    setupHtmlFromAttributeString(bluePrint){

        $("#availableNumbers").val(bluePrint.availableNumbers);
        $("#taskText").val(bluePrint.taskText);
        $("#numberOfRounds").val(bluePrint.numberOfRounds);

        generateNewPaymentStructure($("#availableNumbers").val());

        for (var i = 0; i < $("#paymentStructure input").length ; i ++ ){
            var currCel = $($("#paymentStructure input")[i]);
            var col = $(currCel).data("col");
            var row = $(currCel).data("row");
            $(currCel).val(bluePrint.paymentStructure[row][col]);
        }
    }
    setupAttributesFromHtml(){
        var bluePrint = {};
        bluePrint.paymentStructure = [];
        bluePrint.availableNumbers = $("#availableNumbers").val();

        for(var k=0;k < bluePrint.availableNumbers; k ++){
            bluePrint.paymentStructure[k] = [];
            for(var j=0;j < bluePrint.availableNumbers; j++){
                bluePrint.paymentStructure[k][j] = 0;
            }
        }
        for (var i = 0; i < $("#paymentStructure input").length ; i ++ ){
            var currCel = $($("#paymentStructure input")[i]);
            var cellData = $(currCel).val();
            var col = $(currCel).data("col");
            var row = $(currCel).data("row");
            bluePrint.paymentStructure[row][col] = cellData;

        }
        bluePrint.availableNumbers = $("#availableNumbers").val();
        bluePrint.taskText = $("#taskText").val();
        bluePrint.numberOfRounds = $("#numberOfRounds").val();

        return  {bluePrint: JSON.stringify(bluePrint)};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all cells for non int numbers

        var attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");

    }


}
pogsTaskConfigEditor.register(new MinimumEffortTaskEdit());


function generateNewPaymentStructure(numbers) {
let k = 0;

    $("#paymentStructure").append('<tr><th>Own number</th><th colspan="'+numbers+'">Smallest number in group</th></tr>');

    for(let i = 0 ; i < numbers; i++) {
        if(i == 0){
            let line = $("<tr>");
            let column = $("<th>");
            column.text(" - ");
            line.append(column);
            for(let j = 0 ; j < numbers; j++) {
                let column = $("<th>");
                column.text(numbers - j);
                line.append(column);
            }
            $("#paymentStructure").append(line);

        }
        let line = $("<tr>");
        for(let j = 0 ; j < numbers; j++) {
            if(j == 0){
                let column = $("<th>");
                column.text(numbers - i)
                line.append(column);
            }
            let column = $("<td>");
            if(j>=i) {
                column.append("<input type='text' data-col='"+j+"' data-row='"+i+"' data-ref='" + k + "'>");
                k++;
            }
            line.append(column)
        }
        $("#paymentStructure").append(line);
    }

}