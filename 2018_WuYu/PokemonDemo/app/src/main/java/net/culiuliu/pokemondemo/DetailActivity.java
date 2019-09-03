package net.culiuliu.pokemondemo;

import android.app.FragmentManager;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Scanner;

public class DetailActivity extends AppCompatActivity {

    IntroFragment introFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        String tag = intent.getStringExtra("poke_name");

        introFragment = (IntroFragment) getFragmentManager().findFragmentById(R.id.intro_fragment);
        introFragment.set_select(tag);

        create_img(tag);
    }

    private void create_img(String tag) {
        ImageView imageView = (ImageView) findViewById(R.id.detail_img);

        int img_id = getResources().getIdentifier(
                tag, "drawable", getPackageName()
        );

        imageView.setImageResource(img_id);
    }
}
