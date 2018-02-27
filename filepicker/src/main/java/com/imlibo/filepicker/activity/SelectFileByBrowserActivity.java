package com.imlibo.filepicker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imlibo.filepicker.R;
import com.imlibo.filepicker.SelectFileByBrowserEvent;
import com.imlibo.filepicker.adapter.BreadAdapter;
import com.imlibo.filepicker.adapter.FileListAdapter;
import com.imlibo.filepicker.adapter.SelectSdcardAdapter;
import com.imlibo.filepicker.model.BreadModel;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.FileEvent;
import com.imlibo.filepicker.presenter.SelectFileByBrowserPresenter;
import com.imlibo.filepicker.util.Const;
import com.imlibo.filepicker.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件浏览界面
 */
public class SelectFileByBrowserActivity extends AppCompatActivity
        implements SelectFileByBrowserEvent,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {

    /*只展示指定文件名后缀的文件*/
    private String[] mFileTypes;
    /*文件列表排序类型，默认按文件名升序排列*/
    private int mSortType = FileUtils.BY_NAME_ASC;
    /*是否是单选，默认否*/
    private boolean mIsSingle = false;
    /*最多可选择个数*/
    private int mMaxCount = 10;
    /*todo 是否可预览文件，默认可预览*/
    private boolean mCanPreview = true;

    /*当前目录，默认是SD卡根目录*/
    private String mCurFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    /*所有可访问存储设备列表*/
    private List<String> mSdCardList;

    private SelectFileByBrowserPresenter mPresenter;
    private FileListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView mBreadRecyclerView;
    private ImageView mImbSelectSdCard;
    private Toolbar mToolBar;
    private BreadAdapter mBreadAdapter;
    private PopupWindow mSelectSdCardWindow;

    /*是否刚才切换了SD卡路径*/
    private boolean mHasChangeSdCard = false;
    /*已选中的文件列表*/
    private ArrayList<EssFile> mSelectedFileList = new ArrayList<>();
    /*当前选中排序方式的位置*/
    private int mSelectSortTypeIndex = 0;
    private MenuItem mCountMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        EventBus.getDefault().register(this);
        mFileTypes = getIntent().getStringArrayExtra(Const.EXTRA_KEY_FILE_TYPE);
        mSortType = getIntent().getIntExtra(Const.EXTRA_KEY_SORT_TYPE, FileUtils.BY_NAME_ASC);
        mIsSingle = getIntent().getBooleanExtra(Const.EXTRA_KEY_IS_SINGLE, false);
        mMaxCount = getIntent().getIntExtra(Const.EXTRA_KEY_MAX_COUNT, 10);
        mPresenter = new SelectFileByBrowserPresenter(this);

        mSdCardList = FileUtils.getAllSdPaths(this);
        if (!mSdCardList.isEmpty()) {
            mCurFolder = mSdCardList.get(0) + File.separator;
        }
        initUi();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        mCountMenuItem = menu.findItem(R.id.browser_select_count);
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
        return true;
    }

    private void initUi() {
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

        mRecyclerView = findViewById(R.id.rcv_file_list);
        mBreadRecyclerView = findViewById(R.id.breadcrumbs_view);
        mImbSelectSdCard = findViewById(R.id.imb_select_sdcard);
        mImbSelectSdCard.setOnClickListener(this);
        if (!mSdCardList.isEmpty() && mSdCardList.size() > 1) {
            mImbSelectSdCard.setVisibility(View.VISIBLE);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FileListAdapter(new ArrayList<EssFile>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(this);

        mBreadRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBreadAdapter = new BreadAdapter(new ArrayList<BreadModel>());
        mBreadRecyclerView.setAdapter(mBreadAdapter);
        mBreadAdapter.bindToRecyclerView(mBreadRecyclerView);
        mBreadAdapter.setOnItemChildClickListener(this);

    }

    private void initData() {
        mPresenter.findFileList(mSelectedFileList, mCurFolder, mFileTypes, mSortType);
    }

    /**
     * 查找到文件列表后
     *
     * @param queryPath 查询路径
     * @param fileList  文件列表
     */
    @Override
    public void onFindFileList(String queryPath, List<EssFile> fileList) {
        if (fileList.isEmpty()) {
            mAdapter.setEmptyView(R.layout.empty_file_list);
        }
        mCurFolder = queryPath;
        mAdapter.setNewData(fileList);
        List<BreadModel> breadModelList = FileUtils.getBreadModeListFromPath(mSdCardList, mCurFolder);
        if (mHasChangeSdCard) {
            mBreadAdapter.setNewData(breadModelList);
            mHasChangeSdCard = false;
        } else {
            if (breadModelList.size() > mBreadAdapter.getData().size()) {
                //新增
                List<BreadModel> newList = BreadModel.getNewBreadModel(mBreadAdapter.getData(), breadModelList);
                mBreadAdapter.addData(newList);
            } else {
                //减少
                int removePosition = BreadModel.getRemovedBreadModel(mBreadAdapter.getData(), breadModelList);
                if (removePosition > 0) {
                    mBreadAdapter.setNewData(mBreadAdapter.getData().subList(0, removePosition));
                }
            }
        }

        mBreadRecyclerView.smoothScrollToPosition(mBreadAdapter.getItemCount() - 1);
        //先让其滚动到顶部，然后再scrollBy，滚动到之前保存的位置
        mRecyclerView.scrollToPosition(0);
        int scrollYPosition = mBreadAdapter.getData().get(mBreadAdapter.getData().size() - 1).getPrePosition();
        //恢复之前的滚动位置
        mRecyclerView.scrollBy(0, scrollYPosition);
    }

    /**
     * 显示选择SdCard的PopupWindow
     * 点击其他区域隐藏，阴影
     */
    private void showPopupWindow() {
        if (mSelectSdCardWindow != null) {
            mSelectSdCardWindow.showAsDropDown(mImbSelectSdCard);
            return;
        }
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_select_sdcard, null);
        mSelectSdCardWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectSdCardWindow.setFocusable(true);
        mSelectSdCardWindow.setOutsideTouchable(true);
        RecyclerView recyclerView = popView.findViewById(R.id.rcv_pop_select_sdcard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final SelectSdcardAdapter adapter = new SelectSdcardAdapter(FileUtils.getAllSdCardList(mSdCardList));
        recyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapterIn, View view, int position) {
                mSelectSdCardWindow.dismiss();
                mHasChangeSdCard = true;
                mPresenter.findFileList(mSelectedFileList, FileUtils.getChangeSdCard(adapter.getData().get(position), mSdCardList), mFileTypes, mSortType);
            }
        });
        mSelectSdCardWindow.showAsDropDown(mImbSelectSdCard);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 查找子文件/子文件夹个数
     *
     * @param event EventBus事件
     */
    @Subscribe
    public void findChildCounts(FileEvent event) {
        mPresenter.findChildFileAndFolderCount(event.getPosition(), mAdapter.getData().get(event.getPosition()).getAbsolutePath(), mFileTypes);
    }

    @Override
    public void onFindChildFileAndFolderCount(int position, String childFileCount, String childFolderCount) {
        mAdapter.getData().get(position).setChildCounts(childFileCount, childFolderCount);
        mAdapter.notifyItemChanged(position, "childCountChanges");
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter.equals(mAdapter)) {
            EssFile item = mAdapter.getData().get(position);
            if (item.isDirectory()) {
                //点击文件夹
                //保存当前的垂直滚动位置
                mBreadAdapter.getData().get(mBreadAdapter.getData().size() - 1).setPrePosition(mRecyclerView.computeVerticalScrollOffset());
                mPresenter.findFileList(mSelectedFileList, mCurFolder + item.getName() + File.separator, mFileTypes, mSortType);
            } else {
                //选中某文件后，判断是否单选
                if (mIsSingle) {
                    Intent result = new Intent();
                    result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
                    setResult(RESULT_OK, result);
                    super.onBackPressed();
                    return;
                }
                if (mAdapter.getData().get(position).isChecked()) {
                    int index = findFileIndex(item);
                    if (index != -1) {
                        mSelectedFileList.remove(index);
                    }
                } else {
                    if (mSelectedFileList.size() >= mMaxCount) {
                        //超出最大可选择数量后
                        return;
                    }
                    mSelectedFileList.add(item);
                }
                mAdapter.getData().get(position).setChecked(!mAdapter.getData().get(position).isChecked());
                mAdapter.notifyItemChanged(position, "");
                mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(mMaxCount)));
            }
        }
    }

    /**
     * 查找文件位置
     */
    private int findFileIndex(EssFile item) {
        for (int i = 0; i < mSelectedFileList.size(); i++) {
            if (mSelectedFileList.get(i).getAbsolutePath().equals(item.getAbsolutePath())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        if (!FileUtils.canBackParent(mCurFolder, mSdCardList)) {
            super.onBackPressed();
            return;
        }
        mPresenter.findFileList(mSelectedFileList, new File(mCurFolder).getParentFile().getAbsolutePath() + File.separator, mFileTypes, mSortType);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter.equals(mBreadAdapter) && view.getId() == R.id.btn_bread) {
            //点击某个路径时
            String queryPath = FileUtils.getBreadModelListByPosition(mSdCardList, mBreadAdapter.getData(), position);
            if (mCurFolder.equals(queryPath)) {
                return;
            }
            mPresenter.findFileList(mSelectedFileList, queryPath, mFileTypes, mSortType);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imb_select_sdcard) {
            showPopupWindow();
        }
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
                    .setSingleChoiceItems(R.array.sort_list, 0, new DialogInterface.OnClickListener() {
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
                                case 3:
                                    mSortType = FileUtils.BY_EXTENSION_DESC;
                                    break;
                            }
                            //恢复排序
                            mBreadAdapter.getData().get(mBreadAdapter.getData().size() - 1).setPrePosition(0);
                            mPresenter.findFileList(mSelectedFileList, mCurFolder, mFileTypes, mSortType);
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
                                case 3:
                                    mSortType = FileUtils.BY_EXTENSION_ASC;
                                    break;
                            }
                            //恢复排序
                            mBreadAdapter.getData().get(mBreadAdapter.getData().size() - 1).setPrePosition(0);
                            mPresenter.findFileList(mSelectedFileList, mCurFolder, mFileTypes, mSortType);
                        }
                    })
                    .setTitle("请选择")
                    .show();

        }
        return true;
    }

}
