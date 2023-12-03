package com.android.settings.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.slice.Slice;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes2.dex */
public interface CustomSliceable extends Sliceable {
    static CustomSliceable createInstance(Context context, Class<? extends CustomSliceable> cls) {
        try {
            return cls.getConstructor(Context.class).newInstance(context.getApplicationContext());
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid sliceable class: " + cls, e);
        }
    }

    default PendingIntent getBroadcastIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(getUri().toString()).setData(getUri()).setClass(context, SliceBroadcastReceiver.class), 167772160);
    }

    Intent getIntent();

    Slice getSlice();

    Uri getUri();

    @Override // com.android.settings.slices.Sliceable
    default boolean isSliceable() {
        return true;
    }

    default void onNotifyChange(Intent intent) {
    }
}
