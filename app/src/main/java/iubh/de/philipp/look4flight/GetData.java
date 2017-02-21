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
 * Created by philipp on 27.01.17.
 */

enum ConvertingStatus { IDLE, PROCESSING, INITIALISED, FAILED_OR_EMPTY, OK}
enum DownloadStatus { IDLE, PROCESSING, INITIALISED, FAILED_OR_EMPTY, OK}

public class GetData {

    private static final String LOG_TAG = GetData.class.getSimpleName();
    private Uri mDestinationURI;
    private ArrayList<Flight> mFlights;
    private ConvertingStatus mConvertingStatus;
    private DownloadStatus mDownloadStatus;



    public GetData(String depature, String destination, String date) {

        mDownloadStatus = DownloadStatus.IDLE;
        mConvertingStatus = ConvertingStatus.IDLE;
        createURI(depature, destination, date);
        mFlights = new ArrayList<Flight>();


    }

    public void reset() {

        mDownloadStatus = DownloadStatus.IDLE;
        mConvertingStatus = ConvertingStatus.IDLE;
        mFlights.clear();
        mDestinationURI = null;

    }

    public boolean createURI(String depature, String destination, String date) {

        //URI erzeugen

        final String FLIGHT_BASE_URL = "http://192.168.3.12/Look4Flight/searchflight_nonstop_by_date.php";
        final String IATA_FROM_PARAM = "iata_from";
        final String IATA_TO_PARAM = "iata_to";
        final String DATE_PARAM = "date";

        mDestinationURI = Uri.parse(FLIGHT_BASE_URL).buildUpon()
                .appendQueryParameter(IATA_FROM_PARAM, depature)
                .appendQueryParameter(IATA_TO_PARAM, destination)
                .appendQueryParameter(DATE_PARAM, date)
                .build();

        Log.e(LOG_TAG, mDestinationURI.toString());

        return mDestinationURI != null;

    }

    public boolean startProcessing() {

        getDataFromFlightDB flightData = new getDataFromFlightDB();
        String jsonResponse = null;

        try {

            jsonResponse = flightData.execute(mDestinationURI.toString()).get();
        } catch (InterruptedException e) {
            mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;

            e.printStackTrace();
        } catch (ExecutionException e) {
            mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;

            e.printStackTrace();
        }


        // parse response into Array.
        if (!jsonResponse.isEmpty()) {
            mDownloadStatus = DownloadStatus.OK;
            parseJsonIntoArray(jsonResponse);
            return true;
        } else {
            mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            return false;
        }

    }


    public void parseJsonIntoArray(String jsonResponse) {
        mConvertingStatus = ConvertingStatus.PROCESSING;
        if(mDownloadStatus != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            mConvertingStatus = ConvertingStatus.FAILED_OR_EMPTY;
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

                    this.mFlights.add(FlightObject);
                }

                for (Flight singleFlight : mFlights) {
                    Log.v(LOG_TAG, singleFlight.toString());
                }

            } catch (JSONException jsone) {
                jsone.printStackTrace();
                Log.e(LOG_TAG, "Error processing Json data");
                mConvertingStatus = ConvertingStatus.FAILED_OR_EMPTY;
            }

            this.mConvertingStatus = ConvertingStatus.OK;

        }

    }

    public ArrayList<Flight> getmFlights() {
        return mFlights;
    }

    public ConvertingStatus getmConvertingStatus() {
        return mConvertingStatus;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public class getDataFromFlightDB extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            mDownloadStatus = DownloadStatus.PROCESSING;

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
            mDownloadStatus = DownloadStatus.INITIALISED;
        }
    }

}
