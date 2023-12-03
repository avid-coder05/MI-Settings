package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Collections;
import miuix.preference.DropDownPreference;
import miuix.visual.check.VisualCheckGroup;

/* loaded from: classes.dex */
public class InputMethodFullScreenManager extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, VisualCheckGroup.OnCheckedChangeListener {
    private Activity mActivity;
    private EnableFullScreenKeyboardObserver mEnableFullScreenKeyboardObserver;
    private FullScreenInputMethodPreference mFullIMEPreference;
    private DropDownPreference mLeftPreference;
    private DropDownPreference mMiddlePreference;
    private DropDownPreference mRightPreference;
    private ArrayList<String> mSelectedValueSideList = new ArrayList<>();
    private ArrayList<String> mSelectedValueMiddleList = new ArrayList<>();

    /* loaded from: classes.dex */
    private class EnableFullScreenKeyboardObserver extends ContentObserver {
        public EnableFullScreenKeyboardObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            InputMethodFullScreenManager.this.updateFunctionPreferenceEnable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFunctionPreferenceEnable() {
        int i = Settings.Secure.getInt(getContentResolver(), "enable_miui_ime_bottom_view", 1);
        this.mLeftPreference.setEnabled(i == 1);
        this.mRightPreference.setEnabled(i == 1);
        this.mMiddlePreference.setEnabled(i == 1);
    }

    @Override // miuix.visual.check.VisualCheckGroup.OnCheckedChangeListener
    public void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i) {
        if (i == R.id.high_keyboard) {
            Settings.Secure.putInt(this.mActivity.getContentResolver(), "enable_miui_ime_bottom_view", 1);
            InputMethodFunctionSelectUtils.addMiuiBottomEnableRecord(this.mActivity, "1");
        } else if (i != R.id.default_keyboard) {
            Log.e("FullScreenInputMethod", "check full screen ime error.");
        } else {
            Settings.Secure.putInt(this.mActivity.getContentResolver(), "enable_miui_ime_bottom_view", 0);
            InputMethodFunctionSelectUtils.addMiuiBottomEnableRecord(this.mActivity, "0");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.full_keyboard_settings);
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        Collections.addAll(this.mSelectedValueSideList, activity.getResources().getStringArray(R.array.input_method_function_title));
        Collections.addAll(this.mSelectedValueMiddleList, this.mActivity.getResources().getStringArray(R.array.input_method_middle_function_title));
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("left_function_key");
        this.mLeftPreference = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(this);
        this.mLeftPreference.setValueIndex(InputMethodFunctionSelectUtils.getSelectedFunctionIndex(this.mActivity, true));
        DropDownPreference dropDownPreference2 = (DropDownPreference) findPreference("right_function_key");
        this.mRightPreference = dropDownPreference2;
        dropDownPreference2.setOnPreferenceChangeListener(this);
        this.mRightPreference.setValueIndex(InputMethodFunctionSelectUtils.getSelectedFunctionIndex(this.mActivity, false));
        DropDownPreference dropDownPreference3 = (DropDownPreference) findPreference("middle_function_key");
        this.mMiddlePreference = dropDownPreference3;
        dropDownPreference3.setOnPreferenceChangeListener(this);
        this.mMiddlePreference.setValueIndex(InputMethodFunctionSelectUtils.getMiddleFunctionSelectedIndex(this.mActivity));
        this.mFullIMEPreference = (FullScreenInputMethodPreference) findPreference("full_screen_keyboard_optimization_image");
        this.mFullIMEPreference.setHighKeyboardChecked(Settings.Secure.getInt(getContext().getContentResolver(), "enable_miui_ime_bottom_view", 1) == 1);
        this.mFullIMEPreference.setHighImage(R.drawable.keyboard_settings_image_high);
        this.mFullIMEPreference.setDefaultImage(R.drawable.keyboard_settings_image_normal);
        this.mFullIMEPreference.setHighText(R.string.multifunction_keyboard);
        this.mFullIMEPreference.setDefaultText(R.string.default_keyboard);
        this.mFullIMEPreference.setOnCheckedChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mActivity.getContentResolver().unregisterContentObserver(this.mEnableFullScreenKeyboardObserver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = this.mActivity.getContentResolver();
        if (preference == this.mLeftPreference) {
            Settings.Secure.putString(contentResolver, "full_screen_keyboard_left_function", InputMethodFunctionSelectUtils.getSelectedFunctionKeyByIndex(this.mSelectedValueSideList.indexOf((String) obj)));
            return true;
        } else if (preference == this.mRightPreference) {
            Settings.Secure.putString(contentResolver, "full_screen_keyboard_right_function", InputMethodFunctionSelectUtils.getSelectedFunctionKeyByIndex(this.mSelectedValueSideList.indexOf((String) obj)));
            return true;
        } else if (preference == this.mMiddlePreference) {
            Settings.Secure.putString(contentResolver, "full_screen_keyboard_middle_function", InputMethodFunctionSelectUtils.getMiddleFunctionSelectedKeyByIndex(this.mSelectedValueMiddleList.indexOf((String) obj)));
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ContentResolver contentResolver = this.mActivity.getContentResolver();
        updateFunctionPreferenceEnable();
        if (this.mEnableFullScreenKeyboardObserver == null) {
            this.mEnableFullScreenKeyboardObserver = new EnableFullScreenKeyboardObserver(new Handler());
        }
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("enable_miui_ime_bottom_view"), false, this.mEnableFullScreenKeyboardObserver);
    }
}
