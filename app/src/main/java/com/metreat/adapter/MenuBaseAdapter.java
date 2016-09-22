package com.metreat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.metreat.R;

public class MenuBaseAdapter extends BaseAdapter {
    String name[];
    int image[];
    private static LayoutInflater inflater = null;
    public MenuBaseAdapter(Context context, String[] name, int[] image){
        this.name = name;
        this.image = image;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){return name.length;}
    public Object getItem(int position){return position;}
    public long getItemId(int position){return position;}

    public class Holder{
        TextView title;
        ImageView homeIcon, arrowIcon;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();

        convertView = inflater.inflate(R.layout.drawer_list_items,null);
        holder.homeIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
        holder.title = (TextView)convertView.findViewById(R.id.tvTitle);
        holder.arrowIcon =(ImageView)convertView.findViewById(R.id.ivArrow);
        holder.title.setText(name[position]);
        holder.homeIcon.setImageResource(image[position]);
        return convertView;
    }
}
