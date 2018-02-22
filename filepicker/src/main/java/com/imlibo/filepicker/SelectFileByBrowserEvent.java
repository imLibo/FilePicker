package com.imlibo.filepicker;

import com.imlibo.filepicker.model.EssFile;

import java.util.List;

/**
 *
 * Created by 李波 on 2018/2/3.
 */

public interface SelectFileByBrowserEvent {
    void onFindFileList(String queryPath, List<EssFile> fileList);
    void onFindChildFileAndFolderCount(int position, String childFileCount, String childFolderCount);
}
