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

    private ArrayList<Flight> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<Flight> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
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
            holder.OriginView_2 = (TextView) convertView.findViewById(R.id.list_origin_2);
            holder.DestinationView_2 = (TextView) convertView.findViewById(R.id.list_destination_2);
            holder.DateView_2 = (TextView) convertView.findViewById(R.id.list_date_from_2);
            holder.DepTimeView_2 = (TextView) convertView.findViewById(R.id.list_dep_time_2);
            holder.ArrTimeView_2 = (TextView) convertView.findViewById(R.id.list_arr_time_2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.OriginView.setText(listData.get(position).getmCityFrom());
        holder.DestinationView.setText(listData.get(position).getmCityTo());
        holder.DateView.setText(listData.get(position).getmDate());
        holder.DepTimeView.setText(listData.get(position).getmDepTime());
        holder.ArrTimeView.setText(listData.get(position).getmArrTime());
        holder.Price.setText(Long.toString(listData.get(position).getmPriceE()));

        Log .e("Price", "Preis: " + Long.toString(listData.get(position).getmPriceE()));


        holder.OriginView_2.setText(listData.get(position).getmCityFrom());
        holder.DestinationView_2.setText(listData.get(position).getmCityTo());
        holder.DateView_2.setText(listData.get(position).getmDate());
        holder.DepTimeView_2.setText(listData.get(position).getmDepTime());
        holder.ArrTimeView_2.setText(listData.get(position).getmArrTime());
        return convertView;
    }

    static class ViewHolder {
        TextView OriginView;
        TextView DestinationView;
        TextView DateView;
        TextView DepTimeView;
        TextView ArrTimeView;
        TextView Price;

        TextView OriginView_2;
        TextView DestinationView_2;
        TextView DateView_2;
        TextView DepTimeView_2;
        TextView ArrTimeView_2;

    }


}
