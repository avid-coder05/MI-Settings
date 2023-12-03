package com.miui.maml.elements.filament;

import android.util.Log;
import com.google.android.filament.Engine;
import com.google.android.filament.Filament;

/* loaded from: classes2.dex */
public class FilamentManager {
    private Engine mEngine;
    private boolean mInited;
    private boolean mLoaded;
    private final Object mLock;
    private int mRef;

    /* loaded from: classes2.dex */
    private static class FilamentManagerHolder {
        public static final FilamentManager INSTANCE = new FilamentManager();
    }

    private FilamentManager() {
        this.mLock = new Object();
        this.mRef = 0;
    }

    public static FilamentManager getInstance() {
        return FilamentManagerHolder.INSTANCE;
    }

    public Engine acquireEngine() {
        Engine engine;
        synchronized (this.mLock) {
            if (this.mInited) {
                if (this.mEngine == null) {
                    this.mEngine = Engine.create();
                }
                this.mRef++;
                Log.d("FilamentManager", "acquireEngine, ref " + this.mRef);
            }
            engine = this.mEngine;
        }
        return engine;
    }

    public void load() {
        synchronized (this.mLock) {
            if (!this.mInited) {
                Filament.init();
                this.mInited = true;
            }
        }
    }

    public void loadAll() {
        if (!this.mInited) {
            Filament.init();
            this.mInited = true;
        }
        if (this.mLoaded) {
            return;
        }
        System.loadLibrary("filament-utils-jni");
        this.mLoaded = true;
    }

    public void releaseEngine() {
        synchronized (this.mLock) {
            if (this.mInited) {
                if (this.mEngine == null) {
                    this.mEngine = Engine.create();
                }
                this.mRef--;
                Log.d("FilamentManager", "releaseEngine, ref " + this.mRef);
                if (this.mRef <= 0) {
                    this.mEngine.destroy();
                    this.mEngine = null;
                    Log.d("FilamentManager", "Engine destroyed");
                }
            }
        }
    }
}
