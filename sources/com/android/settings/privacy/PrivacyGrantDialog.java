package com.android.settings.privacy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.cloud.util.Utils;
import com.android.settings.search.provider.SettingsProvider;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class PrivacyGrantDialog extends Activity {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class PrivacyGrantAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<Context> contextReference;
        private String packageName;
        private String url;

        public PrivacyGrantAsyncTask(Context context, String str, String str2) {
            this.contextReference = new WeakReference<>(context);
            this.packageName = str;
            this.url = str2;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            Context context = this.contextReference.get();
            return Boolean.valueOf(context != null ? PrivacyNetUtils.post(context, this.url, this.packageName) : false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNegButtonClick(String str) {
        Intent intent = new Intent();
        intent.putExtra(SettingsProvider.ARGS_KEY, str);
        setResult(0, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePosButtonClick(String str) {
        try {
            Utils.showShortToast(this, R.string.privacy_authorize_success);
            MiuiSettings.Privacy.setEnabled(this, str, true);
            new PrivacyGrantAsyncTask(getApplicationContext(), str, PrivacyNetUtils.isXiaomiAccountLogin(getApplicationContext()) ? "https://appauth.account.xiaomi.com/pass/grantuser" : "https://appauth.account.xiaomi.com/pass/grantdev").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            Intent intent = new Intent();
            intent.putExtra(SettingsProvider.ARGS_KEY, str);
            setResult(-1, intent);
            finish();
        } catch (Exception e) {
            Log.e("PrivacyGrantDialog", "MiuiSettings privacy setEnabled error", e);
        }
    }

    private void showDialog(final String str, String str2, String str3, String str4, String str5) {
        try {
            String locale = Locale.getDefault().toString();
            String string = getString(R.string.privacy_authorize_dialog_message, new Object[]{Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), Build.getRegion(), locale});
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (TextUtils.isEmpty(str2)) {
                builder.setTitle(R.string.privacy_authorize_title);
            } else {
                builder.setTitle(str2);
            }
            if (TextUtils.isEmpty(str3)) {
                builder.setMessage(Html.fromHtml(string));
            } else {
                builder.setMessage(Html.fromHtml(str3));
            }
            builder.setCancelable(false);
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.PrivacyGrantDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    PrivacyGrantDialog.this.handlePosButtonClick(str);
                }
            };
            if (TextUtils.isEmpty(str5)) {
                builder.setPositiveButton(R.string.privacy_authorize_dialog_agree, onClickListener);
            } else {
                builder.setPositiveButton(str5, onClickListener);
            }
            DialogInterface.OnClickListener onClickListener2 = new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.PrivacyGrantDialog.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    PrivacyGrantDialog.this.handleNegButtonClick(str);
                }
            };
            if (TextUtils.isEmpty(str4)) {
                builder.setNegativeButton(R.string.privacy_authorize_dialog_exit, onClickListener2);
            } else {
                builder.setNegativeButton(str4, onClickListener2);
            }
            builder.show().getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e) {
            Log.e("PrivacyGrantDialog", "show dialog", e);
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Build.IS_GLOBAL_BUILD) {
            finish();
            return;
        }
        Intent intent = getIntent();
        if (intent != null) {
            String locale = Locale.getDefault().toString();
            String stringExtra = intent.getStringExtra("language");
            if (!TextUtils.isEmpty(locale) && !TextUtils.isEmpty(stringExtra) && !locale.equalsIgnoreCase(stringExtra)) {
                finish();
                return;
            }
            String stringExtra2 = intent.getStringExtra(SettingsProvider.ARGS_KEY);
            String stringExtra3 = intent.getStringExtra("title");
            String stringExtra4 = intent.getStringExtra("msg");
            String stringExtra5 = intent.getStringExtra("negButton");
            String stringExtra6 = intent.getStringExtra("posButton");
            if (TextUtils.isEmpty(stringExtra2)) {
                return;
            }
            showDialog(stringExtra2, stringExtra3, stringExtra4, stringExtra5, stringExtra6);
        }
    }
}
