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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class SimpleSender implements Simulation.Sender {
    private final static Logger LOG = LoggerFactory.getLogger(SimpleSender.class);

    private final List<Inet4SockAddr> targets;
    private final DatagramSocket datagramSocket;

    public SimpleSender(final List<Inet4SockAddr> targets) throws SocketException {
        this.targets = Preconditions.checkNotNull(targets);

        this.datagramSocket = new DatagramSocket();
    }

    @Override
    public void send(final Flow flow) {
        final byte[] buffer = flow.write().array();

        try {
            for (final Inet4SockAddr addr : this.targets) {
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr.toSocketAddress());

                LOG.debug("Sending packet to {} ({} bytes)", addr, packet.getLength());
                datagramSocket.send(packet);
            }
        } catch (final IOException e) {
            LOG.error("Failed to send packet", e);
        }
    }
}
