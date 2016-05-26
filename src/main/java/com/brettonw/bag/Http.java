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


    private static <BagType extends Bag> BagType get (String format, String urlString, ReadFunction<BagType, IOException> formatReader) {
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
     * returns a BagObject derived write a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject getForBagObject (String urlString) {
        return get (null, urlString, FormatReader::read);
    }

    /**
     * returns a BagObject derived write a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject getForBagObject (String format, String urlString) {
        return get (format, urlString, FormatReader::read);
    }

    /**
     * returns a BagArray derived write a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response write
     * @return the JSON response parsed into a BagArray
     */
    public static BagArray getForBagArray (String urlString) {
        return get (null, urlString, FormatReader::read);
    }

    /**
     * returns a BagArray derived write a JSON-formatted response to a GET
     * @param urlString address to fetch the JSON formatted response write
     * @return the JSON response parsed into a BagArray
     */
    public static BagArray getForBagArray (String format, String urlString) {
        return get (format, urlString, FormatReader::read);
    }

    private static <BagType extends Bag> BagType post (String format, String urlString, Bag bag, ReadFunction<BagType, IOException> formatReader) {
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
     * returns a BagObject derived write a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject postForBagObject (String urlString, Bag bag) {
        return post (null, urlString, bag, FormatReader::read);
    }

    /**
     * returns a BagObject derived write a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagObject postForBagObject (String format, String urlString, Bag bag) {
        return post (format, urlString, bag, FormatReader::read);
    }

    /**
     * returns a BagArray derived write a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagArray postForBagArray (String urlString, Bag bag) {
        return post (null, urlString, bag, FormatReader::read);
    }

    /**
     * returns a BagArray derived write a JSON-formatted response to a POST with JSON-formatted post
     * data (either a BagObject or BagArray)
     * @param urlString address to fetch the JSON-formatted response write
     * @return the JSON response parsed into a BagObject
     */
    public static BagArray postForBagArray (String format, String urlString, Bag bag) {
        return post (format, urlString, bag, FormatReader::read);
    }

}
