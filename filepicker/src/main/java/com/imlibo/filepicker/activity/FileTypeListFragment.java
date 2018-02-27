package com.imlibo.filepicker.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imlibo.filepicker.BaseFileFragment;
import com.imlibo.filepicker.R;
import com.imlibo.filepicker.SelectFileByScanEvent;
import com.imlibo.filepicker.adapter.FileListAdapter;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.FileScanActEvent;
import com.imlibo.filepicker.model.FileScanFragEvent;
import com.imlibo.filepicker.model.FileScanSortChangedEvent;
import com.imlibo.filepicker.presenter.SelectFileByScanPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileTypeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileTypeListFragment extends BaseFileFragment implements SelectFileByScanEvent, BaseQuickAdapter.OnItemClickListener {

    private static final String ARG_FileType = "ARG_FileType";
    private static final String ARG_IsSingle = "mIsSingle";
    private static final String ARG_MaxCount = "mMaxCount";
    private static final String ARG_SortType = "mSortType";

    private String mFileType;
    private boolean mIsSingle;
    private int mMaxCount;
    private int mSortType;

    private List<EssFile> mSelectedFileList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FileListAdapter mAdapter;
    private SelectFileByScanPresenter mPresenter;

    public FileTypeListFragment() {
    }

    public static FileTypeListFragment newInstance(String param1, boolean IsSingle, int mMaxCount, int mSortType) {
        FileTypeListFragment fragment = new FileTypeListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FileType, param1);
        args.putBoolean(ARG_IsSingle, IsSingle);
        args.putInt(ARG_MaxCount, mMaxCount);
        args.putInt(ARG_SortType, mSortType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileType = getArguments().getString(ARG_FileType);
            mIsSingle = getArguments().getBoolean(ARG_IsSingle);
            mMaxCount = getArguments().getInt(ARG_MaxCount);
            mSortType = getArguments().getInt(ARG_SortType);
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_file_type_list;
    }

    @Override
    protected void lazyLoad() {
        mPresenter.findFileList(mFileType,mSortType,0);
    }

    @Override
    protected void initUI(View view) {
        mPresenter = new SelectFileByScanPresenter(this);
        EventBus.getDefault().register(this);
        mRecyclerView = view.findViewById(R.id.rcv_file_list_scan);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FileListAdapter(new ArrayList<EssFile>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.loading_layout);
        mAdapter.setOnItemClickListener(this);
        super.initUI(view);
    }

    /**
     * 接收到Activity刷新最大数量后
     * @param event event
     */
    @Subscribe
    public void onFreshCount(FileScanActEvent event){
        mMaxCount = event.getCanSelectMaxCount();
    }

    /**
     * 接收到Activity改变排序方式后
     */
    @Subscribe
    public void onFreshSortType(FileScanSortChangedEvent event){
        mSortType = event.getSortType();
        mPresenter.findFileList(mFileType,mSortType,0);
    }

    @Override
    public void onFindFileList(List<EssFile> essFileList) {
        mAdapter.setNewData(essFileList);
        if(essFileList.isEmpty()){
            mAdapter.setEmptyView(R.layout.empty_file_list);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        EssFile item = mAdapter.getData().get(position);
        //选中某文件后，判断是否单选
        if(mIsSingle){
            mSelectedFileList.add(item);
            EventBus.getDefault().post(new FileScanFragEvent(item,true));
            return;
        }
        if(mAdapter.getData().get(position).isChecked()){
            int index = findFileIndex(item);
            if(index != -1){
                mSelectedFileList.remove(index);
                EventBus.getDefault().post(new FileScanFragEvent(item,false));
                mAdapter.getData().get(position).setChecked(!mAdapter.getData().get(position).isChecked());
                mAdapter.notifyItemChanged(position,"");
            }
        }else {
            if(mMaxCount <= 0){
                //超出最大可选择数量后
                return;
            }
            mSelectedFileList.add(item);
            EventBus.getDefault().post(new FileScanFragEvent(item,true));
            mAdapter.getData().get(position).setChecked(!mAdapter.getData().get(position).isChecked());
            mAdapter.notifyItemChanged(position,"");
        }

    }

    /**
     * 查找文件位置
     */
    private int findFileIndex(EssFile item) {
        for (int i = 0; i < mSelectedFileList.size(); i++) {
            if(mSelectedFileList.get(i).getAbsolutePath().equals(item.getAbsolutePath())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
