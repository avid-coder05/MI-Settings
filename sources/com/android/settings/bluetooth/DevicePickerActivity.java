package com.android.settings.bluetooth;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.ObservableActivity;

/* loaded from: classes.dex */
public final class DevicePickerActivity extends ObservableActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        setContentView(R.layout.bluetooth_device_picker);
    }
}
