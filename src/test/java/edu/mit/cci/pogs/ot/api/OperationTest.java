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
}
