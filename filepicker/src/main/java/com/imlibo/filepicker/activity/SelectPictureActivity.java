package com.imlibo.filepicker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.imlibo.filepicker.R;
import com.imlibo.filepicker.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 选择图片界面
 */
public class SelectPictureActivity extends AppCompatActivity {

    /*1. 只展示指定文件名后缀的文件*/
    private String[] mFileTypes;
    /*2. 文件列表排序类型，默认按文件名升序排列*/
    private int mSortType = FileUtils.BY_NAME_ASC;
    /*3. 是否是多选，默认否*/
    private boolean mIsMultiSelect = true;
    /*4. 最多可选择个数，默认10*/
    private int mMaxCount;
    /*5. 是否需要压缩，默认返回压缩之后的图片*/
    private boolean mNeedCompress = true;
    /*6. 是否需要裁剪/旋转等，默认不裁剪*/
    private boolean mNeedClip = false;
    /*7. 是否需要显示照相机*/
    private boolean mNeedCamera = true;
    /*8. 相机为自定义相机还是系统相机，仅当mNeedCamera=true时有效*/
    private boolean mUseCustomCamera = true;
    /*9. 是否可预览图片，默认可预览*/
    private boolean mCanPreview = true;

    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        EventBus.getDefault().register(this);
        mToolBar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.rcv_file_picture_list);



    }



}
