// Requires loglevel, fast-diff, ot.js and ot_client.js

ot.StompOtClient = (function() {
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

    class StompOtClient extends Client {
        constructor (padId, clientId, padSelector) {
            super(padId, clientId);
            log.info(`Initializing StompOtClient for pad ${padId} as client ${clientId}`);

            this._inputWatcher = new InputWatcher(padSelector, function(event, difference) {
                let components = [];
                for (let i = 0; i < difference.length; i++) {
                    components.push(convertDiffToComponent(difference[i]))
                }
                let operation = this.createOperation(components);
                this.processClientOperation(operation);
            }.bind(this));

            stomp.subscribe(`/topic/ot/pad/${this.padId}/operations`, function(payload) {
                let json = JSON.parse(payload.body);
                if (Array.isArray(json)) {
                    if (json.length > 0) {
                        log.info(`Catching up to ${json.length} previous operations`);
                        let previousOperations = json;
                        for (let i = 0; i < previousOperations.length; i++) {
                            let operation = previousOperations[i];
                            this.processServerOperation(operation);
                        }
                    }
                } else {
                    let operation = Operation.fromJSON(json);
                    this.processServerOperation(operation);
                }
            }.bind(this));
        }

        sendOperation(operation) {
            if (log.getLevel() >= log.levels.DEBUG) {
                log.debug("Sending operation: " + JSON.stringify(operation));
            }
            stomp.client.send(`/pogsapp/ot/pad/${this.padId}/operations/submit`,
                {}, JSON.stringify(operation));
        }

        applyOperation(operation) {
            if (log.getLevel() >= log.levels.DEBUG) {
                log.debug("Applying operation: " + JSON.stringify(operation));
            }
            let currentContent = this._inputWatcher.value;
            this._inputWatcher.value = operation.apply(currentContent);
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
    return StompOtClient;
})();
