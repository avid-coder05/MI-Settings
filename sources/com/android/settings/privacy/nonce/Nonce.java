package com.android.settings.privacy.nonce;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes2.dex */
public class Nonce {
    public final int minute;
    public final long random;

    public Nonce(long j, int i) {
        this.random = j;
        this.minute = i;
    }

    public String serialize() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeLong(this.random);
            dataOutputStream.writeInt(this.minute);
            dataOutputStream.flush();
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
        } catch (IOException e) {
            throw new RuntimeException("", e);
        }
    }
}
