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
    private LayoutInflater layoutInflater;
    private int position;

    public CustomListAdapter(Context aContext, ArrayList<Roundtrip> roundtrip) {
        this.mRoundtrip = roundtrip;
        layoutInflater = LayoutInflater.from(aContext);
    }

    /*@Override
    public int getCountTo() {
        return flightTo.size();
    }

    public int getCountBack() {
        return flightBack.size();
    }

    @Override
    public Object getItemTo(int position) {
        return flightTo.get(position);
    }

    public Object getItemBack(int position) {
        return flightBack.get(position);
    }*/

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
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout_grid, null);
            holder = new ViewHolder();
            holder.OriginView = (TextView) convertView.findViewById(R.id.list_origin);
            holder.DestinationView = (TextView) convertView.findViewById(R.id.list_destination);
            holder.DateView = (TextView) convertView.findViewById(R.id.list_date_from);
            holder.DepTimeView = (TextView) convertView.findViewById(R.id.list_dep_time);
            holder.ArrTimeView = (TextView) convertView.findViewById(R.id.list_arr_time);
            holder.Price = (TextView) convertView.findViewById(R.id.list_price);

            //holder = new ViewHolder();
            holder.OriginViewBack = (TextView) convertView.findViewById(R.id.list_origin_2);
            holder.DestinationViewBack = (TextView) convertView.findViewById(R.id.list_destination_2);
            holder.DateViewBack = (TextView) convertView.findViewById(R.id.list_date_from_2);
            holder.DepTimeViewBack = (TextView) convertView.findViewById(R.id.list_dep_time_2);
            holder.ArrTimeViewBack = (TextView) convertView.findViewById(R.id.list_arr_time_2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.OriginView.setText(mRoundtrip.get(position).getmFlightTo().getmCityFrom());
        holder.DestinationView.setText(mRoundtrip.get(position).getmFlightTo().getmCityTo());
        holder.DateView.setText(mRoundtrip.get(position).getmFlightTo().getmDate());
        holder.DepTimeView.setText(mRoundtrip.get(position).getmFlightTo().getmDepTime());
        holder.ArrTimeView.setText(mRoundtrip.get(position).getmFlightTo().getmArrTime());


        //Log .e("Price", "Preis: " + Long.toString(listData.get(position).getmPriceE()));

        holder.OriginViewBack.setText(mRoundtrip.get(position).getmFlightBack().getmCityFrom());
        holder.DestinationViewBack.setText(mRoundtrip.get(position).getmFlightBack().getmCityTo());
        holder.DateViewBack.setText(mRoundtrip.get(position).getmFlightBack().getmDate());
        holder.DepTimeViewBack.setText(mRoundtrip.get(position).getmFlightBack().getmDepTime());
        holder.ArrTimeViewBack.setText(mRoundtrip.get(position).getmFlightBack().getmArrTime());

        holder.Price.setText(Long.toString(mRoundtrip.get(position).getmFlightTo().getmPriceE() + mRoundtrip.get(position).getmFlightBack().getmPriceE()));
        return convertView;
    }

    static class ViewHolder {
        TextView OriginView;
        TextView DestinationView;
        TextView DateView;
        TextView DepTimeView;
        TextView ArrTimeView;
        TextView Price;

        TextView OriginViewBack;
        TextView DestinationViewBack;
        TextView DateViewBack;
        TextView DepTimeViewBack;
        TextView ArrTimeViewBack;

    }


}
