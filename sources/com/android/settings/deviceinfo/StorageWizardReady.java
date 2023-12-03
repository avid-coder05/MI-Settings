package com.android.settings.deviceinfo;

import android.os.Bundle;
import android.os.storage.VolumeInfo;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class StorageWizardReady extends StorageWizardBase {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mDisk == null) {
            finish();
            return;
        }
        setContentView(R.layout.storage_wizard_generic);
        setBodyText(R.string.storage_wizard_ready_title, this.mDisk.getDescription());
        VolumeInfo findFirstVolume = findFirstVolume(1);
        boolean booleanExtra = getIntent().getBooleanExtra("migrate_skip", false);
        if (findFirstVolume == null || booleanExtra) {
            setSecondaryBodyText(R.string.storage_wizard_ready_v2_external_body, this.mDisk.getDescription());
        } else {
            setSecondaryBodyText(R.string.storage_wizard_ready_v2_internal_body, this.mDisk.getDescription(), this.mDisk.getShortDescription());
        }
        setNextButtonText(R.string.done, new CharSequence[0]);
        setBackButtonVisibility(4);
        setBackButtonVisibility(4);
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateNext(View view) {
        finishAffinity();
    }
}
