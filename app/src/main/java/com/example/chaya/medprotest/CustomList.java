package com.example.chaya.medprotest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> names;
    private final ArrayList<Integer> images;
    /*initialize passed data to the custom list*/
    public CustomList(Activity context, ArrayList<Integer> images, ArrayList<String> names) {
        super(context, R.layout.customlist_item, names);
        this.context = context;
        this.names = names;
        this.images = images;
    }
    /*create a cutomized view for each single item selected from the list*/
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.customlist_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
     /*set images and texts for the list items*/
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        txtTitle.setText(names.get(position));
        imageView.setImageResource(images.get(position));
        return rowView;
    }
}
