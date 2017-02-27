package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 13.02.17.
 */

public class Roundtrip {

    private static final String LOG_TAG = Roundtrip.class.getSimpleName();
    private Trip mTripTo;
    private Trip mTripBack;


    public Roundtrip(Trip flightTo) {
        this.mTripTo = flightTo;

    }

    public Roundtrip(Trip tripTo, Trip tripBack) {

        this.mTripTo = tripTo;
        this.mTripBack = tripBack;

    }

    public Trip getmTripBack() {
        return mTripBack;
    }

    public Trip getmTripTo() {
        return mTripTo;
    }


}
