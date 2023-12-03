package com.android.settings.slices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.settings.bluetooth.BluetoothSliceBuilder;
import com.android.settings.notification.zen.ZenModeSliceBuilder;
import miui.settings.splitlib.SplitUtils;

/* loaded from: classes2.dex */
public class SliceDeepLinkSpringBoard extends Activity {
    private static Uri parse(Uri uri) {
        String queryParameter = uri.getQueryParameter("slice");
        if (TextUtils.isEmpty(queryParameter)) {
            EventLog.writeEvent(1397638484, "122836081", -1, "");
            return null;
        }
        return Uri.parse(queryParameter);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Uri parse = parse(getIntent().getData());
        if (parse == null) {
            Log.e("DeeplinkSpringboard", "No data found");
            finish();
            return;
        }
        try {
            startActivity(CustomSliceRegistry.isValidUri(parse) ? CustomSliceable.createInstance(getApplicationContext(), CustomSliceRegistry.getSliceClassByUri(parse)).getIntent() : CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(parse) ? ZenModeSliceBuilder.getIntent(this) : CustomSliceRegistry.BLUETOOTH_URI.equals(parse) ? BluetoothSliceBuilder.getIntent(this) : SliceBuilderUtils.getContentIntent(this, new SlicesDatabaseAccessor(this).getSliceDataFromUri(parse)));
            finish();
        } catch (Exception e) {
            Log.w("DeeplinkSpringboard", "Couldn't launch Slice intent", e);
            startActivity(new Intent(SplitUtils.SETTINGS_MAIN_INTENT));
            finish();
        }
    }
}
