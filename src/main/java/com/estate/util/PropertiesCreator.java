package com.estate.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesCreator {
    public static void main(String[] args) {

        try (OutputStream output = Files.newOutputStream(Paths.get("../resources/config.properties"))) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("db.hostname", "localhost");
            prop.setProperty("db.port", "3000");
            prop.setProperty("db.namespace", "test");

            // save properties to project root folder
            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}
