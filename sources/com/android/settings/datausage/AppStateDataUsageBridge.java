package com.android.settings.datausage;

import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AppStateDataUsageBridge extends AppStateBaseBridge {
    private final DataSaverBackend mDataSaverBackend;

    /* loaded from: classes.dex */
    public static class DataUsageState {
        public boolean isDataSaverAllowlisted;
        public boolean isDataSaverDenylisted;

        public DataUsageState(boolean z, boolean z2) {
            this.isDataSaverAllowlisted = z;
            this.isDataSaverDenylisted = z2;
        }
    }

    public AppStateDataUsageBridge(ApplicationsState applicationsState, AppStateBaseBridge.Callback callback, DataSaverBackend dataSaverBackend) {
        super(applicationsState, callback);
        this.mDataSaverBackend = dataSaverBackend;
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            appEntry.extraInfo = new DataUsageState(this.mDataSaverBackend.isAllowlisted(appEntry.info.uid), this.mDataSaverBackend.isDenylisted(appEntry.info.uid));
        }
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = new DataUsageState(this.mDataSaverBackend.isAllowlisted(i), this.mDataSaverBackend.isDenylisted(i));
    }
}
