package com.ess.filepicker.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * DialogUtil
 * Created by 李波 on 2018/2/27.
 */

public class DialogUtil {

    /***
     * 显示权限拒绝提醒对话框
     */
    public static void showPermissionDialog(final Activity mActivity, String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder
                .setMessage("您未授权 " + permission + " 权限, 请在权限管理中开启此权限。")
                .setTitle("提示")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        mActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
