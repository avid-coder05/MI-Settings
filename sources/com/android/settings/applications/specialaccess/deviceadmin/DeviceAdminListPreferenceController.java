package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.AppGlobals;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.widget.FooterPreference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class DeviceAdminListPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final IntentFilter FILTER;
    private static final String KEY_DEVICE_ADMIN_FOOTER = "device_admin_footer";
    private static final String TAG = "DeviceAdminListPrefCtrl";
    private final ArrayList<DeviceAdminListItem> mAdmins;
    private final BroadcastReceiver mBroadcastReceiver;
    private final DevicePolicyManager mDPM;
    private FooterPreference mFooterPreference;
    private final IPackageManager mIPackageManager;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;
    private PreferenceGroup mPreferenceGroup;
    private final SparseArray<ComponentName> mProfileOwnerComponents;
    private final UserManager mUm;

    static {
        IntentFilter intentFilter = new IntentFilter();
        FILTER = intentFilter;
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    }

    public DeviceAdminListPreferenceController(Context context, String str) {
        super(context, str);
        this.mAdmins = new ArrayList<>();
        this.mProfileOwnerComponents = new SparseArray<>();
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminListPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TextUtils.equals("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED", intent.getAction())) {
                    DeviceAdminListPreferenceController.this.updateList();
                }
            }
        };
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mUm = (UserManager) context.getSystemService("user");
        this.mPackageManager = this.mContext.getPackageManager();
        this.mIPackageManager = AppGlobals.getPackageManager();
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    private void addActiveAdminsForProfile(List<ComponentName> list, int i) {
        if (list == null) {
            return;
        }
        for (ComponentName componentName : list) {
            try {
                DeviceAdminInfo createDeviceAdminInfo = createDeviceAdminInfo(this.mContext, this.mIPackageManager.getReceiverInfo(componentName, 819328, i));
                if (createDeviceAdminInfo != null) {
                    this.mAdmins.add(new DeviceAdminListItem(this.mContext, createDeviceAdminInfo));
                }
            } catch (RemoteException unused) {
                Log.w(TAG, "Unable to load component: " + componentName);
            }
        }
    }

    private void addDeviceAdminBroadcastReceiversForProfile(Collection<ComponentName> collection, int i) {
        List<ResolveInfo> queryBroadcastReceiversAsUser = this.mPackageManager.queryBroadcastReceiversAsUser(new Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 32896, i);
        if (queryBroadcastReceiversAsUser == null) {
            return;
        }
        for (ResolveInfo resolveInfo : queryBroadcastReceiversAsUser) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
            if (collection == null || !collection.contains(componentName)) {
                DeviceAdminInfo createDeviceAdminInfo = createDeviceAdminInfo(this.mContext, resolveInfo.activityInfo);
                if (createDeviceAdminInfo != null && createDeviceAdminInfo.isVisible() && createDeviceAdminInfo.getActivityInfo().applicationInfo.isInternal()) {
                    this.mAdmins.add(new DeviceAdminListItem(this.mContext, createDeviceAdminInfo));
                }
            }
        }
    }

    private void bindPreference(final DeviceAdminListItem deviceAdminListItem, ValuePreference valuePreference) {
        valuePreference.setKey(deviceAdminListItem.getKey());
        valuePreference.setTitle(deviceAdminListItem.getName());
        valuePreference.setIcon(deviceAdminListItem.getIcon());
        valuePreference.setSummary(deviceAdminListItem.getDescription());
        valuePreference.setEnabled(deviceAdminListItem.isEnabled());
        valuePreference.setValue(deviceAdminListItem.isActive() ? R.string.mode_enable : R.string.mode_disable);
        valuePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminListPreferenceController$$ExternalSyntheticLambda1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$bindPreference$0;
                lambda$bindPreference$0 = DeviceAdminListPreferenceController.this.lambda$bindPreference$0(deviceAdminListItem, preference);
                return lambda$bindPreference$0;
            }
        });
        valuePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminListPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$bindPreference$1;
                lambda$bindPreference$1 = DeviceAdminListPreferenceController.lambda$bindPreference$1(preference, obj);
                return lambda$bindPreference$1;
            }
        });
        valuePreference.setSingleLineTitle(true);
    }

    private static DeviceAdminInfo createDeviceAdminInfo(Context context, ActivityInfo activityInfo) {
        try {
            return new DeviceAdminInfo(context, activityInfo);
        } catch (IOException | XmlPullParserException e) {
            Log.w(TAG, "Skipping " + activityInfo, e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$bindPreference$0(DeviceAdminListItem deviceAdminListItem, Preference preference) {
        UserHandle user = deviceAdminListItem.getUser();
        Intent launchIntent = deviceAdminListItem.getLaunchIntent(this.mContext);
        if (MiuiUtils.getInstance().canFindActivity(this.mContext, launchIntent)) {
            try {
                this.mContext.startActivityAsUser(launchIntent, user);
                return true;
            } catch (Exception unused) {
                this.mContext.startActivity(launchIntent);
                return true;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$bindPreference$1(Preference preference, Object obj) {
        return false;
    }

    private void refreshData() {
        this.mAdmins.clear();
        Iterator<UserHandle> it = this.mUm.getUserProfiles().iterator();
        while (it.hasNext()) {
            updateAvailableAdminsForProfile(it.next().getIdentifier());
        }
        Collections.sort(this.mAdmins);
    }

    private void refreshUI() {
        if (this.mPreferenceGroup == null) {
            return;
        }
        FooterPreference footerPreference = this.mFooterPreference;
        if (footerPreference != null) {
            footerPreference.setVisible(this.mAdmins.isEmpty());
        }
        ArrayMap arrayMap = new ArrayMap();
        Context context = this.mPreferenceGroup.getContext();
        int preferenceCount = this.mPreferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = this.mPreferenceGroup.getPreference(i);
            if (preference instanceof ValuePreference) {
                ValuePreference valuePreference = (ValuePreference) preference;
                arrayMap.put(valuePreference.getKey(), valuePreference);
            }
        }
        Iterator<DeviceAdminListItem> it = this.mAdmins.iterator();
        while (it.hasNext()) {
            DeviceAdminListItem next = it.next();
            ValuePreference valuePreference2 = (ValuePreference) arrayMap.remove(next.getKey());
            if (valuePreference2 == null) {
                valuePreference2 = new ValuePreference(context);
                this.mPreferenceGroup.addPreference(valuePreference2);
            }
            bindPreference(next, valuePreference2);
        }
        Iterator it2 = arrayMap.values().iterator();
        while (it2.hasNext()) {
            this.mPreferenceGroup.removePreference((ValuePreference) it2.next());
        }
    }

    private void updateAvailableAdminsForProfile(int i) {
        List<ComponentName> activeAdminsAsUser = this.mDPM.getActiveAdminsAsUser(i);
        addActiveAdminsForProfile(activeAdminsAsUser, i);
        addDeviceAdminBroadcastReceiversForProfile(activeAdminsAsUser, i);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceGroup = preferenceGroup;
        this.mFooterPreference = (FooterPreference) preferenceGroup.findPreference(KEY_DEVICE_ADMIN_FOOTER);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, FILTER, null, null);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    void updateList() {
        refreshData();
        refreshUI();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mProfileOwnerComponents.clear();
        List<UserHandle> userProfiles = this.mUm.getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            int identifier = userProfiles.get(i).getIdentifier();
            this.mProfileOwnerComponents.put(identifier, this.mDPM.getProfileOwnerAsUser(identifier));
        }
        updateList();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
