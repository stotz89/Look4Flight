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
import java.util.List;

/**
 * Created by philipp on 20.01.17.
 */

public class FlightsActivity extends AppCompatActivity {
    private static final String LOG_TAG = FlightsActivity.class.getSimpleName();

    private ListView itemListView;

    private ArrayList<Flight> mFlightsTo = new ArrayList<Flight>();
    private ArrayList<Flight> mFlightsBack = new ArrayList<Flight>();
    private ArrayList<Roundtrip> mRoundtrip;

    private List<String> originArray = new ArrayList<String>();
    private List<String> destinationArray = new ArrayList<String>();

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

        //Split destination and origin into an srtring array in case of a multi entry
        String[] temp = origin.split(",");
        for(String s : temp) {
            if (s != null && s.length() >= 3) {
                originArray.add(s.substring(0,3));
            }
        }

        temp = destination.split(",");
        for(String s : temp) {
            if (s != null && s.length() >= 3) {
                destinationArray.add(s.substring(0,3));
            }
        }

        //Loop über die Arrays um alle Kombinationen zu bekommen.
        for(int iOrigin=0; iOrigin<originArray.size(); iOrigin++) {

            for(int iDest=0; iDest<destinationArray.size(); iDest++) {
                Log.e(LOG_TAG, "Origin: " + originArray.get(iOrigin));
                Log.e(LOG_TAG, "Destination: " + destinationArray.get(iDest));

                jsonFlightTo = new GetData(originArray.get(iOrigin), destinationArray.get(iDest), dateFrom);
                jsonFlightBack = new GetData(destinationArray.get(iDest), originArray.get(iOrigin), dateTo);

                jsonFlightTo.startProcessing(FlightsActivity.this);
                jsonFlightBack.startProcessing(FlightsActivity.this);

                this.mFlightsTo.addAll(jsonFlightTo.getmFlights());
                this.mFlightsBack.addAll(jsonFlightBack.getmFlights());

            }
        }

        mRoundtrip = new ArrayList<Roundtrip>();

        // Loop über jeden Hinflug
        for(int iHin=0; iHin<mFlightsTo.size(); iHin++) {

            Flight tempFlightTo = mFlightsTo.get(iHin);
            // Jeden Hinflug mit allen Rückflügen kombinieren.
            for(int iRue=0; iRue<mFlightsBack.size(); iRue++) {
                Flight tempFlightBack = mFlightsBack.get(iRue);

                Roundtrip RoundtripObject = new Roundtrip(tempFlightTo, tempFlightBack);

                this.mRoundtrip.add(RoundtripObject);

            }

        }

        for(Roundtrip singleRoundtrip: mRoundtrip) {
            Log.v(LOG_TAG, mRoundtrip.toString());
        }

        itemListView.setAdapter(new CustomListAdapter(this, mRoundtrip));
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = itemListView.getItemAtPosition(position);
                Roundtrip selectedFlight = (Roundtrip) o;
                Log.e(LOG_TAG, selectedFlight.getmFlightTo().toString());
                Log.e(LOG_TAG, selectedFlight.getmFlightBack().toString());
            }
        });

    }


}
