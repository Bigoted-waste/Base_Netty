package com.cola.NIO.Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileCopy {
    public static void main(String[] args) throws IOException {
        String source = "/home/cola/Downloads/1";
        String target = "/home/cola/Downloads/1aaaa";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 是目录
                if (Files.isDirectory(path)) {

                    //   /home/cola/Downloads/1
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是目录
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
