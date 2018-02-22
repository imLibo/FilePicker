package com.imlibo.filepicker;

import android.content.Context;
import android.content.Intent;

import com.imlibo.filepicker.activity.SelectFileByBrowserActivity;
import com.imlibo.filepicker.callback.OnSelectFileListener;
import com.imlibo.filepicker.util.Const;

/**
 * EssFilePicker
 * Created by 李波 on 2018/2/8.
 *
 */

public class EssFilePicker {

    EssFilePicker(Builder builder) {
        Intent intent = new Intent(builder.mContext, SelectFileByBrowserActivity.class);
        intent.putExtra(Const.EXTRA_KEY_FILE_TYPE, builder.mFileTypes);
        intent.putExtra(Const.EXTRA_KEY_SORT_TYPE, builder.mSortType);
        intent.putExtra(Const.EXTRA_KEY_IS_MULTI_SELECT, builder.isMultiSelect);
        SelectFileByBrowserActivity.setOnSelectFileListener(builder.onSelectFileListener);
        builder.mContext.startActivity(intent);
    }

    public static class Builder{
        private String[] mFileTypes;
        private String mSortType;
        private boolean isMultiSelect;
        private OnSelectFileListener onSelectFileListener;
        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
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

        public Builder setOnSelectListener(OnSelectFileListener onSelectListener){
            this.onSelectFileListener = onSelectListener;
            return this;
        }

        public EssFilePicker build(){
            return new EssFilePicker(this);
        }

    }

}
