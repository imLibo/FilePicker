package com.ess.filepicker.task;

import android.os.AsyncTask;

import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.model.EssFileCountCallBack;
import com.ess.filepicker.model.EssFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * EssFileCountTask
 * Created by 李波 on 2018/3/5.
 */

public class EssFileCountTask extends AsyncTask<Void,Void,Void>{

    private int position;
    private String queryPath;
    private String[] types;
    private EssFileCountCallBack countCallBack;
    private int childFileCount = 0;
    private int childFolderCount = 0;

    public EssFileCountTask(int position, String queryPath, String[] types, EssFileCountCallBack countCallBack) {
        this.position = position;
        this.queryPath = queryPath;
        this.types = types;
        this.countCallBack = countCallBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File file = new File(queryPath);
        File[] files = file.listFiles(new EssFileFilter(types));
        if(files == null){
            return null;
        }
        List<EssFile> fileList = EssFile.getEssFileList(Arrays.asList(files));
        for (EssFile essFile :
                fileList) {
            if(essFile.isDirectory()){
                childFolderCount++;
            }else {
                childFileCount++;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(countCallBack!=null){
            countCallBack.onFindChildFileAndFolderCount(position,String.valueOf(childFileCount),String.valueOf(childFolderCount));
        }
    }
}
