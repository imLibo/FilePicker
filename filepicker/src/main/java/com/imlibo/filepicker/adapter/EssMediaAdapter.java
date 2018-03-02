package com.imlibo.filepicker.adapter;

import android.media.midi.MidiManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imlibo.filepicker.R;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.GlideApp;

import java.util.List;

/**
 * EssMediaAdapter
 * Created by 李波 on 2018/3/2.
 */

public class EssMediaAdapter extends BaseQuickAdapter<EssFile, BaseViewHolder> {

    private int mImageResize;


    public EssMediaAdapter(@Nullable List<EssFile> data) {
        super(R.layout.ess_media_item, data);
    }

    public void setmImageResize(int imageSize) {
        this.mImageResize = imageSize;
    }

    @Override
    protected void convert(BaseViewHolder helper, EssFile item) {
        helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImageResize));
        AppCompatCheckBox checkBox = helper.getView(R.id.check_view);
        ImageView imageView = helper.getView(R.id.media_thumbnail);
        GlideApp
                .with(mContext)
                .load(item.getUri())
                .placeholder(R.mipmap.png)
                .override(mImageResize, mImageResize)
                .centerCrop()
                .into(imageView);
        checkBox.setChecked(item.isChecked());
        helper.addOnClickListener(R.id.check_view);
        helper.addOnClickListener(R.id.media_thumbnail);
    }


}
