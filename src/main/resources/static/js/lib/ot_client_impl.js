// Requires loglevel, fast-diff, ot.js and ot_client.js, ot_input_watcher.js

ot.AbstractOtClient = (function() {
    // Imports
    let Operation = ot.Operation;
    let Client = ot.Client;
    let Component = ot.Component;
    let InputWatcher = ot.InputWatcher;


    /**
     * A convenience implementation that takes care of everything except for communication.
     *
     * The subclass needs to do two things:
     * 1. invoke the receiveOperation(operationJson) method whenever a new operation is received
     *    from the server
     * 2. implement the sendOperation(operation) method to send an operation to the server
     */
    class AbstractOtClient extends Client {
        constructor(padId, clientId, padElementId) {
            super(padId, clientId);
            this._authorship = [];

            this._contentWatcher = new InputWatcher(padElementId,
                function (event, difference) {
                    let components = [];
                    for (let i = 0; i < difference.length; i++) {
                        components.push(convertDiffToComponent(difference[i]))
                    }
                    let operation = this.createOperation(components);
                    // noinspection JSPotentiallyInvalidUsageOfClassThis
                    this.processClientOperation(operation);
                }.bind(this),
                function (currentText) {
                    let html = '';
                    for (let i = 0; i < currentText.length; i++) {
                        let currentAuthorId = this._authorship[i];
                        let isSelfAuthor = currentAuthorId === this._clientId;
                        if (i === 0 || this._authorship[i - 1] !== currentAuthorId) {
                            if (!isSelfAuthor) {
                                html += `<span data-author="${currentAuthorId}">`
                            }
                        }
                        html += currentText[i];
                        if (i === currentText.length - 1 || this._authorship[i + 1]
                            !== currentAuthorId) {
                            if (!isSelfAuthor) {
                                html += '</span>'
                            }
                        }
                    }
                    return html;
                }.bind(this));
        }

        processClientOperation(operation) {
            super.processClientOperation(operation);
            this._updateAuthorship(operation);
            this._contentWatcher.updateMirrorContent();
        }

        /**
         * Processes an incoming message.
         *
         * The message must be an object that can be converted to an Operation object.
         * The implementing class must ensure that this method is called every time a message
         * is received.
         *
         * @param operationJson An object that can be parsed as Operation
         */
        receiveOperation(operationJson) {
            let operation = Operation.fromJSON(operationJson);
            if (log.getLevel() <= log.levels.DEBUG) {
                log.debug("Received operation: " + JSON.stringify(operation));
            }
            this.processServerOperation(operation);
        }

        applyOperation(operation) {
            let currentContent = this._contentWatcher.text();
            this._updateAuthorship(operation);
            this._contentWatcher.text(operation.apply(currentContent), operation);
        }

        _updateAuthorship(operation) {
            let components = operation.components;

            let authorshipCursor = 0;
            for (let i = 0; i < components.length; i++) {
                let component = components[i];
                if (component.isRetain()) {
                    authorshipCursor += component.retain;
                } else if (component.isInsert()) {
                    //insert this operation's clientId component.size() times
                    for (let j = 0; j < component.size(); j++) {
                        this._authorship.splice(authorshipCursor, 0, operation.metaData.authorId);
                    }
                } else if (component.isDelete()) {
                    //delete authorship information
                    this._authorship.splice(authorshipCursor, component.size());
                }
            }
        }
    }

    function convertDiffToComponent(differenceObj) {
        switch (differenceObj[0]) {
            case fastDiff.INSERT:
                return new Component('INSERT', 0, differenceObj[1]);
            case fastDiff.DELETE:
                return new Component('DELETE', 0, differenceObj[1]);
            case fastDiff.EQUAL:
                return new Component('RETAIN', differenceObj[1].length, '');
            default:
                throw "Shouldn't happen";
        }
    }

    return AbstractOtClient;
})();
