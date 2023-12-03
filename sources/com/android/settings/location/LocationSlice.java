package com.android.settings.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R;
import com.android.settings.SubSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.Utils;

/* loaded from: classes.dex */
public class LocationSlice implements CustomSliceable {
    private final Context mContext;

    public LocationSlice(Context context) {
        this.mContext = context;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(R.string.location_settings_title).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, LocationSettings.class.getName(), "location", charSequence, 63).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("location").build());
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, 17302870);
        CharSequence text = this.mContext.getText(R.string.location_settings_title);
        return new ListBuilder(this.mContext, CustomSliceRegistry.LOCATION_SLICE_URI, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(new ListBuilder.RowBuilder().setTitle(text).setTitleItem(createWithResource, 0).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, text))).build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.LOCATION_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
    }
}
