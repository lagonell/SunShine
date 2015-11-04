package com.quinielavirtual.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by a451383 on 02/11/2015.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        //region Fields
        private ArrayList<String> dateList = new ArrayList<String>();
        private Calendar rightNow = Calendar.getInstance();
        private String weekdays[] = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
        //private Random r = new Random();
        //endregion

        //region Constructor
        public PlaceholderFragment() {
        }
        //endregion

        //region Fragment Implements
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            try {

                for(Integer i = 0;i <= 15;i++)
                {
                    if(i==0)
                        dateList.add("Today - Sunny - 88 / 63");
                    else
                    {

                        rightNow.add(Calendar.DAY_OF_MONTH,1);
                        dateList.add(String.format("%1$s - %2$s - %3$s / %4$s", weekdays[rightNow.get(Calendar.DAY_OF_WEEK)],
                                "Rainy",String.valueOf(80+(int)(Math.random() * (90-80)+1)),
                                String.valueOf(60+(int)(Math.random() * (70-60)+1))));
                    }
                }

                ArrayAdapter<String> adapterData = new ArrayAdapter<String>(getActivity(),
                        R.layout.list_item_forecast,R.id.list_item_forecast_textview,dateList);
                ListView listView =  (ListView)rootView.findViewById(R.id.listview_forecast);
                listView.setAdapter(adapterData);

            } catch (Exception e){
                System.out.print(e.getMessage());
            }


            return rootView;
        }
        //endregion
    }
}
