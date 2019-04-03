class SudokuGame {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    setupGrid(gridBluePrint){

        var gridBluePrint = gridBluePrint;
        var gridBluePrintCells = gridBluePrint.split(",");
        var self = this;
        $(".sudoku__gametable-cell").each(function() {
            var cellIndex = parseInt($(this).data( "cell-reference-index"));
            if(cellIndex <= gridBluePrintCells.length) {
                if(gridBluePrintCells[cellIndex] == 0 ){
                    //set as editable
                    $(this).addClass("sudoku__gametable-cell--editable");
                    $(this).attr("contenteditable", "true");
                    //register edit listeners
                    //event.target
                    $(this).on('focusin', self.handleOnClick.bind(self));

                    $(this).on('focusout', self.handleOnBlur.bind(self));
                    $(this).on('keydown', self.handleKeydown.bind(self));
                    $(this).text('');
                } else {
                    $(this).text(gridBluePrintCells[cellIndex]);
                    $(this).attr("contenteditable", "false");
                }
            }
        });

    }
    handleKeydown(theEvent){
        console.log(theEvent);
        console.log(theEvent.target);

        let key = theEvent.keyCode || theEvent.which;
        if(key == 9||key == 8 || key == 46){//backspace delete
            return true;
        }
        console.log("THIS IS THE TARGET LENGTH: " +$(theEvent.target).text().length);
        if($(theEvent.target).text().length>=1) {
            theEvent.returnValue = false;
            theEvent.preventDefault()
            return false;
        }

        key = String.fromCharCode(key);
        let regex = /[0-9]|\./;
        if( !regex.test(key) ) {
            theEvent.returnValue = false;
            theEvent.preventDefault()
            return false;
        }
        return true;

    }
    broadcastReceived(message){
        var attrName = message.content.attributeName;
        if(message.sender != this.pogsPlugin.subjectId) {
            if (attrName == "focusInCell") {

                $("#sudoku_cell" + message.content.attributeIntegerValue)
                    .addClass(message.sender+"_color ") //get subject backgroud COLOR sudoku__gametable-cell--error
                    .delay(1000).queue(function (next) {
                    $(this).removeClass(message.sender+"_color");
                    next();
                });
            } else {
                if (attrName.indexOf("sudokuAnswer_") != -1) {
                    var cell = attrName.replace("sudokuAnswer_", "");
                    $("#sudoku_cell" + cell).text(message.content.attributeStringValue);
                }
            }
        }
        //All attributes sync(send all set attributes)
    }
    handleOnClick(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusInCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }

    }
    handleOnBlur(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {

            var valueTyped = $(event.target).text().replace(/\r?\n?/g, '').trim();
            console.log("Typed value : " + valueTyped);
            if(valueTyped != null && ! isNaN(valueTyped)) {
                this.pogsPlugin.saveCompletedTaskAttribute('sudokuAnswer_' + cellIndex,
                    valueTyped, 0.0,
                    0, true);
            }
        }
    }
}

var sudokuPlugin = pogs.createPlugin('sudokuTaskPlugin',function(){


    var sudokuGame = new SudokuGame(this);
    // get config attributes from task plugin
    sudokuGame.setupGrid(this.getStringAttribute("gridBluePrint"));
    this.subscribeTaskAttributeBroadcast(sudokuGame.broadcastReceived.bind(sudokuGame))

});