package com.example.chaya.medprotest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaya.medprotest.R;

import java.io.IOException;
import java.sql.SQLException;

public class DrugSearchItemActivity extends Activity {
    private Button viewPharmacyBtn;
    private Button viewSpecificPharmacyBtn;
    private Button viewOnMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_search_item);
         /*get the passed information from the previous Activity*/
        Intent intent = getIntent();
        final String drugName = intent.getStringExtra("DRUG_NAME");
         /*get the identifier of the textviewer from the design*/
        TextView name = (TextView) findViewById(R.id.drug_name);
        name.setText(drugName);
         /*get the identifier of the imageviewer from the design*/
        ImageView imageView = (ImageView) findViewById(R.id.drug_image);
         /*get the images according to the selected input*/
        int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"drugsearch_drug_"+drugName.toLowerCase().replaceAll("\\s",""), null, null);
        /*set the image to display*/
        imageView.setImageResource(id);
        /*get resource identifiers for the textviewers*/
        TextView overView = (TextView) findViewById(R.id.overView_des);
        TextView dosage = (TextView) findViewById(R.id.dosage_des);
        TextView precautions = (TextView) findViewById(R.id.precautions_des);
        TextView interactions = (TextView) findViewById(R.id.interactions_des);
        TextView sideEffects = (TextView) findViewById(R.id.sideEffects_des);
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
        String selectQuery = "SELECT * FROM Drug";
        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /*iterate through the search result*/
        if (cursor.moveToFirst()) {
            do {
        /*set details of the user selected drugs to display*/
                if (cursor.getString(1).equals(drugName)) {
                    overView.setText(cursor.getString(2));
                    dosage.setText(cursor.getString(3));
                    precautions.setText(cursor.getString(4));
                    interactions.setText(cursor.getString(5));
                    sideEffects.setText(cursor.getString(8));
                }
            } while (cursor.moveToNext());
        }
        /*close search result object and the database object*/
        cursor.close();
        dataBaseHelper.close();
        /*get the identity of the view map button from the design*/
        viewPharmacyBtn = (Button) findViewById(R.id.viewPharmacy);
        viewSpecificPharmacyBtn = (Button) findViewById(R.id.viewSpecificPharmacy);
        viewOnMapBtn = (Button) findViewById(R.id.viewOnMap);
        /*button action performed event*/
        viewPharmacyBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DrugSearchItemActivity.this,ViewPharmacy.class);
                        intent.putExtra("OPTION", "VIEW_ALL");
                        startActivity(intent);
                    }
                }
        );

        viewSpecificPharmacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugSearchItemActivity.this,ViewPharmacy.class);
                intent.putExtra("OPTION", "VIEW_SPECIFIC");
                intent.putExtra("DRUG_NAME", drugName);
                startActivity(intent);
            }
        });

        viewOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugSearchItemActivity.this,MapActivity.class);
                intent.putExtra("OPTION","VIEW_PHARMACIES" );
                startActivity(intent);
            }
        });
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
}
