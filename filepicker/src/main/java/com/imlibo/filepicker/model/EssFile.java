package com.imlibo.filepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.imlibo.filepicker.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * EssFile
 * Created by 李波 on 2018/2/5.
 */

public class EssFile implements Parcelable {

    private String mFilePath;
    private String mimeType;
    private String childFolderCount = "加载中";
    private String childFileCount = "加载中";
    private boolean isChecked = false;
    private boolean isExits = false;
    private boolean isDirectory = false;
    private boolean isFile = false;
    private String mFileName;

    protected EssFile(Parcel in) {
        mFilePath = in.readString();
        mimeType = in.readString();
        childFolderCount = in.readString();
        childFileCount = in.readString();
        isChecked = in.readByte() != 0;
        isExits = in.readByte() != 0;
        isDirectory = in.readByte() != 0;
        isFile = in.readByte() != 0;
        mFileName = in.readString();
    }

    public static final Creator<EssFile> CREATOR = new Creator<EssFile>() {
        @Override
        public EssFile createFromParcel(Parcel in) {
            return new EssFile(in);
        }

        @Override
        public EssFile[] newArray(int size) {
            return new EssFile[size];
        }
    };

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public EssFile(String path){
        mFilePath = path;
        File file = new File(mFilePath);
        if(file.exists()){
            isExits = true;
            isDirectory = file.isDirectory();
            isFile = file.isFile();
            mFileName = file.getName();
        }
        mimeType = FileUtils.getMimeType(mFilePath);
    }

    public EssFile(File file) {
        mFilePath = file.getAbsolutePath();
        if(file.exists()){
            isExits = true;
            isDirectory = file.isDirectory();
            isFile = file.isFile();
        }
        mimeType = FileUtils.getMimeType(file.getAbsolutePath());
    }

    public boolean isExits() {
        return isExits;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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
        return new File(mFilePath);
    }

    public String getName() {
        return new File(mFilePath).getName();
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getAbsolutePath() {
        return mFilePath;
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
                "mFilePath=" + mFileName +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFilePath);
        dest.writeString(mimeType);
        dest.writeString(childFolderCount);
        dest.writeString(childFileCount);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isExits ? 1 : 0));
        dest.writeByte((byte) (isDirectory ? 1 : 0));
        dest.writeByte((byte) (isFile ? 1 : 0));
        dest.writeString(mFileName);
    }
}
