package com.ess.filepicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * BaseFileFragment
 * Created by 李波 on 2018/2/23.
 */

public abstract class BaseFileFragment extends Fragment{

    protected Activity mActivity = null;
    protected Bundle bundle = null;
    protected Fragment mFragment = null;
    /**
     * fragment管理器
     */
    protected FragmentManager mFManager = null;

    protected boolean isVisible; //是否可见的标志

    protected boolean isFirstLoad = true; //是否是第一次加载

    protected boolean isPrepared = false; //是否已经准备好

    protected boolean isOnDetach = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
        this.mFragment = this;
        isOnDetach = false;
        bundle = getArguments();
        mFManager = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentLayout(), container, false);
        initUI(view);
        onVisible();
        return view;
    }


    /**
     * fragment布局文件
     *
     * @return
     */
    public abstract int getFragmentLayout();


    /**
     * 设置是否是第一次加载 用于刷新数据
     *
     * @param isFirstLoad
     */
    public void setFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见的时候
     */
    private void onVisible() {
        if (isFirstLoad && isVisible && isPrepared) {
            lazyLoad();
            isFirstLoad = false;
        }
    }

    /**
     * 不可见时
     */
    protected void onInvisible() {

    }

    /**
     * 初始化控件
     * @param view
     */
    protected void initUI(View view) {
        isPrepared = true;
    }

    /**
     * 延迟加载
     * 需要在
     */
    protected abstract void lazyLoad();

    /**
     * 刷新数据
     *
     * @param msg
     */
    protected void onRefresh(Message msg) {
        if (!isOnDetach) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isOnDetach = true;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
