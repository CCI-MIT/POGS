package edu.mit.cci.pogs.ot.api;

import edu.mit.cci.pogs.ot.api.components.InsertComponent;
import org.junit.Test;

import static org.junit.Assert.*;

public class InsertComponentTest {

    @Test
    public void testApply__givenCharacterInsertionIntoEmptyString__shouldReturnCharacter() {
        final InsertComponent operation = new InsertComponent("a");
        final String result = operation.apply("", 0);

        assertEquals("Single character not inserted correctly into empty string",
                "a", result);
    }

    @Test
    public void testApply__givenCharacterInsertionBeforeString__shouldReturnCharacterBeforeString() {
        final InsertComponent operation = new InsertComponent("a");
        final String result = operation.apply("test", 0);

        assertEquals("Single character not inserted correctly before existing string",
                "atest", result);
    }

    @Test
    public void testApply__givenCharacterInsertionAfterString__shouldReturnCharacterAfterString() {
        final InsertComponent operation = new InsertComponent("a");
        final String result = operation.apply("test", 4);

        assertEquals("Single character not inserted correctly after existing string",
                "testa", result);
    }

    @Test
    public void testApply__givenCharacterInsertionIntoString__shouldReturnCharacterInString() {
        final InsertComponent operation = new InsertComponent("a");
        final String result = operation.apply("test", 2);

        assertEquals("Single character not inserted correctly into existing string",
                "teast", result);
    }

    @Test
    public void testApply__givenMultiCharacterInsertionIntoString__shouldReturnCharactersInString() {
        final InsertComponent operation = new InsertComponent("mpe");
        final String result = operation.apply("test", 2);

        assertEquals("Characters not inserted correctly into existing string",
                "tempest", result);
    }

}
