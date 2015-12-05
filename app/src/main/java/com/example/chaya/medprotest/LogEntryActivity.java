package com.example.chaya.medprotest;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.chaya.medprotest.R;

import java.io.IOException;
import java.sql.SQLException;

public class LogEntryActivity extends ActionBarActivity {
    private DataBaseHelper dataBaseHelper;
    private Cursor cursor;
    private Button addEntry;
    /* assigning check value to the correctly loaded image from the gallery
        * */
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath;
    private ImageView image;
    private String logName,imagePath,name,description;
    TableLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_entry);
        /* access the passed log name from the previous activity
        * */
        Intent intent = getIntent();
        logName = intent.getStringExtra("LOG_NAME");
        /* method for loading existing entries
        * */
        loadEntries();
          /* method for adding new entry to the logbook
        * */
        addNewEntry();
    }
    public void loadEntries(){
        dataBaseHelper=new DataBaseHelper(this);
        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         /* getting the added results from database
        * */
        cursor=dataBaseHelper.readFrom("SELECT * FROM LogEntry WHERE LogName='"+logName+"'");
        int i=0;
        if (cursor.moveToFirst()) {
             /*
             creating the dynamic layout for the added entries
            * */
            do {
                TableLayout tableLayout=new TableLayout(this);
                TableRow tableRow1=new TableRow(this);
                TableRow tableRow2=new TableRow(this);
                TableRow tableRow3=new TableRow(this);
                TableRow tableRow4=new TableRow(this);
                TextView entryName=new TextView(this);
                ImageView entryImage=new ImageView(this);
                TextView entrydescription=new TextView(this);
                Button editBtn=new Button(this);
                Button deleteBtn=new Button(this);
                /*
               setting the layout parameters for the view
            * */
                TableLayout.LayoutParams tableLP=new TableLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.WRAP_CONTENT);
                tableLayout.setLayoutParams(tableLP);
                TableRow.LayoutParams rowLp=new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT,0.5f);
                TableRow.LayoutParams imageLp=new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT,1f);
                TableRow.LayoutParams rowLpButtons=new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT,0.5f);
                imageLp.span=2;
                entryName.setLayoutParams(rowLp);
                entrydescription.setLayoutParams(rowLp);
                entryImage.setLayoutParams(imageLp);
                deleteBtn.setText("Delete");
                editBtn.setText("Edit");
                editBtn.setWidth(110);
                deleteBtn.setWidth(110);
                editBtn.setHeight(40);
                deleteBtn.setHeight(40);
                entryImage.setMaxHeight(300);
                entryImage.setMaxWidth(300);
                editBtn.setLayoutParams(rowLpButtons);
                deleteBtn.setLayoutParams(rowLpButtons);
                description=cursor.getString(2);
                name=cursor.getString(1);
                entryName.setText(name);
                 /*
             styling the dynamic layout and decode the image
            * */
                entryName.setTextColor(Color.parseColor("#CCCCCC"));
                entryName.setBackgroundColor(Color.parseColor("#017597"));
                entryName.setTextSize(20);
                entryName.setGravity(Gravity.CENTER);
                entryName.setPadding(10,10,10,10);
                entrydescription.setPadding(10,10,10,10);
                entryImage.setPadding(10,10,10,10);
                entrydescription.setText(description);
                /*
                decode the image
            * */
                imagePath=cursor.getString(4);
                final Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if(bitmap != null){
                    entryImage.setImageBitmap(bitmap);
                    entryImage.setAdjustViewBounds(true);
                }
                tableRow1.addView(entryName);
                tableRow2.addView(entryImage);
                // tableRow2.addView(addImageBtn);
                tableRow3.addView(entrydescription);
                tableRow4.addView(editBtn);
                tableRow4.addView(deleteBtn);
                tableLayout.setPadding(20,20,20,20);
                tableLayout.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);
                tableLayout.addView(tableRow1,0);
                tableLayout.addView(tableRow2,1);
                tableLayout.addView(tableRow3,2);
                tableLayout.addView(tableRow4,3);
                parentLayout= (TableLayout) findViewById(R.id.parentTableLayout);
                parentLayout.addView(tableLayout,i);
                i++;
                 /*
                 hndling the edit functionality for the edit
            * */
                editBtn.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // custom dialog
                                final Dialog dialog = new Dialog(LogEntryActivity.this);
                                dialog.setContentView(R.layout.addnew_journal);
                                dialog.setTitle("Edit Entry");
                                final EditText entryname= (EditText) dialog.findViewById(R.id.EntryName);
                                final Button addImageBtn= (Button) dialog.findViewById(R.id.AddImageBtn);
                                Button save= (Button) dialog.findViewById(R.id.addToDB);
                                image= (ImageView) dialog.findViewById(R.id.entry_image);
                                final EditText editdescription= (EditText) dialog.findViewById(R.id.entryDescription);
                                save.setText("Save");
                                entryname.setText(name);
                                editdescription.setText(description);
                                final Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                                if(bitmap != null){
                                    image.setImageBitmap(bitmap);
                                    image.setAdjustViewBounds(true);
                                }
                                 /**
                                  * get the image from the gallery
                                  * */
                                addImageBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(
                                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                                    }
                                });
                              /*
                              * save entry to the logbook*/
                                save.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ContentValues values = new ContentValues();
                                                values.put("EntryName",entryname.getText().toString());
                                                values.put("Description", editdescription.getText().toString());
                                                values.put("LogName",logName);
                                                values.put("ImagePath",picturePath);
                                                try {
         /*access the database*/
                                                    dataBaseHelper.createDataBase();
                                                    dataBaseHelper.openDataBase();
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                dataBaseHelper.Update("LogEntry",values,"EntryName=?",new String[]{name});
                                                dataBaseHelper.close();

                                            }
                                        }
                                );
                                dialog.show();
                            }
                        }

                );
                 /* delete entry from the logbook*/
                deleteBtn.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
         /*access the database*/
                                    dataBaseHelper.createDataBase();
                                    dataBaseHelper.openDataBase();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dataBaseHelper.delete("LogEntry","EntryName=?",new String[]{name});
                                dataBaseHelper.close();
                                if(parentLayout!=null){
                                    parentLayout.removeAllViews();
                                }
                                loadEntries();

                            }
                        }
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        dataBaseHelper.close();
    }

    /**
     * This is the method to add entry to the logbook
     */
    public  void addNewEntry(){
        addEntry= (Button) findViewById(R.id.addEntryBtn);
        addEntry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // custom dialog
                        final Dialog dialog = new Dialog(LogEntryActivity.this);
                        dialog.setContentView(R.layout.addnew_journal);
                        dialog.setTitle("Add New Entry");
                         /*
                         * access the layout parameters*/
                        final EditText entryname= (EditText) dialog.findViewById(R.id.EntryName);
                        final Button addImageBtn= (Button) dialog.findViewById(R.id.AddImageBtn);
                        Button addToDB= (Button) dialog.findViewById(R.id.addToDB);
                        image= (ImageView) dialog.findViewById(R.id.entry_image);
                        final EditText description= (EditText) dialog.findViewById(R.id.entryDescription);
                        addImageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(
                                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            }
                        });
                         /* add entries to the databse*/
                        addToDB.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ContentValues values = new ContentValues();
                                        values.put("EntryName",entryname.getText().toString());
                                        values.put("Description", description.getText().toString());
                                        values.put("LogName",logName);
                                        values.put("ImagePath",picturePath);
                                        addToDatabase(values);
                                        dialog.dismiss();
                                        if(parentLayout!=null){
                                            parentLayout.removeAllViews();
                                        }
                                        loadEntries();

                                    }
                                }
                        );

                        dialog.show();
                    }
                }
        );
    }
    /*query to access the database*/
    public void addToDatabase(ContentValues values){
        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataBaseHelper.insertInto("LogEntry", values);
        dataBaseHelper.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_entry, menu);
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
    /*method to get the image from gallery*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
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
