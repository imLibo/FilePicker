package com.imlibo.filepicker.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.EssFileFilter;
import com.imlibo.filepicker.SelectFileByBrowserEvent;
import com.imlibo.filepicker.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * SelectFileByBrowserPresenter
 * Created by 李波 on 2018/2/3.
 */

public class SelectFileByBrowserPresenter {
    private SelectFileByBrowserEvent mPresenterView;
    private Activity mActivity;

    public SelectFileByBrowserPresenter(SelectFileByBrowserEvent mPresenterView) {
        this.mPresenterView = mPresenterView;
        if (mPresenterView instanceof Activity) {
            mActivity = (Activity) mPresenterView;
        } else if (mPresenterView instanceof Fragment) {
            mActivity = ((Fragment) mPresenterView).getActivity();
        }
    }

    /**
     * 查找文件列表
     * @param mSelectedFileList 当前已选中的文件列表
     * @param queryPath 查询路径
     * @param types 需要返回的文件类型
     * @param mSortType 排序类型
     */
    public void findFileList(final List<EssFile> mSelectedFileList, final String queryPath, final String[] types, final int mSortType){
        Observable
                .fromCallable(new Callable<List<EssFile>>() {
                    @Override
                    public List<EssFile> call() throws Exception {
                        File file = new File(queryPath);
                        File[] files = file.listFiles(new EssFileFilter(types));
                        if(files == null){
                            return new ArrayList<>();
                        }
                        List<File> fileList = Arrays.asList(files);
                        if(mSortType == FileUtils.BY_NAME_ASC){
                            Collections.sort(fileList, new FileUtils.SortByName());
                        }else if(mSortType == FileUtils.BY_NAME_DESC){
                            Collections.sort(fileList, new FileUtils.SortByName());
                            Collections.reverse(fileList);
                        }else if(mSortType == FileUtils.BY_TIME_ASC){
                            Collections.sort(fileList,new FileUtils.SortByTime());
                        }else if(mSortType == FileUtils.BY_TIME_DESC){
                            Collections.sort(fileList,new FileUtils.SortByTime());
                            Collections.reverse(fileList);
                        }else if(mSortType == FileUtils.BY_SIZE_ASC){
                            Collections.sort(fileList,new FileUtils.SortBySize());
                        }else if(mSortType == FileUtils.BY_SIZE_DESC){
                            Collections.sort(fileList,new FileUtils.SortBySize());
                            Collections.reverse(fileList);
                        }else if(mSortType == FileUtils.BY_EXTENSION_ASC){
                            Collections.sort(fileList,new FileUtils.SortByExtension());
                        }else if(mSortType == FileUtils.BY_EXTENSION_DESC){
                            Collections.sort(fileList,new FileUtils.SortByExtension());
                            Collections.reverse(fileList);
                        }
                        return EssFile.getEssFileList(fileList);
                    }
                })
                .map(new Function<List<EssFile>, List<EssFile>>() {
                    @Override
                    public List<EssFile> apply(List<EssFile> essFileList) throws Exception {
                            for (EssFile selectedFile :
                                    mSelectedFileList) {
                                for (int i = 0; i < essFileList.size(); i++) {
                                    if (selectedFile.getAbsolutePath().equals(essFileList.get(i).getAbsolutePath())) {
                                        essFileList.get(i).setChecked(true);
                                        break;
                                    }
                                }
                            }
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
                    public void onNext(List<EssFile> fileList) {
                        mPresenterView.onFindFileList(queryPath,fileList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 查找指定文件夹目录下的子文件/子文件夹的数量
     * @param queryPath 要查询的文件夹路径
     * @param types 要过滤掉的文件类型
     */
    public void findChildFileAndFolderCount(final int position, final String queryPath, final String[] types){
        Observable
                .fromCallable(new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        File file = new File(queryPath);
                        File[] files = file.listFiles(new EssFileFilter(types));
                        if(files == null){
                            return new String[]{"0","0"};
                        }
                        List<EssFile> fileList = EssFile.getEssFileList(Arrays.asList(files));
                        int countChildFile = 0;
                        int countChildFolder = 0;
                        for (EssFile essFile :
                                fileList) {
                            if(essFile.isDirectory()){
                                countChildFolder++;
                            }else {
                                countChildFile++;
                            }
                        }
                        return new String[]{String.valueOf(countChildFile),String.valueOf(countChildFolder)};
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] fileList) {
                        mPresenterView.onFindChildFileAndFolderCount(position, fileList[0],fileList[1]);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
