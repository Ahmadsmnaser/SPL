package bgu.spl.net.impl.tftp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class TftpClient {
    private final Socket socket;
    private final BufferedReader input;
    private final BufferedWriter output;
    private final BlockingQueue<String> commandQueue;
    private final tftpEncoderDercoder encoderDercoder;
    private MyFiles myFiles;
    private final ConnectionHandler handler;
    int port;
    private Boolean command = false;
    private final Semaphore semaphore = new Semaphore(0);

    public TftpClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.port = port;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.commandQueue = new LinkedBlockingQueue<>();
        this.encoderDercoder = new tftpEncoderDercoder();
        this.myFiles = new MyFiles("/workspaces/Skeleton/client");
        this.handler = new ConnectionHandler(host, port, this, this.myFiles);
    }

    public void sendCommand(String command) {
        commandQueue.add(command);
    }

    public class KeyboardThread extends Thread {
        private TftpClient client;
        private ConnectionHandler handler;

        public KeyboardThread(TftpClient client, ConnectionHandler handler) {
            this.handler = handler;
            client = client;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine().trim();
                int spaceIndex = input.indexOf(" ");
                String firstPart = spaceIndex != -1 ? input.substring(0, spaceIndex) : input;
                String secondPart = spaceIndex != -1 ? input.substring(spaceIndex + 1) : "";
                handleCommand(firstPart, secondPart, this.handler);
                semaphore.release();
            }
        }
    }

    public class ListeningThread extends Thread {

        private BlockingQueue<String> commandQueue;
        private ConnectionHandler handler;
        private TftpClient client;

        public ListeningThread(BlockingQueue<String> commandQueue, ConnectionHandler handler, TftpClient client) {
            this.commandQueue = commandQueue;
            this.handler = handler;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                byte[] message;
                while (!isClosed()) {
                    semaphore.acquire();
                    while (handler.receive() != null) {
                    }
                    if (socket.isClosed()) {
                        break;
                    }

                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void start() {
        KeyboardThread keyboardThread = new KeyboardThread(this, handler);
        keyboardThread.start();
        ListeningThread listeningThread = new ListeningThread(commandQueue, handler, this);
        listeningThread.start();
    }

    public void stop() throws IOException {
        commandQueue.add("DISC");
        socket.close();
        input.close();
        output.close();
        Thread.currentThread().interrupt();
        Thread.currentThread().interrupt();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void handleCommand(String command, String args, ConnectionHandler handler) {
        if (command.equals("LOGRQ")) {
            handler.status.setLogingreq(true);
            String username = args;
            String message = "07" + username + "\0";
            byte[] bytes = message.getBytes();
            byte[] array = new byte[2 + username.length() + 1];
            array[0] = 0;
            array[1] = 7;
            for (int i = 0; i < username.length(); i++) {
                array[i + 2] = bytes[i + 2];
            }
            array[2 + username.length()] = "\0".getBytes()[0];
            try {
                this.command = true;
                handler.send(array);
                handler.status.setLogingreq(true);
                handler.status.setLastCommand("LOGRQ");
            } catch (IOException e) {
                System.out.println("Error Sending LOGRQ packet");
            }
        } else if (command.equals("DELRQ")) {
            // handler.send(DELRQ);
            String filename = args;
            String message = "08" + filename + "\0";
            byte[] bytes = message.getBytes();
            byte[] array = new byte[2 + filename.length() + 1];
            array[0] = 0;
            array[1] = 8;
            for (int i = 0; i < filename.length(); i++) {
                array[i + 2] = bytes[i + 2];
            }
            array[2 + filename.length()] = "\0".getBytes()[0];
            try {
                handler.send(array);
                handler.status.setLastCommand("DELRQ");
            } catch (IOException e) {
                System.out.println("Error Sending DELRQ packet");
            }
        } else if (command.equals("RRQ")) {
            String filename = args;
            if (myFiles.fileExists(filename)) {
                System.out.println("File already exists");
                return;
            } else {
                try {
                    myFiles.createFile(filename);
                    handler.status.setFileName(filename);
                    String message = "01" + filename + "\0";
                    byte[] bytes = message.getBytes();
                    byte[] array = new byte[2 + filename.length() + 1];
                    array[0] = 0;
                    array[1] = 1;
                    for (int i = 0; i < filename.length(); i++) {
                        array[i + 2] = bytes[i + 2];
                    }
                    array[2 + filename.length()] = "\0".getBytes()[0];
                    try {
                        handler.send(array);
                        handler.status.setRRQrequsted(true);
                        handler.status.setLastCommand("RRQ");
                    } catch (IOException e) {
                        System.out.println("Error Sending RRQ packet");
                    }
                } catch (IOException j) {
                    System.out.println("Error creating file");
                }
            }

        } else if (command.equals("WRQ")) {
            String filename = args;
            if (!myFiles.fileExists(filename)) {
                System.out.println("File does not exists");
                return;
            } else {
                String message = "02" + filename + "\0";
                byte[] bytes = message.getBytes();
                byte[] array = new byte[2 + filename.length() + 1];
                array[0] = 0;
                array[1] = 2;
                for (int i = 0; i < filename.length(); i++) {
                    array[i + 2] = bytes[i + 2];
                }
                array[2 + filename.length()] = "\0".getBytes()[0];
                try {
                    handler.send(array);
                    handler.status.setFileName(filename);
                    handler.status.setWRQrequsted(true);
                    handler.status.setLastCommand("WRQ");
                    handler.status.setWRQrequsted(true);
                } catch (IOException e) {
                    System.out.println("Error Sending WRQ packet");
                }
            }
        } else if (command.equals("DIRQ")) {
            String message = "06";
            byte[] bytes = message.getBytes();
            byte[] array = new byte[2];
            array[0] = 0;
            array[1] = 6;
            try {
                handler.send(array);
                handler.status.setLastCommand("DIRQ");
                handler.status.setDIRQrequsted(true);
            } catch (IOException e) {
                System.out.println("Error Sending DIRQ packet");
            }
        } else if (command.equals("DISC")) {
            // handler.send(DISC);
            if (handler.status.isLogedIn()) {
                handler.status.setShouldTerminate(true);
                String message = "10";
                byte[] bytes = message.getBytes();
                byte[] array = new byte[2];
                array[0] = 0;
                array[1] = 10;
                try {
                    handler.send(array);
                    handler.status.setLastCommand("DISC");
                } catch (IOException e) {
                    System.out.println("Error Sending DISC packet");
                }
                System.exit(0);// Close the program
            } else {
                System.out.println("You are not logged in");
                return;
            }
        }
    }

    public boolean isillegalCommand(String command, String args) {
        if (command.equals("LOGRQ") || command.equals("DELRQ") || command.equals("RRQ") || command.equals("WRQ")
                || command.equals("DIRQ") || command.equals("DISC")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 7777;
        TftpClient client = new TftpClient(host, port);
        try {
            client.start();
        } catch (Exception e) {
            System.out.println("Error starting client");
        }
    }
}
