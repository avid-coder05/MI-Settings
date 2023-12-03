package com.android.settings.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class SilentModeDeleteRuleSettings extends AppCompatActivity {
    private SilentModeDeleteSettings mDeleteRuleSettings;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dndm_activity_with_fragment);
        this.mDeleteRuleSettings = new SilentModeDeleteSettings();
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar == null) {
            return;
        }
        View customView = appCompatActionBar.getCustomView();
        ((TextView) customView.findViewById(16908310)).setText(getResources().getString(R.string.delete_rule));
        TextView textView = (TextView) customView.findViewById(16908313);
        textView.setBackgroundResource(R.drawable.action_mode_title_button_cancel);
        textView.setText((CharSequence) null);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeDeleteRuleSettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SilentModeDeleteRuleSettings.this.finish();
            }
        });
        TextView textView2 = (TextView) customView.findViewById(16908314);
        textView2.setBackgroundResource(R.drawable.action_mode_title_button_confirm);
        textView2.setText((CharSequence) null);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeDeleteRuleSettings.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SilentModeDeleteRuleSettings.this.mDeleteRuleSettings.commitRules();
                SilentModeDeleteRuleSettings.this.finish();
            }
        });
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mDeleteRuleSettings).commit();
        }
    }
}
