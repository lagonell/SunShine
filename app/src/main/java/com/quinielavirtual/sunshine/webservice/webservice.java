package com.quinielavirtual.sunshine.webservice;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a451383 on 05/11/2015.
 */
public class webservice extends AsyncTask<String,Void,String > {

    //region fields
    private final  String LOG_TAG = webservice.class.getSimpleName();
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;
    private Map<String, String> urlParameters = new HashMap<>();
    private URL url;
    private String urlBase;
    //endregion

    //region Properties
    public Map<String, String> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(Map<String, String> urlParameters) {
        this.urlParameters = urlParameters;
    }
    //endregion

    //region constructor
//    public webservice(String url) {
//        urlBase = url;
//    }
    //endregion

    //region method publics
    private String ConnectionWebservice() {
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            String internalParameters = "";
            for (Map.Entry<String, String> item : urlParameters.entrySet()) {
                internalParameters = internalParameters + String.format("%1$s=%2$s&", item.getKey(), item.getValue());
            }

            url = new URL(urlBase + internalParameters.substring(0, internalParameters.length() - 1));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return forecastJsonStr;
        }
    }

    @Override
    protected String doInBackground(String... params) {
        urlBase = params[0].toString();
        return ConnectionWebservice();
    }
    //endregion
}
