package com.miui.maml.util;

import android.text.TextUtils;
import com.miui.maml.ResourceLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class FolderResourceLoader extends ResourceLoader {
    private String mResourcePath;

    public FolderResourceLoader(String str) {
        this.mResourcePath = str;
    }

    @Override // com.miui.maml.ResourceLoader
    public String getID() {
        return "FolderResourceLoader" + this.mResourcePath;
    }

    @Override // com.miui.maml.ResourceLoader
    public InputStream getInputStream(String str, long[] jArr) {
        FileInputStream fileInputStream = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            FileInputStream fileInputStream2 = new FileInputStream(this.mResourcePath + "/" + str);
            if (jArr != null) {
                try {
                    if (jArr.length > 0) {
                        jArr[0] = fileInputStream2.available();
                        return fileInputStream2;
                    }
                    return fileInputStream2;
                } catch (Exception e) {
                    e = e;
                    fileInputStream = fileInputStream2;
                    e.printStackTrace();
                    return fileInputStream;
                }
            }
            return fileInputStream2;
        } catch (Exception e2) {
            e = e2;
        }
    }

    @Override // com.miui.maml.ResourceLoader
    public boolean resourceExists(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return new File(this.mResourcePath + "/" + str).exists();
    }
}
