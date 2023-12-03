package com.android.settings.deviceinfo;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.storage.VolumeInfo;
import android.util.Pair;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.PreferenceCategory;
import java.io.File;
import java.lang.ref.WeakReference;
import miuix.text.utilities.ExtraTextUtils;

/* loaded from: classes.dex */
public class MiuiStorageVolumePreferenceCategory extends PreferenceCategory {
    private final Resources mResources;
    private long mTotalSize;
    private UsageBarPreference mUsageBarPreference;

    /* loaded from: classes.dex */
    private static class ReadVolumeTask extends AsyncTask<VolumeInfo, Integer, Pair<Long, Long>> {
        private WeakReference<MiuiStorageVolumePreferenceCategory> mUI;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Pair<Long, Long> doInBackground(VolumeInfo... volumeInfoArr) {
            File path = volumeInfoArr[0].getPath();
            return new Pair<>(Long.valueOf(path.getTotalSpace()), Long.valueOf(path.getUsableSpace()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Pair<Long, Long> pair) {
            if (this.mUI.get() != null) {
                this.mUI.get().updateApproximate(((Long) pair.first).longValue(), ((Long) pair.second).longValue());
            }
        }
    }

    private String formatSize(long j) {
        return ExtraTextUtils.formatFileSize(getContext(), j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateApproximate(long j, long j2) {
        this.mTotalSize = j;
        long j3 = j - j2;
        this.mUsageBarPreference.clear();
        if (this.mTotalSize > 0) {
            this.mUsageBarPreference.addEntry(0, ((float) j3) / ((float) j), -65536);
            this.mUsageBarPreference.setTitle(this.mResources.getString(R.string.memory_title, formatSize(j2), formatSize(j)));
        }
        this.mUsageBarPreference.commit();
    }
}
