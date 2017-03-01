package iubh.de.philipp.look4flight;

import java.util.ArrayList;

/**
 * Created by philipp on 27.02.17.
 */

public class Trip {

    private ArrayList<Flight> mTrips;

    public Trip() {

        mTrips = new ArrayList<Flight>();

    }

    public void addFlight(Flight flight) {
        mTrips.add(flight);
    }

    public ArrayList<Flight> getmTrips() {
        return mTrips;
    }

    @Override
    public String toString() {
        // Needs to be implemented
        String response = null;

        for (int i = 0; i < mTrips.size(); i++) {
            if (response.isEmpty()) {
                response = "Flight: " + Integer.toString(i) + mTrips.get(i).toString();
            } else {
                response = response + "Flight: " + Integer.toString(i) + mTrips.get(i).toString();
            }
        }

        return response;
    }
}
