package com.imlibo.essfilepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imlibo.filepicker.FilePicker;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.util.Const;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_filename)
    TextView textView;

    private static final int REQUEST_CODE_CHOOSE = 23;

    @OnClick(R.id.button_browse)
    public void onBrowse(View view) {
        new FilePicker.Builder(this)
                .isByBrowser()
                .setMaxCount(2)
                .setFileTypes("doc", "apk", "mp3", "gif", "txt", "mp4", "zip", "ppt", "exe", "png", "jpg", "jpeg")
                .requestCode(REQUEST_CODE_CHOOSE)
                .build();
    }

    @OnClick(R.id.button_scan)
    public void onScan(View view) {
        new FilePicker.Builder(this)
                .isByScan()
                .setMaxCount(10)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .build();
    }

    @OnClick(R.id.button_select_pictures)
    public void onSelectPitures(View view){
        new FilePicker.Builder(this)
                .isSelectMedia()
                .enabledCapture(true)
                .requestCode(REQUEST_CODE_CHOOSE)
                .build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
