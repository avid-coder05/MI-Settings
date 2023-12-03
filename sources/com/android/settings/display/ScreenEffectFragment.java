package com.android.settings.display;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.CustomRadioButtonPreference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import miui.hardware.display.DisplayFeatureManager;
import miui.util.FeatureParser;
import miuix.preference.FolmeAnimationController;
import miuix.preference.RadioButtonPreferenceCategory;
import miuix.util.Log;

/* loaded from: classes.dex */
public class ScreenEffectFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, FolmeAnimationController {
    private static final Map<String, Integer> SCREEN_MSG_KV_MAP;
    private static final boolean SUPPORT_UNLIMITED_COLOR;
    private static final boolean SUPPORT_UNLIMITED_COLOR_MODE;
    private static final int mScreenEffectSupport;
    private Context mContext;
    private int mCurrentOptimizeMode;
    private ExpertRadioButtonPreference mExpertPreference;
    private ScreenEffectHandler mHandler = new ScreenEffectHandler(this);
    private HandlerThread mHandlerThread;
    private PaperModeObserver mPaperModeObserver;
    private ScreenColorPreference mScreenColorPreference;
    private Toast mToast;
    private static final boolean IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT = FeatureParser.getBoolean("is_compatible_paper_and_screen_effect", false);
    public static final boolean SUPPORT_DISPLAY_EXPERT_MODE = FeatureParser.getBoolean("support_display_expert_mode", false);
    private static final String[] SCREEN_OPTIMIZE_ARR = {"screen_optimize_adapt", "screen_optimize_enhance", "screen_optimize_standard"};
    private static final int[] SCREEN_OPTIMIZE_VALUE_ARR = {1, 2, 3};
    private static final String[] SCREEN_COLOR_ARR = {"screen_color_warm", "screen_color_nature", "screen_color_cool"};
    private static final int[] SCREEN_COLOR_VALUE_ARR = {1, 2, 3};

    /* loaded from: classes.dex */
    private class PaperModeObserver extends ContentObserver {
        private final Uri PAPER_MODE_ENABLED_URI;
        private final Uri PAPER_MODE_URI;

        public PaperModeObserver() {
            super(new Handler(ScreenEffectFragment.this.mContext.getMainLooper()));
            this.PAPER_MODE_ENABLED_URI = Settings.System.getUriFor("screen_paper_mode_enabled");
            this.PAPER_MODE_URI = Settings.System.getUriFor("screen_paper_mode");
        }

        private boolean isGlobalPaperMode() {
            return Settings.System.getInt(ScreenEffectFragment.this.getContentResolver(), "screen_paper_mode", 1) == 1;
        }

        private boolean isPaperModeEnable() {
            return MiuiSettings.System.getBoolean(ScreenEffectFragment.this.getContentResolver(), "screen_paper_mode_enabled", false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (!isPaperModeEnable() || !isGlobalPaperMode()) {
                ScreenEffectFragment.this.setScreenEffectChannelEnable(true);
                if (!ScreenEffectFragment.this.getPreferenceScreen().isEnabled()) {
                    ScreenEffectFragment.this.getPreferenceScreen().setEnabled(true);
                    ScreenEffectFragment.this.cancelToast();
                }
                ScreenEffectFragment.this.updateExpertStatus(true);
                return;
            }
            if (ScreenEffectFragment.IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT || !ScreenEffectFragment.this.getPreferenceScreen().isEnabled()) {
                ScreenEffectFragment.this.doCompatibleAction();
            } else {
                ScreenEffectFragment.this.getPreferenceScreen().setEnabled(false);
                ScreenEffectFragment screenEffectFragment = ScreenEffectFragment.this;
                screenEffectFragment.showToast(screenEffectFragment.getResources().getString(R.string.screen_color_and_optimize_disabled));
            }
            ScreenEffectFragment.this.updateExpertStatus(false);
        }

        public void register() {
            ScreenEffectFragment.this.getContentResolver().registerContentObserver(this.PAPER_MODE_ENABLED_URI, false, this);
            ScreenEffectFragment.this.getContentResolver().registerContentObserver(this.PAPER_MODE_URI, false, this);
        }

        public void unregister() {
            ScreenEffectFragment.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* loaded from: classes.dex */
    private static class ScreenEffectHandler extends Handler {
        private WeakReference<ScreenEffectFragment> mScreenEffectFragmentWeakReference;

        public ScreenEffectHandler(ScreenEffectFragment screenEffectFragment) {
            super(Looper.getMainLooper());
            this.mScreenEffectFragmentWeakReference = new WeakReference<>(screenEffectFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ScreenEffectFragment screenEffectFragment = this.mScreenEffectFragmentWeakReference.get();
            if (screenEffectFragment == null) {
                return;
            }
            int i = message.what;
            if (i == 1) {
                screenEffectFragment.setScreenColor(((Integer) message.obj).intValue());
            } else if (i != 3) {
            } else {
                screenEffectFragment.setScreenOptimizeMode(((Integer) message.obj).intValue());
                screenEffectFragment.updateScreenColorPreference(((Integer) message.obj).intValue());
            }
        }
    }

    static {
        HashMap hashMap = new HashMap();
        SCREEN_MSG_KV_MAP = hashMap;
        hashMap.put("screen_color_warm", 1);
        hashMap.put("screen_color_nature", 2);
        hashMap.put("screen_color_cool", 3);
        hashMap.put("screen_optimize_adapt", 1);
        hashMap.put("screen_optimize_enhance", 2);
        hashMap.put("screen_optimize_standard", 3);
        hashMap.put("screen_optimize_expert", 4);
        mScreenEffectSupport = MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED;
        SUPPORT_UNLIMITED_COLOR = SystemProperties.getBoolean("ro.vendor.colorpick_adjust", false) || SystemProperties.getBoolean("ro.colorpick_adjust", false);
        SUPPORT_UNLIMITED_COLOR_MODE = SystemProperties.getBoolean("ro.vendor.all_modes.colorpick_adjust", false) || SystemProperties.getBoolean("ro.all_modes.colorpick_adjust", false);
    }

    private void addExpertModeIfNeed() {
        if (SUPPORT_DISPLAY_EXPERT_MODE && UserHandle.myUserId() == 0) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("screen_optimize");
            RadioButtonPreferenceCategory radioButtonPreferenceCategory = new RadioButtonPreferenceCategory(getThemedContext());
            preferenceCategory.addPreference(radioButtonPreferenceCategory);
            ExpertRadioButtonPreference expertRadioButtonPreference = new ExpertRadioButtonPreference(getThemedContext());
            expertRadioButtonPreference.setKey("screen_optimize_expert");
            expertRadioButtonPreference.setTitle(getResources().getString(R.string.display_advanced_mode_title));
            expertRadioButtonPreference.setSummary(getResources().getString(R.string.display_advanced_mode_summary));
            expertRadioButtonPreference.setPersistent(false);
            expertRadioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
            expertRadioButtonPreference.setOnPreferenceChangeListener(this);
            radioButtonPreferenceCategory.addPreference(expertRadioButtonPreference);
            expertRadioButtonPreference.setChecked(4 == this.mCurrentOptimizeMode);
            this.mExpertPreference = expertRadioButtonPreference;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelToast() {
        Toast toast = this.mToast;
        if (toast != null) {
            toast.cancel();
        }
    }

    private void checkDataIslegal() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "screen_optimize_mode", MiuiSettings.ScreenEffect.DEFAULT_SCREEN_OPTIMIZE_MODE);
        if (i != 1 && i != 2 && i != 3) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_optimize_mode", MiuiSettings.ScreenEffect.DEFAULT_SCREEN_OPTIMIZE_MODE);
        }
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "screen_color_level", 2);
        if (i2 == 2 || i2 == 1 || i2 == 3) {
            return;
        }
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_color_level", 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCompatibleAction() {
        setScreenEffectChannelEnable(false);
        if (getScreenMode() == 4) {
            showToast(getResources().getString(R.string.screen_color_and_optimize_disabled));
        } else if (SUPPORT_UNLIMITED_COLOR_MODE) {
            showToast(getResources().getString(R.string.screen_optimize_disabled));
        } else if (getScreenMode() == 1) {
            showToast(getResources().getString(R.string.screen_optimize_disabled));
        } else {
            showToast(getResources().getString(R.string.screen_color_and_optimize_disabled));
        }
    }

    private PreferenceCategory generateCategory(String str, int i) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey(str);
        preferenceCategory.setTitle(i);
        preferenceCategory.setPersistent(false);
        preferenceScreen.addPreference(preferenceCategory);
        return preferenceCategory;
    }

    private void generateScreenColorPreference() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "screen_color_level", 2);
        PreferenceCategory generateCategory = generateCategory("screen_color", R.string.screen_color_temperature);
        if (SUPPORT_UNLIMITED_COLOR) {
            ScreenColorPreference screenColorPreference = new ScreenColorPreference(getPrefContext());
            screenColorPreference.setKey("screen_color_pre");
            screenColorPreference.setPersistent(false);
            generateCategory.addPreference(screenColorPreference);
            return;
        }
        String[] stringArray = getResources().getStringArray(R.array.screen_color_title);
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            RadioButtonPreferenceCategory radioButtonPreferenceCategory = new RadioButtonPreferenceCategory(getThemedContext());
            generateCategory.addPreference(radioButtonPreferenceCategory);
            miuix.preference.RadioButtonPreference radioButtonPreference = new miuix.preference.RadioButtonPreference(getThemedContext());
            radioButtonPreference.setKey(SCREEN_COLOR_ARR[i2]);
            radioButtonPreference.setTitle(stringArray[i2]);
            radioButtonPreference.setPersistent(false);
            radioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
            radioButtonPreference.setOnPreferenceChangeListener(this);
            radioButtonPreferenceCategory.addPreference(radioButtonPreference);
            radioButtonPreference.setChecked(SCREEN_COLOR_VALUE_ARR[i2] == i);
        }
    }

    private void generateScreenOptimizePreference() {
        PreferenceCategory generateCategory = generateCategory("screen_optimize", R.string.screen_optimize);
        boolean z = SettingsFeatures.IS_SUPPORT_TRUE_COLOR;
        int i = z ? R.array.true_color_screen_optimize_title : R.array.screen_optimize_title;
        int i2 = z ? R.array.true_color_screen_optimize_summary : R.array.screen_optimize_summary;
        String[] stringArray = getResources().getStringArray(i);
        String[] stringArray2 = getResources().getStringArray(i2);
        this.mCurrentOptimizeMode = getScreenMode();
        for (int i3 = 0; i3 < stringArray.length; i3++) {
            if ((mScreenEffectSupport & (1 << i3)) != 0) {
                RadioButtonPreferenceCategory radioButtonPreferenceCategory = new RadioButtonPreferenceCategory(getThemedContext());
                generateCategory.addPreference(radioButtonPreferenceCategory);
                CustomRadioButtonPreference customRadioButtonPreference = new CustomRadioButtonPreference(getThemedContext());
                customRadioButtonPreference.setKey(SCREEN_OPTIMIZE_ARR[i3]);
                customRadioButtonPreference.setTitle(stringArray[i3]);
                customRadioButtonPreference.setSummary(stringArray2[i3]);
                customRadioButtonPreference.setPersistent(false);
                customRadioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                customRadioButtonPreference.setOnPreferenceChangeListener(this);
                radioButtonPreferenceCategory.addPreference(customRadioButtonPreference);
                customRadioButtonPreference.setChecked(SCREEN_OPTIMIZE_VALUE_ARR[i3] == this.mCurrentOptimizeMode);
            }
        }
    }

    private void generateTrueToneModePrefIfNeed() {
        if (FeatureParser.getBoolean("support_truetone", false)) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
            preferenceCategory.setKey("true_tone_key");
            getPreferenceScreen().addPreference(preferenceCategory);
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
            checkBoxPreference.setKey("screen_truetone_pref");
            checkBoxPreference.setTitle(getResources().getString(R.string.truetone_title));
            checkBoxPreference.setSummary(getResources().getString(R.string.truetone_summary));
            checkBoxPreference.setChecked(getTrueToneStatues());
            checkBoxPreference.setOnPreferenceChangeListener(this);
            preferenceCategory.addPreference(checkBoxPreference);
        }
    }

    private int getScreenColorPrefer() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_color_level", 2);
    }

    private int getScreenMode() {
        return getScreenMode(this.mContext);
    }

    public static int getScreenMode(Context context) {
        int i = (mScreenEffectSupport & 1) == 0 ? 2 : 1;
        if (FeatureParser.getInteger("default_display_color_mode", -1) == 3) {
            i = 3;
        }
        return context == null ? i : Settings.System.getInt(context.getContentResolver(), "screen_optimize_mode", i);
    }

    private boolean getTrueToneStatues() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_true_tone", 0) == 1;
    }

    private void handleIfNeedDisableAIDisplayMode(int i) {
        if ((i == 3 || i == 4) && TextUtils.equals(Settings.Global.getString(this.mContext.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status"), "true")) {
            Log.v("ScreenEffect", "disable AI display mode!");
            Settings.Global.putString(this.mContext.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status", "false");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScreenColor(int i) {
        if (getScreenColorPrefer() != i) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_color_level", i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScreenEffectChannelEnable(boolean z) {
        Preference findPreference = getPreferenceScreen().findPreference("screen_optimize");
        if (findPreference != null) {
            findPreference.setEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScreenOptimizeMode(int i) {
        Log.d("ScreenEffectFragment", "setScreenOptimizeMode: " + i);
        if (getScreenMode() == i) {
            return;
        }
        this.mCurrentOptimizeMode = i;
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_optimize_mode", i);
        if (i == 4) {
            DisplayFeatureManager.getInstance().setScreenEffect(26, 0, 10);
        }
        handleIfNeedDisableAIDisplayMode(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        cancelToast();
        Toast makeText = Toast.makeText(getActivity(), str, 0);
        this.mToast = makeText;
        makeText.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateExpertStatus(boolean z) {
        ExpertRadioButtonPreference expertRadioButtonPreference = this.mExpertPreference;
        if (expertRadioButtonPreference != null) {
            expertRadioButtonPreference.setPreferenceScreenStatus(z);
            if (z) {
                this.mExpertPreference.setChecked(4 == this.mCurrentOptimizeMode);
            }
        }
    }

    private void updateRadioButtonPreference(String str, String str2) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference(str);
        if (preferenceCategory != null) {
            int preferenceCount = preferenceCategory.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                miuix.preference.RadioButtonPreference radioButtonPreference = (miuix.preference.RadioButtonPreference) ((PreferenceCategory) preferenceCategory.getPreference(i)).getPreference(0);
                if (radioButtonPreference != null) {
                    radioButtonPreference.setChecked(radioButtonPreference.getKey().equals(str2));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScreenColorPreference(int i) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("screen_color");
        if (preferenceCategory == null || (mScreenEffectSupport & 1) == 0) {
            return;
        }
        if (!SUPPORT_UNLIMITED_COLOR) {
            if (i == 1) {
                preferenceCategory.setEnabled(true);
                ((miuix.preference.RadioButtonPreference) ((RadioButtonPreferenceCategory) preferenceCategory.getPreference(1)).getPreference(0)).setChecked(false);
                ((miuix.preference.RadioButtonPreference) ((RadioButtonPreferenceCategory) preferenceCategory.getPreference(Math.abs(getScreenColorPrefer() - 1))).getPreference(0)).setChecked(true);
                return;
            }
            preferenceCategory.setEnabled(false);
            ((miuix.preference.RadioButtonPreference) ((RadioButtonPreferenceCategory) preferenceCategory.getPreference(Math.abs(getScreenColorPrefer() - 1))).getPreference(0)).setChecked(false);
            ((miuix.preference.RadioButtonPreference) ((RadioButtonPreferenceCategory) preferenceCategory.getPreference(1)).getPreference(0)).setChecked(true);
        } else if (!SUPPORT_UNLIMITED_COLOR_MODE) {
            if (i == 1) {
                preferenceCategory.setEnabled(true);
            } else {
                preferenceCategory.setEnabled(false);
            }
        } else {
            if (!preferenceCategory.isEnabled()) {
                preferenceCategory.setEnabled(true);
            }
            if (i == 4) {
                preferenceCategory.setEnabled(false);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return ScreenEffectFragment.class.getName();
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        HandlerThread handlerThread = new HandlerThread("ScreenEffectHandler");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mContext = getActivity();
        addPreferencesFromResource(R.xml.screen_effect_settings);
        boolean z = SUPPORT_UNLIMITED_COLOR;
        if (!z) {
            checkDataIslegal();
        }
        generateScreenOptimizePreference();
        generateTrueToneModePrefIfNeed();
        generateScreenColorPreference();
        Preference preference = new Preference(getPrefContext());
        preference.setLayoutResource(R.layout.blank_preference);
        preference.setEnabled(false);
        getPreferenceScreen().addPreference(preference);
        updateScreenColorPreference(this.mCurrentOptimizeMode);
        if (z) {
            this.mScreenColorPreference = (ScreenColorPreference) findPreference("screen_color_pre");
        }
        addExpertModeIfNeed();
        if (FeatureParser.getBoolean("support_screen_paper_mode", false)) {
            PaperModeObserver paperModeObserver = new PaperModeObserver();
            this.mPaperModeObserver = paperModeObserver;
            paperModeObserver.register();
            this.mPaperModeObserver.onChange(true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        PaperModeObserver paperModeObserver = this.mPaperModeObserver;
        if (paperModeObserver != null) {
            paperModeObserver.unregister();
        }
        this.mHandlerThread.quit();
        this.mContext = null;
        this.mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        cancelToast();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        char c;
        String str;
        int i;
        int i2;
        String key = preference.getKey();
        key.hashCode();
        switch (key.hashCode()) {
            case -1918257064:
                if (key.equals("screen_color_cool")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1917674604:
                if (key.equals("screen_color_warm")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1663854913:
                if (key.equals("screen_optimize_enhance")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -601915914:
                if (key.equals("screen_color_nature")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -568996660:
                if (key.equals("screen_optimize_standard")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -217491345:
                if (key.equals("screen_truetone_pref")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 371446777:
                if (key.equals("screen_optimize_expert")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 700414835:
                if (key.equals("screen_optimize_adapt")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 3:
                i = SCREEN_MSG_KV_MAP.get(key).intValue();
                str = "screen_color";
                i2 = 1;
                break;
            case 2:
            case 4:
            case 6:
            case 7:
                i = SCREEN_MSG_KV_MAP.get(key).intValue();
                this.mHandler.removeMessages(3);
                str = "screen_optimize";
                i2 = 3;
                break;
            case 5:
                Settings.System.putInt(getContentResolver(), "screen_true_tone", ((Boolean) obj).booleanValue() ? 1 : 0);
            default:
                str = "";
                i = -1;
                i2 = -1;
                break;
        }
        if (i2 != -1) {
            ScreenEffectHandler screenEffectHandler = this.mHandler;
            screenEffectHandler.sendMessageDelayed(screenEffectHandler.obtainMessage(i2, Integer.valueOf(i)), i2 == 3 ? 50L : 0L);
        }
        if (preference instanceof miuix.preference.RadioButtonPreference) {
            updateRadioButtonPreference(str, key);
            return false;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        ViewGroup viewGroup;
        RecyclerView listView = getListView();
        if (listView != null && (viewGroup = (ViewGroup) listView.getParent()) != null) {
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
            viewGroup2.removeAllViews();
            viewGroup2.addView(View.inflate(getContext(), R.layout.preview_preference, null));
            viewGroup2.addView(viewGroup);
        }
        super.onViewCreated(view, bundle);
    }
}
