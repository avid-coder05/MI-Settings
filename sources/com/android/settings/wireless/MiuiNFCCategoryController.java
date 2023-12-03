package com.android.settings.wireless;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.util.Log;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;

/* loaded from: classes2.dex */
public class MiuiNFCCategoryController extends AbstractPreferenceController implements LifecycleObserver {
    private NfcAdapter mNfcAdapter;

    public MiuiNFCCategoryController(Context context, Lifecycle lifecycle) {
        super(context);
        lifecycle.addObserver(this);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        Log.i("MiuiNFCCategoryController", "MiuiNFCCategoryController: mNfcAdapter = " + this.mNfcAdapter);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "nfc_category";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (SettingsFeatures.isNeedShowMiuiNFC() || this.mNfcAdapter == null) ? false : true;
    }
}
