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

import io.novaordis.utilities.NotSupportedException;
import io.novaordis.utilities.env.EnvironmentVariableProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A scope that is aware of the environment variables declared in the OS process environment.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/17/17
 */
public class OSProcessScope extends ScopeBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private EnvironmentVariableProvider environmentVariableProvider;

    //
    // we keep track of variables we declared to identify duplicate declarations
    //

    private List<String> declaredVariableNames;

    // Constructors ----------------------------------------------------------------------------------------------------

    public OSProcessScope() {

        this.declaredVariableNames = new ArrayList<>();
    }

    // ScopeBase overrides ---------------------------------------------------------------------------------------------

    /**
     * Depending on the underlying EnvironmentVariableProvider implementation, declaring environment variables in the
     * process' environment may not be possible, and this method may throw an exception.
     */
    @Override
    public <T> Variable<T> declare(String name, Class<? extends T> type) {

        return declareEnvironmentVariable(name, type, null);
    }

    /**
     * Depending on the underlying EnvironmentVariableProvider implementation, declaring environment variables in the
     * process' environment may not be possible, and this method may throw an exception.
     */
    @Override
    public <T> Variable<T> declare(String name, Class<? extends T> type, T value) {

        return declareEnvironmentVariable(name, type, value);
    }

    /**
     * Depending on the underlying EnvironmentVariableProvider implementation, declaring environment variables in the
     * process' environment may not be possible, and this method may throw an exception.
     */
    @Override
    public <T> Variable<T> declare(String name, T value) {

        return declareEnvironmentVariable(name, null, value);
    }

    /**
     * Depending on the underlying EnvironmentVariableProvider implementation, undeclaring environment variables in the
     * process' environment may not be possible, and this method may throw an exception.
     */
    @Override
    public Variable undeclare(String name) {

        EnvironmentVariableProxy v =  getVariable(name);

        if (v == null) {

            return null;
        }

        v.setUndeclared();

        environmentVariableProvider.unset(name);
        declaredVariableNames.remove(name);

        return v;
    }

    /**
     * Overrides superclass, relies on whatever it finds in the environment.
     * @param name
     * @return
     */
    @Override
    public EnvironmentVariableProxy getVariable(String name) {

        //
        // there's never an enclosing scope, we cannot control that from the JVM
        //

        String value = environmentVariableProvider.getenv(name);

        if (value == null) {

            return null;
        }

        return new EnvironmentVariableProxy(this, name);
    }

    @Override
    public List<Variable> getVariablesDeclaredInScope() {

        if (declaredVariableNames.isEmpty()) {

            return Collections.emptyList();
        }

        List<Variable> result = new ArrayList<>();

        for(String name: declaredVariableNames) {

            result.add(getVariable(name));
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    EnvironmentVariableProvider getEnvironmentVariableProvider() {

        return environmentVariableProvider;
    }

    /**
     * For testing.
     */
    void setEnvironmentVariableProvider(EnvironmentVariableProvider p) {

        this.environmentVariableProvider = p;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    public <T> Variable<T> declareEnvironmentVariable(String name, Class<? extends T> type, T value) {

        //
        // cannot declare undefined environment variables
        //

        if (value == null) {

            throw new NotSupportedException("cannot declare undefined environment variables");
        }

        Class effectiveType;

        if (type == null) {

            effectiveType = value.getClass();
        }
        else {

            effectiveType = type;
        }

        if (!String.class.equals(effectiveType)) {

            throw new NotSupportedException("cannot declare " + effectiveType + " variables");
        }

        if (declaredVariableNames.contains(name)) {

            throw new DuplicateDeclarationException(name);
        }

        environmentVariableProvider.export(name, value.toString());

        declaredVariableNames.add(name);

        //noinspection unchecked
        return getVariable(name);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
