/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ess.filepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.ess.filepicker.model.EssFile;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 按照文件类型查找
 */
public class EssMimeTypeCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_ID = 3;
    private WeakReference<Context> mContext;
    private static final String ARGS_EXTENSION = "args_extension";
    private static final String ARGS_SORT_TYPE = "args_sort_type";
    private LoaderManager mLoaderManager;
    private EssMimeTypeCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        String extension = args.getString(ARGS_EXTENSION);
        int sortType = args.getInt(ARGS_SORT_TYPE);

        return EssMimeTypeLoader.newInstance(context, extension, sortType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mCallbacks.onFileLoad(((EssMimeTypeLoader)loader).getEssFileList());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        if(mCallbacks!=null){
            mCallbacks.onFileReset();
        }
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull EssMimeTypeCallbacks callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public void load(String extension, int sortType,int loaderId) {
        Bundle args = new Bundle();
        args.putString(ARGS_EXTENSION,extension);
        args.putInt(ARGS_SORT_TYPE,sortType);
        if(mContext.get() == null){
            mLoaderManager.initLoader(loaderId, args, this);
        }else {
            mLoaderManager.restartLoader(loaderId,args,this);
        }
    }

    public interface EssMimeTypeCallbacks {

        void onFileLoad(List<EssFile> essFileList);

        void onFileReset();
    }
}
