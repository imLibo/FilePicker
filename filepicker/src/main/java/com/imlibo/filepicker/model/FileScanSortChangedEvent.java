package com.imlibo.filepicker.model;

/**
 * FileScanSortChangedEvent
 * Created by 李波 on 2018/2/26.
 */

public class FileScanSortChangedEvent {
    private int sortType;

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
