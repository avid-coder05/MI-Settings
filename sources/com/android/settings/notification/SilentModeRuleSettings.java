package com.android.settings.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class SilentModeRuleSettings extends AppCompatActivity {
    private SilentModeAddRuleSettings mAddFragment;
    private SilentModeEditRuleSettings mEditFragment;

    private void customActionBar(ActionBar actionBar, final int i) {
        View customView = actionBar.getCustomView();
        if (i == 2) {
            ((TextView) customView.findViewById(16908310)).setText(getResources().getString(R.string.add_rule));
        } else if (i == 3) {
            ((TextView) customView.findViewById(16908310)).setText(getResources().getString(R.string.edit_rule));
        }
        TextView textView = (TextView) customView.findViewById(16908313);
        textView.setBackgroundResource(R.drawable.action_mode_title_button_cancel);
        textView.setText((CharSequence) null);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeRuleSettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SilentModeRuleSettings.this.finish();
            }
        });
        TextView textView2 = (TextView) customView.findViewById(16908314);
        textView2.setBackgroundResource(R.drawable.action_mode_title_button_confirm);
        textView2.setText((CharSequence) null);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeRuleSettings.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i2 = i;
                if (i2 == 2) {
                    if (SilentModeRuleSettings.this.mAddFragment == null || !SilentModeRuleSettings.this.mAddFragment.commitRule()) {
                        return;
                    }
                    SilentModeRuleSettings.this.finish();
                } else if (i2 == 3 && SilentModeRuleSettings.this.mEditFragment != null && SilentModeRuleSettings.this.mEditFragment.commitRule()) {
                    SilentModeRuleSettings.this.finish();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dndm_activity_with_fragment);
        int intExtra = getIntent().getIntExtra(ExtraTelephony.FirewallLog.MODE, 0);
        customActionBar(getAppCompatActionBar(), intExtra);
        if (bundle != null) {
            if (intExtra == 2) {
                this.mAddFragment = (SilentModeAddRuleSettings) getSupportFragmentManager().getFragment(bundle, "addFragment");
            } else {
                this.mEditFragment = (SilentModeEditRuleSettings) getSupportFragmentManager().getFragment(bundle, "editFragment");
            }
        } else if (intExtra == 2) {
            this.mAddFragment = new SilentModeAddRuleSettings();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mAddFragment).commit();
        } else {
            this.mEditFragment = new SilentModeEditRuleSettings();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mEditFragment).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mAddFragment != null) {
            getSupportFragmentManager().putFragment(bundle, "addFragment", this.mAddFragment);
        } else {
            getSupportFragmentManager().putFragment(bundle, "editFragment", this.mEditFragment);
        }
        super.onSaveInstanceState(bundle);
    }
}
