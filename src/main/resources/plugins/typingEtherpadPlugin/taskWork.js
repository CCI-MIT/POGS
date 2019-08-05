class Etherpad {
    constructor(pogsPlugin) {
        this._pogsPlugin = pogsPlugin;
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

        console.log("sessionID "  + sessionId);
        console.log("padID " + padId);

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
        let etherpadAddress = "http://etherpad.pogs-main.mit.edu/"
        if(window.location.href.indexOf("localhost")!=-1){
            etherpadAddress = "http://localhost:9001/p/"
        }
        let iframe_src = etherpadAddress + padId + "?showControls=false&showLineNumbers=false&showChat=false&userColor="+currentUserColor;

        $("#etherpadArea").append('<iframe src="'+iframe_src+'" frameborder="0" style="position:relative;width:100%;height:100%;"></iframe>');
    }
}

function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
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

var typingPlugin = pogs.createPlugin('typingPluginEtherpad',function(){


    var etherpadRef = new Etherpad(this);
    // get config attributes from task plugin
    var padId = this.getCompletedTaskStringAttribute("padID");

    etherpadRef.setupPad(padId);


},function(){
    eraseCookie("sessionID");
    console.log("Erasing the cookie");

});

//<iframe id="etherpad-main" src="{{ iframe_src }}"></iframe>
//iframe_src = completed_task.etherpad_workspace_url + \
//                 "?showControls=true&showLineNumbers=false" + \
//                 ("&grid=true" if completed_task.task.task_type == 'G' else "") + \
//                 ("&showChat=true" if completed_task.task.chat_enabled else "")