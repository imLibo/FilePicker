package com.ess.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.ess.filepicker.activity.SelectFileByBrowserActivity;
import com.ess.filepicker.activity.SelectFileByScanActivity;
import com.ess.filepicker.activity.SelectPictureActivity;

import java.io.File;

/**
 * SelectCreator
 * Created by 李波 on 2018/3/7.
 */

public final class SelectCreator {

    private final FilePicker filePicker;
    private final SelectOptions selectOptions;
    private String chooseType;

    public SelectCreator(FilePicker filePicker, String chooseType) {
        selectOptions = SelectOptions.getCleanInstance();
        this.chooseType = chooseType;
        this.filePicker = filePicker;
    }

    public SelectCreator setMaxCount(int maxCount) {
        selectOptions.maxCount = maxCount;
        if (maxCount <= 1) {
            selectOptions.maxCount = 1;
            selectOptions.isSingle = true;
        } else {
            selectOptions.isSingle = false;
        }
        return this;
    }

    public SelectCreator setCompressImage(boolean compressImage){
        selectOptions.compressImage = compressImage;
        return this;
    }

    public SelectCreator setTargetPath(String path){
        selectOptions.targetPath = path;
        return this;
    }

    public SelectCreator setTheme(@StyleRes int theme) {
        selectOptions.themeId = theme;
        return this;
    }

    public SelectCreator setFileTypes(String... fileTypes) {
        selectOptions.mFileTypes = fileTypes;
        return this;
    }

    public SelectCreator setSortType(String sortType) {
        selectOptions.mSortType = sortType;
        return this;
    }

    public SelectCreator isSingle() {
        selectOptions.isSingle = true;
        selectOptions.maxCount = 1;
        return this;
    }

    public SelectCreator onlyShowImages() {
        selectOptions.onlyShowImages = true;
        return this;
    }

    public SelectCreator onlyShowVideos() {
        selectOptions.onlyShowVideos = true;
        return this;
    }

    public SelectCreator placeHolder(Drawable placeHolder) {
        selectOptions.placeHolder = placeHolder;
        return this;
    }

    public SelectCreator enabledCapture(boolean enabledCapture) {
        selectOptions.enabledCapture = enabledCapture;
        return this;
    }

    public SelectCreator requestCode(int requestCode) {
        selectOptions.request_code = requestCode;
        return this;
    }

    public void start() {
        final Activity activity = filePicker.getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        if (SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_BROWSER)) {
            intent.setClass(activity, SelectFileByBrowserActivity.class);
        } else if (SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_SCAN)) {
            intent.setClass(activity, SelectFileByScanActivity.class);
        } else if (SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_MEDIA)) {
            intent.setClass(activity, SelectPictureActivity.class);
        } else {
            return;
        }
        Fragment fragment = filePicker.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, selectOptions.request_code);
        } else {
            activity.startActivityForResult(intent, selectOptions.request_code);
        }
    }

}
