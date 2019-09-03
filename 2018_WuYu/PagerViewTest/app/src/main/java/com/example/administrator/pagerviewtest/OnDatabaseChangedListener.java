package com.example.administrator.pagerviewtest;

import com.example.administrator.pagerviewtest.bean.RecordingItem;

public interface OnDatabaseChangedListener {
    void onAddNewItem(RecordingItem item);
//    void onRenameNewItem(String fileName, int position);
}
