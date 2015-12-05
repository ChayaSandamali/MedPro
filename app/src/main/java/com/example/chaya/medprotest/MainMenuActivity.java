package com.example.chaya.medprotest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends ActionBarActivity {
    Button NormalUser,Doctor,Pharmacist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        NormalUser = (Button) findViewById(R.id.NormalUser);
        Doctor = (Button) findViewById(R.id.Doctor);
        Pharmacist = (Button) findViewById(R.id.Pharmacist);
        /* eventhandler for ButtonClick */
        NormalUser.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainMenuActivity.this,UserLoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
        Doctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainMenuActivity.this,DoctorLoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
        Pharmacist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainMenuActivity.this,PharmacyLoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
