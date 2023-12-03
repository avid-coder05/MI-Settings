package com.android.settings.security;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import java.util.List;

/* loaded from: classes2.dex */
public class SecurityPackageVerifier extends BasePreferenceController {
    private static final String SECURITY_STATUS_PACKAGE_VERIFIER = "security_status_package_verifier";
    private Tile mTile;

    public SecurityPackageVerifier(Context context, String str) {
        super(context, str);
        this.mTile = getGooglePlayProtectTile();
    }

    private void bindPreferenceToTile(final Context context, Preference preference, Tile tile) {
        preference.setTitle(tile.getTitle(context));
        if (!TextUtils.isEmpty(tile.getSummary(context))) {
            preference.setSummary(tile.getSummary(context));
        }
        if (tile.getIntent() != null) {
            final Intent intent = tile.getIntent();
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.security.SecurityPackageVerifier$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    boolean lambda$bindPreferenceToTile$0;
                    lambda$bindPreferenceToTile$0 = SecurityPackageVerifier.lambda$bindPreferenceToTile$0(context, intent, preference2);
                    return lambda$bindPreferenceToTile$0;
                }
            });
        }
    }

    private Tile getGooglePlayProtectTile() {
        List<Tile> tiles;
        DashboardFeatureProvider dashboardFeatureProvider = FeatureFactory.getFactory(this.mContext).getDashboardFeatureProvider(this.mContext);
        DashboardCategory tilesForCategory = dashboardFeatureProvider.getTilesForCategory("com.android.settings.category.ia.security");
        if (tilesForCategory != null && (tiles = tilesForCategory.getTiles()) != null && !tiles.isEmpty()) {
            for (Tile tile : tiles) {
                String dashboardKeyForTile = dashboardFeatureProvider.getDashboardKeyForTile(tile);
                if (!TextUtils.isEmpty(dashboardKeyForTile) && dashboardKeyForTile.equals(SECURITY_STATUS_PACKAGE_VERIFIER)) {
                    return tile;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$bindPreferenceToTile$0(Context context, Intent intent, Preference preference) {
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            context.startActivity(intent);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mTile != null ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        bindPreferenceToTile(this.mContext, preference, this.mTile);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
