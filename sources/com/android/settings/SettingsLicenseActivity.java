package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settingslib.license.LicenseHtmlLoaderCompat;
import java.io.File;
import miui.settings.commonlib.MemoryOptimizationUtil;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class SettingsLicenseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<File> {
    private String mLicenseHtmlPath;
    private String mLicenseType;

    private void showErrorAndFinish() {
        Toast.makeText(this, R.string.settings_license_activity_unavailable, 1).show();
        finish();
    }

    private void showGeneratedHtmlFile(File file) {
        if (file != null) {
            showHtmlFromUri(getUriFromGeneratedHtmlFile(file));
            return;
        }
        Log.e("SettingsLicenseActivity", "Failed to generate.");
        showErrorAndFinish();
    }

    private void showHtmlFromDefaultXmlFiles() {
        getSupportLoaderManager().initLoader(0, Bundle.EMPTY, this);
    }

    private void showHtmlFromUri(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        if ("content".equals(uri.getScheme())) {
            grantUriPermission(MemoryOptimizationUtil.CONTROLLER_PKG, uri, 1);
        }
        intent.setClassName(MemoryOptimizationUtil.CONTROLLER_PKG, "com.android.settings.SettingsLicenseActivity");
        if (!TextUtils.isEmpty(this.mLicenseType)) {
            intent.putExtra("license_type", this.mLicenseType);
            intent.putExtra("license_path", this.mLicenseHtmlPath);
        }
        try {
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } catch (ActivityNotFoundException e) {
            Log.e("SettingsLicenseActivity", "Failed to find viewer", e);
            showErrorAndFinish();
        }
    }

    Uri getUriFromGeneratedHtmlFile(File file) {
        return FileProvider.getUriForFile(this, "com.android.settings.files", file);
    }

    boolean isFileValid(File file) {
        return file.exists() && file.length() != 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mLicenseHtmlPath = "/system/etc/NOTICE.html.gz";
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("license_path")) {
            this.mLicenseHtmlPath = intent.getStringExtra("license_path");
        }
        if (intent != null && intent.hasExtra("license_type")) {
            this.mLicenseType = intent.getStringExtra("license_type");
        }
        File file = new File(this.mLicenseHtmlPath);
        if (isFileValid(file)) {
            showHtmlFromUri(Uri.fromFile(file));
        } else {
            showHtmlFromDefaultXmlFiles();
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<File> onCreateLoader(int i, Bundle bundle) {
        return new LicenseHtmlLoaderCompat(this);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<File> loader, File file) {
        showGeneratedHtmlFile(file);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<File> loader) {
    }
}
