package com.android.settings;

import android.app.Activity;
import android.app.MiuiActivitySplitterImpl;
import android.content.Intent;
import android.view.ViewGroup;
import com.android.settingslib.utils.SplitUtils;

/* loaded from: classes.dex */
public class MiuiCustSplitUtilsImpl extends MiuiCustSplitUtils {
    private MiuiActivitySplitterImpl mIMiuiActivitySplitterImpl;

    public MiuiCustSplitUtilsImpl(Activity activity) {
        super(activity);
        if (SplitUtils.isSplitAllowed()) {
            this.mIMiuiActivitySplitterImpl = MiuiActivitySplitterImpl.getDefault(activity, isBaseActivity(activity));
        }
    }

    private boolean isBaseActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        return activity.getClass().getName().equals(MiuiSettings.class.getName());
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public void finishAllSubActivities() {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            miuiActivitySplitterImpl.finishAllSubActivities();
        }
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public Intent getCurrentSubIntent() {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            return miuiActivitySplitterImpl.getCurrentSubIntent();
        }
        return null;
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public boolean isSecondStageActivity() {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            return miuiActivitySplitterImpl.isSplitSecondActivity();
        }
        return false;
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public boolean reachSplitSize() {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            return miuiActivitySplitterImpl.reachSplitSize();
        }
        return false;
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public void setFirstIntent(Intent intent) {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            miuiActivitySplitterImpl.setFirstIntent(intent);
        }
    }

    @Override // com.android.settings.MiuiCustSplitUtils
    public void setSplit(ViewGroup viewGroup) {
        MiuiActivitySplitterImpl miuiActivitySplitterImpl = this.mIMiuiActivitySplitterImpl;
        if (miuiActivitySplitterImpl != null) {
            miuiActivitySplitterImpl.setSplit(viewGroup);
        }
    }
}
