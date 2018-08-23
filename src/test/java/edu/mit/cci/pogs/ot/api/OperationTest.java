package edu.mit.cci.pogs.ot.api;

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
}
