package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.TftpClient;

public class ConnectionHandler {
    private final String host;
    private final int port;
    private final Socket socket;
    private final tftpEncoderDercoder encdec;
    public ClientStatus status;
    private MyFiles myFiles;
    private TftpClient client;
    private int sendblocknum = 1;
    private int index = 0;

    public ConnectionHandler(String host, int port, TftpClient client, MyFiles files) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
        System.out.println("Connected to the server!");
        this.encdec = new tftpEncoderDercoder();
        this.status = new ClientStatus();
        this.client = client;
        this.myFiles = files;

    }

    public void send(byte[] bytes) throws IOException {
        OutputStream outputStream = this.socket.getOutputStream();
        byte[] encodedBytes = this.encdec.encode(bytes);
        outputStream.write(encodedBytes);
        outputStream.flush();
    }

    public byte[] receive() throws IOException {
        if (client.isClosed()) {
            return null;
        }
        InputStream inputStream = new BufferedInputStream(this.socket.getInputStream());
        int read;
        while ((read = inputStream.read()) >= 0) {
            byte[] nextMessage = this.encdec.decodeNextByte((byte) read);
            if (nextMessage == null) {
            }
            if (nextMessage != null) {
                short opcode = encdec.getOpcode();
                int errorcode = encdec.getErrorcode();
                int blocknum = encdec.getBlocknum();
                int packetsize = encdec.getPacketsize();
                encdec.reset();
                String Filedata = new String(nextMessage);
                if (opcode == 4) {// ACK
                    // System.out.println("ACK received");
                    ACK(nextMessage, blocknum);
                } else if (opcode == 3) {// DATA
                    // System.out.println("DATA received");
                    DATA(nextMessage, blocknum, packetsize);
                } else if (opcode == 5) {// ERROR
                    // System.out.println("ERROR received");
                    ERROR(nextMessage, Filedata, errorcode);
                } else if (opcode == 9) {// BCAST
                    // System.out.println("BCAST received");
                    BCAST(nextMessage, blocknum);
                } else {
                    // System.out.println("Invalid opcode");
                }
                return nextMessage;
            }
        }
        return null;
    }

    public void disconnect() throws IOException {
        try {
            this.socket.close();
            System.out.println("Disconnected from the server!");
        } catch (IOException e) {
            System.out.println("Error while disconnecting");
        }
    }

    public void ACK(byte[] msg, int blocknum) {
        if (status.getLastCommand().equals("DISC")) {
            if (blocknum == 0) {
                status.setShouldTerminate(true);
                System.out.println("ACK 0 received, disconnecting...");
                try {
                    client.stop();
                    disconnect();
                } catch (IOException e) {
                    System.out.println("Error while disconnecting");
                }
            }

        } else if (status.getLastCommand().equals("WRQ")) {
            if (blocknum == 0) {
                System.out.println("ACK 0 received, WRQ");
                WRQhandler();
            } else {
                System.out.println("ACK " + blocknum + " received, WRQ");
            }
        } else if (status.getLastCommand().equals("LOGRQ")) {
            if (blocknum == 0) {
                status.setLogedIn(true);
                status.setLogingreq(false);
                System.out.println("ACK 0 received, logged in successfully.");
            }
        } else if (status.getLastCommand().equals("DELRQ")) {
            if (blocknum == 0) {
                System.out.println("ACK 0 received, file deleted successfully.");
            }
        }
    }

    public void DATA(byte[] msg, int blocknum, int packetsize) {
        if (status.getLastCommand().equals("RRQ")) {
            RRQhandler(msg, blocknum, packetsize);
        }
        if (status.getLastCommand().equals("DIRQ")) {
            DIRQhandler(msg, blocknum, packetsize);

        }
    }

    public void ERROR(byte[] msg, String errormsg, int errorcode) {
        System.out.println("Error : " + errorcode + " : " + new String(msg));
    }

    public void BCAST(byte[] msg, int blocknum) {
        String fileName = encdec.extractFilename(msg);
        if (blocknum == 0) {
            System.out.println("BCAST received, deleted ,file name: " + fileName);
        } else if (blocknum == 1) {
            System.out.println("BCAST received, added ,file name: " + fileName);
        }
    }

    public void WRQhandler() {
        while (status.isWRQrequsted() || !status.isWRQfinished() || status.isWRQstarted()) {
            if (status.isWRQrequsted() || !status.isWRQstarted() || !status.isWRQfinished()) {
                try {
                    byte[] data = myFiles.readDataFromFile(status.getFileName());
                    if (data.length - index < 512) {
                        status.setWRQfinished(true);
                        status.setWRQstarted(false);
                        status.setWRQrequsted(false);
                        byte[] tosend = new byte[data.length - index];
                        for (int i = 0; i < data.length - index; i++) {
                            tosend[i] = data[index + i];
                        }
                        Integer size = tosend.length;
                        int size1 = size;
                        byte[] array = new byte[2 + 2 + 2 + size];
                        array[0] = 0;
                        array[1] = 3;
                        array[2] = (byte) (size1 >> 8);
                        array[3] = (byte) size1;
                        array[4] = (byte) (sendblocknum >> 8);
                        array[5] = (byte) sendblocknum;
                        for (int i = 0; i < size; i++) {
                            array[i + 6] = tosend[i];
                        }
                        try {
                            send(array);
                            sendblocknum = 1;
                            index = 0;
                        } catch (IOException e) {
                            System.out.println("Error while sending DATA");
                        }
                        return;
                    } else {
                        status.setWRQstarted(true);
                        status.setWRQrequsted(false);
                        status.setWRQfinished(false);
                        byte[] tosend = new byte[512];
                        for (int i = 0; i < 512; i++) {
                            tosend[i] = data[index + i];
                        }
                        Integer size = tosend.length;
                        int size1 = size;
                        byte[] array = new byte[2 + 2 + 2 + 512];
                        array[0] = 0;
                        array[1] = 3;
                        array[2] = (byte) (size1 >> 8);
                        array[3] = (byte) size1;
                        array[4] = (byte) (sendblocknum >> 8);
                        array[5] = (byte) sendblocknum;
                        for (int i = 0; i < 512; i++) {
                            array[i + 6] = tosend[i];
                        }
                        try {
                            send(array);
                            index += 512;
                            sendblocknum++;
                        } catch (IOException e) {
                            System.out.println("Error while sending DATA");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error while reading the file");
                }
            }
        }
    }

    public void DIRQhandler(byte[] msg, int blocknum, int packetsize) {
        if (packetsize == 512) {
            if (blocknum == 1 && status.isDIRQrequsted()) {
                status.setDIRQrequsted(false);
                status.setDIRQstarted(true);
                // Should start the download
                try {
                    byte[] data = status.getData();
                    byte[] newData = new byte[data.length + 512];
                    for (int i = 0; i < data.length; i++) {
                        newData[i] = data[i];
                    }
                    for (int i = 0; i < 512; i++) {
                        newData[i + data.length] = msg[i];
                    }
                    status.setData(newData);
                    byte[] array = new byte[4];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = 0;
                    array[3] = 1;
                    send(array);
                } catch (IOException e) {
                    System.out.println("Error while sending ACK");
                }
            } else {
                if (blocknum != 1 && status.isDIRQstarted()) {
                    // Should continue the download
                    try {
                        byte[] data = status.getData();
                        byte[] newData = new byte[data.length + 512];
                        for (int i = 0; i < data.length; i++) {
                            newData[i] = data[i];
                        }
                        for (int i = 0; i < 512; i++) {
                            newData[i + data.length] = msg[i];
                        }
                        status.setData(newData);
                        byte[] array = new byte[4];
                        array[0] = 0;
                        array[1] = 4;
                        array[2] = (byte) (blocknum >> 8);
                        array[3] = (byte) blocknum;
                        send(array);
                    } catch (IOException e) {
                        System.out.println("Error while sending ACK");
                    }
                }
            }
        } else if (packetsize < 512 && (status.isDIRQstarted() || status.isDIRQrequsted())) {
            status.setDIRQrequsted(false);
            status.setDIRQstarted(false);
            status.setDIRQfinished(true);
            Integer block = blocknum;
            // Should start the download
            try {
                byte[] data = status.getData();
                byte[] newData = new byte[data.length + packetsize];
                for (int i = 0; i < data.length; i++) {
                    newData[i] = data[i];
                }
                for (int i = 0; i < packetsize; i++) {
                    newData[i + data.length] = msg[i];
                }
                status.setData(new byte[512]);
                String allfiles = new String(newData, StandardCharsets.UTF_8);
                String[] files = allfiles.split("0");
                for (String file : files) {
                    System.out.print(file + "\n");
                }
                byte[] array = new byte[4];
                array[0] = 0;
                array[1] = 4;
                array[2] = (byte) (blocknum >> 8);
                array[3] = (byte) blocknum;
                send(array);
            } catch (IOException e) {
                System.out.println("Error while sending ACK");
            }
        }

    }

    public void RRQhandler(byte[] msg, int blocknum, int packetsize) {
        String Filename = status.getFileName();
        if (packetsize == 512) {
            if (blocknum == 1 && status.isRRQrequsted()) {
                status.setRRQrequsted(false);
                status.setRRQstarted(true);
                // Should start the download
                try {
                    myFiles.writeDataToFile(Filename, msg);
                    byte[] array = new byte[4];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = 0;
                    array[3] = 1;
                    send(array);
                } catch (IOException e) {
                    System.out.println("Error while sending ACK");
                }
            } else {
                if (blocknum != 1 && status.isRRQstarted()) {
                    Integer block = blocknum;
                    // Should continue the download
                    try {
                        myFiles.writeDataToFile(Filename, msg);
                        byte[] array = new byte[4];
                        array[0] = 0;
                        array[1] = 4;
                        array[2] = (byte) (blocknum >> 8);
                        array[3] = (byte) blocknum;
                        send(array);
                    } catch (IOException e) {
                        System.out.println("Error while sending ACK");
                    }
                }
            }
        } else if (packetsize < 512) {
            if ((status.isRRQstarted() || status.isRRQrequsted())) {
                status.setRRQrequsted(false);
                status.setRRQstarted(false);
                status.setRRQfinished(true);
                Integer block = blocknum;
                // Should start the download
                try {
                    myFiles.writeDataToFile(Filename, msg);
                    byte[] array = new byte[4];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = (byte) (blocknum >> 8);
                    array[3] = (byte) blocknum;
                    send(array);
                } catch (IOException e) {
                    System.out.println("Error while sending ACK");
                }
            }
            return;
        }
    }
}
