package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 13.02.17.
 */

public class Roundtrip {

    private static final String LOG_TAG = Roundtrip.class.getSimpleName();
    private Flight mFlightTo;
    private Flight mFlightBack;

    public Roundtrip(Flight mFlightTo, Flight mFlightBack) {
        this.mFlightTo = mFlightTo;
        this.mFlightBack = mFlightBack;
    }

    public Flight getmFlightTo() {
        return mFlightTo;
    }

    public Flight getmFlightBack() {
        return mFlightBack;
    }

    @Override
    public String toString() {
        return "Roundtrip{" +
                "mFlightTo=" + mFlightTo.toString() +
                ", mFlightBack=" + mFlightBack.toString() +
                '}';
    }
}
