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
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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

    public final Duration uptime;
    public final Instant time;

    public final long flowSequence;

    public final short engineType;
    public final short engineId;

    public final SamplingMode samplingMode;
    public final int samplingInterval;

    public final List<FlowRecord> records;

    private Flow(final Builder builder) {
        this.uptime = Preconditions.checkNotNull(builder.uptime);
        this.time = Preconditions.checkNotNull(builder.time);
        this.flowSequence = builder.flowSequence;
        this.engineType = builder.engineType;
        this.engineId = builder.engineId;
        this.samplingMode = Preconditions.checkNotNull(builder.samplingMode);
        this.samplingInterval = builder.samplingInterval;

        // TODO fooker: Check not empty
        this.records = Preconditions.checkNotNull(builder.records).stream()
                .map(FlowRecord.Builder::build)
                .collect(Collectors.toList());

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uptime", uptime)
                .add("time", time)
                .add("flowSequence", flowSequence)
                .add("engineType", engineType)
                .add("engineId", engineId)
                .add("samplingMode", samplingMode)
                .add("samplingInterval", samplingInterval)
                .add("records", records)
                .toString();
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

        private List<FlowRecord.Builder> records;

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

        public Builder withRecord(final FlowRecord.Builder builder) {
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

        for (final FlowRecord record : this.records) {
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
