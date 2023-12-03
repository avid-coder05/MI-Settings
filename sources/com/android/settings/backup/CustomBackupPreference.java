package com.android.settings.backup;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.MiuiSettings;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class CustomBackupPreference extends Preference {
    public CustomBackupPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.enable_local_backup_widget_lyt);
    }

    public CustomBackupPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWidgetLayoutResource(R.layout.enable_local_backup_widget_lyt);
    }

    public CustomBackupPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setWidgetLayoutResource(R.layout.enable_local_backup_widget_lyt);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.findViewById(16908312).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.backup.CustomBackupPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AlertDialog.Builder(CustomBackupPreference.this.getContext()).setTitle(R.string.enable_local_backup_title).setMessage(Build.IS_INTERNATIONAL_BUILD ? R.string.enable_local_backup_message_intledition : R.string.enable_local_backup_message).setNegativeButton(R.string.enable_local_backup_cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.enable_local_backup_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.backup.CustomBackupPreference.1.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MiuiSettings.System.putBoolean(CustomBackupPreference.this.getContext().getContentResolver(), "local_backup_disable_service", false);
                    }
                }).show();
            }
        });
        preferenceViewHolder.itemView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.backup.CustomBackupPreference.2
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setClickable(false);
                accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
        });
    }
}
