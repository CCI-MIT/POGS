class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        console.log(pogsPlugin);
    }
    setupGrid(title){
        var self = this;

        $("#wackamoleTitle").text(title);

        // Only show readyView at start
        $("#gameColumn").children().hide();
        $("#readyView").show();
        $("#readyBtn").on('click',self.handleReadyOnClick.bind(self));

        // Game grid appearred
        



    }

    broadcastReceived(message){
        var attrName = message.content.attributeName;
       console.log("broadcast message: " + message);
    }

    handleReadyOnClick(event){
        // for single player
        console.log("clicked");
        console.log(event);
        $("#gameColumn").children().hide();
        $(".whack_grid").show();
    }

}



var wackamolePlugin = pogs.createPlugin('wackamoleTaskPlugin', function(){
    console.log("Wack-a-mole Plugin Loaded");
    var wackamole = new Wackamole(this);
    wackamole.setupGrid("Wack-a-mole")
    this.subscribeTaskAttributeBroadcast(wackamole.broadcastReceived.bind(wackamole))

});