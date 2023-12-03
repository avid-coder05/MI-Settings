package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;
import miuix.internal.util.AttributeResolver;

/* loaded from: classes.dex */
public class KeyguardRestrictedListPreference extends CustomListPreference {
    private final KeyguardRestrictedPreferenceHelper mHelper;
    private final List<RestrictedItem> mRestrictedItems;
    protected TextView mValueRight;

    /* loaded from: classes.dex */
    public class RestrictedArrayAdapter extends ArrayAdapter<CharSequence> {
        private final int mSelectedIndex;

        public RestrictedArrayAdapter(Context context, CharSequence[] charSequenceArr, int i) {
            super(context, R.layout.keyguard_restricted_dialog_singlechoice, R.id.text1, charSequenceArr);
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
            View findViewById = view2.findViewById(R.id.restricted_lock_root);
            ImageView imageView = (ImageView) view2.findViewById(R.id.restricted_lock_icon);
            findViewById.setBackground(AttributeResolver.resolveDrawable(getContext(), R.attr.dialogListItemBackground));
            Folme.useAt(view2).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).clearTintColor().setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f).handleTouchOf(view2, new AnimConfig[0]);
            if (KeyguardRestrictedListPreference.this.isRestrictedForEntry(item)) {
                checkedTextView.setEnabled(false);
                checkedTextView.setChecked(false);
                imageView.setVisibility(0);
            } else {
                int i2 = this.mSelectedIndex;
                if (i2 != -1) {
                    checkedTextView.setChecked(i == i2);
                    if (i == this.mSelectedIndex) {
                        findViewById.setBackgroundColor(getContext().getResources().getColor(R.color.restricted_lock_item_select_bg));
                    }
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

    public KeyguardRestrictedListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRestrictedItems = new ArrayList();
        setWidgetLayoutResource(R.layout.value_and_restricted_icon_for_preference);
        this.mHelper = new KeyguardRestrictedPreferenceHelper(context, this, attributeSet);
    }

    public KeyguardRestrictedListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mRestrictedItems = new ArrayList();
        this.mHelper = new KeyguardRestrictedPreferenceHelper(context, this, attributeSet);
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

    protected ListAdapter createListAdapter() {
        return new RestrictedArrayAdapter(getContext(), getEntries(), getSelectedValuePos());
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
        View view = preferenceViewHolder.itemView;
        View findViewById = view.findViewById(R.id.restricted_icon);
        this.mValueRight = (TextView) view.findViewById(R.id.value_right);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        final ListAdapter createListAdapter = createListAdapter();
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.KeyguardRestrictedListPreference.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                for (int i = 0; i < createListAdapter.getCount(); i++) {
                    Folme.clean(createListAdapter.getView(i, null, null));
                }
            }
        });
        builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.screen_timeout_title, (ViewGroup) null, false));
        builder.setAdapter(createListAdapter, new DialogInterface.OnClickListener() { // from class: com.android.settings.KeyguardRestrictedListPreference.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < 0 || i >= KeyguardRestrictedListPreference.this.getEntries().length) {
                    return;
                }
                String charSequence = KeyguardRestrictedListPreference.this.getEntryValues()[i].toString();
                RestrictedItem restrictedItemForEntryValue = KeyguardRestrictedListPreference.this.getRestrictedItemForEntryValue(charSequence);
                if (restrictedItemForEntryValue != null) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(KeyguardRestrictedListPreference.this.getContext(), restrictedItemForEntryValue.enforcedAdmin);
                } else if (KeyguardRestrictedListPreference.this.callChangeListener(charSequence) && !charSequence.equals(KeyguardRestrictedListPreference.this.getValue())) {
                    KeyguardRestrictedListPreference.this.setValue(charSequence);
                }
                dialogInterface.dismiss();
            }
        });
    }

    @Override // androidx.preference.Preference
    public void performClick() {
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
}
