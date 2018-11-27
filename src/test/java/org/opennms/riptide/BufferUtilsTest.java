package org.opennms.riptide;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Test;

public class BufferUtilsTest {
    @Test
    public void uint8() {
        final ByteBuffer b = ByteBuffer.allocate(4);
        BufferUtils.uint8(b, 0x00);
        BufferUtils.uint8(b, 0x04);
        BufferUtils.uint8(b, 0xD2);
        BufferUtils.uint8(b, 0xFF);

        assertArrayEquals(new byte[] {
                (byte) 0x00,
                (byte) 0x04,
                (byte) 0xD2,
                (byte) 0xFF,
        }, b.array());
    }

    @Test
    public void uint16() {
        final ByteBuffer b = ByteBuffer.allocate(8);
        BufferUtils.uint16(b, 0x0000);
        BufferUtils.uint16(b, 0x04D2);
        BufferUtils.uint16(b, 0x127A);
        BufferUtils.uint16(b, 0xFFFF);

        assertArrayEquals(new byte[] {
                (byte) 0x00, (byte) 0x00,
                (byte) 0x04, (byte) 0xD2,
                (byte) 0x12, (byte) 0x7A,
                (byte) 0xFF, (byte) 0xFF,
        }, b.array());
    }

    @Test
    public void uint32() {
        final ByteBuffer b = ByteBuffer.allocate(12);
        BufferUtils.uint32(b, 0x00000000);
        BufferUtils.uint32(b, 0x04D2127A);
        BufferUtils.uint32(b, 0xFFFFFFFF);

        assertArrayEquals(new byte[] {
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x04, (byte) 0xD2, (byte) 0x12, (byte) 0x7A,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        }, b.array());
    }
}
