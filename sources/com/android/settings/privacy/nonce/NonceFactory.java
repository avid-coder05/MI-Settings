package com.android.settings.privacy.nonce;

import java.security.SecureRandom;
import java.util.Random;

/* loaded from: classes2.dex */
public class NonceFactory {
    private static Random RANDOM = new Random(new SecureRandom().nextLong());

    public static String generateNonce() {
        return new Nonce(RANDOM.nextLong(), (int) (System.currentTimeMillis() / 60000)).serialize();
    }
}
