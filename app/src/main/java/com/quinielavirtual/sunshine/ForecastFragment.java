package com.quinielavirtual.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.quinielavirtual.sunshine.webservice.WebService;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by a451383 on 05/11/2015.
 */
public class ForecastFragment extends Fragment {

    //region Fields
    private String[] dateList;
    private Calendar rightNow = Calendar.getInstance();
    private String weekdays[] = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
    private Map<String, String> parametersSunshine = new HashMap<String, String>();
    //endregion

    //region Constructor
    public ForecastFragment() {
    }
    //endregion

    //region Fragment Implements


    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    /**
     * Se crea antes de onCreatedView y con eso le decimos al código que tiene que sobreescribir el
     * método de onCreateOptionsMenu para agregar opciones de menu.
     *
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
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Method private
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        WebService web = new WebService(getActivity());
        parametersSunshine.put("UrlBase", "api.openweathermap.org");
        parametersSunshine.put("q", prefs.getString(getString(R.string.pref_location_key), ""));
        parametersSunshine.put("mode", "json");
        parametersSunshine.put("units", prefs.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_label_metric)));
        parametersSunshine.put("cnt", "7");
        parametersSunshine.put("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        web.execute(parametersSunshine);
    }
    //endregion

}
