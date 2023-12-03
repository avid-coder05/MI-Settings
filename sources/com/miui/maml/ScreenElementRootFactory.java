package com.miui.maml;

import android.content.Context;
import com.miui.maml.util.ZipResourceLoader;
import java.io.File;
import java.util.Objects;

/* loaded from: classes2.dex */
public class ScreenElementRootFactory {

    /* loaded from: classes2.dex */
    public static class Parameter {
        private Context mContext;
        private String mPath;
        private ResourceLoader mResourceLoader;

        public Parameter(Context context, ResourceLoader resourceLoader) {
            if (context != null) {
                this.mContext = context.getApplicationContext();
            }
            this.mResourceLoader = resourceLoader;
        }

        public Parameter(Context context, String str) {
            if (context != null) {
                this.mContext = context.getApplicationContext();
            }
            this.mPath = str;
        }
    }

    public static ScreenElementRoot create(Parameter parameter) {
        Context context = parameter.mContext;
        Objects.requireNonNull(context);
        ResourceLoader resourceLoader = parameter.mResourceLoader;
        String str = parameter.mPath;
        if (resourceLoader == null && str != null && new File(str).exists()) {
            resourceLoader = new ZipResourceLoader(str).setLocal(context.getResources().getConfiguration().locale);
        }
        ResourceLoader resourceLoader2 = resourceLoader;
        if (resourceLoader2 == null) {
            return null;
        }
        return new ScreenElementRoot(new ScreenContext(context, new LifecycleResourceManager(resourceLoader2, 3600000L, 360000L)));
    }
}
