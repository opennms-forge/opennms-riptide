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
import java.util.Iterator;
import java.util.Objects;

import com.google.common.base.Preconditions;

public class Simulation {

    interface Sender {
        void send(Flow flow);
    }

    // TODO fooker: Is this feasible?
    private final Instant bootTime = Instant.ofEpochSecond((long) ((System.currentTimeMillis() / 1000L) * Math.random()));

    private final TcpSession tcpSession;
    private final Duration flushInterval;

    public Simulation(final TcpSession tcpSession, final Duration flushInterval) {
        this.tcpSession = Objects.requireNonNull(tcpSession);
        this.flushInterval = Objects.requireNonNull(flushInterval);
    }

    public void simulate(final Sender sender) {
        final float sendPacketRate = this.tcpSession.sendPackets / (float) this.tcpSession.timespan.duration().toMillis();
        final float sendOctetsRate = this.tcpSession.sendSize / (float) this.tcpSession.timespan.duration().toMillis();
        final float recvPacketRate = this.tcpSession.recvPackets / (float) this.tcpSession.timespan.duration().toMillis();
        final float recvOctetsRate = this.tcpSession.recvSize / (float) this.tcpSession.timespan.duration().toMillis();

        final Sleeper sleeper = new Sleeper(this.tcpSession.timespan.ticker(this.flushInterval));
        for (final Positional.Positioned<Timespan> tick : Positional.from(sleeper)) {
            short sendFlags = 0x18;
            short recvFlags = 0x18;
            if (tick.first) {
                sendFlags |= 0xDA;
                recvFlags |= 0x5A;
            }
            if (tick.last) {
                sendFlags |= 0x19;
                recvFlags |= 0x99;
            }

            final FlowRecord.Builder sendRecord = FlowRecord.builder()
                    .withSrcAddr(this.tcpSession.client.address)
                    .withDstAddr(this.tcpSession.server.address)
                    .withNextHop(this.tcpSession.client.gateway)
                    .withPackets((long) (sendPacketRate * tick.element.duration().toMillis()))
                    .withOctets((long) (sendOctetsRate * tick.element.duration().toMillis()))
                    .withFirstUptime(Duration.between(this.bootTime, tick.element.s))
                    .withLastUptime(Duration.between(this.bootTime, tick.element.e))
                    .withSnmpInputIface(this.tcpSession.client.snmpInterface)
                    .withSnmpOutputIface(this.tcpSession.server.snmpInterface)
                    .withSrcPort(this.tcpSession.client.port)
                    .withDstPort(this.tcpSession.server.port)
                    .withTcpFlags(sendFlags)
                    .withProtocol((short) 0x06) // TCP
                    .withSrcAS(this.tcpSession.client.as)
                    .withDstAS(this.tcpSession.server.as)
                    .withTos((short) 0x00)
                    .withSrcMask(this.tcpSession.client.mask)
                    .withDstMask(this.tcpSession.server.mask);

            final FlowRecord.Builder recvRecord = FlowRecord.builder()
                    .withSrcAddr(this.tcpSession.server.address)
                    .withDstAddr(this.tcpSession.client.address)
                    .withNextHop(this.tcpSession.server.gateway)
                    .withPackets((long) (recvPacketRate * tick.element.duration().toMillis()))
                    .withOctets((long) (recvOctetsRate * tick.element.duration().toMillis()))
                    .withFirstUptime(Duration.between(this.bootTime, tick.element.s))
                    .withLastUptime(Duration.between(this.bootTime, tick.element.e))
                    .withSnmpInputIface(this.tcpSession.server.snmpInterface)
                    .withSnmpOutputIface(this.tcpSession.client.snmpInterface)
                    .withSrcPort(this.tcpSession.server.port)
                    .withDstPort(this.tcpSession.client.port)
                    .withTcpFlags(recvFlags)
                    .withProtocol((short) 0x06) // TCP
                    .withSrcAS(this.tcpSession.server.as)
                    .withDstAS(this.tcpSession.client.as)
                    .withTos((short) 0x00)
                    .withSrcMask(this.tcpSession.server.mask)
                    .withDstMask(this.tcpSession.client.mask);

            final Flow.Builder flow = Flow.builder()
                    .withUptime(Duration.between(this.bootTime, tick.element.e))
                    .withTime(tick.element.e)
                    .withEngineType((short) 0x20)
                    .withEngineId((short) 0x20)
                    .withSamplingMode(Flow.SamplingMode.Unassigned)
                    .withSamplingInterval(0)
                    .withRecord(sendRecord)
                    .withRecord(recvRecord);

            sender.send(flow.build());
        }
    }


    public static class Sleeper implements Iterable<Timespan> {

        private final Timespan.Ticker ticker;

        public Sleeper(final Timespan.Ticker ticker) {
            this.ticker = Preconditions.checkNotNull(ticker);
        }

        @Override
        public Iterator<Timespan> iterator() {
            return new Iterator<Timespan>() {
                public Iterator<Timespan> it = Sleeper.this.ticker.iterator();

                @Override
                public boolean hasNext() {
                    return this.it.hasNext();
                }

                @Override
                public Timespan next() {
                    final Timespan next = this.it.next();
                    final Duration duration = Duration.between(Instant.now(), next.e);

                    // zzzZZZZzzz
                    try {
                        Thread.sleep(duration.toMillis());
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return next;
                }
            };
        }
    }

    public static class Positional<T> implements Iterable<Positional.Positioned<T>> {
        public static <T> Iterable<Positioned<T>> from(final Iterable<T> nested) {
            return new Positional<>(nested);
        }

        public static class Positioned<T> {
            public final T element;

            public final boolean first;
            public final boolean last;

            public Positioned(final T element,
                              final boolean first,
                              final boolean last) {
                this.element = element;
                this.first = first;
                this.last = last;
            }
        }

        private final Iterable<T> nested;

        private Positional(final Iterable<T> nested) {
            this.nested = Objects.requireNonNull(nested);
        }

        @Override
        public Iterator<Positioned<T>> iterator() {
            return new Iterator<Positioned<T>>() {
                private final Iterator<T> it = Positional.this.nested.iterator();

                private boolean first = true;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Positioned<T> next() {
                    final boolean first = this.first;
                    this.first = false;

                    final T element = this.it.next();

                    return new Positioned<>(element, first, !this.it.hasNext());
                }
            };
        }
    }
}
