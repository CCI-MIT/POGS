package edu.mit.cci.pogs.ot.api.impl;

import edu.mit.cci.pogs.ot.api.Operation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperationStateImplTest {

    private OperationStateImpl operationState;

    @Before
    public void beforeTest() {
        operationState = new OperationStateImpl();
    }


    @Test
    public void testApply__givenOneOperation__shouldApplyCorrectly() {
        final Operation operation = Operation
                .begin()
                .insert("Lorem ipsum");

        operationState.apply(operation);

        assertEquals("Single operation applied incorrectly", "Lorem ipsum",
                operationState.getText());
    }

    @Test
    public void testApply__givenMultipleOperations__shouldApplyCorrectly() {
        final Operation op1 = Operation
                .begin()
                .insert("Lorem ipsum");
        final Operation op2 = Operation
                .begin(0)
                .retain(11)
                .insert(" dolor sit amet");
        final Operation op3 = Operation
                .begin(1)
                .retain(26)
                .insert("!");

        operationState.apply(op1);
        operationState.apply(op2);
        operationState.apply(op3);

        assertEquals("Multiple operation applied incorrectly",
                "Lorem ipsum dolor sit amet!", operationState.getText());
    }

    @Test
    public void testApply__givenTwoConcurrentOperations__shouldTransformAndApplyCorrectly() {
        final Operation op1 = Operation
                .begin()
                .insert("Lorem ipsum");
        final Operation op2 = Operation
                .begin()
                .insert("goat");

        operationState.apply(op1);
        operationState.apply(op2);

        assertEquals("Concurrent operation not transformed correctly",
                "Lorem ipsumgoat", operationState.getText());
    }

    @Test
    public void testApply__givenMultipleConcurrentOperations__shouldTransformAndApplyCorrectly() {
        final Operation op1 = Operation
                .begin()
                .insert("lorem ipsum");
        final Operation op2 = Operation
                .begin()
                .insert("goat");
        final Operation op3 = Operation
                .begin(1)
                .retain(15)
                .insert("brown fox");
        final Operation op4 = Operation
                .begin(2)
                .delete("l")
                .insert("L")
                .retain(10)
                .insert(" dolor sit amet. ")
                .retain(4)
                .insert(" ")
                .delete("brown")
                .insert("orange")
                .retain(4);

        operationState.apply(op1);
        operationState.apply(op2);
        operationState.apply(op3);
        operationState.apply(op4);

        assertEquals("Multiple concurrent operation not transformed correctly",
                "Lorem ipsum dolor sit amet. goat orange fox", operationState.getText());
    }

}
