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

package io.novaordis.utilities.os;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/16
 */
public class WindowsOS extends OSBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Must not be invoked from outside the package.
     * Use OS.getInstance() to create the appropriate instance.
     *
     * @see OS#getInstance()
     */
    protected WindowsOS() throws Exception {
    }

    // OS implementation -----------------------------------------------------------------------------------------------

    @Override
    public WindowsOSConfiguration getConfiguration() {
        throw new RuntimeException("getConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public NativeExecutionResult execute(String command) throws NativeExecutionException {
        throw new RuntimeException("execute() NOT YET IMPLEMENTED");
    }

    @Override
    public String getName() {
        return "Windows";
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
