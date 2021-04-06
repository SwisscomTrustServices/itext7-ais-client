/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.AisClientException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    private static final String USAGE_TEXT_FILE_PATH = "/cli/usage.txt";
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String TIME_PLACEHOLDER = "#time";

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

    public static String generateOutputFileName(String inputFile, String suffix) {
        String finalSuffix = suffix.replaceAll(TIME_PLACEHOLDER, TIME_PATTERN.format(LocalDateTime.now()));
        int extensionIndex = inputFile.lastIndexOf('.');
        return extensionIndex > 0 ? inputFile.substring(0, extensionIndex) + finalSuffix + inputFile.substring(extensionIndex)
                                  : inputFile + finalSuffix;
    }

    private static <S extends OutputStream> void writeToOutputStream(InputStream inputStream, S outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
