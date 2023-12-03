package com.android.settings.backup;

import android.content.Intent;

/* loaded from: classes.dex */
public class PrivacySettingsConfigData {
    private static PrivacySettingsConfigData sInstance;
    private boolean mBackupEnabled = false;
    private boolean mBackupGray = false;
    private Intent mConfigIntent = null;
    private String mConfigSummary = null;
    private Intent mManageIntent = null;
    private CharSequence mManageLabel = null;

    private PrivacySettingsConfigData() {
    }

    public static PrivacySettingsConfigData getInstance() {
        if (sInstance == null) {
            sInstance = new PrivacySettingsConfigData();
        }
        return sInstance;
    }

    public Intent getConfigIntent() {
        return this.mConfigIntent;
    }

    public String getConfigSummary() {
        return this.mConfigSummary;
    }

    public Intent getManageIntent() {
        return this.mManageIntent;
    }

    public CharSequence getManageLabel() {
        return this.mManageLabel;
    }

    public boolean isBackupEnabled() {
        return this.mBackupEnabled;
    }

    public boolean isBackupGray() {
        return this.mBackupGray;
    }

    public void setBackupEnabled(boolean z) {
        this.mBackupEnabled = z;
    }

    public void setBackupGray(boolean z) {
        this.mBackupGray = z;
    }

    public void setConfigIntent(Intent intent) {
        this.mConfigIntent = intent;
    }

    public void setConfigSummary(String str) {
        this.mConfigSummary = str;
    }

    public void setManageIntent(Intent intent) {
        this.mManageIntent = intent;
    }

    public void setManageLabel(CharSequence charSequence) {
        this.mManageLabel = charSequence;
    }
}
