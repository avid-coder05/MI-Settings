package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.net.TetheringManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.SystemProperties;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class UsbDetailsFunctionsController extends UsbDetailsController implements RadioButtonPreference.OnClickListener {
    private static final boolean DEBUG = Log.isLoggable("UsbFunctionsCtrl", 3);
    static final Map<Long, Integer> FUNCTIONS_MAP;
    private Handler mHandler;
    OnStartTetheringCallback mOnStartTetheringCallback;
    long mPreviousFunction;
    private PreferenceCategory mProfilesContainer;
    private TetheringManager mTetheringManager;

    /* loaded from: classes.dex */
    final class OnStartTetheringCallback implements TetheringManager.StartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringFailed(int i) {
            Log.w("UsbFunctionsCtrl", "onTetheringFailed() error : " + i);
            UsbDetailsFunctionsController usbDetailsFunctionsController = UsbDetailsFunctionsController.this;
            usbDetailsFunctionsController.mUsbBackend.setCurrentFunctions(usbDetailsFunctionsController.mPreviousFunction);
        }
    }

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        FUNCTIONS_MAP = linkedHashMap;
        linkedHashMap.put(4L, Integer.valueOf(R.string.usb_use_file_transfers));
        if (!TextUtils.equals("jp_sb", SystemProperties.get("ro.miui.customized.region", ""))) {
            linkedHashMap.put(32L, Integer.valueOf(R.string.usb_use_tethering));
        }
        linkedHashMap.put(8L, Integer.valueOf(R.string.usb_use_MIDI));
        linkedHashMap.put(16L, Integer.valueOf(R.string.usb_use_photo_transfers));
        linkedHashMap.put(0L, Integer.valueOf(R.string.usb_use_charging_only));
    }

    public UsbDetailsFunctionsController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mOnStartTetheringCallback = new OnStartTetheringCallback();
        this.mPreviousFunction = this.mUsbBackend.getCurrentFunctions();
        this.mHandler = new Handler(context.getMainLooper());
    }

    public static int getFunction(long j) {
        Map<Long, Integer> map = FUNCTIONS_MAP;
        if (!map.containsKey(Long.valueOf(j)) || map.get(Long.valueOf(j)) == null) {
            Log.e("UsbFunctionsCtrl", "getFunction error: " + j);
            return R.string.usb_use_charging_only;
        }
        return map.get(Long.valueOf(j)).intValue();
    }

    private RadioButtonPreference getProfilePreference(String str, int i) {
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) this.mProfilesContainer.findPreference(str);
        if (radioButtonPreference == null) {
            RadioButtonPreference radioButtonPreference2 = new RadioButtonPreference(this.mProfilesContainer.getContext());
            radioButtonPreference2.setKey(str);
            radioButtonPreference2.setTitle(i);
            radioButtonPreference2.setSingleLineTitle(false);
            radioButtonPreference2.setOnClickListener(this);
            this.mProfilesContainer.addPreference(radioButtonPreference2);
            return radioButtonPreference2;
        }
        return radioButtonPreference;
    }

    private boolean isAccessoryMode(long j) {
        return (j & 2) != 0;
    }

    private boolean isClickEventIgnored(long j, long j2) {
        return isAccessoryMode(j2) && j == 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mProfilesContainer = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        refresh(false, this.mUsbBackend.getDefaultUsbFunctions(), 0, 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_details_functions";
    }

    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(radioButtonPreference.getKey());
        long currentFunctions = this.mUsbBackend.getCurrentFunctions();
        if (DEBUG) {
            Log.d("UsbFunctionsCtrl", "onRadioButtonClicked() function : " + usbFunctionsFromString + ", toString() : " + UsbManager.usbFunctionsToString(usbFunctionsFromString) + ", previousFunction : " + currentFunctions + ", toString() : " + UsbManager.usbFunctionsToString(currentFunctions));
        }
        if (usbFunctionsFromString == currentFunctions || Utils.isMonkeyRunning() || isClickEventIgnored(usbFunctionsFromString, currentFunctions)) {
            return;
        }
        this.mPreviousFunction = currentFunctions;
        RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) this.mProfilesContainer.findPreference(UsbBackend.usbFunctionsToString(currentFunctions));
        if (radioButtonPreference2 != null) {
            radioButtonPreference2.setChecked(false);
            radioButtonPreference.setChecked(true);
        }
        if (usbFunctionsFromString == 32 || usbFunctionsFromString == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            this.mTetheringManager.startTethering(1, new HandlerExecutor(this.mHandler), this.mOnStartTetheringCallback);
        } else {
            this.mUsbBackend.setCurrentFunctions(usbFunctionsFromString);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        if (DEBUG) {
            Log.d("UsbFunctionsCtrl", "refresh() connected : " + z + ", functions : " + j + ", powerRole : " + i + ", dataRole : " + i2);
        }
        if (z && i2 == 2) {
            this.mProfilesContainer.setEnabled(true);
        } else {
            this.mProfilesContainer.setEnabled(false);
        }
        Iterator<Long> it = FUNCTIONS_MAP.keySet().iterator();
        while (it.hasNext()) {
            long longValue = it.next().longValue();
            RadioButtonPreference profilePreference = getProfilePreference(UsbBackend.usbFunctionsToString(longValue), FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            if (!this.mUsbBackend.areFunctionsSupported(longValue)) {
                this.mProfilesContainer.removePreference(profilePreference);
            } else if (isAccessoryMode(j)) {
                profilePreference.setChecked(4 == longValue);
            } else if (j == PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                profilePreference.setChecked(32 == longValue);
            } else {
                profilePreference.setChecked(j == longValue);
            }
        }
    }
}
