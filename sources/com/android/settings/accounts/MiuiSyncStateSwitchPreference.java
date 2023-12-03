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
public class MiuiSyncStateSwitchPreference extends CheckBoxPreference {
    private Account mAccount;
    private String mAuthority;
    private boolean mFailed;
    private boolean mIsActive;
    private boolean mIsPending;
    private boolean mOneTimeSyncMode;
    private String mPackageName;
    private int mUid;

    public MiuiSyncStateSwitchPreference(Context context, Account account, String str, String str2, int i) {
        super(context, null, 0, R.style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        setup(account, str, str2, i);
        setLayoutResource(R.layout.miuix_preference_layout);
    }

    public MiuiSyncStateSwitchPreference(Context context, AttributeSet attributeSet) {
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

    public Account getAccount() {
        return this.mAccount;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getUid() {
        return this.mUid;
    }

    public boolean isOneTimeSyncMode() {
        return this.mOneTimeSyncMode;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        AnimatedImageView animatedImageView = (AnimatedImageView) view.findViewById(R.id.sync_active);
        View findViewById = view.findViewById(R.id.sync_failed);
        boolean z = this.mIsActive || this.mIsPending;
        animatedImageView.setVisibility(z ? 0 : 8);
        animatedImageView.setAnimating(this.mIsActive);
        findViewById.setVisibility(this.mFailed && !z ? 0 : 8);
        View findViewById2 = view.findViewById(16908289);
        if (this.mOneTimeSyncMode) {
            findViewById2.setVisibility(8);
            ((TextView) view.findViewById(16908304)).setText(getContext().getString(R.string.sync_one_time_sync, getSummary()));
        } else {
            findViewById2.setVisibility(0);
        }
        View findViewById3 = view.findViewById(R.id.icon_frame);
        if (findViewById3 != null) {
            findViewById3.setVisibility(8);
        }
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

    public void setActive(boolean z) {
        this.mIsActive = z;
        notifyChanged();
    }

    public void setFailed(boolean z) {
        this.mFailed = z;
        notifyChanged();
    }

    public void setOneTimeSyncMode(boolean z) {
        this.mOneTimeSyncMode = z;
        notifyChanged();
    }

    public void setPending(boolean z) {
        this.mIsPending = z;
        notifyChanged();
    }

    public void setup(Account account, String str, String str2, int i) {
        this.mAccount = account;
        this.mAuthority = str;
        this.mPackageName = str2;
        this.mUid = i;
        notifyChanged();
    }
}
