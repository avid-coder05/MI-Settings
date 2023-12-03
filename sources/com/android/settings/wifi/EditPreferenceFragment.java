package com.android.settings.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class EditPreferenceFragment extends SettingsPreferenceFragment {
    private ActionBar mActionBar;
    private boolean mEditEnabled = true;

    public String getTitle() {
        return "";
    }

    public void onCancel() {
        finish();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setCustomView((View) null);
        }
    }

    public void onEditStateChange(boolean z) {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null && actionBar.getCustomView() != null) {
            this.mActionBar.getCustomView().findViewById(16908314).setEnabled(z);
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
                getActivity().setResult(-1, intent);
            }
        }
        finish();
    }

    public void onSave(boolean z) {
        onSave((Bundle) null);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof AppCompatActivity) {
            this.mActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayOptions(16, 16);
        this.mActionBar.setCustomView(R.layout.miuix_appcompat_edit_mode_title);
        View customView = this.mActionBar.getCustomView();
        ((TextView) customView.findViewById(16908310)).setText(getTitle());
        TextView textView = (TextView) customView.findViewById(16908313);
        textView.setBackgroundResource(R.drawable.action_mode_title_button_cancel);
        textView.setText((CharSequence) null);
        textView.setContentDescription(getText(17039360));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.EditPreferenceFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditPreferenceFragment.this.onCancel();
            }
        });
        TextView textView2 = (TextView) customView.findViewById(16908314);
        textView2.setBackgroundResource(R.drawable.action_mode_title_button_confirm);
        textView2.setText((CharSequence) null);
        textView2.setContentDescription(getText(17039370));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.EditPreferenceFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditPreferenceFragment.this.onSave();
            }
        });
        getActivity().setTitle(getTitle());
        MiuiUtils.onStartEdit(this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        MiuiUtils.onFinishEdit(this);
        super.onStop();
    }
}
