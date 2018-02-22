package com.imlibo.essfilepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.util.FileUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 打开文件浏览器
     * @param view view
     */
    public void onScanFileList(View view) {
        Intent intent = new Intent(this,SelectFileByBrowserActivity.class);
//        intent.putExtra("TYPES",new String[]{"doc","png","jpg"});
//        intent.putExtra("SORT_TYPE", FileUtils.BY_TIME_DESC);
        startActivityForResult(intent,1);
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
