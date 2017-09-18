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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockEnvironmentVariableProvider implements EnvironmentVariableProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> environment;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockEnvironmentVariableProvider() {

        this.environment = new HashMap<>();
    }

    // EnvironmentVariableProvider implementation ----------------------------------------------------------------------

    @Override
    public String getenv(String name) {

        if (name == null) {

            throw new NullPointerException();
        }

        return environment.get(name);
    }

    @Override
    public void export(String name, String value) {
        throw new RuntimeException("export() NOT YET IMPLEMENTED");
    }

    @Override
    public void unset(String name) {
        throw new RuntimeException("unset() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void installEnvironmentVariable(String name, String value) {

        environment.put(name, value);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
