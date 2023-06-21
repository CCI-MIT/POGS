class TrafficJunctionTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        let subject = this.pogsPlugin.getSubjectByExternalId(this.pogsPlugin.getSubjectId());
        let completedTask = this.pogsPlugin.pogsRef.completedTaskId;
        let workerId = "";
        let assignmentId = "";
        let hitId = "";


        for (var j = 0; j < subject.attributes.length; j++) {
            if (subject.attributes[j].attributeName == "workerId") {
                workerId = subject.attributes[j].stringValue;
            }
            if (subject.attributes[j].attributeName == "assignmentId") {
                assignmentId = subject.attributes[j].stringValue;
            }
            if (subject.attributes[j].attributeName == "hitId") {
                hitId = subject.attributes[j].stringValue;
            }
        }

        this.extraData = "/" + completedTask + "/" + subject;//"?workerId=" +workerId + "&assignmentId=" + assignmentId + "&hitId=" + hitId;

    }
    setupGrid(gridBluePrintz){

        var gridBluePrint = JSON.parse(gridBluePrintz);
        //iframeWidget
        let link = gridBluePrint.qualtrixLink + this.extraData;

        let iframa = $("<iframe>",{
            src:link , width: '100%', height: '100%'
        })
        $("#iframeWidget").append(iframa);

    }

    broadcastReceived(message){

    }
}

var qualtrixPlugin = pogs.createPlugin('trafficJunctionTaskPlugin',function(){
//iframeWidget

    var trafficJunctionTask = new TrafficJunctionTask(this);
    // get config attributes from task plugin
    trafficJunctionTask.setupGrid(this.getStringAttribute("gridBluePrint"));
    this.subscribeTaskAttributeBroadcast(trafficJunctionTask.broadcastReceived.bind(trafficJunctionTask))

});