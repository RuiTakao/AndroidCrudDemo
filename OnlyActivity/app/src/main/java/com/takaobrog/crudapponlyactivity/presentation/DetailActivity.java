package com.takaobrog.crudapponlyactivity.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.takaobrog.crudapponlyactivity.R;
import com.takaobrog.crudapponlyactivity.connection.ServerConnection;
import com.takaobrog.crudapponlyactivity.util.Data;

public class DetailActivity extends AppCompatActivity {

    public static final String DATA_ID = "id";
    private TextView title;
    private TextView memo;
    private int id;
    private AlertDialog dialog;

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

        id = getIntent().getIntExtra(DATA_ID, 0);
        title = findViewById(R.id.detail_title);
        memo = findViewById(R.id.detail_memo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("お待ちください");
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerConnection conn = new ServerConnection();
        new Thread(() -> {
            Data data = conn.getData(id);
            title.post(() -> {
                title.setText(data.getTitle());
            });
            memo.post(() -> {
                memo.setText(data.getMemo());
            });
            dialog.dismiss();
        }).start();
    }

    public void onClickBack(View view) {
        finish();
    }
}