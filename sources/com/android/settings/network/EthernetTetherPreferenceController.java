package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.network.EthernetTetherPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public final class EthernetTetherPreferenceController extends TetherBasePreferenceController {
    @VisibleForTesting
    EthernetManager.Listener mEthernetListener;
    private final EthernetManager mEthernetManager;
    private final String mEthernetRegex;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.network.EthernetTetherPreferenceController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements EthernetManager.Listener {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAvailabilityChanged$0() {
            EthernetTetherPreferenceController ethernetTetherPreferenceController = EthernetTetherPreferenceController.this;
            ethernetTetherPreferenceController.updateState(ethernetTetherPreferenceController.mPreference);
        }

        public void onAvailabilityChanged(String str, boolean z) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.settings.network.EthernetTetherPreferenceController$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EthernetTetherPreferenceController.AnonymousClass1.this.lambda$onAvailabilityChanged$0();
                }
            });
        }
    }

    public EthernetTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mEthernetRegex = context.getString(17039958);
        this.mEthernetManager = (EthernetManager) context.getSystemService("ethernet");
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 5;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AnonymousClass1 anonymousClass1 = new AnonymousClass1();
        this.mEthernetListener = anonymousClass1;
        this.mEthernetManager.addListener(anonymousClass1);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mEthernetManager.removeListener(this.mEthernetListener);
        this.mEthernetListener = null;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        for (String str : this.mTm.getTetherableIfaces()) {
            if (str.matches(this.mEthernetRegex)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        return !TextUtils.isEmpty(this.mEthernetRegex);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
