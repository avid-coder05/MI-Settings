package com.android.settings.applications.specialaccess.notificationaccess;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.service.notification.NotificationListenerFilter;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public abstract class TypeFilterPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private static final String FLAG_SEPARATOR = "\\|";
    private static final String TAG = "TypeFilterPrefCntlr";
    private ComponentName mCn;
    private NotificationListenerFilter mNlf;
    private NotificationBackend mNm;
    private ServiceInfo mSi;
    private int mTargetSdk;
    private int mUserId;

    public TypeFilterPreferenceController(Context context, String str) {
        super(context, str);
    }

    private boolean hasFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mNm.isNotificationListenerAccessGranted(this.mCn)) {
            if (this.mTargetSdk > 31) {
                return 0;
            }
            NotificationListenerFilter listenerFilter = this.mNm.getListenerFilter(this.mCn, this.mUserId);
            this.mNlf = listenerFilter;
            return (listenerFilter.areAllTypesAllowed() && this.mNlf.getDisallowedPackages().isEmpty()) ? 5 : 0;
        }
        return 5;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected abstract int getType();

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mNlf = this.mNm.getListenerFilter(this.mCn, this.mUserId);
        boolean booleanValue = ((Boolean) obj).booleanValue();
        int types = this.mNlf.getTypes();
        this.mNlf.setTypes(booleanValue ? getType() | types : (~getType()) & types);
        this.mNm.setListenerFilter(this.mCn, this.mUserId, this.mNlf);
        return true;
    }

    public TypeFilterPreferenceController setCn(ComponentName componentName) {
        this.mCn = componentName;
        return this;
    }

    public TypeFilterPreferenceController setNm(NotificationBackend notificationBackend) {
        this.mNm = notificationBackend;
        return this;
    }

    public TypeFilterPreferenceController setServiceInfo(ServiceInfo serviceInfo) {
        this.mSi = serviceInfo;
        return this;
    }

    public TypeFilterPreferenceController setTargetSdk(int i) {
        this.mTargetSdk = i;
        return this;
    }

    public TypeFilterPreferenceController setUserId(int i) {
        this.mUserId = i;
        return this;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z;
        Bundle bundle;
        String obj;
        NotificationListenerFilter listenerFilter = this.mNm.getListenerFilter(this.mCn, this.mUserId);
        this.mNlf = listenerFilter;
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
        checkBoxPreference.setChecked(hasFlag(listenerFilter.getTypes(), getType()));
        ServiceInfo serviceInfo = this.mSi;
        if (serviceInfo != null && (bundle = serviceInfo.metaData) != null && bundle.containsKey("android.service.notification.disabled_filter_types") && (obj = this.mSi.metaData.get("android.service.notification.disabled_filter_types").toString()) != null) {
            int i = 0;
            for (String str : obj.split(FLAG_SEPARATOR)) {
                if (!TextUtils.isEmpty(str)) {
                    if (str.equalsIgnoreCase("ONGOING")) {
                        i |= 8;
                    } else if (str.equalsIgnoreCase("CONVERSATIONS")) {
                        i |= 1;
                    } else if (str.equalsIgnoreCase("SILENT")) {
                        i |= 4;
                    } else if (str.equalsIgnoreCase("ALERTING")) {
                        i |= 2;
                    } else {
                        try {
                            i |= Integer.parseInt(str);
                        } catch (NumberFormatException unused) {
                        }
                    }
                }
            }
            if (hasFlag(i, getType())) {
                z = true;
                preference.setEnabled((getAvailabilityStatus() == 0 || (!z && !checkBoxPreference.isChecked())) ? false : true);
            }
        }
        z = false;
        preference.setEnabled((getAvailabilityStatus() == 0 || (!z && !checkBoxPreference.isChecked())) ? false : true);
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
