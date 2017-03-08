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
import android.widget.Toast;

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
    private boolean mSwRoundtrip;


    private ArrayList<Trip> mTripTo = new ArrayList<Trip>();
    private ArrayList<Trip> mTripBack = new ArrayList<Trip>();
    private ArrayList<Roundtrip> mRoundtrip;

    private List<String> mOriginArray = new ArrayList<String>();
    private List<String> mDestinationArray = new ArrayList<String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        mItemListView = (ListView) findViewById(R.id.custom_list);
        mRoundtrip = new ArrayList<Roundtrip>();

        ProgressDialog progressDialog = new ProgressDialog(FlightsActivity.this);
        //Progressdialog starten.
        progressDialog.show();

        // Get values of old activity
        Intent oldactivity = getIntent();
        String origin = oldactivity.getExtras().getString("origin");
        String destination = oldactivity.getExtras().getString("destination");
        mDateFrom = oldactivity.getExtras().getString("dateFrom");
        mDateTo = oldactivity.getExtras().getString("dateTo");
        boolean swNonStop = oldactivity.getExtras().getBoolean("NonStop");
        mSwRoundtrip = oldactivity.getExtras().getBoolean("Roundtrip");
        int persons = Integer.parseInt(oldactivity.getExtras().getString("Persons"));

        //Abflug und Ziel in Array speichern (Im Falle von Multiangaben)
        String[] temp = origin.split(", ");
        for (String s : temp) {
            if (s != null && s.length() >= 3) {
                mOriginArray.add(s.substring(0, 3));
            }
        }

        temp = destination.split(", ");
        for (String s : temp) {
            if (s != null && s.length() >= 3) {
                mDestinationArray.add(s.substring(0, 3));
            }
        }

        // Hole alle Hin- und Rückflüge (non-stop)
        getAllNonStopFlights();

        // Nur wenn gewünscht, auch Multi Stop selektieren
        if (!swNonStop) {
            getAllFlightsWithStops();
        }

        if (!mTripTo.isEmpty()) {

            // Loop über alle Kombinationen
            getAllFlightCombinations();
            //Progressdialog schließen.
            progressDialog.dismiss();

        } else {
            //Auf den ersten Activity zurückkehren.
            //Progressdialog schließen.
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Keine Flugkombination/Flüge gefunden", Toast.LENGTH_LONG).show();
            this.finish();
        }


        // Flugkombi auf den ItemListViewer setzen.
        mItemListView.setAdapter(new CustomListAdapter(this, mRoundtrip, mSwRoundtrip, persons));
        mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = mItemListView.getItemAtPosition(position);
                Roundtrip selectedFlight = (Roundtrip) o;
                Log.e(LOG_TAG, selectedFlight.getmTripTo().toString());
                if (mSwRoundtrip) {
                    Log.e(LOG_TAG, selectedFlight.getmTripBack().toString());
                }

            }
        });

    }

    private boolean getAllNonStopFlights() {

        //Loop über die Arrays um alle Kombinationen zu bekommen (Non-Stop).
        for (int iOrigin = 0; iOrigin < mOriginArray.size(); iOrigin++) {

            for (int iDest = 0; iDest < mDestinationArray.size(); iDest++) {
                Log.e(LOG_TAG, "Origin: " + mOriginArray.get(iOrigin));
                Log.e(LOG_TAG, "Destination: " + mDestinationArray.get(iDest));

                GetData jsonFlightTo = new GetData(mOriginArray.get(iOrigin), mDestinationArray.get(iDest), mDateFrom);
                if (jsonFlightTo.startProcessing()) {
                    this.mTripTo.addAll(jsonFlightTo.getmTrips());

                } else {
                    // Keine Flüge gefunden --> return false
                    return false;
                }

                if (mSwRoundtrip) {
                    GetData jsonFlightBack = new GetData(mDestinationArray.get(iDest), mOriginArray.get(iOrigin), mDateTo);

                    if (jsonFlightBack.startProcessing()) {
                        this.mTripBack.addAll(jsonFlightBack.getmTrips());
                    } else {
                        // Keine Flüge gefunden --> return false
                        return false;
                    }
                }
            }
        }
        return true;

    }

    private boolean getAllFlightsWithStops() {

        //Loop über die Arrays um alle Kombinationen zu bekommen (Multi-Stop).
        for (int iOrigin = 0; iOrigin < mOriginArray.size(); iOrigin++) {

            for (int iDest = 0; iDest < mDestinationArray.size(); iDest++) {
                Log.e(LOG_TAG, "Origin: " + mOriginArray.get(iOrigin));
                Log.e(LOG_TAG, "Destination: " + mDestinationArray.get(iDest));

                GetDataMultiStop multiStopFlightTo = new GetDataMultiStop(mOriginArray.get(iOrigin), mDestinationArray.get(iDest), mDateFrom);
                if (multiStopFlightTo.startProcessing()) {
                    this.mTripTo.addAll(multiStopFlightTo.getmTrips());

                } else {
                    // Keine Flüge gefunden --> return false
                    return false;
                }

                if (mSwRoundtrip) {
                    GetDataMultiStop multiStopFlightBack = new GetDataMultiStop(mDestinationArray.get(iDest), mOriginArray.get(iOrigin), mDateTo);

                    if (multiStopFlightBack.startProcessing()) {
                        this.mTripBack.addAll(multiStopFlightBack.getmTrips());
                    } else {
                        // Keine Flüge gefunden --> return false
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void getAllFlightCombinations() {

        // Alle Non-Stop-Hinflüge
        for (int iHin = 0; iHin < mTripTo.size(); iHin++) {

            Trip tempFlightTo = mTripTo.get(iHin);
            // Jeden Hinflug mit allen Rückflügen kombinieren falls erwünscht.
            if (mSwRoundtrip) {
                for (int iRue = 0; iRue < mTripBack.size(); iRue++) {
                    Trip tempFlightBack = mTripBack.get(iRue);

                    Roundtrip RoundtripObject = new Roundtrip(tempFlightTo, tempFlightBack);

                    this.mRoundtrip.add(RoundtripObject);

                }
            } else {

                Roundtrip RoundtripObject = new Roundtrip(tempFlightTo);

                this.mRoundtrip.add(RoundtripObject);

            }
        }

        // Alle Multi-Stop Hinflüge
        /*for (int iHin = 0; iHin < mMuiltiStopFlightsToArray.size(); iHin++) {

            Trip tempMultiFlightTo = mMuiltiStopFlightsToArray.get(iHin);
            // Jeden Hinflug mit allen Rückflügen kombinieren falls erwünscht.
            if (mSwRoundtrip) {
                // Mit allen Non-Stop Flügen
                for (int iRue = 0; iRue < mTripBack.size(); iRue++) {
                    Trip tempFlightBack = mTripBack.get(iRue);

                    Roundtrip RoundtripObject = new Roundtrip(tempMultiFlightTo, tempFlightBack);

                    this.mRoundtrip.add(RoundtripObject);

                }
                // Mit allen Multi-Stop Flügen
                for (int iRue = 0; iRue < mMuiltiStopFlightsBackArray.size(); iRue++) {
                    Trip tempMultiFlightBack = mMuiltiStopFlightsBackArray.get(iRue);

                    Roundtrip RoundtripObject = new Roundtrip(tempMultiFlightTo, tempMultiFlightBack);

                    this.mRoundtrip.add(RoundtripObject);

                }
            } else {

                Roundtrip RoundtripObject = new Roundtrip(tempMultiFlightTo);

                this.mRoundtrip.add(RoundtripObject);

            }

        }*/

    }
}

