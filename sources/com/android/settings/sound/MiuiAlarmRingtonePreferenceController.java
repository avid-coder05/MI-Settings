package com.android.settings.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.AudioHelper;
import com.android.settings.utils.MiuiBaseController;

/* loaded from: classes2.dex */
public class MiuiAlarmRingtonePreferenceController extends MiuiBaseController<Preference> {
    private AudioHelper mHelper;
    private boolean mIsRegistered;
    private int mManagedProfileId;
    private final BroadcastReceiver mManagedProfileReceiver;
    private UserManager mUserManager;

    public MiuiAlarmRingtonePreferenceController(PreferenceScreen preferenceScreen) {
        super(preferenceScreen);
        this.mIsRegistered = false;
        this.mManagedProfileReceiver = new BroadcastReceiver() { // from class: com.android.settings.sound.MiuiAlarmRingtonePreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int identifier = ((UserHandle) intent.getExtra("android.intent.extra.USER")).getIdentifier();
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                    MiuiAlarmRingtonePreferenceController.this.onManagedProfileAdded(identifier);
                } else if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                    MiuiAlarmRingtonePreferenceController.this.onManagedProfileRemoved(identifier);
                }
            }
        };
    }

    private Context getManagedProfileContext() {
        int i = this.mManagedProfileId;
        if (i == -10000) {
            return null;
        }
        return this.mHelper.createPackageContextAsUser(i);
    }

    private void updatePreference() {
        setVisible(this.mPreference, isAvailable());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "alarm_ringtone";
    }

    @Override // com.android.settings.utils.MiuiBaseController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (getManagedProfileContext() == null || this.mHelper.getManagedProfileId(this.mUserManager) == -10000) ? false : true;
    }

    @Override // com.android.settings.utils.MiuiBaseController
    protected void onAttach() {
        this.mUserManager = UserManager.get(this.mContext);
        this.mHelper = new AudioHelper(this.mContext);
    }

    public void onManagedProfileAdded(int i) {
        if (this.mManagedProfileId == -10000) {
            this.mManagedProfileId = i;
            updatePreference();
        }
    }

    public void onManagedProfileRemoved(int i) {
        if (this.mManagedProfileId == i) {
            this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
            updatePreference();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.utils.MiuiBaseController
    public void onPause() {
        super.onPause();
        if (this.mIsRegistered) {
            this.mIsRegistered = false;
            this.mContext.unregisterReceiver(this.mManagedProfileReceiver);
        }
    }

    @Override // com.android.settings.utils.MiuiBaseController
    public void onResume() {
        if (!this.mIsRegistered) {
            this.mIsRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            this.mContext.registerReceiver(this.mManagedProfileReceiver, intentFilter);
        }
        this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
        updatePreference();
    }
}
