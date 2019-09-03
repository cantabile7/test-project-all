package com.example.cantabile.flagviewer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Scanner;

public class ShowDetail extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Bundle bundle=getIntent().getExtras();
        int id=bundle.getInt("skill_photo");
        int id2=bundle.getInt("skill_detail");
        //String message=bundle.getString("skill_detail");
        ImageView Iv=(ImageView) findViewById(R.id.detail_iv);
        Iv.setImageResource(id);
        TextView tv=(TextView) findViewById(R.id.detail_tv);

        Scanner scanner = new Scanner(getResources().openRawResource(id2));
        String line = "";
        while (scanner.hasNext()) {
            line += scanner.nextLine();
        }
        tv.setText(line);

    }

}
