package com.example.chaya.medprotest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chaya on 9/5/2014.
 */
public class LogCustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> names;
    /*initialize passed data to the custom list*/
    public LogCustomList(Activity context,ArrayList<String> names) {
        super(context, R.layout.customlist_item, names);
        this.context = context;
        this.names = names;
    }
    /*create a cutomized view for each single item selected from the list*/
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.logcustom_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        txtTitle.setText(names.get(position));
        return rowView;
    }
}
