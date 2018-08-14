class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.subjectColors = ["red", "green", "blue", "purple", "aqua"]; // maximum no. of players = 5
        this.numberOfClicks = 0; // TODO: keep track of the number of clicks
        console.log(pogsPlugin);
    }

    setupGrid(title, teammates) {
        var self = this;
        var score = 0;

        console.log("self for setup"+self);

        $("#wackamoleTitle").text(title);

        // display teammates' id
        $.each(teammates, function (teammate) {
            console.log(teammate);
        });

        // change cursor color
        // self.changeCursorColor("#00FFFF");

        // Only show readyView at start
        $("#gameColumn").children().hide();
        $("#informationColumn").children().hide();
        $("#readyView").show();
        $("#readyBtn").on('click', self.handleReadyOnClick.bind(self));

        // Game grid appearred
        $(".whack_gametable-cell").each(function (i) {
            $("#whack_cell" + i).on('click', self.handleCellOnClick.bind(self));
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
        if(message.sender != this.subjectId) {
            var attrName = message.content.attributeName;
            if (attrName == 'mouseMove') {
                console.log("received-> " + message.content.attributeStringValue )
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
        console.log("self for handle"+self);

        // for single player
        console.log("clicked");
        console.log(event);
        $("#gameColumn").children().hide();

        // Show CountDown hovering over gameGrid and result table
        $("#countDownModal").modal("show");
        $('.modal-backdrop').appendTo('#gameColumn');
        $("#gameColumn").addClass("after_modal_appended");
        $(".whack_grid").show();
        $("#informationColumn").children().show();

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

    handleCellOnClick(event) {
        console.log("clickEvent: " + event);
        var cell = parseInt($(event.target).data("cell-reference-index"));

        this.numberOfClicks++;
        // broadcast mouse click
        this.pogsPlugin.saveCompletedTaskAttribute('clickInCell',
            "", 0.0,
            cell, false);

        if(!($("#whack_cell"+cell).hasClass("clicked"))) {

            if($("#whack_cell"+cell).hasClass("hasMole")){
                var newScore = parseInt($("#score").text()) + 1;
                $("#score").text(newScore);
            }

            $("#whack_cell" + cell).css('background-color', this.teammates[this.subjectId].color);
            $("#whack_cell" + cell).addClass('clicked');

            setTimeout(function () {
                $("#whack_cell" + cell).css('background-color', 'white');
                $("#whack_cell" + cell).removeClass('clicked');
            }, 1000); // TODO: change back to 500 after testing
        }
    }

    handleMouseMove(event) {
        var offset = $('#wackamoleContainer').offset();
        var x = event.pageX - offset.left;
        var y = event.pageY - offset.top;
        // broadcast mouse move
        this.pogsPlugin.saveCompletedTaskAttribute('mouseMove',
            x + ":" + y, 0.0,
            0, false);
        console.log("mouse move event" + x + ":" + y );
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
        }, 1000);
    }

    molePopUp() {
        var self = this;
        var roundEndTime = new Date().getTime() + 60000;
        var lastPopUpCell = -1;

        var x = setInterval(function () {
            // Time Left
            var now = new Date().getTime();
            var distance = roundEndTime - now;
            var seconds = Math.floor(distance / 1000);
            document.getElementById("roundTimeRemain").innerHTML = seconds + "s";

            // Make mole appear

            // remove last mole
            if(lastPopUpCell >= 0){
                $("#whack_cell"+lastPopUpCell).removeClass("hasMole");
                $("#whack_cell"+lastPopUpCell).find('.fa-optin-monster').remove();
            }
            // add new mole
            var randomCell = Math.floor(Math.random()*100);
            $("#whack_cell"+randomCell).addClass("hasMole");
            $("#whack_cell"+randomCell).append('<i class="fa fa-optin-monster" data-cell-reference-index="'+randomCell+'"></i>');
            lastPopUpCell = randomCell;

            // If the count down is over, write some text
            if (distance < 0) {
                clearInterval(x);
                document.getElementById("roundTimeRemain").innerHTML = "GameOver";

                $("#whack_cell"+lastPopUpCell).removeClass("hasMole");
                $("#whack_cell"+lastPopUpCell).find('.fa-optin-monster').remove();
            }
        }, 1000); // TODO: change back to 500 after testing
    }

    changeCursorColor(colorInHex){
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
        $('#wackamoleContainer').css('cursor', 'url('+dataURL+'), auto');
        $('.whack_gametable-cell').css('cursor', 'url('+dataURL+'), auto');
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