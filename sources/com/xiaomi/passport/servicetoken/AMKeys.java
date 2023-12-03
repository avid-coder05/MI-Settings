package com.xiaomi.passport.servicetoken;

import miui.accounts.ExtraAccountManager;

/* loaded from: classes2.dex */
public final class AMKeys {
    public String getAmUserDataKeyCUserId() {
        return ExtraAccountManager.KEY_ENCRYPTED_USER_ID;
    }

    public String getAmUserDataKeyPh(String str) {
        return str + "_ph";
    }

    public String getAmUserDataKeySlh(String str) {
        return str + "_slh";
    }

    public String getType() {
        return "com.xiaomi";
    }
}
