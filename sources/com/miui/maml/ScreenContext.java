package com.miui.maml;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.miui.maml.data.ContextVariables;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ScreenElementFactory;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class ScreenContext {
    public final Context mContext;
    public final ContextVariables mContextVariables;
    public final ScreenElementFactory mFactory;
    private final Handler mHandler;
    private HashMap<String, ObjectFactory> mObjectFactories;
    public final ResourceManager mResourceManager;
    public final Variables mVariables;

    public ScreenContext(Context context, ResourceManager resourceManager) {
        this(context, resourceManager, new ScreenElementFactory());
    }

    public ScreenContext(Context context, ResourceManager resourceManager, ScreenElementFactory screenElementFactory) {
        this(context, resourceManager, screenElementFactory, new Variables());
    }

    public ScreenContext(Context context, ResourceManager resourceManager, ScreenElementFactory screenElementFactory, Variables variables) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext != null ? applicationContext : context;
        this.mResourceManager = resourceManager;
        this.mFactory = screenElementFactory;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mVariables = variables;
        this.mContextVariables = new ContextVariables();
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v3, types: [com.miui.maml.ObjectFactory] */
    public final synchronized <T extends ObjectFactory> T getObjectFactory(String str) {
        T t;
        t = null;
        try {
            HashMap<String, ObjectFactory> hashMap = this.mObjectFactories;
            if (hashMap != null) {
                t = hashMap.get(str);
            }
        } catch (ClassCastException unused) {
            return null;
        }
        return t;
    }

    public boolean postDelayed(Runnable runnable, long j) {
        return this.mHandler.postDelayed(runnable, j);
    }

    public void removeCallbacks(Runnable runnable) {
        this.mHandler.removeCallbacks(runnable);
    }
}
