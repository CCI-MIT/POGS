// Requires ot.js and supports loglevel (optional - configurable logging)

ot.Client = (function() {
    // Imports
    let Operation = ot.Operation;

    const clientStatus = {
        SYNCHRONIZED: Symbol("synchronized"),
        AWAITING_CONFIRM: Symbol("awaiting_confirm"),
        AWAITING_WITH_BUFFER: Symbol("awaiting_with_buffer")
    };

    //wrap log in function to make loglevel dependency optional
    function debug(msg) {
        if (typeof log !== 'undefined') {
            log.debug(msg);
        }
    }
    function warn(msg) {
        if (typeof log !== 'undefined') {
            log.warn(msg);
        } else {
            console.warn(msg);
        }
    }

    class ClientState {

        constructor(client) {
            this._client = client;
            this._latestKnownServerOperationId = -1;
            this._status = clientStatus.SYNCHRONIZED;
            this._outstanding = null;
            this._buffer = null;
        }

        applyClient(operation) {
            debug("Applying client operation");
            operation.parentId = this._latestKnownServerOperationId;
            switch (this._status) {
                case clientStatus.SYNCHRONIZED:
                    this._client.sendOperation(operation);
                    this._outstanding = operation;
                    this._status = clientStatus.AWAITING_CONFIRM;
                    break;
                case clientStatus.AWAITING_CONFIRM:
                    debug("Currently AWAITING_CONFIRM: buffering operation");
                    this._buffer = operation;
                    this._status = clientStatus.AWAITING_WITH_BUFFER;
                    break;
                case clientStatus.AWAITING_WITH_BUFFER:
                    debug("Currently AWAITING_WITH_BUFFER: buffering operation");
                    this._buffer = this._buffer.compose(operation);
                    break;
            }
        }

        applyServer(operation) {
            debug(`Applying operation ${operation.id} from server`);
            if (operation.id > this._latestKnownServerOperationId + 1) {
                warn(`Operation id jumped from ${this._latestKnownServerOperationId} to ${operation.id}`);
            }
            this._latestKnownServerOperationId = operation.id;
            switch (this._status) {
                case clientStatus.SYNCHRONIZED:
                    this._client.applyOperation(operation);
                    break;
                case clientStatus.AWAITING_CONFIRM:
                    debug("Currently AWAITING_CONFIRM - transforming outstanding");
                    let pair = this._outstanding.transform(operation);
                    this._outstanding = pair[0];
                    this._client.applyOperation(pair[1]);
                    break;
                case clientStatus.AWAITING_WITH_BUFFER:
                    debug("Currently AWAITING_WITH_BUFFER - transforming outstanding and buffer");
                    let pair1 = this._outstanding.transform(operation);
                    let pair2 = this._buffer.transform(pair1[1]);
                    this._outstanding = pair1[0];
                    this._buffer = pair2[0];
                    this._buffer.parentId = operation.id;
                    this._client.applyOperation(pair2[1]);
                    break;
            }
        }

        serverAck(operation) {
            debug(`Processing server ack for operation ${operation.id}`);
            this._latestKnownServerOperationId = operation.id;
            switch (this._status) {
                case clientStatus.SYNCHRONIZED:
                    // This might be desirable if the client has reloaded the page
                    // and is catching up to the current state
                    warn(`No outstanding operation ${operation.id} - assuming we re-joined and our own operation is being re-played.`);
                    try {
                        this._client.applyOperation(operation);
                    } catch (err) {
                        throw `Tried to apply server ack for operation ${operation.id} as no
                                outstanding operation was found, but failed with error ${err}`;
                    }
                    break;
                case clientStatus.AWAITING_CONFIRM:
                    debug("Currently AWAITING_CONFIRM: reverting to synchronized");
                    this._outstanding = null;
                    this._status = clientStatus.SYNCHRONIZED;
                    break;
                case clientStatus.AWAITING_WITH_BUFFER:
                    debug("Currently AWAITING_CONFIRM: sending buffer");
                    this._outstanding = this._buffer;
                    this._outstanding.parentId = operation.id;
                    this._client.sendOperation(this._outstanding);
                    this._buffer = null;
                    this._status = clientStatus.AWAITING_CONFIRM;
                    break;
            }
        }

        onReconnect() {
            switch (this._status) {
                case clientStatus.SYNCHRONIZED:
                    //nothing to resend
                    break;
                case clientStatus.AWAITING_CONFIRM:
                case clientStatus.AWAITING_WITH_BUFFER:
                    client.sendOperation(this._outstanding);
                    break;
            }
        }
    }

    class Client {

        constructor(padId, clientId) {
            this._padId = padId;
            this._clientId = clientId;
            this._clientState = new ClientState(this)
        }

        get padId() {
            return this._padId;
        }

        get clientId() {
            return this._clientId;
        }

        createOperation(components) {
            let operation = new Operation(this._padId, -1, this._clientId);
            for (let i = 0; i < components.length; i++) {
                operation.addComponent(components[i]);
            }
            return operation;
        }

        processClientOperation(operation) {
            this._clientState.applyClient(operation);
        }

        processServerOperation(operation) {
            if (operation.metaData.authorId === this._clientId) {
                // we don't need to apply our own operation
                this._clientState.serverAck(operation);
            } else {
                this._clientState.applyServer(operation);
            }
        }

        /**
         * This method sends the operation to the server.
         *
         * Subclasses need to implement this method to send it to the server via the appropriate
         * means of communication.
         *
         * @param operation the operation to be sent
         */
        sendOperation(operation) {

        }

        /**
         * Apply this operation to the client's state.
         *
         * This method is called when an operation is received from the server and the change needs
         * to be reflected in the client's text state.
         *
         * @param operation the operation received from the server
         */
        applyOperation(operation) {

        }

        onReconnect() {
            this._clientState.onReconnect();
        }
    }
    return Client;
})();
