package com.android.settings.display;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.SparseArray;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;

/* loaded from: classes.dex */
public class PageLayoutStatusController extends BaseSettingsController {
    private static SparseArray<Integer> sUiModeOrder;

    static {
        SparseArray<Integer> sparseArray = new SparseArray<>();
        sUiModeOrder = sparseArray;
        sparseArray.put(10, 0);
        sUiModeOrder.put(12, 1);
        sUiModeOrder.put(1, 2);
        sUiModeOrder.put(13, 2);
        sUiModeOrder.put(14, 3);
        sUiModeOrder.put(15, 4);
        sUiModeOrder.put(11, 5);
    }

    public PageLayoutStatusController(Context context, TextView textView) {
        super(context, textView);
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        TextView textView;
        Integer num = null;
        try {
            Configuration configuration = ActivityManagerNative.getDefault().getConfiguration();
            if (configuration != null) {
                num = sUiModeOrder.get(configuration.uiMode & 15);
            }
        } catch (RemoteException unused) {
        }
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.font_size_title);
        if (num == null || (textView = this.mStatusView) == null) {
            return;
        }
        textView.setText(stringArray[num.intValue()]);
    }
}
