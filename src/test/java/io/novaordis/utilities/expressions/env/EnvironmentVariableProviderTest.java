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

package io.novaordis.utilities.expressions.env;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class EnvironmentVariableProviderTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private Logger log = LoggerFactory.getLogger(EnvironmentVariableProviderTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void nullName() throws Exception {

        EnvironmentVariableProvider p = getEnvironmentVariableProviderToTest();

        try {

            p.getenv(null);
            fail("should have thrown exception");
        }
        catch(NullPointerException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void noSuchName() throws Exception {

        EnvironmentVariableProvider p = getEnvironmentVariableProviderToTest();

        String s = p.getenv("I_AM_PRETTY_SURE_THERE_IS_NO_SUCH_ENVIRONMENT_VARIABLE");
        assertNull(s);
    }

    @Test
    public void home() throws Exception {

        EnvironmentVariableProvider p = getEnvironmentVariableProviderToTest();

        String s = p.getenv("HOME");
        assertNotNull(s);
        log.info(s);
    }

    // getInstance() ---------------------------------------------------------------------------------------------------

    @Test
    public void getInstance_Default() throws Exception {

        assertNull(System.getProperty(
                EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY));

        EnvironmentVariableProvider p = EnvironmentVariableProvider.getInstance();
        assertTrue(p instanceof SystemEnvironmentVariableProvider);

        EnvironmentVariableProvider p2 = EnvironmentVariableProvider.getInstance();
        assertTrue(p == p2);
    }

    @Test
    public void getInstance_MockInstance() throws Exception {

        try {

            System.setProperty(
                    EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY,
                    MockEnvironmentVariableProvider.class.getName());

            EnvironmentVariableProvider p = EnvironmentVariableProvider.getInstance();
            assertTrue(p instanceof MockEnvironmentVariableProvider);

            EnvironmentVariableProvider p2 = EnvironmentVariableProvider.getInstance();
            assertTrue(p == p2);
        }
        finally {

            System.clearProperty(EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY);
            EnvironmentVariableProvider.reset();
        }
    }

    // reset() ---------------------------------------------------------------------------------------------------------

    @Test
    public void reset() throws Exception {

        EnvironmentVariableProvider.reset();
        assertNull(EnvironmentVariableProvider.INSTANCE[0]);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract EnvironmentVariableProvider getEnvironmentVariableProviderToTest() throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
