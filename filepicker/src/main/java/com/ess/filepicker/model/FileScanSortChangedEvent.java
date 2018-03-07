package com.ess.filepicker.model;

/**
 * FileScanSortChangedEvent
 * Created by 李波 on 2018/2/26.
 */

public class FileScanSortChangedEvent {
    private int sortType;
    private int currentItem;

    public FileScanSortChangedEvent(int sortType, int currentItem) {
        this.sortType = sortType;
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public FileScanSortChangedEvent(int sortType) {
        this.sortType = sortType;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }
}
