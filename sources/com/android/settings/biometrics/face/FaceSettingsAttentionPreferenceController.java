package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class FaceSettingsAttentionPreferenceController extends FaceSettingsPreferenceController {
    public static final String KEY = "security_settings_face_require_attention";
    private FaceManager mFaceManager;
    private final FaceManager.GetFeatureCallback mGetFeatureCallback;
    private SwitchPreference mPreference;
    private final FaceManager.SetFeatureCallback mSetFeatureCallback;
    private byte[] mToken;

    public FaceSettingsAttentionPreferenceController(Context context) {
        this(context, KEY);
    }

    public FaceSettingsAttentionPreferenceController(Context context, String str) {
        super(context, str);
        this.mSetFeatureCallback = new FaceManager.SetFeatureCallback() { // from class: com.android.settings.biometrics.face.FaceSettingsAttentionPreferenceController.1
            public void onCompleted(boolean z, int i) {
                if (i == 1) {
                    FaceSettingsAttentionPreferenceController.this.mPreference.setEnabled(true);
                    if (!z) {
                        FaceSettingsAttentionPreferenceController.this.mPreference.setChecked(!FaceSettingsAttentionPreferenceController.this.mPreference.isChecked());
                        return;
                    }
                    Settings.Secure.putIntForUser(((AbstractPreferenceController) FaceSettingsAttentionPreferenceController.this).mContext.getContentResolver(), "face_unlock_attention_required", FaceSettingsAttentionPreferenceController.this.mPreference.isChecked() ? 1 : 0, FaceSettingsAttentionPreferenceController.this.getUserId());
                }
            }
        };
        this.mGetFeatureCallback = new FaceManager.GetFeatureCallback() { // from class: com.android.settings.biometrics.face.FaceSettingsAttentionPreferenceController.2
            public void onCompleted(boolean z, int[] iArr, boolean[] zArr) {
                boolean z2 = false;
                for (int i = 0; i < iArr.length; i++) {
                    if (iArr[i] == 1) {
                        z2 = zArr[i];
                    }
                }
                FaceSettingsAttentionPreferenceController.this.mPreference.setEnabled(z);
                FaceSettingsAttentionPreferenceController.this.mPreference.setChecked(z2);
            }
        };
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(KEY);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (FaceSettings.isFaceHardwareDetected(this.mContext)) {
            this.mPreference.setEnabled(false);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mPreference.setEnabled(false);
        this.mPreference.setChecked(z);
        return true;
    }

    public void setToken(byte[] bArr) {
        this.mToken = bArr;
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
