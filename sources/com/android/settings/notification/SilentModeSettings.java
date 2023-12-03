package com.android.settings.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class SilentModeSettings extends AppCompatActivity {
    private SilentModeAutomationSettings mAutomationRuleSettings;

    private void customAction(ActionBar actionBar) {
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.action_button_edit_rules);
        button.setContentDescription(getString(R.string.edit_rules));
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.useAt(button).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(button, new AnimConfig[0]);
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeSettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SilentModeSettings.this.startActivity(new Intent(SilentModeSettings.this, SilentModeDeleteRuleSettings.class));
            }
        });
        actionBar.setDisplayOptions(16, 16);
        actionBar.setEndView(button);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dndm_activity_with_fragment);
        this.mAutomationRuleSettings = new SilentModeAutomationSettings();
        customAction(getAppCompatActionBar());
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mAutomationRuleSettings).commit();
        }
    }
}
