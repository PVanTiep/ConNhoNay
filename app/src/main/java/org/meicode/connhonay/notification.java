package org.meicode.connhonay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.installations.Utils;

import java.util.Calendar;

public class notification extends AppCompatActivity {
    Button btn;
    NotificationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        btn = (Button) findViewById(R.id.button4);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel("My notification","My notification",NotificationManager.IMPORTANCE_DEFAULT);

            channel.setSound(alarmSound, audioAttributes);
            manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "This is notification example";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(notification.this,"My notification")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("New Notification")
                 .setShowWhen(true)
                .setWhen(Calendar.getInstance().getTimeInMillis()+100000)
                .setContentText(message)
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(alarmSound)
                .setAutoCancel(true);
                manager.notify(1,builder.build());
                Intent intent = new Intent(notification.this, input_time.class);
                //startActivity(intent);



            }
        });
    }
}