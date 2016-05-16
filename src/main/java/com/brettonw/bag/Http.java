package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    private static final Logger log = LogManager.getLogger (Http.class);

    public static BagObject getBag (String urlString) {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            // get the response
            InputStream inputStream = connection.getInputStream();
            return new BagObject (inputStream);
        }
        catch (Exception exception) {
            log.error (exception);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static BagObject postBag (String urlString, BagObject bagObject) {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String  jsonString = bagObject.toJsonString();
            connection.setRequestProperty("Content-Length", Integer.toString(jsonString.getBytes().length));
            connection.setUseCaches(false);

            // send the request with data
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(jsonString);
            dataOutputStream.close();

            // get the response
            InputStream inputStream = connection.getInputStream();
            return new BagObject (inputStream);
        } catch (Exception exception) {
            log.error (exception);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
