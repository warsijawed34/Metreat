package com.metreat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.metreat.R;
import com.metreat.model.EventTypeListModel;
import java.util.ArrayList;

public class EventTypeListAdapter extends BaseAdapter {

    Context mContext;
    EventTypeListModel model;
    LayoutInflater inflater;
    ArrayList<EventTypeListModel> eventTypeListModelArrayList;

    public EventTypeListAdapter(Context mContext){
        this.mContext = mContext;
        eventTypeListModelArrayList = new ArrayList<>();
    }
    public void clear(){
        eventTypeListModelArrayList.clear();
    }

    public void addToArrayList(EventTypeListModel model){
        eventTypeListModelArrayList.add(model);
    }

    @Override
    public EventTypeListModel getItem(int position) {
        return eventTypeListModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return eventTypeListModelArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.listitem_eventlist, parent, false);
        holder.tvEventList = (TextView) convertView.findViewById(R.id.tv_eventList);

        if(eventTypeListModelArrayList.size()>0){
             model = eventTypeListModelArrayList.get(position);
              holder.tvEventList.setText(model.getTittle());
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvEventList;
    }
}
