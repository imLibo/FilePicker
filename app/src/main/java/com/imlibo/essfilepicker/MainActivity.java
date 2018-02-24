package com.imlibo.essfilepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imlibo.filepicker.EssFilePicker;
import com.imlibo.filepicker.model.EssFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_filename)
    TextView textView;

    @OnClick(R.id.button_browse)
    public void onBrowse(View view){
        new EssFilePicker.Builder(this)
                .isMultiSelect(true)
                .isByBrowser()
                .setMaxCount(2)
                .setFileTypes("doc","apk","mp3","gif","txt","mp4","zip","ppt","exe","png","jpg","jpeg")
//                .setOnSelectListener(new OnSelectFileListener() {
//                    @Override
//                    public void onSelectFile(List<EssFile> essFileList) {
//                        StringBuilder builder = new StringBuilder();
//                        for (EssFile file :
//                                essFileList) {
//                            builder.append(file.getMimeType()).append(" | ").append(file.getName()).append("\n\n");
//                        }
//                        textView.setText(builder.toString());
//                    }
//                })
                .build();
    }

    @OnClick(R.id.button_scan)
    public void onScan(View view){
        new EssFilePicker.Builder(this)
                .isMultiSelect(true)
                .isByScan()
                .setMaxCount(10)
                .setFileTypes("png","doc")
//                .setOnSelectListener(new OnSelectFileListener() {
//                    @Override
//                    public void onSelectFile(List<EssFile> essFileList) {
//                        StringBuilder builder = new StringBuilder();
//                        for (EssFile file :
//                                essFileList) {
//                            builder.append(file.getMimeType()).append(" | ").append(file.getName()).append("\n\n");
//                        }
//                        textView.setText(builder.toString());
//                    }
//                })
                .build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


}
