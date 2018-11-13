package org.opennms.riptide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Test;

public class TimespanTest {
    @Test
    public void timespan() {
        final Timespan timespan = new Timespan(Instant.ofEpochSecond(0), Instant.ofEpochSecond(100));
        assertEquals(0, timespan.s.getEpochSecond());
        assertEquals(100, timespan.e.getEpochSecond());
        assertEquals(100, timespan.duration().getSeconds());
    }

    @Test
    public void timespanTickerShort() {
        final Timespan timespan = new Timespan(Instant.ofEpochSecond(0), Instant.ofEpochSecond(30));
        final Timespan.Ticker ticker = new Timespan.Ticker(timespan, Duration.ofSeconds(30));

        final Iterator<Timespan> it = ticker.iterator();

        assertTrue(it.hasNext());
        assertTrue(it.hasNext());

        final Timespan tick = it.next();
        assertEquals(0, tick.s.getEpochSecond());
        assertEquals(30, tick.e.getEpochSecond());

        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }

    @Test
    public void timespanTickerMedium() {
        final Timespan timespan = new Timespan(Instant.ofEpochSecond(0), Instant.ofEpochSecond(45));
        final Timespan.Ticker ticker = new Timespan.Ticker(timespan, Duration.ofSeconds(30));

        final Iterator<Timespan> it = ticker.iterator();

        assertTrue(it.hasNext());
        assertTrue(it.hasNext());

        {
            final Timespan tick = it.next();
            assertEquals(0, tick.s.getEpochSecond());
            assertEquals(30, tick.e.getEpochSecond());

            assertTrue(it.hasNext());
            assertTrue(it.hasNext());
        }

        {
            final Timespan tick = it.next();
            assertEquals(30, tick.s.getEpochSecond());
            assertEquals(45, tick.e.getEpochSecond());

            assertFalse(it.hasNext());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void timespanTickerLong() {
        final Timespan timespan = new Timespan(Instant.ofEpochSecond(0), Instant.ofEpochSecond(110));
        final Timespan.Ticker ticker = new Timespan.Ticker(timespan, Duration.ofSeconds(30));

        final Iterator<Timespan> it = ticker.iterator();

        assertTrue(it.hasNext());

        {
            Timespan tick = it.next();
            assertEquals(0, tick.s.getEpochSecond());
            assertEquals(30, tick.e.getEpochSecond());
            assertTrue(it.hasNext());
        }

        {
            Timespan tick = it.next();
            assertEquals(30, tick.s.getEpochSecond());
            assertEquals(60, tick.e.getEpochSecond());
            assertTrue(it.hasNext());
        }

        {
            Timespan tick = it.next();
            assertEquals(60, tick.s.getEpochSecond());
            assertEquals(90, tick.e.getEpochSecond());
            assertTrue(it.hasNext());
        }

        {
            Timespan tick = it.next();
            assertEquals(90, tick.s.getEpochSecond());
            assertEquals(110, tick.e.getEpochSecond());
            assertFalse(it.hasNext());
        }
    }
}
