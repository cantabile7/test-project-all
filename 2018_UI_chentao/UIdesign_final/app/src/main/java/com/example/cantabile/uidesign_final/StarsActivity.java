package com.example.cantabile.uidesign_final;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class StarsActivity extends Activity {
    private String[] contents={"No.1     Godv","No.2     imp","No.3     Pyl","No.4     Acorn","No.5     TBQ"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stars_activity);
        Button bt = (Button) findViewById(R.id.quit);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StarsActivity.this,R.layout.stars_list_text,contents);
        ListView listView = (ListView) findViewById(R.id.rank_list);
        listView.setAdapter(adapter);
        Typeface tp = Typeface.createFromAsset(this.getAssets(), "main.ttf");
        bt.setTypeface(tp);
        bt.setTextSize(30);
    }
    public void onQuit(View v){
        Log.d("MyTAG","onQuit");
        MediaPlayer mp=MediaPlayer.create(this,R.raw.audio_quit);
        mp.start();
        this.finish();
    }
}
