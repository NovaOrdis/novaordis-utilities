/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package io.novaordis.utilities.variable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class StringWithVariablesTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(StringWithVariablesTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor_Null() throws Exception {

        try {

            new StringWithVariables(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null argument", msg);
        }
    }

    @Test
    public void constructor_NoVariables() throws Exception {

        StringWithVariables s = new StringWithVariables("something");
        assertEquals("something", s.getLiteral());
        String s2 = s.resolve((VariableProvider)null);
        assertEquals("something", s2);
    }

    @Test
    public void constructor_OnlyVariable() throws Exception {

        StringWithVariables s = new StringWithVariables("${something}");
        assertEquals("${something}", s.getLiteral());
        String s2 = s.resolve((VariableProvider)null);
        assertEquals("${something}", s2);

        List<Token> tokens = s.getTokens();
        assertEquals(1, tokens.size());
        Variable v = (Variable)tokens.get(0);
        assertEquals("${something}", v.getLiteral());
    }

    @Test
    public void constructor_VariableAndConstant() throws Exception {

        StringWithVariables s = new StringWithVariables("blah${something}");
        assertEquals("blah${something}", s.getLiteral());
        String s2 = s.resolve((VariableProvider)null);
        assertEquals("blah${something}", s2);

        List<Token> tokens = s.getTokens();
        assertEquals(2, tokens.size());
        Constant c = (Constant)tokens.get(0);
        assertEquals("blah", c.getLiteral());
        Variable v = (Variable)tokens.get(1);
        assertEquals("${something}", v.getLiteral());
    }

    @Test
    public void constructor_UnbalancedBrackets() throws Exception {

        try {
            new StringWithVariables("blah${something");
            fail("should have failed");
        }
        catch(VariableFormatException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid variable definition, missing closing bracket", msg);
        }
    }

    @Test
    public void constructor_MultipleTokens() throws Exception {

        StringWithVariables s = new StringWithVariables("${a}b${c}d${d}");
        assertEquals("${a}b${c}d${d}", s.getLiteral());
        String s2 = s.resolve((VariableProvider)null);
        assertEquals("${a}b${c}d${d}", s2);

        List<Token> tokens = s.getTokens();
        assertEquals(5, tokens.size());
        Variable v = (Variable)tokens.get(0);
        assertEquals("a", v.getName());
        Constant c = (Constant)tokens.get(1);
        assertEquals("b", c.getLiteral());
        Variable v2 = (Variable)tokens.get(2);
        assertEquals("c", v2.getName());
        Constant c2 = (Constant)tokens.get(3);
        assertEquals("d", c2.getLiteral());
        Variable v3 = (Variable)tokens.get(4);
        assertEquals("d", v3.getName());
    }

    // resolve with key/value pair list --------------------------------------------------------------------------------

    @Test
    public void resolveWithKeyValuePairs() throws Exception {

        StringWithVariables s = new StringWithVariables("a ${b} c ${d}");

        String s2 = s.resolve("b", "BValue", "d", "DValue", "x", "XValue", "y", "yValue");

        assertEquals("a BValue c DValue", s2);
    }

    @Test
    public void resolveWithKeyValuePairs_MissingKeyValue() throws Exception {

        StringWithVariables s = new StringWithVariables("a ${b} c ${d}");

        String s2 = s.resolve("x", "XValue", "y", "yValue", "d", "DValue");

        assertEquals("a ${b} c DValue", s2);
    }

    @Test
    public void resolveWithKeyValuePairs_OddElementCount() throws Exception {

        StringWithVariables s = new StringWithVariables("a ${b} c ${d}");

        String s2 = s.resolve("b", "BValue", "d");

        assertEquals("a BValue c ${d}", s2);
    }

    // resolve with key/value pair map ---------------------------------------------------------------------------------

    @Test
    public void resolveWithKeyValuePairs_Map() throws Exception {

        StringWithVariables s = new StringWithVariables("a ${b} c ${d}");

        Map<String, String> map = new HashMap<>();
        map.put("b", "BValue");
        map.put("d", "DValue");
        map.put("x", "XValue");
        map.put("y", "yValue");

        String s2 = s.resolve(map);

        assertEquals("a BValue c DValue", s2);
    }

    @Test
    public void resolveWithKeyValuePairs_MissingKeyValue_Map() throws Exception {

        StringWithVariables s = new StringWithVariables("a ${b} c ${d}");

        Map<String, String> map = new HashMap<>();
        map.put("d", "DValue");
        map.put("x", "XValue");
        map.put("y", "yValue");

        String s2 = s.resolve(map);

        assertEquals("a ${b} c DValue", s2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
