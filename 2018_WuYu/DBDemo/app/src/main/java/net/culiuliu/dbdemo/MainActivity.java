package net.culiuliu.dbdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static SimpleDBHelper dbHelper;

    Button queryBtn;
    Button create_db_btn;
    EditText query_height_left;
    EditText query_height_right;
    EditText query_username;
    Button insertBtn;
    EditText insert_username;
    EditText insert_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SimpleDBHelper(this, 3); // too ugly
        queryBtn = (Button) findViewById(R.id.query_btn);
        create_db_btn = (Button) findViewById(R.id.create_db_btn);
        query_height_left = (EditText) findViewById(R.id.query_height_1);
        query_height_right = (EditText) findViewById(R.id.query_height_2);
        insertBtn = (Button) findViewById(R.id.insert_btn);
        insert_username = (EditText) findViewById(R.id.insert_username);
        insert_height = (EditText) findViewById(R.id.insert_height);

        initiliaze();

    }

    private void initiliaze() {
        create_db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.getWritableDatabase();
            }
        });

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = getDB();
                ContentValues values = new ContentValues();
                values.put("name", insert_username.getText().toString());
                values.put("height", Integer.parseInt(insert_height.getText().toString()));
                db.insert("STUDENT", null, values);
            }
        });

        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                intent.putExtra("height1", query_height_left.getText().toString());
                intent.putExtra("height2", query_height_right.getText().toString());
                startActivity(intent);
            }
        });
    }

    public static SQLiteDatabase getDB() {
        return dbHelper.getWritableDatabase();
    }
}
