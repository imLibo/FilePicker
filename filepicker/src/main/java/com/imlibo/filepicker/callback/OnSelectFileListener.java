package com.imlibo.filepicker.callback;

import android.os.Parcelable;

import com.imlibo.filepicker.model.EssFile;

import java.io.Serializable;
import java.util.List;

/**
 * OnSelectFileListener
 * Created by 李波 on 2018/2/8.
 */

public interface OnSelectFileListener{
    void onSelectFile(List<EssFile> essFileList);
}
