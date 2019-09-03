package com.example.administrator.pagerviewtest.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pagerviewtest.MainActivity;
import com.example.administrator.pagerviewtest.R;
import com.example.administrator.pagerviewtest.Services.RecordingService;
import com.example.administrator.pagerviewtest.Volume;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.example.administrator.pagerviewtest.MainActivity.FOLDER_NAME;

public class RecordFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private final int REQUEST_CODE = 1000;

    private MainActivity mainActivity = (MainActivity) getActivity();

    public static Chronometer mChronometer;
    private FloatingActionButton mRecordButton;
    private ImageButton mPauseButton;
    private TextView mRecordStatus;

    private Handler mHandler = new Handler();
    private List<String> mPermissionList;
    private ProgressBar mProgressBar;
    private ValueAnimator mProgressAnimator;

    private boolean isRecording = false;
    private boolean isPause = false;
    private int position;
    public static long pauseTime = 0;
    public static long sumpauseTime=0;
    public static long pausingTime=0;
    public static long resumeTime=0;
    private int mRecordCounter = 0;

    private Vibrator mVibrator=null;  //声明一个振动器对象
    private TextView dbs;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressAnimator = ValueAnimator.ofInt(0, mProgressBar.getMax());
            mProgressAnimator.setDuration(1000);
            mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animProgress = (Integer) animation.getAnimatedValue();
                    mProgressBar.setProgress(animProgress);
                }
            });

            mProgressAnimator.start();
            updateProgressBar();
        }
    };

    private void updateProgressBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    public RecordFragment() {
    }

    public static RecordFragment newInstance(int position) {
        RecordFragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);

        mRecordStatus = (TextView) view.findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) view.findViewById(R.id.fab_record);

        mVibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
        dbs = (TextView) view.findViewById(R.id.db_tv);

        //开始录音按键
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Volume.isVibrate){
                    mVibrator.vibrate(new long[]{0,100}, -1);
                }
                if (checkPermission()) {
                    onRecord(isRecording);
                    isRecording = !isRecording;
                } else {
                    requestPermission();
                }
            }
        });

        mPauseButton = (ImageButton) view.findViewById(R.id.btn_pause);
        mPauseButton.setVisibility(View.GONE);
        //暂停录音按键
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordStatus.setText("Paused.");
                    onPauseRecord(isPause);
                    isPause = !isPause;
            }
        });

        return view;
    }


    private boolean checkPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};
        mPermissionList = new ArrayList<>();
        boolean hasPermission = true;

        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
                hasPermission = false;
            }
        }

        return hasPermission;
    }

    private void requestPermission() {
//        ActivityCompat.requestPermissions(mainActivity,
//                mPermissionList.toArray(new String[mPermissionList.size()]), REQUEST_CODE);
        RecordFragment.this.requestPermissions(mPermissionList.toArray(
                new String[mPermissionList.size()]), REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                onRecord(isRecording);
                isRecording = !isRecording;
                break;
            default:
                Toast.makeText(getActivity(), "Permission Denied",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onRecord(boolean isRecording) {

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (!isRecording) {
            mRecordButton.setImageResource(R.drawable.ic_stop_white_24dp);
            mPauseButton.setVisibility(View.VISIBLE);
            dbs.setVisibility(View.VISIBLE);
            File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
                if (!folder.exists()) {
                    folder.mkdir();
                }
            //Toast.makeText(getContext(),"开始录音：isRecording=true",Toast.LENGTH_SHORT).show();

            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setCountDown(false);
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordCounter == 0) {
                        mRecordStatus.setText("Recording.");
                        updateMicStatus();
                    } else if (mRecordCounter == 1) {
                        mRecordStatus.setText("Recording..");
                        updateMicStatus();
                    } else if (mRecordCounter == 2) {
                        mRecordStatus.setText("Recording...");
                        mRecordCounter = -1;
                        updateMicStatus();
                    }
                    mRecordCounter++;
                }
            });

            mRecordStatus.setText("Recording.");
            mRecordCounter++;

            getActivity().startService(intent);
            // 保持屏幕常亮
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            if(isPause){
                onPauseRecord(isPause);
                isPause=!isPause;
            }
            mRecordButton.setImageResource(R.drawable.ic_mic_white_24dp);
            mPauseButton.setVisibility(View.INVISIBLE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mRecordStatus.setText("Tap the button to start recording");
            //pauseTime=0;

            getActivity().stopService(intent);
            // 取消屏幕常亮
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dbs.setVisibility(View.INVISIBLE);
        }
    }

    private void onPauseRecord(boolean isPause) {
        if (!isPause) {
            //如果录音中，则暂停
            pauseTime = mChronometer.getBase()-SystemClock.elapsedRealtime();
            pausingTime=System.currentTimeMillis();
            mChronometer.stop();
            mPauseButton.setImageResource(R.drawable.resume);
            //Toast.makeText(getContext(),"pauseTime:"+pauseTime,Toast.LENGTH_SHORT).show();
            RecordingService.pauseRecording();
            dbs.setVisibility(View.INVISIBLE);
        } else {
            //如果不是录音中，则继续
            mPauseButton.setImageResource(R.drawable.pause);
            mChronometer.setBase(SystemClock.elapsedRealtime() + pauseTime);
            //获得总暂停时间
            sumpauseTime+=(pausingTime-System.currentTimeMillis());
            mRecordCounter=0;
            //Toast.makeText(getContext(),"继续录音！！！！"+sumpauseTime,Toast.LENGTH_SHORT).show();
            mChronometer.start();
            RecordingService.resumeRecording();
            dbs.setVisibility(View.VISIBLE);
        }
    }


    //获取分贝值
    private final Handler mmHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 1;
    private int SPACE = 500;// 间隔取样时间

    private void updateMicStatus() {
        if (RecordingService.mMediaRecorder != null) {
            double ratio = (double)RecordingService.mMediaRecorder.getMaxAmplitude() /BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            Log.d(TAG,"分贝值："+db);
            java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.00");
            String str = myformat.format(db);

            mmHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
            //Toast.makeText(getContext(),"当前分贝："+db,Toast.LENGTH_SHORT).show();
            dbs.setText("当前分贝："+str);
        }


    }



}
