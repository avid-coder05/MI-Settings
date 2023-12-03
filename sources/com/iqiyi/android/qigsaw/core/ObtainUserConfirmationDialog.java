package com.iqiyi.android.qigsaw.core;

import android.app.Activity;
import android.os.Bundle;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitApkInstaller;
import com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class ObtainUserConfirmationDialog extends Activity {
    private SplitInstallSupervisor installService;
    private List<String> moduleNames;
    private long realTotalBytesNeedToDownload;
    private int sessionId;

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkInternParametersIllegal() {
        List<String> list;
        return this.sessionId == 0 || this.realTotalBytesNeedToDownload <= 0 || (list = this.moduleNames) == null || list.isEmpty();
    }

    protected List<String> getModuleNames() {
        return this.moduleNames;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getRealTotalBytesNeedToDownload() {
        return this.realTotalBytesNeedToDownload;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.sessionId = getIntent().getIntExtra("sessionId", 0);
        this.realTotalBytesNeedToDownload = getIntent().getLongExtra("realTotalBytesNeedToDownload", 0L);
        this.moduleNames = getIntent().getStringArrayListExtra("moduleNames");
        this.installService = SplitApkInstaller.getSplitInstallSupervisor();
    }

    protected void onUserCancel() {
        SplitInstallSupervisor splitInstallSupervisor = this.installService;
        if (splitInstallSupervisor != null) {
            if (splitInstallSupervisor.cancelInstallWithoutUserConfirmation(this.sessionId)) {
                setResult(0);
            }
            finish();
        }
    }

    protected void onUserConfirm() {
        SplitInstallSupervisor splitInstallSupervisor = this.installService;
        if (splitInstallSupervisor != null) {
            if (splitInstallSupervisor.continueInstallWithUserConfirmation(this.sessionId)) {
                setResult(-1);
            }
            finish();
        }
    }
}
