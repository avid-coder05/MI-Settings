package com.android.settings.utils;

import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.MiuiAbstractPreferenceController;

/* loaded from: classes2.dex */
public abstract class MiuiBaseController<T extends Preference> extends MiuiAbstractPreferenceController {
    protected T mPreference;

    public MiuiBaseController(PreferenceScreen preferenceScreen) {
        super(preferenceScreen.getContext());
        attach();
        displayPreference(preferenceScreen);
        T t = (T) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = t;
        if (t == null) {
            Log.w("MiuiBaseController", "preference not found: " + getPreferenceKey());
        }
        create();
    }

    private final void attach() {
        onAttach();
    }

    private final void create() {
        if (this.mPreference == null) {
            return;
        }
        onCreate();
    }

    public final void destroy() {
        if (this.mPreference == null) {
            return;
        }
        onDestroy();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    protected void onAttach() {
    }

    protected void onCreate() {
    }

    protected void onDestroy() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPause() {
    }

    protected void onResume() {
    }

    public final void pause() {
        if (this.mPreference == null) {
            return;
        }
        onPause();
    }

    public final void resume() {
        if (this.mPreference == null) {
            return;
        }
        onResume();
        updateState(this.mPreference);
    }
}
