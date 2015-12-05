package com.example.chaya.medprotest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiseaseSearchItemActivity extends Activity {
   private Button viewDrugListBtn;
    String diseaseName;
    private Dialog dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_search_item);
         /*get the passed information from the previous Activity*/
        Intent intent = getIntent();
        diseaseName = intent.getStringExtra("DISEASE_NAME");
         /*get the identifier of the textviewer from the design*/
        TextView name = (TextView) findViewById(R.id.disease_name);
        name.setText(diseaseName);
         /*get the identifier of the imageviewer from the design*/
        ImageView imageView = (ImageView) findViewById(R.id.disease_image);
         /*get the images according to the selected input*/
        int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"diseasesearch_"+diseaseName.toLowerCase().replaceAll("\\s",""), null, null);
        /*set the image to display*/
        imageView.setImageResource(id);
        /*get resource identifiers for the textviewers*/
        TextView scientificName = (TextView) findViewById(R.id.scientific_name);
        TextView category = (TextView) findViewById(R.id.category);
        TextView overview = (TextView) findViewById(R.id.Overview);
        TextView symptoms = (TextView) findViewById(R.id.Symptoms);
        TextView causes = (TextView) findViewById(R.id.Causes);
        TextView healthTips = (TextView) findViewById(R.id.HealthTips);
        TextView preventions = (TextView) findViewById(R.id.Preventions);
        /*initiate database instance and access the database*/
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        try {
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*search from the database using cursor object*/
        String selectQuery = "SELECT * FROM Disease";
        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /*iterate through the search result*/
        if (cursor.moveToFirst()) {
            do {
        /*set details of the user selected drugs to display*/
                if (cursor.getString(1).equals(diseaseName)) {
                    scientificName.setText(cursor.getString(2));
                    category.setText(cursor.getString(9));
                    overview.setText(cursor.getString(3));
                    symptoms.setText(cursor.getString(4));
                    causes.setText(cursor.getString(5));
                    healthTips.setText(cursor.getString(6));
                    preventions.setText(cursor.getString(7));
                }
            } while (cursor.moveToNext());
        }
        /*close search result object and the database object*/
        cursor.close();
        dataBaseHelper.close();
        /*get the identity of the view map button from the design*/
        viewDrugListBtn = (Button) findViewById(R.id.ViewDrugList);
        /*button action performed event*/
        viewDrugListBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // custom dialog
                        dialog = new Dialog(DiseaseSearchItemActivity.this);
                        dialog.setContentView(R.layout.customalert);
                        dialog.setTitle("DrugList");
                        TextView text = (TextView) dialog.findViewById(R.id.text);
//                        ImageView image = (ImageView) dialog.findViewById(R.id.image);
                        ArrayList<String> names = new ArrayList<String>();
                        ArrayList<Integer> images = new ArrayList<Integer>();
                         /*initiate the database instance*/
                        final DataBaseHelper dataBaseHelper = new DataBaseHelper(DiseaseSearchItemActivity.this);
        /*Initiate a customlist to display the drugs*/
                        final CustomList customList = new CustomList(DiseaseSearchItemActivity.this, images, names);
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
                        String selectQuery = "SELECT DrugName FROM Drug_Disease WHERE DiseaseName= '"+diseaseName+"'";
                        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /* iterate through the resultset*/
                        if (cursor.moveToFirst()) {
                            do {
                                names.add(cursor.getString(0));
                               // Log.e("drug",cursor.getString(0));
                                int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"drug_disease_"+cursor.getString(0).toLowerCase().replaceAll("\\s",""), null, null);
                                images.add(id);
                            } while (cursor.moveToNext());
                        }
        /* close the database connection
         and the search resultset object*/
                        cursor.close();
                        dataBaseHelper.close();
                        /*get the list view identifier from design*/
                        ListView listView = (ListView) dialog.findViewById(R.id.drugList);
        /*set the custom adapter for the list to customize display*/
                        listView.setAdapter(customList);
//                        // set the custom dialog components - text, image and button
//                        text.setText("Android custom dialog example!");
//                        image.setImageResource(R.drawable.ic_launcher);

                        Button dialogButton = (Button) dialog.findViewById(R.id.OK);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drug_search_item, menu);
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

    public Dialog getDialog(){
        return this.dialog;
    }
}
