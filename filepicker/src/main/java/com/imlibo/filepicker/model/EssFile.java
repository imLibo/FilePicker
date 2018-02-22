package com.imlibo.filepicker.model;

import android.os.Parcel;

import org.w3c.dom.ProcessingInstruction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EssFile
 * Created by 李波 on 2018/2/5.
 */

public class EssFile implements Serializable {

    private File mFile;
    private String childFolderCount = "加载中";
    private String childFileCount = "加载中";
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public EssFile(File file) {
        mFile = file;
    }

    protected EssFile(Parcel in) {
    }

    public String getChildFolderCount() {
        return childFolderCount;
    }

    public void setChildFolderCount(String childFolderCount) {
        this.childFolderCount = childFolderCount;
    }

    public String getChildFileCount() {
        return childFileCount;
    }

    public void setChildFileCount(String childFileCount) {
        this.childFileCount = childFileCount;
    }

    public void setChildCounts(String childFileCount, String childFolderCount) {
        this.childFileCount = childFileCount;
        this.childFolderCount = childFolderCount;
    }


    public File getFile() {
        return mFile;
    }

    public String getName() {
        return mFile.getName();
    }

    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    public boolean isFile() {
        return mFile.isFile();
    }

    public File getParentFile() {
        return mFile.getParentFile();
    }

    public String getAbsolutePath() {
        return mFile.getAbsolutePath();
    }

    public static List<EssFile> getEssFileList(List<File> files) {
        List<EssFile> essFileList = new ArrayList<>();
        for (File file :
                files) {
            essFileList.add(new EssFile(file));
        }
        return essFileList;
    }

    @Override
    public String toString() {
        return "EssFile{" +
                "mFile=" + mFile.getName() +
                '}';
    }
}
