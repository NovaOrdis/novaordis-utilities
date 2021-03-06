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
 * The common behavior shared among variable implementations.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/13/17
 */
abstract class VariableBase<T> implements Variable<T> {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;

    private T value;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @exception IllegalNameException
     */
    VariableBase(String name) {

        this.name = Variable.validateVariableName(name);
    }

    // Variable implementation -----------------------------------------------------------------------------------------

    @Override
    public String name() {

        return name;
    }

    @Override
    public T get() {

        return value;
    }

    @Override
    public T set(T value) {

        T old = this.value;

        this.value = value;

        return old;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return name + "=" + get();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
