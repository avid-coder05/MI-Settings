package com.iqiyi.android.qigsaw.core.splitload;

/* loaded from: classes2.dex */
final class Split {
    final String splitApkPath;
    final String splitName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Split(String str, String str2) {
        this.splitName = str;
        this.splitApkPath = str2;
    }

    public String toString() {
        return "{" + this.splitName + "," + this.splitApkPath + "}";
    }
}
