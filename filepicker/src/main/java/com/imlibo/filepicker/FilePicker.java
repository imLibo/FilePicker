package com.imlibo.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.activity.SelectFileByScanActivity;
import com.imlibo.filepicker.activity.SelectPictureActivity;
import com.imlibo.filepicker.util.Const;
import com.imlibo.filepicker.util.DialogUtil;
import com.imlibo.filepicker.util.FileUtils;
import com.imlibo.filepicker.util.MimeType;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;


/**
 * FilePicker 构造器
 * Created by 李波 on 2018/2/8.
 *
 */

public class FilePicker {

    private static Builder builder;

    FilePicker(final Builder builder) {
        FilePicker.builder = builder;
        final Activity activity = builder.getActivity();
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
                        if(builder.isByBrowser){
                            intent.setClass(activity,SelectFileByBrowserActivity.class);
                        }else if(builder.isByScan){
                            intent.setClass(activity,SelectFileByScanActivity.class);
                        }else if(builder.isSelectMedia){
                            intent.setClass(activity,SelectPictureActivity.class);
                        }
                        Fragment fragment = builder.getFragment();
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, builder.request_code);
                        } else {
                            activity.startActivityForResult(intent, builder.request_code);
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

    public static Builder getBuilder() {
        return builder;
    }

    public static class Builder{

        private final WeakReference<Activity> mContext;
        private final WeakReference<Fragment> mFragment;

        private String[] mFileTypes;
        private String mSortType;
        private boolean isSingle = false;
        private boolean isByBrowser = true;
        private boolean isByScan = false;
        private boolean isSelectMedia = false;
        private int maxCount = 10;
        private int request_code;
        private boolean onlyShowImages = false;
        private boolean onlyShowVideos = false;
        private boolean enabledCapture = false;
        private Drawable placeHolder;
        private Set<MimeType> mimeTypeSet;

        public Builder(Activity activity) {
            this(activity,null);
        }

        public Builder(Fragment fragment){
            this(fragment.getActivity(),fragment);
        }

        public Builder(Activity mContext, Fragment mFragment) {
            this.mContext = new WeakReference<>(mContext);
            this.mFragment = new WeakReference<>(mFragment);
        }

        public Builder choose(Set<MimeType> mimeTypes){
            this.mimeTypeSet = mimeTypes;
            return this;
        }

        public boolean isSingleton(){
            return isSingle;
        }

        public String[] getFileTypes() {
            if(mFileTypes == null || mFileTypes.length == 0){
                return new String[]{"doc","pdf","apk"};
            }
            return mFileTypes;
        }

        public int getSortType() {
            if(TextUtils.isEmpty(mSortType)){
                return FileUtils.BY_NAME_ASC;
            }
            return Integer.valueOf(mSortType);
        }

        public int getMaxCount() {
            return maxCount;
        }

        public int getRequest_code() {
            return request_code;
        }

        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
            if(maxCount <= 1){
                this.maxCount = 1;
                this.isSingle = true;
            }else{
                this.isSingle = false;
            }
            return this;
        }

        public Builder setFileTypes(String...fileTypes){
            mFileTypes = fileTypes;
            return this;
        }

        public Builder setSortTypes(String sortType){
            mSortType = sortType;
            return this;
        }

        public Builder isSingle(){
            this.isSingle = true;
            this.maxCount = 1;
            return this;
        }

        public Builder onlyShowImages(){
            this.onlyShowImages = true;
            return this;
        }

        public Builder onlyShowVideos(){
            this.onlyShowVideos = true;
            return this;
        }

        public Builder placeHolder(Drawable placeHolder){
            this.placeHolder = placeHolder;
            return this;
        }

        public Drawable getPlaceHolder(){
            return this.placeHolder;
        }

        public Builder enabledCapture(boolean enabledCapture){
            this.enabledCapture = enabledCapture;
            return this;
        }

        public boolean isOnlyShowImages(){
            return onlyShowImages;
        }

        public boolean isOnlyShowVideos(){
            return onlyShowVideos;
        }

        public boolean isEnabledCapture(){
            return enabledCapture;
        }

        public Builder isByBrowser(){
            this.isByBrowser = true;
            this.isByScan = false;
            this.isSelectMedia = false;
            return this;
        }

        public Builder isByScan(){
            this.isByScan = true;
            this.isByBrowser = false;
            this.isSelectMedia = false;
            return this;
        }

        public Builder isSelectMedia(){
            this.isSelectMedia = true;
            this.isByScan = false;
            this.isByBrowser = false;
            return this;
        }

        public Builder requestCode(int requestCode){
            this.request_code = requestCode;
            return this;
        }

        @Nullable
        Activity getActivity() {
            return mContext.get();
        }

        @Nullable
        Fragment getFragment() {
            return mFragment != null ? mFragment.get() : null;
        }

        public FilePicker build(){
            return new FilePicker(this);
        }

    }

}
