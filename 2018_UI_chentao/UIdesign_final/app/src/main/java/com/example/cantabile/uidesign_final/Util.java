package com.example.cantabile.uidesign_final;

import android.content.Context;
import android.media.MediaPlayer;

public class Util {
    private static MediaPlayer mp=null;
    public static void clikeAudioNormal(Context context){
        if(mp==null){
            mp=MediaPlayer.create(context,R.raw.audio_click);
        }
        mp.start();
    }
}
