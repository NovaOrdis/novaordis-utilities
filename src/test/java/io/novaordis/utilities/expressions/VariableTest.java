/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.utilities.expressions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/13/17
 */
public abstract class VariableTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public abstract void declarationWithAssignment() throws Exception;

    @Test
    public abstract void declarationWithoutAssignment() throws Exception;

    // variable names --------------------------------------------------------------------------------------------------

    @Test
    public void variableName_InvalidName_StartsWithDigit() throws Exception {

        Object value = getValueToTest();

        try {

            getVariableToTest("2something", value);
            fail("should have thrown Exception");
        }
        catch(IllegalNameException e) {

            String msg = e.getMessage();
            assertEquals("2something", msg);
        }
    }

    @Test
    public void variableName_InvalidName_ContainsIllegalCharacter() throws Exception {

        Object value = getValueToTest();

        try {

            getVariableToTest("some@thing", value);
            fail("should have thrown Exception");
        }
        catch(IllegalNameException e) {

            String msg = e.getMessage();
            assertEquals("some@thing", msg);
        }
    }

    @Test
    public void variableName_ValidName() throws Exception {

        Object value = getValueToTest();

        Variable v = getVariableToTest("some.thing", value);

        assertEquals("some.thing", v.name());
    }

    @Test
    public void variableName_Null() throws Exception {

        Object value = getValueToTest();

        try {

            getVariableToTest(null, value);
            fail("should have thrown Exception");
        }
        catch(IllegalNameException e) {

            String msg = e.getMessage();
            assertEquals("null", msg);
        }
    }

    @Test
    public void variableName_LiteralNull() throws Exception {

        Object value = getValueToTest();

        try {

            getVariableToTest("null", value);
            fail("should have thrown Exception");
        }
        catch(IllegalNameException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("reserved name"));
            assertTrue(msg.contains("'null'"));
        }
    }

    @Test
    public void variableName() throws Exception {

        Object value = getValueToTest();

        Variable v = getVariableToTest("som2et_h-i-ng", value);

        String name = v.name();
        assertEquals("som2et_h-i-ng", name);
        assertEquals(value, v.get());
        assertEquals(value.getClass(), v.type());
    }

    // identity --------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        Object value = getValueToTest();

        Variable v = getVariableToTest("test", value);

        String name = v.name();
        assertEquals("test", name);
        assertEquals(value, v.get());
        assertEquals(value.getClass(), v.type());
    }

    // validVariableNameChar() -----------------------------------------------------------------------------------------

    @Test
    public void validVariableNameChar() throws Exception {

        assertTrue(Variable.validVariableNameChar('a'));
        assertTrue(Variable.validVariableNameChar('b'));
        assertTrue(Variable.validVariableNameChar('x'));
        assertTrue(Variable.validVariableNameChar('z'));
        assertTrue(Variable.validVariableNameChar('A'));
        assertTrue(Variable.validVariableNameChar('B'));
        assertTrue(Variable.validVariableNameChar('X'));
        assertTrue(Variable.validVariableNameChar('Z'));
        assertTrue(Variable.validVariableNameChar('0'));
        assertTrue(Variable.validVariableNameChar('9'));
        assertTrue(Variable.validVariableNameChar('-'));
        assertTrue(Variable.validVariableNameChar('_'));
        assertTrue(Variable.validVariableNameChar('.'));
    }

    // variableReferenceTerminator() -----------------------------------------------------------------------------------

    @Test
    public void variableReferenceSeparator() throws Exception {

        assertTrue(Variable.variableReferenceTerminator('}'));
        assertTrue(Variable.variableReferenceTerminator(' '));
        assertTrue(Variable.variableReferenceTerminator('/'));
        assertTrue(Variable.variableReferenceTerminator(':'));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * The variable is declared in an implicit scope.
     *
     * @param optionalValue may be null.
     */
    protected abstract <T> Variable<T> getVariableToTest(String name, T optionalValue) throws Exception;

    protected abstract Object getValueToTest();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
