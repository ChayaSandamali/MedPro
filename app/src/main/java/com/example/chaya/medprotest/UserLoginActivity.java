package com.example.chaya.medprotest;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chaya.medprotest.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserLoginActivity extends ActionBarActivity {
    private EditText loginUsername;
    private EditText loginPassword;
    private EditText signupName;
    private EditText signupUsername;
    private EditText signupPassword;
    private EditText signupPasswordRepeat;
    private EditText signupTelephoneNo;
    private String name,username, password,repeatPass,telno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ActionBar bar = getActionBar();
        bar.setTitle("");

        loginUsername = (EditText) findViewById(R.id.login_username);
        loginPassword = (EditText) findViewById(R.id.login_password);
        signupName = (EditText) findViewById(R.id.signup_name);
        signupUsername = (EditText) findViewById(R.id.signup_username);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        signupPasswordRepeat = (EditText) findViewById(R.id.signup_repeat_password);
        signupTelephoneNo = (EditText) findViewById(R.id.signup_telno);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void signup(View v){
        name = signupName.getText().toString();
        username = signupUsername.getText().toString();
        password = signupPassword.getText().toString();
        repeatPass = signupPasswordRepeat.getText().toString();
        telno   = signupTelephoneNo.getText().toString();

        boolean validInputs = validateInputs(username,password, repeatPass,name,telno);
        if(validInputs){
            writeToDB();
        }
    }

    private void writeToDB() {
        new ServerOperations(
                "http://medprohost.site11.com/user_register.php?","registerUser",
                username,password,repeatPass,name,telno).execute();
    }

    private boolean validateInputs(String username,String password,String repeatPass,String name,String telno) {
        String errorMessage = "";
        //String digits = "\\d+";
        if(name.equals("") || username.equals("") || password.equals("") ||telno.equals("")){
            errorMessage = "Please fill-in all the fields!";
        } else if (username.length() < 5){
            errorMessage = "Username should contain minimum of 5 characters.";
        } else if(password.length() < 5){
            errorMessage = "Password should be more than 5 characters.";
        } else if(!password.equals(repeatPass)){
            errorMessage = "Your passwords does not match.";
        } else{
            return true;
        }
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void login(View v){
        String usrName = loginUsername.getText().toString();
        String usrPwd = loginPassword.getText().toString();
        String username, password, name = null;
        boolean isValidUser = false;
        final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(
                "http://medprohost.site11.com/user_login.php","readFromDb").execute();
        try {
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
            if (isValidUser){
                Toast.makeText(getApplicationContext(),
                        "Welcome to MedPro, " + name + "!", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                SharedPreferences settings = getSharedPreferences("SESSION_USERNAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", name);
                editor.commit();
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

    private class ServerOperations extends AsyncTask<Void, Void, JSONArray> {

        private String url;
        private String method;
        private HttpClient client = new DefaultHttpClient();
        private HttpResponse response;
        private String username,password,repeatPass,name,telno;

        public ServerOperations(String url, String method){
            this.url = url;
            this.method = method;
        }

        public ServerOperations(String url,String method,String username,String password,String repeatPass,String name,String telno){
            this.url = url;
            this.method = method;
            this.name = name;
            this.username = username;
            this.password = password;
            this.repeatPass=repeatPass;
            this.telno=telno;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            if(method.equals("readFromDb")){
                return readFromDb();
            } else if(method.equals("registerUser")){
                registerUser();
            }
            return null;
        }

        private void registerUser() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("passwordRepeat",repeatPass));
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("telno",telno));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.e("response", response.getEntity().getContent().toString());
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        private JSONArray readFromDb(){
            try {
                response = client.execute(new HttpGet(url));
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                    Log.e("der",line);
                }
                JSONArray finalResult = new JSONArray(builder.toString());
//                    Log.e("", finalResult.getJSONObject(0).getString("Name"));
                return finalResult;
            } catch(IOException e) {
                //do something here
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {}

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
