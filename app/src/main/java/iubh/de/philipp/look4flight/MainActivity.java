package iubh.de.philipp.look4flight;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Alles zur Abflug/Zielflughafen
    private MultiAutoCompleteTextView mMultiAutoCompleteOrigin;
    private MultiAutoCompleteTextView mMultiAutoCompleteDestination;
    //private MultiAutoCompleteTextView MultiAuto;
    private ArrayList<String> AirlineList = new ArrayList<String>();

    // Alles was zur Datumsauswahl gehört....
    private EditText mFromDateEditText;
    private EditText mToDateEditText;
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private SimpleDateFormat mDateFormatter;
    private SimpleDateFormat mDateFormatterDB;
    private View.OnClickListener mOnClickListener = null;

    //Sonstiges
    private Switch mSwRoundtrip;
    private Switch mSwNonStop;

    //Buttons
    private Button mBtnSearch;
    private Button mBtnGps;

    //Location
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Double mLatitude;
    private Double mLongtitude;

    //Sonstiges
    private Context mContext;

    //Progress Dialog
    //private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeOnClick();

        findViewsById();
        setDateTimeField();

        initializeAutoCompletion();
        mContext = getApplicationContext();
        mDateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        mDateFormatterDB = new SimpleDateFormat("yyyy-MM-dd");

    }


    private void initializeOnClick() {

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dateFrom = mFromDateEditText.getText().toString();
                String dateTo = mToDateEditText.getText().toString();
                String origin = mMultiAutoCompleteOrigin.getText().toString();
                String destination = mMultiAutoCompleteDestination.getText().toString();


                if (view == mFromDateEditText) {
                    mFromDatePickerDialog.show();
                } else if (view == mToDateEditText) {
                    mToDatePickerDialog.show();
                } else if (view == mBtnGps) {

/*                    getAirportByGPS();

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);*/


                } else if (view == mBtnSearch) {

                    if (origin.isEmpty() || destination.isEmpty()) {
                        Toast.makeText(mContext, "Bitte Flughäfen eingeben", Toast.LENGTH_SHORT).show();
                        return;
                        //Log.e(LOG_TAG, "Flughäfen nicht eingegeben");
                    } else {

                        try {
                            if (!dateFrom.isEmpty()) {
                                dateFrom = mDateFormatterDB.format(mDateFormatter.parse(dateFrom));
                            } else {
                                Toast.makeText(mContext, "Bitte Abflugdatum eingeben", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!dateTo.isEmpty()) {
                                dateTo = mDateFormatterDB.format(mDateFormatter.parse(dateTo));
                                if ( dateTo.compareTo(dateFrom) < 0 ) {
                                    Toast.makeText(mContext, "Bitte Rückflugdatum größer dem Hinflugdatum wählen.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Pass value to new activity

                        Intent flightActivity = new Intent(MainActivity.this, FlightsActivity.class);
                        flightActivity.putExtra("origin", origin);
                        flightActivity.putExtra("destination", destination);
                        flightActivity.putExtra("dateFrom", dateFrom);
                        flightActivity.putExtra("dateTo", dateTo);
                        flightActivity.putExtra("Roundtrip", mSwRoundtrip.isChecked());
                        flightActivity.putExtra("NonStop", mSwNonStop.isChecked());

                        startActivity(flightActivity);
                    }


                }
            }
        };

    }

    private void initializeAutoCompletion() {

        int index = 0;

        InputStream is = getResources().openRawResource(R.raw.airports_full);
        InputStreamReader csvISR = new InputStreamReader(is);

        CsvReader airlines = new CsvReader(csvISR);

        try {
            airlines.readHeaders();

            while (airlines.readRecord()) {
                String airlineID = airlines.get("ID");
                String airlineName = airlines.get("NAME");
                String airlineIATA = airlines.get("IATA");
                String airlineCity = airlines.get("CITY");
                String airlineCountry = airlines.get("COUNTRY");

                AirlineList.add(index, airlineIATA + " / " + airlineName + " / " + airlineCity);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.e("Array", Integer.toString(AirlineList.size()));
        //Log.e("Array", AirlineList.get(0));

        mMultiAutoCompleteDestination.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiAutoCompleteOrigin.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, AirlineList);

        mMultiAutoCompleteOrigin.setAdapter(adapter);
        mMultiAutoCompleteOrigin.setThreshold(2);
        mMultiAutoCompleteDestination.setAdapter(adapter);
        mMultiAutoCompleteDestination.setThreshold(2);

        //MultiAuto.setAdapter(adapter);
        //MultiAuto.setThreshold(2);


    }


    private void findViewsById() {

        mMultiAutoCompleteOrigin = (MultiAutoCompleteTextView) findViewById(R.id.etxt_autoComplete_origin);
        mMultiAutoCompleteDestination = (MultiAutoCompleteTextView) findViewById(R.id.etxt_autoComplete_destination);

        //MultiAuto = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);

        mFromDateEditText = (EditText) findViewById(R.id.etxt_fromdate);
        mFromDateEditText.setInputType(InputType.TYPE_NULL);
        mFromDateEditText.requestFocus();

        mToDateEditText = (EditText) findViewById(R.id.etxt_todate);
        mToDateEditText.setInputType(InputType.TYPE_NULL);

        mBtnSearch = (Button) findViewById(R.id.start_search);
        mBtnGps = (Button) findViewById(R.id.gps);
        mBtnSearch.setOnClickListener(mOnClickListener);
        mBtnGps.setOnClickListener(mOnClickListener);

        mSwNonStop = (Switch) findViewById(R.id.nonstop);
        mSwRoundtrip = (Switch) findViewById(R.id.roundtrip);

    }

    ;


    private void setDateTimeField() {
        mFromDateEditText.setOnClickListener(mOnClickListener);
        mToDateEditText.setOnClickListener(mOnClickListener);

        Calendar newCalendar = Calendar.getInstance();

        mFromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.e("FROM", Integer.toString(year) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(dayOfMonth));
                mFromDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        mToDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.e("TO ", Integer.toString(year) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(dayOfMonth));
                mToDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    /*private void getAirportByGPS() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLatitude = location.getLatitude();
                mLongtitude = location.getLongitude();
                Log.e(LOG_TAG, "Latitude: " + Double.toString(mLatitude) + "/ Longitude: " + Double.toString(mLongtitude));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e(LOG_TAG, "GPS is disabled");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10 );
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                return;
                }
        }
    }*/
}
