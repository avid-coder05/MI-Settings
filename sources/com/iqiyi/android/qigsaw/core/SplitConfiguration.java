package com.iqiyi.android.qigsaw.core;

import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter;

/* loaded from: classes2.dex */
public class SplitConfiguration {
    final String[] forbiddenWorkProcesses;
    final SplitInstallReporter installReporter;
    final SplitLoadReporter loadReporter;
    final Class<? extends ObtainUserConfirmationDialog> obtainUserConfirmationDialogClass;
    final int splitLoadMode;
    final SplitUninstallReporter uninstallReporter;
    final SplitUpdateReporter updateReporter;
    final boolean verifySignature;
    final String[] workProcesses;

    /* loaded from: classes2.dex */
    public static class Builder {
        private String[] forbiddenWorkProcesses;
        private SplitInstallReporter installReporter;
        private SplitLoadReporter loadReporter;
        private Class<? extends ObtainUserConfirmationDialog> obtainUserConfirmationDialogClass;
        private int splitLoadMode;
        private SplitUninstallReporter uninstallReporter;
        private SplitUpdateReporter updateReporter;
        private boolean verifySignature;
        private String[] workProcesses;

        private Builder() {
            this.splitLoadMode = 1;
            this.verifySignature = true;
            this.obtainUserConfirmationDialogClass = DefaultObtainUserConfirmationDialog.class;
        }

        public SplitConfiguration build() {
            return new SplitConfiguration(this);
        }

        public Builder forbiddenWorkProcesses(String[] strArr) {
            if (strArr.length > 0) {
                this.forbiddenWorkProcesses = strArr;
            }
            return this;
        }

        public Builder installReporter(SplitInstallReporter splitInstallReporter) {
            this.installReporter = splitInstallReporter;
            return this;
        }

        public Builder loadReporter(SplitLoadReporter splitLoadReporter) {
            this.loadReporter = splitLoadReporter;
            return this;
        }

        public Builder logger(SplitLog.Logger logger) {
            SplitLog.setSplitLogImp(logger);
            return this;
        }

        public Builder obtainUserConfirmationDialogClass(Class<? extends ObtainUserConfirmationDialog> cls) {
            this.obtainUserConfirmationDialogClass = cls;
            return this;
        }

        public Builder splitLoadMode(int i) {
            this.splitLoadMode = i;
            return this;
        }

        public Builder uninstallReporter(SplitUninstallReporter splitUninstallReporter) {
            this.uninstallReporter = splitUninstallReporter;
            return this;
        }

        public Builder updateReporter(SplitUpdateReporter splitUpdateReporter) {
            this.updateReporter = splitUpdateReporter;
            return this;
        }

        public Builder verifySignature(boolean z) {
            this.verifySignature = z;
            return this;
        }

        public Builder workProcesses(String[] strArr) {
            if (strArr.length > 0) {
                this.workProcesses = strArr;
            }
            return this;
        }
    }

    private SplitConfiguration(Builder builder) {
        if (builder.forbiddenWorkProcesses != null && builder.workProcesses != null) {
            throw new RuntimeException("forbiddenWorkProcesses and workProcesses can't be set at the same time, you should choose one of them.");
        }
        this.splitLoadMode = builder.splitLoadMode;
        this.forbiddenWorkProcesses = builder.forbiddenWorkProcesses;
        this.installReporter = builder.installReporter;
        this.loadReporter = builder.loadReporter;
        this.updateReporter = builder.updateReporter;
        this.uninstallReporter = builder.uninstallReporter;
        this.obtainUserConfirmationDialogClass = builder.obtainUserConfirmationDialogClass;
        this.workProcesses = builder.workProcesses;
        this.verifySignature = builder.verifySignature;
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
