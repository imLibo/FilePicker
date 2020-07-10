package com.ess.essfilepicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ess.filepicker.FilePicker;
import com.ess.filepicker.SelectCreator;
import com.ess.filepicker.SelectOptions;
import com.ess.filepicker.activity.SelectFileByBrowserActivity;
import com.ess.filepicker.activity.SelectFileByScanActivity;
import com.ess.filepicker.activity.SelectPictureActivity;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.ess.filepicker.util.DialogUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_filename)
    TextView textView;

    private static final int REQUEST_CODE_CHOOSE = 23;

    @OnClick(R.id.button_browse)
    public void onBrowse(View view) {
        FilePicker.from(this)
                .chooseForBrowser()
                .setMaxCount(2)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }

    @OnClick(R.id.button_scan)
    public void onScan(View view) {
        FilePicker
                .from(this)
                .chooseForMimeType()
                .setMaxCount(10)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }


    @OnClick(R.id.button_single_pick)
    public void onSinglePick(View view) {
        FilePicker
                .from(this)
                .chooseForBrowser()
                .isSingle()
                .setFileTypes("pdf")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }

    @OnClick(R.id.button_select_pictures)
    public void onSelectPictures(View view){
        FilePicker
                .from(this)
                .chooseMedia()
                .enabledCapture(true)
                .setTheme(R.style.FilePicker_Dracula)
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }

    @OnClick(R.id.button_fragment)
    public void onFragment(View view){
        startActivity(new Intent(this,FragmentTestActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.CAMERA,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(Object data) {

                    }
                }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(@NonNull List<String> permissions) {
                if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                }


            }
        })
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
            StringBuilder builder = new StringBuilder();
            for (EssFile file :
                    essFileList) {
                builder.append(file.getMimeType()).append(" | ").append(file.getName()).append("\n\n");
            }
            textView.setText(builder.toString());
        }
    }
}
