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

import java.util.Iterator;
import java.util.Random;

public class PortRange implements Iterable<Integer> {
    public final int min;
    public final int max;

    public PortRange(final int min, final int max) {
        this.min = min < max ? min : max;
        this.max = min < max ? max : min;
    }

    public int pick(final Random random) {
        return this.min + random.nextInt(this.max - this.min);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new PortRangeIterator(this.min, this.max);
    }

    private class PortRangeIterator implements Iterator<Integer> {

        public int cur;
        public int max;

        private PortRangeIterator(final int min, final int max) {
            this.cur = min;
            this.max = max;
        }

        @Override
        public boolean hasNext() {
            return this.cur <= this.max;
        }

        @Override
        public Integer next() {
            return this.cur++;
        }
    }
}
