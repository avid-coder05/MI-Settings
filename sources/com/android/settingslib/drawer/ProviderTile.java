package com.android.settingslib.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import java.util.List;

/* loaded from: classes2.dex */
public class ProviderTile extends Tile {
    private String mAuthority;
    private String mKey;

    public ProviderTile(ProviderInfo providerInfo, String str, Bundle bundle) {
        super(providerInfo, str);
        setMetaData(bundle);
        this.mAuthority = providerInfo.authority;
        this.mKey = bundle.getString("com.android.settings.keyhint");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProviderTile(Parcel parcel) {
        super(parcel);
        this.mAuthority = ((ProviderInfo) this.mComponentInfo).authority;
        this.mKey = getMetaData().getString("com.android.settings.keyhint");
    }

    @Override // com.android.settingslib.drawer.Tile
    protected ComponentInfo getComponentInfo(Context context) {
        if (this.mComponentInfo == null) {
            System.currentTimeMillis();
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            Intent intent = getIntent();
            List<ResolveInfo> queryIntentContentProviders = packageManager.queryIntentContentProviders(intent, 0);
            if (queryIntentContentProviders == null || queryIntentContentProviders.isEmpty()) {
                Log.e("ProviderTile", "Cannot find package info for " + intent.getComponent().flattenToString());
            } else {
                ProviderInfo providerInfo = queryIntentContentProviders.get(0).providerInfo;
                this.mComponentInfo = providerInfo;
                setMetaData(TileUtils.getSwitchDataFromProvider(context, providerInfo.authority, this.mKey));
            }
        }
        return this.mComponentInfo;
    }

    @Override // com.android.settingslib.drawer.Tile
    protected CharSequence getComponentLabel(Context context) {
        return null;
    }

    @Override // com.android.settingslib.drawer.Tile
    public String getDescription() {
        return this.mAuthority + "/" + this.mKey;
    }
}
