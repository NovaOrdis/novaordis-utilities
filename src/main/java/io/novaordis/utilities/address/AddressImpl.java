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

package io.novaordis.utilities.address;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/12/17
 */
public class AddressImpl implements Address {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String PROTOCOL_SEPARATOR = "://";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String protocol;
    private String host;
    private Integer port;
    private boolean portSpecifiedInLiteral;
    private String username;
    private char[] password;

    //
    // IMPORTANT: if adding more state here, make sure copy() copies over when creating a new instance.
    //

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param address the string representation of the address in the format:
     *                protocol://[username[:password]]@hostname[:port]
     * @throws AddressException
     */
    public AddressImpl(String address) throws AddressException {

        parse(address);
    }

    public AddressImpl(String protocol, String username, String password, String host, Integer port) {

        setProtocol(protocol);
        setUsername(username);
        setPassword(password == null ? null : password.toCharArray());
        setHost(host);
        setPort(port);

        if (port != null) {

            portSpecifiedInLiteral = true;
        }
    }

    /**
     * For use by copy().
     */
    protected AddressImpl() {
    }

    // Address implementation ------------------------------------------------------------------------------------------

    @Override
    public String getProtocol() {

        return protocol;
    }

    @Override
    public void setProtocol(String p) {

        this.protocol = p;
    }

    @Override
    public String getHost() {

        return host;
    }

    @Override
    public Integer getPort() {

        return port;
    }

    @Override
    public void setPort(Integer port) {

        this.port = port;
    }

    @Override
    public String getUsername() {

        return username;
    }

    @Override
    public char[] getPassword() {

        return password;
    }

    @Override
    public String getLiteral() {

        String s = "";

        if (protocol != null) {

            s += protocol + PROTOCOL_SEPARATOR;
        }

        if (username != null) {

            s += username + "@";
        }

        s += host;

        if (port != null && portSpecifiedInLiteral) {

            s += ":";
            s += port;
        }

        return s;
    }

    @Override
    public AddressImpl copy() {

        AddressImpl copy = createBlankInstance();

        copy.setProtocol(getProtocol());
        copy.setHost(getHost());
        copy.setPort(getPort());
        copy.setUsername(getUsername());
        copy.setPassword(getPassword());
        copy.portSpecifiedInLiteral = this.portSpecifiedInLiteral;

        return copy;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * This method is public because there are situations when an externally specified username (for example, on
     * command line with --username=) must be set on the address.
     */
    public void setUsername(String username) {

        this.username = username;
    }

    /**
     * This method is public because there are situations when an externally specified password (for example, on
     * command line with --password=) must be set on the address.
     */
    public void setPassword(char[] password) {

        if (password == null) {

            this.password = null;
        }
        else {

            this.password = new char[password.length];
            System.arraycopy(password, 0, this.password, 0, this.password.length);
        }
    }

    @Override
    public boolean equals(Object o)  {

        if (this == o) {

            return true;
        }

        if (!(o instanceof AddressImpl)) {

            return false;
        }

        AddressImpl that = (AddressImpl)o;

        if (this.protocol == null) {

            if (that.protocol != null) {

                return false;
            }
        }
        else if (!this.protocol.equals(that.protocol)) {

            return false;
        }

        if (this.host == null) {

            return false;
        }

        if (!this.host.equals(that.host)) {

            return false;
        }

        if (this.username == null) {

            if (that.username != null) {

                return false;
            }
        }
        else {

            if (!this.username.equals(that.username)) {

                return false;
            }
        }

        if (this.port == null) {

            if (that.port != null) {

                return false;
            }
        }
        else {

            if (!this.port.equals(that.port)) {

                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        return getLiteral().hashCode();
    }

    @Override
    public String toString() {

        String s = protocol == null ? "" : protocol + PROTOCOL_SEPARATOR;

        if (username != null) {

            s += username + ":***@";
        }

        s += host;

        if (port != null) {

            s += ":";
            s += port;
        }

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setHost(String host) {

        this.host = host;
    }

    protected boolean isPortSpecifiedInLiteral() {

        return portSpecifiedInLiteral;
    }

    protected AddressImpl createBlankInstance() {

        return new AddressImpl();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse(String address) throws AddressException {

        if (address == null) {

            throw new IllegalArgumentException("null address");
        }

        int i = address.indexOf(PROTOCOL_SEPARATOR);

        if (i != -1) {

            String p = address.substring(0, i);
            setProtocol(p);
            address = address.substring(i + PROTOCOL_SEPARATOR.length());
        }

        i = address.indexOf('@');
        String hostAndPort;

        if (i != -1) {

            //
            // username and password
            //

            String usernameAndPassword = address.substring(0, i);
            hostAndPort = address.substring(i + 1);

            i = usernameAndPassword.indexOf(':');

            if (i == -1) {

                this.username = usernameAndPassword;
            }
            else {

                this.username = usernameAndPassword.substring(0, i);
                this.password = usernameAndPassword.substring(i + 1).toCharArray();
            }
        }
        else {

            hostAndPort = address;
        }

        //
        // allow for a trailing slash - for the time being in WILL NOT be reflected in getLiteral()
        //

        if (hostAndPort.endsWith("/")) {

            hostAndPort = hostAndPort.substring(0, hostAndPort.length() - 1);
        }

        i = hostAndPort.indexOf(':');

        if (i == -1) {

            //
            // no port
            //

            host = hostAndPort.trim();
        }
        else {

            host = hostAndPort.substring(0, i).trim();

            if (host.isEmpty()) {

                throw new AddressException("invalid host specification");
            }

            String s = hostAndPort.substring(i + 1).trim();

            if (s.isEmpty()) {

                throw new AddressException("missing port value");
            }

            int p;

            try {

                p = Integer.parseInt(s);
            }
            catch(Exception e) {

                throw new AddressException("invalid port \"" + s + "\"");
            }

            if (p <= 0 || p >= 65536) {

                throw new AddressException("port value out of bounds: " + p);
            }

            portSpecifiedInLiteral = true;
            setPort(p);
        }


        if (host != null && host.isEmpty()) {

            //
            // set the host to null, give the subclass the change to implement a default host mechanism
            //

            setHost(null);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
