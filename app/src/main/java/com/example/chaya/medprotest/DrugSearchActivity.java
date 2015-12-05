package com.example.chaya.medprotest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Chaya on 8/14/2014.
 */
public class DrugSearchActivity extends Activity {
    /* Initialize arraylists to keep images and names of drugs*/
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Integer> images = new ArrayList<Integer>();
    TextView drugSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_search);
        /*initiate the database instance*/
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        /*Initiate a customlist to display the drugs*/
        final CustomList customList = new CustomList(this, images, names);
        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
          /* search results from the database and get the whole result set object*/
        String selectQuery = "SELECT * FROM Drug";
        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /* iterate through the resultset*/
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));
                int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"drugsearch_drug_"+cursor.getString(1) .toLowerCase().replaceAll("\\s",""), null, null);
                images.add(id);
            } while (cursor.moveToNext());
        }
        /* close the database connection
         and the search resultset object*/
        cursor.close();
        dataBaseHelper.close();
        drugSearch= (TextView) findViewById(R.id.searchDrug);
        /**
         * code for drugSearch
         */
        drugSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                names.removeAll(names);
                images.removeAll(images);
                try {
         /*access the database*/
                    dataBaseHelper.createDataBase();
                    dataBaseHelper.openDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String selectQuery = "SELECT * FROM Drug WHERE Name LIKE '"+s+"%'";
                Cursor cursor = dataBaseHelper.readFrom(selectQuery);
                if (cursor.moveToFirst()) {
                    do {
                        names.add(cursor.getString(1));
                        //Log.d("drugname",cursor.getString(1));
                        /*get the images according to the selected input*/
                        int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"drugsearch_drug_"+cursor.getString(1) .toLowerCase().replaceAll("\\s",""), null, null);
                        /*set the image to display*/
                        images.add(id);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dataBaseHelper.close();
                customList.notifyDataSetChanged();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        dataBaseHelper.close();

        /*get the list view identifier from design*/
        ListView listView = (ListView) findViewById(R.id.drugList);
        /*set the custom adapter for the list to customize display*/
        listView.setAdapter(customList);
        /*adding the functionality to list click event*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
         /*create a container to pass information to the next view*/
                Intent intent = new Intent(DrugSearchActivity.this, DrugSearchItemActivity.class);
         /*get the index of the selected item from the list*/
                intent.putExtra("DRUG_NAME", names.get(+i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drug_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
