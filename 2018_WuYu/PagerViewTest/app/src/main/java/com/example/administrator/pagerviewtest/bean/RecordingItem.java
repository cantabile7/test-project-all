package com.example.administrator.pagerviewtest.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingItem implements Parcelable {
    private String name;
    private String filePath;
    private int Id;             //id in database
    private int length;         // length of recording
    private long time;          // date/time of the recording

    public RecordingItem() {
    }

    public RecordingItem(Parcel in) {
        this.name = in.readString();
        this.filePath = in.readString();
        this.Id = in.readInt();
        this.length = in.readInt();
        this.time = in.readLong();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static final Parcelable.Creator<RecordingItem> CREATOR =
        new Parcelable.Creator<RecordingItem>() {

            @Override
            public RecordingItem createFromParcel(Parcel source) {
                return new RecordingItem(source);
            }

            @Override
            public RecordingItem[] newArray(int size) {
                return new RecordingItem[size];
            }
        };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.filePath);
        dest.writeInt(this.Id);
        dest.writeInt(this.length);
        dest.writeLong(this.time);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
