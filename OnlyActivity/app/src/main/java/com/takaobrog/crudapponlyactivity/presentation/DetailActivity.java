package com.takaobrog.crudapponlyactivity.presentation;

import static com.takaobrog.crudapponlyactivity.presentation.MainActivity.LIST_ID;
import static com.takaobrog.crudapponlyactivity.presentation.MainActivity.TRANSITION_TYPE;
import static com.takaobrog.crudapponlyactivity.presentation.MainActivity.TRANSITION_TYPE_BACK;
import static com.takaobrog.crudapponlyactivity.presentation.MainActivity.TRANSITION_TYPE_DELETE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.takaobrog.crudapponlyactivity.R;
import com.takaobrog.crudapponlyactivity.connection.ServerConnection;
import com.takaobrog.crudapponlyactivity.util.Data;
import com.takaobrog.crudapponlyactivity.util.Logging;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String DATA_ID = "id";
    private int id;
    private String title;
    private String memo;
    private TextView titleView;
    private TextView memoView;
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
        titleView = findViewById(R.id.detail_title);
        memoView = findViewById(R.id.detail_memo);
        backBtn = findViewById(R.id.back_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        showLoadingDialog();
        fetchData(this::dismissLoadingDialog);
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
        Intent intent = new Intent();
        intent.putExtra(TRANSITION_TYPE, TRANSITION_TYPE_BACK);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickEdit(View view) {
        Logging.d(TAG, "edit id: " + id);
        editDialog();
    }

    public void onClickDelete(View view) {
        Logging.d(TAG, "delete id: " + id);
        buttonSetting();
        Intent intent = new Intent();
        intent.putExtra(TRANSITION_TYPE, TRANSITION_TYPE_DELETE);
        intent.putExtra(LIST_ID, id);
        setResult(RESULT_OK, intent);
        finish();
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

    private void editDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.edit_dialog, null);
        TextInputLayout textInputLayoutTitle = view.findViewById(R.id.edit_title);
        TextInputLayout textInputLayoutMemo = view.findViewById(R.id.edit_memo);
        textInputLayoutTitle.getEditText().setText(title);
        textInputLayoutMemo.getEditText().setText(memo);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.edit_dialog_title))
                .setView(view)
                .setPositiveButton(getString(R.string.edit_dialog_button_positive), new DialogInterface.OnClickListener() {
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
                            conn.update(id, title, memo);
                            fetchData(() -> dismissLoadingDialog());
                        }).start();
                    }
                })
                .setNegativeButton(getString(R.string.edit_dialog_button_negative), null)
                .create()
                .show();
    }

    private void fetchData(Runnable callback) {
        Logging.d(TAG, "fetchData");
        new Thread(() -> {
            Data data = conn.getData(id);
            title = data.getTitle();
            memo = data.getMemo();
            titleView.post(() -> titleView.setText(title));
            memoView.post(() -> memoView.setText(memo));
            if (callback != null) callback.run();
        }).start();
    }
}