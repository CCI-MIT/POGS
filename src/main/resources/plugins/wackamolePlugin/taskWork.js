class Wackamole {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    setupGrid(title){
        $("#wackamoleTitle").text(title);
    }

}




var wackamolePlugin = pogs.createPlugin('wackamoleTaskPlugin', function(){
    console.log("Wack-a-mole Plugin Loaded");
    var wackamole = new Wackamole(this);
    wackamole.setupGrid("Wack-a-mole")
});