package edu.mit.cci.pogs.ot.api;

import edu.mit.cci.pogs.ot.api.components.DeleteComponent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteComponentTest {

    @Test
    public void testApply__givenDeletionAtBeginning__shouldReturnCorrectString() {
        final DeleteComponent operation = new DeleteComponent("t");
        final String result = operation.apply("test", 0);

        assertEquals("Single character not deleted correctly.",
                "est", result);
    }

    @Test
    public void testApply__givenDeletionAtEnd__shouldReturnCorrectString() {
        final DeleteComponent operation = new DeleteComponent("t");
        final String result = operation.apply("test", 3);

        assertEquals("Single character not deleted correctly.",
                "tes", result);
    }

    @Test
    public void testApply__givenFullTextDeletion__shouldReturnEmptyString() {
        final DeleteComponent operation = new DeleteComponent("test");
        final String result = operation.apply("test", 0);

        assertEquals("Full deletion not working.",
                "", result);
    }
}
