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

    @Test
    public void testApply__givenMultipleConcurrentAndOutdatedOperations__shouldTransformAndApplyCorrectly() {
        final Operation op1 = Operation.begin()
                .insert("lorem ipsum");
        final Operation op2 = Operation.begin()
                .insert("goat");
        // Text = lorem ipsumgoat
        final Operation op3 = Operation.begin(1)
                .retain(15)
                .insert("brown fox");
        // Text = lorem ipsumgoatbrown fox
        final Operation op4 = Operation.begin(2)
                .delete("l")
                .insert("L")
                .retain(10)
                .insert(" dolor sit amet. ")
                .retain(4)
                .insert(" ")
                .delete("brown")
                .insert("orange")
                .retain(4);
        // Text = Lorem ipsum dolor sit amet. goat orange fox
        final Operation op5 = Operation.begin(1)
                .delete("l")
                .retain(4)
                .delete(" ipsum")
                .retain(4)
                .insert(" test");
        // Text = Lorem dolor sit amet. goat orange fox test
        final Operation op6 = Operation.begin(0)
                .retain(4)
                .delete("m")
                .insert("M")
                .retain(1)
                .insert(" this")
                .retain(5);
        // Text = LoreM this dolor sit amet. goat orange fox test
        final Operation op7 = Operation.begin(1)
                .delete("lorem ipsum")
                .retain(4);

        operationState.apply(op1);
        operationState.apply(op2);
        operationState.apply(op3);
        operationState.apply(op4);
        operationState.apply(op5);
        operationState.apply(op6);
        operationState.apply(op7);

        assertEquals("Multiple concurrent operation not transformed correctly",
                "LM this dolor sit amet. goat orange fox test", operationState.getText());
    }
}
