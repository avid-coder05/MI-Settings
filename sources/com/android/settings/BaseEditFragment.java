package com.android.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class BaseEditFragment extends BaseFragment {
    private boolean mEditEnabled = true;

    public String getTitle() {
        return "";
    }

    public boolean isChanged() {
        return false;
    }

    public boolean isEditEabled() {
        return this.mEditEnabled;
    }

    @Override // com.android.settings.BaseFragment, com.android.settings.OnBackPressedListener
    public boolean onBackPressed() {
        if (!isChanged()) {
            if (getAppCompatActivity() != null && (getAppCompatActivity() instanceof MiuiSettings)) {
                ((MiuiSettings) getAppCompatActivity()).onFinishEdit();
            }
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getAppCompatActivity());
        builder.setTitle(R.string.cancel_alert);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.BaseEditFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseEditFragment.this.onCancel();
            }
        });
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        builder.setCancelable(false);
        builder.show();
        return true;
    }

    public void onCancel() {
        finish();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setCustomView((View) null);
        }
    }

    public void onEditStateChange(boolean z) {
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar != null && appCompatActionBar.getCustomView() != null) {
            appCompatActionBar.getCustomView().findViewById(16908314).setEnabled(z);
        }
        this.mEditEnabled = z;
    }

    public void onSave() {
        onSave(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSave(Bundle bundle) {
        if (bundle != null && this.mEditEnabled) {
            SettingsPreferenceFragment settingsPreferenceFragment = (SettingsPreferenceFragment) getTargetFragment();
            if (settingsPreferenceFragment != null) {
                settingsPreferenceFragment.onFragmentResult(getTargetRequestCode(), bundle);
            } else {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                getAppCompatActivity().setResult(-1, intent);
            }
        }
        finish();
    }

    public void onSave(boolean z) {
        onSave((Bundle) null);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        ActionBar appCompatActionBar;
        super.onStart();
        if (getAppCompatActivity() == null || (appCompatActionBar = getAppCompatActivity().getAppCompatActionBar()) == null) {
            return;
        }
        appCompatActionBar.setDisplayOptions(16, 16);
        appCompatActionBar.setCustomView(R.layout.miuix_appcompat_edit_mode_title);
        View customView = appCompatActionBar.getCustomView();
        ((TextView) customView.findViewById(16908310)).setText(getTitle());
        TextView textView = (TextView) customView.findViewById(16908313);
        textView.setBackgroundResource(R.drawable.action_mode_title_button_cancel);
        textView.setText((CharSequence) null);
        textView.setContentDescription(getText(17039360));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.BaseEditFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BaseEditFragment.this.onCancel();
            }
        });
        TextView textView2 = (TextView) customView.findViewById(16908314);
        textView2.setBackgroundResource(R.drawable.action_mode_title_button_confirm);
        textView2.setText((CharSequence) null);
        textView2.setContentDescription(getText(17039370));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.BaseEditFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BaseEditFragment.this.onSave();
            }
        });
        getAppCompatActivity().setTitle(getTitle());
        MiuiUtils.onStartEdit(this);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        MiuiUtils.onFinishEdit(this);
        super.onStop();
    }
}
