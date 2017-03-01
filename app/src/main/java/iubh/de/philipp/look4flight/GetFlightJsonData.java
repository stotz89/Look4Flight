package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 25.01.17.
 */


//public class GetFlightJsonData extends GetRawDataFromDB {

    /*private String LOG_TAG = GetFlightJsonData.class.getSimpleName();
    private ArrayList<Flight> mFlights;
    private Uri mDestinationURI;
    private ConvertingStatus mConvertingStatus;
    private ProgressDialog dialog;

    public GetFlightJsonData(String depature, String destination, String date) {
        super(null);
        Log.v(LOG_TAG, "Constructor");
        createURI(depature, destination, date);
        mFlights = new ArrayList<Flight>();
        this.mConvertingStatus = ConvertingStatus.IDLE;

    }

    public ArrayList<Flight> getmTrips() {
        for(Flight singleFlight: mFlights) {
            Log.v(LOG_TAG + "_GET", singleFlight.toString());
        }
        return mFlights;
    }

    public ConvertingStatus getmConvertingStatus() {
        return this.mConvertingStatus;
    }

    public boolean isFinished() {

        Log.e(LOG_TAG, "Convertingstatus: " + this.mConvertingStatus);
        if (this.mConvertingStatus.equals(ConvertingStatus.IDLE)) {
            Log.e(LOG_TAG, "Convertingstatus: Return false");
            return false;
        } else {
            Log.e(LOG_TAG, "Convertingstatus: Return true");
            return true;
        }
    }

    public void execute() {
        super.setmRawUrl(mDestinationURI.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.v(LOG_TAG, "Built URI = " + mDestinationURI.toString());
        downloadJsonData.execute(mDestinationURI.toString());
    }

    public boolean createURI(String depature, String destination, String date) {


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

    public void processResult() {
        this.mConvertingStatus = ConvertingStatus.PROCESSING;

        if(getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            this.mConvertingStatus = ConvertingStatus.FAILED_OR_EMPTY;
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
        final String FLIGHT_IATA_TO = "iata_to";
        final String FLIGHT_DURATION = "duration";
        final String FLIGHT_DEP_TIME = "dep_time";
        final String FLIGHT_ARR_TIME = "arr_time";

        try {

            JSONObject jsonData = new JSONObject(getmData());
            JSONArray itemsArray = jsonData.getJSONArray(FLIGHTS);
            for(int i=0; i<itemsArray.length(); i++) {

                JSONObject jsonFlight = itemsArray.getJSONObject(i);
                String id = jsonFlight.getString(FLIGHT_ID);
                int no = jsonFlight.getInt(FLIGHT_NO);
                String date = jsonFlight.getString(FLIGHT_DATE);
                long price_e = jsonFlight.getLong(FLIGHT_PRICE_E);
                long price_b = jsonFlight.getLong(FLIGHT_PRICE_B);
                long price_f = jsonFlight.getLong(FLIGHT_PRICE_F);
                String curr = jsonFlight.getString(FLIGHT_CURR);
                String iata_from = jsonFlight.getString(FLIGHT_IATA_FROM);
                String iata_to = jsonFlight.getString(FLIGHT_IATA_TO);
                long duration = jsonFlight.getLong(FLIGHT_DURATION);
                String dep_time = jsonFlight.getString(FLIGHT_DEP_TIME);
                String arr_time = jsonFlight.getString(FLIGHT_ARR_TIME);

                Flight FlightObject = new Flight(id, no, date, price_e, price_b, price_f, curr, iata_from, iata_to, duration, dep_time, arr_time);

                this.mFlights.add(FlightObject);
            }

            for(Flight singleFlight: mFlights) {
                Log.v(LOG_TAG, singleFlight.toString());
            }

        } catch(JSONException jsone) {
            jsone.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json data");
            this.mConvertingStatus = ConvertingStatus.FAILED_OR_EMPTY;
        }

        this.mConvertingStatus = ConvertingStatus.OK;

    }



    public class DownloadJsonData extends DownloadRawData {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();


        }

        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }


    }*/

//}
