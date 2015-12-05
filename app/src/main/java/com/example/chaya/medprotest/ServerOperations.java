package com.example.chaya.medprotest;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by ChayaSandamali on 9/14/2014.
 */
public class ServerOperations extends AsyncTask<Void, Void, JSONArray> {

    private String url;
    private String method;
    private HttpClient client = new DefaultHttpClient();
    private HttpResponse response;
    private String username,password,repeatPass,name,address,telno,category;

    public ServerOperations(String url, String method){
        this.url = url;
        this.method = method;
    }

    public ServerOperations(String url,String method,String username,String password,String repeatPass,String name,String address,String telno,String category){
        this.url = url;
        this.method = method;
        this.name = name;
        this.username = username;
        this.password = password;
        this.repeatPass=repeatPass;
        this.address=address;
        this.telno=telno;
        this.category=category;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        if(method.equals("readFromDb")){
            return readFromDb();
        } else if(method.equals("registerUser")){
            registerUser();
        } else if(method.equals("getWriteToDb")){
            getWriteToDb();
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
            nameValuePairs.add(new BasicNameValuePair("address",address));
            nameValuePairs.add(new BasicNameValuePair("telno",telno));
            nameValuePairs.add(new BasicNameValuePair("category",category));
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
            }
            JSONArray finalResult = new JSONArray(builder.toString());
            return finalResult;
        } catch(IOException e) {
            //do something here
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    protected void onPostExecute(JSONArray result) {}

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}
