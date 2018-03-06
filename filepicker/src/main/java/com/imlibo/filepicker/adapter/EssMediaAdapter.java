package com.imlibo.filepicker.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imlibo.filepicker.FilePicker;
import com.imlibo.filepicker.R;
import com.imlibo.filepicker.model.EssFile;
import com.imlibo.filepicker.model.GlideApp;
import com.imlibo.filepicker.util.UiUtils;

import java.util.List;


/**
 * EssMediaAdapter
 * Created by 李波 on 2018/3/2.
 */

public class EssMediaAdapter extends BaseQuickAdapter<EssFile, BaseViewHolder> {

    private int mImageResize;

    public EssMediaAdapter(@Nullable List<EssFile> data) {
        super(R.layout.ess_media_item,data);
    }

    public void setmImageResize(int imageSize) {
        this.mImageResize = imageSize;
    }

    @Override
    protected void convert(BaseViewHolder helper, EssFile item) {
        if (item.getItemType() == EssFile.CAPTURE) {
            helper.getView(R.id.media).setVisibility(View.GONE);
            helper.getView(R.id.capture).setVisibility(View.VISIBLE);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(mImageResize- UiUtils.dpToPx(mContext,4), mImageResize));
            helper.addOnClickListener(R.id.capture);
        } else {
            helper.getView(R.id.capture).setVisibility(View.GONE);
            helper.getView(R.id.media).setVisibility(View.VISIBLE);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImageResize));
            ImageView imageView = helper.getView(R.id.media_thumbnail);
            GlideApp
                    .with(mContext)
                    .load(item.getUri())
                    .placeholder(FilePicker.getBuilder().getPlaceHolder() == null ? mContext.getResources().getDrawable(R.mipmap.png_holder) : FilePicker.getBuilder().getPlaceHolder())
                    .override(mImageResize, mImageResize)
                    .centerCrop()
                    .into(imageView);
            if(FilePicker.getBuilder().isSingleton() || FilePicker.getBuilder().getMaxCount() == 1){
                helper.setVisible(R.id.check_view,false);
            }else {
                AppCompatCheckBox checkBox = helper.getView(R.id.check_view);
                helper.setVisible(R.id.check_view,true);
                helper.addOnClickListener(R.id.check_view);
                helper.addOnClickListener(R.id.media_thumbnail);
                checkBox.setChecked(item.isChecked());
            }
        }

    }


}
