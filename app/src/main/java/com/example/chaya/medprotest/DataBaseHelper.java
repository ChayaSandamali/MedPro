package com.example.chaya.medprotest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaya on 8/6/2014.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    /*The Android's default system path of  application database.*/
    private static String DB_PATH = "/data/data/com.example.chaya.medprotest/databases/";
    private static String DB_NAME = "medPro";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    /*Creates a empty database on the system and rewrites it with the database. */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
     /*do nothing - database already exist*/
        } else {
    /*By calling this method and empty database will be created into the default system path
           of the application,then it will be able to overwrite that database with the database.*/
            this.getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
               throw new Error("Error copying database");
            }
        }
    }
    /*
      Check if the database already exist to avoid re-copying the file each time open the application.
      @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbFile=new File(myPath);
            return dbFile.exists();
           // checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
           //database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    /**
     * Copies  database from local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        //Open local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
    public void insertInto(String tableName,ContentValues content) {
        SQLiteDatabase database=this.getWritableDatabase();
        database.insert(tableName,null,content);
    }
    /*search data from the database*/
    public void Update(String tableName,ContentValues content,String whereClause,String[] whereArgs) {
        SQLiteDatabase database=this.getWritableDatabase();
        database.update(tableName,content,whereClause,whereArgs);
    }
    public Cursor readFrom(String selectQuery) {
       // String selectQuery = "SELECT * FROM Drug";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        return cursor;
    }
    public void delete(String tableName,String whereClause,String[] whereArgs){
        SQLiteDatabase database=this.getWritableDatabase();
        database.delete(tableName,whereClause,whereArgs);
    }
}
