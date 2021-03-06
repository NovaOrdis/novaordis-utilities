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

package io.novaordis.utilities.help;

import io.novaordis.utilities.UserErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static utilities for in-line help.
 *
 * See https://kb.novaordis.com/index.php/Project_In-Line_Help
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/24/17
 */
public class InLineHelp {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final String HELP_FILE_NAME = "help.txt";

    // Static ----------------------------------------------------------------------------------------------------------

    public static String get() throws UserErrorException {

        return get(null);
    }

    public static String get(String applicationName) throws UserErrorException {

        return get(applicationName, HELP_FILE_NAME);
    }

    /**
     * Return in-line help text, if the corresponding file is found in the classpath, or throws exception the
     * calling layer will have to handle.
     *
     * @param applicationName optional, may be null. The human-readable application name, to create meaningful error
     *                        messages.
     * @param helpFileName the name of the file, expected to be available on the classpath, that contains the help
     *                     content.
     *
     * @exception UserErrorException if the in-line help file is not found in the classpath.
     */
    public static String get(String applicationName, String helpFileName) throws UserErrorException {

        InputStream is = InLineHelp.class.getClassLoader().getResourceAsStream(helpFileName);

        if (is == null) {

            String msg = "no " + helpFileName + " file found on the classpath; this usually means that ";
            msg += applicationName == null ? "the application" : applicationName;
            msg += " was not built or installed correctly";

            throw new UserErrorException(msg);
        }

        String help = "";
        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null) {

                help += line + "\n";
            }
        }
        catch (Exception e) {

            throw new IllegalStateException(e);
        }
        finally {

            if (br != null) {

                try {

                    br.close();
                }
                catch(IOException e) {

                    System.err.println("warn: failed to close the input stream");
                }
            }
        }

        return help;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Static package protected ----------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
