package com.android.settings.development;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiDirectEnterSystemController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    private LockPatternUtils mLockPatternUtils;
    private LockScreenAsyncTask mLockScreenAsyncTask;

    /* loaded from: classes.dex */
    public class LockScreenAsyncTask extends AsyncTask<Boolean, Void, Void> {
        public LockScreenAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Boolean... boolArr) {
            MiuiDirectEnterSystemController.this.mLockPatternUtils.setLockScreenDisabled(boolArr[0].booleanValue(), UserHandle.myUserId());
            return null;
        }
    }

    public MiuiDirectEnterSystemController(Context context) {
        super(context);
        this.mLockPatternUtils = new LockPatternUtils(context);
    }

    public static boolean isEnabled(LockPatternUtils lockPatternUtils, int i) {
        return (lockPatternUtils == null || lockPatternUtils.isSecure(i) || Build.IS_ALPHA_BUILD) ? false : true;
    }

    private void updateUnlockEnterSystemPref(boolean z) {
        LockScreenAsyncTask lockScreenAsyncTask = this.mLockScreenAsyncTask;
        if (lockScreenAsyncTask != null) {
            lockScreenAsyncTask.cancel(false);
        }
        LockScreenAsyncTask lockScreenAsyncTask2 = new LockScreenAsyncTask();
        this.mLockScreenAsyncTask = lockScreenAsyncTask2;
        lockScreenAsyncTask2.execute(Boolean.valueOf(z));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "unlock_enter_system";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != null && TextUtils.equals("unlock_enter_system", preference.getKey()) && (obj instanceof Boolean)) {
            updateUnlockEnterSystemPref(((Boolean) obj).booleanValue());
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int userId = UserHandle.getUserId(Process.myUid());
        if (!isEnabled(this.mLockPatternUtils, userId)) {
            this.mPreference.setEnabled(false);
            return;
        }
        this.mPreference.setEnabled(true);
        ((CheckBoxPreference) this.mPreference).setChecked(this.mLockPatternUtils.isLockScreenDisabled(userId));
    }
}
