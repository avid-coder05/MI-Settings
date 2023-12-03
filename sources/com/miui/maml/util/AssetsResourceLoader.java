package com.miui.maml.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class AssetsResourceLoader extends ResourceLoader {
    private Context mContext;
    private String mResourcePath;

    public AssetsResourceLoader(Context context, String str) {
        this.mContext = context.getApplicationContext();
        this.mResourcePath = str;
    }

    @Override // com.miui.maml.ResourceLoader
    public String getID() {
        return "AssetsResourceLoader" + this.mResourcePath;
    }

    @Override // com.miui.maml.ResourceLoader
    public InputStream getInputStream(String str, long[] jArr) {
        InputStream inputStream = null;
        if (!TextUtils.isEmpty(str)) {
            try {
                inputStream = this.mContext.getAssets().open(this.mResourcePath + "/" + str);
                if (jArr != null && jArr.length > 0) {
                    jArr[0] = inputStream.available();
                }
            } catch (IOException unused) {
                Log.d("AssetsResourceLoader", "resource " + str + " do not exists");
            }
        }
        return inputStream;
    }

    @Override // com.miui.maml.ResourceLoader
    public boolean resourceExists(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                InputStream open = this.mContext.getAssets().open(this.mResourcePath + "/" + str);
                if (open != null) {
                    try {
                        open.close();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException unused) {
                Log.d("AssetsResourceLoader", "resource " + str + " do not exists");
            }
        }
        return false;
    }
}
