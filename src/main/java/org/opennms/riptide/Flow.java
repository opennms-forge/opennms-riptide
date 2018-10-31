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
import static org.opennms.riptide.BufferUtils.uint32;
import static org.opennms.riptide.BufferUtils.uint8;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;

public class Flow {

    public final static int VERSION = 5;

    public enum SamplingMode {
        Unassigned(0),
        SystematicCountBased(1),
        RandomNoutOfN(2);

        public final int value;

        SamplingMode(final int value) {
            this.value = value;
        }
    }

    public static class Record {
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

        private Record(final Builder builder) {
            this.srcAddr = builder.srcAddr;
            this.dstAddr = builder.dstAddr;
            this.nextHop = builder.nextHop;
            this.snmpInputIface = builder.snmpInputIface;
            this.snmpOutputIface = builder.snmpOutputIface;
            this.packets = builder.packets;
            this.octets = builder.octets;
            this.firstUptime = builder.firstUptime;
            this.lastUptime = builder.lastUptime;
            this.srcPort = builder.srcPort;
            this.dstPort = builder.dstPort;
            this.tcpFlags = builder.tcpFlags;
            this.protocol = builder.protocol;
            this.srcAS = builder.srcAS;
            this.dstAS = builder.dstAS;
            this.tos = builder.tos;
            this.srcMask = builder.srcMask;
            this.dstMask = builder.dstMask;
            this.egress = builder.egress;
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

            public Record build() {
                return new Record(this);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    public final Duration uptime;
    public final Instant time;

    public final long flowSequence;

    public final short engineType;
    public final short engineId;

    public final SamplingMode samplingMode;
    public final int samplingInterval;

    public final List<Record> records;

    private Flow(final Builder builder) {
        this.uptime = builder.uptime;
        this.time = builder.time;
        this.flowSequence = builder.flowSequence;
        this.engineType = builder.engineType;
        this.engineId = builder.engineId;
        this.samplingMode = builder.samplingMode;
        this.samplingInterval = builder.samplingInterval;
        this.records = builder.records.stream()
                .map(Record.Builder::build)
                .collect(Collectors.toList());

    }

    public static class Builder {
        private static long AUTO_SEQUENCE = 1;

        private Duration uptime;
        private Instant time;

        private long flowSequence;

        private short engineType;
        private short engineId;

        private Flow.SamplingMode samplingMode;
        private int samplingInterval;

        private List<Record.Builder> records;

        private Builder() {
            this.uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()); // TODO fooker: Better default value?
            this.time = Instant.now();
            this.flowSequence = AUTO_SEQUENCE++;
            this.samplingMode = Flow.SamplingMode.Unassigned;
            this.records = Lists.newLinkedList();
        }

        public Builder withUptime(final Duration uptime) {
            this.uptime = uptime;
            return this;
        }

        public Builder withTime(final Instant time) {
            this.time = time;
            return this;
        }

        public Builder withFlowSequence(final long flowSequence) {
            this.flowSequence = flowSequence;
            return this;
        }

        public Builder withEngineType(final short engineType) {
            this.engineType = engineType;
            return this;
        }

        public Builder withEngineId(final short engineId) {
            this.engineId = engineId;
            return this;
        }

        public Builder withSamplingMode(final Flow.SamplingMode samplingMode) {
            this.samplingMode = samplingMode;
            return this;
        }

        public Builder withSamplingInterval(final int samplingInterval) {
            this.samplingInterval = samplingInterval;
            return this;
        }

        public Builder withRecord(final Record.Builder builder) {
            this.records.add(builder);
            return this;
        }

        public Flow build() {
            return new Flow(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public ByteBuffer write() {
        final ByteBuffer buffer = ByteBuffer.allocate(24 + 48 * this.records.size());

        uint16(buffer, VERSION);
        uint16(buffer, this.records.size());
        uint32(buffer, this.uptime.toMillis());
        uint32(buffer, this.time.getEpochSecond());
        uint32(buffer, this.time.getNano());
        uint32(buffer, this.flowSequence);
        uint8(buffer, this.engineType);
        uint8(buffer, this.engineId);
        uint16(buffer, this.samplingMode.value << 14 | this.samplingInterval);

        for (final Record record: this.records) {
            bytes(buffer, record.srcAddr.getAddress());
            bytes(buffer, record.dstAddr.getAddress());
            bytes(buffer, record.nextHop.getAddress());

            uint16(buffer, record.snmpInputIface);
            uint16(buffer, record.snmpOutputIface);

            uint32(buffer, record.packets);
            uint32(buffer, record.octets);

            uint32(buffer, record.firstUptime.toMillis());
            uint32(buffer, record.lastUptime.toMillis());

            uint16(buffer, record.srcPort);
            uint16(buffer, record.dstPort);

            uint8(buffer, 0); // Padding 1

            uint8(buffer, record.tcpFlags);
            uint8(buffer, record.protocol);
            uint8(buffer, record.tos);

            uint16(buffer, record.srcAS);
            uint16(buffer, record.dstAS);

            uint8(buffer, record.srcMask);
            uint8(buffer, record.dstMask);

            uint8(buffer, 0); // Padding 2
            uint8(buffer, record.egress ? 0x80 : 0x00);
        }

        return buffer;
    }
}
