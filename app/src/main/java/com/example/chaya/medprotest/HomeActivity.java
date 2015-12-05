package com.example.chaya.medprotest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends Activity {
    private Button DrugSearchBtn;
    private Button DiseaseSearchBtn;
    private  Button GeneralSearchBtn;
    private Button ReminderBtn;
    private Button LogBookBtn;
    private Button addDoctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*get the button Identifiers */
        DrugSearchBtn = (Button) findViewById(R.id.drugSearch);
        ReminderBtn = (Button) findViewById(R.id.Reminder);
        DiseaseSearchBtn = (Button) findViewById(R.id.DiseaseSearch);
        GeneralSearchBtn = (Button) findViewById(R.id.GeneralSearch);
        LogBookBtn = (Button) findViewById(R.id.LogBook);
        addDoctor = (Button) findViewById(R.id.AddDoctor);
        /* eventhandler for ButtonClick */
        DrugSearchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*starting the new acivity after button click*/
                        Intent intent = new Intent(HomeActivity.this, DrugSearchActivity.class);
                        startActivity(intent);
                    }
                }
        );
        DiseaseSearchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*starting the new acivity after button click*/
                        Intent intent = new Intent(HomeActivity.this, DiseaseSearchActivity.class);
                        startActivity(intent);
                    }
                }
        );
        ReminderBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*starting the new acivity after button click*/
                        Intent intent = new Intent(HomeActivity.this, ReminderActivity.class);
                        startActivity(intent);
                    }
                }
        );
        GeneralSearchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*starting the new acivity after button click*/
                        Intent intent = new Intent(HomeActivity.this,GeneralSearchActivity.class);
                        startActivity(intent);
                    }
                }
        );
        LogBookBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*starting the new acivity after button click*/
                        Intent intent = new Intent(HomeActivity.this,LogActivity.class);
                        startActivity(intent);
                    }
                }
        );

        addDoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, AddDoctor.class);
                        startActivity(intent);
                    }
                }
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
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
