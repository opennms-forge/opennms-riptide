package org.opennms.riptide;

import static org.junit.Assert.*;

import java.net.Inet4Address;
import java.util.Iterator;

import org.junit.Test;
import org.opennms.riptide.gen.Network;

public class NetworkTest {
    @Test
    public void networkIterator() {
        final Network network = new Network(10<<24, 30);
        final Iterator<Inet4Address> it = network.iterator();

        assertTrue(it.hasNext());
        assertEquals("10.0.0.1", it.next().getHostAddress());

        assertTrue(it.hasNext());
        assertEquals("10.0.0.2", it.next().getHostAddress());

        assertFalse(it.hasNext());
    }

    @Test
    public void networkBase() {
        final Network network = new Network("192.168.23.42", 16);

        assertEquals("192.168.0.0", network.getBase().getHostAddress());
        assertEquals(16, network.getMask());
    }

    @Test
    public void networkParser() {
        final Network network = Network.parse("1.2.3.4/24");

        assertEquals("1.2.3.0", network.getBase().getHostAddress());
        assertEquals(24, network.getMask());
    }
}
