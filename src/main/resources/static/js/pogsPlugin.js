function PogsPlugin(pluginName,initFunc, pogsRef){
    this.pluginName= pluginName;
    this.initFunc = initFunc;
    this.pogsRef = pogsRef;
}

PogsPlugin.prototype = {
    sendMessage: function (url, type, messageContent, sender, receiver, completedTaskId,
                           sessionId) {
        this.pogsRef.sendMessage(url, type, messageContent, sender, receiver, completedTaskId,
                                           sessionId);
    }
}


