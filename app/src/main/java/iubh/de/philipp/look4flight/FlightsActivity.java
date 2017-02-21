package iubh.de.philipp.look4flight;

import android.app.ProgressDialog;
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

    private ListView mItemListView;
    private String mDateFrom;
    private String mDateTo;
    private String mOrigin;
    private String mDestination;


    private ArrayList<Flight> mFlightsTo = new ArrayList<Flight>();
    private ArrayList<Flight> mFlightsBack = new ArrayList<Flight>();
    private ArrayList<Roundtrip> mRoundtrip;

    private List<String> mOriginArray = new ArrayList<String>();
    private List<String> mDestinationArray = new ArrayList<String>();

    private GetData mJsonFlightTo;
    private GetData mJsonFlightBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        mItemListView = (ListView)findViewById(R.id.custom_list);
        mRoundtrip = new ArrayList<Roundtrip>();

        ProgressDialog progressDialog = new ProgressDialog(FlightsActivity.this);
        //Progressdialog starten.
        progressDialog.show();

        // Get values of old activity
        Intent oldactivity = getIntent();
        mOrigin = oldactivity.getExtras().getString("origin");
        mDestination = oldactivity.getExtras().getString("destination");
        mDateFrom = oldactivity.getExtras().getString("dateFrom");
        mDateTo = oldactivity.getExtras().getString("dateTo");

        //Split destination and origin into an srtring array in case of a multi entry
        String[] temp = mOrigin.split(",");
        for(String s : temp) {
            if (s != null && s.length() >= 3) {
                mOriginArray.add(s.substring(0,3));
            }
        }

        temp = mDestination.split(",");
        for(String s : temp) {
            if (s != null && s.length() >= 3) {
                mDestinationArray.add(s.substring(0,3));
            }
        }

        // Hole alle Hin- und Rückflüge
        getAllFlights();

        // Loop über jeden Hinflug
        getAllFlightCombinations();


        // Flugkombi ausgeben
        for(Roundtrip singleRoundtrip: mRoundtrip) {
            Log.v(LOG_TAG, mRoundtrip.toString());
        }

        //Progressdialog schließen.
        progressDialog.dismiss();

        // Flugkombi auf den ItemListViewer setzen.
        mItemListView.setAdapter(new CustomListAdapter(this, mRoundtrip));
        mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = mItemListView.getItemAtPosition(position);
                Roundtrip selectedFlight = (Roundtrip) o;
                Log.e(LOG_TAG, selectedFlight.getmFlightTo().toString());
                Log.e(LOG_TAG, selectedFlight.getmFlightBack().toString());
            }
        });

    }

    private void getAllFlights() {

        //Loop über die Arrays um alle Kombinationen zu bekommen.
        for(int iOrigin=0; iOrigin<mOriginArray.size(); iOrigin++) {

            for(int iDest=0; iDest<mDestinationArray.size(); iDest++) {
                Log.e(LOG_TAG, "Origin: " + mOriginArray.get(iOrigin));
                Log.e(LOG_TAG, "Destination: " + mDestinationArray.get(iDest));

                mJsonFlightTo = new GetData(mOriginArray.get(iOrigin), mDestinationArray.get(iDest), mDateFrom);
                mJsonFlightBack = new GetData(mDestinationArray.get(iDest), mOriginArray.get(iOrigin), mDateTo);

                mJsonFlightTo.startProcessing();
                mJsonFlightBack.startProcessing();

                // Alle Flüge zwischenspeichern
                this.mFlightsTo.addAll(mJsonFlightTo.getmFlights());
                this.mFlightsBack.addAll(mJsonFlightBack.getmFlights());

            }
        }

    }

    private void getAllFlightCombinations() {

        for(int iHin=0; iHin<mFlightsTo.size(); iHin++) {

            Flight tempFlightTo = mFlightsTo.get(iHin);
            // Jeden Hinflug mit allen Rückflügen kombinieren.
            for(int iRue=0; iRue<mFlightsBack.size(); iRue++) {
                Flight tempFlightBack = mFlightsBack.get(iRue);

                Roundtrip RoundtripObject = new Roundtrip(tempFlightTo, tempFlightBack);

                this.mRoundtrip.add(RoundtripObject);

            }

        }

    }


}
