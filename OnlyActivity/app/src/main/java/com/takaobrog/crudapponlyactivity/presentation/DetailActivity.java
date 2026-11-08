package com.takaobrog.crudapponlyactivity.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.takaobrog.crudapponlyactivity.util.Logging;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String DATA_ID = "id";
    private int id;
    private AlertDialog dialog;
    private final ServerConnection conn = new ServerConnection();
    private Button backBtn;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logging.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getIntExtra(DATA_ID, 0);
        TextView title = findViewById(R.id.detail_title);
        TextView memo = findViewById(R.id.detail_memo);
        backBtn = findViewById(R.id.back_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        showLoadingDialog();
        new Thread(() -> {
            Data data = conn.getData(id);
            title.post(() -> {
                title.setText(data.getTitle());
            });
            memo.post(() -> {
                memo.setText(data.getMemo());
            });
            dismissLoadingDialog();
        }).start();
    }

    @Override
    protected void onResume() {
        Logging.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Logging.d(TAG, "onDestroy");
        super.onDestroy();
        dismissLoadingDialog();
    }

    public void onClickBack(View view) {
        buttonSetting();
        finish();
    }

    public void onClickDelete(View view) {
        Logging.d(TAG, "delete id: " + id);
        buttonSetting();
        finish();
        new Thread(() -> conn.delete(id)).start();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("お待ちください")
                .create();
        dialog.show();
    }

    private void dismissLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void buttonSetting() {
        backBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }
}