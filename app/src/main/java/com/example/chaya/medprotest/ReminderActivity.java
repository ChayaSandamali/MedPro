package com.example.chaya.medprotest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chaya.medprotest.R;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

public class ReminderActivity extends Activity {
    private AlarmTest alarm;
    private Button cancelBtn;
    private Button ViewBtn;
    private Button AddAlarmBtn;
    private DatePicker datepick;
    private TimePicker timepick;
    private TextView alarmtitle;
    private EditText alarmDescription;
    private Spinner alarmtype;
    String selectedAlarmtype;
    Calendar cal;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
     DataBaseHelper dataBaseHelper;
    int alarmId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        dataBaseHelper = new DataBaseHelper(this);
//        try {
//         /*access the database*/
//            dataBaseHelper.createDataBase();
//            dataBaseHelper.openDataBase();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Cursor cursor=dataBaseHelper.readFrom("SELECT * FROM Alarm");
//        if (cursor.moveToFirst()) {
//            do {
//                Log.e("a",cursor.getString(1));
//                Log.e("a",cursor.getString(2));
//                Log.e("a",cursor.getString(3));
//                Log.e("a",cursor.getString(4));
//                Log.e("a",cursor.getString(5));
//            } while (cursor.moveToNext());
//        }
//        dataBaseHelper.close();
    /*get element identities from the deisgn*/
       // cancelBtn = (Button) findViewById(R.id.CancelBtn);
        AddAlarmBtn = (Button) findViewById(R.id.AddAlarmBtn);
        datepick = (DatePicker) findViewById(R.id.datePicker);
        timepick = (TimePicker) findViewById(R.id.timePicker);
        alarmtitle = (TextView) findViewById(R.id.ReminderName);
        alarmDescription = (EditText) findViewById(R.id.ReminderDescription);
        alarmtype=(Spinner) findViewById(R.id.spinnertype);
        ViewBtn=(Button) findViewById(R.id.ViewBtn);
        alarmMgr = (AlarmManager)ReminderActivity.this.getSystemService(Context.ALARM_SERVICE);
       final Intent intent = new Intent(ReminderActivity.this, AlarmTest.class);

       /*event handlers for button clich actions*/
//        cancelBtn.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                }
//        );
        AddAlarmBtn.setOnClickListener(
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
          /* search results from the database and get the whole result set object*/
                        String selectQuery = "SELECT AlarmID FROM Alarm WHERE AlarmID=(SELECT MAX(AlarmID) FROM Alarm)";
                        Cursor cursor = dataBaseHelper.readFrom(selectQuery);
        /* iterate through the resultset*/
                        if (cursor.moveToFirst()) {
                            do {
                               // Log.e("gggggg",cursor.getString(0));
                                alarmId=cursor.getInt(0)+1;
                            } while (cursor.moveToNext());
                        }
        /* close the database connection
         and the search resultset object*/
                        cursor.close();
                        dataBaseHelper.close();

                        alarmIntent = PendingIntent.getBroadcast(ReminderActivity.this,alarmId, intent, 0);
                        //intent.putExtra("requestCode",alarmId);
                        selectedAlarmtype=alarmtype.getSelectedItem().toString();
                        /*create a calendar instance to get the user input*/
                        cal = Calendar.getInstance();
      /*get the time and the date*/
                        cal.set(Calendar.DAY_OF_MONTH, datepick.getDayOfMonth());
                        cal.set(Calendar.MONTH, datepick.getMonth());
                        cal.set(Calendar.YEAR, datepick.getYear());
                        cal.set(Calendar.HOUR_OF_DAY, timepick.getCurrentHour());
                        cal.set(Calendar.MINUTE, timepick.getCurrentMinute());
                        cal.set(Calendar.SECOND, 0);
                        if(selectedAlarmtype.equals("One Time")){
                            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
                        }else if(selectedAlarmtype.equals("Repeat")){
                            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                    1000 * 60 *60*24, alarmIntent);
                        }
                        addToDatabase();
                    }
                }
        );
        ViewBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // custom dialog
                        final Dialog parentdialog = new Dialog(ReminderActivity.this);
                        parentdialog.setContentView(R.layout.reminder_dialog);
                        parentdialog.setTitle("Added Reminder List");
                        parentdialog.show();
                        try {
         /*access the database*/
                            dataBaseHelper.createDataBase();
                            dataBaseHelper.openDataBase();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Cursor cursor=dataBaseHelper.readFrom("SELECT * FROM Alarm");
                        TableLayout tableLayout= (TableLayout) parentdialog.findViewById(R.id.dialog_table);
                       int i=1;
                        if (cursor.moveToFirst()) {
                            do {
                                final TableRow tableRow=new TableRow(ReminderActivity.this);
                                tableRow.setBackgroundColor(Color.WHITE);
                                tableRow.setPadding(5,5,5,5);
                                final TextView name=new TextView(ReminderActivity.this);
                                TextView description=new TextView(ReminderActivity.this);
                                TextView type=new TextView(ReminderActivity.this);
                                TextView date=new TextView(ReminderActivity.this);
                                TextView time=new TextView(ReminderActivity.this);
                                TableRow.LayoutParams lp=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,0.2f);
                                name.setLayoutParams(lp);
                                description.setLayoutParams(lp);
                                type.setLayoutParams(lp);
                                date.setLayoutParams(lp);
                                time.setLayoutParams(lp);
                                name.setGravity(Gravity.CENTER);
                                description.setGravity(Gravity.CENTER);
                                type.setGravity(Gravity.CENTER);
                                date.setGravity(Gravity.CENTER);
                                time.setGravity(Gravity.CENTER);
                                name.setText(cursor.getString(1));
                                description.setText(cursor.getString(2));
                                type.setText(cursor.getString(3));
                                date.setText(cursor.getString(4));
                                time.setText(cursor.getString(5));
                                tableRow.addView(name);
                                tableRow.addView(description);
                                tableRow.addView(type);
                                tableRow.addView(date);
                                tableRow.addView(time);
                                tableRow.setId(alarmId);
                                tableLayout.addView(tableRow,i);
                                i++;
                                tableRow.setClickable(true);
                                tableRow.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(final View tableview) {
                                        tableview.setBackgroundColor(Color.RED);
                                        final Dialog dialog = new Dialog(ReminderActivity.this);
                                        dialog.setContentView(R.layout.confirm_delete);
                                        dialog.show();
                                        final Button okBtn= (Button) dialog.findViewById(R.id.OKbtn);
                                        final Button cancelBtn= (Button) dialog.findViewById(R.id.CancelBtn);

                                        okBtn.setOnClickListener(new View.OnClickListener() {
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
                                                TableRow t = (TableRow) tableview;
                                                TextView deleteView = (TextView) t.getChildAt(0);
                                                String deleteText = deleteView.getText().toString();
                                                dataBaseHelper.delete("Alarm","Title=?",new String[]{deleteText});
                                                dataBaseHelper.close();
                                                alarmIntent = PendingIntent.getBroadcast(ReminderActivity.this,tableview.getId(), intent, 0);
                                                alarmMgr.cancel(alarmIntent);
                                                dialog.dismiss();
                                                parentdialog.dismiss();

                                            }
                                        });
                                        cancelBtn.setOnClickListener(
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();

                                                    }
                                                }
                                        );
                                    }
                                });

                            } while (cursor.moveToNext());
                        }
                        dataBaseHelper.close();

                    }
                }
        );
    }
    public void addToDatabase(){
        int day=datepick.getDayOfMonth();
        int month=datepick.getMonth();
        int year=datepick.getYear();
        int hour=timepick.getCurrentHour();
        int minute=timepick.getCurrentMinute();
        ContentValues values=new ContentValues();
        values.put("Title",alarmtitle.getText().toString());
        values.put("Description",alarmDescription.getText().toString());
        values.put("Type", alarmtype.getSelectedItem().toString());
        values.put("Date",year+"/"+month+"/"+day);
        values.put("Time",hour+"-"+minute);

        try {
         /*access the database*/
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataBaseHelper.insertInto("Alarm",values);
        dataBaseHelper.close();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder, menu);
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
