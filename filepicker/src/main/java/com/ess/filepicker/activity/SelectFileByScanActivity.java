package com.ess.filepicker.activity;

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

import com.ess.filepicker.R;
import com.ess.filepicker.SelectOptions;
import com.ess.filepicker.adapter.FragmentPagerAdapter;
import com.ess.filepicker.loader.EssMimeTypeCollection;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.model.FileScanActEvent;
import com.ess.filepicker.model.FileScanFragEvent;
import com.ess.filepicker.model.FileScanSortChangedEvent;
import com.ess.filepicker.util.Const;
import com.ess.filepicker.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 通过扫描来选择文件
 */
public class SelectFileByScanActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

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
        for (int i = 0; i < SelectOptions.getInstance().getFileTypes().length; i++) {
            fragmentList.add(FileTypeListFragment.newInstance(SelectOptions.getInstance().getFileTypes()[i],SelectOptions.getInstance().isSingle,SelectOptions.getInstance().maxCount,SelectOptions.getInstance().getSortType(),EssMimeTypeCollection.LOADER_ID+i));
        }
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(SelectOptions.getInstance().getFileTypes()));
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
            if (SelectOptions.getInstance().isSingle) {
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
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
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
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
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
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_DESC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_ASC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_DESC);
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(),mViewPager.getCurrentItem()));
                        }
                    })
                    .setPositiveButton("升序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_ASC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_DESC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_ASC);
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(),mViewPager.getCurrentItem()));
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
        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
