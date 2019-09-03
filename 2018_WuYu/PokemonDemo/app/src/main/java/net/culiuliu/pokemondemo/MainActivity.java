package net.culiuliu.pokemondemo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    IntroFragment intro_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        intro_fragment = (IntroFragment) fragmentManager.findFragmentById(R.id.intro_fragment);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            intro_fragment.set_select("blastoise");
        }

    }


    public void btn_clicked(View view) {
        String tag = view.getTag().toString();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("poke_name", tag);
            startActivity(intent);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            intro_fragment.set_select(tag);
        }
    }
}
