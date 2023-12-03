package com.android.settings.privacy;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.R;
import com.android.settings.cloud.util.Utils;
import com.android.settings.emergency.util.Config;
import com.android.settings.privacy.PrivacyRevocationAdapter;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes2.dex */
public class PrivacyRevocationSettings extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<PrivacyItem>> {
    private static final String[] PACKAGE_ARRAY = {CalendarSyncInfoProvider.AUTHORITY, "com.xiaomi.calendar", "com.miui.videoplayer", "com.android.updater", SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.msa.global", "com.miui.daemon", "com.miui.bugreport", "com.android.providers.downloads.ui", "com.xiaomi.discover", "com.xiaomi.simactivate.service", "com.miui.powerkeeper", "com.android.settings", "com.xiaomi.mipicks"};
    private static CountDownTimer mCountdownTimer;
    private PrivacyRevocationAdapter mAdapter;
    private XRecyclerView mListView;
    private PrivacyRevokeAsyncTask mPrivacyRevokeAsyncTask;
    private AlertDialog mRevokeDialog;

    /* loaded from: classes2.dex */
    static class PrivacyRevokeAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<PrivacyRevocationSettings> activityReference;
        private ProgressDialog dialog;
        private PrivacyItem privacyItem;
        private String url;
        private int mRetryCount = 0;
        private final int MAX_RETRY_COUNT = 2;

        public PrivacyRevokeAsyncTask(PrivacyRevocationSettings privacyRevocationSettings, PrivacyItem privacyItem, String str) {
            this.activityReference = new WeakReference<>(privacyRevocationSettings);
            this.privacyItem = privacyItem;
            this.url = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code restructure failed: missing block: B:10:0x002a, code lost:
        
            if (r5 == false) goto L18;
         */
        /* JADX WARN: Code restructure failed: missing block: B:12:0x0030, code lost:
        
            if (isCancelled() == false) goto L14;
         */
        /* JADX WARN: Code restructure failed: missing block: B:13:0x0032, code lost:
        
            return null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x0037, code lost:
        
            return java.lang.Boolean.valueOf(r5);
         */
        /* JADX WARN: Code restructure failed: missing block: B:6:0x0011, code lost:
        
            if (r1 != null) goto L7;
         */
        /* JADX WARN: Code restructure failed: missing block: B:7:0x0013, code lost:
        
            r2 = r4.mRetryCount;
            r4.mRetryCount = r2 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:8:0x001a, code lost:
        
            if (r2 >= 2) goto L17;
         */
        /* JADX WARN: Code restructure failed: missing block: B:9:0x001c, code lost:
        
            r5 = com.android.settings.privacy.PrivacyNetUtils.post(r1.getApplicationContext(), r4.url, r4.privacyItem.packageName);
         */
        @Override // android.os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Boolean doInBackground(java.lang.Void... r5) {
            /*
                r4 = this;
                boolean r5 = r4.isCancelled()
                r0 = 0
                if (r5 == 0) goto L8
                return r0
            L8:
                r5 = 0
                java.lang.ref.WeakReference<com.android.settings.privacy.PrivacyRevocationSettings> r1 = r4.activityReference
                java.lang.Object r1 = r1.get()
                com.android.settings.privacy.PrivacyRevocationSettings r1 = (com.android.settings.privacy.PrivacyRevocationSettings) r1
                if (r1 == 0) goto L2c
            L13:
                int r2 = r4.mRetryCount
                int r3 = r2 + 1
                r4.mRetryCount = r3
                r3 = 2
                if (r2 >= r3) goto L2c
                android.content.Context r5 = r1.getApplicationContext()
                java.lang.String r2 = r4.url
                com.android.settings.privacy.PrivacyItem r3 = r4.privacyItem
                java.lang.String r3 = r3.packageName
                boolean r5 = com.android.settings.privacy.PrivacyNetUtils.post(r5, r2, r3)
                if (r5 == 0) goto L13
            L2c:
                boolean r4 = r4.isCancelled()
                if (r4 == 0) goto L33
                return r0
            L33:
                java.lang.Boolean r4 = java.lang.Boolean.valueOf(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.privacy.PrivacyRevocationSettings.PrivacyRevokeAsyncTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            super.onPostExecute((PrivacyRevokeAsyncTask) bool);
            try {
                this.mRetryCount = 0;
                ProgressDialog progressDialog = this.dialog;
                if (progressDialog != null && progressDialog.isShowing()) {
                    this.dialog.dismiss();
                }
                PrivacyRevocationSettings privacyRevocationSettings = this.activityReference.get();
                if (privacyRevocationSettings != null) {
                    if (bool.booleanValue()) {
                        MiuiSettings.Privacy.setEnabled(privacyRevocationSettings, this.privacyItem.packageName, false);
                        if ("com.android.settings".equals(this.privacyItem.packageName)) {
                            Config.setSosEnable(privacyRevocationSettings, false);
                            Config.setSosEmergencyContacts(privacyRevocationSettings, null);
                            Config.setSosCallLogEnable(privacyRevocationSettings, false);
                        }
                        this.privacyItem.enable = false;
                        Utils.showShortToast(privacyRevocationSettings, R.string.privacy_authorize_revoke_success);
                    } else {
                        Utils.showShortToast(privacyRevocationSettings, R.string.privacy_authorize_revoke_failed);
                    }
                    if (privacyRevocationSettings.mAdapter != null) {
                        privacyRevocationSettings.mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            this.mRetryCount = 0;
            PrivacyRevocationSettings privacyRevocationSettings = this.activityReference.get();
            if (privacyRevocationSettings != null) {
                ProgressDialog show = ProgressDialog.show(privacyRevocationSettings, "", privacyRevocationSettings.getResources().getString(R.string.privacy_authorize_revoking));
                this.dialog = show;
                show.setCancelable(true);
                this.dialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    private String getCustomDialogTips(String str) {
        Bundle bundle;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(str, 128);
            if (applicationInfo == null || (bundle = applicationInfo.metaData) == null) {
                return null;
            }
            int i = bundle.getInt("privacy_revoke_tips");
            if (i != 0) {
                String valueOf = String.valueOf(getPackageManager().getText(str, i, applicationInfo));
                Log.d("PrivacyRevocationSettings", "resId != 0 ");
                return valueOf;
            }
            String string = applicationInfo.metaData.getString("privacy_revoke_tips");
            Log.d("PrivacyRevocationSettings", "resId == 0 ");
            return string;
        } catch (Exception e) {
            Log.e("PrivacyRevocationSettings", e.getMessage(), e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isEnabled(Context context, String str) {
        ContentResolver contentResolver = context.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("privacy_status_");
        sb.append(str);
        return Settings.Secure.getInt(contentResolver, sb.toString(), 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isKddiVersion() {
        return "jp_kd".equals(SystemProperties.get("ro.miui.customized.region", ""));
    }

    private AlertDialog showRevokeDialog(final Context context, String str, final PrivacyItem privacyItem) {
        AlertDialog.Builder title = new AlertDialog.Builder(context).setCancelable(false).setTitle(context.getResources().getString(R.string.privacy_authorize_revoke_dialog_title));
        if (TextUtils.isEmpty(str)) {
            title.setMessage(R.string.privacy_authorize_revoke_dialog_msg);
        } else {
            title.setMessage(str);
        }
        title.setPositiveButton(R.string.privacy_authorize_revoke, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.PrivacyRevocationSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if (!Utils.isConnected(context)) {
                        Utils.showShortToast(context, R.string.privacy_authorize_network_error);
                        PrivacyRevocationSettings.this.mAdapter.notifyDataSetChanged();
                        return;
                    }
                    PrivacyRevocationSettings.this.mPrivacyRevokeAsyncTask = new PrivacyRevokeAsyncTask(PrivacyRevocationSettings.this, privacyItem, PrivacyNetUtils.isXiaomiAccountLogin(context.getApplicationContext()) ? "https://appauth.account.xiaomi.com/pass/revokeuser" : "https://appauth.account.xiaomi.com/pass/revokedev");
                    PrivacyRevocationSettings.this.mPrivacyRevokeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    dialogInterface.dismiss();
                } catch (Exception e) {
                    Log.e("PrivacyRevocationSettings", "MiuiSettings privacy modify status:", e);
                }
            }
        });
        title.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.PrivacyRevocationSettings.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    PrivacyRevocationSettings.this.mAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                } catch (Exception e) {
                    Log.e("PrivacyRevocationSettings", "MiuiSettings privacy modify status:", e);
                }
            }
        });
        AlertDialog create = title.create();
        create.show();
        final Button button = create.getButton(-1);
        button.setEnabled(false);
        mCountdownTimer = new CountDownTimer(10000L, 1000L) { // from class: com.android.settings.privacy.PrivacyRevocationSettings.5
            @Override // android.os.CountDownTimer
            public void onFinish() {
                button.setEnabled(true);
                button.setText(PrivacyRevocationSettings.this.getResources().getString(R.string.privacy_authorize_revoke));
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                button.setText(PrivacyRevocationSettings.this.getResources().getString(R.string.privacy_authorize_revoke_time, Long.valueOf(j / 1000)));
            }
        }.start();
        return create;
    }

    public void handleClick(PrivacyItem privacyItem) {
        if (!privacyItem.enable) {
            Intent intent = new Intent("miui.intent.action.PRIVACY_AUTHORIZATION_DIALOG");
            intent.putExtra(SettingsProvider.ARGS_KEY, privacyItem.packageName);
            startActivityForResult(intent, 220);
        } else if (Utils.isConnected(this)) {
            this.mRevokeDialog = showRevokeDialog(this, getCustomDialogTips(privacyItem.packageName), privacyItem);
        } else {
            Utils.showShortToast(this, R.string.privacy_authorize_network_error);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 220 || this.mAdapter == null || intent == null) {
            return;
        }
        String stringExtra = intent.getStringExtra(SettingsProvider.ARGS_KEY);
        Log.d("PrivacyRevocationSettings", "packageName : " + stringExtra);
        PrivacyItem privacyItemByPackageName = this.mAdapter.getPrivacyItemByPackageName(stringExtra);
        if (privacyItemByPackageName != null) {
            if (i2 == -1) {
                Log.d("PrivacyRevocationSettings", "RESULT_OK: ");
                privacyItemByPackageName.enable = true;
            } else if (i2 == 0) {
                Log.d("PrivacyRevocationSettings", "RESULT_CANCELED: ");
                privacyItemByPackageName.enable = false;
            }
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_revocation_settings);
        this.mAdapter = new PrivacyRevocationAdapter(this);
        this.mListView = (XRecyclerView) findViewById(R.id.list_view);
        View inflate = View.inflate(this, R.layout.privacy_revocation_settings_header, null);
        View inflate2 = View.inflate(this, R.layout.privacy_revocation_settings_footer, null);
        this.mListView.setLayoutManager(new XLinearLayoutManager(this));
        this.mListView.setAdapter(this.mAdapter);
        if (!isKddiVersion()) {
            this.mListView.addHeaderView(inflate);
            this.mListView.addFooterView(inflate2);
        }
        this.mAdapter.setListener(new PrivacyRevocationAdapter.ClickListener() { // from class: com.android.settings.privacy.PrivacyRevocationSettings.1
            @Override // com.android.settings.privacy.PrivacyRevocationAdapter.ClickListener
            public void onItemClick(PrivacyItem privacyItem) {
                PrivacyRevocationSettings.this.handleClick(privacyItem);
            }
        });
        getSupportLoaderManager().initLoader(2, null, this);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<PrivacyItem>> onCreateLoader(int i, Bundle bundle) {
        return new DataTaskLoader<List<PrivacyItem>>(this) { // from class: com.android.settings.privacy.PrivacyRevocationSettings.2
            @Override // androidx.loader.content.AsyncTaskLoader
            public List<PrivacyItem> loadInBackground() {
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < PrivacyRevocationSettings.PACKAGE_ARRAY.length; i2++) {
                    String str = PrivacyRevocationSettings.PACKAGE_ARRAY[i2];
                    Context applicationContext = PrivacyRevocationSettings.this.getApplicationContext();
                    if ((!PrivacyRevocationSettings.this.isKddiVersion() || !SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME.equals(str)) && PrivacyPackageUtils.isInstalledPackage(applicationContext, str) && PrivacyPackageUtils.isAppEnabled(applicationContext, str) && (!CalendarSyncInfoProvider.AUTHORITY.equals(str) || !PrivacyPackageUtils.isInstalledPackage(applicationContext, "com.xiaomi.calendar") || !PrivacyPackageUtils.isAppEnabled(applicationContext, "com.xiaomi.calendar"))) {
                        PrivacyItem privacyItem = new PrivacyItem();
                        privacyItem.packageName = str;
                        privacyItem.drawable = PrivacyPackageUtils.getAppIcon(PrivacyRevocationSettings.this.getApplicationContext(), str);
                        privacyItem.label = PrivacyPackageUtils.getAppLabel(PrivacyRevocationSettings.this.getApplicationContext(), str);
                        PrivacyRevocationSettings privacyRevocationSettings = PrivacyRevocationSettings.this;
                        privacyItem.enable = privacyRevocationSettings.isEnabled(privacyRevocationSettings.getApplicationContext(), str);
                        arrayList.add(privacyItem);
                    }
                }
                return arrayList;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        PrivacyRevokeAsyncTask privacyRevokeAsyncTask = this.mPrivacyRevokeAsyncTask;
        if (privacyRevokeAsyncTask != null) {
            privacyRevokeAsyncTask.cancel(true);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<PrivacyItem>> loader, List<PrivacyItem> list) {
        if (list != null) {
            this.mAdapter.setPrivacyItemList(list);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<PrivacyItem>> loader) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        AlertDialog alertDialog = this.mRevokeDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mRevokeDialog = null;
        }
        CountDownTimer countDownTimer = mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            mCountdownTimer = null;
        }
    }
}
