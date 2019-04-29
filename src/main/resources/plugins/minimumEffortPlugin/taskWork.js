class MinimumEffortTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.rounds = []
        this.currentRound = 0;
    }
    setupRounds(gridBluePrint){

        var gridBluePrint = $.parseJSON(gridBluePrint);
        var allTeammates = this.pogsPlugin.getTeammates();

        var numRounds = parseInt(gridBluePrint.numberOfRounds);

        for(var i=0; i < numRounds; i++) {
            let usersArray = [];
            for(var j = 0 ; j < allTeammates.length; j ++){
                usersArray.push({
                             externalId : allTeammates[j].externalId,
                             finalAnswer: null,
                             signal: false})
            }
            this.rounds[i] = {
                roundNumber: i,
                roundText : ('Round ' + (i +1)),
                users: usersArray
            };
        }
        this.paymentStructure = gridBluePrint.paymentStructure;
        this.availableNumbers = parseInt(gridBluePrint.availableNumbers);


        $("#taskText").html(gridBluePrint.taskText);

        this.currentRound = 0;
        this.setupRound();

        //broadcast selfReady.

        // Display text and round NUMBER:
        // Display number options
        // All users select send events
        // Broadcast number and change to next round.
    }
    setupRound(){


        var html = '';
        $("#roundText").text(this.rounds[this.currentRound].roundText);
        for(var i = 0; i < this.availableNumbers; i++) {
            html+='<button class="btn btn-info optionBtn" data-round="'+this.rounds[this.currentRound].roundNumber+'"  data-option="'+(i+1)+'">'+(i+1)+'</button>'
        }


        $("#optionList").html(html)
        $(".optionBtn").click(this.selectedNumber.bind(this));

        $("#informationPanel").text("");

        $("#taskRound").show()
        $("#roundResult").hide();

    }
    selectedNumber(event){
        $(".optionBtn").removeClass("btn-success");
        $(".optionBtn").addClass("btn-info");
        $(event.target).removeClass("btn-info");
        $(event.target).addClass("btn-success");
        console.log("Clicked "+ $(event.target).data("round") + " - " + $(event.target).data("option"));

        this.pogsPlugin.saveCompletedTaskAttribute('roundAnswer_' + this.rounds[this.currentRound].roundNumber,
                                                   this.pogsPlugin.getSubjectId(), 0.0,
                                                   $(event.target).data("option"), true);

        $("#informationPanel").text("Waiting for all users to answer ...");
    }
    broadcastReceived(message) {
        var attrName = message.content.attributeName;
        console.log(attrName);
        if (attrName.indexOf("roundAnswer_") > -1 ) {
            let roundNumber = parseInt(attrName.replace("roundAnswer_",""));

            let allUsersSignalForRound = true;
            for(var i=0; i < this.rounds[roundNumber].users.length;i++){
                let us = this.rounds[roundNumber].users[i];
                console.log( "User: " + us.externalId);
                console.log(us);
                if(us.externalId == message.content.attributeStringValue){
                    this.rounds[roundNumber].users[i].finalAnswer = message.content.attributeIntegerValue;
                    this.rounds[roundNumber].users[i].signal = true;
                }
                if(!this.rounds[roundNumber].users[i].signal){
                    allUsersSignalForRound = false;
                }
            }

            if(allUsersSignalForRound) {
                $("#informationPanel").html("Starting new round in <span id='countdownResult'></span>...");
                this.showFinalAnswersPanel();

            }

        }
        //if(message.sender != this.pogsPlugin.subjectId) {
        //}

    }
    calculatePayout(minimalNumber, userOwn){
        console.log("this.paymentStructure " + this.paymentStructure);
        console.log("userOwn " + userOwn);
        console.log("minimalNumber " + minimalNumber);
        return this.paymentStructure[(this.availableNumbers-(userOwn))][(this.availableNumbers - (minimalNumber))];

    }
    showFinalAnswersPanel(){

        let minimalRoundNumber = 99999999;
        let usersOwnAnswer = 0;


        for(var i=0; i < this.rounds[this.currentRound].users.length;i++){
            if(parseInt(this.rounds[this.currentRound].users[i].finalAnswer)< minimalRoundNumber){
                minimalRoundNumber = parseInt(this.rounds[this.currentRound].users[i].finalAnswer);
            }
            if(this.pogsPlugin.getSubjectId() == this.rounds[this.currentRound].users[i].externalId){
                usersOwnAnswer = parseInt(this.rounds[this.currentRound].users[i].finalAnswer);
            }
        }


        $("#taskRound").hide()
        $("#minimalNumberForRound").text(minimalRoundNumber)
        $("#yourPayoutInfo").text("Your payout for this round is: " + this.calculatePayout(minimalRoundNumber, usersOwnAnswer));
        $("#roundResult").show();

        this.countdown = new Countdown((new Date().getTime()+ 15000),
                                       "countdownResult",
                                       this.changeToNewRound.bind(this));


    }
    changeToNewRound(){

        this.currentRound++;
        this.setupRound();
    }
}

var minimumEffortTaskPlugin = pogs.createPlugin('minimumEffortTaskPlugin',function(){

    var minimumEffortTask = new MinimumEffortTask(this);
    // get config attributes from task plugin
    minimumEffortTask.setupRounds(this.getStringAttribute("gridBluePrint"));
    this.subscribeTaskAttributeBroadcast(minimumEffortTask.broadcastReceived.bind(minimumEffortTask))

});