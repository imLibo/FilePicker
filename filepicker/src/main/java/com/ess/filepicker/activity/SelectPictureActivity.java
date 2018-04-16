package com.ess.filepicker.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ess.filepicker.R;
import com.ess.filepicker.SelectOptions;
import com.ess.filepicker.adapter.BuketAdapter;
import com.ess.filepicker.adapter.EssMediaAdapter;
import com.ess.filepicker.loader.EssAlbumCollection;
import com.ess.filepicker.loader.EssMediaCollection;
import com.ess.filepicker.model.Album;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.ess.filepicker.util.UiUtils;
import com.ess.filepicker.widget.MediaItemDecoration;
import com.ess.filepicker.widget.ToolbarSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * 选择图片界面
 */
public class SelectPictureActivity extends AppCompatActivity implements EssAlbumCollection.EssAlbumCallbacks, AdapterView.OnItemSelectedListener, EssMediaCollection.EssMediaCallbacks, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    /*4. 最多可选择个数，默认10*/
    private int mMaxCount = 10;
    /*5. todo 是否需要压缩，默认返回压缩之后的图片*/
    private boolean mNeedCompress = true;
    /*6. todo 是否需要裁剪/旋转等，默认不裁剪*/
    private boolean mNeedClip = false;
    /*7. 是否需要显示照相机*/
    private boolean mNeedCamera = true;
    /*8. todo 相机为自定义相机还是系统相机，仅当mNeedCamera=true时有效*/
    private boolean mUseCustomCamera = true;
    /*9. todo 是否可预览图片，默认可预览*/
    private boolean mCanPreview = true;

    private RecyclerView mRecyclerView;
    private TextView mTvSelectedFolder;
    private BuketAdapter mBuketAdapter;
    private EssMediaAdapter mMediaAdapter;

    private final EssAlbumCollection mAlbumCollection = new EssAlbumCollection();
    private final EssMediaCollection mMediaCollection = new EssMediaCollection();
    private MenuItem mCountMenuItem;
    private Set<EssFile> mSelectedFileList = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SelectOptions.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
//        EventBus.getDefault().register(this);
        mRecyclerView = findViewById(R.id.rcv_file_picture_list);
        mTvSelectedFolder = findViewById(R.id.selected_folder);

        initUI();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Drawable navigationIcon = toolbar.getNavigationIcon();
        TypedArray ta = getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        mBuketAdapter = new BuketAdapter(this, null, false);
        ToolbarSpinner spinner = new ToolbarSpinner(this);
        spinner.setSelectedTextView((TextView) findViewById(R.id.selected_folder));
        spinner.setPopupAnchorView(findViewById(R.id.toolbar));
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(mBuketAdapter);

        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.load();
        mMediaCollection.onCreate(this, this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new MediaItemDecoration());
        mMediaAdapter = new EssMediaAdapter(new ArrayList<EssFile>());
        mMediaAdapter.setImageResize(UiUtils.getImageResize(this, mRecyclerView));
        mRecyclerView.setAdapter(mMediaAdapter);
        mMediaAdapter.bindToRecyclerView(mRecyclerView);
        mMediaAdapter.setOnItemChildClickListener(this);
        if (SelectOptions.getInstance().isSingle || SelectOptions.getInstance().maxCount == 1) {
            //单选
            mMediaAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        mCountMenuItem = menu.findItem(R.id.browser_select_count);
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
        mMediaCollection.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mBuketAdapter.swapCursor(cursor);
        cursor.moveToFirst();
        Album album = Album.valueOf(cursor);
        mMediaCollection.load(album, mNeedCamera, mSelectedFileList);
    }

    @Override
    public void onAlbumMediaReset() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBuketAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mBuketAdapter.getCursor());
        mMediaCollection.load(album, mNeedCamera, mSelectedFileList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMediaLoad(List<EssFile> essFileList) {
        mMediaAdapter.setNewData(essFileList);
        if (essFileList == null || essFileList.isEmpty()) {
            mMediaAdapter.setEmptyView(R.layout.empty_file_list);
        }
    }

    @Override
    public void onmMediaReset() {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        EssFile item = mMediaAdapter.getItem(position);
        if (!adapter.equals(mMediaAdapter)) {
            return;
        }
        if (view.getId() == R.id.check_view) {
            if (mSelectedFileList.size() >= SelectOptions.getInstance().maxCount && !item.isChecked()) {
                mMediaAdapter.notifyItemChanged(position, "");
                Snackbar.make(mRecyclerView, "您最多只能选择" + SelectOptions.getInstance().maxCount + "个。", Snackbar.LENGTH_SHORT).show();
                return;
            }
            boolean addSuccess = mSelectedFileList.add(mMediaAdapter.getItem(position));
            if (addSuccess) {
                mMediaAdapter.getData().get(position).setChecked(true);
            } else {
                //已经有了就删掉
                mSelectedFileList.remove(item);
                mMediaAdapter.getData().get(position).setChecked(false);
            }
            mMediaAdapter.notifyItemChanged(position, "");
            mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
        } else if (view.getId() == R.id.media_thumbnail) {
            //预览
            // TODO: 2018/3/7  预览照片
        } else if (view.getId() == R.id.capture) {
            //照相
            // TODO: 2018/3/7  自定义相机照相
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.browser_select_count) {
            //选中
            if (mSelectedFileList.isEmpty()) {
                return true;
            }
            if (SelectOptions.getInstance().compressImage) {
                //需要压缩
                final ArrayList<String> imageList = EssFile.getFilePathList(EssFile.getEssFileList(this, mSelectedFileList));
                final int[] successCount = {0};
                //todo 当目标文件夹存在此文件时不覆盖
                Luban.with(this)
                        .load(imageList)// 传人要压缩的图片列表
                        .ignoreBy(100) // 忽略不压缩图片的大小
                        .setTargetDir(SelectOptions.getInstance().getTargetPath())// 设置压缩后文件存储位置
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                successCount[0]++;
                                if(successCount[0] == imageList.size()){
                                    Intent result = new Intent();
                                    result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(SelectPictureActivity.this, mSelectedFileList));
                                    setResult(RESULT_OK, result);
                                    onBackPressed();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("TAG", e.toString());
                            }
                        })
                        .launch();    //启动压缩
            }else {
                Intent result = new Intent();
                result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(SelectPictureActivity.this, mSelectedFileList));
                setResult(RESULT_OK, result);
                onBackPressed();
            }
        }
        return true;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //单选
        mSelectedFileList.add(mMediaAdapter.getData().get(position));
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(this, mSelectedFileList));
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }
}
