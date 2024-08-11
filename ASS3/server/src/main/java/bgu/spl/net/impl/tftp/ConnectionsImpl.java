package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Connections;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, BlockingConnectionHandler<T>> connections;
    private ConcurrentHashMap<Integer, Boolean> ids_login;
    private ConcurrentHashMap<String, Boolean> Username;
    private ConcurrentHashMap<Integer, String> User;
    private MyFiles myFiles;
    private Integer id;
    private static ConnectionsImpl connection = null;
    private String Filetoupload;
    private String FiletoBCAST;

    private String baseDirectory;

    public ConnectionsImpl() {
        this.connections = new ConcurrentHashMap<>();
        this.ids_login = new ConcurrentHashMap<>();
        this.Username = new ConcurrentHashMap<>();
        this.id = 0;
        // Files = new FileManager();
        this.User = new ConcurrentHashMap<>();
        this.Filetoupload = "";
        this.FiletoBCAST = "";
        this.baseDirectory = "/workspaces/Skeleton/server/Flies";
        this.myFiles = new MyFiles(baseDirectory);
    }

    public static ConnectionsImpl getInstance() {
        if (connection == null)
            connection = new ConnectionsImpl();
        return connection;
    }

    @Override
    public void connect(int connectionId, BlockingConnectionHandler<T> handler) {
        connections.put(connectionId, handler);
        ids_login.put(connectionId, false);
    }

    public String namelist() {
        return myFiles.nameslist();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if (connections.containsKey(connectionId)) {
            connections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void disconnect(int connectionId) {
        String username = (String) this.User.get(connectionId);
        Username.remove(username);
        User.remove(connectionId);
        ids_login.remove(username);
        try {
            connections.get(connectionId).close();
            connections.remove(connectionId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getUserName(int id) {
        return User.get(id);
    }

    public int getID() {
        int toreturn = id;
        id++;
        return toreturn;
    }

    public void RRQ(String Filename, int id) {
        if (!ids_login.get(id)) {
            String StringMsg = "0500User not logged in\0";
            String errormsg = "User not logged in";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 6;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            if (!myFiles.fileExists(Filename)) {
                String StringMsg = "0501File not found\0";
                String errormsg = "File not found";
                byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                byte[] array = new byte[2 + 2 + errormsg.length() + 1];
                array[0] = 0;
                array[1] = 5;
                array[2] = 0;
                array[3] = 1;
                for (int i = 0; i < errormsg.length(); i++) {
                    array[i + 4] = msg[i + 4];
                }
                array[4 + errormsg.length()] = "\0".getBytes()[0];
                send(id, (T) array);
            } else {
                try {
                    byte[] thisFile = myFiles.readDataFromFile(Filename);
                    int currentposition = 0;
                    int size = thisFile.length;
                    if (size <= 511) {
                        byte[] array = new byte[2 + 2 + 2 + size + 1];
                        array[0] = 0;
                        array[1] = 3;
                        array[2] = (byte) (size >> 8);
                        array[3] = (byte) size;
                        array[4] = 0;
                        array[5] = 1;
                        for (int i = 0; i < size; i++) {
                            array[i + 6] = thisFile[i];
                        }
                        array[6 + size] = "\0".getBytes()[0];
                        send(id, (T) array);
                        return;
                    } else {
                        Integer blocknumber = 1;
                        while (size - currentposition > 511) {
                            byte[] array = new byte[2 + 2 + 2 + 512 + 1];
                            int block = blocknumber;
                            array[0] = 0;
                            array[1] = 3;
                            array[2] = (byte) (512 >> 8);
                            array[3] = (byte) 512;
                            array[4] = (byte) (block >> 8);
                            array[5] = (byte) (block);
                            for (int i = 0; i < 512; i++) {
                                array[i + 6] = thisFile[i + currentposition];
                            }
                            array[6 + 512] = "\0".getBytes()[0];
                            send(id, (T) (array));
                            currentposition += 512;
                            blocknumber++;
                        }
                        if (size - currentposition <= 511) {
                            byte[] array = new byte[2 + 2 + 2 + (size - currentposition) + 1];
                            int block = blocknumber;
                            array[0] = 0;
                            array[1] = 3;
                            array[2] = (byte) ((size - currentposition) >> 8);
                            array[3] = (byte) (size - currentposition);
                            array[4] = (byte) (block >> 8);
                            array[5] = (byte) (block);
                            for (int i = 0; i < size - currentposition; i++) {
                                array[i + 6] = thisFile[i + currentposition];
                            }

                            array[6 + size - currentposition] = "\0".getBytes()[0];
                            send(id, (T) (array));
                            currentposition = size;
                            blocknumber++;
                            return;
                        }
                    }
                    System.out.println("PRQ <" + Filename + "> complete");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void WRQ(String Filename, int id) {
        if (!ids_login.get(id)) {
            String StringMsg = "0500User not logged in\0";
            String errormsg = "User not logged in";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 6;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            try {
                if (myFiles.fileExists(Filename)) {
                    String StringMsg = "0505File already exists\0";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    String errormsg = "File already exists";
                    byte[] array = new byte[2 + 2 + errormsg.length() + 1];
                    array[0] = 0;
                    array[1] = 5;
                    array[2] = 0;
                    array[3] = 5;
                    for (int i = 0; i < errormsg.length(); i++) {
                        array[i + 4] = msg[i + 4];
                    }
                    array[4 + errormsg.length()] = "\0".getBytes()[0];
                    send(id, (T) array);
                } else {
                    byte[] File = new byte[512];
                    myFiles.createFile(Filename);
                    Filetoupload = Filename;
                    String StringMsg = "0400";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[4];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = 0;
                    array[3] = 0;
                    send(id, (T) array);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void DATA(int id, byte[] message, Integer Blocknum, int Packetsize) {
        if (!ids_login.get(id)) {
            String StringMsg = "0500User not logged in\0";
            String errormsg = "User not logged in";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 6;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            try {
                if (!Filetoupload.equals("")) {
                    byte[] FileData = myFiles.readDataFromFile(Filetoupload);
                    myFiles.writeDataToFile(Filetoupload, message);

                    int block = Blocknum;
                    String StringMsg = "04" + Blocknum.toString() + "\0";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[2 + 2];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = (byte) (block >> 8);
                    array[3] = (byte) (block);
                    send(id, (T) array);
                    if (Packetsize < 512) {
                        FiletoBCAST = Filetoupload;
                        Filetoupload = "";
                        BCAST(FiletoBCAST);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ACK(int blocknumber, int id) {
        System.out.println("ACK <" + blocknumber + "> complete");
    }

    public void ERROR(int errorcode, String errorType, int id) {
    }

    public void DIRQ(int id) {
        if (!ids_login.get(id)) {
            String StringMsg = "0500User not logged in\0";
            String errormsg = "User not logged in";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 6;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            if (myFiles.isEmpty()) {
                String StringMsg = "0500The Folder is Empty\0";
                String errormsg = "The Folder is Empty";
                byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                byte[] array = new byte[2 + 2 + errormsg.length() + 1];
                array[0] = 0;
                array[1] = 5;
                array[2] = 0;
                array[3] = 0;
                for (int i = 0; i < errormsg.length(); i++) {
                    array[i + 4] = msg[i + 4];
                }
                array[4 + errormsg.length()] = "\0".getBytes()[0];
                send(id, (T) array);
            } else {
                String AllFiles = myFiles.nameslist();
                byte[] AllFilesBytes = AllFiles.getBytes(StandardCharsets.UTF_8);
                int currentposition = 0;
                int size = AllFilesBytes.length;
                if (size <= 511) {
                    byte[] array = new byte[2 + 2 + 2 + size + 1];
                    array[0] = 0;
                    array[1] = 3;
                    array[2] = (byte) (size >> 8);
                    array[3] = (byte) size;
                    array[4] = 0;
                    array[5] = 1;
                    for (int i = 0; i < size; i++) {
                        array[i + 6] = AllFilesBytes[i];
                    }
                    array[6 + size] = "\0".getBytes()[0];
                    send(id, (T) (array));
                } else {
                    Integer blocknumber = 1;
                    while (size - currentposition > 511) {
                        byte[] array = new byte[2 + 2 + 2 + 512 + 1];
                        int block = blocknumber;
                        array[0] = 0;
                        array[1] = 3;
                        array[2] = (byte) (512 >> 8);
                        array[3] = (byte) 512;
                        array[4] = (byte) (block >> 8);
                        array[5] = (byte) (block);
                        for (int i = 0; i < 512; i++) {
                            array[i + 6] = AllFilesBytes[i + currentposition];
                        }
                        array[6 + 512] = "\0".getBytes()[0];
                        send(id, (T) array);
                        currentposition += 512;
                        blocknumber++;
                    }
                    if (size - currentposition <= 511) {
                        byte[] array = new byte[2 + 2 + 2 + (size - currentposition) + 1];
                        int block = blocknumber;
                        array[0] = 0;
                        array[1] = 3;
                        array[2] = (byte) ((size - currentposition) >> 8);
                        array[3] = (byte) (size - currentposition);
                        array[4] = (byte) (block >> 8);
                        array[5] = (byte) (block);
                        for (int i = 0; i < size - currentposition; i++) {
                            array[i + 6] = AllFilesBytes[i + currentposition];
                        }
                        array[6 + size - currentposition] = "\0".getBytes()[0];
                        send(id, (T) (array));
                    }
                }
            }
        }
    }

    public void LOGRQ(String username, int id) {
        if (ids_login.get(id) || Username.containsKey(username)) {
            String errormsg = "Login Username already connected";
            String StringMsg = "0507" + errormsg + "0";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 7;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            User.put(id, username);
            Username.put(username, true);
            ids_login.put(id, true);
            String StringMsg = "0400";
            byte[] array = new byte[4];
            array[0] = 0;
            array[1] = 4;
            array[2] = 0;
            array[3] = 0;
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            send(id, (T) array);
        }
    }

    public void DELRQ(String Filename, int id) {
        if (!ids_login.get(id)) {
            String StringMsg = "0500User not logged in\0";
            String errormsg = "User not logged in";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 6;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        } else {
            try {
                if (!myFiles.fileExists(Filename)) {
                    String StringMsg = "0501File not found\0";
                    String errormsg = "File not found";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[2 + 2 + errormsg.length() + 1];
                    array[0] = 0;
                    array[1] = 5;
                    array[2] = 0;
                    array[3] = 1;
                    for (int i = 0; i < errormsg.length(); i++) {
                        array[i + 4] = msg[i + 4];
                    }
                    array[4 + errormsg.length()] = "\0".getBytes()[0];
                    send(id, (T) array);
                } else {
                    myFiles.deleteFile(Filename);
                    String StringMsg = "0400";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[2 + 2];
                    array[0] = 0;
                    array[1] = 4;
                    array[2] = 0;
                    array[3] = 0;
                    send(id, (T) array);
                    FiletoBCAST = Filename;
                    BCAST(FiletoBCAST);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void BCAST(String Filename) {
        if (myFiles.fileExists(Filename)) {
            for (Integer k : ids_login.keySet()) {
                if (ids_login.get(k)) {
                    String StringMsg = "091" + Filename + "\0";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[2 + 1 + Filename.length() + 1];
                    array[0] = 0;
                    array[1] = 9;
                    array[2] = 1;
                    for (int i = 0; i < Filename.length(); i++) {
                        array[i + 3] = msg[i + 3];
                    }
                    array[3 + Filename.length()] = "\0".getBytes()[0];
                    send(k, (T) (array));
                }
            }
        } else if (!myFiles.fileExists(Filename)) {
            for (Integer k : ids_login.keySet()) {
                if (ids_login.get(k)) {
                    String StringMsg = "090" + Filename + "\0";
                    byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
                    byte[] array = new byte[2 + 1 + Filename.length() + 1];
                    array[0] = 0;
                    array[1] = 9;
                    array[2] = 0;
                    for (int i = 0; i < Filename.length(); i++) {
                        array[i + 3] = msg[i + 3];
                    }
                    array[3 + Filename.length()] = "\0".getBytes()[0];
                    send(k, (T) (array));
                }
            }
        }
    }

    public void DISC(String username, int id) {
        if (ids_login.containsKey(id) && ids_login.get(id)) {
            byte[] array = new byte[2 + 2];
            array[0] = 0;
            array[1] = 4;
            array[2] = 0;
            array[3] = 0;
            send(id, (T) array);
            disconnect(id);
        } else {
            String StringMsg = "0500User not found\0";
            byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
            String errormsg = "User not found";
            byte[] array = new byte[2 + 2 + errormsg.length() + 1];
            array[0] = 0;
            array[1] = 5;
            array[2] = 0;
            array[3] = 0;
            for (int i = 0; i < errormsg.length(); i++) {
                array[i + 4] = msg[i + 4];
            }
            array[4 + errormsg.length()] = "\0".getBytes()[0];
            send(id, (T) array);
        }
    }

    public void UnknownOpcode(int id) {
        String StringMsg = "0500Unknown Opcode\0";
        byte[] msg = StringMsg.getBytes(StandardCharsets.UTF_8);
        String errormsg = "Unknown Opcode";
        byte[] array = new byte[2 + 2 + errormsg.length() + 1];
        array[0] = 0;
        array[1] = 5;
        array[2] = 0;
        array[3] = 4;
        for (int i = 0; i < errormsg.length(); i++) {
            array[i + 4] = msg[i + 4];
        }
        array[4 + errormsg.length()] = "\0".getBytes()[0];
        send(id, (T) array);
    }
}
