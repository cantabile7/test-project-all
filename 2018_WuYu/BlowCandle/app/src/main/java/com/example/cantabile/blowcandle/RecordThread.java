package com.example.cantabile.blowcandle;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class RecordThread extends Thread {
    private AudioRecord mAudioRecord;
    private Handler mHandler;
    private int bufferSize;
    private ChangeState mCallback;
    private static final int BUFFER_SIZE = 8000;
    private static final int MIN_VALUE = 4000;

    public RecordThread(ChangeState callback) {

        mCallback = callback;
        Looper looper = Looper.getMainLooper();
        mHandler = new Handler(looper);
        //获取最小缓冲区
        bufferSize = AudioRecord.getMinBufferSize(
                BUFFER_SIZE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (mAudioRecord == null) {
                //Log.d("TAG","mAudioRecord为空，此时新建");
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                        AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);
                //Log.d("TAG","mAudioRecord新建之后才有");
            }

        }

        @Override
        public void run() {
        // TODO Auto-generated method stub
        super.run();
        mAudioRecord.startRecording();
        byte[] buffer = new byte[bufferSize];
        while(MainActivity.isRunning) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int length = mAudioRecord.read(buffer, 0, bufferSize) + 1;
            int av = 0;
            for(int i = 0; i < buffer.length; i++) {
                av += (buffer[i] * buffer[i]);
            }
            av = Integer.valueOf(av / (int) length);
            if(av > MIN_VALUE) {
                //mHandler.sendEmptyMessage(1);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mCallback.change();
                    }
                });
            }
        }
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord=null;
    }


    public interface ChangeState {
        public void change();
    }
}
