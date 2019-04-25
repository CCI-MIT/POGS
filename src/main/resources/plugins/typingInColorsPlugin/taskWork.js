class PogsOtColorsClient extends ot.AbstractOtClient {
    constructor(pogsPlugin, padElementId, blueprint) {
        const padId = "pad-for-task-" + pogsPlugin.getCompletedTaskId();
        const clientId = "subject-" + pogsPlugin.getSubjectId();
        super(padId, clientId, padElementId);
        this.subjectsColors = [];
        this.availableColors = [];
        this.uniqueColors = {};
        this.textSections = [];
        this.textSectionsColors = [];
        log.info(`Initializing PogsOtClient for pad ${padId} as client ${clientId}`);
        $("#padContent").attr("disabled", "disabled");
        for(let k = 0; k < blueprint.length; k++) {

            if(!this.uniqueColors[blueprint[k].color]) {
                this.uniqueColors[blueprint[k].color] = 1;
                this.availableColors.push(blueprint[k].color);
            }
            this.textSections.push(blueprint[k].text);
            this.textSectionsColors.push(blueprint[k].color);
        }
        this.setupText();
        for(let k = 0; k < this.availableColors.length; k++) {
            $('#colorPickerAndAssigner')
                .append('<button type="button" class="btn btn-lg" data-color-index="'+k+'" style="margin:10px;background-color: '+
                        this.availableColors[k]+';color:'+generateFontColorBasedOnBackgroundColor(this.availableColors[k])+'" >Choose this color</button>');
        }
        $("button").click(function (event) {
            var r = confirm("Are you sure you want to select this color? You won't be able to change colors!");
            if (r == true) {
                this.sendAssignedColorToSelf($(event.target).data("color-index"));
            }
        }.bind(this));

        this._pogsPlugin = pogsPlugin;

        pogsPlugin.subscribeTaskAttributeBroadcast(function(message) {
            log.debug('Task attribute received: ' + message.content.attributeName);
            let attrName = message.content.attributeName;
            if (attrName == "operation") {
                log.debug('Operation received: ' + message.content.attributeStringValue);
                this.receiveOperation(JSON.parse(message.content.attributeStringValue));
            }
            if(attrName.indexOf("subjectAssignedToColor")!= -1){
                let isCurrentSubject = false;
                let sub = this._pogsPlugin.getSubjectByExternalId(message.sender);

                this.handleAssignedColor(message.content.attributeIntegerValue, sub);
            }
        }.bind(this));
        $(window).bind('beforeunload', this.beforeLeave.bind(this));
    }
    setupText(){

        let inputsTxt = this.textSections;
        let inputsColors = this.textSectionsColors;

        let container = $('<div>',{
            id: 'typingInColorstaskText',
            class: 'rol col-12'
        });

        let saida = "";
        saida+="<style>\n";
        saida+="#typingInColorstaskText {\n";
        saida+="    -moz-user-select: none;\n";
        saida+="-webkit-user-select: none;\n";
        saida+="-ms-user-select: none;\n";
        saida+="-o-user-select: none;\n";
        saida+="user-select: none;\n";
        saida+="}\n";
        saida+="</style>\n";
        $(".information").append(saida);
        $(".information").append(container);

        let body = "";
        let lastColor = "";
        for(let k=0; k < inputsTxt.length; k++) {
            let newColor = inputsColors[k];
            if(newColor == ""){
                newColor = "#000000";
            }
            if(newColor!=lastColor) {
                if(lastColor!=""){
                    body += "</span>";
                }
                body += "<span style='background-color:"+newColor+";color:"+generateFontColorBasedOnBackgroundColor(newColor)+"'>"
                lastColor = newColor;
            }
            body += replaceNewLinesForBrs(inputsTxt[k]);
        }
        body +="</span>";
        $("#typingInColorstaskText").html(body);
    }
    beforeLeave(){
        this._pogsPlugin.saveCompletedTaskAttribute('fullTextAuthorship',
                                                    $('#padContent_mirror').html(), 0, 0,
                                                    true, '');

        //this.subjectsColors[i].externalId

        let team = this._pogsPlugin.getTeammates()
        let canvas = $('#padContent_mirror');
        for(var i = 0; i < team.length; i ++) {
            if (team[i].externalId) {

                let entries = canvas.find("span[data-author='subject-"+team[i].externalId+"']");
                let text = "";
                for(let i = 0 ; i < entries.length; i ++){
                    text += $(entries[i]).html();
                }
                this._pogsPlugin.saveCompletedTaskAttribute(
                    'fullTextAuthor_' + team[i].externalId,
                    text, 0, 0,
                    true, '');

            }
        }
    }
    handleAssignedColor(colorIndex, subject){
        let allColorButtons = $("#colorPickerAndAssigner button");
        let isCurrentSubject = (subject.externalId == this._pogsPlugin.getSubjectId())?(true):(false);
        for(let k = 0 ; k < allColorButtons.length; k++) {
            if($(allColorButtons[k]).data("color-index") == colorIndex) {
                if(isCurrentSubject) {
                    $(allColorButtons[k]).text("You chose this color!");
                    $("#padContent").attr("disabled", null);
                    $("#colorPickerAndAssigner button").addClass("disabled");
                    $("#colorPickerAndAssigner button").unbind();
                } else {
                    $(allColorButtons[k]).text( subject.displayName+" chose this color!")
                    $(allColorButtons[k]).addClass("disabled");
                    $(allColorButtons[k]).unbind();
                }
                this.subjectsColors.push({externalId: subject.externalId, backgroundColor:this.availableColors[colorIndex],
                                             fontColor: generateFontColorBasedOnBackgroundColor(this.availableColors[colorIndex])});

            }
        }
        this.updateSubjectColors();
    }
    updateSubjectColors(){
        $("head style").remove();
        let rule = "";
        for (let i = 0; i < this.subjectsColors.length; i++) {
            rule += `.${this.subjectsColors[i].externalId}_color, `
                    + `[data-author=subject-${this.subjectsColors[i].externalId}] {`
                    + `background-color: ${this.subjectsColors[i].backgroundColor};`
                    + `color: ${this.subjectsColors[i].fontColor};`
                    + '}';
            rule += `.${this.subjectsColors[i].externalId}_activecolor {`
                    + `color: ${this.subjectsColors[i].backgroundColor};`
                    + '}';

        }
        var css = document.createElement('style'); // Creates <style></style>
        css.type = 'text/css'; // Specifies the type
        if (css.styleSheet) css.styleSheet.cssText = rule; // Support for IE
        else css.appendChild(document.createTextNode(rule)); // Support for the rest
        document.getElementsByTagName("head")[0].appendChild(css); // Specifies where to place the css

        if(this.subjectsColors.length == this.availableColors.length) {
            $("button").attr("disabled", true);
        }

    }
    sendAssignedColorToSelf(colorIndex){
        this._pogsPlugin.saveCompletedTaskAttribute('subjectAssignedToColor_'+colorIndex,
                                                    this._pogsPlugin.getSubjectId(), 0.0,
                                                    colorIndex, true, colorIndex);
    }
    sendOperation(operation) {
        if (log.getLevel() <= log.levels.DEBUG) {
            log.debug("Sending operation: " + JSON.stringify(operation));
        }
        this._pogsPlugin.sendOperation(operation);
    }
}

const typingPlugin = pogs.createPlugin('typingPlugin', function() {



    const otClient = new PogsOtColorsClient(this, 'padContent',$.parseJSON(this.getStringAttribute("gridBluePrint")));
});

function replaceNewLinesForBrs(st){
    if(st === undefined) return "";
    return st.replace(/\n/g,"<br/>");
}
function generateFontColorBasedOnBackgroundColor(colorz) {
    let color = parseColor(colorz);
    let r = color[0];
    let g = color[1];
    let b = color[2];
    let yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
    return (yiq >= 128) ? "#000000" : "#FFFFFF";
}
function parseColor(input) {
    let  m = input.match(/^#([0-9a-f]{6})$/i)[1];
    if( m) {
        return [
            parseInt(m.substr(0,2),16),
            parseInt(m.substr(2,2),16),
            parseInt(m.substr(4,2),16)
        ];
    }
}