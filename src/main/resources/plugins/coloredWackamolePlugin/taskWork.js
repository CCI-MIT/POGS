class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.memberReady = 0;
        this.currentMoleNumber = 0;
        this.maxMoleNumber = 1;
        this.moleAppearTime = 1000; // time in millisecond
        this.clickDelay = 0; // time in millisecond

        this.subjectColors = ["red", "green", "blue", "purple", "aqua"]; // maximum no. of players = 5

        this.numberOfClicks = 0; // TODO: keep track of the number of clicks
        this.totalHitOnTarget = 0; // Change later
        this.playerHitOnTarget = 0;
        this.totalTarget = 0;
        this.multiplayerRound = 0;
        this.numberOfRounds = 0;
        this.countdowns = {};
        this.blueprint = "";
        //console.log(pogsPlugin);
        this.taskStartRefereceTimestamp = new Date().getTime();
        this.mouseBuffer = [];
        this.lastMouseClearedBuffer = 0;
    }

    setupGrid(title, teammates, whackBluePrint, configuration) {
        var self = this;
        this.blueprint = (whackBluePrint);
        let configurationValues = $.parseJSON(configuration)

        //console.log(whackValues);
        //console.log(whackBluePrint);

        var score = 0;

        $("#wackamoleTitle").text(title);

        // display teammates' id
        $.each(teammates, function (teammate) {
            //console.log(teammate);
        });

        // $.each(whackValues, function (i, e) {
        //     //if (teammates[e.player].externalId == self.pogsPlugin.getSubjectId()) {
        //         //console.log(" whackConfig i :" + i);
        //         //console.log(" e.maxMoleNum:" + e.maxMoleNum);
        //         //console.log(" e.numberOfRounds:" + e.numberOfRounds);
        //         //console.log(" e.moleAppearTime:" + e.moleAppearTime);
        //
        //         self.maxMoleNumber = e.maxMoleNum;
        //         self.numberOfRounds = e.numberOfRounds;
        //         self.moleAppearTime = e.moleAppearTime * 1000; // time in millisecond
        //         self.clickDelay = e.clickDelay * 1000;
        //     //}
        // });

        // Only show readyView at start
        $("#gameColumn").children().hide();
        $("#informationColumn").hide();
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
        $("#blackOut").on('click',this.handleSendBlackOut.bind(this));

        self.pogsPlugin.saveCompletedTaskAttribute('totalOfRounds',
                                                   "", 0.0,
                                                   self.multiplayerRound, true,"");
        // check debounce function with different values
        // $("#wackamoleContainer").on('mousemove', self.debounce(self.handleMouseMove.bind(self), 100, true));

    }

    debounce(func, wait, immediate) {
        var timeout;
        return function () {
            var context = this, args = arguments;
            var later = function () {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            var callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        };
    }
    handleSendBlackOut(){
        $("#blackOut").prop("disabled",true);
        $("#blackOut").text("Blackout sent")
        this.pogsPlugin.saveCompletedTaskAttribute('blackoutSent',
            "", 0.0,
            0, true, '', 'Subject sent blackout to other team: ');
        setTimeout(function(){
            $("#blackOut").text("Send a blackout")
            $("#blackOut").prop("disabled",false);
        },1000*60*1)
    }
    initPlayers(teammates, myId) {
        var self = this;
        self.subjectId = myId; // this is my id
        self.teammates = {};
        $.each(teammates, function (key, player) {
            var externalId = player.externalId;
            var displayName = player.displayName;
            var isCurrentPlayer = (externalId == myId);
            //if (isCurrentPlayer) {
            //    self.changeCursorColor(self.subjectColors[key]);
            //}
            var playerObj = new Player(externalId, displayName,  isCurrentPlayer);
            self.teammates[player.externalId] = playerObj;
        });

        setTimeout(function () {
            this.handleReadyOnClick(null);//auto start round after 10 seconds
        }.bind(this),1000*10);


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
                    self.totalTarget++;
                    $("#whack_cell" + cell).addClass("hasMole");
                    $("#whack_cell" + cell).append('<i class="fa monster fa-optin-monster" data-cell-reference-index="' + cell + '"></i>');
                }

            }
            else if (attrName == "memberReady") {
                this.memberReady++;

                //if (self.pogsPlugin.getTeammates().length == self.memberReady) {
                    // 5 seconds Count down, then display gameGrid
                    // and mole start popping up randomly
                    /*var countDownDate = new Date().getTime() + 5000;
                    this.countDownTo(countDownDate, "loadingCountDown",
                    function() {
                        setTimeout(function () {
                            $("#countDownModal").modal("hide");
                            $("#gameColumn").remove('.modal-backdrop');
                            $("#gameColumn").removeClass("after_modal_appended");
                            this.molePopUp();
                        }.bind(this), 5000);
                    }.bind(this));
                     */
                //}
            }

            else if (attrName == 'mouseMove') {
                /*
                let infos = message.content.attributeStringValue.split(";");
                for(let i=0;i < infos.length; i++) {
                    let position = infos[i].split(":");
                    setTimeout(function(){
                        this.teammates[message.sender].updatePosition(position[1], position[2]);
                    }.bind(this),(this.taskStartRefereceTimestamp + parseInt(position[0]) - new Date().getTime()));

                }*/
                let position = message.content.attributeStringValue.split(":");
                this.teammates[message.sender].updatePosition(position[0], position[1]);
            }
            else if (attrName == 'clickInCell') {  // TODO: Same code is repeated; should this be a common function?
                var cell = message.content.attributeIntegerValue;
                if (!($("#whack_cell" + cell).hasClass("clicked"))) {

                    $("#whack_cell" + cell).css('background-color', this.teammates[message.sender].color);
                    $("#whack_cell" + cell).addClass('clicked');

                    setTimeout(function () {
                        $("#whack_cell" + cell).css('background-color', 'white');
                        $("#whack_cell" + cell).removeClass('clicked');
                    }, 1000); // TODO: change back to 500 after testing
                }
                //console.log("click message recieved");
            }
            else if (attrName == 'targetHit') {
                self.totalHitOnTarget++;
                var cell = message.content.attributeIntegerValue;
                $("#teamScore").text(self.totalHitOnTarget);
                //TODO:
                $("#whack_cell" + cell)
                    .addClass("hit_color")
                    .delay(1000).queue(function (next) {
                    $(this).removeClass("hit_color");
                    next();
                });
            }
        }
    }

    handleBlackoutReceived(){
        this.pogsPlugin.saveCompletedTaskAttribute('blackoutReceived',
            "", 0.0,
            0, true, '', 'Subject sent blackout to other team: ');
        $("#loadingCountDown").html("You received a blackout from the other team...");
        $("#modalTitle").html("BLACKOUT")
        $("#countDownModal").modal("show");

        var countDownDate = new Date().getTime() + 10000;
        this.countDownTo(countDownDate, "loadingCountDown",
            function () {
                setTimeout(function () {
                    $("#countDownModal").modal("hide");
                    $("#gameColumn").remove('.modal-backdrop');
                    $("#gameColumn").removeClass("after_modal_appended");

                }.bind(this), 1)
            }.bind(this)
        );
    }
    handleReadyOnClick(event) {
        var self = this;

        $("#gameColumn").children().hide();

        // Show CountDown hovering over gameGrid and result table
        $("#countDownModal").modal("show");
        $('.modal-backdrop').appendTo('#gameColumn');
        $("#gameColumn").addClass("after_modal_appended");
        $(".whack_grid").show();
        $("#informationColumn").show();
        $("#informationColumn").children().show();
        // $(".fa-mouse-pointer").css("color", self.teammates[self.subjectId].color);
        $("#loadingCountDown").html("Waiting for teammates...");

        self.memberReady++;

        // Broadcast member ready
        self.pogsPlugin.saveCompletedTaskAttribute('memberReady',
            "", 0.0,
            0, false);

        //if (this.pogsPlugin.getTeammates().length == self.memberReady) {
            // 5 seconds Count down, then display gameGrid
            // and mole start popping up randomly
            var countDownDate = new Date().getTime() + 60*1000;
            self.countDownTo(countDownDate, "loadingCountDown",
            function () {
                setTimeout(function () {
                    $("#countDownModal").modal("hide");
                    $("#gameColumn").remove('.modal-backdrop');
                    $("#gameColumn").removeClass("after_modal_appended");

                    this.molePopUp();

                }.bind(this), 60*1000)
            }.bind(this)
            );
        //}

    }

    handleCellOnClick(event) {
        var self = this;
        var cell = parseInt($(event.target).data("cell-reference-index"));

        var hasMole = $("#whack_cell" + cell).hasClass("hasMole");
        var hasClicked = $("#whack_cell" + cell).hasClass("clicked");

        //setTimeout(function () {

            // broadcast cell click
            self.pogsPlugin.saveCompletedTaskAttribute('clickInCell',
                "", 0.0,
                cell, true);

            if (!(hasClicked)) {

                self.numberOfClicks++;

                // increment score if a mole is present in cell
                if (hasMole) {
                    self.totalHitOnTarget++;
                    self.playerHitOnTarget++;
                    $("#score").text(self.playerHitOnTarget);
                    $("#teamScore").text(self.totalHitOnTarget);
                    // broadcast target hit because broadcast cell click broadcast has delay
                    // and mole might disappear when cell click broadcast is received
                    self.pogsPlugin.saveCompletedTaskAttribute('targetHit',
                        "", 0.0,
                        cell, true, '', 'Subject hit mole in cell: ' + cell);
                    //TODO: add animation
                    $("#whack_cell" + cell)
                        .addClass("hit_color")
                        .delay(1000).queue(function (next) {
                        $(this).removeClass("hit_color");
                        next();
                    });

                }

                $("#whack_cell" + cell).css('background-color', self.teammates[self.subjectId].color);
                $("#whack_cell" + cell).addClass('clicked');


                // after 0.5 second the cell background change back to white
                setTimeout(function () {
                    $("#whack_cell" + cell).css('background-color', 'white');
                    $("#whack_cell" + cell).removeClass('clicked');
                }, 500);
            }
        //}
                   //self.clickDelay);

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
    appendMouseMovement(t,x,y){
        this.mouseBuffer.push(t + ":" + x + ":" + y);
        if(new Date().getTime() - this.lastMouseClearedBuffer > 500) {
            let mousBuff = this.mouseBuffer.reduce( (ac,cv) => ac +";" +cv , []);
            this.mouseBuffer = [];
            this.lastMouseClearedBuffer = new Date().getTime();
            this.pogsPlugin.saveCompletedTaskAttribute('mouseMove',
                mousBuff, 0.0,
                0, true);
        }
    }
    handleMouseMove(event) {
        var offset = $('#wackamoleContainer').offset();
        var x = event.pageX - offset.left;
        var y = event.pageY - offset.top;
        // broadcast mouse move
        //this.appendMouseMovement((new Date().getTime() - this.taskStartRefereceTimestamp),x,y);
        this.pogsPlugin.saveCompletedTaskAttribute('mouseMove',
            x+':'+y, 0.0,
            0, true);

    }

    countDownTo(countDownDate, elementId, callback) {
        // Update the count down every 1 second
        if(this.countdowns[elementId]) {
            clearInterval(this.countdowns[elementId]);
        }
        this.countdowns[elementId] = setInterval(function () {

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
                clearInterval(this.countdowns[elementId]);
                document.getElementById(elementId).innerHTML = "0s";
                if(callback)
                    callback.call();
            }
        }.bind(this), 300);
    }

    molePopUp() {
        var self = this;
        self.memberReady = 0;


        var lastRound = parseInt($("#rounds").text());
        $("#rounds").text(lastRound + 1);
        self.multiplayerRound++;

        var roundEndTime = new Date().getTime() + 60000;

        self.countDownTo(roundEndTime, "roundTimeRemain");

        let bluePrint = this.blueprint;
        let lines = bluePrint.split("\n");
        let line = [];
        let cellIndex = null;
        let cellKind = null;
        let cellTime = null;
        for(let i =0 ; i < lines.length; i++ ) {
            line = lines[i].split(";");
            cellIndex = line[2];
            cellKind = line[1];
            cellTime = line[0];
            if(cellKind === "BLACK"){
                setTimeout(
                function(){this.handleBlackoutReceived()}.bind(this),
                cellTime)

            } else {


                setTimeout(function (cellIndex, cellKind, cellTime) {
                    //console.log("[ #whack_cell" + cellIndex + "] " + cellTime);

                    $("#whack_cell" + cellIndex).addClass("hasMole");
                    $("#whack_cell" + cellIndex).addClass(cellKind);
                    $("#whack_cell" + cellIndex).append('<i class="fa fa-optin-monster" data-cell-reference-index="' + cellIndex + '"></i>');

                    // trigger mole added event
                    //$("#whack_cell" + randomCell).trigger("addMole");
                    self.totalTarget++;
                    self.currentMoleNumber++;

                    // remove mole after moleAppearTime
                    setTimeout(function (cellIndex, cellKind, cellTime) {
                        $("#whack_cell" + cellIndex).removeClass("hasMole");
                        $("#whack_cell" + cellIndex).removeClass("RED");
                        $("#whack_cell" + cellIndex).removeClass("GREEN");
                        $("#whack_cell" + cellIndex).removeClass("BLUE");
                        $("#whack_cell" + cellIndex).removeClass("BROWN");
                        $("#whack_cell" + cellIndex).find('.fa-optin-monster').remove();

                        // trigger mole remove event
                        //$("#whack_cell" + randomCell).trigger("removeMole");

                        self.currentMoleNumber--;

                    }, self.moleAppearTime, cellIndex, cellKind, cellTime);

                }, cellTime, cellIndex, cellKind, cellTime);
            }
        }
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

    displayEvaluationForm() {
        var str = '';
        for (var id in this.teammates) {
            var player = this.teammates[id];
            if (player.externalId != this.subjectId) {
                str += "<div class='row mar m-3'>" +
                    "<div class='col-md-1 offset-md-6 rounded' style='background-color:red;'></div>" +
                    "<div class='col-md-3'><input type='text' class='form-control' id='value1'></div></div>";
            }
        }
        $("#gameColumn").hide();
        $("#playerEvalList").append(str).show();

        // TODO: make ready scree show up after evaluation.
        // Currently it shows up at the same time as the evaluation
        if(this.multiplayerRound < 20)
        {
            this.numberOfClicks = 0;
            this.totalHitOnTarget = 0;
            this.playerHitOnTarget = 0;
            this.totalTarget = 0;
            $("#gameColumn").children().hide();
            $("#gameColumn").show();
            $("#readyView").show();
        }
    }

}

class Player {
    constructor(id, name,  isCurrentPlayer) {
        this.externalId = id;
        this.displayName = name;
        this.isCurrentPlayer = isCurrentPlayer;
        this.setUpPointer();
        // TODO: should score be tied to players?

    }

    setUpPointer() {
        if (this.isCurrentPlayer) {
            //$("#myPointerColor").addClass(this.externalId + "_activecolor");
        }
        else {
            $("#pointerContainer").append('<i id="' + this.externalId + 'Pointer" class="'+this.externalId+'_activecolor fa fa-mouse-pointer" style="display:none"></i>')
        }
        //console.log("init player " + this.externalId);
        //console.log("color: " + this.color);
    }

    updatePosition(x, y) {
        $("#" + this.externalId + "Pointer").css({left: x + "px", top: y + "px", position: 'absolute'}).show();
    }
}

var wackamolePlugin = pogs.createPlugin('coloredWackamoleTaskPlugin', function () {
    //console.log("Wack-a-mole Plugin Loaded");

    var wackamole = new Wackamole(this);
    wackamole.setupGrid("Whack-a-mole", this.getTeammates(),
                        this.getStringAttribute("blueprint"),
                        this.getStringAttribute("configuration"))
    this.subscribeTaskAttributeBroadcast(wackamole.broadcastReceived.bind(wackamole))
    //console.log("teammates" + this.getTeammates());
    wackamole.initPlayers(this.getTeammates(), this.getSubjectId());
    this.selfRef = wackamole;
});