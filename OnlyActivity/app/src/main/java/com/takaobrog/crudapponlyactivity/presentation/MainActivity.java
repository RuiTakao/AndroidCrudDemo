package com.takaobrog.crudapponlyactivity.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.takaobrog.crudapponlyactivity.connection.ServerConnection;
import com.takaobrog.crudapponlyactivity.util.Data;
import com.takaobrog.crudapponlyactivity.R;
import com.takaobrog.crudapponlyactivity.util.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LIST_ID = "id";
    private static final String LIST_TITLE = "title";
    private static final String LIST_MEMO = "memo";
    private final ServerConnection conn = new ServerConnection();
    private ListView listView;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.d(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.menu_item_list);
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

    @Override
    protected void onResume() {
        super.onResume();
        Logging.d(TAG, "onResume");
        showLoadingDialog();
        fetchList(this::dismissLoadingDialog);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logging.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logging.d(TAG, "onDestroy");
    }

    public void onClickFb(View view) {
        registerDialog();
    }

    private void registerDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.regster_dialog, null);
        TextInputLayout textInputLayoutTitle = view.findViewById(R.id.register_title);
        TextInputLayout textInputLayoutMemo = view.findViewById(R.id.register_memo);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setView(view)
                .setPositiveButton(getString(R.string.dialog_button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoadingDialog();
                        String title;
                        String memo;
                        if (textInputLayoutTitle.getEditText() == null) return;
                        if (textInputLayoutMemo.getEditText() == null) return;
                        title = textInputLayoutTitle.getEditText().getText().toString();
                        memo = textInputLayoutMemo.getEditText().getText().toString();
                        new Thread(() -> {
                            conn.create(title, memo);
                            fetchList(() -> dismissLoadingDialog());
                        }).start();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_negative), null)
                .create()
                .show();
    }

    private void fetchList(Runnable callback) {
        Logging.d(TAG, "fetchList");
        List<Map<String, Object>> list = new ArrayList<>();
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
            listView.post(() -> listView.setAdapter(adapter));
            if (callback != null) callback.run();
            Logging.d(TAG, "fetchList setList");
        }).start();
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
}