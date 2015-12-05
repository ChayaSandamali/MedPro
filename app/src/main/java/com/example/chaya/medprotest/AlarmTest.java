package com.example.chaya.medprotest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chaya on 8/14/2014.
 */
public class AlarmTest extends BroadcastReceiver {
    private  DataBaseHelper dataBaseHelper;
    int requestCode;
    @Override
     /*calls this method when the broadcast receiver is referenced at any time*/
    public void onReceive(Context context, Intent intent) {
      /*display the notification*/
        Toast.makeText(context,"Alarm test!!!", Toast.LENGTH_LONG).show();
      /*plays the alarm sound*/
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
        ringtone.play();
    }

}