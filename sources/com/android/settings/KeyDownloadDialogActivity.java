package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.settings.KeyDownloadDialog;
import com.android.settings.search.tree.GestureSettingsTree;

/* loaded from: classes.dex */
public class KeyDownloadDialogActivity extends Activity {
    private void initView() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            finish();
            return;
        }
        final String string = extras.getString("packageName");
        String string2 = extras.getString("title");
        String string3 = extras.getString("content");
        final String string4 = extras.getString("shortcut");
        new KeyDownloadDialog(this, string2, string3, MiuiShortcut$Key.getResourceForKey("launch_noapp_dialog_install", getApplicationContext()), MiuiShortcut$Key.getResourceForKey("launch_noapp_dialog_replace", getApplicationContext()), new KeyDownloadDialog.IOnClickListener() { // from class: com.android.settings.KeyDownloadDialogActivity.1
            @Override // com.android.settings.KeyDownloadDialog.IOnClickListener
            public void onDismiss() {
                KeyDownloadDialogActivity.this.finish();
            }

            @Override // com.android.settings.KeyDownloadDialog.IOnClickListener
            public void onNegativeBtnClick() {
            }

            @Override // com.android.settings.KeyDownloadDialog.IOnClickListener
            public void onPositiveBtnClick() {
                if ("knock_gesture_v".equals(string4)) {
                    KeyDownloadDialogActivity.this.gotoKnockGestureVSetting();
                } else if ("back_double_tap".equals(string4) || "back_triple_tap".equals(string4)) {
                    KeyDownloadDialogActivity.this.gotoBackTapGestureSettings();
                } else if ("fingerprint_double_tap".equals(string4)) {
                    KeyDownloadDialogActivity.this.gotoFingerPrintTapSettings();
                }
                KeyDownloadDialogActivity.this.finish();
            }

            @Override // com.android.settings.KeyDownloadDialog.IOnClickListener
            public void onReTipBtnClick() {
                KeyDownloadDialogActivity.this.openInMarket(string);
                KeyDownloadDialogActivity.this.finish();
            }
        }).show();
    }

    void gotoBackTapGestureSettings() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.Settings");
        intent.putExtra(":android:show_fragment", "com.android.settings.BackTapSettingsFragment");
        intent.putExtra(":android:show_fragment_title", 0);
        intent.putExtra(":android:show_fragment_short_title", 0);
        intent.putExtra(":android:no_headers", true);
        intent.addFlags(268435456);
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    void gotoFingerPrintTapSettings() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.Settings");
        intent.putExtra(":android:show_fragment", "com.android.settings.GestureShortcutSettingsFragment");
        intent.putExtra(":android:show_fragment_title", 0);
        intent.putExtra(":android:show_fragment_short_title", 0);
        intent.putExtra(":android:no_headers", true);
        intent.addFlags(268435456);
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    void gotoKnockGestureVSetting() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.Settings");
        intent.putExtra(":android:show_fragment", GestureSettingsTree.GESTURE_KNOCK_V_SETTINGS_FRAGMENT);
        intent.putExtra(":android:show_fragment_title", 0);
        intent.putExtra(":android:show_fragment_short_title", 0);
        intent.putExtra(":android:no_headers", true);
        intent.addFlags(268435456);
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initView();
    }

    public void openInMarket(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format("market://details?id=%s&ref=knock_gesture&back=true", str))).addFlags(268435456));
        } catch (Exception e) {
            Slog.e("KeyDownloadDialogActivity", "Exception", e);
        }
    }
}
