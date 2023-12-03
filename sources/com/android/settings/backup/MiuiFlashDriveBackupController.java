package com.android.settings.backup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.Iterator;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiFlashDriveBackupController extends BasePreferenceController implements LifecycleObserver, OnResume, OnDestroy {
    private static final String FLASH_DRIVE_BACKUP = "flash_drive_backup";
    private static final String TAG = "MiuiFlashDriveBackup";
    private Preference.OnPreferenceChangeListener mChangeListener;
    private CustomRadioButtonPreference mFlashDriveBackup;
    private boolean mIsSupportBRWithUsb;
    private final StorageEventListener mStorageListener;
    private StorageManager mStorageManager;

    public MiuiFlashDriveBackupController(Context context, String str) {
        super(context, str);
        this.mStorageListener = new StorageEventListener() { // from class: com.android.settings.backup.MiuiFlashDriveBackupController.1
            public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
                Log.d(MiuiFlashDriveBackupController.TAG, "onVolumeStateChanged: type = " + volumeInfo.getType());
                if (volumeInfo.getType() == 0) {
                    MiuiFlashDriveBackupController.this.updateFlashDriveBackupPreference();
                }
            }
        };
        this.mChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.backup.MiuiFlashDriveBackupController.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                ((CustomRadioButtonPreference) preference).setChecked(false);
                return false;
            }
        };
        this.mIsSupportBRWithUsb = supportBackupRestoreWithUsb();
    }

    private boolean supportBackupRestoreWithUsb() {
        if (!Build.IS_TABLET || SettingsFeatures.isSplitTablet(this.mContext)) {
            try {
                Bundle bundle = this.mContext.getPackageManager().getPackageInfo("com.miui.backup", 128).applicationInfo.metaData;
                if (bundle != null) {
                    return bundle.getBoolean("com.miui.backup.hasBackupToUsbFeature");
                }
                return false;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFlashDriveBackupPreference() {
        boolean z;
        Iterator it = this.mStorageManager.getVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            VolumeInfo volumeInfo = (VolumeInfo) it.next();
            DiskInfo findDiskById = volumeInfo.getType() == 0 ? this.mStorageManager.findDiskById(volumeInfo.getDiskId()) : null;
            if (findDiskById != null && findDiskById.isUsb() && volumeInfo.getState() == 2) {
                z = true;
                break;
            }
        }
        CustomRadioButtonPreference customRadioButtonPreference = this.mFlashDriveBackup;
        if (customRadioButtonPreference != null) {
            customRadioButtonPreference.setEnabled(z);
            this.mFlashDriveBackup.setSummary(this.mContext.getResources().getString(z ? R.string.flash_drive_backup_enable : R.string.flash_drive_backup_disable));
            this.mFlashDriveBackup.setCustomItemIcon(this.mContext.getResources().getDrawable(z ? R.drawable.ic_usb_enable : R.drawable.ic_usb_disable));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    @SuppressLint({"NewApi"})
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mFlashDriveBackup = (CustomRadioButtonPreference) preferenceScreen.findPreference(FLASH_DRIVE_BACKUP);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        if (this.mIsSupportBRWithUsb) {
            storageManager.registerListener(this.mStorageListener);
        }
        this.mFlashDriveBackup.setOnPreferenceChangeListener(this.mChangeListener);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (MiuiUtils.isUsbBackupEnable(this.mContext) && this.mIsSupportBRWithUsb) ? 0 : 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (this.mIsSupportBRWithUsb) {
            this.mStorageManager.unregisterListener(this.mStorageListener);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mIsSupportBRWithUsb) {
            updateFlashDriveBackupPreference();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
