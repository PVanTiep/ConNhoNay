package org.meicode.connhonay;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LocalService extends Service {

    MediaPlayer mMediaPlayer;
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
    }
    public void startMp3Player() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ting);
        mMediaPlayer.start();

        int count = 0; // initialise outside listener to prevent looping

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 3;
            int count = 0; // initialise outside listener to prevent looping
            @Override
            public void onCompletion(MediaPlayer mMediaPlayer) {
                if (count < maxCount) {
                    count++;
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();
                }
            }
        });
    }
    public void mp3Stop() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
    public void mp3Pause() {
        mMediaPlayer.pause();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mp3Stop();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}

