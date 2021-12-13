const X_CLASS = "fa-times";
const O_CLASS = "fa-circle";
const X_COLOR = "tic-tac-toe-cell-x";
const O_COLOR = "tic-tac-toe-cell-o";
class TicTacToeGame {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;

    }
    discoverSubjectTeam(){
        console.log( " this.pogsPlugin.subjectId = " + this.pogsPlugin.getSubjectId());

        for(let i =0; i < this.teamX.length;i++){
            console.log(" this.teamX" + this.teamX[i]);
            if(this.pogsPlugin.getSubjectId() == this.teamX[i]){
                return X_CLASS;
            }
        }
        return O_CLASS;
    }
    setupGrid(gridSize, teamX, teamO, shouldTeamXStart) {
        //If we had a configurable grid size, we would create the html on the javascript side.
        this.clearGrid();

        console.log("TEAM X" + teamX);
        console.log("TEAM O" + teamO);
        this.teamX = teamX;
        this.teamO = teamO;
        this.currentTeamsClass = (shouldTeamXStart)?(X_CLASS):(O_CLASS);
        this.myTeam = this.discoverSubjectTeam();

        $(".tic-tac-toe-cell").on("click", this.handleOnClick.bind(this));
        //$(".tic-tac-toe-cell").on("click", this.handleOnClick.bind(this));
        this.checkAndUpdateTurn();
        $("#game_over").hide();
        $("#game_won").hide();
        $("#game_on").show();
    }
    checkAndUpdateTurn(){


        $("#team_turn").removeClass(X_CLASS);
        $("#team_turn").removeClass(O_CLASS);
        $("#team_turn").addClass(this.currentTeamsClass);

    }
    clearGrid(){
        $(".tic-tac-toe-cell span").removeClass(X_CLASS);
        $(".tic-tac-toe-cell span").removeClass(O_CLASS);
        $(".tic-tac-toe-cell").removeClass(X_COLOR);
        $(".tic-tac-toe-cell").removeClass(O_COLOR);
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
        //if(message.sender != this.pogsPlugin.subjectId) {
            if (attrName == "focusInCell") {

                $("#sudoku_cell" + message.content.attributeIntegerValue)
                    .addClass(message.sender+"_color ") //get subject backgroud COLOR sudoku__gametable-cell--error
                    .delay(1000).queue(function (next) {
                    $(this).removeClass(message.sender+"_color");
                    next();
                });
            } else {
                if (attrName.indexOf("ticTacToeAnswer_") != -1) {
                    var cell = attrName.replace("ticTacToeAnswer_", "");
                    $("#tictac_" + cell + " span").addClass(message.content.attributeStringValue);
                    $("#tictac_" + cell).addClass((message.content.attributeStringValue==X_CLASS)?(X_COLOR):(O_COLOR));

                    if(!this.isGameOver()){
                        this.changeCurrentTeamTurn();
                    }
                }
            }
        //}

        //All attributes sync(send all set attributes)
    }
    changeCurrentTeamTurn(){
        this.currentTeamsClass = (this.currentTeamsClass==O_CLASS)?(X_CLASS):(O_CLASS);
        this.checkAndUpdateTurn()
    }
    isGameOver(){
        let gameOver = false;

        let totalAnswers = $(".tic-tac-toe ." + X_CLASS).length + $(".tic-tac-toe ." + O_CLASS).length ;

        if(totalAnswers!=9) return gameOver;

       // [x][x][x]
       // [][][]
       // [][][]

        // [x][][]
        // [x][][]
        // [x][][]


        // [x][][]
        // [][x][]
        // [][][x]
        let isCellX = [];

        for(let i=0;i<9;i++){
            isCellX.push($("#tictac_"+i + " span").hasClass(X_CLASS));
        }
        //horizontal scenarios
        let winnerClass = null;

        if(isCellX[0]&&isCellX[1]&&isCellX[2] ||
           isCellX[3]&&isCellX[4]&&isCellX[5] ||
            isCellX[6]&&isCellX[7]&&isCellX[8]){
            winnerClass = X_CLASS;
        }

        if(isCellX[0]&&isCellX[3]&&isCellX[6] ||
           isCellX[1]&&isCellX[4]&&isCellX[7] ||
           isCellX[2]&&isCellX[5]&&isCellX[8]){
            winnerClass = X_CLASS;
        }
        if(isCellX[0]&&isCellX[4]&&isCellX[8] ||
           isCellX[2]&&isCellX[4]&&isCellX[6] ){
            winnerClass = X_CLASS;
        }


        if(!isCellX[0]&&!isCellX[1]&&!isCellX[2] ||
           !isCellX[3]&&!isCellX[4]&&!isCellX[5] ||
           !isCellX[6]&&!isCellX[7]&&!isCellX[8]){
            winnerClass = O_CLASS;
        }

        if(!isCellX[0]&&!isCellX[3]&&!isCellX[6] ||
           !isCellX[1]&&!isCellX[4]&&!isCellX[7] ||
           !isCellX[2]&&!isCellX[5]&&!isCellX[8]){
            winnerClass = O_CLASS;
        }
        if(!isCellX[0]&&!isCellX[4]&&!isCellX[8] ||
           !isCellX[2]&&!isCellX[4]&&!isCellX[6] ){
            winnerClass = O_CLASS;
        }

        if(gameOver){
            if(winnerClass!=null) {
                $("#winner").addClass(winnerClass);
                $("#game_won").show();
                $("#game_over").hide();
                $("#game_on").hide();
            } else {
                $("#game_won").hide();
                $("#game_over").show();
                $("#game_on").hide();
            }
        }
        return gameOver;
    }
    isCellAlreadyFilled(reference){
        return $("#tictac_" + reference + " span").hasClass(O_CLASS) || $("#tictac_" + reference + " span").hasClass(X_CLASS);
    }
    handleOnClick(event){

        var cellIndex = parseInt($(event.target).attr( "id").replace("tictac_",""));

        console.log("> " + this.currentTeamsClass + " - " + this.myTeam)
        console.log(">> " + this.isCellAlreadyFilled(cellIndex) + " - " + cellIndex);
        if(this.currentTeamsClass != this.myTeam || this.isCellAlreadyFilled(cellIndex)){
            event.stopPropagation();
            return;
        }



        if(!isNaN(cellIndex)) {

            this.pogsPlugin.saveCompletedTaskAttributeWithoutOverride(
                'ticTacToeAnswer_' + cellIndex,
                this.myTeam, 0.0,
            0, true, null, "Clicked in cell : " +cellIndex);

        }

    }
    handleHover(event){
        var cellIndex = parseInt($(event.target).attr( "id").replace("tictac_",""));

        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusInCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }
    }
}

var ticTacToePlugin = pogs.createPlugin('ticTacToePlugin',function(){


    var ticTacToeGame = new TicTacToeGame(this);
    // get config attributes from task plugin

    ticTacToeGame.setupGrid(this.getStringAttribute("gridSize"),
                            JSON.parse(this.getCompletedTaskStringAttribute("teamX")),
                            JSON.parse(this.getCompletedTaskStringAttribute("teamO")),
                            this.getCompletedTaskStringAttribute("shouldXStart")=="true");

    this.subscribeTaskAttributeBroadcast(ticTacToeGame.broadcastReceived.bind(ticTacToeGame))

});