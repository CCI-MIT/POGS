class Etherpad {
    constructor(pogsPlugin) {
        this._pogsPlugin = pogsPlugin;

    }
    setupPad(padId){
        let iframe_src = padId + "?showControls=false&showLineNumbers=false"

        $("#etherpadArea").append()
    }
}


var typingPlugin = pogs.createPlugin('typingPluginEtherpad',function(){


    var etherpadRef = new Etherpad(this);
    // get config attributes from task plugin
    etherpadRef.setupPad(this.getStringAttribute("padRef"));
    this.subscribeTaskAttributeBroadcast(etherpadRef.broadcastReceived.bind(etherpadRef))

});

//<iframe id="etherpad-main" src="{{ iframe_src }}"></iframe>
//iframe_src = completed_task.etherpad_workspace_url + \
//                 "?showControls=true&showLineNumbers=false" + \
//                 ("&grid=true" if completed_task.task.task_type == 'G' else "") + \
//                 ("&showChat=true" if completed_task.task.chat_enabled else "")