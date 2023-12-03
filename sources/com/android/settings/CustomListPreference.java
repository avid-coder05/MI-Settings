package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.miuisettings.preference.ListPreference;
import com.android.settingslib.miuisettings.preference.ListPreferenceDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class CustomListPreference extends ListPreference {
    private CustomListPreferenceDialogFragment mFragment;

    /* loaded from: classes.dex */
    public static class ConfirmDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 529;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setMessage(getArguments().getCharSequence("android.intent.extra.TEXT")).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.ConfirmDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Fragment targetFragment = ConfirmDialogFragment.this.getTargetFragment();
                    if (targetFragment != null) {
                        ((CustomListPreferenceDialogFragment) targetFragment).onItemConfirmed();
                    }
                }
            }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        }
    }

    /* loaded from: classes.dex */
    public static class CustomListPreferenceDialogFragment extends ListPreferenceDialogFragment {
        private int mClickedDialogEntryIndex;

        /* JADX INFO: Access modifiers changed from: private */
        public CustomListPreference getCustomizablePreference() {
            return (CustomListPreference) getPreference();
        }

        private String getValue() {
            CustomListPreference customizablePreference = getCustomizablePreference();
            if (getCustomizablePreference() == null || this.mClickedDialogEntryIndex < 0 || customizablePreference.getEntryValues() == null) {
                return null;
            }
            return customizablePreference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
        }

        public static ListPreferenceDialogFragment newInstance(String str) {
            CustomListPreferenceDialogFragment customListPreferenceDialogFragment = new CustomListPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString(SettingsProvider.ARGS_KEY, str);
            customListPreferenceDialogFragment.setArguments(bundle);
            return customListPreferenceDialogFragment;
        }

        protected DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(i);
                    if (CustomListPreferenceDialogFragment.this.getCustomizablePreference() != null && CustomListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        CustomListPreferenceDialogFragment.this.onItemChosen();
                    }
                }
            };
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            if (getCustomizablePreference() == null) {
                return;
            }
            getCustomizablePreference().onDialogStateRestored(getDialog(), bundle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settingslib.miuisettings.preference.PreferenceDialogFragment
        public void onBindDialogView(View view) {
            super.onBindDialogView(view);
            if (getCustomizablePreference() == null) {
                return;
            }
            getCustomizablePreference().onBindDialogView(view);
        }

        @Override // com.android.settingslib.miuisettings.preference.PreferenceDialogFragment, androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            if (bundle != null) {
                this.mClickedDialogEntryIndex = bundle.getInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
            }
            if (getCustomizablePreference() != null) {
                getCustomizablePreference().onDialogCreated(onCreateDialog);
            }
            return onCreateDialog;
        }

        @Override // com.android.settingslib.miuisettings.preference.ListPreferenceDialogFragment, com.android.settingslib.miuisettings.preference.PreferenceDialogFragment
        public void onDialogClosed(boolean z) {
            if (getCustomizablePreference() == null) {
                return;
            }
            getCustomizablePreference().onDialogClosed(z);
            CustomListPreference customizablePreference = getCustomizablePreference();
            String value = getValue();
            if (z && value != null && customizablePreference.callChangeListener(value)) {
                customizablePreference.setValue(value);
            }
        }

        protected void onItemChosen() {
            if (getCustomizablePreference() == null) {
                return;
            }
            CharSequence confirmationMessage = getCustomizablePreference().getConfirmationMessage(getValue());
            if (confirmationMessage == null) {
                onItemConfirmed();
                return;
            }
            ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("android.intent.extra.TEXT", confirmationMessage);
            confirmDialogFragment.setArguments(bundle);
            confirmDialogFragment.setTargetFragment(this, 0);
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            beginTransaction.add(confirmDialogFragment, getTag() + "-Confirm");
            beginTransaction.commitAllowingStateLoss();
        }

        protected void onItemConfirmed() {
            onClick(getDialog(), -1);
            getDialog().dismiss();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settingslib.miuisettings.preference.ListPreferenceDialogFragment, com.android.settingslib.miuisettings.preference.PreferenceDialogFragment
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            if (getCustomizablePreference() == null) {
                return;
            }
            this.mClickedDialogEntryIndex = getCustomizablePreference().findIndexOfValue(getCustomizablePreference().getValue());
            getCustomizablePreference().setFragment(this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, getOnItemClickListener());
            if (getCustomizablePreference().isAutoClosePreference()) {
                return;
            }
            builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomListPreferenceDialogFragment.this.onItemChosen();
                }
            });
        }

        @Override // com.android.settingslib.miuisettings.preference.ListPreferenceDialogFragment, com.android.settingslib.miuisettings.preference.PreferenceDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void setClickedDialogEntryIndex(int i) {
            this.mClickedDialogEntryIndex = i;
        }
    }

    public CustomListPreference(Context context) {
        super(context);
    }

    public CustomListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFragment(CustomListPreferenceDialogFragment customListPreferenceDialogFragment) {
        this.mFragment = customListPreferenceDialogFragment;
    }

    protected CharSequence getConfirmationMessage(String str) {
        return null;
    }

    public Dialog getDialog() {
        CustomListPreferenceDialogFragment customListPreferenceDialogFragment = this.mFragment;
        if (customListPreferenceDialogFragment != null) {
            return customListPreferenceDialogFragment.getDialog();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAutoClosePreference() {
        return true;
    }

    protected void onBindDialogView(View view) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogCreated(Dialog dialog) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogStateRestored(Dialog dialog, Bundle bundle) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        onPrepareDialogBuilder(builder);
    }
}
