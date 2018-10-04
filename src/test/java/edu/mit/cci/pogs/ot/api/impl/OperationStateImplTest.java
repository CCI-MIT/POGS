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
                "goatLorem ipsum", operationState.getText());
    }

    @Test
    public void testApply__givenMultipleConcurrentOperations__shouldTransformAndApplyCorrectly() {
        final Operation op1 = Operation
                .begin()
                .insert("lorem ipsum");
        final Operation op2 = Operation
                .begin()
                .insert("goat");
        // text = goatlorem ipsum

        final Operation op3 = Operation
                .begin(1)
                .retain(15)
                .insert("brown fox");

        //text = goatlorem ipsumbrown fox
        final Operation op4 = Operation
                .begin(2)
                .delete("g")
                .insert("G")
                .retain(14)
                .insert(" dolor sit amet. ")
                .delete("brown")
                .insert("orange")
                .retain(4);

        operationState.apply(op1);
        operationState.apply(op2);
        operationState.apply(op3);
        operationState.apply(op4);

        assertEquals("Multiple concurrent operation not transformed correctly",
                "Goatlorem ipsum dolor sit amet. orange fox", operationState.getText());
    }

    @Test
    public void testApply__givenMultipleConcurrentAndOutdatedOperations__shouldTransformAndApplyCorrectly() {
        final Operation op1 = Operation.begin()
                .insert("lorem ipsum");
        operationState.apply(op1); // id = 0

        final Operation op2 = Operation.begin()
                .insert("goat");
        operationState.apply(op2); // id = 1
        // Text = goatlorem ipsum

        final Operation op3 = Operation.begin(1)
                .retain(15)
                .insert("brown fox");
        operationState.apply(op3); // id = 2
        // Text = goatlorem ipsumbrown fox

        final Operation op4 = Operation.begin(2)
                .delete("g")
                .insert("G")
                .retain(14)
                .insert(" dolor sit amet. ")
                .delete("brown")
                .insert("orange")
                .retain(4);
        operationState.apply(op4); // id = 3
        // Text = Goatlorem ipsum dolor sit amet. orange fox

        final Operation op5 = Operation.begin(1)
                .retain(4)
                .delete("l")
                .retain(4)
                .delete(" ipsum")
                .insert(" test");
        operationState.apply(op5); //id = 4
        // Text = Goat orem dolor sit amet. goat orange fox test

        final Operation op6 = Operation.begin(0)
                .retain(4)
                .delete("m")
                .insert("M")
                .retain(1)
                .insert(" this")
                .retain(5);
        operationState.apply(op6); // id = 5
        // Text = LoreM this dolor sit amet. goat orange fox test

        final Operation op7 = Operation.begin(1)
                .retain(4)
                .delete("lorem ipsum");
        operationState.apply(op7); // id = 6

        assertEquals("Multiple concurrent operation not transformed correctly",
                "GoatM this test dolor sit amet. orange fox", operationState.getText());
    }
}
