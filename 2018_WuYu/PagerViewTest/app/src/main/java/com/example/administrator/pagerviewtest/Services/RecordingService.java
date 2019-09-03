package com.example.administrator.pagerviewtest.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.administrator.pagerviewtest.DBHelper;
import com.example.administrator.pagerviewtest.MainActivity;
import com.example.administrator.pagerviewtest.R;
import com.example.administrator.pagerviewtest.fragments.RecordFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.administrator.pagerviewtest.MainActivity.FOLDER_NAME;
import static com.example.administrator.pagerviewtest.MainActivity.FOLDER_PATH;

public class RecordingService extends Service {

    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Date date;
    private DBHelper mDBHelper;
    public static MediaRecorder mMediaRecorder;
    private OnTimeChangedListener onTimeChangedListener;
    private SimpleDateFormat formatter;
    private String mFileName;
    private String mFilePath;
    private Timer mTimer;
    private TimerTask mIncrementTimerTask;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;



    public interface OnTimeChangedListener {
        void onTimeChanged(int seconds);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDBHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaRecorder != null) {
            stopRecording();
        }

    }

    private void startRecording() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        date = new Date();

        mFileName = formatter.format(date) + ".3gp";
        mFilePath = FOLDER_PATH  +"/" + mFileName;

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(mFilePath);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
            Toast.makeText(this, "Start Recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        mMediaRecorder.stop();
        //mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mElapsedMillis = ((System.currentTimeMillis() - mStartingTimeMillis)+RecordFragment.sumpauseTime);
        RecordFragment.pauseTime=0;
        RecordFragment.sumpauseTime=0;
        Toast.makeText(this, "Stop Recording", Toast.LENGTH_SHORT).show();
            mMediaRecorder.release();

            if (mIncrementTimerTask != null) {
                mIncrementTimerTask.cancel();
                mIncrementTimerTask = null;
        }

        mMediaRecorder = null;

        mDBHelper.addItem(mFileName, mFilePath, mElapsedMillis);
    }

    public static void pauseRecording(){
        mMediaRecorder.pause();
    }

    public static void resumeRecording(){
        mMediaRecorder.resume();
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;

                if (onTimeChangedListener != null) {
                    onTimeChangedListener.onTimeChanged(mElapsedSeconds);
                }

                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                "my_channel")
                .setSmallIcon(R.drawable.ic_mic_white_24dp)
                .setContentTitle("Recording...")
                .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                // 常驻通知栏
                .setOngoing(true);
        builder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return builder.build();
    }
}
