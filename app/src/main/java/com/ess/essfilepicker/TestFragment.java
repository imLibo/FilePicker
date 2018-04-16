package com.ess.essfilepicker;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * TestFragment
 */
public class TestFragment extends Fragment {

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

    public TestFragment() {
    }

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
