package com.android.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;
import com.android.settingslib.widget.AnimatedImageView;

/* loaded from: classes.dex */
public class SyncStateSwitchPreference extends CheckBoxPreference {
    private Account mAccount;
    private String mAuthority;
    private boolean mFailed;
    private boolean mIsActive;
    private boolean mIsPending;
    private boolean mOneTimeSyncMode;
    private String mPackageName;
    private int mUid;

    public SyncStateSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0, R.style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        this.mAccount = null;
        this.mAuthority = null;
        this.mPackageName = null;
        this.mUid = 0;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        AnimatedImageView animatedImageView = (AnimatedImageView) preferenceViewHolder.findViewById(R.id.sync_active);
        View findViewById = preferenceViewHolder.findViewById(R.id.sync_failed);
        boolean z = this.mIsActive || this.mIsPending;
        animatedImageView.setVisibility(z ? 0 : 8);
        animatedImageView.setAnimating(this.mIsActive);
        findViewById.setVisibility(this.mFailed && !z ? 0 : 8);
        View findViewById2 = preferenceViewHolder.findViewById(16908289);
        if (!this.mOneTimeSyncMode) {
            findViewById2.setVisibility(0);
            return;
        }
        findViewById2.setVisibility(8);
        ((TextView) preferenceViewHolder.findViewById(16908304)).setText(getContext().getString(R.string.sync_one_time_sync, getSummary()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        if (this.mOneTimeSyncMode) {
            return;
        }
        if (ActivityManager.isUserAMonkey()) {
            Log.d("SyncState", "ignoring monkey's attempt to flip sync state");
        } else {
            super.onClick();
        }
    }
}
