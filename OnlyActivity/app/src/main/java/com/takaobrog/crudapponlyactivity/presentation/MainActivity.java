package com.takaobrog.crudapponlyactivity.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.takaobrog.crudapponlyactivity.connection.ServerConnection;
import com.takaobrog.crudapponlyactivity.util.Data;
import com.takaobrog.crudapponlyactivity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String LIST_ID = "id";
    private static final String LIST_TITLE = "title";
    private static final String LIST_MEMO = "memo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.menu_item_list);
        List<Map<String, Object>> list = new ArrayList<>();
        ServerConnection conn = new ServerConnection();
        new Thread(() -> {
            for (Data data : conn.getData()) {
                Map<String, Object> item = new HashMap<>();
                item.put(LIST_ID, data.getId());
                item.put(LIST_TITLE, data.getTitle());
                item.put(LIST_MEMO, data.getMemo());
                list.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    list,
                    R.layout.list_rows,
                    new String[]{LIST_TITLE, LIST_MEMO},
                    new int[]{R.id.list_title, R.id.list_menu}
            );
            listView.post(() -> {
                listView.setAdapter(adapter);
            });
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Map<String, Integer> item;
                item = (Map<String, Integer>) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.DATA_ID, item.get(LIST_ID));
                startActivity(intent);
            }
        });
    }
}