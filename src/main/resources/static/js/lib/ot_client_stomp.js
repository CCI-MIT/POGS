// Requires loglevel, fast-diff, ot.js and ot_client.js

[ot.AbstractOtClient, ot.StompOtClient] = (function() {
    // Imports
    let Client = ot.Client;
    let Component = ot.Component;
    let Operation = ot.Operation;


    const LAST_VALUE_ATTRIBUTE = 'InputWatcher__last-value';

    class InputWatcher {
        constructor(selector, onInputChange) {
            this._$input = $(selector);

            saveLastInputValue(this._$input);
            this._$input.on("input", function(event) {
                let lastValue = this._$input.data(LAST_VALUE_ATTRIBUTE);
                let difference = fastDiff.diff(lastValue, this._$input.val());
                onInputChange(event, difference);
                saveLastInputValue(this._$input);
            }.bind(this));
        }

        /**
         * Programmatically update the content of the input.
         *
         * Programmatic updates do not fire the input event, so the update needs to
         * manually set the data attribute correctly.
         *
         * @param newContent
         */
        set value(newContent) {
            this._$input.val(newContent);
            saveLastInputValue(this._$input);
        }

        get value() {
            return this._$input.val();
        }
    }

    function saveLastInputValue($input) {
        $input.data(LAST_VALUE_ATTRIBUTE, $input.val());
    }

    /**
     * A convenience implementation that takes care of everything except for communication.
     *
     * The subclass needs to do two things:
     * 1. invoke the receiveOperation(operationJson) method whenever a new operation is received
     *    from the server
     * 2. implement the sendOperation(operation) method to send an operation to the server
     */
    class AbstractOtClient extends Client {
        constructor (padId, clientId, padSelector) {
            super(padId, clientId);

            this._inputWatcher = new InputWatcher(padSelector, function(event, difference) {
                let components = [];
                for (let i = 0; i < difference.length; i++) {
                    components.push(convertDiffToComponent(difference[i]))
                }
                let operation = this.createOperation(components);
                this.processClientOperation(operation);
            }.bind(this));
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
            let currentContent = this._inputWatcher.value;
            this._inputWatcher.value = operation.apply(currentContent);
        }
    }

    class StompOtClient extends AbstractOtClient {
        constructor (padId, clientId, padSelector) {
            super(padId, clientId, padSelector);
            log.info(`Initializing StompOtClient for pad ${padId} as client ${clientId}`);

            stomp.subscribe(`/topic/ot/pad/${this.padId}/operations`, function(payload) {
                let operationJson = JSON.parse(payload.body);
                this.receiveOperation(operationJson);
            }.bind(this));
        }

        sendOperation(operation) {
            if (log.getLevel() <= log.levels.DEBUG) {
                log.debug("Sending operation: " + JSON.stringify(operation));
            }
            stomp.client.send(`/pogsapp/ot/pad/${this.padId}/operations/submit`,
                {}, JSON.stringify(operation));
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
    return [AbstractOtClient, StompOtClient];
})();
