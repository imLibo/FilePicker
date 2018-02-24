package com.imlibo.filepicker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.activity.SelectFileByScanActivity;
import com.imlibo.filepicker.util.Const;

/**
 * EssFilePicker
 * Created by 李波 on 2018/2/8.
 *
 */

public class EssFilePicker {

    EssFilePicker(Builder builder) {
        Intent intent = new Intent();
        intent.putExtra(Const.EXTRA_KEY_FILE_TYPE, builder.mFileTypes);
        intent.putExtra(Const.EXTRA_KEY_SORT_TYPE, builder.mSortType);
        intent.putExtra(Const.EXTRA_KEY_IS_MULTI_SELECT, builder.isMultiSelect);
        intent.putExtra(Const.EXTRA_KEY_MAX_COUNT, builder.maxCount);
        if(builder.isByBrowser){
            intent.setClass(builder.mContext,SelectFileByBrowserActivity.class);
//            SelectFileByBrowserActivity.setOnSelectFileListener(builder.onSelectFileListener);
        }
        if(builder.isByScan){
            intent.setClass(builder.mContext, SelectFileByScanActivity.class);
//            SelectFileByScanActivity.setOnSelectFileListener(builder.onSelectFileListener);
        }
        builder.mContext.startActivity(intent);

    }

    public static class Builder{
        private String[] mFileTypes;
        private String mSortType;
        private boolean isMultiSelect;
        private boolean isByBrowser = false;
        private boolean isByScan = false;
        private int maxCount = 10;
        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
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

        public EssFilePicker build(){
            return new EssFilePicker(this);
        }

    }

}
