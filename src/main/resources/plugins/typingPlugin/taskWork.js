class PogsOtClient extends AbstractOtClient {
    constructor(pogsPlugin, padSelector) {
        super(pogsPlugin.getCompletedTaskId(), pogsPlugin.getSubjectId(), padSelector);
        log.info(`Initializing PogsOtClient for pad ${padId} as client ${clientId}`);

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
        this._pogsPluin.sendOperation(operation);
    }
}

const typingPlugin = pogs.createPlugin('typingPlugin', function() {
    const otClient = new PogsOtClient(this);
});

