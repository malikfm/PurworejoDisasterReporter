package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

/**
 * Created by malikfoundation on 7/1/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private List<DataModel> dataModelList;
    private int resource;
    private LayoutInflater inflater;
    public CustomAdapter(Context context, int resource, List<DataModel> objects) {
        super(context, resource, objects);
        dataModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.datetime = (TextView)convertView.findViewById(R.id.datetime);
            holder.img = (ImageView)convertView.findViewById(R.id.img);
            holder.bencana = (TextView)convertView.findViewById(R.id.bencana);
            holder.level = (TextView)convertView.findViewById(R.id.level);
            holder.latitude = (TextView)convertView.findViewById(R.id.latitude);
            holder.longitude = (TextView)convertView.findViewById(R.id.longitude);
            holder.deskripsi = (TextView)convertView.findViewById(R.id.deskripsi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProgressBar progressBarImg = (ProgressBar)convertView.findViewById(R.id.progressBar);

        holder.datetime.setText("Dilaporkan pada " + dataModelList.get(position).getDatetime());
        // Then later, when you want to display image
        new ImageLoadTask(dataModelList.get(position).getImage(), holder.img, progressBarImg).execute();
        holder.bencana.setText("Bencana: " + dataModelList.get(position).getBencana());
        holder.level.setText("Level Bencana: " + dataModelList.get(position).getLevel());
        holder.latitude.setText("Lat: " + dataModelList.get(position).getLatitude());
        holder.longitude.setText("Lng: " + dataModelList.get(position).getLongitude());
        holder.deskripsi.setText(dataModelList.get(position).getDeskripsi());

        return convertView;
    }

    private class ViewHolder {
        private TextView datetime;
        private ImageView img;
        private TextView bencana;
        private TextView level;
        private TextView latitude;
        private TextView longitude;
        private TextView deskripsi;
    }
}
