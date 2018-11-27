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

import static org.opennms.riptide.BufferUtils.bytes;
import static org.opennms.riptide.BufferUtils.uint16;
import static org.opennms.riptide.BufferUtils.uint8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.savarese.rocksaw.net.RawSocket;

public class SpoofingSender implements Simulation.Sender {
    private final static Logger LOG = LoggerFactory.getLogger(SpoofingSender.class);

    private final Inet4SockAddr source;
    private final List<Inet4SockAddr> targets;

    private final RawSocket socket;

    public SpoofingSender(final Inet4SockAddr source,
                          final List<Inet4SockAddr> targets) throws IOException {
        this.source = Preconditions.checkNotNull(source);
        this.targets = Preconditions.checkNotNull(targets);

        this.socket = new RawSocket();
        this.socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
        this.socket.setIPHeaderInclude(true);
    }

    @Override
    public void send(final Flow flow) {
        final byte[] payload = flow.write().array();

        for (final Inet4SockAddr target : this.targets) {
            LOG.debug("Sending packet from {} to {} ({} bytes)", this.source, target, payload.length);

            final ByteBuffer buffer = ByteBuffer.allocate(20 + 8 + payload.length);

            // IP Header
            uint8(buffer, (0x04 << 4) | 5); // IPv4 version and header size
            uint8(buffer,0); // DSCP and ECN
            uint16(buffer,20 + 8 + payload.length); // Length
            uint16(buffer,0); // Identification
            uint16(buffer, 0); // Flags and fragment offset
            uint8(buffer, 64); // TTL
            uint8(buffer, 17); // Protocol = UDP
            uint16(buffer, 0); // Checksum - will be calculated by kernel
            bytes(buffer, this.source.addr.getAddress()); // Source address
            bytes(buffer, target.addr.getAddress()); // Target address

            // UDP Header
            uint16(buffer, this.source.port); // Source port
            uint16(buffer, target.port); // Target port
            uint16(buffer, 8 + payload.length); // Length
            uint16(buffer, 0); // Checksum - can be omitted

            // Payload
            bytes(buffer, payload);

            try {
                this.socket.write(target.addr, buffer.array());
            } catch (final IOException e) {
                LOG.error("Failed to send packet", e);
            }
        }
    }
}
