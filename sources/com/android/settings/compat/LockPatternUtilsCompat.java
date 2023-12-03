package com.android.settings.compat;

import android.text.TextUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.VerifyCredentialResponse;
import java.util.List;

/* loaded from: classes.dex */
public class LockPatternUtilsCompat {
    public static boolean checkPassword(LockPatternUtils lockPatternUtils, String str, int i) throws LockPatternUtils.RequestThrottledException {
        return lockPatternUtils.checkCredential(getCredential(lockPatternUtils, str, i), i, (LockPatternUtils.CheckCredentialProgressCallback) null);
    }

    public static boolean checkPasswordHistory(LockPatternUtils lockPatternUtils, String str, byte[] bArr, int i) {
        return lockPatternUtils.checkPasswordHistory(str.getBytes(), bArr, i);
    }

    public static boolean checkPattern(LockPatternUtils lockPatternUtils, List<LockPatternView.Cell> list, int i) throws LockPatternUtils.RequestThrottledException {
        return lockPatternUtils.checkCredential(LockscreenCredential.createPattern(list), i, (LockPatternUtils.CheckCredentialProgressCallback) null);
    }

    public static void clearLock(LockPatternUtils lockPatternUtils, int i, boolean z, String str) {
        lockPatternUtils.setLockCredential(LockscreenCredential.createNone(), getCredential(lockPatternUtils, str, i), i);
    }

    private static LockscreenCredential getCredential(LockPatternUtils lockPatternUtils, String str, int i) {
        return TextUtils.isEmpty(str) ? LockscreenCredential.createNone() : lockPatternUtils.isLockPasswordEnabled(i) ? LockPatternUtils.isQualityAlphabeticPassword(lockPatternUtils.getKeyguardStoredPasswordQuality(i)) ? LockscreenCredential.createPassword(str) : LockscreenCredential.createPin(str) : lockPatternUtils.isLockPatternEnabled(i) ? LockscreenCredential.createPattern(LockPatternUtils.byteArrayToPattern(str.getBytes())) : LockscreenCredential.createPassword(str);
    }

    public static byte[] getPasswordHistoryHashFactor(LockPatternUtils lockPatternUtils, String str, int i) {
        return lockPatternUtils.getPasswordHistoryHashFactor(getCredential(lockPatternUtils, str, i), i);
    }

    public static String patternToString(LockPatternUtils lockPatternUtils, List<LockPatternView.Cell> list) {
        return new String(LockPatternUtils.patternToByteArray(list));
    }

    public static void removeGatekeeperPasswordHandle(LockPatternUtils lockPatternUtils, long j) {
        lockPatternUtils.removeGatekeeperPasswordHandle(j);
    }

    public static void saveLockPassword(LockPatternUtils lockPatternUtils, String str, boolean z, String str2, int i, int i2) {
        lockPatternUtils.setLockCredential(z ? LockscreenCredential.createPin(str) : LockscreenCredential.createPassword(str), getCredential(lockPatternUtils, str2, i2), i2);
    }

    public static void saveLockPattern(LockPatternUtils lockPatternUtils, List<LockPatternView.Cell> list, String str, int i, boolean z) {
        lockPatternUtils.setLockCredential(LockscreenCredential.createPattern(list), getCredential(lockPatternUtils, str, i), i);
    }

    public static void setSeparateProfileChallengeEnabled(LockPatternUtils lockPatternUtils, int i, boolean z, String str) {
        lockPatternUtils.setSeparateProfileChallengeEnabled(i, z, getCredential(lockPatternUtils, str, i));
    }

    public static List<LockPatternView.Cell> stringToPattern(LockPatternUtils lockPatternUtils, String str) {
        return LockPatternUtils.byteArrayToPattern(str.getBytes());
    }

    public static byte[] verifyGatekeeperPasswordHandle(LockPatternUtils lockPatternUtils, long j, long j2, int i) {
        return lockPatternUtils.verifyGatekeeperPasswordHandle(j, j2, i).getGatekeeperHAT();
    }

    public static VerifyCredentialResponse verifyPassword(LockPatternUtils lockPatternUtils, String str, int i) throws LockPatternUtils.RequestThrottledException {
        return lockPatternUtils.verifyCredential(getCredential(lockPatternUtils, str, i), i, 1);
    }

    public static VerifyCredentialResponse verifyPattern(LockPatternUtils lockPatternUtils, List<LockPatternView.Cell> list, int i) throws LockPatternUtils.RequestThrottledException {
        return lockPatternUtils.verifyCredential(LockscreenCredential.createPattern(list), i, 1);
    }

    public static byte[] verifyTiedProfileChallenge(LockPatternUtils lockPatternUtils, String str, boolean z, int i) throws LockPatternUtils.RequestThrottledException {
        return lockPatternUtils.verifyTiedProfileChallenge(getCredential(lockPatternUtils, str, i), i, 0).getGatekeeperHAT();
    }
}
