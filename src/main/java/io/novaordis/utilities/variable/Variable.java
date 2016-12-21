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

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class Variable implements Token {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;
    private boolean failOnMissingDefinition;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Default behavior is to NOT fail on missing variable definition upon resolve.
     */
    public Variable(String variableName) {

        this(variableName, false);
    }

    public Variable(String variableName, boolean failOnMissingDefinition) {

        this.name = variableName;
        this.failOnMissingDefinition = failOnMissingDefinition;
    }

    // Token implementation --------------------------------------------------------------------------------------------

    @Override
    public String getLiteral() {

        if (name == null) {
            return null;
        }

        return "${" + name + "}";
    }

    @Override
    public String resolve(VariableProvider provider) throws VariableNotDefinedException {

        if (provider == null) {

            if (failOnMissingDefinition) {

                throw new VariableNotDefinedException(name, "\"" + name + "\" not defined, missing provider");
            }

            return getLiteral();
        }

        String value = provider.getVariableValue(name);

        if (value == null) {

            if (failOnMissingDefinition) {

                throw new VariableNotDefinedException(name);
            }

            return getLiteral();
        }

        return value;
    }

    @Override
    public String resolve(Map<String, String> map) throws VariableNotDefinedException {

        if (map == null) {

            if (failOnMissingDefinition) {

                throw new VariableNotDefinedException(name, "\"" + name + "\" not defined, missing map");
            }

            return getLiteral();
        }

        String value = map.get(name);

        if (value == null) {

            if (failOnMissingDefinition) {

                throw new VariableNotDefinedException(name);
            }

            return getLiteral();
        }

        return value;
    }

    @Override
    public boolean isFailOnMissingDefinition() {

        return failOnMissingDefinition;
    }

    @Override
    public void setFailOnMissingDefinition(boolean b) {

        this.failOnMissingDefinition = b;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the variable name
     */
    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return getLiteral();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
