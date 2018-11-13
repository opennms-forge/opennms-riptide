/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.riptide;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.net.InetAddresses;

@JsonDeserialize(builder = Endpoint.Builder.class)
public class Endpoint {
    public final Inet4Address address;
    public final int port;
    public final Inet4Address gateway;
    public final int snmpInterface;
    public final int as;
    public final short mask;

    public Endpoint(final Builder builder) {
        this.address = builder.address;
        this.port = builder.port;
        this.gateway = builder.gateway;
        this.snmpInterface = builder.snmpInterface;
        this.as = builder.as;
        this.mask = builder.mask;
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class Builder {
        private Inet4Address address;
        private int port;
        private Inet4Address gateway;
        private int snmpInterface;
        private int as;
        private short mask;

        public Builder() {
            this.port = new Random().nextInt(0xFFFF);
            this.gateway = InetAddresses.fromInteger(0);
            this.snmpInterface = 1;
        }

        public Builder withAddress(final String address) {
            try {
                this.address = InetAddresses.getCoercedIPv4Address(Inet4Address.getByName(address));
            } catch (UnknownHostException e) {
                throw new RuntimeException("Failed to parse IPv4 address for address", e);
            }
            return this;
        }

        public Builder withPort(final int port) {
            this.port = port;
            return this;
        }

        public Builder withGateway(final String gateway) {
            try {
                this.gateway = InetAddresses.getCoercedIPv4Address(Inet4Address.getByName(gateway));
            } catch (UnknownHostException e) {
                throw new RuntimeException("Failed to parse IPv4 address for gateway", e);
            }
            return this;
        }

        public Builder withSnmpInterface(final int snmpInterface) {
            this.snmpInterface = snmpInterface;
            return this;
        }

        public Builder withAS(final int as) {
            this.as = as;
            return this;
        }

        public Builder withMask(final short mask) {
            this.mask = mask;
            return this;
        }

        public Endpoint build() {
            return new Endpoint(this);
        }
    }

    public static TcpSession.Builder builder() {
        return new TcpSession.Builder();
    }
}
