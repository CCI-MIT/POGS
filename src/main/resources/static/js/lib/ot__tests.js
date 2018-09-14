let Component = ot.Component;
let Operation = ot.Operation;

describe('Component tests', function() {
    it("Simple insertion into empty string", function() {
        let insert = new Component('INSERT', 0, 'test');
        let result = insert.apply("", 0);
        expect(result).toBe("test")
    });

    it("Insertion in the middle of a string", function() {
        let insert = new Component('INSERT', 0, 'a');
        let result = insert.apply("test", 2);
        expect(result).toBe("teast")
    });

    it("Simple deletion test", function() {
        let insert = new Component('DELETE', 0, 'test');
        let result = insert.apply("test", 0);
        expect(result).toBe("")
    });

    it("Deletion in the middle of the string", function() {
        let insert = new Component('DELETE', 0, 'st');
        let result = insert.apply("test", 2);
        expect(result).toBe("te")
    });

    it("Merge test for retain", function() {
        let retain = new Component('RETAIN', 1, '');
        let retain2 = new Component('RETAIN', 2, '');
        let result = retain.merge(retain2);
        expect(result.retain).toBe(3)
    });

    it("Merge test for insert", function() {
        let insert = new Component('INSERT', 0, 'te');
        let insert2 = new Component('INSERT', 0, 'st');
        let result = insert.merge(insert2);
        expect(result.payload).toBe('test')
    });

    it("Merge test for delete", function() {
        let del = new Component('DELETE', 0, 'te');
        let del2 = new Component('DELETE', 0, 'st');
        let result = del.merge(del2);
        expect(result.payload).toBe('test')
    });
});


describe('Operation apply tests', function() {

    it("Simple insertion", function() {
        let operation = Operation.begin(-1)
            .insert('test');

        let result = operation.apply("");
        expect(result).toBe("test");
    });

    it("Delete then re-insert", function() {
        let operation = Operation.begin(-1)
            .delete('t')
            .insert('T')
            .retain(3);

        let result = operation.apply("test");
        expect(result).toBe("Test");
    });
});


describe('Operation composition tests', function() {

    it("Simple insertions", function() {
        let operation1 = Operation.begin(-1)
            .insert('test');
        let operation2 = Operation.begin(-1)
            .retain(4)
            .insert('goat');

        let composedOp = operation1.compose(operation2);

        let result = composedOp.apply("");
        expect(result).toBe("testgoat");
    });

    it("Multiple components", function() {
        let opA = Operation.begin(-1)
            .retain(6)
            .delete('bla')
            .insert('ipsum')
            .retain(7)
            .delete("erm")
            .retain(1);
        let opB = Operation.begin(-1)
            .retain(18)
            .insert('sit')
            .retain(1)
            .insert('amet');

        let composedOp = opA.compose(opB);

        let inputString = "lorem bla dolor erm ";
        let expected = opB.apply(opA.apply(inputString));

        let result = composedOp.apply(inputString);
        expect(result).toBe(expected);
    });
});

describe('Operation transformation tests', function() {
    it("Simple transform invariant", function() {
        let opA = Operation.begin(-1)
            .insert('go');
        let opB = Operation.begin(-1)
            .insert('at');

        let transformedPair = opA.transform(opB);

        let opAPrime = transformedPair[0];
        let opBPrime = transformedPair[1];

        let composeABPrime = opA.compose(opBPrime);
        let composeBAPrime = opB.compose(opAPrime);
        let applyABPrime = composeABPrime.apply("");
        let applyBAPrime = composeBAPrime.apply("");
        expect(applyABPrime).toBe(applyBAPrime);
    });

    it("Simple transform invariant", function() {
        let opA = Operation.begin(-1)
            .insert('go');
        let opB = Operation.begin(-1)
            .insert('at');

        let transformedPair = opA.transform(opB);

        let opAPrime = transformedPair[0];
        let opBPrime = transformedPair[1];

        let composeABPrime = opA.compose(opBPrime);
        let composeBAPrime = opB.compose(opAPrime);
        let applyABPrime = composeABPrime.apply("");
        let applyBAPrime = composeBAPrime.apply("");
        expect(applyABPrime).toBe(applyBAPrime);
    });

    it("Multiple component transform", function() {
        let opA = Operation.begin()
            .retain(10)
            .delete("brown fox")
            .insert("lazy dog")
            .retain(16)
            .delete("lazy dog")
            .insert("brown fox");
        let opB = Operation.begin()
            .retain(4)
            .delete("quick brown")
            .retain(5)
            .delete("jumps over")
            .insert("slides under")
            .retain(13);

        let inputString = "the quick brown fox jumps over the lazy dog";
        let expected = "the lazy dog slides under the brown fox";

        let transformedPair = opA.transform(opB);

        let opAPrime = transformedPair[0];
        let opBPrime = transformedPair[1];

        let applyA = opA.apply(inputString);
        expect(opBPrime.apply(applyA)).toBe(expected);
        let applyB = opB.apply(inputString);
        expect(opAPrime.apply(applyB)).toBe(expected);
    });

    it("Multiple component transform invariant", function() {
        let opA = Operation.begin()
            .delete("lorem ipsum")
            .insert("dolor sit amet");
        let opB = Operation.begin()
            .retain(6)
            .insert("consectetur adipiscing elit ")
            .retain(5);

        let inputString = "lorem ipsum";

        let transformedPair = opA.transform(opB);

        let opAPrime = transformedPair[0];
        let opBPrime = transformedPair[1];

        let composeABPrime = opA.compose(opBPrime);
        let composeBAPrime = opB.compose(opAPrime);
        let applyABPrime = composeABPrime.apply(inputString);
        let applyBAPrime = composeBAPrime.apply(inputString);
        expect(applyABPrime).toBe(applyBAPrime);
    });
});
