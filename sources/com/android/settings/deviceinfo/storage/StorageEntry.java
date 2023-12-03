package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import com.android.settings.R;
import java.io.File;

/* loaded from: classes.dex */
public class StorageEntry implements Comparable<StorageEntry>, Parcelable {
    public static final Parcelable.Creator<StorageEntry> CREATOR = new Parcelable.Creator<StorageEntry>() { // from class: com.android.settings.deviceinfo.storage.StorageEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageEntry createFromParcel(Parcel parcel) {
            return new StorageEntry(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageEntry[] newArray(int i) {
            return new StorageEntry[i];
        }
    };
    private final VolumeRecord mMissingVolumeRecord;
    private final DiskInfo mUnsupportedDiskInfo;
    private final VolumeInfo mVolumeInfo;
    private final String mVolumeInfoDescription;

    public StorageEntry(Context context, VolumeInfo volumeInfo) {
        this.mVolumeInfo = volumeInfo;
        this.mUnsupportedDiskInfo = null;
        this.mMissingVolumeRecord = null;
        if (isDefaultInternalStorage()) {
            this.mVolumeInfoDescription = context.getResources().getString(R.string.storage_default_internal_storage);
        } else {
            this.mVolumeInfoDescription = ((StorageManager) context.getSystemService(StorageManager.class)).getBestVolumeDescription(volumeInfo);
        }
    }

    private StorageEntry(Parcel parcel) {
        this.mVolumeInfo = parcel.readParcelable(VolumeInfo.class.getClassLoader());
        this.mUnsupportedDiskInfo = parcel.readParcelable(DiskInfo.class.getClassLoader());
        this.mMissingVolumeRecord = parcel.readParcelable(VolumeRecord.class.getClassLoader());
        this.mVolumeInfoDescription = parcel.readString();
    }

    public StorageEntry(DiskInfo diskInfo) {
        this.mVolumeInfo = null;
        this.mUnsupportedDiskInfo = diskInfo;
        this.mMissingVolumeRecord = null;
        this.mVolumeInfoDescription = null;
    }

    public StorageEntry(VolumeRecord volumeRecord) {
        this.mVolumeInfo = null;
        this.mUnsupportedDiskInfo = null;
        this.mMissingVolumeRecord = volumeRecord;
        this.mVolumeInfoDescription = null;
    }

    public static StorageEntry getDefaultInternalStorageEntry(Context context) {
        return new StorageEntry(context, ((StorageManager) context.getSystemService(StorageManager.class)).findVolumeById("private"));
    }

    @Override // java.lang.Comparable
    public int compareTo(StorageEntry storageEntry) {
        if (!isDefaultInternalStorage() || storageEntry.isDefaultInternalStorage()) {
            if (isDefaultInternalStorage() || !storageEntry.isDefaultInternalStorage()) {
                if (!isVolumeInfo() || storageEntry.isVolumeInfo()) {
                    if (isVolumeInfo() || !storageEntry.isVolumeInfo()) {
                        if (!isPrivate() || storageEntry.isPrivate()) {
                            if (isPrivate() || !storageEntry.isPrivate()) {
                                if (!isMounted() || storageEntry.isMounted()) {
                                    if (isMounted() || !storageEntry.isMounted()) {
                                        if (isVolumeRecordMissed() || !storageEntry.isVolumeRecordMissed()) {
                                            if ((!isVolumeRecordMissed() || storageEntry.isVolumeRecordMissed()) && getDescription() != null) {
                                                if (storageEntry.getDescription() == null) {
                                                    return -1;
                                                }
                                                return getDescription().compareTo(storageEntry.getDescription());
                                            }
                                            return 1;
                                        }
                                        return -1;
                                    }
                                    return 1;
                                }
                                return -1;
                            }
                            return 1;
                        }
                        return -1;
                    }
                    return 1;
                }
                return -1;
            }
            return 1;
        }
        return -1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StorageEntry) {
            StorageEntry storageEntry = (StorageEntry) obj;
            return isVolumeInfo() ? this.mVolumeInfo.equals(storageEntry.mVolumeInfo) : isDiskInfoUnsupported() ? this.mUnsupportedDiskInfo.equals(storageEntry.mUnsupportedDiskInfo) : this.mMissingVolumeRecord.equals(storageEntry.mMissingVolumeRecord);
        }
        return false;
    }

    public String getDescription() {
        return isVolumeInfo() ? this.mVolumeInfoDescription : isDiskInfoUnsupported() ? this.mUnsupportedDiskInfo.getDescription() : this.mMissingVolumeRecord.getNickname();
    }

    public String getDiskId() {
        if (isVolumeInfo()) {
            return this.mVolumeInfo.getDiskId();
        }
        if (isDiskInfoUnsupported()) {
            return this.mUnsupportedDiskInfo.getId();
        }
        return null;
    }

    public String getFsUuid() {
        if (isVolumeInfo()) {
            return this.mVolumeInfo.getFsUuid();
        }
        if (isDiskInfoUnsupported()) {
            return null;
        }
        return this.mMissingVolumeRecord.getFsUuid();
    }

    public String getId() {
        return isVolumeInfo() ? this.mVolumeInfo.getId() : isDiskInfoUnsupported() ? this.mUnsupportedDiskInfo.getId() : this.mMissingVolumeRecord.getFsUuid();
    }

    public File getPath() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        if (volumeInfo == null) {
            return null;
        }
        return volumeInfo.getPath();
    }

    public VolumeInfo getVolumeInfo() {
        return this.mVolumeInfo;
    }

    public int hashCode() {
        return isVolumeInfo() ? this.mVolumeInfo.hashCode() : isDiskInfoUnsupported() ? this.mUnsupportedDiskInfo.hashCode() : this.mMissingVolumeRecord.hashCode();
    }

    public boolean isDefaultInternalStorage() {
        return isVolumeInfo() && this.mVolumeInfo.getType() == 1 && TextUtils.equals(this.mVolumeInfo.getId(), "private");
    }

    public boolean isDiskInfoUnsupported() {
        return this.mUnsupportedDiskInfo != null;
    }

    public boolean isMounted() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        if (volumeInfo == null) {
            return false;
        }
        return volumeInfo.getState() == 2 || this.mVolumeInfo.getState() == 3;
    }

    public boolean isPrivate() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        return volumeInfo != null && volumeInfo.getType() == 1;
    }

    public boolean isPublic() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        return volumeInfo != null && volumeInfo.getType() == 0;
    }

    public boolean isUnmountable() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        return volumeInfo != null && volumeInfo.getState() == 6;
    }

    public boolean isUnmounted() {
        VolumeInfo volumeInfo = this.mVolumeInfo;
        return volumeInfo != null && volumeInfo.getState() == 0;
    }

    public boolean isVolumeInfo() {
        return this.mVolumeInfo != null;
    }

    public boolean isVolumeRecordMissed() {
        return this.mMissingVolumeRecord != null;
    }

    public String toString() {
        return isVolumeInfo() ? this.mVolumeInfo.toString() : isDiskInfoUnsupported() ? this.mUnsupportedDiskInfo.toString() : this.mMissingVolumeRecord.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mVolumeInfo, 0);
        parcel.writeParcelable(this.mUnsupportedDiskInfo, 0);
        parcel.writeParcelable(this.mMissingVolumeRecord, 0);
        parcel.writeString(this.mVolumeInfoDescription);
    }
}
