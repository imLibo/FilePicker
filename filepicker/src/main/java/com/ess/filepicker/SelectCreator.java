package com.ess.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.ess.filepicker.activity.SelectFileByBrowserActivity;
import com.ess.filepicker.activity.SelectFileByScanActivity;
import com.ess.filepicker.activity.SelectPictureActivity;
import com.ess.filepicker.util.DialogUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

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
        if(maxCount <= 1){
            selectOptions.maxCount = 1;
            selectOptions.isSingle = true;
        }else{
            selectOptions.isSingle = false;
        }
        return this;
    }

    public SelectCreator setTheme(@StyleRes int theme){
        selectOptions.themeId = theme;
        return this;
    }

    public SelectCreator setFileTypes(String...fileTypes){
        selectOptions.mFileTypes = fileTypes;
        return this;
    }

    public SelectCreator setSortType(String sortType){
        selectOptions.mSortType = sortType;
        return this;
    }

    public SelectCreator isSingle(){
        selectOptions.isSingle = true;
        selectOptions.maxCount = 1;
        return this;
    }

    public SelectCreator onlyShowImages(){
        selectOptions.onlyShowImages = true;
        return this;
    }

    public SelectCreator onlyShowVideos(){
        selectOptions.onlyShowVideos = true;
        return this;
    }

    public SelectCreator placeHolder(Drawable placeHolder){
        selectOptions.placeHolder = placeHolder;
        return this;
    }

    public SelectCreator enabledCapture(boolean enabledCapture){
        selectOptions.enabledCapture = enabledCapture;
        return this;
    }

    public SelectCreator requestCode(int requestCode){
        selectOptions.request_code = requestCode;
        return this;
    }

    public void start(){
        final Activity activity = filePicker.getActivity();
        if (activity == null) {
            return;
        }
        AndPermission
                .with(activity)
                .permission(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //接受权限
                        Intent intent = new Intent();
                        if(SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_BROWSER)){
                            intent.setClass(activity,SelectFileByBrowserActivity.class);
                        }else if(SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_SCAN)){
                            intent.setClass(activity,SelectFileByScanActivity.class);
                        }else if(SelectCreator.this.chooseType.equals(SelectOptions.CHOOSE_TYPE_MEDIA)){
                            intent.setClass(activity,SelectPictureActivity.class);
                        }
                        Fragment fragment = filePicker.getFragment();
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, selectOptions.request_code);
                        } else {
                            activity.startActivityForResult(intent, selectOptions.request_code);
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //拒绝权限
                        DialogUtil.showPermissionDialog(activity,Permission.transformText(activity, permissions).get(0));
                    }
                })
                .start();
    }

}
