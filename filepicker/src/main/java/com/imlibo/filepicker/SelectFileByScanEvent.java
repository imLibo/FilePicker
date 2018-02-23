package com.imlibo.filepicker;

import com.imlibo.filepicker.model.EssFile;

import java.util.List;

/**
 * SelectFileByScanEvent
 * Created by 李波 on 2018/2/23.
 */

public interface SelectFileByScanEvent {
    void onFindFileList(List<EssFile> essFileList);
}
