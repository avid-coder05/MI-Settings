package com.android.settings.notification;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.android.settings.R;
import com.android.settings.RestrictedListPreference;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class NotificationLockscreenPreference extends RestrictedListPreference {
    private RestrictedLockUtils.EnforcedAdmin mAdminRestrictingRemoteInput;
    private boolean mAllowRemoteInput;
    private Listener mListener;
    private boolean mRemoteInputCheckBoxEnabled;
    private boolean mShowRemoteInput;
    private int mUserId;

    /* loaded from: classes2.dex */
    private class Listener implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private final DialogInterface.OnClickListener mInner;
        private View mView;

        public Listener(DialogInterface.OnClickListener onClickListener) {
            this.mInner = onClickListener;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            NotificationLockscreenPreference.this.mAllowRemoteInput = !z;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mInner.onClick(dialogInterface, i);
            int checkedItemPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
            View view = this.mView;
            if (view != null) {
                NotificationLockscreenPreference notificationLockscreenPreference = NotificationLockscreenPreference.this;
                view.setVisibility(notificationLockscreenPreference.checkboxVisibilityForSelectedIndex(checkedItemPosition, notificationLockscreenPreference.mShowRemoteInput));
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view.getId() == 16908909) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(NotificationLockscreenPreference.this.getContext(), NotificationLockscreenPreference.this.mAdminRestrictingRemoteInput);
            }
        }

        public void setView(View view) {
            this.mView = view;
        }
    }

    public NotificationLockscreenPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRemoteInputCheckBoxEnabled = true;
        this.mUserId = UserHandle.myUserId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int checkboxVisibilityForSelectedIndex(int i, boolean z) {
        return (i == 1 && z && this.mRemoteInputCheckBoxEnabled) ? 0 : 8;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public boolean isAutoClosePreference() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        Context context = getContext();
        if (Utils.startQuietModeDialogIfNecessary(context, UserManager.get(context), this.mUserId)) {
            return;
        }
        super.onClick();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        Settings.Secure.putInt(getContext().getContentResolver(), "lock_screen_allow_remote_input", this.mAllowRemoteInput ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onDialogCreated(Dialog dialog) {
        super.onDialogCreated(dialog);
        dialog.create();
        CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.lockscreen_remote_input);
        checkBox.setChecked(!this.mAllowRemoteInput);
        checkBox.setOnCheckedChangeListener(this.mListener);
        checkBox.setEnabled(this.mAdminRestrictingRemoteInput == null);
        dialog.findViewById(R.id.restricted_lock_icon_remote_input).setVisibility(this.mAdminRestrictingRemoteInput == null ? 8 : 0);
        if (this.mAdminRestrictingRemoteInput != null) {
            checkBox.setClickable(false);
            dialog.findViewById(16908909).setOnClickListener(this.mListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onDialogStateRestored(Dialog dialog, Bundle bundle) {
        super.onDialogStateRestored(dialog, bundle);
        int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
        View findViewById = dialog.findViewById(16908909);
        findViewById.setVisibility(checkboxVisibilityForSelectedIndex(checkedItemPosition, this.mShowRemoteInput));
        this.mListener.setView(findViewById);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.RestrictedListPreference, com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        this.mListener = new Listener(onClickListener);
        builder.setSingleChoiceItems(createListAdapter(builder.getContext()), getSelectedValuePos(), this.mListener);
        this.mShowRemoteInput = getEntryValues().length == 3;
        this.mAllowRemoteInput = Settings.Secure.getInt(getContext().getContentResolver(), "lock_screen_allow_remote_input", 0) != 0;
        builder.setView(R.layout.lockscreen_remote_input);
    }
}
