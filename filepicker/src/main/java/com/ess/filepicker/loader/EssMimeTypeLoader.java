package com.ess.filepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.loader.content.CursorLoader;

import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.ess.filepicker.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 按照文件类型查找
 */

public class EssMimeTypeLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATE_MODIFIED};

    private List<EssFile> essFileList;

    public EssMimeTypeLoader(Context context, String selection, String[] selectionArgs, String sortOrder) {
        //默认按照创建时间降序排列
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, sortOrder);
    }

    public static CursorLoader newInstance(Context context, String extension, int mSortType) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "='" + mimeType + "'";
        String[] selectionArgs = null;
        if (extension == null) {
            extension = "";
        }
        if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            selection = MediaStore.Files.FileColumns.MIME_TYPE + " in(?,?) ";
            selectionArgs = new String[]{Const.mimeTypeMap.get("doc"), Const.mimeTypeMap.get("docx")};
        }
        if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            selection = MediaStore.Files.FileColumns.MIME_TYPE + " in(?,?) ";
            selectionArgs = new String[]{Const.mimeTypeMap.get("xls"), Const.mimeTypeMap.get("xlsx")};
        }
        if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            selection = MediaStore.Files.FileColumns.MIME_TYPE + " in(?,?) ";
            selectionArgs = new String[]{Const.mimeTypeMap.get("ppt"), Const.mimeTypeMap.get("pptx")};
        }
        if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
            selection = MediaStore.Files.FileColumns.MIME_TYPE + " in(?,?,?) ";
            selectionArgs = new String[]{Const.mimeTypeMap.get("png"), Const.mimeTypeMap.get("jpg"), Const.mimeTypeMap.get("jpeg")};
            //不扫描有.nomedia文件的文件夹下的多媒体文件，带有.nomedia文件的文件夹下的多媒体文件的media_type都被置为了0
            selection = selection + " and " + MediaStore.Files.FileColumns.MEDIA_TYPE + " != " + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        }
        if (extension.equalsIgnoreCase("apk")) {
            selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk' ";
        }

        selection = selection + " and " + MediaStore.Files.FileColumns.SIZE + " >0 " ;


        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        if (mSortType == FileUtils.BY_NAME_ASC) {
            sortOrder = MediaStore.Files.FileColumns.DATA + " ASC ";
        } else if (mSortType == FileUtils.BY_NAME_DESC) {
            sortOrder = MediaStore.Files.FileColumns.DATA + " DESC ";
        } else if (mSortType == FileUtils.BY_TIME_ASC) {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " ASC ";
        } else if (mSortType == FileUtils.BY_TIME_DESC) {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        } else if (mSortType == FileUtils.BY_SIZE_ASC) {
            sortOrder = MediaStore.Files.FileColumns.SIZE + " ASC ";
        } else if (mSortType == FileUtils.BY_SIZE_DESC) {
            sortOrder = MediaStore.Files.FileColumns.SIZE + " DESC ";
        }
        return new EssMimeTypeLoader(context, selection, selectionArgs, sortOrder);
    }

    public List<EssFile> getEssFileList() {
        return essFileList;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor data = super.loadInBackground();
        essFileList = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()){
                EssFile essFile = new EssFile(data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                if(essFile.isExits()){
                    essFileList.add(essFile);
                }
            }
            data.moveToFirst();
        }

        return data;
    }


}
