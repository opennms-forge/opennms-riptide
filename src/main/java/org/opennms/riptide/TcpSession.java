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

import java.time.Duration;
import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Preconditions;

@JsonDeserialize(builder = TcpSession.Builder.class)
public class TcpSession {
    public final Endpoint client;
    public final Endpoint server;

    public final Timespan timespan;

    public final long sendSize;
    public final long sendPackets;

    public final long recvSize;
    public final long recvPackets;

    public TcpSession(final Builder builder) {
        this.client = Preconditions.checkNotNull(builder.client);
        this.server = Preconditions.checkNotNull(builder.server);
        this.timespan = Preconditions.checkNotNull(builder.timespan);
        this.sendSize = builder.sendSize;
        this.sendPackets = builder.sendPackets;
        this.recvSize = builder.recvSize;
        this.recvPackets = builder.recvPackets;
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class Builder {
        private Endpoint client;
        private Endpoint server;

        private Timespan timespan;

        private long sendSize;
        private long sendPackets;

        private long recvSize;
        private long recvPackets;

        public Builder withClient(final Endpoint client) {
            this.client = client;
            return this;
        }

        public Builder withServer(final Endpoint server) {
            this.server = server;
            return this;
        }

        public Builder withTimespan(final Timespan timespan) {
            this.timespan = timespan;
            return this;
        }

        public Builder withDuration(final Duration duration) {
            final Instant now = Instant.now();
            this.timespan = new Timespan(now, now.plus(duration));
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
