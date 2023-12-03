package com.android.settings.deviceinfo;

import android.content.Context;
import miuix.util.Log;

/* loaded from: classes.dex */
public class FiveGController {
    private static final String TAG = "FiveGController";
    private static FiveGController sFiveGController;

    private FiveGController(Context context) {
    }

    public static FiveGController getInstance(Context context) {
        Log.i(TAG, "FiveGController getInstance");
        if (sFiveGController == null) {
            sFiveGController = new FiveGController(context);
        }
        return sFiveGController;
    }

    public boolean isFiveGConnect(int i, int i2) {
        return false;
    }

    public void pause() {
    }

    public void resume() {
    }
}
