package com.android.settings.network.telephony;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.Utils;

/* loaded from: classes2.dex */
public class Enhanced4gLteSliceHelper {
    private final Context mContext;

    public Enhanced4gLteSliceHelper(Context context) {
        this.mContext = context;
    }

    private PendingIntent getActivityIntent(String str) {
        Intent intent = new Intent(str);
        intent.setPackage("com.android.settings");
        intent.addFlags(268435456);
        return PendingIntent.getActivity(this.mContext, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    private PendingIntent getBroadcastIntent(String str) {
        Intent intent = new Intent(str);
        intent.setClass(this.mContext, SliceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this.mContext, 0, intent, 335544320);
    }

    private PersistableBundle getCarrierConfig(int i) {
        CarrierConfigManager carrierConfigManager = getCarrierConfigManager();
        if (carrierConfigManager != null) {
            return carrierConfigManager.getConfigForSubId(i);
        }
        return null;
    }

    private int getCarrierConfigManagerKeyValue(String str, int i, int i2) {
        PersistableBundle carrierConfig = getCarrierConfig(i);
        return carrierConfig != null ? carrierConfig.getInt(str, i2) : i2;
    }

    private Slice getEnhanced4gLteSlice(Uri uri, boolean z, int i) {
        return new ListBuilder(this.mContext, uri, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(new ListBuilder.RowBuilder().setTitle(getEnhanced4glteModeTitle(i)).addEndItem(SliceAction.createToggle(getBroadcastIntent("com.android.settings.mobilenetwork.action.ENHANCED_4G_LTE_CHANGED"), null, z)).setPrimaryAction(SliceAction.createDeeplink(getActivityIntent("android.settings.NETWORK_OPERATOR_SETTINGS"), IconCompat.createWithResource(this.mContext, R.drawable.ic_launcher_settings), 0, getEnhanced4glteModeTitle(i)))).build();
    }

    private CharSequence getEnhanced4glteModeTitle(int i) {
        int carrierConfigManagerKeyValue = getCarrierConfigManagerKeyValue("enhanced_4g_lte_title_variant_int", i, 0);
        boolean isCarrierConfigManagerKeyEnabled = isCarrierConfigManagerKeyEnabled("show_4g_for_lte_data_icon_bool", i, false);
        CharSequence[] textArray = this.mContext.getResources().getTextArray(R.array.enhanced_4g_lte_mode_title_variant);
        char c = 1;
        if (carrierConfigManagerKeyValue != 1) {
            c = isCarrierConfigManagerKeyEnabled ? (char) 2 : (char) 0;
        }
        return textArray[c];
    }

    private boolean isCarrierConfigManagerKeyEnabled(String str, int i, boolean z) {
        PersistableBundle carrierConfig = getCarrierConfig(i);
        return carrierConfig != null ? carrierConfig.getBoolean(str, z) : z;
    }

    private void notifyEnhanced4gLteUpdate() {
        this.mContext.getContentResolver().notifyChange(CustomSliceRegistry.ENHANCED_4G_SLICE_URI, null);
    }

    public Slice createEnhanced4gLteSlice(Uri uri) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            Log.d("Enhanced4gLteSlice", "Invalid subscription Id");
            return null;
        } else if (isCarrierConfigManagerKeyEnabled("hide_enhanced_4g_lte_bool", defaultVoiceSubId, false) || !isCarrierConfigManagerKeyEnabled("editable_enhanced_4g_lte_bool", defaultVoiceSubId, true)) {
            Log.d("Enhanced4gLteSlice", "Setting is either hidden or not editable");
            return null;
        } else {
            VolteQueryImsState queryImsState = queryImsState(defaultVoiceSubId);
            if (!queryImsState.isVoLteProvisioned()) {
                Log.d("Enhanced4gLteSlice", "Setting is either not provisioned or not enabled by Platform");
                return null;
            }
            try {
                return getEnhanced4gLteSlice(uri, queryImsState.isEnabledByUser(), defaultVoiceSubId);
            } catch (IllegalArgumentException e) {
                Log.e("Enhanced4gLteSlice", "Unable to read the current Enhanced 4g LTE status", e);
                return null;
            }
        }
    }

    protected CarrierConfigManager getCarrierConfigManager() {
        return (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
    }

    protected int getDefaultVoiceSubId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    public void handleEnhanced4gLteChanged(Intent intent) {
        boolean z = false;
        boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false);
        if (booleanExtra != intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", true)) {
            notifyEnhanced4gLteUpdate();
            return;
        }
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            notifyEnhanced4gLteUpdate();
            return;
        }
        VolteQueryImsState queryImsState = queryImsState(defaultVoiceSubId);
        if (queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl()) {
            z = true;
        }
        if (booleanExtra == z) {
            notifyEnhanced4gLteUpdate();
            return;
        }
        if (queryImsState.isVoLteProvisioned()) {
            setEnhanced4gLteModeSetting(defaultVoiceSubId, booleanExtra);
        }
        notifyEnhanced4gLteUpdate();
    }

    VolteQueryImsState queryImsState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    void setEnhanced4gLteModeSetting(int i, boolean z) {
        ImsMmTelManager createForSubscriptionId;
        if (SubscriptionManager.isValidSubscriptionId(i) && (createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(i)) != null) {
            try {
                createForSubscriptionId.setAdvancedCallingSettingEnabled(z);
            } catch (IllegalArgumentException e) {
                Log.w("Enhanced4gLteSlice", "Unable to change the Enhanced 4g LTE to " + z + ". subId=" + i, e);
            }
        }
    }
}
