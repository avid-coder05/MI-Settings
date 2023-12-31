package com.android.settings.deviceinfo.storage;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.storage.VolumeInfo;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageVolumeProvider;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.IOException;

/* loaded from: classes.dex */
public class VolumeSizesLoader extends AsyncLoaderCompat<PrivateStorageInfo> {
    private StorageStatsManager mStats;
    private VolumeInfo mVolume;
    private StorageVolumeProvider mVolumeProvider;

    public VolumeSizesLoader(Context context, StorageVolumeProvider storageVolumeProvider, StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) {
        super(context);
        this.mVolumeProvider = storageVolumeProvider;
        this.mStats = storageStatsManager;
        this.mVolume = volumeInfo;
    }

    static PrivateStorageInfo getVolumeSize(StorageVolumeProvider storageVolumeProvider, StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException {
        return new PrivateStorageInfo(storageVolumeProvider.getFreeBytes(storageStatsManager, volumeInfo), storageVolumeProvider.getTotalBytes(storageStatsManager, volumeInfo));
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public PrivateStorageInfo loadInBackground() {
        try {
            return getVolumeSize(this.mVolumeProvider, this.mStats, this.mVolume);
        } catch (IOException unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(PrivateStorageInfo privateStorageInfo) {
    }
}
