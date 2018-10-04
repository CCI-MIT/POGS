describe('Server state tests', function() {
    let serverState;

    beforeEach(function() {
        serverState = new ot.ServerState('');
    });

    it("Apply operation", function() {
        let operation = ot.Operation
            .begin()
            .insert("Lorem ipsum");

        serverState.apply(operation);

        expect(serverState.text).toBe("Lorem ipsum");
    });

    it("Apply multiple operations", function() {
        let op1 = ot.Operation
            .begin()
            .insert("Lorem ipsum");
        let op2 = ot.Operation
            .begin(0)
            .retain(11)
            .insert(" dolor sit amet");
        let op3 = ot.Operation
            .begin(1)
            .retain(26)
            .insert("!");

        serverState.apply(op1);
        serverState.apply(op2);
        serverState.apply(op3);

        expect(serverState.text).toBe("Lorem ipsum dolor sit amet!");
    });

    it("Apply concurrent operations", function() {
        let op1 = ot.Operation
            .begin()
            .insert("Lorem ipsum");
        let op2 = ot.Operation
            .begin()
            .insert("goat");

        serverState.apply(op1);
        serverState.apply(op2);

        expect(serverState.text).toBe("goatLorem ipsum");
    });

    it("Apply multiple concurrent operations", function() {
        let op1 = ot.Operation
            .begin()
            .insert("lorem ipsum");
        let op2 = ot.Operation
            .begin()
            .insert("goat");
        let op3 = ot.Operation
            .begin(1)
            .retain(15)
            .insert("brown fox");
        let op4 = ot.Operation
            .begin(2)
            .delete("g")
            .insert("G")
            .retain(14)
            .insert(" dolor sit amet. ")
            .delete("brown")
            .insert("orange")
            .retain(4);

        serverState.apply(op1);
        serverState.apply(op2);
        serverState.apply(op3);
        serverState.apply(op4);

        expect(serverState.text).toBe("Goatlorem ipsum dolor sit amet. orange fox");
    });
});
