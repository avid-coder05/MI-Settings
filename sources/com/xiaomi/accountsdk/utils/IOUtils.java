package com.xiaomi.accountsdk.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

/* loaded from: classes2.dex */
public final class IOUtils {
    public static void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException unused) {
            }
            try {
                outputStream.close();
            } catch (IOException unused2) {
            }
        }
    }

    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }
}
