package com.android.settingslib.development;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemProperties;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes2.dex */
public abstract class AbstractLogdSizePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    static final String DEFAULT_SNET_TAG = "I";
    static final String LOW_RAM_CONFIG_PROPERTY_KEY = "ro.config.low_ram";
    static final String SELECT_LOGD_DEFAULT_SIZE_VALUE = "262144";
    static final String SELECT_LOGD_MINIMUM_SIZE_VALUE = "65536";
    static final String SELECT_LOGD_SIZE_PROPERTY = "persist.logd.size";
    static final String SELECT_LOGD_SNET_TAG_PROPERTY = "persist.log.tag.snet_event_log";
    private DropDownPreference mLogdSize;

    public AbstractLogdSizePreferenceController(Context context) {
        super(context);
    }

    private String defaultLogdSizeValue() {
        String str = SystemProperties.get("ro.logd.size");
        return (str == null || str.length() == 0) ? SystemProperties.get(LOW_RAM_CONFIG_PROPERTY_KEY).equals("true") ? SELECT_LOGD_MINIMUM_SIZE_VALUE : SELECT_LOGD_DEFAULT_SIZE_VALUE : str;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mLogdSize = (DropDownPreference) preferenceScreen.findPreference("select_logd_size");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_logd_size";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mLogdSize) {
            writeLogdSizeOption(obj);
            return true;
        }
        return false;
    }

    public void updateLogdSizeValues() {
        int i;
        if (this.mLogdSize != null) {
            String str = SystemProperties.get("persist.log.tag");
            String str2 = SystemProperties.get(SELECT_LOGD_SIZE_PROPERTY);
            if (str != null && str.startsWith("Settings")) {
                str2 = "32768";
            }
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcastSync(new Intent("com.android.settingslib.development.AbstractLogdSizePreferenceController.LOGD_SIZE_UPDATED").putExtra("CURRENT_LOGD_VALUE", str2));
            if (str2 == null || str2.length() == 0) {
                str2 = defaultLogdSizeValue();
            }
            String[] stringArray = this.mContext.getResources().getStringArray(R$array.select_logd_size_values);
            String string = this.mContext.getResources().getString(R$string.string_off);
            Resources resources = this.mContext.getResources();
            int i2 = R$string.string_KB;
            String format = String.format(resources.getString(i2), 64);
            String format2 = String.format(this.mContext.getResources().getString(i2), 256);
            Resources resources2 = this.mContext.getResources();
            int i3 = R$string.string_MB;
            String[] strArr = {string, format, format2, String.format(resources2.getString(i3), 1), String.format(this.mContext.getResources().getString(i3), 4), String.format(this.mContext.getResources().getString(i3), 8)};
            if (SystemProperties.get(LOW_RAM_CONFIG_PROPERTY_KEY).equals("true")) {
                DropDownPreference dropDownPreference = this.mLogdSize;
                int i4 = R$array.select_logd_size_lowram_titles;
                dropDownPreference.setEntries(i4);
                strArr = this.mContext.getResources().getStringArray(i4);
                i = 1;
            } else {
                i = 2;
            }
            String string2 = this.mContext.getResources().getString(R$string.string_offed);
            Resources resources3 = this.mContext.getResources();
            int i5 = R$string.string_KB_per_log_buffer;
            String format3 = String.format(resources3.getString(i5), 64);
            String format4 = String.format(this.mContext.getResources().getString(i5), 256);
            Resources resources4 = this.mContext.getResources();
            int i6 = R$string.string_MB_per_log_buffer;
            String[] strArr2 = {string2, format3, format4, String.format(resources4.getString(i6), 1), String.format(this.mContext.getResources().getString(i6), 4), String.format(this.mContext.getResources().getString(i6), 8)};
            for (int i7 = 0; i7 < strArr.length; i7++) {
                if (str2.equals(stringArray[i7]) || str2.equals(strArr[i7])) {
                    i = i7;
                    break;
                }
            }
            this.mLogdSize.setValue(stringArray[i]);
            this.mLogdSize.setSummary(strArr2[i]);
        }
    }

    public void writeLogdSizeOption(Object obj) {
        String str;
        boolean z = obj != null && obj.toString().equals("32768");
        String str2 = SystemProperties.get("persist.log.tag");
        if (str2 == null) {
            str2 = "";
        }
        String replaceFirst = str2.replaceAll(",+Settings", "").replaceFirst("^Settings,*", "").replaceAll(",+", ",").replaceFirst(",+$", "");
        if (z) {
            String str3 = SystemProperties.get(SELECT_LOGD_SNET_TAG_PROPERTY);
            if ((str3 == null || str3.length() == 0) && ((str = SystemProperties.get("log.tag.snet_event_log")) == null || str.length() == 0)) {
                SystemProperties.set(SELECT_LOGD_SNET_TAG_PROPERTY, DEFAULT_SNET_TAG);
            }
            if (replaceFirst.length() != 0) {
                replaceFirst = "," + replaceFirst;
            }
            replaceFirst = "Settings" + replaceFirst;
            obj = SELECT_LOGD_MINIMUM_SIZE_VALUE;
        }
        if (!replaceFirst.equals(str2)) {
            SystemProperties.set("persist.log.tag", replaceFirst);
        }
        String defaultLogdSizeValue = defaultLogdSizeValue();
        String obj2 = (obj == null || obj.toString().length() == 0) ? defaultLogdSizeValue : obj.toString();
        SystemProperties.set(SELECT_LOGD_SIZE_PROPERTY, defaultLogdSizeValue.equals(obj2) ? "" : obj2);
        SystemProperties.set("ctl.start", "logd-reinit");
        SystemPropPoker.getInstance().poke();
        updateLogdSizeValues();
    }
}
