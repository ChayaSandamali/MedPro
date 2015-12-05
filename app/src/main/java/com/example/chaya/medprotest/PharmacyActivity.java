package com.example.chaya.medprotest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PharmacyActivity extends ActionBarActivity {
    private Spinner drugNames;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ListView availableDrugs;
    private Button Save;
    private String[] drugs,pharmacyNames;
    private String pha_name;
    private TextView pharName;
    /* Initialize arraylists to keep images and names of drugs*/
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Integer> images = new ArrayList<Integer>();
    CustomList customList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);
        pharName= (TextView) findViewById(R.id.PharmacyName);
        Intent intent = getIntent();

        //Get the pharmacy name to show information
        pha_name = intent.getStringExtra("MYPHARMACY_NAME");
        pharName.setText(pha_name);

        //Initializing views
        drugNames= (Spinner) findViewById(R.id.drugList);
        availableDrugs= (ListView) findViewById(R.id.availableList);
        radioGroup= (RadioGroup) findViewById(R.id.radioGrp);
       // loadPharmacies();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,pharmacyNames);
//        pharNames.setAdapter(adapter);
          loadDrugs();
        ArrayAdapter<String> drug_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,drugs);
        drugNames.setAdapter(drug_adapter);
        customList = new CustomList(this, images, names);
        loadAvailableDrugList();
          /*get the list view identifier from design*/
        ListView listView = (ListView) findViewById(R.id.availableList);
        /*set the custom adapter for the list to customize display*/
        listView.setAdapter(customList);
    }

//    private void loadPharmacies() {
//        final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(
//                "http://medprohost.site11.com/pharmacy_names.php", "readFromDb").execute();
//        try {
//            final JSONArray jsonArray = execute.get();
//            pharmacyNames=new String[jsonArray.length()+1];
//            pharmacyNames[0]="Select Your Pharmacy";
//            for (int i = 0; i < jsonArray.length(); i++) {
//                pharmacyNames[i+1]=jsonArray.getJSONObject(i).getString("PharmacyName");
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Load available drug list in the pharmacy
     */
    private void loadAvailableDrugList(){
        Intent intent = getIntent();
        String myPharmacyName = intent.getStringExtra("MYPHARMACY_NAME");
        Log.e("name",myPharmacyName);
        String url = null;
        try {
            url = "//medprohost.site11.com/drug_availability.php?pharmacyName="+myPharmacyName;
            URI uri = new URI("http", url, null);
            url = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(url, "readFromDb").execute();
        try {
            final JSONArray jsonArray = execute.get();
            for (int i = 0; i < jsonArray.length(); i++) {
                String drug= jsonArray.getJSONObject(i).getString("DrugName");
                String availability=jsonArray.getJSONObject(i).getString("Availability");
               // Log.e("test",availability);
                if(availability.equals("Available")){
                    // If the drug is available, add it to the list of available drugs
                    int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" +"pharmacy_drug_available", null, null);
                    names.add(drug);
                    images.add(id);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the list of all drugs to show in the dropdown
     */
    private void loadDrugs() {
        final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(
                "http://medprohost.site11.com/drug_names.php", "readFromDb").execute();
        try {
            final JSONArray jsonArray = execute.get();
            drugs=new String[jsonArray.length()+1];
            drugs[0]="Select Drug";
            for (int i = 0; i < jsonArray.length(); i++) {
                drugs[i+1]= jsonArray.getJSONObject(i).getString("drugName");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save drug in tha databse table
     * @param v
     */
    public void save(View v){
        //pha_name= pharNames.getSelectedItem().toString();
        String d_name= drugNames.getSelectedItem().toString();
        int id= radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(id);
        int radioId = radioGroup.indexOfChild(radioButton);
        RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
        String selection = (String) btn.getText();
       // Log.e("save","aaaa");
//        new ServerOperations(
//                "http://medprohost.site11.com/add_drug.php?","addToDatabase",
//                pha_name,d_name,selection).execute();
        String url = null;
        try {
            url = "//medprohost.site11.com/add_drug.php?PharmacyName="+pha_name+"&DrugName="+d_name+"&Selection="+selection;
            URI uri = new URI("http", url, null);
            url = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Refresh the list of available drugs when the pharmacist adds a new drug
        final AsyncTask<Void, Void, JSONArray> execute = new ServerOperations(url, "getWriteToDb").execute();
        Log.e("URL", url);
        names.removeAll(names);
        images.removeAll(images);
        loadAvailableDrugList();
        customList.notifyDataSetChanged();
    }

    /**
     * Class to handle server operations like accessing the database
     */
    private class ServerOperations extends AsyncTask<Void, Void, JSONArray> {

        private String url;
        private String method;
        private HttpClient client = new DefaultHttpClient();
        private HttpResponse response;
        private String pha_name,d_name,selection;

        public ServerOperations(String url, String method){
            this.url = url;
            this.method = method;
        }

        public ServerOperations(String url,String method,String pharmacyName,String DrugName,String selection){
            this.url = url;
            this.method = method;
            this.pha_name=pharmacyName;
            this.d_name=DrugName;
            this.selection=selection;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            if(method.equals("readFromDb")){
                return readFromDb();
            } else if(method.equals("addToDatabase")){
                Log.e("sss","inside json");
                addToDatabase();
            } else if(method.equals("getWriteToDb")){
                getWriteToDb();
            }
            return null;
        }

        private void getWriteToDb(){
            try {
                HttpGet httpGet = new HttpGet(url);
                client.execute(httpGet);
                Log.e("URL", url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


            /**
             * Add name value pairs to the database
             */
        private void addToDatabase() {
            //Log.e("sss","just in");
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("PharmacyName",pha_name));
                nameValuePairs.add(new BasicNameValuePair("DrugName",d_name));
                nameValuePairs.add(new BasicNameValuePair("Selection",selection));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
               // Log.e("sss","inside addtodatabase");
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
             //  Log.e("response", response.getEntity().getContent().toString());
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        /**
         * read values from database and return them in a json array
         * @return
         */
        private JSONArray readFromDb(){
            try {
                response = client.execute(new HttpGet(url));
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                    // Log.e("der",line);
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pharmacy, menu);
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
