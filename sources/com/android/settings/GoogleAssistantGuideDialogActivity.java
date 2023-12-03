package com.android.settings;

import android.app.Activity;
import android.app.ISearchManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class GoogleAssistantGuideDialogActivity extends Activity {
    private Drawable getIcon() {
        PackageManager packageManager = getPackageManager();
        try {
            return packageManager.getApplicationIcon(packageManager.getApplicationInfo("com.google.android.apps.googleassistant", 128));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showGuideDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.GoogleAssistantGuideDialogActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Settings.System.putStringForUser(GoogleAssistantGuideDialogActivity.this.getContentResolver(), "long_press_power_key", "launch_google_search", -2);
                    Bundle bundle = new Bundle();
                    bundle.putInt("android.intent.extra.ASSIST_INPUT_DEVICE_ID", -1);
                    try {
                        ISearchManager.Stub.asInterface(ServiceManager.getService(YellowPageContract.Search.DIRECTORY)).launchAssist(UserHandle.myUserId(), bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                GoogleAssistantGuideDialogActivity.this.finish();
            }
        };
        DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() { // from class: com.android.settings.GoogleAssistantGuideDialogActivity.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                GoogleAssistantGuideDialogActivity.this.finish();
            }
        };
        View inflate = LayoutInflater.from(this).inflate(R.layout.google_assistant_guide, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.icon);
        TextView textView = (TextView) inflate.findViewById(R.id.title);
        TextView textView2 = (TextView) inflate.findViewById(R.id.message);
        TextView textView3 = (TextView) inflate.findViewById(R.id.comment);
        imageView.setBackgroundDrawable(getIcon());
        textView.setText(getResources().getString(R.string.google_assistant_guide_title));
        textView2.setText(getResources().getString(R.string.google_assistant_guide_message, Double.valueOf(0.5d)));
        textView3.setText(getResources().getString(R.string.google_assistant_guide_tip, 3));
        new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight).setView(inflate).setPositiveButton(getResources().getString(R.string.google_assistant_guide_ok), onClickListener).setNegativeButton(getResources().getString(R.string.google_assistant_guide_cancel), onClickListener).setOnDismissListener(onDismissListener).create().show();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("GoogleAssistantGuideDialogActivity", "showGuideDialog");
        showGuideDialog();
    }
}
