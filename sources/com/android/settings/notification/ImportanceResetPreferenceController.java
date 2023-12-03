package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class ImportanceResetPreferenceController extends BasePreferenceController implements View.OnClickListener {
    public static final String KEY = "asst_importance_reset";
    private static final String TAG = "ResetImportanceButton";
    private NotificationBackend mBackend;
    private TextView mButton;

    public ImportanceResetPreferenceController(Context context, String str) {
        super(context, str);
        this.mBackend = new NotificationBackend();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.mBackend.resetNotificationImportance();
        Toast.makeText(this.mContext, R.string.reset_importance_completed, 0).show();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        TextView textView = (TextView) ((LayoutPreference) preference).findViewById(R.id.reset_importance_button);
        this.mButton = textView;
        textView.setOnClickListener(this);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
