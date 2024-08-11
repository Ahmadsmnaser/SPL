package bgu.spl.net.impl.tftp;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl.net.api.MessageEncoderDecoder;

public class TftpEncoderDecoder implements MessageEncoderDecoder<byte[]> {

    private byte[] bytes = new byte[1 << 10]; // start with 1k
    private static int len = 0;
    public static int errorcode = -1;
    public static short opcode = -1;
    public static int blocknum = -1;
    public static int packetsize = -1;
    public static int currsize = 0;

    protected static String extractFilename(byte[] data) {
        int filenameLength = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                filenameLength = i;
                break;
            }
            if (i == data.length - 1) {
                filenameLength = data.length;
            }
        }
        return new String(data, 0, filenameLength, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decodeNextByte(byte nextByte) {
        currsize++;
        if (len == 1 && opcode == -1) {
            pushByte(nextByte);
            opcode = TwobytesToShort(bytes);
            if (opcode != -1) {
                len = 0;
            }
            if (opcode == 6 || opcode == 10) {
                String result = "   ";
                return result.getBytes();
            }
            return null;
        }
        if (opcode == 9 && len == 0 && blocknum == -1) {
            if (nextByte == 0) {
                blocknum = 0;
                len = 0;
                return null;
            } else if (nextByte == 1) {
                blocknum = 1;
                len = 0;
                return null;
            }
        }
        if (opcode == 5) {
            if (len == 1 && errorcode == -1) {
                pushByte(nextByte);
                errorcode = TwobytesToShort(bytes);
                len = 0;
                return null;
            }
        }

        if (opcode == 4) {
            if (len == 1 && blocknum == -1) {
                pushByte(nextByte);
                blocknum = TwobytesToint(bytes);
                len = 0;
                String result = "";
                return result.getBytes();
            }
        }
        if (opcode == 3) {
            if (len == 1 && packetsize == -1) {
                pushByte(nextByte);
                packetsize = TwobytesToint(bytes);
                len = 0;
                return null;
            }
            if (len == 1 && packetsize != -1 && blocknum == -1) {
                pushByte(nextByte);
                blocknum = TwobytesToint(bytes);
                len = 0;
                return null;
            }
            if (packetsize != -1 && blocknum != -1 && packetsize != -1) {
                pushByte(nextByte);
                if (currsize == packetsize + 6) {
                    return thebytes();
                }
                return null;
            }
        }
        if ((nextByte == '\0' || nextByte == '\n') && (len != 0)) {
            String result = popString();// use UTF8 by default
            return result.getBytes();
        }
        pushByte(nextByte);
        return null;
    }

    public short TwosbytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public short TwobytesToShort(byte[] byteArr) {
        if (byteArr[0] == 0 && byteArr[1] == 0) {
            return 0;
        } else if (byteArr[0] == 0 && byteArr[1] == 1) {
            return 1;
        } else if (byteArr[0] == 0 && byteArr[1] == 2) {
            return 2;
        } else if (byteArr[0] == 0 && byteArr[1] == 3) {
            return 3;
        } else if (byteArr[0] == 0 && byteArr[1] == 4) {
            return 4;
        } else if (byteArr[0] == 0 && byteArr[1] == 5) {
            return 5;
        } else if (byteArr[0] == 0 && byteArr[1] == 6) {
            return 6;
        } else if (byteArr[0] == 0 && byteArr[1] == 7) {
            return 7;
        } else if (byteArr[0] == 0 && byteArr[1] == 8) {
            return 8;
        } else if (byteArr[0] == 0 && byteArr[1] == 9) {
            return 9;
        } else if (byteArr[0] == 0 && byteArr[1] == 10) {
            return 10;
        }
        byteArr[0] = 0;
        byteArr[1] = 0;
        return -1;
    }

    public int TwobytesToint(byte[] byteArr) {
        return ((byteArr[0] & 0xFF) << 8) | (byteArr[1] & 0xFF);

    }

    @Override
    public byte[] encode(byte[] message) {
        return (message);
    }

    public byte[] thebytes() {
        byte[] array = new byte[len];
        for (int i = 0; i < len; i++) {
            array[i] = bytes[i];
        }
        return array;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    public static short getOpcode() {
        return opcode;
    }

    public static int getErrorcode() {
        return errorcode;
    }

    public static int getBlocknum() {
        return blocknum;
    }

    public static int getPacketsize() {
        return packetsize;
    }

    public static void reset() {
        opcode = -1;
        errorcode = -1;
        blocknum = -1;
        packetsize = -1;
        currsize = 0;
        len = 0;
    }

}
