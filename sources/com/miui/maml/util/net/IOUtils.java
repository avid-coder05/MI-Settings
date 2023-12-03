package com.miui.maml.util.net;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes2.dex */
public final class IOUtils {
    public static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }
}
