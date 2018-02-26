package com.imlibo.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.activity.SelectFileByScanActivity;
import com.imlibo.filepicker.util.Const;

import java.lang.ref.WeakReference;


/**
 * EssFilePicker 构造器
 * Created by 李波 on 2018/2/8.
 *
 */

public class EssFilePicker {

    EssFilePicker(Builder builder) {
        Activity activity = builder.getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Const.EXTRA_KEY_FILE_TYPE, builder.mFileTypes);
        intent.putExtra(Const.EXTRA_KEY_SORT_TYPE, builder.mSortType);
        intent.putExtra(Const.EXTRA_KEY_IS_MULTI_SELECT, builder.isMultiSelect);
        intent.putExtra(Const.EXTRA_KEY_MAX_COUNT, builder.maxCount);
        if(builder.isByBrowser){
            intent.setClass(activity,SelectFileByBrowserActivity.class);
        }else if(builder.isByScan){
            intent.setClass(activity,SelectFileByScanActivity.class);
        }
        Fragment fragment = builder.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, builder.request_code);
        } else {
            activity.startActivityForResult(intent, builder.request_code);
        }

    }

    public static class Builder{

        private final WeakReference<Activity> mContext;
        private final WeakReference<Fragment> mFragment;

        private String[] mFileTypes;
        private String mSortType;
        private boolean isMultiSelect;
        private boolean isByBrowser = false;
        private boolean isByScan = false;
        private int maxCount = 10;

        private int request_code;

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

        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
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

        public Builder isMultiSelect(boolean isMultiSelect){
            this.isMultiSelect = isMultiSelect;
            return this;
        }


        public Builder isByBrowser(){
            this.isByBrowser = true;
            this.isByScan = false;
            return this;
        }

        public Builder isByScan(){
            this.isByScan = true;
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

        public EssFilePicker build(){
            return new EssFilePicker(this);
        }

    }

}
