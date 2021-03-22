package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.AisClientException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {

    public static Properties loadPropertiesFromFile(String filepath) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(filepath));
            return properties;
        } catch (IOException e) {
            throw new AisClientException(String.format("Failed to load data properties from the file: %s", filepath), e);
        }
    }
}
