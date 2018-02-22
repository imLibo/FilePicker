package com.imlibo.essfilepicker;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.imlibo.filepicker.EssFilePicker;
import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.callback.OnSelectFileListener;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.util.FileUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_filename)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * 打开文件浏览器
     * @param view view
     */
    public void onScanFileList(View view) {
        new EssFilePicker.Builder(this)
                .isMultiSelect(true)
                .setOnSelectListener(new OnSelectFileListener() {
                    @Override
                    public void onSelectFile(List<EssFile> essFileList) {
                        Log.i("EssFilePicker","--------------onSelectFile--------------");
                        StringBuilder builder = new StringBuilder();
                        for (EssFile file :
                                essFileList) {
                            Log.i("EssFilePicker","---->>>>  "+file.getName());
                            builder.append(file.getName()).append("\n");
                        }
                        textView.setText(builder.toString());
                    }
                })
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == 0){
                if(data == null){
                    return;
                }
                Log.i("Ess",data.getSerializableExtra("selectFile").toString());
            }
        }
    }
}
