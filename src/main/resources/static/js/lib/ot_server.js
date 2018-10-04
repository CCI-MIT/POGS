// Requires ot.js

ot.ServerState = (function() {
    class ServerState {
        constructor(initialText) {
            this._operations = [];
            this._text = initialText;
        }

        apply(operation) {
            if (operation.parentId < this._operations.length - 1) {
                operation = this.transform(operation);
            }
            operation.id = this._operations.length;
            this._text = operation.apply(this._text);
            this._operations.push(operation);
            return operation;
        }

        transform(operation) {
            if (operation.parentId >= this._operations.length) {
                throw "Operation with parent " + operation.parentId + " is not based on any "
                + "operations in this state (size = " + this._operations.length + ")";
            }

            let concurrentOperations = this.findOperationsSince(operation.parentId);

            let transformedOperation = operation;
            for (let i = 0; i < concurrentOperations.length; i++) {
                let concurrentOperation = concurrentOperations[i];
                let transformedOperationPair = transformedOperation.transform(concurrentOperation);
                // let transformedOperationPair = concurrentOperation.transform(transformedOperation);
                transformedOperation = transformedOperationPair[0];
            }
            return transformedOperation;
        }

        findOperationsSince(operationId) {
            return this._operations.slice(operationId + 1)
        }

        get text() {
            return this._text;
        }

        get latestKnownOperationId() {
            return this._operations.length - 1;
        }
    }
    return ServerState;
})();
