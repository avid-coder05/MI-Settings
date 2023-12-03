package com.iqiyi.android.qigsaw.core.common;

import android.content.Context;
import androidx.annotation.Keep;
import java.io.File;
import java.io.InputStream;

@Keep
/* loaded from: classes2.dex */
public interface ICompatBundle {
    boolean disableComponentInfoManager();

    String getMD5(File file);

    String getMD5(InputStream inputStream);

    boolean injectActivityResource();

    Class<?> qigsawConfigClass();

    String readDefaultSplitVersionContent(Context context, String str);
}
