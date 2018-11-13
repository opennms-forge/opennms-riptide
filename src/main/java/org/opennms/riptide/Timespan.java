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

import com.google.common.base.Preconditions;

public class Timespan {
    public final Instant s;
    public final Instant e;

    public Timespan(final Instant s, final Instant e) {
        Preconditions.checkNotNull(s);
        Preconditions.checkNotNull(e);
        Preconditions.checkArgument(s.isBefore(e));

        this.s = s;
        this.e = e;
    }

    public Timespan(final Instant s, final Duration duration) {
        this(s, s.plus(duration));
    }

    public Duration duration() {
        return Duration.between(this.s, this.e);
    }

    public static class Ticker implements Iterable<Timespan> {

//        public static class Tick {
//            public final Instant instant;
//            public final Duration duration;
//
//            public Tick(final Instant instant,
//                        final Duration duration) {
//                this.instant = instant;
//                this.duration = duration;
//            }
//        }

        private final Timespan timespan;
        private final Duration interval;

        public Ticker(final Timespan timespan,
                      final Duration interval) {
            this.timespan = Preconditions.checkNotNull(timespan);
            this.interval = Preconditions.checkNotNull(interval);
        }

        @Override
        public Iterator<Timespan> iterator() {
            return new Iterator<Timespan>() {
                public Instant current = Ticker.this.timespan.s;

                @Override
                public boolean hasNext() {
                    return this.current.isBefore(Ticker.this.timespan.e);
                }

                @Override
                public Timespan next() {
                    final Instant s = this.current;
                    final Instant e = min(this.current.plus(Ticker.this.interval),
                            Ticker.this.timespan.e);

                    this.current = e;

                    return new Timespan(s, e);
                }
            };
        }

        private static Instant min(final Instant i1, final Instant i2) {
            if (i1.isBefore(i2)) {
                return i1;
            } else {
                return i2;
            }
        }
    }

    public Ticker ticker(final Duration interval) {
        return new Ticker(this, interval);
    }

}
