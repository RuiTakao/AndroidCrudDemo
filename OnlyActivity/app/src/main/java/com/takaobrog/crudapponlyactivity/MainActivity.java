package com.takaobrog.crudapponlyactivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        List<Map<String, String>> list = new ArrayList<>();
        new Thread(() -> {
            for (Data data : getData()) {
                Map<String, String> item = new HashMap<>();
                item.put("title", data.title);
                item.put("memo", data.memo);
                list.add(item);
            }
            SimpleAdapter adapter =  new SimpleAdapter(
                    this,
                    list,
                    R.layout.list_rows,
                    new String[]{"title", "memo"},
                    new int[]{R.id.list_title, R.id.list_menu}
            );
            ListView listView = findViewById(R.id.menu_item_list);
            listView.post(() -> {
                listView.setAdapter(adapter);
            });
        }).start();
    }

    private Data[] getData() {
        Gson gson = new Gson();
        Data[] data = new Data[0];
        try {
            URL url = new URL("http://10.0.2.2:8765/todos/todo_list/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                data = gson.fromJson(line, Data[].class);
            }
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}