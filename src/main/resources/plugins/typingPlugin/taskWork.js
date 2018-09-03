class PogsOtClient extends ot.AbstractOtClient {
    constructor(pogsPlugin, padSelector) {
        super(pogsPlugin.getCompletedTaskId(), pogsPlugin.getSubjectId(), padSelector);
        log.info(`Initializing PogsOtClient for pad ${pogsPlugin.getCompletedTaskId()} as client ${pogsPlugin.getSubjectId()}`);

        this._pogsPlugin = pogsPlugin;

        pogsPlugin.subscribeTaskAttributeBroadcast(function(message) {
            let attrName = message.content.attributeName;
            if (attrName == "operation") {
                this.receiveOperation(message.content.attributeStringValue);
            }
        }.bind(this));
    }

    sendOperation(operation) {
        if (log.getLevel() >= log.levels.DEBUG) {
            log.debug("Sending operation: " + JSON.stringify(operation));
        }
        this._pogsPlugin.sendOperation(operation);
    }
}

const typingPlugin = pogs.createPlugin('typingPlugin', function() {
    const otClient = new PogsOtClient(this, '#padContent');
});

