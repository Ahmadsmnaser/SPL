package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MyFiles {
    private String baseDirectory;
    private ConcurrentHashMap<String, byte[]> files;
    private ConcurrentHashMap<String, Integer> size;
    private ConcurrentHashMap<String, Path> NametoPath;

    public MyFiles(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.files = new ConcurrentHashMap<>();
        this.size = new ConcurrentHashMap<>();
        this.NametoPath = new ConcurrentHashMap<>();
        try (Stream<Path> paths = Files.list(Paths.get(baseDirectory))) {
            paths.forEach(path -> {
                String fileName = path.getFileName().toString();
                try {
                    byte[] fileData = Files.readAllBytes(path);
                    files.put(fileName, fileData);
                    size.put(fileName, fileData.length);
                    NametoPath.put(fileName, path);
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            });
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public String nameslist() {
        String result = "";
        for (String name : files.keySet()) {
            result = result + name + "0";
        }
        return result;
    }

    public boolean fileExists(String fileName) {
        return files.containsKey(fileName);
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public void createFile(String fileName) throws IOException {
        try {
            Path filePath = Paths.get(baseDirectory, fileName);
            Files.createFile(filePath);
            System.out.println("File created successfully.");
            files.put(fileName, new byte[0]);
            size.put(fileName, 0);
            NametoPath.put(fileName, filePath);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName) throws IOException {
        if (!fileExists(fileName)) {
            System.out.println("File does not exist.");
        } else {
            Path filePath = Paths.get(baseDirectory, fileName);
            Files.delete(filePath);
            files.remove(fileName);
            size.remove(fileName);
            NametoPath.remove(fileName);
            System.out.println("File deleted successfully.");
        }
    }

    public void writeDataToFile(String fileName, byte[] data) throws IOException {
        if (!fileExists(fileName)) {
            System.out.println("File does not exist.");
            return;
        }

        byte[] fileData = files.get(fileName);
        byte[] newData = new byte[fileData.length + data.length];
        System.arraycopy(fileData, 0, newData, 0, fileData.length);
        System.arraycopy(data, 0, newData, fileData.length, data.length);

        files.put(fileName, newData);
        size.put(fileName, newData.length);

        Path filePath = NametoPath.get(fileName);
        Files.write(filePath, data, StandardOpenOption.APPEND);
        System.out.println("Data added to the file.");
    }

    public void writeDataToFile(String fileName, byte[] data, boolean append) throws IOException {
        if (!fileExists(fileName)) {
            System.out.println("File does not exist.");
            return;
        }

        if (append) {
            writeDataToFile(fileName, data);
        } else {
            files.put(fileName, data);
            size.put(fileName, data.length);

            Path filePath = NametoPath.get(fileName);
            Files.write(filePath, data);
            System.out.println("Data written to the file.");
        }
    }

    public byte[] readDataFromFile(String fileName) throws IOException {
        if (!fileExists(fileName)) {
            System.out.println("File does not exist.");
            return new byte[0];
        }

        return files.get(fileName);
    }
}
