package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioButtonPreferenceCategory;

/* loaded from: classes.dex */
public class KeySettingsSelectFragment extends SettingsPreferenceFragment {
    private ContentObserver mContentObserver;
    private Context mContext;
    private RadioButtonPreference mHidedRadioButtonPreference;
    private PreferenceCategory mKeyGestureFunctionCategory;
    private RadioButtonPreferenceCategory mKeyGestureFunctionOptional;
    private KeySettingsPreviewPreference mKeyGestureFunctionPreview;
    private String mPreferenceKey;
    private RadioButtonPreference mRadioButtonPreference;
    private String mTitle;
    private Resources resources;
    private AlertDialog mFsgChangeDialog = null;
    private AlertDialog mActionChangeDialog = null;

    private void bringUpActionChooseDlg(final String str, String str2, final RadioButtonPreference radioButtonPreference) {
        if (this.mActionChangeDialog != null) {
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.KeySettingsSelectFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    if (KeySettingsSelectFragment.this.isNeedFsgDlg(str)) {
                        KeySettingsSelectFragment keySettingsSelectFragment = KeySettingsSelectFragment.this;
                        keySettingsSelectFragment.bringUpFsgChooseDlg(str, keySettingsSelectFragment.mPreferenceKey, KeySettingsSelectFragment.this.mRadioButtonPreference);
                    } else {
                        KeySettingsSelectFragment.this.performSettingsChange(str, radioButtonPreference);
                    }
                }
                if (KeySettingsSelectFragment.this.mActionChangeDialog != null) {
                    KeySettingsSelectFragment.this.mActionChangeDialog.dismiss();
                    KeySettingsSelectFragment.this.mActionChangeDialog = null;
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle((CharSequence) null).setMessage(this.resources.getString(R.string.key_gesture_function_dialog_message, getAction(str, this.resources), getFunction(str2, this.resources), this.mTitle)).setPositiveButton(R.string.key_gesture_function_dialog_positive, onClickListener).setNegativeButton(R.string.key_gesture_function_dialog_negative, onClickListener).setCancelable(false).create();
        this.mActionChangeDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bringUpFsgChooseDlg(final String str, String str2, final RadioButtonPreference radioButtonPreference) {
        this.mKeyGestureFunctionOptional.setCheckedPreference(this.mRadioButtonPreference);
        if (this.mFsgChangeDialog != null) {
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.KeySettingsSelectFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    KeySettingsSelectFragment.this.performFsgChange(str, radioButtonPreference);
                }
                if (KeySettingsSelectFragment.this.mFsgChangeDialog != null) {
                    KeySettingsSelectFragment.this.mFsgChangeDialog.dismiss();
                    KeySettingsSelectFragment.this.mFsgChangeDialog = null;
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle((CharSequence) null).setMessage(R.string.key_fsg_dialog_message).setPositiveButton(R.string.key_fsg_dialog_positive, onClickListener).setNegativeButton(R.string.key_fsg_dialog_negative, onClickListener).setCancelable(false).create();
        this.mFsgChangeDialog = create;
        create.show();
    }

    private String getAction(String str, Resources resources) {
        return "double_click_power_key".equals(str) ? resources.getString(R.string.double_click_power_key) : "long_press_menu_key".equals(str) ? resources.getString(R.string.long_press_menu_key) : "long_press_home_key".equals(str) ? resources.getString(R.string.long_press_home_key) : "long_press_back_key".equals(str) ? resources.getString(R.string.long_press_back_key) : "key_combination_power_back".equals(str) ? resources.getString(R.string.key_combination_power_back) : "key_combination_power_home".equals(str) ? resources.getString(R.string.key_combination_power_home) : "key_combination_power_menu".equals(str) ? resources.getString(R.string.key_combination_power_menu) : "long_press_menu_key_when_lock".equals(str) ? resources.getString(R.string.long_press_menu_key_when_lock) : "three_gesture_down".equals(str) ? resources.getString(R.string.three_gesture_down) : "three_gesture_long_press".equals(str) ? String.format(resources.getString(R.string.three_gesture_long_press), 3) : "";
    }

    private String getFunction(String str, Resources resources) {
        return "launch_camera".equals(str) ? resources.getString(R.string.launch_camera) : "screen_shot".equals(str) ? resources.getString(R.string.screen_shot) : "partial_screen_shot".equals(str) ? resources.getString(R.string.regional_screen_shot) : "launch_voice_assistant".equals(str) ? resources.getString(R.string.launch_voice_assistant) : "launch_google_search".equals(str) ? resources.getString(R.string.launch_google_search) : "go_to_sleep".equals(str) ? resources.getString(R.string.go_to_sleep) : "turn_on_torch".equals(str) ? resources.getString(R.string.turn_on_torch) : "close_app".equals(str) ? resources.getString(R.string.close_app) : "split_screen".equals(str) ? resources.getString(R.string.split_screen) : "mi_pay".equals(str) ? resources.getString(R.string.mi_pay) : "dump_log".equals(str) ? resources.getString(R.string.dump_log) : "show_menu".equals(str) ? resources.getString(R.string.show_menu) : "launch_recents".equals(str) ? resources.getString(R.string.launch_recents) : "au_pay".equals(str) ? resources.getString(R.string.au_pay) : "google_pay".equals(str) ? resources.getString(R.string.google_pay) : "launch_smarthome".equals(str) ? resources.getString(R.string.launch_smarthome) : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RadioButtonPreference getLongPressPowerPreference(RadioButtonPreferenceCategory radioButtonPreferenceCategory) {
        for (int i = 0; i < radioButtonPreferenceCategory.getPreferenceCount(); i++) {
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) radioButtonPreferenceCategory.getPreference(i);
            if ("long_press_power_key".equals(radioButtonPreference.getKey())) {
                return radioButtonPreference;
            }
        }
        return null;
    }

    private int getResId(String str, Resources resources) {
        int i = R.string.launch_camera;
        if (resources.getString(i).equals(str)) {
            return i;
        }
        int i2 = R.string.screen_shot;
        if (resources.getString(i2).equals(str)) {
            return i2;
        }
        int i3 = R.string.regional_screen_shot;
        if (resources.getString(i3).equals(str)) {
            return i3;
        }
        int i4 = R.string.launch_voice_assistant;
        if (resources.getString(i4).equals(str)) {
            return i4;
        }
        int i5 = R.string.launch_google_search;
        if (resources.getString(i5).equals(str)) {
            return i5;
        }
        int i6 = R.string.go_to_sleep;
        if (resources.getString(i6).equals(str)) {
            return i6;
        }
        int i7 = R.string.turn_on_torch;
        if (resources.getString(i7).equals(str)) {
            return i7;
        }
        int i8 = R.string.close_app;
        if (resources.getString(i8).equals(str)) {
            return i8;
        }
        int i9 = R.string.split_screen;
        if (resources.getString(i9).equals(str)) {
            return i9;
        }
        int i10 = R.string.mi_pay;
        if (resources.getString(i10).equals(str)) {
            return i10;
        }
        int i11 = R.string.dump_log;
        if (resources.getString(i11).equals(str)) {
            return i11;
        }
        int i12 = R.string.show_menu;
        if (resources.getString(i12).equals(str)) {
            return i12;
        }
        int i13 = R.string.launch_recents;
        if (resources.getString(i13).equals(str)) {
            return i13;
        }
        int i14 = R.string.au_pay;
        if (resources.getString(i14).equals(str)) {
            return i14;
        }
        int i15 = R.string.google_pay;
        if (resources.getString(i15).equals(str)) {
            return i15;
        }
        int i16 = R.string.launch_smarthome;
        if (resources.getString(i16).equals(str)) {
            return i16;
        }
        return 0;
    }

    private void init(ArrayList<String> arrayList) {
        boolean z;
        int resId = getResId(this.mTitle, this.resources);
        if (resId == R.string.launch_camera) {
            this.mPreferenceKey = "launch_camera";
            arrayList.add(0, "double_click_power_key");
        } else if (resId == R.string.screen_shot) {
            this.mPreferenceKey = "screen_shot";
            arrayList.add(0, "three_gesture_down");
        } else if (resId == R.string.regional_screen_shot) {
            this.mPreferenceKey = "partial_screen_shot";
            arrayList.add(0, "three_gesture_long_press");
        } else if (resId == R.string.launch_voice_assistant) {
            this.mPreferenceKey = "launch_voice_assistant";
            if (!this.mContext.getResources().getBoolean(R.bool.config_has_aikey)) {
                arrayList.add(0, "long_press_power_key");
            }
        } else if (resId == R.string.launch_google_search) {
            this.mPreferenceKey = "launch_google_search";
        } else if (resId == R.string.go_to_sleep) {
            this.mPreferenceKey = "go_to_sleep";
            arrayList.remove("key_combination_power_back");
            arrayList.remove("key_combination_power_home");
            arrayList.remove("key_combination_power_menu");
        } else if (resId == R.string.turn_on_torch) {
            this.mPreferenceKey = "turn_on_torch";
            arrayList.add(0, "double_click_power_key");
            try {
                z = IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0);
            } catch (RemoteException e) {
                e.printStackTrace();
                z = false;
            }
            if (!z) {
                arrayList.add(0, "long_press_menu_key_when_lock");
            }
        } else if (resId == R.string.close_app) {
            this.mPreferenceKey = "close_app";
        } else if (resId == R.string.split_screen) {
            this.mPreferenceKey = "split_screen";
            arrayList.remove("key_combination_power_back");
            arrayList.remove("key_combination_power_home");
            arrayList.remove("key_combination_power_menu");
        } else if (resId == R.string.mi_pay) {
            this.mPreferenceKey = "mi_pay";
            arrayList.remove("long_press_home_key");
            arrayList.remove("long_press_menu_key");
            arrayList.remove("long_press_back_key");
            arrayList.remove("key_combination_power_back");
            arrayList.remove("key_combination_power_home");
            arrayList.remove("key_combination_power_menu");
            arrayList.add(0, "double_click_power_key");
        } else if (resId == R.string.dump_log) {
            this.mPreferenceKey = "dump_log";
            arrayList.add(0, "three_gesture_down");
        } else if (resId == R.string.show_menu) {
            this.mPreferenceKey = "show_menu";
        } else if (resId == R.string.launch_recents) {
            this.mPreferenceKey = "launch_recents";
        } else if (resId == R.string.au_pay) {
            this.mPreferenceKey = "au_pay";
            arrayList.clear();
            arrayList.add("double_click_power_key");
            arrayList.add("key_none");
        } else if (resId == R.string.google_pay) {
            this.mPreferenceKey = "google_pay";
            arrayList.clear();
            arrayList.add("double_click_power_key");
            arrayList.add("key_none");
        } else if (resId == R.string.launch_smarthome) {
            this.mPreferenceKey = "launch_smarthome";
            arrayList.clear();
            arrayList.add("long_press_power_key");
            arrayList.add("double_click_power_key");
            arrayList.add("key_none");
        } else {
            Log.e("KeySettings", "not found titleId" + resId);
            this.mPreferenceKey = null;
        }
        Log.e("KeySettings", "mPreferenceKey = " + this.mPreferenceKey);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNeedFsgDlg(String str) {
        return (!MiuiSettings.Global.getBoolean(getActivity().getContentResolver(), "force_fsg_nav_bar") || "double_click_power_key".equals(str) || "long_press_power_key".equals(str) || "three_gesture_down".equals(str) || "three_gesture_long_press".equals(str) || "key_none".equals(str)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performFsgChange(String str, RadioButtonPreference radioButtonPreference) {
        MiuiSettings.Global.putBoolean(getContentResolver(), "force_fsg_nav_bar", false);
        performSettingsChange(str, radioButtonPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performSettingsChange(String str, RadioButtonPreference radioButtonPreference) {
        this.mKeyGestureFunctionOptional.setCheckedPreference(radioButtonPreference);
        RadioButtonPreference radioButtonPreference2 = this.mHidedRadioButtonPreference;
        if (radioButtonPreference2 != null) {
            this.mRadioButtonPreference = radioButtonPreference2;
            this.mHidedRadioButtonPreference = null;
        }
        if (!this.mRadioButtonPreference.getKey().equals("key_none")) {
            Settings.System.putStringForUser(getContentResolver(), this.mRadioButtonPreference.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
            if ("launch_voice_assistant".equals(this.mPreferenceKey) && "long_press_power_key".equals(this.mRadioButtonPreference.getKey())) {
                Settings.System.putIntForUser(getContentResolver(), "long_press_power_launch_xiaoai", 0, -2);
            }
        }
        if (!str.equals("key_none")) {
            Settings.System.putStringForUser(getContentResolver(), str, this.mPreferenceKey, -2);
        }
        this.mRadioButtonPreference = radioButtonPreference;
        this.mKeyGestureFunctionPreview.setCheckedAction(radioButtonPreference.getKey());
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return KeySettingsSelectFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        addPreferencesFromResource(R.xml.key_settings_select_fragment);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mTitle = arguments.getString(":settings:show_fragment_title", this.resources.getString(R.string.launch_voice_assistant));
        }
        String[] stringArray = this.resources.getStringArray(R.array.key_and_gesture_shortcut_action);
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : stringArray) {
            arrayList.add(str);
        }
        init(arrayList);
        this.mKeyGestureFunctionPreview = (KeySettingsPreviewPreference) findPreference("key_gesture_function_preview");
        if ("dump_log".equals(this.mPreferenceKey)) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_gesture_function_category");
            this.mKeyGestureFunctionCategory = preferenceCategory;
            preferenceCategory.removePreference(this.mKeyGestureFunctionPreview);
            this.mKeyGestureFunctionCategory.setTitle(R.string.dump_log_title);
            ValuePreference valuePreference = new ValuePreference(getPrefContext());
            valuePreference.setSummary(this.mContext.getResources().getString(R.string.dump_log_summary, 284));
            valuePreference.setShowRightArrow(false);
            valuePreference.setSelectable(false);
            this.mKeyGestureFunctionCategory.addPreference(valuePreference);
        }
        this.mKeyGestureFunctionOptional = (RadioButtonPreferenceCategory) findPreference("key_gesture_function_optional");
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
            radioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
            radioButtonPreference.setKey(next);
            if ("long_press_power_key".equals(next)) {
                radioButtonPreference.setTitle(Html.fromHtml(this.resources.getString(R.string.long_press_power_key_tips), 0));
            } else if ("three_gesture_long_press".equals(next)) {
                radioButtonPreference.setTitle(String.format(this.resources.getString(R.string.three_gesture_long_press), 3));
            } else {
                radioButtonPreference.setTitle(this.resources.getIdentifier(next, "string", this.mContext.getPackageName()));
            }
            radioButtonPreference.setPersistent(false);
            String keyAndGestureShortcutFunction = MiuiSettings.Key.getKeyAndGestureShortcutFunction(this.mContext, next);
            this.mKeyGestureFunctionOptional.addPreference(radioButtonPreference);
            if (TextUtils.equals(keyAndGestureShortcutFunction, this.mPreferenceKey)) {
                this.mRadioButtonPreference = radioButtonPreference;
            }
            if (next.equals("key_none") && this.mRadioButtonPreference == null) {
                this.mRadioButtonPreference = radioButtonPreference;
            }
        }
        if (MiuiSettings.Global.getBoolean(this.mContext.getContentResolver(), "force_fsg_nav_bar") && !"double_click_power_key".equals(this.mRadioButtonPreference.getKey()) && !"long_press_power_key".equals(this.mRadioButtonPreference.getKey()) && !"three_gesture_down".equals(this.mRadioButtonPreference.getKey()) && !"three_gesture_long_press".equals(this.mRadioButtonPreference.getKey()) && !"key_none".equals(this.mRadioButtonPreference.getKey())) {
            this.mHidedRadioButtonPreference = this.mRadioButtonPreference;
            RadioButtonPreferenceCategory radioButtonPreferenceCategory = this.mKeyGestureFunctionOptional;
            this.mRadioButtonPreference = (RadioButtonPreference) radioButtonPreferenceCategory.getPreference(radioButtonPreferenceCategory.getPreferenceCount() - 1);
        }
        this.mKeyGestureFunctionOptional.setCheckedPreference(this.mRadioButtonPreference);
        this.mKeyGestureFunctionPreview.setPreferenceKey(this.mPreferenceKey);
        this.mKeyGestureFunctionPreview.setCheckedAction(this.mRadioButtonPreference.getKey());
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.KeySettingsSelectFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                boolean z2 = Settings.System.getIntForUser(KeySettingsSelectFragment.this.mContext.getContentResolver(), "long_press_power_launch_xiaoai", 0, -2) == 1;
                if ("launch_voice_assistant".equals(KeySettingsSelectFragment.this.mPreferenceKey) && z2) {
                    RadioButtonPreferenceCategory radioButtonPreferenceCategory2 = KeySettingsSelectFragment.this.mKeyGestureFunctionOptional;
                    KeySettingsSelectFragment keySettingsSelectFragment = KeySettingsSelectFragment.this;
                    radioButtonPreferenceCategory2.setCheckedPreference(keySettingsSelectFragment.getLongPressPowerPreference(keySettingsSelectFragment.mKeyGestureFunctionOptional));
                    KeySettingsSelectFragment keySettingsSelectFragment2 = KeySettingsSelectFragment.this;
                    keySettingsSelectFragment2.mRadioButtonPreference = keySettingsSelectFragment2.getLongPressPowerPreference(keySettingsSelectFragment2.mKeyGestureFunctionOptional);
                    KeySettingsSelectFragment.this.mKeyGestureFunctionPreview.setCheckedAction("long_press_power_key");
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("long_press_power_launch_xiaoai"), false, this.mContentObserver, -1);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.resources = context.getResources();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!MiuiShortcut$System.isSupportNewVersionKeySettings(this.mContext) || Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        getIntent().getExtras().getString(":settings:show_fragment_title");
        getIntent().getExtras();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.SubSettings"));
        intent.putExtra(":settings:show_fragment", "com.android.settings.GestureShortcutSettingsSelectFragment");
        intent.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResourceForKey("voice_assist", this.mContext));
        startActivity(intent);
        finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        KeySettingsPreviewPreference keySettingsPreviewPreference = this.mKeyGestureFunctionPreview;
        if (keySettingsPreviewPreference != null) {
            keySettingsPreviewPreference.destroy();
        }
        this.mActionChangeDialog = null;
        this.mFsgChangeDialog = null;
        if (this.mContentObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        RadioButtonPreference radioButtonPreference;
        if ((preference instanceof RadioButtonPreference) && this.mRadioButtonPreference != (radioButtonPreference = (RadioButtonPreference) preference)) {
            String key = radioButtonPreference.getKey();
            String keyAndGestureShortcutFunction = MiuiSettings.Key.getKeyAndGestureShortcutFunction(this.mContext, key);
            if (keyAndGestureShortcutFunction != null && !TextUtils.equals(keyAndGestureShortcutFunction, MiCloudStatusInfo.QuotaInfo.WARN_NONE) && ((Build.hasCameraFlash(getActivity()) || !keyAndGestureShortcutFunction.equals("turn_on_torch")) && !TextUtils.equals(keyAndGestureShortcutFunction, this.mPreferenceKey))) {
                this.mKeyGestureFunctionOptional.setCheckedPreference(this.mRadioButtonPreference);
                bringUpActionChooseDlg(key, keyAndGestureShortcutFunction, radioButtonPreference);
            } else if (isNeedFsgDlg(key)) {
                bringUpFsgChooseDlg(key, keyAndGestureShortcutFunction, this.mRadioButtonPreference);
            } else {
                performSettingsChange(key, radioButtonPreference);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
