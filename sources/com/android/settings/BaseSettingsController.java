package com.android.settings;

import android.content.Context;
import android.widget.TextView;

/* loaded from: classes.dex */
public abstract class BaseSettingsController {
    protected Context mContext;
    protected TextView mStatusView;
    protected UpdateCallback mUpdateCallback = null;

    /* loaded from: classes.dex */
    public static class UpdateCallback {
        public void updateText(String str) {
            throw null;
        }
    }

    public BaseSettingsController(Context context, TextView textView) {
        this.mContext = context;
        this.mStatusView = textView;
    }

    public abstract void pause();

    public abstract void resume();

    public void setStatusView(TextView textView) {
        BaseSettingsController baseSettingsController;
        if (textView != null && this.mStatusView != textView && (baseSettingsController = (BaseSettingsController) textView.getTag()) != null) {
            baseSettingsController.setStatusView(null);
        }
        this.mStatusView = textView;
        updateStatus();
    }

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.mUpdateCallback = updateCallback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stop() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void updateStatus();
}
