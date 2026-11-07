package com.takaobrog.crudapponlyactivity.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.takaobrog.crudapponlyactivity.R;
import com.takaobrog.crudapponlyactivity.connection.ServerConnection;
import com.takaobrog.crudapponlyactivity.util.Data;

public class DetailActivity extends AppCompatActivity {

    public static final String DATA_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView title = findViewById(R.id.detail_title);
        TextView memo = findViewById(R.id.detail_memo);
        int id = getIntent().getIntExtra(DATA_ID, 0);
        ServerConnection conn = new ServerConnection();
        new Thread(() -> {
            Data data = conn.getData(id);
            title.post(() -> {
                title.setText(data.getTitle());
            });
            memo.post(() -> {
                memo.setText(data.getMemo());
            });
        }).start();
    }

    public void onClickBack(View view) {
        finish();
    }
}