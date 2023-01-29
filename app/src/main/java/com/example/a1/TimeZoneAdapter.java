package com.example.a1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TimeZoneAdapter extends ArrayAdapter<CityTimeZone> implements Filterable {
    private List<CityTimeZone> cityTimeZoneList;
    private List<CityTimeZone> filteredTimeZoneList;
    private Context context;
    private LayoutInflater inflater;
    private CityTimeZoneFilter filter;

    public TimeZoneAdapter(List<CityTimeZone> cityTimeZoneList, Context context) {
        super(context, R.layout.adapter_view_layout, cityTimeZoneList);
        this.cityTimeZoneList = cityTimeZoneList;
        this.filteredTimeZoneList = cityTimeZoneList;
        this.context = context;
    }

    public CityTimeZone getItem(int position) {
        return filteredTimeZoneList.get(position);
    }

    @Override
    public int getCount() {
        return filteredTimeZoneList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CityTimeZoneFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CityTimeZoneHolder holder = new CityTimeZoneHolder();

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_view_layout, null);

            holder.cityName = v.findViewById(R.id.city_text_view);
            holder.cityTime = v.findViewById(R.id.time_text_view);


            v.setTag(holder);
        } else {
            holder = (CityTimeZoneHolder) v.getTag();
        }

        CityTimeZone cityTimeZone = filteredTimeZoneList.get(position);
        holder.cityName.setText(cityTimeZone.getName());
        holder.cityTime.setText(Helper.convertTimeZoneToTime(cityTimeZone.getName()));


        return v;
    }

    private class CityTimeZoneFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<CityTimeZone> filteredList = new ArrayList<>();
                for (int i = 0; i < cityTimeZoneList.size(); i++) {
                    if (cityTimeZoneList.get(i).getName().contains(constraint.toString())) {
                        filteredList.add(cityTimeZoneList.get(i));
                    }
                }
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            } else {
                filterResults.count = cityTimeZoneList.size();
                filterResults.values = cityTimeZoneList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTimeZoneList = (ArrayList<CityTimeZone>) results.values;
            notifyDataSetChanged();
        }
    }

    private static class CityTimeZoneHolder {
        public TextView cityName;
        public TextView cityTime;
        public CheckBox checkBox;
    }
}
