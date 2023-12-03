package com.android.settings;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.compat.LockPatternUtilsCompat;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class LockPatternChecker {
    private static final String CURRENT_DEVICE;
    private static final boolean IS_NEED_COMPUTE_ATTEMPT_TIMES_DEVICE;

    /* loaded from: classes.dex */
    public interface OnCheckCallback {
        void onChecked(boolean z, int i);
    }

    /* loaded from: classes.dex */
    public interface OnCheckForUsersCallback {
        void onChecked(boolean z, int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface OnVerifyCallback {
        void onVerified(byte[] bArr, int i);
    }

    static {
        String str = Build.DEVICE;
        CURRENT_DEVICE = str;
        IS_NEED_COMPUTE_ATTEMPT_TIMES_DEVICE = "libra".equals(str) || "aqua".equals(str) || "kenzo".equals(str) || "kate".equals(str);
    }

    public static AsyncTask<?, ?, ?> checkPassword(final LockPatternUtils lockPatternUtils, final String str, final int i, final Context context, final OnCheckCallback onCheckCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.LockPatternChecker.5
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    boolean checkPassword = LockPatternUtilsCompat.checkPassword(lockPatternUtils, str, i);
                    if (checkPassword) {
                        LockPatternChecker.computeAttempTimes(context, i, true);
                    } else {
                        LockPatternChecker.computeAttempTimes(context, i, false);
                        this.mThrottleTimeout = LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i);
                    }
                    return Boolean.valueOf(checkPassword);
                } catch (IllegalStateException e) {
                    Log.e("LockPatternChecker", "Failed to decrypt blob", e);
                    this.mThrottleTimeout = LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i);
                    return Boolean.FALSE;
                } catch (LockPatternUtils.RequestThrottledException e2) {
                    this.mThrottleTimeout = e2.getTimeoutMs();
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckCallback.onChecked(bool.booleanValue(), this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> checkPasswordForUsers(final LockPatternUtils lockPatternUtils, final String str, final List<UserInfo> list, final Context context, final OnCheckForUsersCallback onCheckForUsersCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.LockPatternChecker.6
            private int mThrottleTimeout;
            private int mUserIdMatched = -10000;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        int i = ((UserInfo) it.next()).id;
                        if (lockPatternUtils.isLockPasswordEnabled(i)) {
                            if (LockPatternUtilsCompat.checkPassword(lockPatternUtils, str, i)) {
                                this.mUserIdMatched = i;
                                LockPatternChecker.computeAttempTimes(context, i, true);
                                return Boolean.TRUE;
                            }
                            LockPatternChecker.computeAttempTimes(context, i, false);
                            this.mThrottleTimeout = LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i);
                        }
                    }
                    return Boolean.FALSE;
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckForUsersCallback.onChecked(bool.booleanValue(), this.mUserIdMatched, this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> checkPattern(final LockPatternUtils lockPatternUtils, final List<LockPatternView.Cell> list, final int i, final Context context, final OnCheckCallback onCheckCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.LockPatternChecker.2
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    boolean checkPattern = LockPatternUtilsCompat.checkPattern(lockPatternUtils, list, i);
                    if (checkPattern) {
                        LockPatternChecker.computeAttempTimes(context, i, true);
                    } else {
                        LockPatternChecker.computeAttempTimes(context, i, false);
                        this.mThrottleTimeout = LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i);
                    }
                    return Boolean.valueOf(checkPattern);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckCallback.onChecked(bool.booleanValue(), this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> checkPatternForUsers(final LockPatternUtils lockPatternUtils, final List<LockPatternView.Cell> list, final List<UserInfo> list2, final Context context, final OnCheckForUsersCallback onCheckForUsersCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.LockPatternChecker.3
            private int mThrottleTimeout;
            private int mUserIdMatched = -10000;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    Iterator it = list2.iterator();
                    while (it.hasNext()) {
                        int i = ((UserInfo) it.next()).id;
                        if (lockPatternUtils.isLockPatternEnabled(i)) {
                            if (LockPatternUtilsCompat.checkPattern(lockPatternUtils, list, i)) {
                                this.mUserIdMatched = i;
                                LockPatternChecker.computeAttempTimes(context, i, true);
                                return Boolean.TRUE;
                            }
                            LockPatternChecker.computeAttempTimes(context, i, false);
                            this.mThrottleTimeout = LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i);
                        }
                    }
                    return Boolean.FALSE;
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckForUsersCallback.onChecked(bool.booleanValue(), this.mUserIdMatched, this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void computeAttempTimes(Context context, int i, boolean z) {
        if (IS_NEED_COMPUTE_ATTEMPT_TIMES_DEVICE) {
            Settings.Secure.putIntForUser(context.getContentResolver(), MiuiSettings.Secure.UNLOCK_FAILED_ATTEMPTS, z ? 0 : Settings.Secure.getIntForUser(context.getContentResolver(), MiuiSettings.Secure.UNLOCK_FAILED_ATTEMPTS, 0, i) + 1, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int computeRetryTimeout(Context context, LockPatternUtils lockPatternUtils, int i) {
        int i2 = 0;
        if (IS_NEED_COMPUTE_ATTEMPT_TIMES_DEVICE) {
            int intForUser = Settings.Secure.getIntForUser(context.getContentResolver(), MiuiSettings.Secure.UNLOCK_FAILED_ATTEMPTS, 0, i);
            if (intForUser == 5 || (intForUser >= 10 && intForUser < 30)) {
                i2 = 30000;
            } else if (intForUser >= 30 && intForUser < 140) {
                i2 = (int) (Math.pow(2.0d, (intForUser - 30) / 10.0d) * 30000.0d);
            } else if (intForUser >= 140) {
                i2 = 86400000;
            }
            if (i2 > 0) {
                lockPatternUtils.requireStrongAuth(8, i);
            }
            return i2;
        }
        return 0;
    }

    public static AsyncTask<?, ?, ?> verifyPassword(final LockPatternUtils lockPatternUtils, final String str, final long j, final int i, final Context context, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.settings.LockPatternChecker.4
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    VerifyCredentialResponse verifyPassword = LockPatternUtilsCompat.verifyPassword(lockPatternUtils, str, i);
                    byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, verifyPassword.getGatekeeperPasswordHandle(), j, i);
                    if (verifyPassword.getResponseCode() == 0) {
                        LockPatternChecker.computeAttempTimes(context, i, true);
                    } else {
                        LockPatternChecker.computeAttempTimes(context, i, false);
                        this.mThrottleTimeout = Math.max(LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i), verifyPassword.getTimeout());
                    }
                    return verifyGatekeeperPasswordHandle;
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> verifyPattern(final LockPatternUtils lockPatternUtils, final List<LockPatternView.Cell> list, final long j, final int i, final Context context, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.settings.LockPatternChecker.1
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    VerifyCredentialResponse verifyPattern = LockPatternUtilsCompat.verifyPattern(lockPatternUtils, list, i);
                    byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, verifyPattern.getGatekeeperPasswordHandle(), j, i);
                    if (verifyPattern.getResponseCode() == 0) {
                        LockPatternChecker.computeAttempTimes(context, i, true);
                    } else {
                        LockPatternChecker.computeAttempTimes(context, i, false);
                        this.mThrottleTimeout = Math.max(LockPatternChecker.computeRetryTimeout(context, lockPatternUtils, i), verifyPattern.getTimeout());
                    }
                    return verifyGatekeeperPasswordHandle;
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> verifyTiedProfileChallenge(final LockPatternUtils lockPatternUtils, final String str, final boolean z, final int i, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.settings.LockPatternChecker.7
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    return LockPatternUtilsCompat.verifyTiedProfileChallenge(lockPatternUtils, str, z, i);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return asyncTask;
    }
}
