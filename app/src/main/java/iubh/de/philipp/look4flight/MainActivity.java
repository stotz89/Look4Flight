package iubh.de.philipp.look4flight;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private AutoCompleteTextView AutoCompleteOrigin;
    private AutoCompleteTextView AutoCompleteDestination;
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

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        dateFormatterDB = new SimpleDateFormat("yyyy-MM-dd");

    }


    private void initializeOnClick() {

        OnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dateFrom = fromDateEditText.getText().toString();
                String dateTo = toDateEditText.getText().toString();
                String origin = AutoCompleteOrigin.getText().toString();
                String destination = AutoCompleteDestination.getText().toString();


                if (view == fromDateEditText) {
                    fromDatePickerDialog.show();
                } else if (view == toDateEditText) {
                    toDatePickerDialog.show();
                } else if (view == search) {

                    if (origin.isEmpty() || destination.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Bitte Flughäfen eingeben", Toast.LENGTH_LONG);
                    } else {

                        // Get Values of field input
                        origin = origin.substring(0, 3);
                        destination = destination.substring(0, 3);

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
                String airlineCountry = airlines.get("COUNTRY");

                AirlineList.add(index, airlineIATA + " / " + airlineName);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.e("Array", Integer.toString(AirlineList.size()));
        //Log.e("Array", AirlineList.get(0));

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, AirlineList);

        AutoCompleteOrigin.setAdapter(adapter);
        AutoCompleteOrigin.setThreshold(2);
        AutoCompleteDestination.setAdapter(adapter);
        AutoCompleteDestination.setThreshold(2);


    }


    private void findViewsById() {

        AutoCompleteOrigin = (AutoCompleteTextView) findViewById(R.id.etxt_autoComplete_origin);
        AutoCompleteDestination = (AutoCompleteTextView) findViewById(R.id.etxt_autoComplete_destination);

        fromDateEditText = (EditText) findViewById(R.id.etxt_fromdate);
        fromDateEditText.setInputType(InputType.TYPE_NULL);
        fromDateEditText.requestFocus();

        toDateEditText = (EditText) findViewById(R.id.etxt_todate);
        toDateEditText.setInputType(InputType.TYPE_NULL);

        search = (Button) findViewById(R.id.start_search);
        search.setOnClickListener(OnClickListener);


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

    ;


}
