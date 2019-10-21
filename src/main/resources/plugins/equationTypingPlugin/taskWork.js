const NUMBER_OF_INITIAL_FIELDS = 6;
const TURN_TOTAL_TIME = 16*1000;

class EquationTypingTaskPlugin{
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.gridOrder = null;
        this.orderIndex = -1;
        this.totalFieldIndex = -1;
        this.teamMates = this.pogsPlugin.getTeammates();
        this.isTaskSolo = this.pogsPlugin.isSoloTask();
        this.latestCountdownIndex = null;
    }
    setup(bluePrint,gridOrder) {

        if(bluePrint.taskText) {
            $("#taskText").html(bluePrint.taskText);
        }
        this.executionMode = bluePrint.executionMode;
        this.gridOrder = gridOrder;
        if(this.executionMode == 2) {
            this.updateNextOrderIndex();
            this.createNextFieldAndWhoShouldBeAbleToEdit();
            this.sendTimerStartSetup();
            //schedule ORDER to be sent.
        } else{
            for(let i =0; i < NUMBER_OF_INITIAL_FIELDS; i ++) {
                this.createNextFieldAndWhoShouldBeAbleToEdit();
            }
        }



    }
    updateNextOrderIndex(timerIndex){
        if(timerIndex){
            this.orderIndex = timerIndex;
        }

        this.orderIndex++;

        //auto loop in the turn array.
        if(this.executionMode == 2) {
            if (this.orderIndex == this.teamMates.length) {
                this.orderIndex = 0;
            }
        }
    }
    setupVisibilityForIndependentVersion(){
        $('#doneEquation_' + this.totalFieldIndex).show();
    }
    createNextFieldAndWhoShouldBeAbleToEdit(){

        this.totalFieldIndex++;


        let div = $('<div/>', {
            'id': 'cell_' + this.totalFieldIndex
        });

        let inp = $('<input/>', {
                "class": "doneInp",
                'id': 'inputEquation_' + this.totalFieldIndex,
                'style':'margin-right: 5px;',
                'data-cell-reference-index': this.totalFieldIndex
        });
        if(this.executionMode == 2){
            inp.attr("disabled", true);
        }


        let btn  = $('<input/>', {
            "class": "btn btn-info doneBtn",
            'id': 'doneEquation_' + this.totalFieldIndex,
            "type": "button",
            "style": "display:none",
            'data-cell-reference-index': this.totalFieldIndex,
            "value": "Submit"
        });

        div.append(inp);
        div.append(btn);

        let workOn = $('<div/>', {
            'class': 'workingOn',
            'style': 'margin-bottom: 5px;height: 20px;'
        });


        if(this.executionMode == 2) {

            let btnTK = $('<input/>', {
                "class": "btn btn-danger takeTurnBtn",
                'id': 'takeTurnEquation_' + this.totalFieldIndex,
                "type": "button",
                "style": "margin-left:10px;",
                'data-cell-reference-index': this.totalFieldIndex,
                "value": ""
            });

            let btnTK2 = $('<button/>', {
                "class": "btn btn-sm btn-outline-danger",
                'id': 'countdownTurn_' + this.totalFieldIndex,
                "type": "button",
                "disabled" : "true",
                "style": "margin-left:10px;",
                'data-cell-reference-index': this.totalFieldIndex
            });

            div.append(btnTK);
            div.append(btnTK2);
        }
        let btnEdit  = $('<input/>', {
            "class": "btn btn-danger btnEdit",
            'id': 'editEquation_' + this.totalFieldIndex,
            "type": "button",
            "style": "display: none;margin-left: 5px;",
            'data-cell-reference-index': this.totalFieldIndex,
            "value": "Edit"
        });

        div.append(btnEdit);


        div.append(workOn);

        $("#textInputPad").append(div);


        this.setupHooks();

        if(this.executionMode == 1){
            $('#doneEquation_' + this.totalFieldIndex).show();
        }

    }
    setupWorkOn(){

        let workOn = $('#cell_' + this.totalFieldIndex + ' .workingOn');
        workOn.empty();

        if(!this.isTaskSolo) {
            let sub = this.teamMates[this.orderIndex];



            if (sub.externalId == this.pogsPlugin.getSubjectId()) {

                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge badgeUser ' + sub.externalId + '_color username">' + sub.displayName
                  + '(you)</span>').appendTo(workOn);
                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            } else {
                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge badgeUser ' + sub.externalId + '_color username">'
                  + sub.displayName
                  + '</span>').appendTo(workOn);

                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            }
        }

    }
    userTookTurn() {
        this.countDown.cancelCountDown();
        $('#takeTurnEquation_' + this.totalFieldIndex).hide();
        $('#countdownTurn_' + this.totalFieldIndex).hide();

        let sub = this.teamMates[this.orderIndex];
        if (sub.externalId == this.pogsPlugin.getSubjectId()) {
            $('#doneEquation_' + this.totalFieldIndex).show();
            $('#doneEquation_' + this.totalFieldIndex).removeAttr("disabled");
            $('#doneEquation_' + this.totalFieldIndex).prop("disabled",false);
        } else {
            $('#doneEquation_' + this.totalFieldIndex).show();
            $('#doneEquation_' + this.totalFieldIndex).prop("disabled",true);
        }
        this.setupWorkOn();

    }
    setupTimeoutForTurn(){
        if(this.latestCountdownIndex != null){
            if(this.latestCountdownIndex == this.totalFieldIndex){
                if(this.countDown!=null) {
                    this.countDown.updateCountDownDate((new Date().getTime() + TURN_TOTAL_TIME));
                    return;
                }

            }else{
                if(this.countDown != null){
                    this.countDown.cancelCountDown();
                }
            }
        }

        this.countDown = new Countdown((new Date().getTime() + TURN_TOTAL_TIME),
                                       'countdownTurn_' + this.totalFieldIndex,
                                       this.onCountdownEnd.bind(this));
        this.countDown.updateFinalMessage('Turn is changing ...')
        this.latestCountdownIndex = this.totalFieldIndex;

    }
    onCountdownEnd(){
        this.pogsPlugin.saveCompletedTaskAttribute('timerTimedOutForIndex',
                                                   "", 0.0, this.orderIndex, true, '');
    };
    changeIndexCountDueToTimer(timerIndex) {
        if(this.countDown!=null) {
            this.countDown.cancelCountDown();
            this.countDown = null;
        }
        this.updateNextOrderIndex(timerIndex);
        this.sendTimerStartSetup();
    }
    sendTimerStartSetup() {
        this.pogsPlugin.saveCompletedTaskAttribute('startTimerForUserTurn',
                                                       "", 0.0, this.orderIndex, true, '');
    }
    setupCurrentFieldWithTimerInfo() {

        this.setupTimeoutForTurn();

        let sub = this.teamMates[this.orderIndex];
        if (sub.externalId == this.pogsPlugin.getSubjectId()) {

            $('#takeTurnEquation_' + this.totalFieldIndex).attr("value", "Take turn");
            $('#takeTurnEquation_' + this.totalFieldIndex).removeAttr("disabled");
            //show Done.
        } else {
            $('#takeTurnEquation_' + this.totalFieldIndex).attr("value", "It is " + sub.displayName + " 's turn");
            $('#takeTurnEquation_' + this.totalFieldIndex).attr("disabled", true);
            $('doneEquation_' + this.totalFieldIndex).hide();
        }

    }

    setupHooks(){
        $(".doneInp").unbind().on('keyup', this.handleOnClick.bind(this));

        $(".doneBtn").unbind().on('click', this.handleOnBlur.bind(this));

        $(".btnEdit").unbind().on('click', this.handleOnClickEdit.bind(this));

        $(".takeTurnBtn").unbind().on('click', this.handleTakeTurn.bind(this));


    }
    handleTakeTurn(event) {

        let cellIndex = parseInt($(event.target).data("cell-reference-index"));

        if (!isNaN(cellIndex)) {

            this.pogsPlugin.saveCompletedTaskAttribute('takeTurnInField',
                                                       "", 0.0,
                                                       cellIndex, true, '');

        }
    }
    handleOnClick(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {
            let valueTyped = $(event.target).val();
            this.pogsPlugin.saveCompletedTaskAttribute('typedInField',
                                                       valueTyped, 0.0,
                                                       cellIndex, false);

        }
    }
    handleOnBlur(event){


        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {

            let valueTyped = $('#inputEquation_' + cellIndex).val();

            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('equationAnswer' + cellIndex,
                                                           valueTyped, 0.0,
                                                           0, true, '');
                if(this.executionMode == 2) {
                    $("#editEquation_" + cellIndex).show();
                }
            }
        }
    }
    handleOnClickEdit(event){
        //console.log("Before cellIndex ");
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("Clicked edit " + cellIndex);

        $("#inputEquation_"+cellIndex).prop('disabled', false);
        $("#editEquation_"+cellIndex).hide();
        $("#inputEquation_"+cellIndex).focus();
        $('#doneEquation_' + cellIndex).attr("value", "Done");
        $('#doneEquation_' + this.totalFieldIndex).show();
    }
    broadcastReceived(message) {
        let attrName = message.content.attributeName;
        let cellIndex = message.content.attributeIntegerValue;

        if(attrName == "startTimerForUserTurn") {
            console.log(" <startTimerForUserTurn> Message received from: " + message.sender);
            this.setupCurrentFieldWithTimerInfo();
            return;
        }

        if(attrName == "takeTurnInField"){

            if(message.sender == this.pogsPlugin.getSubjectId()) {
                //if user who took the turn is self
                //enable field
                console.log( $("#inputEquation_"+cellIndex) + " : " + cellIndex);
                $("#inputEquation_"+cellIndex).prop('disabled', false);
                $("#inputEquation_"+cellIndex).removeAttr('disabled');
                $("#takeTurnEquation_"+cellIndex).hide();

            }

            /*
            let sub = this.teamMates[this.orderIndex];
            if(sub.externalId == this.pogsPlugin.getSubjectId()) {
                //if user who lost turn is self
                //disable field
                $("#inputEquation_"+cellIndex).prop('disabled', true);
                $("#takeTurnEquation_"+cellIndex).show();

            }*/

            this.userTookTurn();
            //set current user to submit answer

            /*
            let workOn = $("#cell_"+cellIndex +" .workingOn");

            workOn.empty();

            sub = this.pogsPlugin.getSubjectByExternalId(message.sender);



            if(message.sender == this.pogsPlugin.getSubjectId()) {
                //adds the (you)
                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge badgeUser ' + sub.externalId + '_color username">' + sub.displayName
                  + '(you)</span>').appendTo(workOn);
                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            } else {
                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge badgeUser ' + sub.externalId + '_color username">'
                  + sub.displayName
                  + '</span>').appendTo(workOn);

                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            }

             */

        }
        else {
            if(attrName == "timerTimedOutForIndex"){
                console.log("Timer timed out: " + cellIndex + " - for user: " + message.sender);
                this.changeIndexCountDueToTimer(cellIndex);
                return;
            }
            if (attrName == "typedInField") {

                let index = message.content.attributeIntegerValue;
                //do nothing/
                //console.log(message.sender);
                //console.log( this.pogsPlugin.getSubjectId());
                //console.log( this.pogsPlugin.getSubjectId());
                //console.log('#cell_' + index + ' .doneInp');
                if (message.sender != this.pogsPlugin.getSubjectId()) {
                    $('#cell_' + index + ' .doneInp').val(message.content.attributeStringValue);
                }

            }
            if(attrName.indexOf("equationAnswer")!= -1) {

                let index = attrName.replace('equationAnswer', '');

                //$('#cell_' + index + ' input').val(message.content.attributeStringValue);

                $("#inputEquation_" + index).val(message.content.attributeStringValue);
                $("#inputEquation_" + index).prop('disabled', true);
                $("#doneEquation_" + index).val(message.content.attributeStringValue);
                $("#takeTurnEquation_"+index).hide();
                console.log("Equation typing" + index + " - " + this.executionMode);

                if(this.executionMode == 1) {
                    $("#editEquation_" + index).show();
                } else {
                    if((this.totalFieldIndex) >= 0) {
                        $('#cell_' + (this.totalFieldIndex) + ' .workingOn span').hide();
                    }
                }


                if (this.totalFieldIndex == index) {
                    this.updateNextOrderIndex();
                    if(this.executionMode == 2) {
                        this.createNextFieldAndWhoShouldBeAbleToEdit();
                    } else {
                        const TOTAL_TOADD_AFTERLAST_DONE = this.teamMates.length;
                        for(let i =0 ; i < TOTAL_TOADD_AFTERLAST_DONE; i ++){
                            this.createNextFieldAndWhoShouldBeAbleToEdit();
                        }
                    }
                    this.sendTimerStartSetup();
                }
            }
        }
    }
}




var equationTypingTaskPlugin = pogs.createPlugin('equationTypingTaskPlugin',function(){

    let equationTypingTaskPlugin = new EquationTypingTaskPlugin(this);
    // get config attributes from task plugin
    equationTypingTaskPlugin.setup($.parseJSON(this.getStringAttribute("gridBluePrint")),
                                   $.parseJSON(this.getCompletedTaskStringAttribute("gridOrder")));
    this.subscribeTaskAttributeBroadcast(equationTypingTaskPlugin.broadcastReceived.bind(equationTypingTaskPlugin))

});