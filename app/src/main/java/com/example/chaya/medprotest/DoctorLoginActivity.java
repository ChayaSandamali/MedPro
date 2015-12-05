package com.example.chaya.medprotest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Doctor Login Activity
 */
public class DoctorLoginActivity extends Activity {
        // Variables to hold views data
        private EditText loginUsername;
        private EditText loginPassword;
        private EditText signupName;
        private EditText signupUsername;
        private EditText signupPassword;
        private EditText signupPasswordRepeat;
        private EditText signupAddress;
        private EditText signupTelephoneNo;
        private EditText signupCategory;
        private String name,username, password,repeatPass,address,telno,category;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_doctor_login);

            // Initializing view variables
            loginUsername = (EditText) findViewById(R.id.login_username);
            loginPassword = (EditText) findViewById(R.id.login_password);
            signupName = (EditText) findViewById(R.id.signup_name);
            signupUsername = (EditText) findViewById(R.id.signup_username);
            signupPassword = (EditText) findViewById(R.id.signup_password);
            signupPasswordRepeat = (EditText) findViewById(R.id.signup_repeat_password);
            signupAddress = (EditText) findViewById(R.id.signup_address);
            signupTelephoneNo = (EditText) findViewById(R.id.signup_telno);
            signupCategory = (EditText) findViewById(R.id.signup_category);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.doctor_login, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            return id == R.id.action_settings || super.onOptionsItemSelected(item);
        }

        public void signUp(View v){

            // Getting user inputs
            name = signupName.getText().toString();
            username = signupUsername.getText().toString();
            password = signupPassword.getText().toString();
            repeatPass = signupPasswordRepeat.getText().toString();
            address = signupAddress.getText().toString();
            telno   = signupTelephoneNo.getText().toString();
            category = signupCategory.getText().toString();

            // If inputs are valid, register him
            boolean validInputs = validateInputs(username,password, repeatPass,name,address,telno,category);
            if(validInputs){
                writeToDB();
            }
        }

        private void writeToDB() {
            new ServerOperations(
                    "http://medprohost.site11.com/doctor_register.php?","registerUser",
                    username,password,repeatPass,name,address,telno,category).execute();
        }

    /**
     * Validating user inputs
     * @param username  user name
     * @param password  password
     * @param repeatPass    repeat password
     * @param name  name
     * @param address   address
     * @param telno telephone number
     * @return
     */
        private boolean validateInputs(String username,String password,String repeatPass,String name,String address,String telno,String category) {
            String errorMessage = "";
            //String digits = "\\d+";
            if(name.equals("") || username.equals("") || password.equals("") || address.equals("") || telno.equals("") || category.equals("")){
                errorMessage = "Please fill-in all the fields!";
            } else if (username.length() < 5){
                errorMessage = "Username should contain minimum of 5 characters.";
            } else if(password.length() < 5){
                errorMessage = "Password should be more than 5 characters.";
            } else if(!password.equals(repeatPass)){
                errorMessage = "Your passwords do not match.";
            } else{
                return true;
            }
            Toast.makeText(getApplicationContext(),errorMessage, Toast.LENGTH_SHORT).show();
            return false;
        }

    /**
     * Login the Doctor
     * @param v pressed button
     */
        public void login(View v){
            String usrName = loginUsername.getText().toString();
            String usrPwd = loginPassword.getText().toString();
            String username, password, name = null;
            boolean isValidUser = false;
            final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(
                    "http://medprohost.site11.com/doctor_login.php","readFromDb").execute();
            try {

                // Read all the entries in the returned json array and authenticating
                final JSONArray jsonArray = execute.get();
                for(int i = 0; i < jsonArray.length(); i++){
                    username = jsonArray.getJSONObject(i).getString("Username");
                    password = jsonArray.getJSONObject(i).getString("Password");
                    name = jsonArray.getJSONObject(i).getString("Name");
                    if(username.equals(usrName) && password.equals(usrPwd)){
                        isValidUser = true;
                        break;
                    }
                }
                if (isValidUser){       // if the user is authenticated, greet him and send him to the Doctor activity
                    Toast.makeText(getApplicationContext(),
                            "Welcome to MedPro, " + name + "!", Toast.LENGTH_SHORT)
                            .show();
                    // Storing credentials for the session
                    SharedPreferences settings = getSharedPreferences("SESSION_USERNAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", name);
                    editor.commit();
                    Intent intent = new Intent(DoctorLoginActivity.this, DoctorActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(),
                            "Sorry, username and password does not match!", Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}
