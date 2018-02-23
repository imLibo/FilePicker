package com.imlibo.filepicker.model;

import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.imlibo.filepicker.util.FileUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * EssFileFilter
 */
public class EssFileFilter implements FileFilter {
    private String[] mTypes;

    public EssFileFilter(String[] types) {
        this.mTypes = types;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        if (mTypes != null && mTypes.length > 0) {
            for (String mType : mTypes) {
                if (file.getName().endsWith(mType.toLowerCase()) || file.getName().endsWith(mType.toUpperCase())) {
//                if (FileUtils.getMimeType(file.getAbsolutePath()).equalsIgnoreCase(MimeTypeMap.getSingleton().getMimeTypeFromExtension(mType))) {
                    return true;
                }
            }
        }else {
            return true;
        }
        return false;
    }
}
