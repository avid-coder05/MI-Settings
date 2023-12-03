package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import com.miui.maml.NotifierManager;

/* loaded from: classes2.dex */
public class DarkModeVariableUpdater extends NotifierVariableUpdater {
    private IndexedVariable mDarkMode;
    private IndexedVariable mDarkWallpaperMode;

    public DarkModeVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        super(variableUpdaterManager, NotifierManager.TYPE_DARK_MODE);
        this.mDarkMode = new IndexedVariable("__darkmode", getRoot().getContext().mVariables, true);
        this.mDarkWallpaperMode = new IndexedVariable("__darkmode_wallpaper", getRoot().getContext().mVariables, true);
    }

    @Override // com.miui.maml.NotifierManager.OnNotifyListener
    public void onNotify(Context context, Intent intent, Object obj) {
        if (obj instanceof Integer) {
            int intValue = ((Integer) obj).intValue();
            if ((intValue & 1) == 1) {
                this.mDarkMode.set(Boolean.TRUE);
            } else {
                this.mDarkMode.set(Boolean.FALSE);
            }
            if ((intValue & 3) == 3) {
                this.mDarkWallpaperMode.set(Boolean.TRUE);
                getRoot().setDarkWallpaperMode(true);
            } else {
                this.mDarkWallpaperMode.set(Boolean.FALSE);
                getRoot().setDarkWallpaperMode(false);
            }
            getRoot().requestUpdate();
        }
    }
}
