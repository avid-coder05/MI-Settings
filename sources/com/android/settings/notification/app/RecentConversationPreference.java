package com.android.settings.notification.app;

import android.content.Context;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.widget.TwoTargetPreference;

/* loaded from: classes2.dex */
public class RecentConversationPreference extends TwoTargetPreference {
    private View mClearView;
    private OnClearClickListener mOnClearClickListener;

    /* loaded from: classes2.dex */
    public interface OnClearClickListener {
        void onClear();
    }

    public RecentConversationPreference(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        OnClearClickListener onClearClickListener = this.mOnClearClickListener;
        if (onClearClickListener != null) {
            onClearClickListener.onClear();
        }
    }

    int getClearId() {
        return R.id.clear_button;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getClearView() {
        return this.mClearView;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.preference_widget_clear;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasClearListener() {
        return this.mOnClearClickListener != null;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(16908312).setVisibility(this.mOnClearClickListener != null ? 0 : 8);
        View findViewById = preferenceViewHolder.findViewById(getClearId());
        this.mClearView = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.app.RecentConversationPreference$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentConversationPreference.this.lambda$onBindViewHolder$0(view);
            }
        });
    }

    public void setOnClearClickListener(OnClearClickListener onClearClickListener) {
        this.mOnClearClickListener = onClearClickListener;
    }
}
