class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.memberReady = 0;
        this.currentMoleNumber = 0;
        this.maxMoleNumber = 3;
        this.moleAppearTime = 1000; // time in millisecond
        this.clickDelay = 0;
        this.subjectColor = null;
    }

    setupGrid(title) {
        var self = this;
        var score = 0;

        $("#wackamoleTitle").text(title);

        // change cursor color
        var subjectNumToColor = {
            0: "#6495ED",
            1: "#98FB98",
            2: "#F08080",
            3: "#707070"
        };
        var teammates = self.pogsPlugin.getTeammates();
        for (var i = 0; i < teammates.length; i++) {
            if (teammates[i].externalId == self.pogsPlugin.getSubjectId()) {
                self.subjectColor = subjectNumToColor[i];
            }
        }
        self.changeCursorColor(self.subjectColor);

        // Only show readyView at start
        $("#gameColumn").children().hide();
        $("#informationColumn").children().hide();
        $("#readyView").show();
        $("#readyBtn").valid
        $("#readyBtn").on('click', self.handleReadyOnClick.bind(self));

        // Game grid appearred
        $(".whack_gametable-cell").each(function (i) {
            $("#whack_cell" + i).on('click', self.handleCellOnClick.bind(self));
            $("#whack_cell" + i).on('removeMole', self.handleCellRemoveMole.bind(self));
            $("#whack_cell" + i).on('addMole', self.handleCellAddMole.bind(self));

        });
    }

    broadcastReceived(message) {
        var self = this;
        var attrName = message.content.attributeName;
        if (message.sender != this.pogsPlugin.getSubjectId()) {
            if (attrName == "clickedCell") {
                var cell = message.content.attributeIntegerValue;
                var color = message.content.attributeStringValue;

                // change cell background color
                $("#whack_cell" + cell).css('background-color', color);
                $("#whack_cell" + cell).addClass('clicked');

                // after 0.5 second the cell background change back to white
                setTimeout(function () {
                    $("#whack_cell" + cell).css('background-color', 'white');
                    $("#whack_cell" + cell).removeClass('clicked');
                }, 500); // background change back to white after __ ms
            }
            else if (attrName == "removeMole") {
                var cell = message.content.attributeIntegerValue;

                $("#whack_cell" + cell).removeClass("hasMole");
                $("#whack_cell" + cell).find('.fa-optin-monster').remove();

            }
            else if (attrName == "addMole") {
                var cell = message.content.attributeIntegerValue;

                //safe checking in case multi mole spawn at same spot
                if (!($("#whack_cell" + cell).hasClass("hasMole"))) {
                    $("#whack_cell" + cell).addClass("hasMole");
                    $("#whack_cell" + cell).append('<i class="fa fa-optin-monster" data-cell-reference-index="' + cell + '"></i>');
                }

            }
            else if (attrName == "memberReady") {
                this.memberReady++;
                if (self.pogsPlugin.getTeammates().length == self.memberReady) {
                    // 5 seconds Count down, then display gameGrid
                    // and mole start popping up randomly
                    var countDownDate = new Date().getTime() + 5000;
                    this.countDownTo(countDownDate, "loadingCountDown");

                    setTimeout(function () {
                        $("#countDownModal").modal("hide");
                        $("#gameColumn").remove('.modal-backdrop');
                        $("#gameColumn").removeClass("after_modal_appended");
                        self.molePopUp();
                    }, 5000);
                }
            }
        }
    }

    handleReadyOnClick(event) {
        var self = this;

        if ($("#readyForm")[0].checkValidity()) {
            event.preventDefault();

            // get max mole, mole appear and delay time from input fields
            self.moleAppearTime = $("#moleAppearTime").val() * 1000;
            self.maxMoleNumber = $("#maxMoleNumber").val();
            self.clickDelay = $("#clickDelay").val() * 1000;

            // for single player
            $("#gameColumn").children().hide();

            // Show CountDown hovering over gameGrid and result table
            $("#countDownModal").modal("show");
            $('.modal-backdrop').appendTo('#gameColumn');
            $("#gameColumn").addClass("after_modal_appended");
            $(".whack_grid").show();
            $("#informationColumn").children().show();
            $(".fa-mouse-pointer").css("color", self.subjectColor);
            $("#loadingCountDown").html("Waiting for teammates...");

            self.memberReady++;

            // Broadcast member ready
            self.pogsPlugin.saveCompletedTaskAttribute('memberReady',
                "", 0.0,
                0, false);

            if (this.pogsPlugin.getTeammates().length == self.memberReady) {
                // 5 seconds Count down, then display gameGrid
                // and mole start popping up randomly
                var countDownDate = new Date().getTime() + 5000;
                self.countDownTo(countDownDate, "loadingCountDown");

                setTimeout(function () {
                    $("#countDownModal").modal("hide");
                    $("#gameColumn").remove('.modal-backdrop');
                    $("#gameColumn").removeClass("after_modal_appended");
                    self.molePopUp();
                }, 5000);
            }
        }
    }

    handleCellOnClick(event) {
        var self = this;
        var cell = parseInt($(event.target).data("cell-reference-index"));

        setTimeout(function () {
            console.log("delay in click function" + self.clickDelay);
            if (!($("#whack_cell" + cell).hasClass("clicked"))) {

                // increment score if a mole is present in cell
                if ($("#whack_cell" + cell).hasClass("hasMole")) {
                    var newScore = parseInt($("#score").text()) + 1;
                    $("#score").text(newScore);
                }

                // change cell background color
                $("#whack_cell" + cell).css('background-color', self.subjectColor);
                $("#whack_cell" + cell).addClass('clicked');

                // Broadcast cell click
                self.pogsPlugin.saveCompletedTaskAttribute('clickedCell',
                    self.subjectColor, 0.0,
                    cell, false);

                // after 0.5 second the cell background change back to white
                setTimeout(function () {
                    $("#whack_cell" + cell).css('background-color', 'white');
                    $("#whack_cell" + cell).removeClass('clicked');
                }, 500);
            }
        }, self.clickDelay);

    }

    handleCellRemoveMole(event) {
        var cell = parseInt($(event.target).data("cell-reference-index"));

        this.pogsPlugin.saveCompletedTaskAttribute('removeMole',
            "", 0.0,
            cell, false);
    }

    handleCellAddMole(event) {
        var cell = parseInt($(event.target).data("cell-reference-index"));

        this.pogsPlugin.saveCompletedTaskAttribute('addMole',
            "", 0.0,
            cell, false);
    }

    countDownTo(countDownDate, elementId) {
        // Update the count down every 1 second
        var x = setInterval(function () {

            // Get todays date and time
            var now = new Date().getTime();

            // Find the distance between now and the count down date
            var distance = countDownDate - now;

            // Time calculations for days, hours, minutes and seconds
            var seconds = Math.floor(distance / 1000);

            // Output the result in an element with id="demo"
            document.getElementById(elementId).innerHTML = seconds + "s";

            // If the count down is over, write some text
            if (distance < 0) {
                clearInterval(x);
                document.getElementById(elementId).innerHTML = "0s";
            }
        }, 300);
    }

    molePopUp() {
        var self = this;
        var roundEndTime = new Date().getTime() + 60000;

        console.log("current mole" + self.currentMoleNumber);
        console.log("max mole" + self.maxMoleNumber);
        console.log("mole appear time" + self.moleAppearTime);


        self.countDownTo(roundEndTime, "roundTimeRemain");

        var x = setInterval(function () {
            // Time Left
            var now = new Date().getTime();
            var distance = roundEndTime - now;

            if (self.currentMoleNumber < self.maxMoleNumber) {
                // add new mole
                var randomCell = Math.floor(Math.random() * 100);
                // check if cell already contain mole
                while ($("#whack_cell" + randomCell).hasClass("hasMole")) {
                    randomCell = Math.floor(Math.random() * 100);
                }
                $("#whack_cell" + randomCell).addClass("hasMole");
                $("#whack_cell" + randomCell).append('<i class="fa fa-optin-monster" data-cell-reference-index="' + randomCell + '"></i>');

                // trigger mole added event
                $("#whack_cell" + randomCell).trigger("addMole");

                self.currentMoleNumber++;

                // remove mole after moleAppearTime
                setTimeout(function () {
                    $("#whack_cell" + randomCell).removeClass("hasMole");
                    $("#whack_cell" + randomCell).find('.fa-optin-monster').remove();

                    // trigger mole remove event
                    $("#whack_cell" + randomCell).trigger("removeMole");

                    self.currentMoleNumber--;

                }, self.moleAppearTime);

            }

            // If the count down is over, empty all mole and print gamer over at time slot
            if (distance < 0) {
                clearInterval(x);
                document.getElementById("roundTimeRemain").innerHTML = "GameOver";
                $(".whack_gametable-cell").find('.fa-optin-monster').remove();
                $(".whack_gametable-cell").removeClass("hasMole");
            }
        }, 100); // This is mole appear frequency when moleNum is less than maxMoleNum
    }

    changeCursorColor(colorInHex) {
        var canvas = document.createElement("canvas");
        canvas.width = 24;
        canvas.height = 24;
        //document.body.appendChild(canvas);
        var ctx = canvas.getContext("2d");
        ctx.fillStyle = colorInHex;
        ctx.font = "16px FontAwesome";
        ctx.textAlign = "left";
        ctx.textBaseline = "top";
        ctx.fillText("\uf245", 0, 0);
        var dataURL = canvas.toDataURL('image/png')
        $('#wackamoleContainer').css('cursor', 'url(' + dataURL + '), auto');
        $('.whack_gametable-cell').css('cursor', 'url(' + dataURL + '), auto');
    }

}


var wackamolePlugin = pogs.createPlugin('wackamoleTaskPlugin', function () {
    console.log("Wack-a-mole Plugin Loaded");
    var wackamole = new Wackamole(this);
    wackamole.setupGrid("Wack-a-mole")
    this.subscribeTaskAttributeBroadcast(wackamole.broadcastReceived.bind(wackamole))
});