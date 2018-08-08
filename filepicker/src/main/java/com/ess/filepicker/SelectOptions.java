package com.ess.filepicker;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import com.ess.filepicker.util.FileUtils;
import com.ess.filepicker.util.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * SelectOptions
 * Created by 李波 on 2018/3/7.
 */

public class SelectOptions {

    public static final String CHOOSE_TYPE_BROWSER = "choose_type_browser";
    public static final String CHOOSE_TYPE_SCAN = "choose_type_scan";
    public static final String CHOOSE_TYPE_MEDIA = "choose_type_media";

    //默认存放隐藏目录的路径
    public static final String defaultTargetPath = Environment.getExternalStorageDirectory() + "/essPictures";
    //默认存放用于图库选择的路径
    public static final String defaultShowPath = Environment.getExternalStorageDirectory() + "/FilePickerPics/";

    public String[] mFileTypes;
    public String mSortType;
    public boolean isSingle = false;
    public int maxCount = 10;
    public int request_code;
    public boolean onlyShowImages = false;
    public boolean onlyShowVideos = false;
    public boolean enabledCapture = false;
    public Drawable placeHolder;
    public boolean compressImage = true;
    public String targetPath = defaultTargetPath;
    public int themeId = R.style.FilePicker_Elec;

    public SelectOptions() {
        getTargetPath();
    }

    public String[] getFileTypes() {
        if (mFileTypes == null || mFileTypes.length == 0) {
            return new String[]{};
        }
        return mFileTypes;
    }

    public int getSortType() {
        if (TextUtils.isEmpty(mSortType)) {
            return FileUtils.BY_NAME_ASC;
        }
        return Integer.valueOf(mSortType);
    }

    public String getTargetPath() {
        if (!new File(targetPath).exists()) {
            try {
                File file = new File(defaultTargetPath);
                File fileNoMedia = new File(defaultTargetPath + "/.nomedia");
                if (!file.exists()) {
                    file.mkdirs();
                }
                if(!fileNoMedia.exists()){
                    fileNoMedia.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return defaultTargetPath;
        }
        if(!new File(defaultShowPath).exists()){
            new File(defaultShowPath).mkdir();
        }
        return targetPath;
    }

    public void setSortType(int sortType) {
        mSortType = String.valueOf(sortType);
    }

    public static SelectOptions getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectOptions getCleanInstance() {
        SelectOptions options = getInstance();
        options.reset();
        return options;
    }

    private void reset() {
        mFileTypes = new String[]{};
        mSortType = String.valueOf(FileUtils.BY_NAME_ASC);
        isSingle = false;
        maxCount = 10;
        onlyShowImages = false;
        onlyShowVideos = false;
        enabledCapture = false;
        compressImage = true;
        themeId = R.style.FilePicker_Elec;
    }

    private static final class InstanceHolder {
        private static final SelectOptions INSTANCE = new SelectOptions();
    }

}
