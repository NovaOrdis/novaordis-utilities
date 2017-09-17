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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/17/17
 */
public class UndeclaredVariableException extends Exception {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String undeclaredVariableName;

    // Constructors ----------------------------------------------------------------------------------------------------

    public UndeclaredVariableException(String undeclaredVariableName, String msg) {

        super(msg);

        if (undeclaredVariableName == null) {

            throw new IllegalArgumentException("null undeclared variable name");
        }

        this.undeclaredVariableName = undeclaredVariableName;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getUndeclaredVariableName() {

        return undeclaredVariableName;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}