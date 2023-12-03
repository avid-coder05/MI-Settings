package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes.dex */
public class AodNotificationListPreference extends KeyguardRestrictedListPreference {
    private Context mContext;

    public AodNotificationListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    private int convertNotificationStatusToPrefIndex(int i) {
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.keyguard_notification_status_values);
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            if (i == Integer.valueOf(stringArray[i2]).intValue()) {
                return i2;
            }
        }
        return 0;
    }

    @Override // com.android.settings.KeyguardRestrictedListPreference, com.android.settingslib.miuisettings.preference.ListPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = this.mValueRight;
        if (textView != null) {
            textView.setVisibility(0);
            updatePreferenceSummary();
        }
    }

    public void updatePreferenceSummary() {
        Context context = this.mContext;
        int convertNotificationStatusToPrefIndex = convertNotificationStatusToPrefIndex(MiuiKeyguardSettingsUtils.getKeyguardNotificationStatus(context, context.getContentResolver()));
        if (convertNotificationStatusToPrefIndex >= getEntries().length || convertNotificationStatusToPrefIndex < 0) {
            return;
        }
        setValueIndex(convertNotificationStatusToPrefIndex);
        String[] stringArray = getContext().getResources().getStringArray(R.array.keyguard_notification_status_entries);
        TextView textView = this.mValueRight;
        if (textView != null) {
            textView.setText(stringArray[convertNotificationStatusToPrefIndex]);
        }
    }
}
