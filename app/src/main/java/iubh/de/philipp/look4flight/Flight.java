package iubh.de.philipp.look4flight;

/**
 * Created by philipp on 25.01.17.
 */

public class Flight {



    private String mID;
    private Integer mNO;
    private String mDate;
    private long mPriceE;
    private long mPriceB;
    private long mPriceF;
    private String mCurr;
    private String mIataFrom;
    private String mCityFrom;
    private String mIataTo;
    private String mCityTo;
    private long mDuration;
    private String mDepTime;
    private String mArrTime;

    public Flight(String mID, Integer mNO, String mDate, long mPriceE, long mPriceB, long mPriceF, String mCurr, String mIataFrom, String mCityFrom, String mIataTo, String mCityTo, long mDuration, String mDepTime, String mArrTime) {
        this.mID = mID;
        this.mNO = mNO;
        this.mDate = mDate;
        this.mPriceE = mPriceE;
        this.mPriceB = mPriceB;
        this.mPriceF = mPriceF;
        this.mCurr = mCurr;
        this.mIataFrom = mIataFrom;
        this.mCityFrom = mCityFrom;
        this.mIataTo = mIataTo;
        this.mCityTo = mCityTo;
        this.mDuration = mDuration;
        this.mDepTime = mDepTime;
        this.mArrTime = mArrTime;
    }

    public String getmID() {
        return mID;
    }

    public Integer getmNO() {
        return mNO;
    }

    public String getmDate() {
        return mDate;
    }

    public long getmPriceE() {
        return mPriceE;
    }

    public long getmPriceB() {
        return mPriceB;
    }

    public long getmPriceF() {
        return mPriceF;
    }

    public String getmCurr() {
        return mCurr;
    }

    public String getmIataFrom() {
        return mIataFrom;

    }

    public String getmIataTo() {
        return mIataTo;
    }

    public long getmDuration() {
        return mDuration;
    }

    public String getmDepTime() {
        return mDepTime;
    }

    public String getmArrTime() {
        return mArrTime;
    }

    public String getmCityFrom() {
        return mCityFrom;
    }

    public String getmCityTo() {
        return mCityTo;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "mID='" + mID + '\'' +
                ", mNO=" + mNO +
                ", mDate='" + mDate + '\'' +
                ", mPriceE=" + mPriceE +
                ", mPriceB=" + mPriceB +
                ", mPriceF=" + mPriceF +
                ", mCurr='" + mCurr + '\'' +
                ", mIataFrom='" + mIataFrom + '\'' +
                ", mCityFrom='" + mCityFrom + '\'' +
                ", mIataTo='" + mIataTo + '\'' +
                ", mCityTo='" + mCityTo + '\'' +
                ", mDuration=" + mDuration +
                ", mDepTime='" + mDepTime + '\'' +
                ", mArrTime='" + mArrTime + '\'' +
                '}';
    }
}