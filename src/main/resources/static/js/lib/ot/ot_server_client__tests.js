describe('Server-client tests', function() {

    class MockServerClient extends ot.Client {

        constructor(serverState, clientId, serverNetworkBuffer) {
            super(0, clientId);
            this._serverState = serverState;
            this._text = "";
            this._serverNetworkBuffer = serverNetworkBuffer;
        }

        get text() {
            return this._text;
        }

        applyOperation(operation) {
            this._text = operation.apply(this._text);
        }

        sendOperation(operation) {
            let ack = this._serverState.apply(operation);
            // Don't deliver the operation to clients right away
            this._serverNetworkBuffer.push(ack);
        }

        simulateClientOperation(operation) {
            operation.metaData.authorId = this._clientId;
            this._text = operation.apply(this._text);
            this.processClientOperation(operation);
        }
    }

    let serverNetworkBuffer;
    let serverState;
    let clients;

    function deliverNextOperationFromBuffer() {
        const operation = serverNetworkBuffer.shift();
        for (let client of clients) {
            client.processServerOperation(operation)
        }
    }

    beforeEach(function() {
        serverNetworkBuffer = [];
        serverState = new ot.ServerState('');
        clients = [
            new MockServerClient(serverState, 0, serverNetworkBuffer),
            new MockServerClient(serverState, 1, serverNetworkBuffer),
            new MockServerClient(serverState, 2, serverNetworkBuffer)
        ];
    });

    it("Simple client operation", function() {
        let op1 = ot.Operation
            .begin(0)
            .withPadId(0)
            .insert("Lorem ipsum");
        clients[0].simulateClientOperation(op1);
        deliverNextOperationFromBuffer(clients);

        expect(serverState.text).toBe("Lorem ipsum");

        // Make sure all client states match the server state
        expect(clients[0].text).toBe(serverState.text);
        expect(clients[1].text).toBe(serverState.text);
        expect(clients[2].text).toBe(serverState.text);
    });

    it("Concurrent client operations", function() {
        let op1 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("Lorem ipsum");
        clients[0].simulateClientOperation(op1);

        let op2 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("test");
        clients[1].simulateClientOperation(op2);
        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        const expected = "testLorem ipsum";
        expect(serverState.text).toBe(expected);

        // Make sure all client states match the server state
        expect('client 0: ' + clients[0].text).toBe('client 0: ' + expected);
        expect('client 1: ' + clients[1].text).toBe('client 1: ' + expected);
        expect('client 2: ' + clients[2].text).toBe('client 2: ' + expected);
    });

    it("Multiple concurrent client operations", function() {
        let op1 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("Lorem ipsum");
        clients[0].simulateClientOperation(op1);

        let op2 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("goat");
        clients[1].simulateClientOperation(op2);
        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        // Text = goatLorem ipsum

        let op3 = ot.Operation
            .begin(1)
            .withPadId(2)
            .retain(15)
            .insert("!");
        clients[0].simulateClientOperation(op3);

        let op4 = ot.Operation
            .begin(1)
            .withPadId(0)
            .retain(15)
            .insert(" dolor sit amet");
        clients[1].simulateClientOperation(op4);

        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        const expected = "goatLorem ipsum dolor sit amet!";
        expect(serverState.text).toBe(expected);

        // Make sure all client states match the server state
        expect('client 0: ' + clients[0].text).toBe('client 0: ' + expected);
        expect('client 1: ' + clients[1].text).toBe('client 1: ' + expected);
        expect('client 2: ' + clients[2].text).toBe('client 2: ' + expected);
    });

    it("Multiple concurrent client-server operations with client buffering", function() {
        let op1 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("Lorem ipsum");
        clients[0].simulateClientOperation(op1);

        let op2 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("goat");
        clients[1].simulateClientOperation(op2);

        // Text = goatLorem ipsum

        let op3 = ot.Operation
            .begin(1)
            .withPadId(0)
            .retain(11)
            .insert("!");
        clients[0].simulateClientOperation(op3);

        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        let op4 = ot.Operation
            .begin(1)
            .withPadId(0)
            .retain(15)
            .insert(" dolor sit amet");
        clients[1].simulateClientOperation(op4);

        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        const expected = "goatLorem ipsum dolor sit amet!";
        expect(serverState.text).toBe(expected);

        // Make sure all client states match the server state
        expect('client 0: ' + clients[0].text).toBe('client 0: ' + expected);
        expect('client 1: ' + clients[1].text).toBe('client 1: ' + expected);
        expect('client 2: ' + clients[2].text).toBe('client 2: ' + expected);
    });

    it("Concurrent, split inserts", function() {
        let op1 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("l");
        clients[0].simulateClientOperation(op1);

        let op2 = ot.Operation
            .begin()
            .withPadId(0)
            .insert("t");
        clients[1].simulateClientOperation(op2);

        // Text = tl

        let op3 = ot.Operation
            .begin(0)
            .withPadId(0)
            .retain(1)
            .insert("orem");
        clients[0].simulateClientOperation(op3);

        let op4 = ot.Operation
            .begin(0)
            .withPadId(0)
            .retain(1)
            .insert("est");
        clients[1].simulateClientOperation(op4);

        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        deliverNextOperationFromBuffer(clients);
        deliverNextOperationFromBuffer(clients);

        const expected = "testlorem";
        expect(serverState.text).toBe(expected);

        // Make sure all client states match the server state
        expect('client 0: ' + clients[0].text).toBe('client 0: ' + expected);
        expect('client 1: ' + clients[1].text).toBe('client 1: ' + expected);
        expect('client 2: ' + clients[2].text).toBe('client 2: ' + expected);
    });

});
