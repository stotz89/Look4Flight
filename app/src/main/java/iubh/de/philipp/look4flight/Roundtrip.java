package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 13.02.17.
 */

public class Roundtrip {

    private static final String LOG_TAG = Roundtrip.class.getSimpleName();
    private MultiStopFlight mMultiStopFlightTo;
    private MultiStopFlight mMultiStopFlightBack;


    public Roundtrip(MultiStopFlight flightTo) {
        this.mMultiStopFlightTo = flightTo;

    }

    public Roundtrip(MultiStopFlight multiStopFlightTo, MultiStopFlight multiStopFlightBack) {

        this.mMultiStopFlightTo = multiStopFlightTo;
        this.mMultiStopFlightBack = multiStopFlightBack;

    }

    public MultiStopFlight getmMultiStopFlightBack() {
        return mMultiStopFlightBack;
    }

    public MultiStopFlight getmMultiStopFlightTo() {
        return mMultiStopFlightTo;
    }


}
