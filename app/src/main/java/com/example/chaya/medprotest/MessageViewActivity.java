package com.example.chaya.medprotest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;


public class MessageViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String body = intent.getStringExtra("BODY");
        String doctor = intent.getStringExtra("DOC");
        String imgName = intent.getStringExtra("IMG");
        String reply = intent.getStringExtra("REPLY");

        TextView titleTv = (TextView)findViewById(R.id.msgViewTitle);
        TextView bodyTv = (TextView)findViewById(R.id.msgViewBody);
        TextView doctorTv = (TextView)findViewById(R.id.msgViewDoc);
        TextView replyTv = (TextView)findViewById(R.id.msgViewReply);
        ImageView imgTv = (ImageView)findViewById(R.id.msgViewImage);

        titleTv.setText(title);
        bodyTv.setText(body);
        doctorTv.setText(doctor);
        replyTv.setText(reply);

        ImageDownloader imgDownloader = new ImageDownloader();

        Bitmap bmp = null;
        try {
            bmp = imgDownloader.execute(imgName).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(bmp != null) {
            imgTv.setImageBitmap(bmp);
        }else{
            imgTv.setImageBitmap(null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.message_view, menu);
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
