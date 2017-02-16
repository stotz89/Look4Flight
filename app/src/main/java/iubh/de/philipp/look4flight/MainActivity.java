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
    private MultiAutoCompleteTextView MultiAutoCompleteOrigin;
    private MultiAutoCompleteTextView MultiAutoCompleteDestination;
    //private MultiAutoCompleteTextView MultiAuto;
    private ArrayList<String> AirlineList = new ArrayList<String>();

    // Alles was zur Datumsauswahl gehört....
    private EditText fromDateEditText;
    private EditText toDateEditText;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatterDB;
    private View.OnClickListener OnClickListener = null;

    //Buttons
    private Button search;
    private Button gps;

    //Location
    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double mLatitude;
    private Double mLongtitude;

    //Sonstiges
    private Context context;

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
        context = getApplicationContext();
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        dateFormatterDB = new SimpleDateFormat("yyyy-MM-dd");

    }


    private void initializeOnClick() {

        OnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dateFrom = fromDateEditText.getText().toString();
                String dateTo = toDateEditText.getText().toString();
                String origin = MultiAutoCompleteOrigin.getText().toString();
                String destination = MultiAutoCompleteDestination.getText().toString();


                if (view == fromDateEditText) {
                    fromDatePickerDialog.show();
                } else if (view == toDateEditText) {
                    toDatePickerDialog.show();
                } else if (view == gps) {

/*                    getAirportByGPS();

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);*/


                } else if (view == search) {

                    if (origin.isEmpty() || destination.isEmpty()) {
                        Toast.makeText(context, "Bitte Flughäfen eingeben", Toast.LENGTH_SHORT).show();
                        //Log.e(LOG_TAG, "Flughäfen nicht eingegeben");
                    } else {

                        // Get Values of field input
                        /*origin = origin.substring(0, 3);
                        destination = destination.substring(0, 3);*/

                        try {
                            if (!dateFrom.isEmpty()) {
                                dateFrom = dateFormatterDB.format(dateFormatter.parse(dateFrom));
                            }

                            if (!dateTo.isEmpty()) {
                                dateTo = dateFormatterDB.format(dateFormatter.parse(dateTo));
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

        MultiAutoCompleteDestination.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        MultiAutoCompleteOrigin.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, AirlineList);

        MultiAutoCompleteOrigin.setAdapter(adapter);
        MultiAutoCompleteOrigin.setThreshold(2);
        MultiAutoCompleteDestination.setAdapter(adapter);
        MultiAutoCompleteDestination.setThreshold(2);

        //MultiAuto.setAdapter(adapter);
        //MultiAuto.setThreshold(2);


    }


    private void findViewsById() {

        MultiAutoCompleteOrigin = (MultiAutoCompleteTextView) findViewById(R.id.etxt_autoComplete_origin);
        MultiAutoCompleteDestination = (MultiAutoCompleteTextView) findViewById(R.id.etxt_autoComplete_destination);

        //MultiAuto = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);

        fromDateEditText = (EditText) findViewById(R.id.etxt_fromdate);
        fromDateEditText.setInputType(InputType.TYPE_NULL);
        fromDateEditText.requestFocus();

        toDateEditText = (EditText) findViewById(R.id.etxt_todate);
        toDateEditText.setInputType(InputType.TYPE_NULL);

        search = (Button) findViewById(R.id.start_search);
        gps = (Button) findViewById(R.id.gps);
        search.setOnClickListener(OnClickListener);
        gps.setOnClickListener(OnClickListener);


    }

    ;


    private void setDateTimeField() {
        fromDateEditText.setOnClickListener(OnClickListener);
        toDateEditText.setOnClickListener(OnClickListener);

        Calendar newCalendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.e("FROM", Integer.toString(year) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(dayOfMonth));
                fromDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.e("TO ", Integer.toString(year) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(dayOfMonth));
                toDateEditText.setText(dateFormatter.format(newDate.getTime()));
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
