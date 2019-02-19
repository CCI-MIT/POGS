var jeopardyPlugin = pogs.createPlugin('jeopardyTaskPlugin',function(){

    console.info("Jeopardy Plugin Loaded");

    var jeopardy = new Jeopardy(this);
    // get config attributes from task plugin
    jeopardy.setupJeopardy(this.getStringAttribute("questionBluePrint"),this.getStringAttribute("jeopardyBluePrint"));
    this.subscribeTaskAttributeBroadcast(jeopardy.broadcastReceived.bind(jeopardy))

});