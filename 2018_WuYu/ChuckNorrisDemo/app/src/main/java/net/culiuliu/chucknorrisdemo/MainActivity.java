package net.culiuliu.chucknorrisdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static String URLSTR = "http://thecatapi.com/api/images/vote?api_key=xxxxx&image_id=BC24&score=8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {
        Button button  = (Button) findViewById(R.id.joke_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetchData(URLSTR);
                fetchDataIon(URLSTR);
            }
        });
    }

    private void fetchData(final String urlString) {
        Thread thread = new Thread (new Runnable() {
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream input = conn.getInputStream();
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            int ch = input.read();
                            if (ch == -1)
                                break;
                            sb.append((char) ch);
                        }
                        String text = sb.toString();
                        processJsonData(text);
                    } else {
                        Log.d("url", "HTTP fail, code " + responseCode);
                    }
                } catch (IOException ioe) {
                    Log.wtf("url", ioe);
                }
            }
        });
        thread.start();
    }

    private void fetchDataIon (final String urlString) {
        Ion.with(this)
                .load(urlString)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processJsonData(result);
                    }
                });
    }

    private void processJsonData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONObject value = json.getJSONObject("value");
            final String joke = value.getString("joke");
            updateTextView(joke);

        } catch (JSONException e) {
            Log.wtf("json", e);
        }
    }

    private void updateTextView(final String joke) {
        final TextView tv = (TextView) findViewById(R.id.joke_text);
        final ImageView iv = (ImageView) findViewById(R.id.img);
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(joke);
            }
        });
        iv.post(new Runnable() {
            @Override
            public void run() {
                //iv.setImageURI();
            }
        });
    }
}
