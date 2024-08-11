package bgu.spl.net.impl.tftp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.Connections;

class holder {
    static ConcurrentHashMap<Integer, Boolean> ids_login = new ConcurrentHashMap<>();
}

public class TftpProtocol implements BidiMessagingProtocol<byte[]> {
    private int connectionId;
    private Connections<byte[]> connections;
    private boolean shouldTerminate = false;

    @Override
    public void start(int connectionId, Connections<byte[]> connections) {
        this.shouldTerminate = false;
        this.connectionId = connectionId;
        this.connections = connections;
        holder.ids_login.put(connectionId, true);
    }

    @Override
    public void process(byte[] message) {
        int opcode = TftpEncoderDecoder.getOpcode();
        if (opcode == 1) {// Download
            // RRQ
            System.out.println("RRQ");
            String Filename = TftpEncoderDecoder.extractFilename(message);
            ConnectionsImpl.getInstance().RRQ(Filename, connectionId);
        } else if (opcode == 2) {// Upload
            // WRQ
            System.out.println("WRQ");
            String Filename = TftpEncoderDecoder.extractFilename(message);
            ConnectionsImpl.getInstance().WRQ(Filename, connectionId);
        } else if (opcode == 3) {// Data
            // DATA
            System.out.println("DATA");
            int packetsize = TftpEncoderDecoder.getPacketsize();
            Integer blocknumber = TftpEncoderDecoder.getBlocknum();
            ConnectionsImpl.getInstance().DATA(connectionId, message, blocknumber, packetsize);
        } else if (opcode == 4) {// Print ACK
            // ACK
            System.out.println("ACK");
            Integer blocknumber = TftpEncoderDecoder.getBlocknum();
            ConnectionsImpl.getInstance().ACK(blocknumber, connectionId);
        } else if (opcode == 6) {// Files List
            // DIRQ
            System.out.println("DIRQ");
            ConnectionsImpl.getInstance().DIRQ(connectionId);
        } else if (opcode == 7) {// Login
            // LOGRQ
            System.out.println("LOGRQ");
            String username = TftpEncoderDecoder.extractFilename(message);
            ConnectionsImpl.getInstance().LOGRQ(username, connectionId);
        } else if (opcode == 8) {// Delete
            // DELRQ
            System.out.println("DELRQ");
            String Filename = TftpEncoderDecoder.extractFilename(message);
            ConnectionsImpl.getInstance().DELRQ(Filename, connectionId);
        } else if (opcode == 10) {
            System.out.println("DISC");
            String username = TftpEncoderDecoder.extractFilename(message);
            ConnectionsImpl.getInstance().DISC(username, connectionId);
            this.shouldTerminate = true;

        } else {
            ConnectionsImpl.getInstance().UnknownOpcode(connectionId);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
