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
        }

        $("#addPlayer").click(function () { //Setup add question button
            this.addPlayer(1,1,0);
        }.bind(this));

    }

    addPlayer(moleAppearTime, maxMoleNum, clickDelay){
        this.numOfPlayer++;
        var str =
            '<div class="container" id="player'+ this.numOfPlayer +'">\n' +
            '            <div class="form-group row">\n' +
            '                <bold class="col-sm-2 col-form-label">Player '+ this.numOfPlayer +'</bold>\n' +
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="moleAppearTime'+ this.numOfPlayer +'">Mole appear time: </label>\n' +
            '                <input type="number" min="0.01" step="0.01" class="form-control col-sm-8" id="moleAppearTime'+ this.numOfPlayer +'" placeholder="Enter seconds" value="'+ moleAppearTime +'">\n' +
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="maxMoleNum'+ this.numOfPlayer +'">Max mole number: </label>\n' +
            '                <input type="number" min="1" max="100" step="1" class="form-control col-sm-8" id="maxMoleNum'+ this.numOfPlayer +'" placeholder="Enter mole number" value="'+ maxMoleNum +'">\n' +
            '            </div>\n' +
            '            <div class="form-group row">\n' +
            '                <label class="col-sm-2 col-form-label" for="clickDelay'+ this.numOfPlayer +'">Click delay: </label>\n' +
            '                <input type="number" min="0" step="0.0001" class="form-control col-sm-8" id="clickDelay'+ this.numOfPlayer +'" placeholder="Enter seconds" value="'+ clickDelay +'">\n' +
            '            </div>\n' +
            '        </div> <hr>';
        $("#whack").append(str);
    }


    setupHtmlFromAttributeString(bluePrint){
        var self = this;
        $.each(bluePrint, function(i, e){
            self.addPlayer(e.moleAppearTime, e.maxMoleNum, e.clickDelay)
        });

    }

    setupAttributesFromHtml(){
        var bluePrint = [];
        for(var i = 1; i < this.numOfPlayer + 1; i++){
            var playerConfig = {};
            playerConfig["player"] = i.toString();
            playerConfig["moleAppearTime"] = $("#moleAppearTime"+i).val();
            playerConfig["maxMoleNum"] = $("#maxMoleNum"+i).val();
            playerConfig["clickDelay"] = $("#clickDelay"+i).val();
            bluePrint.push(playerConfig);
        }
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

