package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A helper class for GET and POST with JSON data in the request and the response.
 */
public class Http {
    Http () {}

    private static final Logger log = LogManager.getLogger (Http.class);

    @FunctionalInterface
    interface CheckedFunction<T, R> {
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

    /**
     * returns a BagObject derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject getForBagObject (String urlString) {
        return get (urlString, inputStream -> new BagObject (inputStream));
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagArray
     */
    public static BagArray getForBagArray (String urlString) {
        return get (urlString, inputStream -> new BagArray (inputStream));
    }

    public static <T> T post (String urlString, Bag bag, CheckedFunction<InputStream, Object> function) {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String  jsonString = bag.toString(BuilderJson.JSON_FORMAT);
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
    /**
     * returns a BagObject derived from a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject postForBagObject (String urlString, Bag bag) {
        return post (urlString, bag, inputStream -> new BagObject (inputStream));
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagArray postForBagArray (String urlString, Bag bag) {
        return post (urlString, bag, inputStream -> new BagArray (inputStream));
    }

}
