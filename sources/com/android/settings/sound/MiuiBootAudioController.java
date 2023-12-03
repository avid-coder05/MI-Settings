package com.android.settings.sound;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class MiuiBootAudioController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String BOOT_AUDIO = "boot_audio";
    private CheckBoxPreference mPreference;

    public MiuiBootAudioController(Context context, String str) {
        super(context, str);
    }

    public static void setBootAudioOn(Context context) {
        try {
            Method declaredMethod = Class.forName("miui.content.res.BootAnimationHelper").getDeclaredMethod("updateBootAudioEnabled", Context.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(null, context);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference(this.mPreferenceKey);
            this.mPreference = checkBoxPreference;
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return TextUtils.equals(SystemProperties.get("ro.crypto.type", ""), "block") ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public int getBootupAudioStatus(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), BOOT_AUDIO, 1);
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

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mPreferenceKey.equals(preference.getKey())) {
            setBootupAudioStatus(this.mContext, ((Boolean) obj).booleanValue() ? 1 : 0);
            setBootAudioOn(this.mContext);
            return true;
        }
        return false;
    }

    public void setBootupAudioStatus(Context context, int i) {
        Settings.Global.putInt(context.getContentResolver(), BOOT_AUDIO, i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(getBootupAudioStatus(this.mContext) != 0);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
