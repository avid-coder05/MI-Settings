package com.android.settings;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.CustomListPreference;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.miuisettings.preference.ListPreferenceDialogFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class RestrictedListPreference extends CustomListPreference {
    private final RestrictedPreferenceHelper mHelper;
    private int mProfileUserId;
    private boolean mRequiresActiveUnlockedProfile;
    private final List<RestrictedItem> mRestrictedItems;

    /* loaded from: classes.dex */
    public class RestrictedArrayAdapter extends ArrayAdapter<CharSequence> {
        private final int mSelectedIndex;

        public RestrictedArrayAdapter(Context context, CharSequence[] charSequenceArr, int i) {
            super(context, R.layout.restricted_dialog_singlechoice, R.id.text1, charSequenceArr);
            this.mSelectedIndex = i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            CharSequence item = getItem(i);
            CheckedTextView checkedTextView = (CheckedTextView) view2.findViewById(R.id.text1);
            ImageView imageView = (ImageView) view2.findViewById(R.id.restricted_lock_icon);
            if (RestrictedListPreference.this.isRestrictedForEntry(item)) {
                checkedTextView.setEnabled(false);
                checkedTextView.setChecked(false);
                imageView.setVisibility(0);
            } else {
                int i2 = this.mSelectedIndex;
                if (i2 != -1) {
                    checkedTextView.setChecked(i == i2);
                }
                if (!checkedTextView.isEnabled()) {
                    checkedTextView.setEnabled(true);
                }
                imageView.setVisibility(8);
            }
            return view2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static class RestrictedItem {
        public final RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        public final CharSequence entry;
        public final CharSequence entryValue;

        public RestrictedItem(CharSequence charSequence, CharSequence charSequence2, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
            this.entry = charSequence;
            this.entryValue = charSequence2;
            this.enforcedAdmin = enforcedAdmin;
        }
    }

    /* loaded from: classes.dex */
    public static class RestrictedListPreferenceDialogFragment extends CustomListPreference.CustomListPreferenceDialogFragment {
        private int mLastCheckedPosition = -1;

        /* JADX INFO: Access modifiers changed from: private */
        public RestrictedListPreference getCustomizablePreference() {
            return (RestrictedListPreference) getPreference();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getLastCheckedPosition() {
            if (this.mLastCheckedPosition == -1) {
                this.mLastCheckedPosition = getCustomizablePreference().getSelectedValuePos();
            }
            return this.mLastCheckedPosition;
        }

        public static ListPreferenceDialogFragment newInstance(String str) {
            RestrictedListPreferenceDialogFragment restrictedListPreferenceDialogFragment = new RestrictedListPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString(SettingsProvider.ARGS_KEY, str);
            restrictedListPreferenceDialogFragment.setArguments(bundle);
            return restrictedListPreferenceDialogFragment;
        }

        @Override // com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment
        protected DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() { // from class: com.android.settings.RestrictedListPreference.RestrictedListPreferenceDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    RestrictedListPreference customizablePreference = RestrictedListPreferenceDialogFragment.this.getCustomizablePreference();
                    if (i < 0 || i >= customizablePreference.getEntryValues().length) {
                        return;
                    }
                    RestrictedItem restrictedItemForEntryValue = customizablePreference.getRestrictedItemForEntryValue(customizablePreference.getEntryValues()[i].toString());
                    if (restrictedItemForEntryValue != null) {
                        ((AlertDialog) dialogInterface).getListView().setItemChecked(RestrictedListPreferenceDialogFragment.this.getLastCheckedPosition(), true);
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(RestrictedListPreferenceDialogFragment.this.getContext(), restrictedItemForEntryValue.enforcedAdmin);
                    } else {
                        RestrictedListPreferenceDialogFragment.this.setClickedDialogEntryIndex(i);
                    }
                    if (RestrictedListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        RestrictedListPreferenceDialogFragment.this.onClick(dialogInterface, -1);
                        dialogInterface.dismiss();
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment
        public void setClickedDialogEntryIndex(int i) {
            super.setClickedDialogEntryIndex(i);
            this.mLastCheckedPosition = i;
        }
    }

    public RestrictedListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRestrictedItems = new ArrayList();
        this.mRequiresActiveUnlockedProfile = false;
        setWidgetLayoutResource(R.layout.restricted_icon);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mRestrictedItems = new ArrayList();
        this.mRequiresActiveUnlockedProfile = false;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RestrictedItem getRestrictedItemForEntryValue(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        for (RestrictedItem restrictedItem : this.mRestrictedItems) {
            if (charSequence.equals(restrictedItem.entryValue)) {
                return restrictedItem;
            }
        }
        return null;
    }

    public void addRestrictedItem(RestrictedItem restrictedItem) {
        this.mRestrictedItems.add(restrictedItem);
    }

    public void clearRestrictedItems() {
        this.mRestrictedItems.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListAdapter createListAdapter(Context context) {
        return new RestrictedArrayAdapter(context, getEntries(), getSelectedValuePos());
    }

    public int getSelectedValuePos() {
        String value = getValue();
        if (value == null) {
            return -1;
        }
        return findIndexOfValue(value);
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    public boolean isRestrictedForEntry(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }
        Iterator<RestrictedItem> it = this.mRestrictedItems.iterator();
        while (it.hasNext()) {
            if (charSequence.equals(it.next().entry)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.ListPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R.id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setAdapter(createListAdapter(builder.getContext()), onClickListener);
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mRequiresActiveUnlockedProfile) {
            if (Utils.startQuietModeDialogIfNecessary(getContext(), UserManager.get(getContext()), this.mProfileUserId)) {
                return;
            }
            KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService("keyguard");
            if (keyguardManager.isDeviceLocked(this.mProfileUserId)) {
                getContext().startActivity(keyguardManager.createConfirmDeviceCredentialIntent(null, null, this.mProfileUserId));
                return;
            }
        }
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mHelper.setDisabledByAdmin(enforcedAdmin)) {
            notifyChanged();
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (z && isDisabledByAdmin()) {
            this.mHelper.setDisabledByAdmin(null);
        } else {
            super.setEnabled(z);
        }
    }

    public void setProfileUserId(int i) {
        this.mProfileUserId = i;
    }

    public void setRequiresActiveUnlockedProfile(boolean z) {
        this.mRequiresActiveUnlockedProfile = z;
    }
}
