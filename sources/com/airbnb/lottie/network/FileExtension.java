package com.airbnb.lottie.network;

import com.iqiyi.android.qigsaw.core.common.SplitConstants;

/* loaded from: classes.dex */
public enum FileExtension {
    JSON(SplitConstants.DOT_JSON),
    ZIP(SplitConstants.DOT_ZIP);

    public final String extension;

    FileExtension(String str) {
        this.extension = str;
    }

    public String tempExtension() {
        return ".temp" + this.extension;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.extension;
    }
}
