package com.example.chaya.medprotest;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * Doctors Activity class
 */
public class DoctorActivity extends ActionBarActivity {
    /*
    * variables to hold views
    * */
    ArrayList<Message> msgList = new ArrayList<Message>();
    TableLayout msgTable;
    TextView msgTitleTv;
    TextView msgBodyTv;
    TextView msgDateTv;
    ImageView msgImg;
    Button reply;
    private int selectedMsgId;
    String docName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        /*getting identifiers from layout file*/
        msgTable = (TableLayout) findViewById(R.id.msgTable);
        msgTitleTv = (TextView) findViewById(R.id.msgTitle);
        msgBodyTv = (TextView) findViewById(R.id.msgBody);
        msgDateTv = (TextView) findViewById(R.id.msgDate);
        reply = (Button) findViewById(R.id.reply);
        msgImg = (ImageView) findViewById(R.id.msgImage);


        final Dialog d = new Dialog(DoctorActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Remove title bar from the dialog box
        d.setContentView(R.layout.send_msg_to_user);
        WindowManager.LayoutParams attributes = d.getWindow().getAttributes();
        d.getWindow().setLayout(attributes.MATCH_PARENT, attributes.WRAP_CONTENT);
        /*setting the username in a shared preferences file. in order to handle the session */
        SharedPreferences settings = getSharedPreferences("SESSION_USERNAME", 0);
        docName = settings.getString("username", null);

        Button replyButton = (Button) d.findViewById(R.id.sendReply);

        // Send the reply to the serever
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText et = (EditText) d.findViewById(R.id.messageBody);
                    String reply = et.getText().toString();
                    String url = "//medprohost.site11.com/reply.php?message_id="+selectedMsgId+"&reply="+reply;
                    URI uri = new URI("http", url, null);
                    url = uri.toString();
                    ServerOperations serverOperations = new ServerOperations(url,"getWriteToDb");
                    serverOperations.execute();
                    d.dismiss();
                    msgTable.removeAllViews();
                    initMessageList();
                    initMessageTable();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedMsgId != 0) {
                    d.show();
                }
            }
        });

        initMessageList();
        initMessageTable();
    }

    /**
     * Initializing the messages inside the table
     */
    private void initMessageTable() {

        // Layout parameters for the rows and the table
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParamsId = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
        TableRow.LayoutParams rowParamsSender = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.125f);
        TableRow.LayoutParams rowParamsMsg = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.65f);
        TableRow.LayoutParams rowParamsDate = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.125f);

        TextView msgId, msgSender, msg, msgDate;

        int textSize = 15;
        int gravity = Gravity.CENTER;

        Iterator<Message> iterator = msgList.iterator();

        int id;
        String sender, msgTitle, msgReply, msgCreatedDate;
        Message message;

        // Iterate through all the messages and add them into the table views
        while (iterator.hasNext()){

            TableRow row = new TableRow(this);

            row.setLayoutParams(tableParams);
            row.setGravity(gravity);
            row.setPadding(15, 15, 15, 15);
            row.setBackgroundColor(Color.parseColor("#EEEEEE"));

            msgId = new TextView(this);
            msgSender = new TextView(this);
            msg = new TextView(this);
            msgDate = new TextView(this);

            msgId.setLayoutParams(rowParamsId);
            msgSender.setLayoutParams(rowParamsSender);
            msg.setLayoutParams(rowParamsMsg);
            msgDate.setLayoutParams(rowParamsDate);

            msgId.setGravity(gravity);
            msgSender.setGravity(gravity);
            msg.setGravity(gravity);
            msgDate.setGravity(gravity);

            msgId.setTextSize(textSize);
            msgSender.setTextSize(textSize);
            msg.setTextSize(textSize);
            msgDate.setTextSize(textSize);

            msgSender.setMaxWidth(80);
            msg.setMaxWidth(60);
            msgDate.setMaxWidth(40);

            message = iterator.next();
            id = message.getId();
            sender = message.getUserName();
            msgTitle = message.getTitle();
            msgReply = message.getReply();
            msgCreatedDate = message.getCreatedDate();
            if(msgReply == null){
                row.setBackgroundColor(Color.parseColor("#F5F5F5"));
            }

            msgId.setText(""+id);
            msgSender.setText(sender);
            msg.setText(msgTitle);
            msgDate.setText(msgCreatedDate);


            row.setId(id);
            // If a row is clicked, the full message is shown
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Iterator<Message> iterator1 = msgList.iterator();
                    while (iterator1.hasNext()){
                        Message m = iterator1.next();
                        if(m.getId() == view.getId()){
                            selectedMsgId = m.getId();
                            msgTitleTv.setText(m.getTitle());
                            msgBodyTv.setText(m.getBody());
                            msgDateTv.setText(m.getCreatedDate());
                            ImageDownloader downloader = new ImageDownloader(); // Downloading the images of the message
                            Bitmap bmp = null;
                            try {
                                bmp = downloader.execute(m.getImgPath()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if(bmp != null) {
                                msgImg.setImageBitmap(bmp);
                            }else{
                                msgImg.setImageBitmap(null);
                            }
                        }
                    }
                }
            });

            row.addView(msgId);
            row.addView(msgSender);
            row.addView(msg);
            row.addView(msgDate);

            msgTable.addView(row);
        }

    }

    // Read all the messages from the database and put them in the List
    private void initMessageList() {

        try {
            String url =  "//medprohost.site11.com/get_messages.php?doc_name="+docName+"&func_to_execute=getMessagesForDoc";
            URI uri;
            uri = new URI("http", url, null);
            url = uri.toString();
            ServerOperations serverOperations = new ServerOperations(url, "readFromDb");
            JSONArray msgs = serverOperations.execute().get();
            for(int i = 0; i < msgs.length(); i++){     // Read all the messages from the json array and populating Message objects
                Message msg = new Message();
                msg.setId(msgs.getJSONObject(i).getInt("id"));
                msg.setUserName(msgs.getJSONObject(i).getString("user_name"));
                msg.setDocName(msgs.getJSONObject(i).getString("doc_name"));
                msg.setTitle(msgs.getJSONObject(i).getString("title"));
                msg.setBody(msgs.getJSONObject(i).getString("body"));
                msg.setImgPath(msgs.getJSONObject(i).getString("img_path"));
                msg.setReply(msgs.getJSONObject(i).getString("reply"));
                msg.setCreatedDate(msgs.getJSONObject(i).getString("date"));
                msgList.add(msg);
            }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.doctor, menu);
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
