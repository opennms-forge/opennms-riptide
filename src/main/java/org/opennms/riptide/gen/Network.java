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

package org.opennms.riptide.gen;

import java.net.Inet4Address;
import java.util.Iterator;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;

public class Network implements Iterable<Inet4Address> {

    private final int base;
    private final int mask;

    public Network(final String base, final int mask) {
        this(InetAddresses.getCoercedIPv4Address(InetAddresses.forString(base)), mask);
    }

    public Network(final Inet4Address base, final int mask) {
        this(Ints.fromByteArray(base.getAddress()), mask);
    }

    public Network(final int base, final int mask) {
        this.base = base & (0xFFFFFFFF << (32 - mask));
        this.mask = mask;
    }

    public Inet4Address getBase() {
        return InetAddresses.fromInteger(this.base);
    }

    public int getMask() {
        return this.mask;
    }

    public int pick(final Random random) {
        return this.base + 1 + random.nextInt(this.size() - 2);
    }

    public int size() {
        return 1 << (32 - this.mask);
    }

    @Override
    public Iterator<Inet4Address> iterator() {
        return new NetworkIterator(this);
    }

    private static class NetworkIterator implements Iterator<Inet4Address> {

        private final Network network;
        private int index = 0;

        private NetworkIterator(Network network) {
            this.network = network;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.network.size() - 2;
        }

        @Override
        public Inet4Address next() {
            return InetAddresses.fromInteger(this.network.base + (++this.index));
        }
    }

    public static Network parse(final String network) {
        final int slash = network.indexOf('/');
        Preconditions.checkArgument(slash != -1, "Network must be in format 'address/mask'");

        final Inet4Address base = InetAddresses.getCoercedIPv4Address(InetAddresses.forString(network.substring(0, slash)));
        final int mask = Integer.parseInt(network.substring(slash + 1));

        return new Network(base, mask);
    }
}
