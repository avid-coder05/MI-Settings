package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.TetheringManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class UsbDefaultFragment extends RadioButtonPickerFragment {
    long mCurrentFunctions;
    Handler mHandler;
    long mPreviousFunctions;
    TetheringManager mTetheringManager;
    UsbBackend mUsbBackend;
    private UsbConnectionBroadcastReceiver mUsbReceiver;
    OnStartTetheringCallback mOnStartTetheringCallback = new OnStartTetheringCallback();
    boolean mIsStartTethering = false;
    private boolean mIsConnected = false;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() { // from class: com.android.settings.connecteddevice.usb.UsbDefaultFragment$$ExternalSyntheticLambda0
        @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
        public final void onUsbConnectionChanged(boolean z, long j, int i, int i2) {
            UsbDefaultFragment.this.lambda$new$0(z, j, i, i2);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class OnStartTetheringCallback implements TetheringManager.StartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringFailed(int i) {
            Log.w("UsbDefaultFragment", "onTetheringFailed() error : " + i);
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mPreviousFunctions);
            UsbDefaultFragment.this.updateCandidates();
        }

        public void onTetheringStarted() {
            Log.d("UsbDefaultFragment", "onTetheringStarted()");
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mCurrentFunctions);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2) {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        Log.d("UsbDefaultFragment", "UsbConnectionListener() connected : " + z + ", functions : " + j + ", defaultFunctions : " + defaultUsbFunctions + ", mIsStartTethering : " + this.mIsStartTethering);
        if (z && !this.mIsConnected && ((defaultUsbFunctions == 32 || defaultUsbFunctions == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) && !this.mIsStartTethering)) {
            this.mCurrentFunctions = defaultUsbFunctions;
            startTethering();
        }
        if (this.mIsStartTethering && z) {
            this.mCurrentFunctions = j;
            refresh(j);
            this.mUsbBackend.setDefaultUsbFunctions(j);
            this.mIsStartTethering = false;
        }
        this.mIsConnected = z;
    }

    private void refresh(long j) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Iterator<Long> it = UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet().iterator();
        while (it.hasNext()) {
            long longValue = it.next().longValue();
            RadioButtonPickerFragment.MiuiRadioButtonPreference miuiRadioButtonPreference = (RadioButtonPickerFragment.MiuiRadioButtonPreference) preferenceScreen.findPreference(UsbBackend.usbFunctionsToString(longValue));
            if (miuiRadioButtonPreference != null) {
                boolean areFunctionsSupported = this.mUsbBackend.areFunctionsSupported(longValue);
                miuiRadioButtonPreference.setEnabled(areFunctionsSupported);
                if (areFunctionsSupported) {
                    if (j == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                        miuiRadioButtonPreference.setChecked(32 == longValue);
                    } else {
                        miuiRadioButtonPreference.setChecked(j == longValue);
                    }
                }
            }
        }
    }

    private void startTethering() {
        Log.d("UsbDefaultFragment", "startTethering()");
        this.mIsStartTethering = true;
        this.mTetheringManager.startTethering(1, new HandlerExecutor(this.mHandler), this.mOnStartTetheringCallback);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList newArrayList = Lists.newArrayList();
        Iterator<Long> it = UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet().iterator();
        while (it.hasNext()) {
            long longValue = it.next().longValue();
            final String string = getContext().getString(UsbDetailsFunctionsController.FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            final String usbFunctionsToString = UsbBackend.usbFunctionsToString(longValue);
            if (this.mUsbBackend.areFunctionsSupported(longValue)) {
                newArrayList.add(new CandidateInfo(true) { // from class: com.android.settings.connecteddevice.usb.UsbDefaultFragment.1
                    @Override // com.android.settingslib.widget.CandidateInfo
                    public String getKey() {
                        return usbFunctionsToString;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public Drawable loadIcon() {
                        return null;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public CharSequence loadLabel() {
                        return string;
                    }
                });
            }
        }
        return newArrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        if (defaultUsbFunctions == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            defaultUsbFunctions = 32;
        }
        return UsbBackend.usbFunctionsToString(defaultUsbFunctions);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1312;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.usb_default_fragment;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUsbBackend = new UsbBackend(context);
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mHandler = new Handler(context.getMainLooper());
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        this.mCurrentFunctions = this.mUsbBackend.getDefaultUsbFunctions();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        getPreferenceScreen().addPreference(new FooterPreference.Builder(getActivity()).setTitle(R.string.usb_default_info).build());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mUsbBackend.setDefaultUsbFunctions(this.mCurrentFunctions);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(str);
        this.mPreviousFunctions = this.mUsbBackend.getCurrentFunctions();
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        if (usbFunctionsFromString == 32 || usbFunctionsFromString == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            this.mCurrentFunctions = usbFunctionsFromString;
            startTethering();
            return true;
        }
        this.mIsStartTethering = false;
        this.mCurrentFunctions = usbFunctionsFromString;
        this.mUsbBackend.setDefaultUsbFunctions(usbFunctionsFromString);
        return true;
    }
}
