class EtherpadWithColors {
    constructor(pogsPlugin,blueprint, padID) {
        this._pogsPlugin = pogsPlugin;

        this.padID = padID;
        this.subjectsColors = [];
        this.availableColors = [];
        this.uniqueColors = {};
        this.textSections = [];
        this.textSectionsColors = [];

        //$("#padContent").attr("disabled", "disabled");
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
        this._pogsPlugin.pogsRef.subscribe('onUnload', this.beforeLeave.bind(this));
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
        $("#padContent").attr("disabled","disabled");
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
        console.log('Before leave');
    }
    handleAssignedColor(colorIndex, subject){
        let allColorButtons = $("#colorPickerAndAssigner button");
        let isCurrentSubject = (subject.externalId == this._pogsPlugin.getSubjectId())?(true):(false);
        for(let k = 0 ; k < allColorButtons.length; k++) {
            if($(allColorButtons[k]).data("color-index") == colorIndex) {
                if(isCurrentSubject) {
                    $(allColorButtons[k]).text("You chose this color!");
                    //$("#padContent").attr("disabled", null);
                    //TODO INSERT IFRAME. with selected COLOR.
                    $("#colorPickerAndAssigner button").addClass("disabled");
                    $("#colorPickerAndAssigner button").unbind();
                    this.setupPad(this.padID,this.availableColors[colorIndex]);
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

    setupPad(padId,currentUserColor){
        //1 - get sessionID for this pad
        const subjectId = this._pogsPlugin.getSubjectId();
        const sessionIDAtt = this._pogsPlugin.getTeammateAttribute(subjectId,
                                                                   "ETHERPAD_SESSION_ID");
        let sessionId = null;
        if(sessionIDAtt){
            sessionId = sessionIDAtt.stringValue;
        }

        //2 - set COOKIE according to the DOMAIN.

        console.log("sessionID "  + sessionId);
        console.log("padID " + padId);

        setCookie("sessionID",sessionId, 1);

        let etherpadAddress = "https://etherpad.pogs.info/p/"
        if(window.location.href.indexOf("localhost")!=-1){
            etherpadAddress = "http://localhost:9001/p/"
        }
        let iframe_src = etherpadAddress + padId + "?showControls=false&showLineNumbers=false&showChat=false&userColor="+currentUserColor;

        $("#etherpadArea").append('<iframe src="'+iframe_src+'" frameborder="0" style="position:relative;width:100%;height:100%;"></iframe>');
    }
}

const typingPlugin = pogs.createPlugin('typingInColorsPlugin', function() {

    const otClient = new EtherpadWithColors(this,
                                            $.parseJSON(
                                                this.getStringAttribute("gridBluePrint")),
                                            this.getCompletedTaskStringAttribute("padID"));
},
    function(){
        eraseCookie("sessionID");
        console.log("Erasing the cookie");

    });

function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    if(window.location.href.indexOf("localhost")!=-1) {
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    } else {
        document.cookie = name + "=" + (value || "") + expires + "; domain=pogs.info";
    }
}
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
function eraseCookie(name) {
    document.cookie = name+'=; Max-Age=-99999999;';
}

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