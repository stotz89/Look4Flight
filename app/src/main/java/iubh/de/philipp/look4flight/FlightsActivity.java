package iubh.de.philipp.look4flight;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by philipp on 20.01.17.
 */

public class FlightsActivity extends AppCompatActivity {
    private static final String LOG_TAG = FlightsActivity.class.getSimpleName();

    private ListView itemListView;

    private ArrayList<Flight> mFlightsTo;
    private ArrayList<Flight> mFlightsBack;

    private GetData jsonFlightTo;
    private GetData jsonFlightBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        itemListView = (ListView)findViewById(R.id.custom_list);

        // Get values of old activity
        Intent oldactivity = getIntent();
        String origin = oldactivity.getExtras().getString("origin");
        String destination = oldactivity.getExtras().getString("destination");
        String dateFrom = oldactivity.getExtras().getString("dateFrom");
        String dateTo = oldactivity.getExtras().getString("dateTo");

        //Log.e(LOG_TAG, origin + destination + date);

        jsonFlightTo = new GetData(origin, destination, dateFrom);
        jsonFlightBack = new GetData(destination, origin, dateTo);

        jsonFlightTo.startProcessing(FlightsActivity.this);
        jsonFlightBack.startProcessing(FlightsActivity.this);

        mFlightsTo = jsonFlightTo.getmFlights();
        mFlightsBack = jsonFlightBack.getmFlights();

        itemListView.setAdapter(new CustomListAdapter(this, mFlightsTo));
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = itemListView.getItemAtPosition(position);
                Flight selectedFlight = (Flight) o;
                Log.e(LOG_TAG, selectedFlight.toString());
            }
        });

    }


}
