class PogsOtEquationClient extends ot.AbstractOtClient {
    constructor(pogsPlugin, padElementId, bluePrint) {
        const padId = "pad-for-task-" + pogsPlugin.getCompletedTaskId();
        const clientId = "subject-" + pogsPlugin.getSubjectId();
        super(padId, clientId, padElementId);
        log.info(`Initializing PogsOtClient for pad ${padId} as client ${clientId}`);

        this._pogsPlugin = pogsPlugin;

        if(bluePrint.taskText) {
            $("#taskText").html(bluePrint.taskText);
        }

        pogsPlugin.subscribeTaskAttributeBroadcast(function(message) {
            log.debug('Task attribute received: ' + message.content.attributeName);
            let attrName = message.content.attributeName;
            if (attrName == "operation") {
                log.debug('Operation received: ' + message.content.attributeStringValue);
                this.receiveOperation(JSON.parse(message.content.attributeStringValue));
            }
        }.bind(this));
    }

    sendOperation(operation) {
        if (log.getLevel() <= log.levels.DEBUG) {
            log.debug("Sending operation: " + JSON.stringify(operation));
        }
        this._pogsPlugin.sendOperation(operation);
    }
}

const typingPlugin = pogs.createPlugin('typingPlugin', function() {
    const otClient = new PogsOtEquationClient(this, 'padContent',$.parseJSON(this.getStringAttribute("gridBluePrint")));
});

