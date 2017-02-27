package iubh.de.philipp.look4flight;

import java.util.ArrayList;

/**
 * Created by philipp on 27.02.17.
 */

public class Trip {
    private static final String LOG_TAG = Trip.class.getSimpleName();

    private ArrayList<Flight> mMultiStopFlight;

    public Trip() {

        mMultiStopFlight = new ArrayList<Flight>();

    }

    public void addFlight(Flight flight) {
        mMultiStopFlight.add(flight);
    }

    public ArrayList<Flight> getmMultiStopFlight() {
        return mMultiStopFlight;
    }

    @Override
    public String toString() {
        // Needs to be implemented
        String response = null;

        for (int i = 0; i < mMultiStopFlight.size(); i++) {

            response = response + "Flight: " + Integer.toString(i) + mMultiStopFlight.get(i).toString();

        }

        return response;
    }
}
