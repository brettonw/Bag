package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    private static final Logger log = LogManager.getLogger (Http.class);

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    private static <T> T get (String urlString, CheckedFunction<InputStream, Object> function) {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            // get the response
            InputStream inputStream = connection.getInputStream();
            return (T) function.apply (inputStream);
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

    public static BagObject getForBagObject (String urlString) {
        return get (urlString, inputStream -> new BagObject (inputStream));
    }

    public static BagArray getForBagArray (String urlString) {
        return get (urlString, inputStream -> new BagArray (inputStream));
    }

    public static <T> T post (String urlString, Base base, CheckedFunction<InputStream, Object> function) {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String  jsonString = base.toJsonString();
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
            return (T) function.apply (inputStream);
        } catch (Exception exception) {
            log.error (exception);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public static BagObject postForBagObject (String urlString, Base base) {
        return post (urlString, base, inputStream -> new BagObject (inputStream));
    }

    public static BagArray postForBagArray (String urlString, Base base) {
        return post (urlString, base, inputStream -> new BagArray (inputStream));
    }

}
