package com.imlibo.filepicker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.imlibo.filepicker.R;
import com.imlibo.filepicker.adapter.FragmentPagerAdapter;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.FileScanActEvent;
import com.imlibo.filepicker.model.FileScanFragEvent;
import com.imlibo.filepicker.model.FileScanSortChangedEvent;
import com.imlibo.filepicker.util.Const;
import com.imlibo.filepicker.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 通过扫描来选择文件
 */
public class SelectFileByScanActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    /*只展示指定文件名后缀的文件*/
    private String[] mFileTypes;
    /*文件列表排序类型，默认按文件名升序排列*/
    private int mSortType = FileUtils.BY_NAME_ASC;
    /*是否是单选，默认否*/
    private boolean mIsSingle = false;
    /*最多可选择个数，默认10*/
    private int mMaxCount;
    /*todo 是否可预览文件，默认可预览*/
    private boolean mCanPreview = true;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;
    private MenuItem mCountMenuItem;

    private ArrayList<EssFile> mSelectedFileList = new ArrayList<>();
    /*当前选中排序方式的位置*/
    private int mSelectSortTypeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file_by_scan);
        EventBus.getDefault().register(this);
        mFileTypes = getIntent().getStringArrayExtra(Const.EXTRA_KEY_FILE_TYPE);
        mSortType = getIntent().getIntExtra(Const.EXTRA_KEY_SORT_TYPE, FileUtils.BY_NAME_ASC);
        mIsSingle = getIntent().getBooleanExtra(Const.EXTRA_KEY_IS_SINGLE, false);
        mMaxCount = getIntent().getIntExtra(Const.EXTRA_KEY_MAX_COUNT, 10);
        initUi();
        initData();
    }

    private void initData() {
    }

    private void initUi() {
        mViewPager = findViewById(R.id.vp_select_file_scan);
        mTabLayout = findViewById(R.id.tabl_select_file_scan);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("文件选择");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        List<Fragment> fragmentList = new ArrayList<>();
        for (String fileType :
                mFileTypes) {
            fragmentList.add(FileTypeListFragment.newInstance(fileType, mIsSingle, mMaxCount, mSortType));
        }
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(mFileTypes));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.addOnPageChangeListener(this);

    }


    /**
     * Fragment中选择文件后
     *
     * @param event event
     */
    @Subscribe
    public void onFragSelectFile(FileScanFragEvent event) {
        if (event.isAdd()) {
            if (mIsSingle) {
                mSelectedFileList.add(event.getSelectedFile());
                Intent result = new Intent();
                result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
                setResult(RESULT_OK, result);
                super.onBackPressed();
                return;
            }
            mSelectedFileList.add(event.getSelectedFile());
        } else {
            mSelectedFileList.remove(event.getSelectedFile());
        }
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
        EventBus.getDefault().post(new FileScanActEvent(mMaxCount - mSelectedFileList.size()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        mCountMenuItem = menu.findItem(R.id.browser_select_count);
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.browser_select_count) {
            //选中
            if (mSelectedFileList.isEmpty()) {
                return true;
            }
            //不为空
            Intent result = new Intent();
            result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
            setResult(RESULT_OK, result);
            super.onBackPressed();
        } else if (i == R.id.browser_sort) {
            //排序
            new AlertDialog
                    .Builder(this)
                    .setSingleChoiceItems(R.array.sort_list_scan, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSelectSortTypeIndex = which;
                        }
                    })
                    .setNegativeButton("降序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    mSortType = FileUtils.BY_NAME_DESC;
                                    break;
                                case 1:
                                    mSortType = FileUtils.BY_TIME_ASC;
                                    break;
                                case 2:
                                    mSortType = FileUtils.BY_SIZE_DESC;
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(mSortType));
                        }
                    })
                    .setPositiveButton("升序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    mSortType = FileUtils.BY_NAME_ASC;
                                    break;
                                case 1:
                                    mSortType = FileUtils.BY_TIME_DESC;
                                    break;
                                case 2:
                                    mSortType = FileUtils.BY_SIZE_ASC;
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(mSortType));
                        }
                    })
                    .setTitle("请选择")
                    .show();

        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        EventBus.getDefault().post(new FileScanActEvent(mMaxCount - mSelectedFileList.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
