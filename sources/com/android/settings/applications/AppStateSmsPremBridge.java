package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.SmsManager;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AppStateSmsPremBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_APP_PREMIUM_SMS = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateSmsPremBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj = appEntry.extraInfo;
            return (obj instanceof SmsState) && ((SmsState) obj).smsState != 0;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }
    };
    private final Context mContext;
    private final SmsManager mSmsManager;

    /* loaded from: classes.dex */
    public static class SmsState {
        public int smsState;
    }

    public AppStateSmsPremBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mSmsManager = SmsManager.getDefault();
    }

    private int getSmsState(String str) {
        return this.mSmsManager.getPremiumSmsConsent(str);
    }

    public SmsState getState(String str) {
        SmsState smsState = new SmsState();
        smsState.smsState = getSmsState(str);
        return smsState;
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            ApplicationInfo applicationInfo = appEntry.info;
            updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
        }
    }

    public void setSmsState(String str, int i) {
        this.mSmsManager.setPremiumSmsConsent(str, i);
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = getState(str);
    }
}
