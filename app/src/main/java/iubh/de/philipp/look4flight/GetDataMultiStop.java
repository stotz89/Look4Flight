package iubh.de.philipp.look4flight;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by philipp on 27.02.17.
 */

enum ConvertingStatusMulti { IDLE, PROCESSING, INITIALISED, FAILED_OR_EMPTY, OK}
enum DownloadStatusMulti { IDLE, PROCESSING, INITIALISED, FAILED_OR_EMPTY, OK}

public class GetDataMultiStop {

    private static final String LOG_TAG = GetData.class.getSimpleName();
    private Uri mDestinationUriFrom;
    private Uri mDestinationUriTo;
    private ArrayList<Flight> mFlightsFrom;
    private ArrayList<Flight> mFlightsTo;
    private ArrayList<MultiStopFlight> mMultiStopFlights;
    private ConvertingStatusMulti mConvertingStatus;
    private DownloadStatusMulti mDownloadStatus;



    public GetDataMultiStop(String depature, String destination, String date) {

        mDownloadStatus = DownloadStatusMulti.IDLE;
        mConvertingStatus = ConvertingStatusMulti.IDLE;
        createUriFrom(depature, date);
        createUriTo(destination, date);
        mMultiStopFlights = new ArrayList<MultiStopFlight>();
        mFlightsFrom = new ArrayList<Flight>();
        mFlightsTo = new ArrayList<Flight>();


    }

    public void reset() {

        mDownloadStatus = DownloadStatusMulti.IDLE;
        mConvertingStatus = ConvertingStatusMulti.IDLE;
        mMultiStopFlights.clear();
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
        final String IATA_FROM_PARAM = "iata_to";
        final String DATE_PARAM = "date";

        mDestinationUriTo = Uri.parse(FLIGHT_BASE_URL).buildUpon()
                .appendQueryParameter(IATA_FROM_PARAM, arrival)
                .appendQueryParameter(DATE_PARAM, date)
                .build();

        Log.e(LOG_TAG, mDestinationUriTo.toString());

        return mDestinationUriTo != null;

    }

    public boolean startProcessing() {

        getDataFromFlightDB flightDataFrom = new getDataFromFlightDB();
        getDataFromFlightDB flightDataTo = new getDataFromFlightDB();
        String jsonResponseFrom = null;
        String jsonResponseTo = null;

        try {

            jsonResponseFrom = flightDataFrom.execute(mDestinationUriFrom.toString()).get();
            jsonResponseTo = flightDataTo.execute(mDestinationUriTo.toString()).get();

        } catch (InterruptedException e) {
            mDownloadStatus = DownloadStatusMulti.FAILED_OR_EMPTY;

            e.printStackTrace();
        } catch (ExecutionException e) {
            mDownloadStatus = DownloadStatusMulti.FAILED_OR_EMPTY;

            e.printStackTrace();
        }


        // parse response into Array.
        // Initiale Response = { "flights": null }
        //if (!jsonResponse.isEmpty()) {
        if (jsonResponseFrom.length() > 30 && jsonResponseTo.length() > 30) {
            mDownloadStatus = DownloadStatusMulti.OK;
            parseJsonIntoArray(jsonResponseFrom, mFlightsFrom);
            parseJsonIntoArray(jsonResponseTo, mFlightsTo);

        } else {
            mDownloadStatus = DownloadStatusMulti.FAILED_OR_EMPTY;
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
                    MultiStopFlight tempMultiStopFlight = new MultiStopFlight();
                    tempMultiStopFlight.addFlight(FlightFrom);
                    tempMultiStopFlight.addFlight(FlightTo);
                    mMultiStopFlights.add(tempMultiStopFlight);
                }
            }
        }

        return (!mMultiStopFlights.isEmpty());

    }


    public void parseJsonIntoArray(String jsonResponse, ArrayList<Flight> array) {
        mConvertingStatus = ConvertingStatusMulti.PROCESSING;
        if(mDownloadStatus != DownloadStatusMulti.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            mConvertingStatus = ConvertingStatusMulti.FAILED_OR_EMPTY;
            return;
        }
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
                mConvertingStatus = ConvertingStatusMulti.FAILED_OR_EMPTY;
            }

            this.mConvertingStatus = ConvertingStatusMulti.OK;

        }

    }

    public ArrayList<MultiStopFlight> getmMultiStopFlights() {
        return mMultiStopFlights;
    }

    public ConvertingStatusMulti getmConvertingStatusMulti() {
        return mConvertingStatus;
    }

    public DownloadStatusMulti getmDownloadStatusMulti() {
        return mDownloadStatus;
    }

    public class getDataFromFlightDB extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            mDownloadStatus = DownloadStatusMulti.PROCESSING;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if(params == null)
                return null;

            try {
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null) {
                    return null;
                }

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                //Response of HTTP-Call is now in the buffer
                return buffer.toString();


            } catch(IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch(final IOException e) {
                        Log.e(LOG_TAG,"Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDownloadStatus = DownloadStatusMulti.INITIALISED;
        }
    }

}
