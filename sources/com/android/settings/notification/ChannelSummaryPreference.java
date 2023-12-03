package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.TwoTargetPreference;

/* loaded from: classes2.dex */
public class ChannelSummaryPreference extends TwoTargetPreference {
    private CheckBox mCheckBox;
    private boolean mChecked;
    private Context mContext;
    private boolean mEnableCheckBox;
    private Intent mIntent;
    private View.OnClickListener mOnCheckBoxClickListener;

    /* renamed from: com.android.settings.notification.ChannelSummaryPreference$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements View.OnClickListener {
        final /* synthetic */ ChannelSummaryPreference this$0;

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.this$0.mCheckBox == null || this.this$0.mCheckBox.isEnabled()) {
                this.this$0.setChecked(!r2.mChecked);
                ChannelSummaryPreference channelSummaryPreference = this.this$0;
                if (channelSummaryPreference.callChangeListener(Boolean.valueOf(channelSummaryPreference.mChecked))) {
                    ChannelSummaryPreference channelSummaryPreference2 = this.this$0;
                    channelSummaryPreference2.persistBoolean(channelSummaryPreference2.mChecked);
                    return;
                }
                this.this$0.setChecked(!r1.mChecked);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        this.mContext.startActivity(this.mIntent);
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        View findViewById2 = preferenceViewHolder.findViewById(R.id.two_target_divider);
        if (this.mIntent != null) {
            findViewById2.setVisibility(0);
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.ChannelSummaryPreference$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChannelSummaryPreference.this.lambda$onBindViewHolder$0(view);
                }
            });
        } else {
            findViewById2.setVisibility(8);
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(null);
        }
        View findViewById3 = preferenceViewHolder.findViewById(R.id.checkbox_container);
        if (findViewById3 != null) {
            findViewById3.setOnClickListener(this.mOnCheckBoxClickListener);
        }
        CheckBox checkBox = (CheckBox) preferenceViewHolder.findViewById(16908289);
        this.mCheckBox = checkBox;
        if (checkBox != null) {
            checkBox.setChecked(this.mChecked);
            this.mCheckBox.setEnabled(this.mEnableCheckBox);
        }
    }

    @Override // androidx.preference.Preference
    public void onClick() {
        this.mOnCheckBoxClickListener.onClick(null);
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference
    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }
}
