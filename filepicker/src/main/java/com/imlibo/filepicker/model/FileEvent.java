package com.imlibo.filepicker.model;

/**
 * FileEvent
 * Created by 李波 on 2018/2/7.
 */

public class FileEvent {
    private int position;

    public FileEvent() {
    }

    public FileEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
