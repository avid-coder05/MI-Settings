package com.android.settings.backup;

/* loaded from: classes.dex */
public class SystemData {
    public String mFileName;
    public String mFilePath;
    public String mPackageName;
    public int mResourceType;

    public SystemData(String str, String str2, String str3, int i) {
        this.mPackageName = "";
        this.mFileName = "";
        this.mFilePath = "";
        this.mPackageName = str;
        this.mFileName = str2;
        this.mFilePath = str3;
        this.mResourceType = i;
    }
}
