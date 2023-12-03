package com.android.settings.magicwindow;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.Fragment;

/* loaded from: classes.dex */
public class MagicWindowActivity extends AppCompatActivity {
    private FragmentTransaction transaction;

    private Fragment onCreateFragment() {
        return new MagicWinAppControlFragment();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.magic_window_name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        this.transaction = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        this.transaction = beginTransaction;
        beginTransaction.replace(16908290, onCreateFragment()).commit();
    }
}
