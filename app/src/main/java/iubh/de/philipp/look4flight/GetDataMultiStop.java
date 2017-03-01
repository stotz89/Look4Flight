package iubh.de.philipp.look4flight;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by philipp on 27.02.17.
 */


public class GetDataMultiStop {

    private static final String LOG_TAG = GetData.class.getSimpleName();
    private Uri mDestinationUriFrom;
    private Uri mDestinationUriTo;
    private ArrayList<Flight> mFlightsFrom;
    private ArrayList<Flight> mFlightsTo;
    private ArrayList<Trip> mTrips;

    public GetDataMultiStop(String depature, String destination, String date) {

        createUriFrom(depature, date);
        createUriTo(destination, date);
        mTrips = new ArrayList<Trip>();
        mFlightsFrom = new ArrayList<Flight>();
        mFlightsTo = new ArrayList<Flight>();
    }

    public void reset() {

        mTrips.clear();
        mFlightsFrom.clear();
        mFlightsTo.clear();
        mDestinationUriTo = null;
        mDestinationUriFrom = null;

    }

    public boolean createUriFrom(String depature, String date) {

        //URI erzeugen
        final String FLIGHT_BASE_URL = "http://192.168.3.12/Look4Flight/searchflight_1stop_from.php";
        final String IATA_FROM_PARAM = "iata_from";
        final String DATE_PARAM = "date";

        mDestinationUriFrom = Uri.parse(FLIGHT_BASE_URL).buildUpon()
                .appendQueryParameter(IATA_FROM_PARAM, depature)
                .appendQueryParameter(DATE_PARAM, date)
                .build();

        Log.e(LOG_TAG, mDestinationUriFrom.toString());

        return mDestinationUriFrom != null;

    }

    public boolean createUriTo(String arrival, String date) {

        //URI erzeugen
        final String FLIGHT_BASE_URL = "http://192.168.3.12/Look4Flight/searchflight_1stop_to.php";
        final String IATA_TO_PARAM = "iata_to";
        final String DATE_PARAM = "date";

        mDestinationUriTo = Uri.parse(FLIGHT_BASE_URL).buildUpon()
                .appendQueryParameter(IATA_TO_PARAM, arrival)
                .appendQueryParameter(DATE_PARAM, date)
                .build();

        Log.e(LOG_TAG, mDestinationUriTo.toString());

        return mDestinationUriTo != null;

    }

    public boolean startProcessing() {

        DBConnection flightDataFrom = new DBConnection();
        DBConnection flightDataTo = new DBConnection();
        String jsonResponseFrom = null;
        String jsonResponseTo = null;

        try {

            jsonResponseFrom = flightDataFrom.execute(mDestinationUriFrom.toString()).get();
            jsonResponseTo = flightDataTo.execute(mDestinationUriTo.toString()).get();

        } catch (InterruptedException e) {
            //Download failed

            e.printStackTrace();
        } catch (ExecutionException e) {
            //Download failed

            e.printStackTrace();
        }


        // parse response into Array.
        // Initiale Response = { "flights": null }
        //if (!jsonResponse.isEmpty()) {
        if (jsonResponseFrom.length() > 30 && jsonResponseTo.length() > 30) {
            //Download OK
            parseJsonIntoArray(jsonResponseFrom, mFlightsFrom);
            parseJsonIntoArray(jsonResponseTo, mFlightsTo);

        } else {
            //Download failed
            return false;
        }

        // Flüge miteinander kombinieren.
        return createMultiStopFlights();

    }

    public boolean createMultiStopFlights() {

        // Loop über alle "ersten" Flüge. Anschließend loop über die "zweiten" Flüge.
        // Hier muss darauf geachtet werden, dass Destination des ersten Fluges und
        // Origin des zweiten Fluges, sowie Abflugszeit des zweiten Fluges größer gleich
        // Ankunftszeit des ersten Fluges ist.
        for(int iErster=0; iErster<mFlightsFrom.size(); iErster++) {

            for(int iZweiter=0; iZweiter<mFlightsTo.size(); iZweiter++) {

                //Informationen zu den Flügen holen.
                Flight FlightFrom = mFlightsFrom.get(iErster);
                Flight FlightTo = mFlightsTo.get(iZweiter);

                //Check auf Destination und Origin
                if (FlightFrom.getmIataTo().equals(FlightTo.getmIataFrom())) { //&&
                        //(FlightFrom.getmArrTime() <= FlightTo.getmDepTime() )   ) {
                    Trip tempTrip = new Trip();
                    tempTrip.addFlight(FlightFrom);
                    tempTrip.addFlight(FlightTo);
                    mTrips.add(tempTrip);
                }
            }
        }

        return (!mTrips.isEmpty());

    }


    public void parseJsonIntoArray(String jsonResponse, ArrayList<Flight> array) {

        final String FLIGHTS = "flights";
        final String FLIGHT_ID = "id";
        final String FLIGHT_NO = "no";
        final String FLIGHT_DATE = "dep_date";
        final String FLIGHT_PRICE_E = "price_e";
        final String FLIGHT_PRICE_B = "price_b";
        final String FLIGHT_PRICE_F = "price_f";
        final String FLIGHT_CURR = "currency";
        final String FLIGHT_IATA_FROM = "iata_from";
        final String FLIGHT_CITY_FROM = "city_from";
        final String FLIGHT_IATA_TO = "iata_to";
        final String FLIGHT_CITY_TO = "city_to";
        final String FLIGHT_DURATION = "duration";
        final String FLIGHT_DEP_TIME = "dep_time";
        final String FLIGHT_ARR_TIME = "arr_time";

        if (!jsonResponse.isEmpty()) {

            try {

                JSONObject jsonData = new JSONObject(jsonResponse);
                JSONArray itemsArray = jsonData.getJSONArray(FLIGHTS);
                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject jsonFlight = itemsArray.getJSONObject(i);
                    String id = jsonFlight.getString(FLIGHT_ID);
                    int no = jsonFlight.getInt(FLIGHT_NO);
                    String date = jsonFlight.getString(FLIGHT_DATE);
                    long price_e = jsonFlight.getLong(FLIGHT_PRICE_E);
                    long price_b = jsonFlight.getLong(FLIGHT_PRICE_B);
                    long price_f = jsonFlight.getLong(FLIGHT_PRICE_F);
                    String curr = jsonFlight.getString(FLIGHT_CURR);
                    String iata_from = jsonFlight.getString(FLIGHT_IATA_FROM);
                    String city_from = jsonFlight.getString(FLIGHT_CITY_FROM);
                    String iata_to = jsonFlight.getString(FLIGHT_IATA_TO);
                    String city_to = jsonFlight.getString(FLIGHT_CITY_TO);
                    long duration = jsonFlight.getLong(FLIGHT_DURATION);
                    String dep_time = jsonFlight.getString(FLIGHT_DEP_TIME);
                    String arr_time = jsonFlight.getString(FLIGHT_ARR_TIME);

                    Flight FlightObject = new Flight(id, no, date, price_e, price_b, price_f, curr, iata_from, city_from, iata_to, city_to, duration, dep_time, arr_time);

                    array.add(FlightObject);
                }

                for (Flight singleFlight : array) {
                    Log.v(LOG_TAG, singleFlight.toString());
                }

            } catch (JSONException jsone) {
                jsone.printStackTrace();
                Log.e(LOG_TAG, "Error processing Json data");
                //Converting failed
            }
        }

    }

    public ArrayList<Trip> getmTrips() {
        return mTrips;
    }



}
