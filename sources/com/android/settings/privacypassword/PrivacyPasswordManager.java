package com.android.settings.privacypassword;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.security.ChooseLockSettingsHelper;
import android.text.TextUtils;

/* loaded from: classes2.dex */
public class PrivacyPasswordManager {
    private static PrivacyPasswordManager sInstance;
    private ContentResolver mContentResolver;
    private Context mContext;

    private PrivacyPasswordManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mContentResolver = applicationContext.getContentResolver();
    }

    public static synchronized PrivacyPasswordManager getInstance(Context context) {
        PrivacyPasswordManager privacyPasswordManager;
        synchronized (PrivacyPasswordManager.class) {
            if (sInstance == null) {
                sInstance = new PrivacyPasswordManager(context);
            }
            privacyPasswordManager = sInstance;
        }
        return privacyPasswordManager;
    }

    public void bindXiaoMiAccount(String str) {
        Settings.Secure.putString(this.mContentResolver, "privacy_add_account_md5", !TextUtils.isEmpty(str) ? PrivacyPasswordUtils.md5Hex(str.getBytes()) : null);
    }

    public int getACLockMode() {
        return Settings.Secure.getInt(this.mContentResolver, "access_control_lock_mode", 1);
    }

    public String getBindXiaoMiAccount() {
        String string = Settings.Secure.getString(this.mContentResolver, "privacy_password_bind_xiaomi_account");
        if (string != null) {
            Settings.Secure.putString(this.mContentResolver, "privacy_add_account_md5", PrivacyPasswordUtils.md5Hex(string.getBytes()));
            Settings.Secure.putString(this.mContentResolver, "privacy_password_bind_xiaomi_account", null);
        }
        return Settings.Secure.getString(this.mContentResolver, "privacy_add_account_md5");
    }

    public long getLockoutAttepmDeadline() {
        return Settings.Secure.getLong(this.mContentResolver, "privacy_password_countDownTimer_deadline", 0L);
    }

    public boolean havePattern() {
        return new ChooseLockSettingsHelper(this.mContext).isPrivacyPasswordEnabled();
    }

    public boolean isConvenientEnabled() {
        return Settings.Secure.getInt(this.mContentResolver, "access_control_lock_convenient", 0) == 1;
    }

    public boolean isFingerprintEnable() {
        return Settings.Secure.getInt(this.mContentResolver, "fingerprint_apply_to_privacy_password", 1) == 2;
    }

    public boolean isNeverRemind() {
        return this.mContext.getSharedPreferences("privacy_password_sharedPreference", 0).getBoolean("nerver_remind", false);
    }

    public boolean isNeverRemindOpenFinger() {
        return this.mContext.getSharedPreferences("privacy_password_sharedPreference", 0).getBoolean("remind_open_fingerprint", false);
    }

    public boolean isUsedPrivacyInBussiness() {
        return Settings.Secure.getInt(this.mContentResolver, "privacy_password_status", 0) == 1;
    }

    public boolean isVisibilePattern() {
        return Settings.Secure.getInt(this.mContentResolver, "privacy_password_is_visible_pattern", 1) == 1;
    }

    public void setFingerprintEnable(boolean z) {
        Settings.Secure.putInt(this.mContentResolver, "fingerprint_apply_to_privacy_password", z ? 2 : 1);
    }

    public void setIsNeverRemindOpenFinger(boolean z) {
        this.mContext.getSharedPreferences("privacy_password_sharedPreference", 0).edit().putBoolean("remind_open_fingerprint", z).commit();
    }

    public void setLockoutAttepmpDeadline(long j) {
        Settings.Secure.putLong(this.mContentResolver, "privacy_password_countDownTimer_deadline", j);
    }

    public void setNerverRemind(boolean z) {
        this.mContext.getSharedPreferences("privacy_password_sharedPreference", 0).edit().putBoolean("nerver_remind", z).commit();
    }

    public void setPasswordEnable(Activity activity, boolean z) {
        new ChooseLockSettingsHelper(activity, 3).setPrivacyPasswordEnable(z);
    }

    public void setUsedPrivacyInBussiness(boolean z) {
        Settings.Secure.putInt(this.mContentResolver, "privacy_password_status", z ? 1 : 0);
    }

    public void setVisibilePattern(boolean z) {
        Settings.Secure.putInt(this.mContentResolver, "privacy_password_is_visible_pattern", z ? 1 : 0);
    }
}
