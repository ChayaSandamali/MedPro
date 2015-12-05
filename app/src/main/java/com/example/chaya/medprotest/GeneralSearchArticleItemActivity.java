package com.example.chaya.medprotest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaya.medprotest.R;

import java.io.IOException;
import java.sql.SQLException;

public class GeneralSearchArticleItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_search_article_item);
        Intent intent = getIntent();
        String articleTopic = intent.getStringExtra("ARTICLE_TOPIC");
        String articleCategory = intent.getStringExtra("ARTICLE_CATEGORY");
         /*get the identifier of the textviewer from the design*/
        TextView name = (TextView) findViewById(R.id.article_name);
        name.setText(articleTopic);
         /*get the identifier of the imageviewer from the design*/
        ImageView imageView = (ImageView) findViewById(R.id.article_image);
         /*get the images according to the selected input*/
        int id = getResources().getIdentifier("com.example.chaya.medprotest:drawable/" + "generalsearch_"+articleCategory.toLowerCase().replaceAll("\\s", "")+"_" +articleTopic.toLowerCase().replaceAll("\\s", ""), null, null);
        /*set the image to display*/
        imageView.setImageResource(id);
        /*get resource identifiers for the textviewers*/
        TextView description = (TextView) findViewById(R.id.Description);
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
        String selectQuery = "SELECT Description FROM generalsearch WHERE Topic= '"+articleTopic+"'";
        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /*iterate through the search result*/
        if (cursor.moveToFirst()) {
            do {
        /*set details of the user selected drugs to display*/
                description.setText(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        /*close search result object and the database object*/
        cursor.close();
        dataBaseHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_search_article_item, menu);
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
