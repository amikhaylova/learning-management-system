package com.mikhaylova.lms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

@Service
public class FileUtilService {
    @Value("${file.storage.path}")
    private String path;

    public void saveFile(InputStream is, String filename) throws IllegalStateException {
        try (OutputStream os = Files.newOutputStream(Path.of(path, filename), CREATE, WRITE, TRUNCATE_EXISTING)) {
            is.transferTo(os);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public byte[] readFile(String filename) throws IllegalStateException {
        try {
            return Files.readAllBytes(Path.of(path, filename));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
