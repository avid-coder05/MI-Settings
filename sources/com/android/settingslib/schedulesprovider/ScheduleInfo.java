package com.android.settingslib.schedulesprovider;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class ScheduleInfo implements Parcelable {
    public static final Parcelable.Creator<ScheduleInfo> CREATOR = new Parcelable.Creator<ScheduleInfo>() { // from class: com.android.settingslib.schedulesprovider.ScheduleInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScheduleInfo createFromParcel(Parcel parcel) {
            return new ScheduleInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScheduleInfo[] newArray(int i) {
            return new ScheduleInfo[i];
        }
    };
    private final PendingIntent mPendingIntent;
    private final String mSummary;
    private final String mTitle;

    private ScheduleInfo(Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mSummary = parcel.readString();
        this.mPendingIntent = (PendingIntent) parcel.readParcelable(PendingIntent.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "title: " + this.mTitle + ", summary: " + this.mSummary + ", pendingIntent: " + this.mPendingIntent;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mSummary);
        parcel.writeParcelable(this.mPendingIntent, i);
    }
}
