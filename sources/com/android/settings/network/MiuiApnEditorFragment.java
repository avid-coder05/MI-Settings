package com.android.settings.network;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.R;
import com.android.settings.network.apn.ApnEditor;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiApnEditorFragment extends ApnEditor {
    private boolean checkToSave() {
        if (noModifyApn()) {
            return true;
        }
        new AlertDialog.Builder(getActivity()).setTitle(R.string.apn_save).setMessage(R.string.apn_save_msg).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.MiuiApnEditorFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MiuiApnEditorFragment.this.validateAndSaveApnData()) {
                    MiuiApnEditorFragment.this.finish();
                }
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.MiuiApnEditorFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiApnEditorFragment.this.finish();
            }
        }).show();
        return false;
    }

    @Override // com.android.settings.network.apn.ApnEditor, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String action = getIntent().getAction();
        if (action.equals("android.intent.action.INSERT")) {
            getActivity().setTitle(R.string.apn_new);
        } else if (action.equals("android.intent.action.EDIT")) {
            getActivity().setTitle(R.string.apn_edit);
        }
    }

    @Override // com.android.settings.network.apn.ApnEditor, android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 4) {
            if (checkToSave()) {
                finish();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.network.apn.ApnEditor, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (checkToSave()) {
            finish();
            return true;
        }
        return true;
    }
}
