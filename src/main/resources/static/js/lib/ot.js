let ot = {};
[ot.Component, ot.Operation] = (function() {
    class Component {
        /**
         * Constructs a new component.
         *
         * Retain and payload should not be both specified.
         *
         * @param type the type of this component
         * @param retain the amount of characters to skip
         * @param payload
         */
        constructor(type, retain, payload) {
            this._type = type;
            this._retain = retain != null ? retain : 0;
            this._payload = payload != null ? payload : '';
        }

        get type() {
            return this._type;
        }

        get retain() {
            return this._retain;
        }

        get payload() {
            return this._payload;
        }

        size() {
            return this._retain + this._payload.length
        }

        getLengthChange() {
            switch (this._type) {
                case 'RETAIN':
                    return 0;
                case 'INSERT':
                    return this._payload.length;
                case 'DELETE':
                    return -this._payload.length;
            }
        }

        getCharactersAdded() {
            if (this.getLengthChange() > 0) {
                return this.getLengthChange();
            }
            return 0;
        }

        getCharactersRemoved() {
            if (this.getLengthChange() < 0) {
                return -this.getLengthChange();
            }
            return 0;
        }

        apply(text, cursorPosition) {
            switch (this._type) {
                case 'RETAIN':
                    return text;
                case 'INSERT': {
                    let textBefore = text.substring(0, cursorPosition);
                    let textAfter = text.substring(cursorPosition);
                    return textBefore + this._payload + textAfter;
                }
                case 'DELETE': {
                    let textBefore = text.substring(0, cursorPosition);
                    let textAfter = text.substring(cursorPosition);
                    if (!textAfter.startsWith(this._payload)) {
                        throw "This operation cannot be applied to text " + text + " at position "
                        + cursorPosition + ". Expected '" + this._payload + "', found '"
                        + textAfter.substring(0, this._payload.length) + "'.";
                    }
                    return textBefore + textAfter.substring(this._payload.length);
                }
            }
        }

        canMergeWith(otherComponent) {
            return this.type === otherComponent.type;
        }

        merge(otherComponent) {
            if (!this.canMergeWith(otherComponent)) {
                throw "Cannot merge with component of type " + otherComponent.type;
            }
            switch (this._type) {
                case 'RETAIN':
                    return new Component(this._type, this._retain + otherComponent.retain, '');
                case 'INSERT':
                case 'DELETE':
                    return new Component(this._type, 0, this._payload + otherComponent.payload);
            }
        }

        advance(advanceBy) {
            switch (this._type) {
                case 'RETAIN':
                    return new Component(this._type, this._retain - advanceBy, '');
                case 'INSERT':
                case 'DELETE':
                    return new Component(this._type, 0, this._payload.substring(advanceBy));
            }
        }

        isRetain() {
            return this._type === 'RETAIN';
        }

        isInsert() {
            return this._type === 'INSERT';
        }

        isDelete() {
            return this._type === 'DELETE';
        }
    }

    class Operation {

        constructor(padId, parentId, authorId) {
            this.padId = padId;
            this.id = null;
            this.parentId = parentId;
            this.metaData = {
                authorId: authorId,
                timestamp: new Date().getTime() / 1000
            };

            this._baseLength = 0;
            this._targetLength = 0;
            this._components = [];
        }

        get baseLength() {
            return this._baseLength;
        }

        get targetLength() {
            return this._targetLength;
        }

        get components() {
            return this._components;
        }

        toJSON() {
            let json = {padId: this.padId, parentId: this.parentId, components: [], metaData: {
                    authorId: this.metaData.authorId, timestamp: this.metaData.timestamp
                }};
            for (let i = 0; i < this._components.length; i++) {
                let component = this._components[i];
                json.components.push({type: component.type, retain: component.retain,
                    payload: component.payload});
            }
            return json;
        }

        static fromJSON(json) {
            if (!(json.hasOwnProperty('padId') && json.hasOwnProperty('id')
                && json.hasOwnProperty('parentId') && json.hasOwnProperty('components')
                && Array.isArray(json.components) && json.hasOwnProperty('metaData'))) {
                throw "Invalid operation format: " + json;
            }
            let operation = new Operation(json.padId, json.parentId, json.authorId);
            operation.id = json.id;
            operation.metaData = json.metaData;
            for (let i = 0; i < json.components.length; i++) {
                let componentJson = json.components[i];
                if (!(componentJson.hasOwnProperty('type') && componentJson.hasOwnProperty('retain')
                    && componentJson.hasOwnProperty('payload'))) {
                    throw "Invalid component format: " + componentJson;
                }
                operation.addComponent(new Component(componentJson.type, componentJson.retain,
                    componentJson.payload));
            }

            return operation;
        }

        static begin(parentId = -1) {
            return new Operation(null, parentId, 0)
        }

        retain(retain) {
            if (retain > 0) {
                this.addComponent(new Component('RETAIN', retain, ''));
            }
            return this;
        }

        insert(payload) {
            if (payload.length > 0) {
                this.addComponent(new Component('INSERT', 0, payload));
            }
            return this;
        }

        delete(payload) {
            if (payload.length > 0) {
                this.addComponent(new Component('DELETE', 0, payload));
            }
            return this;
        }

        withPadId(padId) {
            this.padId = padId;
            return this;
        }

        withMetaData(metaData) {
            this.metaData = metaData;
            return this;
        }

        addComponent(component) {
            this._baseLength += component.retain + component.getCharactersRemoved();
            this._targetLength += component.retain + component.getCharactersAdded();
            if (this._targetLength < 0) {
                throw "Operation would reduce target length below zero.";
            }

            if (this._components.length > 0) {
                let lastComponentIndex = this._components.length - 1;
                let lastComponent = this._components[lastComponentIndex];
                if (lastComponent.canMergeWith(component)) {
                    this._components[lastComponentIndex] = lastComponent.merge(component);
                    return;
                }
            }
            this._components.push(component);
        }

        apply(text) {
            if (text.length !== this._baseLength) {
                throw "Operation with baseLength " + this._baseLength
                + " cannot be applied to text of length " + text.length + ": "
                + JSON.stringify(this._components);
            }
            let outputText = text;
            let position = 0;
            for (let i = 0; i < this._components.length; i++) {
                let component = this._components[i];
                outputText = component.apply(outputText, position);
                position += component.retain + component.getCharactersAdded();
            }
            return outputText;
        }

        compose(opB) {
            if (this._targetLength !== opB.baseLength) {
                throw "The second operation's baseLength (" + opB.baseLength
                + ") must be the first operation's targetLength (" + this._targetLength + ").";
            }

            let composedOperation = Operation.begin(this.parentId)
                .withPadId(this.padId)
                .withMetaData(this.metaData);

            let componentsA = this._components.slice(0);
            let componentsB = opB.components.slice(0);

            while (componentsA.length > 0 || componentsB.length > 0) {
                let componentA = componentsA.length > 0 ? componentsA[0] : null;
                let componentB = componentsB.length > 0 ? componentsB[0] : null;

                if (componentA != null && componentA.isDelete()) {
                    composedOperation.addComponent(componentsA.shift());
                    continue;
                }

                if (componentB != null && componentB.isInsert()) {
                    composedOperation.addComponent(componentsB.shift());
                    continue;
                }

                if (componentA == null) {
                    throw "Operation a is too short.";
                }

                if (componentB == null) {
                    throw "Operation b is too short.";
                }

                if (componentA.size() > componentB.size()) {
                    advance(componentsA, componentB.size());
                    composedOperation.addComponent(componentsB.shift());
                } else if (componentA.size() < componentB.size()) {
                    composedOperation.addComponent(componentsA.shift());
                    advance(componentsB, componentA.size());
                } else {
                    //components have the same size
                    if (componentA.isRetain() && componentB.isRetain()) {
                        composedOperation.addComponent(componentsA.shift());
                        componentsB.shift();
                    } else if (componentA.isRetain() && componentB.isDelete()) {
                        composedOperation.addComponent(componentsB.shift());
                        componentsA.shift();
                    } else if (componentA.isInsert() && componentB.isDelete()) {
                        componentsA.shift();
                        componentsB.shift();
                    } else if (componentA.isInsert() && componentB.isRetain()) {
                        composedOperation.addComponent(componentsA.shift());
                        componentsB.shift();
                    }
                }
            }

            return composedOperation;
        }

        transform(operationB) {
            let operationA = this;

            if (operationA.baseLength !== operationB.baseLength) {
                throw "Both operations must have the same baseLength: operationA.baseLength = "
                + operationA.baseLength + ", operationB.baseLength = " + operationB.baseLength;
            }

            if (operationA.parentId !== operationB.parentId) {
                throw "Both operations must have the same parent.";
            }

            let operationAPrime = Operation.begin(operationA.id)
                .withPadId(this.padId)
                .withMetaData(operationA.metaData);
            let operationBPrime = Operation.begin(operationA.id)
                .withPadId(this.padId)
                .withMetaData(operationB.metaData);

            let componentsA = this.components.slice(0);
            let componentsB = operationB.components.slice(0);

            while (componentsA.length > 0 || componentsB.length > 0) {
                let componentA = componentsA.length > 0 ? componentsA[0] : null;
                let componentB = componentsB.length > 0 ? componentsB[0] : null;

                if (componentA != null && componentA.isInsert()) {
                    operationAPrime.addComponent(componentsA.shift());
                    operationBPrime.retain(componentA.getCharactersAdded());
                    continue;
                }
                if (componentB != null && componentB.isInsert()) {
                    operationBPrime.addComponent(componentsB.shift());
                    operationAPrime.retain(componentB.getCharactersAdded());
                    continue;
                }

                if (componentA == null) {
                    throw "Operation a is too short.";
                }

                if (componentB == null) {
                    throw "Operation b is too short.";
                }

                let shorterComponent;
                if (componentA.size() === componentB.size()) {
                    shorterComponent = componentA;
                    componentsA.shift();
                    componentsB.shift();
                } else if (componentA.size() > componentB.size()) {
                    shorterComponent = componentB;
                    advance(componentsA, shorterComponent.size());
                    componentsB.shift();
                } else {
                    shorterComponent = componentA;
                    advance(componentsB, shorterComponent.size());
                    componentsA.shift();
                }


                if (componentA.isRetain() && componentB.isRetain()) {
                    operationAPrime.addComponent(shorterComponent);
                    operationBPrime.addComponent(shorterComponent);
                } else if (componentA.isDelete() && componentB.isRetain()) {
                    let deletePayload = componentA.payload;
                    let minLengthPayload = deletePayload.substring(0, shorterComponent.size());
                    operationAPrime.delete(minLengthPayload);
                } else if (componentA.isRetain() && componentB.isDelete()) {
                    let deletePayload = componentB.payload;
                    let minLengthPayload = deletePayload.substring(0, shorterComponent.size());
                    operationBPrime.delete(minLengthPayload);
                }
                //unhandled case: both are delete components --> we don't need to do anything
            }

            return [operationAPrime, operationBPrime];
        }
    }

    function advance(components, advanceBy) {
        let oldComponent = components.shift();
        components.unshift(oldComponent.advance(advanceBy));
    }

    return [Component, Operation];
})();
