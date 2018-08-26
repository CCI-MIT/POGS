package edu.mit.cci.pogs.ot.api;

import edu.mit.cci.pogs.ot.api.Operation.TransformedOperationPair;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    /*
     * ==============================
     * ======== test apply() ========
     * ==============================
     */

    @Test
    public void testApply__givenInsertInEmptyString__shouldReturnPayload() {
        String testString = "";
        final Operation operation = Operation
                .begin()
                .insert("a");
        final String result = operation.apply(testString);
        assertEquals("Result is wrong", "a", result);
    }

    @Test
    public void testApply__givenInsertAtEnd__shouldAppendAtEnd() {
        String testString = "go";
        final Operation operation = Operation
                .begin()
                .retain(2)
                .insert("a");
        final String result = operation.apply(testString);
        assertEquals("Result is wrong", "goa", result);
    }

    @Test
    public void testApply__givenInsertInMiddle__shouldInsertInMiddle() {
        String testString = "got";
        final Operation operation = Operation
                .begin()
                .retain(2)
                .insert("a")
                .retain(1);
        final String result = operation.apply(testString);
        assertEquals("Result is wrong", "goat", result);
    }

    @Test
    public void testApply__givenDeleteThenInsert__shouldReturnInitialText() {
        String testString = "goat";
        final Operation operation = Operation
                .begin()
                .retain(2)
                .delete("a")
                .insert("a")
                .retain(1);
        final String result = operation.apply(testString);
        assertEquals("Result is wrong", "goat", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply__givenWrongLength__shouldFail() {
        String testString = "got";
        final Operation operation = Operation
                .begin()
                .retain(2)
                .insert("a");
        operation.apply(testString);
        fail();
    }

    /*
     * ================================
     * ======== test compose() ========
     * ================================
     */

    @Test
    public void testCompose__givenEmpty__shouldReturnEmpty() {
        final Operation opA = Operation.begin();
        final Operation opB = Operation.begin();

        final String inputString = "";
        final Operation composedOperation = opA.compose(opB);
        final String result = composedOperation.apply(inputString);

        assertEquals("Empty operations not composed correctly", "", result);
    }

    @Test
    public void testCompose__givenInsertions__shouldEqualConsecutiveApplication() {
        final Operation opA = Operation.begin()
                                       .insert("go");
        final Operation opB = Operation.begin()
                                       .retain(2)
                                       .insert("at");

        final String inputString = "";
        final String expected = opB.apply(opA.apply(inputString));

        final Operation composedOperation = opA.compose(opB);
        final String result = composedOperation.apply(inputString);

        assertEquals("Insert operations not composed correctly", expected, result);
    }

    @Test
    public void testCompose__givenDeletions__shouldEqualConsecutiveApplication() {
        final Operation opA = Operation.begin()
                                       .delete("go")
                                       .retain(2);
        final Operation opB = Operation.begin()
                                       .delete("at");

        final String inputString = "goat";
        final String expected = opB.apply(opA.apply(inputString));

        final Operation composedOperation = opA.compose(opB);
        final String result = composedOperation.apply(inputString);

        assertEquals("Delete operations not composed correctly", expected, result);
    }

    @Test
    public void testCompose__givenMultipleComponents__shouldEqualConsecutiveApplication() {
        final Operation opA = Operation.begin()
                                       .retain(2)
                                       .insert(" banan")
                                       .retain(2);
        final Operation opB = Operation.begin()
                                       .retain(9)
                                       .insert("s")
                                       .delete("t");


        final String inputString = "goat";
        final String expected = opB.apply(opA.apply(inputString));

        final Operation composedOperation = opA.compose(opB);
        final String result = composedOperation.apply(inputString);
        assertEquals("Multiple operations not composed correctly", expected, result);
    }

    @Test
    public void testCompose__givenMultipleComponents2__shouldEqualConsecutiveApplication() {
        final Operation opA = Operation.begin()
                                       .retain(6)
                                       .delete("bla")
                                       .insert("ipsum")
                                       .retain(7)
                                       .delete("erm")
                                       .retain(1);
        final Operation opB = Operation.begin()
                                       .retain(18)
                                       .insert("sit")
                                       .retain(1)
                                       .insert("amet");

        final String inputString = "lorem bla dolor erm ";
        final String expected = opB.apply(opA.apply(inputString));

        final Operation composedOperation = opA.compose(opB);
        final String result = composedOperation.apply(inputString);
        assertEquals("Multiple operations not composed correctly", expected, result);
    }

    /*
     * ================================
     * ======== test transform() ========
     * ================================
     */

    @Test
    public void testTransform__givenTwoInserts__shouldMoveSecondForward() {
        final TransformTestInput testInput = getTransformTestInputForTwoInserts();
        assertTransformedOutputMatches("Two insert operations not transformed correctly",
                testInput);
    }

    @Test
    public void testTransform__givenTwoInserts__shouldInvariantHold() {
        final TransformTestInput testInput = getTransformTestInputForTwoInserts();
        assertTransformInvariantHolds(testInput);
    }

    private TransformTestInput getTransformTestInputForTwoInserts() {
        final Operation opA = Operation.begin()
                                       .insert("go");
        final Operation opB = Operation.begin()
                                       .insert("at");

        final String inputString = "";
        final String expected = "goat";

        return new TransformTestInput(opA, opB, inputString, expected);
    }


    @Test
    public void testTransform__givenTwoEquivalentDeletes__shouldChangeNothing() {
        final TransformTestInput testInput = getTransformTestInputForTwoEquivalentDeletes();
        assertTransformedOutputMatches("Two equivalent deletes not transformed correctly",
                testInput);
    }

    @Test
    public void testTransform__givenTwoEquivalentDeletes__shouldInvariantHold() {
        final TransformTestInput testInput = getTransformTestInputForTwoEquivalentDeletes();
        assertTransformInvariantHolds(testInput);
    }

    private TransformTestInput getTransformTestInputForTwoEquivalentDeletes() {
        final Operation opA = Operation.begin()
                                       .delete("go")
                                       .retain(2);
        final Operation opB = Operation.begin()
                                       .delete("go")
                                       .retain(2);

        final String inputString = "goat";
        final String expected = "at";

        return new TransformTestInput(opA, opB, inputString, expected);
    }


    @Test
    public void testTransform__givenMultipleOperations__shouldTransformCorrectly() {
        final TransformTestInput testInput = getTransformTestInputForMultipleOperations();
        assertTransformedOutputMatches("Multiple operations not transformed correctly",
                testInput);
    }

    @Test
    public void testTransform__givenMultipleOperations__shouldInvariantHold() {
        final TransformTestInput testInput = getTransformTestInputForMultipleOperations();

        assertTransformInvariantHolds(testInput);
    }

    private TransformTestInput getTransformTestInputForMultipleOperations() {
        final Operation opA = Operation.begin()
                                       .retain(10)
                                       .delete("brown fox")
                                       .insert("lazy dog")
                                       .retain(16)
                                       .delete("lazy dog")
                                       .insert("brown fox");
        final Operation opB = Operation.begin()
                                       .retain(4)
                                       .delete("quick brown")
                                       .retain(5)
                                       .delete("jumps over")
                                       .insert("slides under")
                                       .retain(13);

        final String inputString = "the quick brown fox jumps over the lazy dog";
        final String expected = "the lazy dog slides under the brown fox";

        return new TransformTestInput(opA, opB, inputString, expected);
    }

    @Test
    public void testTransform__givenMultipleOperations2__shouldTransformCorrectly() {
        final TransformTestInput testInput = getTransformTestInputForMultipleOperations2();
        assertTransformedOutputMatches("Multiple operations not transformed correctly",
                testInput);
    }

    @Test
    public void testTransform__givenMultipleOperations2__shouldInvariantHold() {
        final TransformTestInput testInput = getTransformTestInputForMultipleOperations2();

        assertTransformInvariantHolds(testInput);
    }

    private TransformTestInput getTransformTestInputForMultipleOperations2() {
        final Operation opA = Operation.begin()
                                       .delete("lorem ipsum")
                                       .insert("dolor sit amet");
        final Operation opB = Operation.begin()
                                       .retain(6)
                                       .insert("consectetur adipiscing elit ")
                                       .retain(5);

        final String inputString = "lorem ipsum";
        final String expected = "consectetur adipiscing elit dolor sit amet";

        return new TransformTestInput(opA, opB, inputString, expected);
    }


    // Helpers

    private void assertTransformedOutputMatches(String message, TransformTestInput testInput) {
        final Operation opA = testInput.opA;
        final Operation opB = testInput.opB;

        final TransformedOperationPair transformedPair = opA.transform(opB);

        final Operation opAPrime = transformedPair.getAPrime();
        final Operation opBPrime = transformedPair.getBPrime();

        final String applyA = opA.apply(testInput.inputString);
        assertEquals(message, testInput.expectedOutput, opBPrime.apply(applyA));
        final String applyB = opB.apply(testInput.inputString);
        assertEquals(message, testInput.expectedOutput, opAPrime.apply(applyB));
    }

    private void assertTransformInvariantHolds(TransformTestInput testInput) {
        // Tests that the following invariant holds:
        // a.transform(b) = [a’, b’], where a.compose(b’) == b.compose(a’)
        final Operation opA = testInput.opA;
        final Operation opB = testInput.opB;

        final TransformedOperationPair transformedPair = opA.transform(opB);

        final Operation opAPrime = transformedPair.getAPrime();
        final Operation opBPrime = transformedPair.getBPrime();

        final Operation composeABPrime = opA.compose(opBPrime);
        final Operation composeBAPrime = opB.compose(opAPrime);
        final String applyABPrime = composeABPrime.apply(testInput.inputString);
        final String applyBAPrime = composeBAPrime.apply(testInput.inputString);
        assertEquals("Composed operations are not equal after transform.", applyABPrime,
                applyBAPrime);
    }

    static class TransformTestInput {

        private Operation opA;
        private Operation opB;
        private String inputString;
        private String expectedOutput;

        TransformTestInput(Operation opA, Operation opB, String inputString,
                String expectedOutput) {
            this.opA = opA;
            this.opB = opB;
            this.inputString = inputString;
            this.expectedOutput = expectedOutput;
        }
    }
}
