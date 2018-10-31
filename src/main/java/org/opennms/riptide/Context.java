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
import java.net.Inet4Address;
import java.util.List;
import java.util.Random;

import org.opennms.riptide.gen.Network;
import org.opennms.riptide.gen.PortRange;

public class Context {
    private final List<Flow> flows;

    private final Random random;

    public Context(final List<Flow> flows, final Random random) {
        this.flows = flows;
        this.random = random;
    }

    public Flow.Builder flow() {
        return Flow.builder();
    }

    public Flow.Record.Builder record() {
        return Flow.Record.builder();
    }

    public Network network(final Inet4Address base, final int mask) {
        return new Network(base, mask);
    }

    public Network network(final String base, final int mask) {
        return new Network(base, mask);
    }

    public Network network(final String network) {
        return Network.parse(network);
    }

    public PortRange ports(final int min, final int max) {
        return new PortRange(min, max);
    }

    public void send(final Flow flow) throws IOException {
        this.flows.add(flow);
    }

    public void send(final Flow.Builder flow) throws IOException {
        this.send(flow.build());
    }
}
