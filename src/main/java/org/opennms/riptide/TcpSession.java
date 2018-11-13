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
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;

public class TcpSession {
    public static class Transport {
        // FIXME fooker: Not sure if this is the right name...

        public final Inet4Address address;
        public final int port;
        public final Inet4Address gateway;
        public final int snmpInterface;
        public final int as;
        public final short mask;

        private Transport(final Builder builder) {
            this.address = builder.address;
            this.port = builder.port;
            this.gateway = builder.gateway;
            this.snmpInterface = builder.snmpInterface;
            this.as = builder.as;
            this.mask = builder.mask;
        }

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

            public Builder withAddress(final Inet4Address address) {
                this.address = address;
                return this;
            }

            public Builder withPort(final int port) {
                this.port = port;
                return this;
            }

            public Builder withGateway(final Inet4Address gateway) {
                this.gateway = gateway;
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

            public Transport build() {
                return new Transport(this);
            }
        }

        public static TcpSession.Builder builder() {
            return new TcpSession.Builder();
        }
    }

    public final Transport client;
    public final Transport server;

    public final Timespan timespan;

    public final long sendSize;
    public final long sendPackets;

    public final long recvSize;
    public final long recvPackets;


    public TcpSession(final Builder builder) {
        this.client = Preconditions.checkNotNull(builder.client).build();
        this.server = Preconditions.checkNotNull(builder.server).build();
        this.timespan = Preconditions.checkNotNull(builder.timespan);
        this.sendSize = builder.sendSize;
        this.sendPackets = builder.sendPackets;
        this.recvSize = builder.recvSize;
        this.recvPackets = builder.recvPackets;
    }

    public static class Builder {
        private Transport.Builder client;
        private Transport.Builder server;

        private Timespan timespan;

        private long sendSize;
        private long sendPackets;

        private long recvSize;
        private long recvPackets;

        public Builder withClient(final Transport.Builder client) {
            this.client = client;
            return this;
        }

        public Builder withServer(final Transport.Builder server) {
            this.server = server;
            return this;
        }

        public Builder withTimespan(final Timespan timespan) {
            this.timespan = timespan;
            return this;
        }

        public Builder withSendSize(final long sendSize) {
            this.sendSize = sendSize;
            return this;
        }

        public Builder withSendPackets(final long sendPackets) {
            this.sendPackets = sendPackets;
            return this;
        }

        public Builder withRecvSize(final long recvSize) {
            this.recvSize = recvSize;
            return this;
        }

        public Builder withRecvPackets(final long recvPackets) {
            this.recvPackets = recvPackets;
            return this;
        }

        public TcpSession build() {
            return new TcpSession(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
