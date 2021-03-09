package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.AisClientException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static final String USAGE_TEXT_FILE_PATH = "/cli/usage.txt";

    public static String readUsageText() {
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(USAGE_TEXT_FILE_PATH);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writeToOutputStream(inputStream, outputStream);
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AisClientException("Failed to read the usage text.");
        }
    }

    public static void writeClasspathFile(String inputFile, String outputFile) {
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            writeToOutputStream(inputStream, outputStream);
        } catch (IOException e) {
            throw new AisClientException(String.format("Failed to create the file: [%s].", outputFile));
        }
    }

    private static <S extends OutputStream> void writeToOutputStream(InputStream inputStream, S outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
