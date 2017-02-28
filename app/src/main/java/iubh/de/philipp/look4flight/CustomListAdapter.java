package iubh.de.philipp.look4flight;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by philipp on 27.01.17.
 */

public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Roundtrip> mRoundtrip;
    private LayoutInflater mLayoutInflater;
    private boolean mRoundtripBool;
    private int mPersons;

    public CustomListAdapter(Context aContext, ArrayList<Roundtrip> roundtrip, boolean roundtripBool, int persons) {
        this.mRoundtrip = roundtrip;
        this.mRoundtripBool = roundtripBool;
        this.mPersons = persons;
        mLayoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        Log.e("GETCOUNT", "Es wurde getCount aufgerufen.");
        return mRoundtrip.size();
    }

    @Override
    public Object getItem(int position) {
        Log.e("GETITEM", "Es wurde getItem aufgerufen.");
        return mRoundtrip.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        long priceTo = 0;
        long priceBack = 0;

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_row_layout_grid, null);
            holder = new ViewHolder();
            holder.OriginView = (TextView) convertView.findViewById(R.id.list_origin);
            holder.DestinationView = (TextView) convertView.findViewById(R.id.list_destination);
            holder.DateView = (TextView) convertView.findViewById(R.id.list_date_from);
            holder.DepTimeView = (TextView) convertView.findViewById(R.id.list_dep_time);
            holder.ArrTimeView = (TextView) convertView.findViewById(R.id.list_arr_time);
            holder.Price = (TextView) convertView.findViewById(R.id.list_price);
            holder.StopsTo = (TextView) convertView.findViewById(R.id.list_stops_to);

            //holder = new ViewHolder();
            holder.OriginViewBack = (TextView) convertView.findViewById(R.id.list_origin_2);
            holder.DestinationViewBack = (TextView) convertView.findViewById(R.id.list_destination_2);
            holder.DateViewBack = (TextView) convertView.findViewById(R.id.list_date_from_2);
            holder.DepTimeViewBack = (TextView) convertView.findViewById(R.id.list_dep_time_2);
            holder.ArrTimeViewBack = (TextView) convertView.findViewById(R.id.list_arr_time_2);
            holder.StopsBack = (TextView) convertView.findViewById(R.id.list_stops_back);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Loop über Array der MultiFlights (beinhaltet sowohl NonStop als auch MultiStop Flüge)
        for (int iHin = 0; iHin < mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().size(); iHin++) {
            //Origin und DepTime werden nur beim ersten Durchgang gesetzt.

            if (iHin == 0) {
                holder.OriginView.setText(mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmCityFrom());
                holder.DepTimeView.setText(mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmDepTime());
            }

            // Diese Daten werden von dem eventuellen zweiten Flug überschrieben
            holder.DestinationView.setText(mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmCityTo());
            holder.DateView.setText(mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmDate());
            holder.ArrTimeView.setText(mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmArrTime());
            holder.StopsTo.setText(Integer.toString(iHin));
            priceTo += mRoundtrip.get(position).getmTripTo().getmMultiStopFlight().get(iHin).getmPriceE();
        }


        if (mRoundtripBool) {
            for (int iRueck = 0; iRueck < mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().size(); iRueck++) {
                if (iRueck == 0) {
                    holder.OriginViewBack.setText(mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmCityFrom());
                    holder.DepTimeViewBack.setText(mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmDepTime());
                }

                holder.DestinationViewBack.setText(mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmCityTo());
                holder.DateViewBack.setText(mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmDate());
                holder.ArrTimeViewBack.setText(mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmArrTime());
                holder.StopsBack.setText(Integer.toString(iRueck));
                priceBack += mRoundtrip.get(position).getmTripBack().getmMultiStopFlight().get(iRueck).getmPriceE();
            }

            holder.Price.setText(Long.toString( (priceTo + priceBack ) * mPersons ));
        } else {
            holder.Price.setText(Long.toString(priceTo * mPersons));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView OriginView;
        TextView DestinationView;
        TextView DateView;
        TextView DepTimeView;
        TextView ArrTimeView;
        TextView Price;
        TextView StopsTo;

        TextView OriginViewBack;
        TextView DestinationViewBack;
        TextView DateViewBack;
        TextView DepTimeViewBack;
        TextView ArrTimeViewBack;
        TextView StopsBack;

    }


}
