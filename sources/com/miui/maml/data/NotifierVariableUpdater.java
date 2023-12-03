package com.miui.maml.data;

import com.miui.maml.NotifierManager;

/* loaded from: classes2.dex */
public abstract class NotifierVariableUpdater extends VariableUpdater implements NotifierManager.OnNotifyListener {
    protected NotifierManager mNotifierManager;
    private String mType;

    public NotifierVariableUpdater(VariableUpdaterManager variableUpdaterManager, String str) {
        super(variableUpdaterManager);
        this.mType = str;
        this.mNotifierManager = NotifierManager.getInstance(getContext().mContext);
    }

    @Override // com.miui.maml.data.VariableUpdater
    public void finish() {
        this.mNotifierManager.releaseNotifier(this.mType, this);
    }

    @Override // com.miui.maml.data.VariableUpdater
    public void init() {
        this.mNotifierManager.acquireNotifier(this.mType, this);
    }

    @Override // com.miui.maml.data.VariableUpdater
    public void pause() {
        this.mNotifierManager.pause(this.mType, this);
    }

    @Override // com.miui.maml.data.VariableUpdater
    public void resume() {
        this.mNotifierManager.resume(this.mType, this);
    }
}
