package com.android.settings.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import java.util.List;

/* loaded from: classes2.dex */
public class SecurityGooglePlayUpdateController extends BasePreferenceController {
    private static final String SECURITY_STATUS_PARTIAL_SYSTEM_UPDATES = "security_status_partial_system_updates";
    private Activity mActivity;
    private Tile mTile;

    public SecurityGooglePlayUpdateController(Activity activity, String str) {
        super(activity, str);
        this.mActivity = activity;
        this.mTile = getGooglePlayUpdateTile();
    }

    private void bindPreferenceToTile(final Context context, Preference preference, Tile tile) {
        FeatureFactory.getFactory(this.mContext).getDashboardFeatureProvider(this.mContext).bindPreferenceToTileAndGetObservers((FragmentActivity) this.mActivity, false, 0, preference, tile, SECURITY_STATUS_PARTIAL_SYSTEM_UPDATES, Integer.MAX_VALUE);
        preference.setTitle(tile.getTitle(context));
        if (!TextUtils.isEmpty(tile.getSummary(context))) {
            preference.setSummary(tile.getSummary(context));
        }
        preference.setIcon((Drawable) null);
        preference.setIconSpaceReserved(false);
        if (tile.getIntent() != null) {
            final Intent intent = tile.getIntent();
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.security.SecurityGooglePlayUpdateController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    boolean lambda$bindPreferenceToTile$0;
                    lambda$bindPreferenceToTile$0 = SecurityGooglePlayUpdateController.lambda$bindPreferenceToTile$0(context, intent, preference2);
                    return lambda$bindPreferenceToTile$0;
                }
            });
        }
    }

    private Tile getGooglePlayUpdateTile() {
        List<Tile> tiles;
        DashboardFeatureProvider dashboardFeatureProvider = FeatureFactory.getFactory(this.mContext).getDashboardFeatureProvider(this.mContext);
        DashboardCategory tilesForCategory = dashboardFeatureProvider.getTilesForCategory("com.android.settings.category.ia.security");
        if (tilesForCategory != null && (tiles = tilesForCategory.getTiles()) != null && !tiles.isEmpty()) {
            for (Tile tile : tiles) {
                String dashboardKeyForTile = dashboardFeatureProvider.getDashboardKeyForTile(tile);
                if (!TextUtils.isEmpty(dashboardKeyForTile) && dashboardKeyForTile.equals(SECURITY_STATUS_PARTIAL_SYSTEM_UPDATES)) {
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
