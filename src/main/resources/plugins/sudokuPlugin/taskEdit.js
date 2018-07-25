class SudokuTaskEdit {

    init(taskConfigId, currentAttributes){

        this.taskConfigId = taskConfigId;
        var gridBluePrint = null;
        var answerSheet = null;
        for(var i = 0 ; i< currentAttributes.length; i ++){
            if(currentAttributes[i].attributeName == "gridBluePrint") {
                gridBluePrint = currentAttributes[i];
            }
            if(currentAttributes[i].attributeName == "answerSheet") {
                answerSheet = currentAttributes[i];
            }
        }

        if(gridBluePrint!=null &&answerSheet!=null ){

            this.setupHtmlFromAttributeString(gridBluePrint.stringValue.replace(/,/g,''),answerSheet.stringValue.replace(/,/g,''));
            createOrUpdateAttribute("gridBluePrint",gridBluePrint.stringValue,null,null,this.taskConfigId,0, gridBluePrint.id);
            createOrUpdateAttribute("answerSheet",answerSheet.stringValue,null,null,this.taskConfigId,1, answerSheet.id);
        }


        $("#createSudoku").click(function () {
            var bluePrintAnswerSheet = generateNewSodukuGame($("#sudokuDifculty").val());
            this.setupHtmlFromAttributeString(bluePrintAnswerSheet.bluePrint,bluePrintAnswerSheet.answerSheet);
        }.bind(this));

    }
    setupHtmlFromAttributeString(bluePrint, answerSheet){
        $(".sudoku__gametable-cell").each(function() {
            var cellIndex = parseInt($(this).data( "cell-reference-index"));
            if(cellIndex <= bluePrint.length) {
                if(bluePrint[cellIndex] == '0' ){
                    $(this).text("-" + answerSheet[cellIndex].toString());
                } else {
                    $(this).text(bluePrint[cellIndex].toString());
                }
            }
        });
    }
    setupAttributesFromHtml(){
        var bluePrint = [], answerSheet = [];

        for (var i = 0; i < $(".sudoku__gametable-cell").length ; i ++ ){
            var currCel = $($(".sudoku__gametable-cell")[i]);
            var cellData = $(currCel).text();
            if(parseInt(cellData) < 0 ){
                answerSheet.push(parseInt(cellData)*-1);//should store the normal number
                bluePrint.push(0);//sets input to zero
            } else {
                answerSheet.push(cellData);
                bluePrint.push(cellData)
            }

        }
        var bluePrintString = "";
        var sudokuSolveString = "";
        for( var i = 0; i < answerSheet.length; i ++ ){
            bluePrintString +=  bluePrint[i];
            sudokuSolveString += answerSheet[i];
            if(i + 1 != answerSheet.length) {
                bluePrintString += ",";
                sudokuSolveString += ",";
            }

        }
        return  {bluePrint: bluePrintString, answerSheet: sudokuSolveString};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all cells for non int numbers

        var attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("gridBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("answerSheet",attr.answerSheet,null,null,this.taskConfigId,1, "");
    }


}
pogsTaskConfigEditor.register(new SudokuTaskEdit());

function generateNewSodukuGame(level) {
    var sudokuString = sudoku.generate(level, true);
    var sudokuSolveString = sudoku.solve(sudokuString);
    return {bluePrint: sudokuString.replace(/\./g,'0'), answerSheet: sudokuSolveString};
}
