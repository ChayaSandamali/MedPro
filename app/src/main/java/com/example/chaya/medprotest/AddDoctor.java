package com.example.chaya.medprotest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class AddDoctor extends ActionBarActivity {
    ArrayList<Doctor> doctors;
    ArrayList<String> doctorsNames;
    ArrayList<String> subscribedDoctorsNames;
    ArrayList<Message> msgList;
    ArrayList<String> msgTitleList;
    Button addDoc, removeDoc, sendMessage;
    ListView registeredDocList;
    ListView msgListView;
    TextView docName, docTel, docMedicalCenter, docSpecialized, alreadyConnected;
    ServerOperations serverOperations;
    String user_name = "";
    //check value for the correctly opened image from gallery
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView image;
    private String picturePath;
    ProgressDialog dialog;
    String upLoadServerUri = "http://medprohost.site11.com/media/UploadToServer.php";
    int serverResponseCode = 0;
    ArrayAdapter msgAdapter;
    Dialog sendMsgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        initSubscribedDocList();
        initViewVariables();
        initDocList();
        msgListView = (ListView) findViewById(R.id.messageList);
     /*initialize the arraylist for view messages*/
        msgTitleList = new ArrayList<String>();
       /*setting the custom adapter for the list*/
        msgAdapter = new ArrayAdapter(this, R.layout.listview_item_textview, msgTitleList);
        msgListView.setAdapter(msgAdapter);
    /* getting the loggged username for the session*/
        SharedPreferences settings = getSharedPreferences("SESSION_USERNAME", 0);
        user_name = settings.getString("username", null);
    /*pass varibales to the other intent to view the messages from users*/
        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message message = msgList.get(+i);
                Intent intent = new Intent(AddDoctor.this, MessageViewActivity.class);
                intent.putExtra("TITLE", message.getTitle());
                intent.putExtra("BODY", message.getBody());
                intent.putExtra("DOC", message.getDocName());
                intent.putExtra("REPLY", message.getReply());
                intent.putExtra("IMG", message.getImgPath());
                startActivity(intent);
            }
        });
    }

    /**
     * This message fetches data from database and initialize the messages sent by users
     * @param doc_name
     */
    private void initMessageList(String doc_name) {
        String url =  "//medprohost.site11.com/get_messages.php?user_name="+user_name+"&doc_name="+doc_name+"&func_to_execute=getMessagesForUser";
        URI uri;
        msgList = new ArrayList<Message>();
        msgTitleList.removeAll(msgTitleList);
        try {
            uri = new URI("http", url, null);
            url = uri.toString();
            serverOperations = new ServerOperations(url, "readFromDb");
              /*get results as a json object and decoding the object to exract data*/
            JSONArray messages = serverOperations.execute().get();
            for(int i = 0; i < messages.length(); i++){
                Message msg = new Message();
                msg.setId(messages.getJSONObject(i).getInt("id"));
                msg.setUserName(messages.getJSONObject(i).getString("user_name"));
                msg.setDocName(messages.getJSONObject(i).getString("doc_name"));
                msg.setTitle(messages.getJSONObject(i).getString("title"));
                msg.setBody(messages.getJSONObject(i).getString("body"));
                msg.setImgPath(messages.getJSONObject(i).getString("img_path"));
                msg.setReply(messages.getJSONObject(i).getString("reply"));
                msg.setCreatedDate(messages.getJSONObject(i).getString("date"));
                  /*add messages to the list*/
                msgList.add(msg);
                msgTitleList.add(messages.getJSONObject(i).getString("title"));
            }
              /*notify the eventListner for the msglist*/
            msgAdapter.notifyDataSetChanged();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is to handle the details about subscribed doctors
     */
    private void initSubscribedDocList() {

        JSONArray subbedDocs;
        subscribedDoctorsNames = new ArrayList<String>();
        try {
            String url =  "//medprohost.site11.com/get_subscribed_doctors.php?user_name="+user_name;
            URI uri = new URI("http", url, null);
            url = uri.toString();
              /*access the online database and fetching data*/
            serverOperations = new ServerOperations(url, "readFromDb");
            subbedDocs = serverOperations.execute().get();
            for(int i = 0; i < subbedDocs.length(); i++){
                subscribedDoctorsNames.add(subbedDocs.getJSONObject(i).getString("doctor_name"));
                Log.e("SUBNAMES", subbedDocs.getJSONObject(i).getString("doctor_name"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is to set details of all doctors  where the user can subscribed for
     */
    private void initDocList() {
        doctors = new ArrayList<Doctor>();
        doctorsNames = new ArrayList<String>();
        String url = "http://medprohost.site11.com/doctor_login.php";
        serverOperations = new ServerOperations(url,"readFromDb");
        try {
              /*fecthing data drom online database*/
            JSONArray docJsonArray = serverOperations.execute().get();
            for(int i = 0; i < docJsonArray.length(); i++){
                Doctor doc = new Doctor();
                  /*set each doctors details via doctor object*/
                doc.setUsername(docJsonArray.getJSONObject(i).getString("Username"));
                doc.setName(docJsonArray.getJSONObject(i).getString("Name"));
                doc.setTelNo(docJsonArray.getJSONObject(i).getString("Tel_No"));
                doc.setMedicalCenter(docJsonArray.getJSONObject(i).getString("MediAddress"));
                doc.setSpecArea(docJsonArray.getJSONObject(i).getString("Spec_Area"));
                doctorsNames.add(docJsonArray.getJSONObject(i).getString("Name"));
                doctors.add(doc);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
          /*adapter to view all registered doctors*/
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_item_textview, doctorsNames);
        registeredDocList.setAdapter(adapter);
    }

    /**
     * This method is to save users subscriptions to doctors to the database where they can add or remove them
     */
    private void initViewVariables() {
        addDoc = (Button) findViewById(R.id.connectDoc);
        removeDoc = (Button) findViewById(R.id.disconnectDoc);
        sendMessage = (Button) findViewById(R.id.messageDoc);
        registeredDocList = (ListView) findViewById(R.id.registeredDoctorList);
        docName = (TextView) findViewById(R.id.docName);
        docTel = (TextView) findViewById(R.id.docTel);
        docMedicalCenter = (TextView) findViewById(R.id.docMedicalCenter);
        docSpecialized = (TextView) findViewById(R.id.docSpecialized);
        alreadyConnected = (TextView) findViewById(R.id.alreadyCon);


     /*handling subscription of doctors*/
        addDoc.setOnClickListener(new View.OnClickListener() {
            String doc_name, url;
            @Override
            public void onClick(View view) {
                doc_name = docName.getText().toString();
                if(doc_name != null && user_name != null){
                    try {
                        url = "//medprohost.site11.com/connect_to_doc.php?user_name="+user_name+"&doc_name="+doc_name;
                        URI uri = new URI("http", url, null);
                        url = uri.toString();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    serverOperations = new ServerOperations(url,"getWriteToDb");
                    serverOperations.execute();
                    initSubscribedDocList();
                      /*display a message if a user tries to connect to an already subscribed doctor*/
                    alreadyConnected.setText("You have successfully connected to this doctor!");
                    addDoc.setEnabled(false);
                    removeDoc.setEnabled(true);
                    sendMessage.setEnabled(true);
                }
            }
        });
      /*handle unsubscription of doctors*/
        removeDoc.setOnClickListener(new View.OnClickListener() {
            String doc_name, url;
            @Override
            public void onClick(View view) {
                doc_name = docName.getText().toString();
                if(doc_name != null && user_name != null){
                    try {
      /*parsing the url to proper format*/
                        url = "//medprohost.site11.com/disconnect_from_doc.php?user_name="+user_name+"&doc_name="+doc_name;
                        URI uri = new URI("http", url, null);
                        url = uri.toString();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    serverOperations = new ServerOperations(url,"getWriteToDb");
                    serverOperations.execute();
                    initSubscribedDocList();
        /*display a message for succesful subscription*/
                    alreadyConnected.setText("You have successfully unsubscribed from this doctor!");
                    addDoc.setEnabled(true);
                    removeDoc.setEnabled(false);
                    sendMessage.setEnabled(false);
                }
            }
        });
      /*handle the message sending to doctors */
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        /*get the identifiers for resources from layout*/
                sendMsgDialog = new Dialog(AddDoctor.this);
                sendMsgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                sendMsgDialog.setContentView(R.layout.send_msg_to_doc);
                WindowManager.LayoutParams attributes = sendMsgDialog.getWindow().getAttributes();
                sendMsgDialog.getWindow().setLayout(attributes.MATCH_PARENT, attributes.WRAP_CONTENT);
                sendMsgDialog.show();
                final EditText entryName= (EditText) sendMsgDialog.findViewById(R.id.EntryName);
                final Button addImageBtn= (Button) sendMsgDialog.findViewById(R.id.AddImageBtn);
                Button addToDB= (Button) sendMsgDialog.findViewById(R.id.addToDB);
                image= (ImageView) sendMsgDialog.findViewById(R.id.entry_image);
                final EditText description= (EditText) sendMsgDialog.findViewById(R.id.entryDescription);
           /*acess the android system media from application*/
                addImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                });
                  /*passing values inorder to save to the database as a content object*/
                addToDB.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ContentValues values = new ContentValues();
                                values.put("msg_title", entryName.getText().toString());
                                values.put("msg_body", description.getText().toString());
                                values.put("img_path", picturePath);
                                values.put("doc_name", docName.getText().toString());
                                values.put("user_name", user_name);
                                send(values);
                            }
                        }
                );
            }
        });
      /*enabling and disabling subscribe and unsubscribe,send message buttons according to the subscription type of the user*/
        registeredDocList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Doctor doctor = doctors.get(+i);
                  /*set button text values*/
                docName.setText(doctor.getName());
                docTel.setText(doctor.getTelNo());
                docMedicalCenter.setText(doctor.getMedicalCenter());
                docSpecialized.setText(doctor.getSpecArea());
                initSubscribedDocList();
                if(subscribedDoctorsNames.contains(docName.getText().toString())){
                    alreadyConnected.setText("*You are already connected to this doctor");
                    addDoc.setEnabled(false);
                    removeDoc.setEnabled(true);
                    sendMessage.setEnabled(true);
                }else{
                    alreadyConnected.setText("");
                    addDoc.setEnabled(true);
                    removeDoc.setEnabled(false);
                    sendMessage.setEnabled(false);
                }
                initMessageList(docName.getText().toString());
            }
        });
    }

    /**
     * This method is to edit the message content and send those to the medpro server
     * @param values
     */
    private void send(ContentValues values) {
        String msgTitle = values.getAsString("msg_title");
        String msgBody = values.getAsString("msg_body");
        String msgDocName = values.getAsString("doc_name");
        String msgUserName = values.getAsString("user_name");
        String msgImagePath = values.getAsString("img_path");
          /*send the date for the message*/
        Time now = new Time();
        now.setToNow();
        int year = now.year;
        int month = now.month + 1;
        int day = now.monthDay;
        int hour = now.hour;
        int minute = now.minute;
        String dateCreated = year + "/" + month + "/" + day + " at " + hour + ":" +minute ;
        if(msgImagePath != null) {
            for (String s : msgImagePath.split("/")) {
                msgImagePath = s;
            }
            ;
        }
          /*parsing the url for proper format*/
        String url =  "//medprohost.site11.com/send_message.php?user_name="+msgUserName+"&doc_name="+msgDocName+"&title="+msgTitle+"&body="+msgBody+"&img_path="+msgImagePath+"&date="+dateCreated;
        URI uri = null;
        try {
            uri = new URI("http", url, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        url = uri.toString();
        serverOperations = new ServerOperations(url, "getWriteToDb");
        serverOperations.execute();

        if(msgImagePath != null) {
            new Thread(new Runnable() {
                public void run() {
                    uploadFile(picturePath);
                }
            }).start();
        }
        sendMsgDialog.dismiss();
    }

    /**
     * handle uploading of the user messages to server
     * @param sourceFileUri
     * @return
     */
    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            //dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"+picturePath);
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file; filename="+ fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(AddDoctor.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddDoctor.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddDoctor.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.add_doctor, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //setting path for the image from the device
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            // String picturePath contains the path of selected Image
        }
    }
}
