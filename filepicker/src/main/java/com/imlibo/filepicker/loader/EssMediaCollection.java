package com.imlibo.filepicker.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.imlibo.filepicker.model.Album;
import com.imlibo.filepicker.model.EssFile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * EssMediaCollection
 * Created by 李波 on 2018/3/2.
 */

public class EssMediaCollection implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 3;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private static final String ARGS_ONLY_SHOWIMAGE = "ARGS_ONLY_SHOWIMAGE";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private EssMediaCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }

        return EssMediaLoader.newInstance(context, album);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        List<EssFile> essFileList = new ArrayList<>();
        while (data.moveToNext()){
            EssFile essFile = new EssFile(data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
                    data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)));
            essFileList.add(essFile);
        }

        mCallbacks.onMediaLoad(essFileList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onmMediaReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull EssMediaCallbacks callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public void load(@Nullable Album target) {
        load(target, false);
    }

    public void load(@Nullable Album target, boolean enableCapture) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        if(mContext.get() == null){
            mLoaderManager.initLoader(LOADER_ID, args, this);
        }else {
            mLoaderManager.restartLoader(LOADER_ID,args,this);
        }
    }

    public void load(@Nullable Album target, boolean enableCapture, boolean onlyShouwIMage) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        args.putBoolean(ARGS_ONLY_SHOWIMAGE, onlyShouwIMage);
        if(mContext.get() == null){
            mLoaderManager.initLoader(LOADER_ID, args, this);
        }else {
            mLoaderManager.restartLoader(LOADER_ID,args,this);
        }
    }

    public interface EssMediaCallbacks {

        void onMediaLoad(List<EssFile> cursor);

        void onmMediaReset();
    }
}
