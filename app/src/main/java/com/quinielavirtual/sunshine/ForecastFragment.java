package com.quinielavirtual.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by a451383 on 05/11/2015.
 */
public class ForecastFragment extends Fragment {

    //region Fields
    private ArrayList<String> dateList = new ArrayList<>();
    private Calendar rightNow = Calendar.getInstance();
    private String weekdays[] = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
    private Map<String, String> parametersSunshine = new HashMap<String, String>();
    //endregion

    //region Constructor
    public ForecastFragment() {
    }
    //endregion

    //region Fragment Implements

    /**
     * Se crea antes de onCreatedView y con eso le decimos al código que tiene que sobreescribir el
     * método de onCreateOptionsMenu para agregar opciones de menu.
     * @param savedInstanceState
     * @return
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        try {
//            webservice web = new webservice();
//            parametersSunshine.put("q", "94043");
//            parametersSunshine.put("mode", "json");
//            parametersSunshine.put("units", "metric");
//            parametersSunshine.put("cnt", "7");
////                parametersSunshine.put("appid", "e288bfc52c9b791e0b92586e628b56cc");
//            web.setUrlParameters(parametersSunshine);
//            web.execute("http://api.openweathermap.org/data/2.5/forecast/daily?");

            FetchWeatherTask task = new FetchWeatherTask();
            task.execute();

            for (Integer i = 0; i <= 15; i++) {
                if (i == 0)
                    dateList.add("Today - Sunny - 88 / 63");
                else {

                    rightNow.add(Calendar.DAY_OF_MONTH, 1);
                    dateList.add(String.format("%1$s - %2$s - %3$s / %4$s", weekdays[rightNow.get(Calendar.DAY_OF_WEEK)],
                            "Rainy", String.valueOf(80 + (int) (Math.random() * (90 - 80) + 1)),
                            String.valueOf(60 + (int) (Math.random() * (70 - 60) + 1))));
                }
            }

            ArrayAdapter<String> adapterData = new ArrayAdapter<String>(getActivity(),
                    R.layout.list_item_forecast, R.id.list_item_forecast_textview, dateList);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(adapterData);

        } catch (Exception e) {
            Log.e("PlaceholderFragment", "Error ", e);
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fresh) {
            FetchWeatherTask task = new FetchWeatherTask();
            task.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=e288bfc52c9b791e0b92586e628b56cc");

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
                Log.v(LOG_TAG,"Forecast JSON String:" + forecastJsonStr);
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
//                return forecastJsonStr;
                return null;
            }
        }
    }
    //endregion

}
