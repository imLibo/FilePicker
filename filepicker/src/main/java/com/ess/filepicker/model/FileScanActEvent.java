package com.ess.filepicker.model;

/**
 * FileScanActEvent
 * Created by 李波 on 2018/2/23.
 */

public class FileScanActEvent {
    private int canSelectMaxCount;

    public FileScanActEvent(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }

    public int getCanSelectMaxCount() {
        return canSelectMaxCount;
    }

    public void setCanSelectMaxCount(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }
}
