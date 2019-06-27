class MinimumEffortTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.rounds = []
        this.currentRound = 0;
    }

    setupRounds(gridBluePrint) {

        var gridBluePrint = $.parseJSON(gridBluePrint);
        var allTeammates = this.pogsPlugin.getTeammates();

        var numRounds = parseInt(gridBluePrint.numberOfRounds);

        for (var i = 0; i < numRounds; i++) {
            let usersArray = [];
            for (var j = 0; j < allTeammates.length; j++) {
                usersArray.push({
                                    externalId: allTeammates[j].externalId,
                                    finalAnswer: null,
                                    payout: 0,
                                    signal: false
                                })
            }
            this.rounds[i] = {
                roundNumber: i,
                roundText: ('Round ' + (i + 1)),
                users: usersArray
            };
        }
        this.paymentStructure = gridBluePrint.paymentStructure;
        this.availableNumbers = parseInt(gridBluePrint.availableNumbers);

        $("#taskText").html(gridBluePrint.taskText);

        this.generateNewPaymentStructure(this.availableNumbers);
        this.populatePaymentStructure(this.paymentStructure);
        this.currentRound = 0;
        this.setupRound();

        //broadcast selfReady.

        // Display text and round NUMBER:
        // Display number options
        // All users select send events
        // Broadcast number and change to next round.
    }

    setupRound() {

        var html = '';
        $("#roundText").text(this.rounds[this.currentRound].roundText);
        for (var i = 0; i < this.availableNumbers; i++) {
            html +=
                '<button class="btn btn-info optionBtn" data-round="'
                + this.rounds[this.currentRound].roundNumber + '"  data-option="' + (i + 1) + '">'
                + (i + 1) + '</button>'
        }

        $("#optionList").html(html)
        $(".optionBtn").click(this.selectedNumber.bind(this));

        $("#taskRound").show()
        $("#roundResult").hide();

        $("#informationPanel").html(
            "<span id='countdownRound_" + this.rounds[this.currentRound].roundNumber
            + "'></span> left to select your answer");

        this.roundCountdown = new Countdown((new Date().getTime() + 15000),
                                            ("countdownRound_"
                                             + this.rounds[this.currentRound].roundNumber),
                                            this.changeToResults.bind(this));
    }

    changeToResults() {
        $("#informationPanel").html("Starting new round in <span id='countdownResult'></span>...");
        this.showFinalAnswersPanel();
    }

    selectedNumber(event) {
        $(".optionBtn").removeClass("btn-success");
        $(".optionBtn").addClass("btn-info");
        $(event.target).removeClass("btn-info");
        $(event.target).addClass("btn-success");
        console.log(
            "Clicked " + $(event.target).data("round") + " - " + $(event.target).data("option"));

        this.pogsPlugin.saveCompletedTaskAttribute(
            'roundAnswer_' + this.rounds[this.currentRound].roundNumber+ '|' + this.pogsPlugin.getSubjectId(),
            this.pogsPlugin.getSubjectId(), 0.0,
            $(event.target).data("option"), true);

    }

    broadcastReceived(message) {
        var attrName = message.content.attributeName;

        if (attrName.indexOf("roundAnswer_") > -1) {
            let roundNumber = parseInt(attrName.replace("roundAnswer_", ""));

            let allUsersSignalForRound = true;

            for (var i = 0; i < this.rounds[roundNumber].users.length; i++) {
                let us = this.rounds[roundNumber].users[i];
                if (us.externalId == message.content.attributeStringValue) {
                    this.rounds[roundNumber].users[i].finalAnswer =
                        message.content.attributeIntegerValue;
                    this.rounds[roundNumber].users[i].signal = true;
                }
                if (!this.rounds[roundNumber].users[i].signal) {
                    allUsersSignalForRound = false;
                }
            }

        }
        //if(message.sender != this.pogsPlugin.subjectId) {
        //}

    }

    calculatePayout(minimalNumber, userOwn) {
        console.log("this.paymentStructure " + this.paymentStructure);
        console.log("userOwn " + userOwn);
        console.log("minimalNumber " + minimalNumber);
        return this.paymentStructure[(this.availableNumbers - (userOwn))][(this.availableNumbers
                                                                           - (minimalNumber))];

    }

    showFinalAnswersPanel() {

        let minimalRoundNumber = 99999999;
        let usersOwnAnswer = 0;

        let currentUserIndex = 0;
        for (var i = 0; i < this.rounds[this.currentRound].users.length; i++) {
            if (this.rounds[this.currentRound].users[i].signal) {
                if (parseInt(this.rounds[this.currentRound].users[i].finalAnswer)
                    < minimalRoundNumber) {
                    minimalRoundNumber =
                        parseInt(this.rounds[this.currentRound].users[i].finalAnswer);
                }
                if (this.pogsPlugin.getSubjectId()
                    == this.rounds[this.currentRound].users[i].externalId) {
                    usersOwnAnswer = parseInt(this.rounds[this.currentRound].users[i].finalAnswer);
                    currentUserIndex = i;
                }
            }
        }


        $("#taskRound").hide()

        if (usersOwnAnswer == 0) {
            $("#yourPayoutInfo")
                .text("You did not chose a number, so you don't get any payoff for this round ");
            $("#roundResult h3").hide();
        } else {
            let payoutValue = this.calculatePayout(minimalRoundNumber,
                                                   usersOwnAnswer);
            this.rounds[this.currentRound].users[currentUserIndex].payout = payoutValue;
            $("#minimalNumberForRound").text(minimalRoundNumber);
            $("#roundResult h3").show();
            $("#yourPayoutInfo").text(
                "Your payoff for this round is: " + payoutValue);
        }
        $("#roundResult").show();

        this.countdown = new Countdown((new Date().getTime() + 15000),
                                       "countdownResult",
                                       this.changeToNewRound.bind(this));

    }

    changeToNewRound() {

        if (this.currentRound + 1 != this.rounds.length) {
            this.currentRound++;
            this.setupRound();
        } else {
            this.showDoneRounds();
        }
    }

    showDoneRounds() {
        $("#minEff").hide();
        let totalPayout = 0;
        for (var j = 0; j < this.rounds.length; j++) {
            for (var i = 0; i < this.rounds[j].users.length; i++) {
                if (this.pogsPlugin.getSubjectId()
                    == this.rounds[j].users[i].externalId) {
                    if (this.rounds[j].users[i].signal) {
                        totalPayout += parseInt(this.rounds[j].users[i].payout);
                    }
                }
            }
        }
        $("#finalPayout").text(totalPayout)
        $("#noMoreRounds").show();
    }

    populatePaymentStructure(paymentStructure) {
        for (var i = 0; i < $("#paymentStructure .inpz").length; i++) {
            var currCel = $($("#paymentStructure .inpz")[i]);
            var col = $(currCel).data("col");
            var row = $(currCel).data("row");
            $(currCel).text(paymentStructure[row][col]);
        }
    }

    createTable() {
        //$(".information")
        $("#payoffContainer")
            .append('<table id="paymentStructure" border="1" style="margin: auto"></table>');
    }

    generateNewPaymentStructure(numbers) {
        let k = 0;
        this.createTable();
        $("#paymentStructure").append('<tr><th> - </th><th colspan="' + numbers
                                 + '">Smallest number in group</th></tr>');

        for (let i = 0; i < numbers; i++) {
            if (i == 0) {
                let line = $("<tr>");
                let column = $("<th>");
                column.css("width","40px")
                column.text(" Your number ");
                line.append(column);
                for (let j = 0; j < numbers; j++) {
                    let column = $("<th>");
                    column.text(numbers - j);
                    line.append(column);
                }
                $("#paymentStructure").append(line);

            }
            let line = $("<tr>");
            for (let j = 0; j < numbers; j++) {
                if (j == 0) {
                    let column = $("<th>");
                    column.text(numbers - i)
                    line.append(column);
                }
                let column = $("<td>");
                if (j >= i) {
                    column.append(
                        "<div style='background-color:white;color:black;text-align:center' class='inpz' data-col='"
                        + j + "' data-row='" + i + "' data-ref='" + k + "'>");
                    k++;
                }
                line.append(column)
            }
            $("#paymentStructure").append(line);
        }

    }
}
var minimumEffortTaskPlugin = pogs.createPlugin('minimumEffortTaskPlugin',function(){

    var minimumEffortTask = new MinimumEffortTask(this);
    // get config attributes from task plugin
    minimumEffortTask.setupRounds(this.getStringAttribute("gridBluePrint"));
    this.subscribeTaskAttributeBroadcast(minimumEffortTask.broadcastReceived.bind(minimumEffortTask))

});