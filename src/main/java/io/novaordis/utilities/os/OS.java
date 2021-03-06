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

import io.novaordis.utilities.Files;
import org.slf4j.Logger;

import java.io.File;

/**
 * A proxy for the underlying operating system. A thread-safe JVM-level singleton that can be used for native command
 * execution.
 *
 * Implementations must correctly implement equals() and hashCode().
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/16
 */
public interface OS extends NativeExecutor {

    // Constants -------------------------------------------------------------------------------------------------------

    String OS_IMPLEMENTATION_PROPERTY_NAME = "os.class";

    //
    // Conventional OS names
    //

    @SuppressWarnings("unused")
    String MacOS = "MacOS";

    @SuppressWarnings("unused")
    String Linux = "Linux";

    @SuppressWarnings("unused")
    String Windows = "Windows";

    //
    // cached instance
    //

    OS[] instance = new OS[1];

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Build and return the OS implementation appropriated for this system. The default behavior can be modified by
     * setting "os.class" system property to contain a fully qualified class name of an OS implementation.
     *
     * The method caches the instance internally upon creation. It is guaranteed that two successive invocations return
     * instances that are identical (instance1 == instance2).
     */
    static OS getInstance() throws Exception {

        synchronized (instance) {

            if (instance[0] != null) {

                return instance[0];
            }

            //
            // first attempt to look up a custom implementation - this is mainly useful while testing
            //

            String osImplementationClassName = System.getProperty(OS_IMPLEMENTATION_PROPERTY_NAME);

            if (osImplementationClassName != null) {
                Class c = Class.forName(osImplementationClassName);
                instance[0] = (OS) c.newInstance();
            }
            else {

                String osName = System.getProperty("os.name");

                if (osName == null) {
                    throw new IllegalStateException(
                            "'" + OS_IMPLEMENTATION_PROPERTY_NAME + "' or 'os.name' system properties not available");
                }

                String lcOsName = osName.toLowerCase();

                if (lcOsName.contains("mac")) {

                    instance[0] = new MacOS();
                }
                else if (lcOsName.contains("linux")) {

                    instance[0] = new LinuxOS();
                }
                else if (lcOsName.contains("windows")) {

                    instance[0] = new WindowsOS();
                }
                else {
                    throw new IllegalStateException("unrecognized 'os.name' value " + osName);
                }
            }

            return instance[0];
        }
    }

    /**
     * Clears the cached OS instance, if any.
     */
    static void clearInstance() {

        synchronized (instance) {
            instance[0] = null;
        }
    }

    static void logExecution(Logger log, File directory, String command) {

        if (log == null) {

            return;
        }

        String location = "";

        if (directory != null) {

            location = directory.getAbsolutePath();
            location = Files.normalizePath(location);
            location = " in " + location;
        }

        log.debug("executing \"" + command + "\"" + location);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @see OSConfiguration
     */
    OSConfiguration getConfiguration();


    /**
     * "Linux" (OS.Linux), "MacOS" (OS.MacOS), "Windows" (OS.Windows).
     *
     * @see OS#Linux
     * @see OS#MacOS
     * @see OS#Windows
     */
    String getName();

}
