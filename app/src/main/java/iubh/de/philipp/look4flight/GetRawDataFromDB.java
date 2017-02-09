package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 25.01.17.
 */



/*
public class GetRawDataFromDB {

    private String LOG_TAG = GetRawDataFromDB.class.getSimpleName();
    private String mRawURL;
    private String mData;
    private DownloadStatus mDownloadStatus;


    public GetRawDataFromDB(String mRawURL) {
        Log.e(LOG_TAG, "Constructor");

        this.mRawURL = mRawURL;
        this.mDownloadStatus = DownloadStatus.IDLE;

    }

    public void reset() {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mRawURL = null;
        this.mData = null;
    }

    public String getmData() {
        return mData;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public void setmRawUrl(String mRawUrl) {
        this.mRawURL = mRawUrl;
    }

    public void execute() {

        Log.e(LOG_TAG, "Start - execute");
        this.mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        try {
            String waitfor = downloadRawData.execute(mRawURL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    public class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        protected void onPostExecute(String webData) {

            mData = webData;
            Log.v(LOG_TAG, "Data returned was: " +mData);
            if(mData == null) {
                if(mRawURL == null) {
                    mDownloadStatus = DownloadStatus.NOT_INITIALISED;
                } else {
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
            } else {
                // Success
                mDownloadStatus = DownloadStatus.OK;
            }
            Log.v(LOG_TAG, "Downloadstatus: " +mDownloadStatus.toString());
        }

        protected String doInBackground(String... params) {

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
    }

}*/
