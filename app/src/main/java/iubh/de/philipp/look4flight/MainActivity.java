package iubh.de.philipp.look4flight;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Alles zur Abflug/Zielflughafen
    private MultiAutoCompleteTextView mMultiAutoCompleteOrigin;
    private MultiAutoCompleteTextView mMultiAutoCompleteDestination;
    //private MultiAutoCompleteTextView MultiAuto;
    private ArrayList<String> mAirlineList = new ArrayList<String>();

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
    private Spinner mPersons;

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
    private String mProvider;

    //Progress Dialog
    //private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeOnClick();

        findViewsById();
        setDateTimeField();

        initializeMultiAutoCompletion();
        initializeSpinner();
        mContext = getApplicationContext();
        mDateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        mDateFormatterDB = new SimpleDateFormat("yyyy-MM-dd");
        //mLocationManager = getSystemService(LocationManager.class);

    }

    private void initializeSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.zaehler, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mPersons.setAdapter(adapter);

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

                    getAirportByGPS();

                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);


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
                                if (dateTo.compareTo(dateFrom) < 0) {
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
                        flightActivity.putExtra("Persons", mPersons.getSelectedItem().toString());

                        startActivity(flightActivity);
                    }


                }
            }
        };

    }

    private void initializeMultiAutoCompletion() {

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

                mAirlineList.add(index, airlineIATA + " / " + airlineName + " / " + airlineCity);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.e("Array", Integer.toString(mAirlineList.size()));
        //Log.e("Array", mAirlineList.get(0));

        mMultiAutoCompleteDestination.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiAutoCompleteOrigin.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mAirlineList);

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

        mPersons = (Spinner) findViewById(R.id.persons);

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


    private void getAirportByGPS() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }else {
            doIt();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doIt();
                }
        }
    }

    private void doIt() {
        Log.e(LOG_TAG, "Method: DoIt()");
        Location location = null;
        //mLocationManager = getSystemService(LocationManager.class);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getAllProviders();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        mProvider = mLocationManager.getBestProvider(criteria, true);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(LOG_TAG, "Location changed");
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

        try {
            mLocationManager.requestLocationUpdates(mProvider, 3000, 0, mLocationListener);
            location = mLocationManager.getLastKnownLocation(mProvider);
        } catch (SecurityException se) {
            Toast.makeText(mContext, "Keine Berechtigung das GPS zu verwenden", Toast.LENGTH_SHORT).show();
        }
        mLatitude = location.getLatitude();
        mLongtitude = location.getLongitude();
        Log.e(LOG_TAG, "Initial - Latitude: " + Double.toString(mLatitude) + "/ Longitude: " + Double.toString(mLongtitude));
        String setText = getclosestairport(mLatitude, mLongtitude);
        mMultiAutoCompleteOrigin.setText(setText);
    }

    private String getclosestairport(Double latitude, Double longtitude) {


        Double nearestTemp;
        Double nearest = 200.0;
        Double airportLat = 0.0;
        Double airportLon = 0.0;

        String airportIATA = "";
        String airportName = "";
        String airportCity = "";
        String airport     = "No airport found";

        InputStream is = getResources().openRawResource(R.raw.airports_full);
        InputStreamReader csvISR = new InputStreamReader(is);

        CsvReader airports = new CsvReader(csvISR);

        try {
            airports.readHeaders();

            while (airports.readRecord()) {

                String tempLat = airports.get("LATITUDE");
                String tempLon = airports.get("LONGTITUDE");

                if (!(tempLat.isEmpty() && tempLon.isEmpty())) {
                    airportLat = Double.valueOf(tempLat).doubleValue();
                    airportLon = Double.valueOf(tempLon).doubleValue();
                } else {
                    continue;
                }


                //Get closest Latitude
                //Nähe berechnen
                nearestTemp = calculateNearness(airportLat, latitude) + calculateNearness(airportLon, longtitude);
                if (nearestTemp < nearest) {
                    nearest = nearestTemp;
                    airportName = airports.get("NAME");
                    airportIATA = airports.get("IATA");
                    airportCity = airports.get("CITY");
                }


            }

            airport = airportIATA + " / " + airportName + " / " + airportCity;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return airport;
    }

    private Double calculateNearness(Double airport, Double currLoc) {

        Double nearness = 0.0;

        if (airport > 0 && currLoc > 0) {
            if (airport >= currLoc) {
                nearness = airport - currLoc;
            } else if (airport < currLoc) {
                nearness = currLoc - airport;
            }
        } else if (airport < 0 && currLoc < 0) {
            if (airport >= currLoc) {
                nearness = airport - currLoc;
            } else if (airport < currLoc) {
                nearness = currLoc - airport;
            }
        } else if (airport > 0 && currLoc < 0) {
            currLoc = currLoc * (-1);
            if (airport >= currLoc) {
                nearness = airport - currLoc;
            } else if (airport < currLoc) {
                nearness = currLoc - airport;
            }
        } else if (airport < 0 && currLoc > 0) {
            airport = airport * (-1);
            if (airport >= currLoc) {
                nearness = airport - currLoc;
            } else if (airport < currLoc) {
                nearness = currLoc - airport;
            }
        } else if (airport == 0) {
            if (currLoc < 0) {
                currLoc = currLoc * (-1);
            }
            nearness = currLoc;
        } else if (currLoc == 0) {
            if (airport < 0) {
                airport = airport * (-1);
            }
            nearness = airport;
        }

        return nearness;
    }

}
