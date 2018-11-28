class TestOtClient extends ot.Client {
    constructor() {
        super(0, 0);

        this._text = "";
    }

    get text() {
        return this._text;
    }

    sendOperation(operation) {

    }

    applyOperation(operation) {
        this._text = operation.apply(this._text);
    }

    simulateServerOperation(operation) {
        operation.id = this._clientState._latestKnownServerOperationId + 1;
        this.processServerOperation(operation);
    }

    simulateClientOperation(operation) {
        this._text = operation.apply(this._text);
        this.processClientOperation(operation);
    }

    simulateServerAck(operation) {
        operation.parentId = this._clientState._latestKnownServerOperationId;
        operation.id = this._clientState._latestKnownServerOperationId + 1;
        this.processServerOperation(operation);
    }
}

describe('Client tests', function() {

    let client;

    beforeEach(function() {
        client = new TestOtClient();
    });

    it("Simple server operation", function() {
        let serverOperation = ot.Operation
            .begin(0)
            .withPadId(0)
            .withAuthorId(1)
            .insert("Lorem ipsum");
        client.processServerOperation(serverOperation);
        expect(client.text).toBe("Lorem ipsum")
    });

    it("Simple client operation", function() {
        let clientOperation = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(0)
            .insert("Lorem ipsum");
        client.simulateClientOperation(clientOperation);
        client.simulateServerAck(clientOperation);
        expect(client.text).toBe("Lorem ipsum")
    });

    it("Concurrent client-server operation", function() {
        let clientOperation = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(0)
            .insert("Lorem ipsum");
        let serverOperation = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(1)
            .insert("test");
        client.simulateClientOperation(clientOperation);
        client.processServerOperation(serverOperation);
        client.simulateServerAck(clientOperation);
        expect(client.text).toBe("Lorem ipsumtest")
    });

    it("Multiple concurrent client-server operations", function() {
        let clientOperation1 = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(0)
            .insert("Lorem ipsum");
        client.simulateClientOperation(clientOperation1);

        let serverOperation1 = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(1)
            .insert("goat");
        client.simulateServerOperation(serverOperation1);

        client.simulateServerAck(clientOperation1);

        // Text client = Lorem ipsumgoat

        let clientOperation2 = ot.Operation
            .begin(1)
            .withPadId(2)
            .withAuthorId(0)
            .retain(15)
            .insert("!");
        client.simulateClientOperation(clientOperation2);

        let serverOperation2 = ot.Operation
            .begin(1)
            .withPadId(0)
            .withAuthorId(1)
            .retain(15)
            .insert(" dolor sit amet");
        client.simulateServerOperation(serverOperation2);

        client.simulateServerAck(clientOperation2);

        expect(client.text).toBe("Lorem ipsumgoat! dolor sit amet")
    });

    it("Multiple concurrent client-server operations with client buffering", function() {
        let clientOperation1 = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(0)
            .insert("Lorem ipsum");
        client.simulateClientOperation(clientOperation1);

        let serverOperation1 = ot.Operation
            .begin()
            .withPadId(0)
            .withAuthorId(1)
            .insert("goat");
        client.simulateServerOperation(serverOperation1);

        // Text client = Lorem ipsumgoat

        let clientOperation2 = ot.Operation
            .begin()
            .withPadId(2)
            .withAuthorId(0)
            .retain(15)
            .insert("!");
        client.simulateClientOperation(clientOperation2);

        let serverOperation2 = ot.Operation
            .begin(0)
            .withPadId(0)
            .withAuthorId(1)
            .retain(4)
            .insert(" dolor sit amet");
        client.simulateServerOperation(serverOperation2);

        client.simulateServerAck(clientOperation1);
        client.simulateServerAck(clientOperation2);

        expect(client.text).toBe("Lorem ipsumgoat! dolor sit amet")
    });

});
