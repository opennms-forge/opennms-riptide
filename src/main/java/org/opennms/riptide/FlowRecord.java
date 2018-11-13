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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;

public class FlowRecord {
    public final Inet4Address srcAddr;
    public final Inet4Address dstAddr;
    public final Inet4Address nextHop;

    public final int snmpInputIface;
    public final int snmpOutputIface;

    public final long packets;
    public final long octets;

    public final Duration firstUptime;
    public final Duration lastUptime;

    public final int srcPort;
    public final int dstPort;

    public final short tcpFlags;
    public final short protocol;

    public final int srcAS;
    public final int dstAS;

    public final short tos;

    public final short srcMask;
    public final short dstMask;

    public final boolean egress;

    private FlowRecord(final Builder builder) {
        this.srcAddr = Preconditions.checkNotNull(builder.srcAddr);
        this.dstAddr = Preconditions.checkNotNull(builder.dstAddr);
        this.nextHop = Preconditions.checkNotNull(builder.nextHop);
        this.snmpInputIface = Preconditions.checkNotNull(builder.snmpInputIface);
        this.snmpOutputIface = Preconditions.checkNotNull(builder.snmpOutputIface);
        this.packets = Preconditions.checkNotNull(builder.packets);
        this.octets = Preconditions.checkNotNull(builder.octets);
        this.firstUptime = Preconditions.checkNotNull(builder.firstUptime);
        this.lastUptime = Preconditions.checkNotNull(builder.lastUptime);
        this.srcPort = Preconditions.checkNotNull(builder.srcPort);
        this.dstPort = Preconditions.checkNotNull(builder.dstPort);
        this.tcpFlags = Preconditions.checkNotNull(builder.tcpFlags);
        this.protocol = Preconditions.checkNotNull(builder.protocol);
        this.srcAS = Preconditions.checkNotNull(builder.srcAS);
        this.dstAS = Preconditions.checkNotNull(builder.dstAS);
        this.tos = Preconditions.checkNotNull(builder.tos);
        this.srcMask = Preconditions.checkNotNull(builder.srcMask);
        this.dstMask = Preconditions.checkNotNull(builder.dstMask);
        this.egress = Preconditions.checkNotNull(builder.egress);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("srcAddr", srcAddr)
                .add("dstAddr", dstAddr)
                .add("nextHop", nextHop)
                .add("snmpInputIface", snmpInputIface)
                .add("snmpOutputIface", snmpOutputIface)
                .add("packets", packets)
                .add("octets", octets)
                .add("firstUptime", firstUptime)
                .add("lastUptime", lastUptime)
                .add("srcPort", srcPort)
                .add("dstPort", dstPort)
                .add("tcpFlags", tcpFlags)
                .add("protocol", protocol)
                .add("srcAS", srcAS)
                .add("dstAS", dstAS)
                .add("tos", tos)
                .add("srcMask", srcMask)
                .add("dstMask", dstMask)
                .add("egress", egress)
                .toString();
    }

    public static class Builder {
        private Inet4Address srcAddr;
        private Inet4Address dstAddr;
        private Inet4Address nextHop;

        private int snmpInputIface;
        private int snmpOutputIface;

        private long packets;
        private long octets;

        private Duration firstUptime;
        private Duration lastUptime;

        private int srcPort;
        private int dstPort;

        private short tcpFlags;
        private short protocol;

        private int srcAS;
        private int dstAS;

        private short tos;

        private short srcMask;
        private short dstMask;

        private boolean egress;

        private Builder() {
            this.srcAddr = InetAddresses.fromInteger(0);
            this.dstAddr = InetAddresses.fromInteger(0);
            this.nextHop = InetAddresses.fromInteger(0);
            this.firstUptime = Duration.ZERO;
            this.lastUptime = Duration.ZERO;
        }

        public Builder withSrcAddr(final Inet4Address srcAddr) {
            this.srcAddr = srcAddr;
            return this;
        }

        public Builder withSrcAddr(final int b1, final int b2, final int b3, final int b4) throws UnknownHostException {
            this.srcAddr = (Inet4Address) InetAddress.getByAddress(new byte[]{(byte) b1, (byte) b2, (byte) b3, (byte) b4});
            return this;
        }

        public Builder withDstAddr(final Inet4Address dstAddr) {
            this.dstAddr = dstAddr;
            return this;
        }

        public Builder withNextHop(final Inet4Address nextHop) {
            this.nextHop = nextHop;
            return this;
        }

        public Builder withSnmpInputIface(final int snmpInputIface) {
            this.snmpInputIface = snmpInputIface;
            return this;
        }

        public Builder withSnmpOutputIface(final int snmpOutputIface) {
            this.snmpOutputIface = snmpOutputIface;
            return this;
        }

        public Builder withPackets(final long packets) {
            this.packets = packets;
            return this;
        }

        public Builder withOctets(final long octets) {
            this.octets = octets;
            return this;
        }

        public Builder withFirstUptime(final Duration firstUptime) {
            this.firstUptime = firstUptime;
            return this;
        }

        public Builder withLastUptime(final Duration lastUptime) {
            this.lastUptime = lastUptime;
            return this;
        }

        public Builder withSrcPort(final int srcPort) {
            this.srcPort = srcPort;
            return this;
        }

        public Builder withDstPort(final int dstPort) {
            this.dstPort = dstPort;
            return this;
        }

        public Builder withTcpFlags(final short tcpFlags) {
            this.tcpFlags = tcpFlags;
            return this;
        }

        public Builder withProtocol(final short protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder withSrcAS(final int srcAS) {
            this.srcAS = srcAS;
            return this;
        }

        public Builder withDstAS(final int dstAS) {
            this.dstAS = dstAS;
            return this;
        }

        public Builder withTos(final short tos) {
            this.tos = tos;
            return this;
        }

        public Builder withSrcMask(final short srcMask) {
            this.srcMask = srcMask;
            return this;
        }

        public Builder withDstMask(final short dstMask) {
            this.dstMask = dstMask;
            return this;
        }

        public Builder withEgress(final boolean egress) {
            this.egress = egress;
            return this;
        }

        public FlowRecord build() {
            return new FlowRecord(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
