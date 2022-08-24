class Etherpad {
    constructor(pogsPlugin) {
        this._pogsPlugin = pogsPlugin;
    }
    setupTextIfAny(){
        let dict = this._pogsPlugin.pogsRef.getDictionary();
        if(dict){
            if(dict.hasGroundTruth){
                let dictEntry = dict.dictionaryEntries[0];
                if(dictEntry) {
                    this._pogsPlugin.pogsRef.getDictionaryEntry(dictEntry,function(ret){

                        $(".information").append('<div id="canvasText" style="margin-left: -15px"></div>');


                        let texts = [{textContent:atob(ret.entryValue),
                            backgroundColor:"#ffffff", fontColor: "#000000"}];
                        this.canvasImage = new CanvasTextToImage(texts,"canvasText", 318);


                    }.bind(this));
                }
            }
        }
    }
    setupPad(padId){
        //1 - get sessionID for this pad
        const subjectId = this._pogsPlugin.getSubjectId();
        const sessionIDAtt = this._pogsPlugin.getTeammateAttribute(subjectId,
            "ETHERPAD_SESSION_ID");
        let sessionId = null;
        if(sessionIDAtt){
            sessionId = sessionIDAtt.stringValue;
        }

        //2 - set COOKIE according to the DOMAIN.

        //console.log("sessionID "  + sessionId);
        //console.log("padID " + padId);

        setCookie("sessionID",sessionId, 1);
        /*
        resp.set_cookie( "sessionID"

            , value=subject.etherpad_session_id
            , path="/"
            , domain=MCI_DOMAIN
        )
        */
        //3 - get the current user's color.
        const colorVariable = this._pogsPlugin.getTeammateAttribute(subjectId,
        "SUBJECT_DEFAULT_BACKGROUND_COLOR");
        let currentUserColor = "#000000";
        if(colorVariable!=null){
            currentUserColor = colorVariable.stringValue;
        }
        let etherpadAddress = "https://etherpad.pogs.info/p/"
        if(window.location.href.indexOf("localhost")!=-1){
            etherpadAddress = "http://localhost:9001/p/"
        }

        let iframe_src = etherpadAddress + padId + "?sessionID="+sessionId+"&showControls=false&showLineNumbers=false&showChat=false&userColor="+currentUserColor;

        $("#etherpadArea").append('<iframe src="'+iframe_src+'" frameborder="0" style="position:relative;width:100%;height:100%;"></iframe>');

        this.setupTextIfAny();
    }
}

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
        document.cookie = name + "=" + (value || "") + expires + "; domain=pogs.info ; path=/";
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
    if(window.location.href.indexOf("localhost")!=-1) {
        document.cookie = name + "=;Max-Age=-99999999; path=/";
    } else {
        document.cookie = name + "=;Max-Age=-99999999; domain=pogs.info ; path=/";
    }
}

var typingPlugin = pogs.createPlugin('typingPluginEtherpad',function(){


    var etherpadRef = new Etherpad(this);
    // get config attributes from task plugin
    var padId = this.getCompletedTaskStringAttribute("padID");

    etherpadRef.setupPad(padId);


},function(){
    eraseCookie("sessionID");
    //console.log("Erasing the cookie");

});



//<iframe id="etherpad-main" src="{{ iframe_src }}"></iframe>
//iframe_src = completed_task.etherpad_workspace_url + \
//                 "?showControls=true&showLineNumbers=false" + \
//                 ("&grid=true" if completed_task.task.task_type == 'G' else "") + \
//                 ("&showChat=true" if completed_task.task.chat_enabled else "")