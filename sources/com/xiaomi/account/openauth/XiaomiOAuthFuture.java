package com.xiaomi.account.openauth;

import android.accounts.OperationCanceledException;
import java.io.IOException;

/* loaded from: classes2.dex */
public interface XiaomiOAuthFuture<V> {
    V getResult() throws OperationCanceledException, IOException, XMAuthericationException;
}
