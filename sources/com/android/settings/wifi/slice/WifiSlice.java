package com.android.settings.wifi.slice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.ProviderModelSliceHelper$$ExternalSyntheticLambda1;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.MiuiWifiSettings;
import com.android.settings.wifi.WifiDialogActivity;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.settingslib.wifi.WifiUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/* loaded from: classes2.dex */
public class WifiSlice implements CustomSliceable {
    static final int DEFAULT_EXPANDED_ROW_COUNT = 3;
    protected final Context mContext;
    protected final WifiManager mWifiManager;

    public WifiSlice(Context context) {
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    private SliceAction getActivityAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, i, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE), iconCompat, 0, charSequence);
    }

    private SliceAction getBroadcastAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        intent.addFlags(268435456);
        return SliceAction.create(PendingIntent.getBroadcast(this.mContext, i, intent, 201326592), iconCompat, 0, charSequence);
    }

    private Set<String> getKeywords() {
        return (Set) Arrays.asList(TextUtils.split(this.mContext.getString(R.string.keywords_wifi), ",")).stream().map(ProviderModelSliceHelper$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toSet());
    }

    private ListBuilder getListBuilder(boolean z, WifiSliceItem wifiSliceItem) {
        return new ListBuilder(this.mContext, getUri(), -1L).setAccentColor(-1).setKeywords(getKeywords()).addRow(getHeaderRow(z, wifiSliceItem)).addAction(SliceAction.createToggle(getBroadcastIntent(this.mContext), null, z));
    }

    private ListBuilder.RowBuilder getLoadingRow(CharSequence charSequence) {
        return new ListBuilder.RowBuilder().setTitleItem(Utils.createIconWithDrawable(new ColorDrawable(0)), 0).setTitle(charSequence).setSubtitle(this.mContext.getText(R.string.wifi_empty_list_wifi_on));
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    private boolean isWifiEnabled() {
        int wifiState = this.mWifiManager.getWifiState();
        return wifiState == 2 || wifiState == 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return WifiScanWorker.class;
    }

    protected IconCompat getEndIcon(WifiSliceItem wifiSliceItem) {
        if (wifiSliceItem.getConnectedState() != 0) {
            return IconCompat.createWithResource(this.mContext, R.drawable.ic_settings_24dp);
        }
        if (wifiSliceItem.getSecurity() != 0) {
            return IconCompat.createWithResource(this.mContext, R.drawable.ic_friction_lock_closed);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder.RowBuilder getHeaderRow(boolean z, WifiSliceItem wifiSliceItem) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R.drawable.ic_settings_wireless);
        String string = this.mContext.getString(R.string.wifi_settings);
        return new ListBuilder.RowBuilder().setTitle(string).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, string));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(R.string.wifi_settings).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, MiuiWifiSettings.class.getName(), "wifi", charSequence, 603).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("wifi").build());
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        boolean isWifiEnabled = isWifiEnabled();
        ListBuilder listBuilder = getListBuilder(isWifiEnabled, null);
        if (isWifiEnabled) {
            WifiScanWorker wifiScanWorker = (WifiScanWorker) SliceBackgroundWorker.getInstance(getUri());
            List<WifiSliceItem> results = wifiScanWorker != null ? wifiScanWorker.getResults() : null;
            int size = results == null ? 0 : results.size();
            if (size > 0 && results.get(0).getConnectedState() != 0) {
                listBuilder = getListBuilder(true, results.get(0));
            }
            if (isApRowCollapsed()) {
                return listBuilder.build();
            }
            CharSequence text = this.mContext.getText(R.string.summary_placeholder);
            for (int i = 0; i < 3; i++) {
                if (i < size) {
                    listBuilder.addRow(getWifiSliceItemRow(results.get(i)));
                } else if (i == size) {
                    listBuilder.addRow(getLoadingRow(text));
                } else {
                    listBuilder.addRow(new ListBuilder.RowBuilder().setTitle(text).setSubtitle(text));
                }
            }
            return listBuilder.build();
        }
        return listBuilder.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.WIFI_SLICE_URI;
    }

    protected SliceAction getWifiEntryAction(WifiSliceItem wifiSliceItem, IconCompat iconCompat, CharSequence charSequence) {
        int hashCode = wifiSliceItem.getKey().hashCode();
        if (wifiSliceItem.getConnectedState() == 0) {
            return wifiSliceItem.shouldEditBeforeConnect() ? getActivityAction(hashCode, new Intent(this.mContext, WifiDialogActivity.class).putExtra("key_chosen_wifientry_key", wifiSliceItem.getKey()), iconCompat, charSequence) : getBroadcastAction(hashCode, new Intent(this.mContext, ConnectToWifiHandler.class).putExtra("key_chosen_wifientry_key", wifiSliceItem.getKey()).putExtra("key_wifi_slice_uri", getUri()), iconCompat, charSequence);
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiSliceItem.getKey());
        return getActivityAction(hashCode, new SubSettingLauncher(this.mContext).setTitleRes(R.string.pref_title_network_details).setDestination(WifiNetworkDetailsFragment2.class.getName()).setArguments(bundle).setSourceMetricsCategory(103).toIntent(), iconCompat, charSequence);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IconCompat getWifiSliceItemLevelIcon(WifiSliceItem wifiSliceItem) {
        int disabled;
        if (wifiSliceItem.getConnectedState() == 2) {
            disabled = com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext);
        } else if (wifiSliceItem.getConnectedState() == 0) {
            disabled = com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, 16843817);
        } else {
            Context context = this.mContext;
            disabled = com.android.settingslib.Utils.getDisabled(context, com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16843817));
        }
        Drawable drawable = this.mContext.getDrawable(WifiUtils.getInternetIconResource(wifiSliceItem.getLevel(), wifiSliceItem.shouldShowXLevelIcon()));
        drawable.setTint(disabled);
        return Utils.createIconWithDrawable(drawable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder.RowBuilder getWifiSliceItemRow(WifiSliceItem wifiSliceItem) {
        String title = wifiSliceItem.getTitle();
        IconCompat wifiSliceItemLevelIcon = getWifiSliceItemLevelIcon(wifiSliceItem);
        ListBuilder.RowBuilder primaryAction = new ListBuilder.RowBuilder().setTitleItem(wifiSliceItemLevelIcon, 0).setTitle(title).setSubtitle(wifiSliceItem.getSummary()).setContentDescription(wifiSliceItem.getContentDescription()).setPrimaryAction(getWifiEntryAction(wifiSliceItem, wifiSliceItemLevelIcon, title));
        IconCompat endIcon = getEndIcon(wifiSliceItem);
        if (endIcon != null) {
            primaryAction.addEndItem(endIcon, 0);
        }
        return primaryAction;
    }

    protected boolean isApRowCollapsed() {
        return false;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        this.mWifiManager.setWifiEnabled(intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", this.mWifiManager.isWifiEnabled()));
    }
}
