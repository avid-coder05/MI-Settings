package com.android.settings.development;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes.dex */
public class WindowAnimationScalePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final float DEFAULT_VALUE = 1.0f;
    static final int WINDOW_ANIMATION_SCALE_SELECTOR = 0;
    private final String[] mListSummaries;
    private final String[] mListValues;
    private final IWindowManager mWindowManager;

    public WindowAnimationScalePreferenceController(Context context) {
        super(context);
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mListValues = context.getResources().getStringArray(R.array.window_animation_scale_values);
        this.mListSummaries = context.getResources().getStringArray(R.array.window_animation_scale_entries);
    }

    private void updateAnimationScaleValue() {
        try {
            int i = 0;
            float animationScale = this.mWindowManager.getAnimationScale(0);
            int i2 = 0;
            while (true) {
                String[] strArr = this.mListValues;
                if (i2 >= strArr.length) {
                    break;
                } else if (animationScale <= Float.parseFloat(strArr[i2])) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            DropDownPreference dropDownPreference = (DropDownPreference) this.mPreference;
            dropDownPreference.setValue(this.mListValues[i]);
            dropDownPreference.setSummary(this.mListSummaries[i]);
        } catch (RemoteException unused) {
        }
    }

    private void writeAnimationScaleOption(Object obj) {
        float parseFloat;
        if (obj != null) {
            try {
                parseFloat = Float.parseFloat(obj.toString());
            } catch (RemoteException unused) {
                return;
            }
        } else {
            parseFloat = DEFAULT_VALUE;
        }
        this.mWindowManager.setAnimationScale(0, parseFloat);
        updateAnimationScaleValue();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "window_animation_scale";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeAnimationScaleOption(null);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeAnimationScaleOption(obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateAnimationScaleValue();
    }
}
