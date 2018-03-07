package com.ess.filepicker;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;


/**
 * FilePicker 构造器
 * Created by 李波 on 2018/2/8.
 *
 */

public final class FilePicker {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private FilePicker(Activity activity) {
        this(activity,null);
    }

    private FilePicker(Fragment fragment){
        this(fragment.getActivity(),fragment);
    }

    private FilePicker(Activity mContext, Fragment mFragment) {
        this.mContext = new WeakReference<>(mContext);
        this.mFragment = new WeakReference<>(mFragment);
    }

    public static FilePicker from(Activity activity){
        return new FilePicker(activity);
    }

    public static FilePicker from(Fragment fragment){
        return new FilePicker(fragment);
    }

    public SelectCreator chooseForBrowser(){
        return new SelectCreator(this,SelectOptions.CHOOSE_TYPE_BROWSER);
    }

    public SelectCreator chooseForMimeType(){
        return new SelectCreator(this,SelectOptions.CHOOSE_TYPE_SCAN);
    }

    public SelectCreator chooseMedia(){
        return new SelectCreator(this,SelectOptions.CHOOSE_TYPE_MEDIA);
    }


    public Activity getActivity() {
        return mContext.get();
    }

    public Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
