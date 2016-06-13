package com.brettonw.bag;

import com.brettonw.bag.json.FormatWriterJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A helper class for GET and POST with JSON data in the request and the response.
 */
public class Http {
    private static final Logger log = LogManager.getLogger (Http.class);

    Http () {}

    @FunctionalInterface
    interface ReadFunction<BagType extends Bag, ExceptionType extends Throwable> {
        BagType read(BagType bagType, String format, String name, Reader reader) throws ExceptionType;
    }


    private static <BagType extends Bag> BagType get (String format, String urlString, ReadFunction<BagType, IOException> formatReader) throws IOException {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            // get the response
            InputStream inputStream = connection.getInputStream();
            Reader inputStreamReader = new InputStreamReader (inputStream);
            return formatReader.read (null, format, urlString, inputStreamReader);
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
     * returns a BagObject derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject getForBagObject (String urlString, CheckedSupplier<BagObject, IOException> supplier) throws IOException {
        BagObject result = get (null, urlString, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagObject derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject getForBagObject (String format, String urlString, CheckedSupplier<BagObject, IOException> supplier) throws IOException {
        BagObject result = get (format, urlString, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagArray
     */
    public static BagArray getForBagArray (String urlString, CheckedSupplier<BagArray, IOException> supplier) throws IOException {
        BagArray result = get (null, urlString, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response from
     * @return the JSON response parsed into a BagArray
     */
    public static BagArray getForBagArray (String format, String urlString, CheckedSupplier<BagArray, IOException> supplier) throws IOException {
        BagArray result = get (format, urlString, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    private static <BagType extends Bag> BagType post (String format, String urlString, Bag bag, ReadFunction<BagType, IOException> formatReader) throws IOException {
        HttpURLConnection connection = null;
        try {
            // create the connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String  jsonString = bag.toString(FormatWriterJson.JSON_FORMAT);
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
            Reader inputStreamReader = new InputStreamReader (inputStream);
            return formatReader.read (null, format, urlString, inputStreamReader);
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
    public static BagObject postForBagObject (String urlString, Bag bag, CheckedSupplier<BagObject, IOException> supplier) throws IOException {
        BagObject result = post (null, urlString, bag, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagObject derived from a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject postForBagObject (String format, String urlString, Bag bag, CheckedSupplier<BagObject, IOException> supplier) throws IOException {
        BagObject result = post (format, urlString, bag, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagArray postForBagArray (String urlString, Bag bag, CheckedSupplier<BagArray, IOException> supplier) throws IOException {
        BagArray result = post (null, urlString, bag, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

    /**
     * returns a BagArray derived from a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response from
     * @return the JSON response parsed into a BagObject
     */
    public static BagArray postForBagArray (String format, String urlString, Bag bag, CheckedSupplier<BagArray, IOException> supplier) throws IOException {
        BagArray result = post (format, urlString, bag, FormatReader::read);
        return (result != null) ? result : supplier.get ();
    }

}
