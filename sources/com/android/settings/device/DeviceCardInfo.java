package com.android.settings.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/* loaded from: classes.dex */
public class DeviceCardInfo implements Parcelable {
    public static final Parcelable.Creator<DeviceCardInfo> CREATOR = new Parcelable.Creator<DeviceCardInfo>() { // from class: com.android.settings.device.DeviceCardInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceCardInfo createFromParcel(Parcel parcel) {
            return new DeviceCardInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceCardInfo[] newArray(int i) {
            return new DeviceCardInfo[i];
        }
    };
    private View.OnClickListener listener;
    private int mIconResId;
    private int mIndex;
    private String mKey;
    private String mTitle;
    private int mType;
    private String mValue;

    public DeviceCardInfo() {
    }

    protected DeviceCardInfo(Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mValue = parcel.readString();
        this.mIconResId = parcel.readInt();
        this.mType = parcel.readInt();
        this.mKey = parcel.readString();
        this.mIndex = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public String getKey() {
        return this.mKey;
    }

    public View.OnClickListener getListener() {
        return this.listener;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int getType() {
        return this.mType;
    }

    public String getValue() {
        return this.mValue;
    }

    public void setIconResId(int i) {
        this.mIconResId = i;
    }

    public void setIndex(int i) {
        this.mIndex = i;
    }

    public void setKey(String str) {
        this.mKey = str;
    }

    public void setListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public void setType(int i) {
        this.mType = i;
    }

    public void setValue(String str) {
        this.mValue = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mValue);
        parcel.writeInt(this.mIconResId);
        parcel.writeInt(this.mType);
        parcel.writeString(this.mKey);
        parcel.writeInt(this.mIndex);
    }
}
