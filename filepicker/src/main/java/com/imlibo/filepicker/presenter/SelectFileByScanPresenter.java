package com.imlibo.filepicker.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.imlibo.filepicker.SelectFileByScanEvent;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.util.Const;
import com.imlibo.filepicker.util.DateUtils;
import com.imlibo.filepicker.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * SelectFileByScanPresenter
 * Created by 李波 on 2018/2/23.
 */

public class SelectFileByScanPresenter {
    private SelectFileByScanEvent mPresenterView;
    private Activity mActivity;

    public SelectFileByScanPresenter(SelectFileByScanEvent mPresenterView) {
        this.mPresenterView = mPresenterView;
        if (mPresenterView instanceof Activity) {
            mActivity = (Activity) mPresenterView;
        } else if (mPresenterView instanceof Fragment) {
            mActivity = ((Fragment) mPresenterView).getActivity();
        }
    }

    /**
     * 根据文件扩展名，排序方式，查找文件列表
     * @param extension 文件扩展名
     * @param mSortType 排序方式
     */
    public void findFileList(final String extension, final int mSortType, final int pageNum) {
        Observable
                .fromCallable(new Callable<List<EssFile>>() {
                    @Override
                    public List<EssFile> call() throws Exception {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        final String[] projection = new String[]{
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.DATA,
                                MediaStore.Files.FileColumns.MIME_TYPE,
                                MediaStore.Files.FileColumns.SIZE,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Files.FileColumns.TITLE,
                                MediaStore.Files.FileColumns.DATE_MODIFIED};
                        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "='" + mimeType + "'";
                        String[] selectionArgs = null;
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
                        }
                        if (extension.equalsIgnoreCase("apk")) {
                            selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk' ";
                        }
                        //默认按照创建时间降序排列
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
                        //分页
                        if(pageNum >= 0){
//                            sortOrder = sortOrder + " limit 0*"+pageNum+",10 ";
                        }
                        long preQueryTime =  System.currentTimeMillis();
                        Cursor cursor = mActivity.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder);
                        long afterQueryTime = System.currentTimeMillis();
                        Log.i("TAG 查询时长 --> ", String.valueOf(DateUtils.calculateDifferentSecond(preQueryTime,afterQueryTime)));
                        List<EssFile> essFileList = new ArrayList<>();
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                EssFile essFile = new EssFile(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                                if (essFile.isExits()) {
                                    essFileList.add(essFile);
                                }
                            }
                            cursor.close();
                        }
                        Log.i("TAG 处理时长 --> ", String.valueOf(DateUtils.calculateDifferentSecond(afterQueryTime,System.currentTimeMillis())));
                        return essFileList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<EssFile>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<EssFile> essFileList) {
                        mPresenterView.onFindFileList(essFileList);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
