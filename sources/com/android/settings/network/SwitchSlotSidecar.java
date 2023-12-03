package com.android.settings.network;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.AsyncTaskSidecar;
import com.android.settings.SidecarFragment;

/* loaded from: classes.dex */
public class SwitchSlotSidecar extends AsyncTaskSidecar<Param, Result> {
    private Exception mException;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Param {
        int command;
        int slotId;

        Param() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Result {
        Exception exception;

        Result() {
        }
    }

    public static SwitchSlotSidecar get(FragmentManager fragmentManager) {
        return (SwitchSlotSidecar) SidecarFragment.get(fragmentManager, "SwitchSlotSidecar", SwitchSlotSidecar.class, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.AsyncTaskSidecar
    public Result doInBackground(Param param) {
        Result result = new Result();
        if (param == null) {
            result.exception = new UiccSlotsException("Null param");
            return result;
        }
        try {
            if (param.command != 0) {
                Log.e("SwitchSlotSidecar", "Wrong command.");
            } else {
                UiccSlotUtil.switchToRemovableSlot(param.slotId, getContext());
            }
        } catch (UiccSlotsException e) {
            result.exception = e;
        }
        return result;
    }

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.AsyncTaskSidecar
    /* renamed from: onPostExecute  reason: avoid collision after fix types in other method and merged with bridge method [inline-methods] */
    public void lambda$run$0(Result result) {
        Exception exc = result.exception;
        if (exc == null) {
            setState(2, 0);
            return;
        }
        this.mException = exc;
        setState(3, 0);
    }

    public void runSwitchToRemovableSlot(int i) {
        Param param = new Param();
        param.command = 0;
        param.slotId = i;
        super.run(param);
    }
}
