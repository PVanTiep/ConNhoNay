package org.meicode.connhonay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class TimeCoutDown extends AppCompatActivity {
    private static  long START_TIME_IN_MILLIS = 60000;
    private boolean mIsBound;
    private TextView mTextViewCountDown, tv_name;
    private Button mButtonStartPause;
    private Button mButtonReset, mButtonFinish;
    private LocalService mBoundService;
    private CountDownTimer mCountDownTimer;
    private int count=0, countSum=0;
    private boolean mTimerRunning,kt;
    private ProgressBar pb;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long mTimeRelax;
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder builder, builder1,builder2;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    String uid,Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_cout_down);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        int time = intent.getIntExtra("time", 0);
        int time1 = intent.getIntExtra("time1", 0);
        Name = intent.getStringExtra("name");
        mTimeLeftInMillis = time*60000;
        mTimeRelax = 5*60000;
        kt=false;
        START_TIME_IN_MILLIS=mTimeLeftInMillis;
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        tv_name=findViewById(R.id.textView2);
        tv_name.setText(Name);
        pb = findViewById(R.id.progressBar);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonFinish = findViewById(R.id.button_finish);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://auth-4a763-default-rtdb.firebaseio.com/");
        reference = database.getReference("Users");
        uid = mAuth.getUid();
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        kt=false;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("My notification","My notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        String message = "Bạn có 5 phút để giải lao";
        builder = new NotificationCompat.Builder(TimeCoutDown.this,"My notification")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Đã đến thời gian nghỉ")
                .setContentText(message)
                .setSound(alarmSound)
                .setAutoCancel(true);

        String message1 = "Chúc mừng bạn đã hoàn thành";
        builder1 = new NotificationCompat.Builder(TimeCoutDown.this,"My notification")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Hết thời gian")
                .setContentText(message1)
                .setSound(alarmSound)
                .setAutoCancel(true);

        String message2 = "Quay lại làm việc thôi";
        builder2 = new NotificationCompat.Builder(TimeCoutDown.this,"My notification")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Hết thời gian nghỉ")
                .setContentText(message2)
                .setSound(alarmSound)
                .setAutoCancel(true);
        managerCompat = NotificationManagerCompat.from(TimeCoutDown.this);
        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                mCountDownTimer.onFinish();
            }
        });
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                count++;
                if( count%10==0 && kt==false){
                    managerCompat.notify(count,builder.build());
                    countSum+=count;
                    count=0;
                    kt=true;
                }else
                if(kt==true && count%(20)==0){
                    managerCompat.notify(count,builder2.build());
                    countSum+=count;
                    count=0;
                    kt=false;
                }
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                managerCompat.notify(count,builder1.build());
                mTimerRunning = false;
                count+=countSum;
                int c= (int) (count*100000 / START_TIME_IN_MILLIS);
                reference.child(uid).child("FocusTask").child(Name).child("Hoàn thành được").setValue(c+"%");
                Intent intent = new Intent(TimeCoutDown.this,input_time.class);
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                startActivity(intent);
            }
        }.start();



        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int hours = (int) ((mTimeLeftInMillis / 1000) / 60 )/60;
        int minutes = (int) ((mTimeLeftInMillis / 1000) / 60) %60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

}