package com.quinielavirtual.sunshine.webservice;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.quinielavirtual.sunshine.R;

import org.json.JSONException;

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
public class WebService extends AsyncTask<Map<String, String>, Void, String[]> {

    //region fields
    private final String LOG_TAG = WebService.class.getSimpleName();
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;
    private Map<String, String> urlParameters = new HashMap<>();
    private URL url;

    FragmentActivity fragmentActivity;
    //endregion

    //region Properties

    //endregion

    //region constructor
    public WebService(FragmentActivity frament) {
        fragmentActivity = frament;
    }
    //endregion

    //region method publics
    private String[] ConnectionWebservice() {
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(urlParameters.get("UrlBase").toString())
                    .path("data/2.5/forecast/daily");

            for (Map.Entry<String, String> item : urlParameters.entrySet()) {
                if (!item.getKey().contains("UrlBase"))
                    builder.appendQueryParameter(item.getKey(), item.getValue());
            }

//            Log.v(LOG_TAG, builder.build().toString());
            url = new URL(builder.build().toString());
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
//            Log.v(LOG_TAG, "Forecast JSON String:" + forecastJsonStr);
            WeatherDataParser parser = new WeatherDataParser();
            return parser.getWeatherDataFromJson(forecastJsonStr, 7);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
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

        }
    }

    @Override
    protected String[] doInBackground(Map<String, String>... params) {
        if (params.length < 0)
            return null;
        urlParameters = params[0];
        return ConnectionWebservice();
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(result != null)
        {
            ArrayAdapter<String> adapterData = new ArrayAdapter<>(fragmentActivity,
                    R.layout.list_item_forecast, R.id.list_item_forecast_textview, result);
            //adapterData.notifyDataSetChanged();
            ListView listView = (ListView) fragmentActivity.findViewById(R.id.listview_forecast);
            listView.setAdapter(adapterData);
        }
    }
    //endregion
}
