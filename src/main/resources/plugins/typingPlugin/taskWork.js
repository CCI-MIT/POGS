class PogsOtClient extends ot.AbstractOtClient {
    constructor(pogsPlugin, padElementId) {
        const padId = "pad-for-task-" + pogsPlugin.getCompletedTaskId();
        const clientId = "subject-" + pogsPlugin.getSubjectId();
        super(padId, clientId, padElementId);
        log.info(`Initializing PogsOtClient for pad ${padId} as client ${clientId}`);

        this._pogsPlugin = pogsPlugin;

        pogsPlugin.subscribeTaskAttributeBroadcast(function(message) {
            log.debug('Task attribute received: ' + message.content.attributeName);
            let attrName = message.content.attributeName;
            if (attrName == "operation") {
                log.debug('Operation received: ' + message.content.attributeStringValue);
                this.receiveOperation(JSON.parse(message.content.attributeStringValue));
            }
        }.bind(this));

        this._pogsPlugin.pogsRef.subscribe('onUnload', this.beforeLeave.bind(this));
    }
    beforeLeave(){
        console.log("Just before leave is called")
        $("#padContent").attr("disabled","disabled");
        console.log("after disabling");
        this._pogsPlugin.saveCompletedTaskAttribute('fullTextAuthorship',
                                                    $('#padContent_mirror').html(), 0, 0,
                                                    true, '');
        console.log("after Saved completed attr");
    }
    sendOperation(operation) {
        if (log.getLevel() <= log.levels.DEBUG) {
            log.debug("Sending operation: " + JSON.stringify(operation));
        }
        this._pogsPlugin.sendOperation(operation);
    }
}

const typingPlugin = pogs.createPlugin('typingPlugin', function() {
    const otClient = new PogsOtClient(this, 'padContent');
});

