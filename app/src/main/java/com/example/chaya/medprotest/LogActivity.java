package com.example.chaya.medprotest;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chaya.medprotest.R;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class LogActivity extends ActionBarActivity {
    ArrayList<String> names = new ArrayList<String>();
    TextView searchLog;
    private Button addNew;
    private Button cancelBtn;
    EditText editText;
     private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        /*get button identifier*/
        addNew= (Button) findViewById(R.id.newLogBtn);
        /*initiate the database instance*/
        dataBaseHelper = new DataBaseHelper(this);
        /*Initiate a customlist to display the drugs*/
        final LogCustomList customList = new LogCustomList(this,names);

        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
          /* search results from the database and get the whole result set object*/
        String selectQuery = "SELECT LogName FROM LogBook";
        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /* iterate through the resultset*/
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        /* close the database connection
         and the search resultset object*/
        cursor.close();
        dataBaseHelper.close();
        ///////view entered data
        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* select entered logs from the database
        * */
         cursor=dataBaseHelper.readFrom("SELECT * FROM LogBook");
        /* iterate through the search result
        * */
        if (cursor.moveToFirst()) {
            do {
                Log.e("a",cursor.getString(1));
            } while (cursor.moveToNext());
        }
        dataBaseHelper.close();
        searchLog= (TextView) findViewById(R.id.searchLog);
        searchLog.addTextChangedListener(new TextWatcher() {
            /* getting the user inputs for implementing the search
            * */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                names.removeAll(names);
                try {
         /*access the database*/
                    dataBaseHelper.createDataBase();
                    dataBaseHelper.openDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /* implementing the search for logbook
               * */
                String selectQuery = "SELECT LogName FROM LogBook WHERE LogName LIKE '" + s + "%'";
                Cursor cursor = dataBaseHelper.readFrom(selectQuery);
                if (cursor.moveToFirst()) {
                    do {
                        names.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dataBaseHelper.close();
                /* notify the changes in search bar
                * */
                customList.notifyDataSetChanged();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        dataBaseHelper.close();

        /*get the list view identifier from design*/
        ListView listView = (ListView) findViewById(R.id.logList);
        /*set the custom adapter for the list to customize display*/
        listView.setAdapter(customList);
        /*adding the functionality to list click event*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
         /*create a container to pass information to the next view*/
                Intent intent = new Intent(LogActivity.this,LogEntryActivity.class);
         /*get the index of the selected item from the list*/
                intent.putExtra("LOG_NAME", names.get(+i));
                startActivity(intent);
            }
        });
        /* button event listner for add new entry
        * */
        addNew.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // custom dialog view
                        final Dialog dialog = new Dialog(LogActivity.this);
                        dialog.setContentView(R.layout.new_log);
                        dialog.setTitle("Create a new Log");
                        Button add= (Button) dialog.findViewById(R.id.AddLog);
                        cancelBtn= (Button) dialog.findViewById(R.id.CancelLog);
                        editText= (EditText) dialog.findViewById(R.id.LogName);
                        /* add new entry to database
                         * */
                        add.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        addToDatabase();
                                    }
                                }
                        );
                        /* return to the search journal database
                        * */
                        cancelBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        dialog.dismiss();
                                    }
                                }
                        );
                        dialog.show();
                    }
                }
        );


    }
    /* add logs to database
        * */
    public void addToDatabase(){

        ContentValues values=new ContentValues();
        values.put("LogName", editText.getText().toString());
        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataBaseHelper.insertInto("LogBook",values);
        dataBaseHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log, menu);
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
