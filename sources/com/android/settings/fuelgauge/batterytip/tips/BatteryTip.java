package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;
import androidx.preference.Preference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/* loaded from: classes.dex */
public abstract class BatteryTip implements Comparable<BatteryTip>, Parcelable {
    static final SparseIntArray TIP_ORDER;
    protected boolean mNeedUpdate;
    protected boolean mShowDialog;
    protected int mState;
    protected int mType;

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        TIP_ORDER = sparseIntArray;
        sparseIntArray.append(3, 0);
        sparseIntArray.append(5, 1);
        sparseIntArray.append(8, 2);
        sparseIntArray.append(1, 3);
        sparseIntArray.append(2, 4);
        sparseIntArray.append(6, 5);
        sparseIntArray.append(0, 6);
        sparseIntArray.append(4, 7);
        sparseIntArray.append(7, 8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BatteryTip(int i, int i2, boolean z) {
        this.mType = i;
        this.mState = i2;
        this.mShowDialog = z;
        this.mNeedUpdate = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BatteryTip(Parcel parcel) {
        this.mType = parcel.readInt();
        this.mState = parcel.readInt();
        this.mShowDialog = parcel.readBoolean();
        this.mNeedUpdate = parcel.readBoolean();
    }

    @Override // java.lang.Comparable
    public int compareTo(BatteryTip batteryTip) {
        SparseIntArray sparseIntArray = TIP_ORDER;
        return sparseIntArray.get(this.mType) - sparseIntArray.get(batteryTip.mType);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public abstract int getIconId();

    public int getIconTintColorId() {
        return -1;
    }

    public int getState() {
        return this.mState;
    }

    public abstract CharSequence getSummary(Context context);

    public abstract CharSequence getTitle(Context context);

    public int getType() {
        return this.mType;
    }

    public boolean isVisible() {
        return this.mState != 2;
    }

    public abstract void log(Context context, MetricsFeatureProvider metricsFeatureProvider);

    public boolean needUpdate() {
        return this.mNeedUpdate;
    }

    public boolean shouldShowDialog() {
        return this.mShowDialog;
    }

    public String toString() {
        return "type=" + this.mType + " state=" + this.mState;
    }

    public void updatePreference(Preference preference) {
        Context context = preference.getContext();
        preference.setTitle(getTitle(context));
        preference.setSummary(getSummary(context));
        preference.setIcon(getIconId());
        int iconTintColorId = getIconTintColorId();
        if (iconTintColorId != -1) {
            preference.getIcon().setTint(context.getColor(iconTintColorId));
        }
    }

    public abstract void updateState(BatteryTip batteryTip);

    public void validateCheck(Context context) {
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mState);
        parcel.writeBoolean(this.mShowDialog);
        parcel.writeBoolean(this.mNeedUpdate);
    }
}
