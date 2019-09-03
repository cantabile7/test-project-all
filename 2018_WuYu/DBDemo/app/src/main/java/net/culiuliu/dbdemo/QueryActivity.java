package net.culiuliu.dbdemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Vector;

public class QueryActivity extends AppCompatActivity {

    private ListView entry_list;
    private Vector<String> entries;

    SQLiteDatabase db;
    String height_min;
    String height_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        entry_list = (ListView) findViewById(R.id.entry_list);
        entries = new Vector<String> ();

        Intent intent = getIntent();
        String height1 = intent.getStringExtra("height1");
        String height2 = intent.getStringExtra("height2");

        // create wehre clause

        height_min = height1.length() == 0 ? "0" :height1;
        height_max = height2.length() == 0 ? "300" : height2;

        query();
        create_list();

        entry_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                String[] parts = item.split(",");

                String height=parts[1];
                String whereclause = "height = ?" + " and name = ?";

                db.delete("STUDENT", whereclause, new String[] {parts[1], parts[0]});

                query();
                create_list();
            }
        });


    }

    private void query() {
        String whereclause = "height >= ?" + " and height <= ?";

        entries.clear();
        db  = MainActivity.getDB();

        Cursor cursor = db.query("STUDENT",
                null,
                whereclause,
                new String[] {height_min, height_max},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String height = cursor.getString(cursor.getColumnIndex("height"));
                entries.add("" + name + "," + height);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void create_list() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(QueryActivity.this, android.R.layout.simple_list_item_1, entries);
        entry_list.setAdapter(adapter);
    }
}
