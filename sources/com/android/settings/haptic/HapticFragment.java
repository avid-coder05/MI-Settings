package com.android.settings.haptic;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiSoundSettings;
import com.android.settings.MiuiSoundSettingsActivity;
import com.android.settings.R;
import com.android.settings.aidl.IRemoteGetDeviceInfoService;
import com.android.settings.aidl.IRequestCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.device.DeviceParamsInitHelper;
import com.android.settings.device.JSONUtils;
import com.android.settings.device.ParseMiShopDataUtils;
import com.android.settings.device.RemoteServiceUtil;
import com.android.settings.haptic.HapticDemoVideoPreference;
import com.android.settings.sound.HapticSeekBarPreference;
import com.android.settings.soundsettings.SoundSpeakerDescPreference;
import java.lang.ref.WeakReference;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class HapticFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener, HapticDemoVideoPreference.IVideoState {
    private UpdateInfoCallback mDeviceInfoCallback;
    private MyHandler mHandler;
    private HapticDemoVideoPreference mHapticDemoVideoPreference;
    protected PreferenceCategory mHapticFeedbackCategory;
    protected HapticSeekBarPreference mHapticFeedbackSeekbar;
    private PreferenceCategory mHapticMotorCategory;
    private SoundSpeakerDescPreference mHapticMotorPreference;
    private DeviceParamsInitHelper mHelper;
    private IRemoteGetDeviceInfoService mRemoteService;
    private RemoteServiceConn mRemoteServiceConn;
    protected CheckBoxPreference mSystemHapticPreference;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MyHandler extends Handler {
        private WeakReference<HapticFragment> mFragmentRef;

        private MyHandler(Looper looper, HapticFragment hapticFragment) {
            super(looper);
            this.mFragmentRef = new WeakReference<>(hapticFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            WeakReference<HapticFragment> weakReference;
            super.handleMessage(message);
            if (message.what == 1 && (weakReference = this.mFragmentRef) != null) {
                weakReference.get().updateHapticDesc((String) message.obj);
            }
        }
    }

    /* loaded from: classes.dex */
    private class RemoteServiceConn implements ServiceConnection {
        private RemoteServiceConn() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FragmentActivity activity = HapticFragment.this.getActivity();
            if (activity == null) {
                return;
            }
            HapticFragment.this.mRemoteService = IRemoteGetDeviceInfoService.Stub.asInterface(iBinder);
            HapticFragment hapticFragment = HapticFragment.this;
            hapticFragment.mHelper = new DeviceParamsInitHelper(activity, hapticFragment.mRemoteService);
            try {
                HapticFragment.this.mRemoteService.registerCallback(HapticFragment.this.mDeviceInfoCallback);
                HapticFragment.this.mHelper.initSoundParams();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("HapticFragment", "onServiceDisconnected");
        }
    }

    /* loaded from: classes.dex */
    private static class UpdateInfoCallback extends IRequestCallback.Stub {
        private WeakReference<HapticFragment> mFragmentRef;

        public UpdateInfoCallback(HapticFragment hapticFragment) {
            this.mFragmentRef = new WeakReference<>(hapticFragment);
        }

        @Override // com.android.settings.aidl.IRequestCallback
        public void onRequestComplete(int i, String str) {
            HapticFragment hapticFragment = this.mFragmentRef.get();
            if (i == 2 && hapticFragment != null) {
                hapticFragment.initHapticParams(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initHapticParams(String str) {
        MyHandler myHandler;
        if (TextUtils.isEmpty(str) || !ParseMiShopDataUtils.showBasicItems(str)) {
            return;
        }
        String str2 = null;
        JSONArray basicItemsArray = ParseMiShopDataUtils.getBasicItemsArray(str);
        if (basicItemsArray != null && basicItemsArray.length() > 0) {
            for (int i = 0; i < basicItemsArray.length(); i++) {
                JSONObject jSONObject = JSONUtils.getJSONObject(basicItemsArray, i);
                if (ParseMiShopDataUtils.getItemIndex(jSONObject) == 2) {
                    str2 = ParseMiShopDataUtils.getItemSummary(jSONObject);
                }
            }
        }
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str2.trim()) || (myHandler = this.mHandler) == null) {
            return;
        }
        Message obtainMessage = myHandler.obtainMessage(1);
        obtainMessage.obj = str2;
        this.mHandler.sendMessage(obtainMessage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHapticDesc(String str) {
        PreferenceCategory preferenceCategory = this.mHapticMotorCategory;
        if (preferenceCategory != null) {
            preferenceCategory.setVisible(!TextUtils.isEmpty(str));
            this.mHapticMotorPreference.setVisible(!TextUtils.isEmpty(str));
            this.mHapticMotorPreference.setSummary(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "HapticFragment";
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 7;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.settings_haptic_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new MyHandler(Looper.getMainLooper(), this);
        this.mHapticFeedbackCategory = (PreferenceCategory) findPreference("haptic_feedback_category_new");
        this.mHapticFeedbackSeekbar = (HapticSeekBarPreference) findPreference("haptic_feedback_progress_new");
        this.mSystemHapticPreference = (CheckBoxPreference) findPreference("system_haptic_feedback_new");
        HapticDemoVideoPreference hapticDemoVideoPreference = (HapticDemoVideoPreference) findPreference("key_haptic_main_video");
        this.mHapticDemoVideoPreference = hapticDemoVideoPreference;
        hapticDemoVideoPreference.setVideoState(this);
        this.mSystemHapticPreference.setChecked(MiuiSoundSettings.isSystemHapticEnable(getContext()));
        this.mSystemHapticPreference.setOnPreferenceChangeListener(this);
        this.mSystemHapticPreference.setTitle(R.string.open_haptic_feedback);
        this.mSystemHapticPreference.setSummary(R.string.haptic_feedback_summary);
        this.mHapticFeedbackSeekbar.setIcon(R.drawable.ic_haptic_feedback);
        this.mHapticMotorCategory = (PreferenceCategory) findPreference("haptic_motor_category");
        SoundSpeakerDescPreference soundSpeakerDescPreference = (SoundSpeakerDescPreference) findPreference("haptic_motor_preference");
        this.mHapticMotorPreference = soundSpeakerDescPreference;
        soundSpeakerDescPreference.setIcon(R.drawable.haptic_motor_icon);
        this.mHapticMotorCategory.setVisible(false);
        this.mHapticMotorPreference.setVisible(false);
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        this.mDeviceInfoCallback = new UpdateInfoCallback(this);
        this.mRemoteServiceConn = new RemoteServiceConn();
        RemoteServiceUtil.bindRemoteService(getActivity(), this.mRemoteServiceConn);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        IRemoteGetDeviceInfoService iRemoteGetDeviceInfoService = this.mRemoteService;
        if (iRemoteGetDeviceInfoService != null) {
            try {
                UpdateInfoCallback updateInfoCallback = this.mDeviceInfoCallback;
                if (updateInfoCallback != null) {
                    iRemoteGetDeviceInfoService.unregisteCallback(updateInfoCallback);
                    this.mDeviceInfoCallback = null;
                }
                this.mRemoteService = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (this.mRemoteServiceConn != null) {
            RemoteServiceUtil.unBindRemoteService(getActivity(), this.mRemoteServiceConn);
        }
        MyHandler myHandler = this.mHandler;
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.android.settings.haptic.HapticDemoVideoPreference.IVideoState
    public void onHapticVideoStateChange(boolean z) {
        if (getActivity() == null) {
            return;
        }
        if (z) {
            getActivity().getWindow().addFlags(128);
        } else {
            getActivity().getWindow().clearFlags(128);
        }
        this.mHapticFeedbackSeekbar.setIsHapticVideoPlaying(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getListView().isComputingLayout()) {
            return false;
        }
        if (preference == this.mSystemHapticPreference) {
            this.mHapticFeedbackSeekbar.setVisible(!r0.isChecked());
            MiuiSoundSettings.setSystemHapticEnable(getContext(), ((Boolean) obj).booleanValue());
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference != this.mSystemHapticPreference || this.mHapticFeedbackSeekbar == null) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        super.onPreferenceTreeClick(preferenceScreen, preference);
        this.mHapticFeedbackSeekbar.setVisible(this.mSystemHapticPreference.isChecked());
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        CheckBoxPreference checkBoxPreference;
        super.onResume();
        HapticSeekBarPreference hapticSeekBarPreference = this.mHapticFeedbackSeekbar;
        if (hapticSeekBarPreference != null && (checkBoxPreference = this.mSystemHapticPreference) != null) {
            hapticSeekBarPreference.setVisible(checkBoxPreference.isChecked());
        }
        if (((MiuiSoundSettingsActivity) getActivity()).getCurrentPage() == 1) {
            this.mHapticDemoVideoPreference.onVisible();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mHapticDemoVideoPreference.onStart();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mHapticDemoVideoPreference.onStop();
    }

    public void onVisible(boolean z) {
        this.mHapticDemoVideoPreference.onVisible(z);
    }
}
