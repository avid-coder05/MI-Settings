package com.google.android.setupcompat.portal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.setupcompat.internal.Preconditions;

/* loaded from: classes2.dex */
public class ProgressServiceComponent implements Parcelable {
    public static final Parcelable.Creator<ProgressServiceComponent> CREATOR = new Parcelable.Creator<ProgressServiceComponent>() { // from class: com.google.android.setupcompat.portal.ProgressServiceComponent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgressServiceComponent createFromParcel(Parcel parcel) {
            return ProgressServiceComponent.newBuilder().setPackageName(parcel.readString()).setTaskName(parcel.readString()).setSilentMode(parcel.readInt() == 1).setDisplayName(parcel.readInt()).setDisplayIcon(parcel.readInt()).setServiceIntent((Intent) parcel.readParcelable(Intent.class.getClassLoader())).setItemClickIntent((Intent) parcel.readParcelable(Intent.class.getClassLoader())).setAutoRebind(parcel.readInt() == 1).setTimeoutForReRegister(parcel.readLong()).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgressServiceComponent[] newArray(int i) {
            return new ProgressServiceComponent[i];
        }
    };
    private final boolean autoRebind;
    private final int displayIconResId;
    private final int displayNameResId;
    private final boolean isSilent;
    private final Intent itemClickIntent;
    private final String packageName;
    private final Intent serviceIntent;
    private final String taskName;
    private final long timeoutForReRegister;

    /* loaded from: classes2.dex */
    public static class Builder {
        private boolean autoRebind;
        private int displayIconResId;
        private int displayNameResId;
        private boolean isSilent;
        private Intent itemClickIntent;
        private String packageName;
        private Intent serviceIntent;
        private String taskName;
        private long timeoutForReRegister;

        private Builder() {
            this.isSilent = false;
            this.autoRebind = true;
            this.timeoutForReRegister = 0L;
        }

        public ProgressServiceComponent build() {
            Preconditions.checkNotNull(this.packageName, "packageName cannot be null.");
            Preconditions.checkNotNull(this.taskName, "serviceClass cannot be null.");
            Preconditions.checkNotNull(this.serviceIntent, "Service intent cannot be null.");
            Preconditions.checkNotNull(this.itemClickIntent, "Item click intent cannot be null");
            if (!this.isSilent) {
                Preconditions.checkArgument(this.displayNameResId != 0, "Invalidate resource id of display name");
                Preconditions.checkArgument(this.displayIconResId != 0, "Invalidate resource id of display icon");
            }
            return new ProgressServiceComponent(this.packageName, this.taskName, this.isSilent, this.autoRebind, this.timeoutForReRegister, this.displayNameResId, this.displayIconResId, this.serviceIntent, this.itemClickIntent);
        }

        public Builder setAutoRebind(boolean z) {
            this.autoRebind = z;
            return this;
        }

        public Builder setDisplayIcon(int i) {
            this.displayIconResId = i;
            return this;
        }

        public Builder setDisplayName(int i) {
            this.displayNameResId = i;
            return this;
        }

        public Builder setItemClickIntent(Intent intent) {
            this.itemClickIntent = intent;
            return this;
        }

        public Builder setPackageName(String str) {
            this.packageName = str;
            return this;
        }

        public Builder setServiceIntent(Intent intent) {
            this.serviceIntent = intent;
            return this;
        }

        public Builder setSilentMode(boolean z) {
            this.isSilent = z;
            return this;
        }

        public Builder setTaskName(String str) {
            this.taskName = str;
            return this;
        }

        public Builder setTimeoutForReRegister(long j) {
            this.timeoutForReRegister = j;
            return this;
        }
    }

    private ProgressServiceComponent(String str, String str2, boolean z, boolean z2, long j, int i, int i2, Intent intent, Intent intent2) {
        this.packageName = str;
        this.taskName = str2;
        this.isSilent = z;
        this.autoRebind = z2;
        this.timeoutForReRegister = j;
        this.displayNameResId = i;
        this.displayIconResId = i2;
        this.serviceIntent = intent;
        this.itemClickIntent = intent2;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getDisplayIcon() {
        return this.displayIconResId;
    }

    public int getDisplayName() {
        return this.displayNameResId;
    }

    public Intent getItemClickIntent() {
        return this.itemClickIntent;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Intent getServiceIntent() {
        return this.serviceIntent;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public long getTimeoutForReRegister() {
        return this.timeoutForReRegister;
    }

    public boolean isAutoRebind() {
        return this.autoRebind;
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getPackageName());
        parcel.writeString(getTaskName());
        parcel.writeInt(isSilent() ? 1 : 0);
        parcel.writeInt(getDisplayName());
        parcel.writeInt(getDisplayIcon());
        parcel.writeParcelable(getServiceIntent(), 0);
        parcel.writeParcelable(getItemClickIntent(), 0);
        parcel.writeInt(isAutoRebind() ? 1 : 0);
        parcel.writeLong(getTimeoutForReRegister());
    }
}
