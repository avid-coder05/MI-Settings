package com.miui.maml.data;

import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;

/* loaded from: classes2.dex */
public class VariableUpdater {
    private VariableUpdaterManager mVariableUpdaterManager;

    public VariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        this.mVariableUpdaterManager = variableUpdaterManager;
    }

    public void finish() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ScreenContext getContext() {
        return getRoot().getContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ScreenElementRoot getRoot() {
        return this.mVariableUpdaterManager.getRoot();
    }

    public void init() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void tick(long j) {
    }
}
