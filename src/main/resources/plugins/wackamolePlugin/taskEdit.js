class WhackTaskEdit {

    constructor(){
        this.numOfPlayer = 0;
    }

    init(taskConfigId, currentAttributes) {
        console.log("taskConfigId: " + taskConfigId);
        console.log("currentAttributes: " + currentAttributes)

        this.taskConfigId = taskConfigId;

        var whackBluePrint = null;
        for (var i = 0; i < currentAttributes.length; i++) {
            if (currentAttributes[i].attributeName == "whackBluePrint") {
                whackBluePrint = $.parseJSON(currentAttributes[i].stringValue);
                console.log(whackBluePrint);
            }
        }

        if (whackBluePrint != null) {
            this.setupHtmlFromAttributeString(whackBluePrint);
        } else {
            this.addPlayer(1, 1,1,0);
        }

        $("#addPlayer").click(function () { //Setup add question button
            this.addPlayer(1, 1,1,0);
        }.bind(this));

    }

    addPlayer(numberOfRounds, moleAppearTime, maxMoleNum, clickDelay){
        var self = this;
        this.numOfPlayer++;
        var str =
            '<div class="container player" id="player'+ this.numOfPlayer +'">\n' +
            '            <div class="form-group row">\n' +
            '                <bold class="col-sm-2 col-form-label">Configuration</bold>\n' + //'+ this.numOfPlayer +'
            //'                <div class="col-sm-8">' +
            //'                   <button type="button" class="btn btn-danger btn-sm pull-right" id="removePlayer' + this.numOfPlayer + '">remove</button>' +
            //'                </div>' +
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="moleAppearTime'+ this.numOfPlayer +'">Number of rounds: </label>\n' +
            '                <input type="number" min="1" step="1" class="form-control col-sm-8" id="numberOfRounds'+ this.numOfPlayer +'" placeholder="Enter the number of rounds" value="'+ numberOfRounds +'">\n' +
            '                <small id="" class="form-text text-muted">Total amount of rounds subjecs will be able to play</small>'+
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="moleAppearTime'+ this.numOfPlayer +'">Mole appear time: </label>\n' +
            '                <input type="number" min="0.01" step="0.01" class="form-control col-sm-8" id="moleAppearTime'+ this.numOfPlayer +'" placeholder="Enter seconds" value="'+ moleAppearTime +'">\n' +
            '                <small id="" class="form-text text-muted">Total amount of seconds a mole will be visible to subjects</small>'+
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="maxMoleNum'+ this.numOfPlayer +'">Max mole number: </label>\n' +
            '                <input type="number" min="1" max="100" step="1" class="form-control col-sm-8" id="maxMoleNum'+ this.numOfPlayer +'" placeholder="Enter mole number" value="'+ maxMoleNum +'">\n' +
            '                <small id="" class="form-text text-muted">Number of moles to appear at the same time</small>'+
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="clickDelay'+ this.numOfPlayer +'">Click delay: </label>\n' +
            '                <input type="number" min="0" step="0.0001" class="form-control col-sm-8" id="clickDelay'+ this.numOfPlayer +'" placeholder="Enter seconds" value="'+ clickDelay +'">\n' +
            '                <small id="" class="form-text text-muted">Number of seconds that the system will count as valid clicks after a mole disappeared</small>'+
            '            </div> <hr>\n' +
            '        </div>';
        $("#whack").append(str);

        $('#removePlayer' + this.numOfPlayer).click(function(){
            var playerNum = $(this).attr("id").match(/\d+/);
            console.log("clicked: " + playerNum);
            $("#player" + playerNum).remove();
        });
    }


    setupHtmlFromAttributeString(bluePrint){
        var self = this;
        $.each(bluePrint, function(i, e){
            self.addPlayer(e.numberOfRounds, e.moleAppearTime, e.maxMoleNum, e.clickDelay)
        });

    }

    setupAttributesFromHtml(){
        var bluePrint = [];
        $.each($('.player'), function(i,e){
            var playerConfig = {};
            var playerId = e.id.match(/\d+/);
            playerConfig["player"] = i.toString();
            playerConfig["numberOfRounds"] = $("#numberOfRounds"+playerId).val();
            playerConfig["moleAppearTime"] = $("#moleAppearTime"+playerId).val();
            playerConfig["maxMoleNum"] = $("#maxMoleNum"+playerId).val();
            playerConfig["clickDelay"] = $("#clickDelay"+playerId).val();
            bluePrint.push(playerConfig);
        });
        return  {bluePrint: JSON.stringify(bluePrint)};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all fields for non empty fields


        var attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("whackBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
    }

}
pogsTaskConfigEditor.register(new WhackTaskEdit());

