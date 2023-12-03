package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AccountPreference extends Preference {
    private Account mAccount;
    private ArrayList<String> mAuthorities;
    private boolean mShowTypeIcon;
    private int mStatus;
    private ImageView mSyncStatusIcon;

    public AccountPreference(Context context, Account account, Drawable drawable, ArrayList<String> arrayList, boolean z) {
        super(context);
        this.mAccount = account;
        this.mAuthorities = arrayList;
        this.mShowTypeIcon = z;
        if (z) {
            setIcon(drawable);
        } else {
            setIcon(getSyncStatusIcon(1));
        }
        setTitle(this.mAccount.name);
        setSummary("");
        setPersistent(false);
        setSyncStatus(1, false);
    }

    private String getSyncContentDescription(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        Log.e("AccountPreference", "Unknown sync status: " + i);
                        return getContext().getString(R.string.accessibility_sync_error);
                    }
                    return getContext().getString(R.string.accessibility_sync_in_progress);
                }
                return getContext().getString(R.string.accessibility_sync_error);
            }
            return getContext().getString(R.string.accessibility_sync_disabled);
        }
        return getContext().getString(R.string.accessibility_sync_enabled);
    }

    private int getSyncStatusIcon(int i) {
        if (i != 0) {
            if (i == 1) {
                return R.drawable.ic_settings_sync_disabled;
            }
            if (i == 2) {
                return R.drawable.ic_settings_sync_failed;
            }
            if (i != 3) {
                int i2 = R.drawable.ic_settings_sync_failed;
                Log.e("AccountPreference", "Unknown sync status: " + i);
                return i2;
            }
        }
        return R.drawable.ic_settings_sync;
    }

    private int getSyncStatusMessage(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        int i2 = R.string.sync_error;
                        Log.e("AccountPreference", "Unknown sync status: " + i);
                        return i2;
                    }
                    return R.string.sync_in_progress;
                }
                return R.string.sync_error;
            }
            return R.string.sync_disabled;
        }
        return R.string.sync_enabled;
    }

    public Account getAccount() {
        return this.mAccount;
    }

    public ArrayList<String> getAuthorities() {
        return this.mAuthorities;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mShowTypeIcon) {
            return;
        }
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
        this.mSyncStatusIcon = imageView;
        imageView.setImageResource(getSyncStatusIcon(this.mStatus));
        this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
    }

    public void setSyncStatus(int i, boolean z) {
        ImageView imageView;
        if (this.mStatus == i) {
            Log.d("AccountPreference", "Status is the same, not changing anything");
            return;
        }
        this.mStatus = i;
        if (!this.mShowTypeIcon && (imageView = this.mSyncStatusIcon) != null) {
            imageView.setImageResource(getSyncStatusIcon(i));
            this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
        }
        if (z) {
            setSummary(getSyncStatusMessage(i));
        }
    }
}
