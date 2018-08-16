class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.memberReady = 0;
        this.currentMoleNumber = 0;
        this.maxMoleNumber = 3;
        this.moleAppearTime = 1000; // time in millisecond
        this.clickDelay = 0;
        this.subjectColors = ["red", "green", "blue", "purple", "aqua"]; // maximum no. of players = 5
        this.numberOfClicks = 0; // TODO: keep track of the number of clicks
        console.log(pogsPlugin);
    }

    setupGrid(title, teammates) {
        var self = this;
        var score = 0;

        $("#wackamoleTitle").text(title);

        // display teammates' id
        $.each(teammates, function (teammate) {
            console.log(teammate);
        });



        // Only show readyView at start
        $("#gameColumn").children().hide();
        $("#informationColumn").children().hide();
        $("#readyView").show();
        $("#readyBtn").on('click', self.handleReadyOnClick.bind(self));

        // Game grid appearred
        $(".whack_gametable-cell").each(function (i) {
            $("#whack_cell" + i).on('click', self.handleCellOnClick.bind(self));
            $("#whack_cell" + i).on('removeMole', self.handleCellRemoveMole.bind(self));
            $("#whack_cell" + i).on('addMole', self.handleCellAddMole.bind(self));
        });

        // mouse move event
        $("#wackamoleContainer").on('mousemove', self.handleMouseMove.bind(self));
        // check debounce function with different values
        // $("#wackamoleContainer").on('mousemove', self.debounce(self.handleMouseMove.bind(self), 100, true));

    }

    debounce(func, wait, immediate) {
        var timeout;
        return function() {
            var context = this, args = arguments;
            var later = function() {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            var callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        };
    }

    initPlayers(teammates, myId) {
        var self = this;
        self.subjectId = myId; // this is my id
        self.teammates = {};
        $.each(teammates, function(key, player) {
            var externalId = player.externalId;
            var displayName = player.displayName;
            var isCurrentPlayer = (externalId == myId);
            if (isCurrentPlayer) {
                self.changeCursorColor(self.subjectColors[key]);
            }
            var playerObj = new Player(externalId, displayName, self.subjectColors[key], isCurrentPlayer);
            self.teammates[player.externalId] = playerObj;
        });
    }

    broadcastReceived(message) {
        var self = this;
        var attrName = message.content.attributeName;
        if (message.sender != this.pogsPlugin.getSubjectId()) {
             if (attrName == "removeMole") {
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

            else if (attrName == 'mouseMove') {
                var position = message.content.attributeStringValue.split(":");
                this.teammates[message.sender].updatePosition(position[0], position[1]);
            }
            else if(attrName == 'clickInCell') {  // TODO: Same code is repeated; should this be a common function?
                var cell = message.content.attributeIntegerValue;
                if(!($("#whack_cell"+cell).hasClass("clicked"))) {

                    if($("#whack_cell"+cell).hasClass("hasMole")){
                        var newScore = parseInt($("#score").text()) + 1;
                        $("#score").text(newScore);
                    }

                    $("#whack_cell" + cell).css('background-color', this.teammates[message.sender].color);
                    $("#whack_cell" + cell).addClass('clicked');

                    setTimeout(function () {
                        $("#whack_cell" + cell).css('background-color', 'white');
                        $("#whack_cell" + cell).removeClass('clicked');
                    }, 1000); // TODO: change back to 500 after testing
                }
                console.log("click message recieved");
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

            $("#gameColumn").children().hide();

            // Show CountDown hovering over gameGrid and result table
            $("#countDownModal").modal("show");
            $('.modal-backdrop').appendTo('#gameColumn');
            $("#gameColumn").addClass("after_modal_appended");
            $(".whack_grid").show();
            $("#informationColumn").children().show();
            // $(".fa-mouse-pointer").css("color", self.teammates[self.subjectId].color);
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

            this.numberOfClicks++;
            // broadcast mouse click
            self.pogsPlugin.saveCompletedTaskAttribute('clickInCell',
                "", 0.0,
                cell, false);

            if (!($("#whack_cell" + cell).hasClass("clicked"))) {

                // increment score if a mole is present in cell
                if ($("#whack_cell" + cell).hasClass("hasMole")) {
                    var newScore = parseInt($("#score").text()) + 1;
                    $("#score").text(newScore);
                }

            $("#whack_cell" + cell).css('background-color', self.teammates[self.subjectId].color);
            $("#whack_cell" + cell).addClass('clicked');


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

    handleMouseMove(event) {
        var offset = $('#wackamoleContainer').offset();
        var x = event.pageX - offset.left;
        var y = event.pageY - offset.top;
        // broadcast mouse move
        this.pogsPlugin.saveCompletedTaskAttribute('mouseMove',
            x + ":" + y, 0.0,
            0, false);
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

class Player {
    constructor(id, name, color, isCurrentPlayer) {
        this.externalId = id;
        this.displayName = name;
        this.color = color;
        this.isCurrentPlayer = isCurrentPlayer;
        this.setUpPointer();
        // TODO: should score be tied to players?

    }
    setUpPointer() {
        if(this.isCurrentPlayer) {
            $("#myPointerColor").css("color", this.color);
        }
        else {
            $("#pointerContainer").append('<i id="'+this.externalId+'Pointer" class="fa fa-mouse-pointer" style="color:'+this.color+';display:none"></i>')
        }
        console.log("init player "+this.externalId);
        console.log("color: "+this.color);
    }
    updatePosition(x, y) {
        $("#"+this.externalId+"Pointer").css({left: x+"px", top: y+"px", position:'absolute'}).show();
    }
}

var wackamolePlugin = pogs.createPlugin('wackamoleTaskPlugin', function () {
    console.log("Wack-a-mole Plugin Loaded");
    var wackamole = new Wackamole(this);
    wackamole.setupGrid("Wack-a-mole", this.getTeammates())
    this.subscribeTaskAttributeBroadcast(wackamole.broadcastReceived.bind(wackamole))
    console.log("teammates" + this.getTeammates());
    wackamole.initPlayers(this.getTeammates(), this.getSubjectId());
});