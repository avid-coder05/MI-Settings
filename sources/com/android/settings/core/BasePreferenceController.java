package com.android.settings.core;

import android.content.Context;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.slices.Sliceable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.SearchIndexableRaw;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import miui.yellowpage.YellowPageStatistic;

/* loaded from: classes.dex */
public abstract class BasePreferenceController extends AbstractPreferenceController implements Sliceable {
    public static final int AVAILABLE = 0;
    public static final int AVAILABLE_UNSEARCHABLE = 1;
    public static final int CONDITIONALLY_UNAVAILABLE = 2;
    public static final int DISABLED_DEPENDENT_SETTING = 5;
    public static final int DISABLED_FOR_USER = 4;
    private static final String TAG = "SettingsPrefController";
    public static final int UNSUPPORTED_ON_DEVICE = 3;
    private boolean mIsForWork;
    private int mMetricsCategory;
    protected final String mPreferenceKey;
    protected UiBlockListener mUiBlockListener;
    private UserHandle mWorkProfileUser;

    /* loaded from: classes.dex */
    public interface UiBlockListener {
        void onBlockerWorkFinished(BasePreferenceController basePreferenceController);
    }

    /* loaded from: classes.dex */
    public interface UiBlocker {
    }

    public BasePreferenceController(Context context, String str) {
        super(context);
        this.mPreferenceKey = str;
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Preference key must be set");
        }
    }

    public static BasePreferenceController createInstance(Context context, String str) {
        try {
            return (BasePreferenceController) Class.forName(str).getConstructor(Context.class).newInstance(context);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid preference controller: " + str, e);
        }
    }

    public static BasePreferenceController createInstance(Context context, String str, String str2) {
        try {
            return (BasePreferenceController) Class.forName(str).getConstructor(Context.class, String.class).newInstance(context, str2);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid preference controller: " + str, e);
        }
    }

    public static BasePreferenceController createInstance(Context context, String str, String str2, boolean z) {
        try {
            BasePreferenceController basePreferenceController = (BasePreferenceController) Class.forName(str).getConstructor(Context.class, String.class).newInstance(context, str2);
            basePreferenceController.setForWork(z);
            return basePreferenceController;
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid preference controller: " + str, e);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference;
        super.displayPreference(preferenceScreen);
        if (getAvailabilityStatus() != 5 || (findPreference = preferenceScreen.findPreference(getPreferenceKey())) == null) {
            return;
        }
        findPreference.setEnabled(false);
    }

    public abstract int getAvailabilityStatus();

    /* JADX INFO: Access modifiers changed from: protected */
    public int getMetricsCategory() {
        return this.mMetricsCategory;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    public int getSliceType() {
        return 0;
    }

    public Uri getSliceUri() {
        return new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath(getPreferenceKey()).build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UserHandle getWorkProfileUser() {
        return this.mWorkProfileUser;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            if (!this.mIsForWork || this.mWorkProfileUser == null) {
                return super.handlePreferenceTreeClick(preference);
            }
            preference.getExtras().putInt("android.intent.extra.USER_ID", this.mWorkProfileUser.getIdentifier());
            new SubSettingLauncher(preference.getContext()).setDestination(preference.getFragment()).setSourceMetricsCategory(preference.getExtras().getInt(YellowPageStatistic.Display.CATEGORY, 0)).setArguments(preference.getExtras()).setUserHandle(this.mWorkProfileUser).launch();
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public final boolean isAvailable() {
        if (this.mIsForWork && this.mWorkProfileUser == null) {
            return false;
        }
        int availabilityStatus = getAvailabilityStatus();
        return availabilityStatus == 0 || availabilityStatus == 1 || availabilityStatus == 5;
    }

    public final boolean isSupported() {
        return getAvailabilityStatus() != 3;
    }

    void setForWork(boolean z) {
        this.mIsForWork = z;
        if (z) {
            this.mWorkProfileUser = Utils.getManagedProfile(UserManager.get(this.mContext));
        }
    }

    public void setMetricsCategory(int i) {
        this.mMetricsCategory = i;
    }

    public void setUiBlockListener(UiBlockListener uiBlockListener) {
        this.mUiBlockListener = uiBlockListener;
    }

    public void updateDynamicRawDataToIndex(List<SearchIndexableRaw> list) {
    }

    public void updateNonIndexableKeys(List<String> list) {
        boolean z = true;
        if (isAvailable() && getAvailabilityStatus() != 1) {
            z = false;
        }
        if (z) {
            String preferenceKey = getPreferenceKey();
            if (TextUtils.isEmpty(preferenceKey)) {
                Log.w(TAG, "Skipping updateNonIndexableKeys due to empty key " + toString());
            } else if (!list.contains(preferenceKey)) {
                list.add(preferenceKey);
            } else {
                Log.w(TAG, "Skipping updateNonIndexableKeys, key already in list. " + toString());
            }
        }
    }

    public void updateRawDataToIndex(List<SearchIndexableRaw> list) {
    }
}
