package org.daylight.pathweaver.common.converters;

public class BitConverters {

    private static final byte[] hexmap = "0123456789abcdef".getBytes();

    public static String int16bit2hex(int i16) {
        String out;
        byte[] nibbles = new byte[4];

        if (i16 < 0x0000 || i16 > 0xffff) {
            return null;
        }
        nibbles[0] = BitConverters.int2Nibble((i16 & 0xf000) >> 12);
        nibbles[1] = BitConverters.int2Nibble((i16 & 0x0f00) >> 8);
        nibbles[2] = BitConverters.int2Nibble((i16 & 0x00f0) >> 4);
        nibbles[3] = BitConverters.int2Nibble((i16 & 0x000f) >> 0);
        out = new String(nibbles);
        return out;
    }

    public static int hex16bit2int(String in) {
        int i;
        int last;
        int base;
        int v;
        byte[] hex;
        int out;

        out = 0;
        base = 1;
        if (in == null) {
            return -1;
        }
        hex = in.getBytes();
        if (hex.length > 4) {
            return -1;
        }
        last = hex.length - 1;
        for (i = last; i >= 0; i--) {
            v = BitConverters.nibble2Int(hex[i]);
            if (v == -1) {
                return -1;
            }
            out += v * base;
            base *= 16;
        }

        return out;
    }



    public static int nibble2Int(byte nibble) {
        final char[] hexBytes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int out = -1;

        for (final int i : hexBytes) {
                if (hexBytes[i] == nibble) {
                    out = Character.digit(nibble,16);
                    break;
                }
        }

        if (out < 0) {
            out = -1;

        }
        return out;
    }

    public static byte int2Nibble(int in) {
        return (in < 0 || in > 15) ? (byte) -1 : hexmap[in];
    }

    public static int ubyte2int(byte in) {
        return (in >= 0) ? (int) in : (int) in + 256;
    }

    public static byte int2ubyte(int in) {
        in &= 0xff;
        return (in < 128) ? (byte) in : (byte) (in - 256);
    }

    public static String byte2hex(byte ubyte) {
        byte[] nibbleBytes;
        String out;

        int lo;
        int hi;

        lo = ubyte & 0x0f;
        hi = (ubyte & 0xf0) >> 4;
        nibbleBytes = new byte[]{int2Nibble(hi), int2Nibble(lo)};
        out = new String(nibbleBytes);
        return out;
    }

    // Only positive ints though. BigEndian
    public static byte[] uint2bytes(int in) {
        byte out[] = new byte[4];
        if (in < 0) {
            in = 0 - in;// Don't put up with sign bits;
        }

        out[3] = int2ubyte(in & 0xff);
        in >>= 8;
        out[2] = int2ubyte(in & 0xff);
        in >>= 8;
        out[1] = int2ubyte(in & 0xff);
        in >>= 8;
        out[0] = int2ubyte(in & 0xff);
        return out;

    }

    public static String bytes2hex(byte[] bytes) {
        StringBuffer sb;
        int i;

        sb = new StringBuffer();
        for (i = 0; i < bytes.length; i++) {
            sb.append(byte2hex(bytes[i]));
        }
        return sb.toString();
    }
}
