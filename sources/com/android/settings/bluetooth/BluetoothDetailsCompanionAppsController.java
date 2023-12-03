package com.android.settings.bluetooth;

import android.companion.Association;
import android.companion.CompanionDeviceManager;
import android.companion.ICompanionDeviceManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* loaded from: classes.dex */
public class BluetoothDetailsCompanionAppsController extends BluetoothDetailsController {
    private CachedBluetoothDevice mCachedDevice;
    CompanionDeviceManager mCompanionDeviceManager;
    PackageManager mPackageManager;
    PreferenceCategory mProfilesContainer;

    public BluetoothDetailsCompanionAppsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mCachedDevice = cachedBluetoothDevice;
        this.mCompanionDeviceManager = (CompanionDeviceManager) context.getSystemService(CompanionDeviceManager.class);
        this.mPackageManager = context.getPackageManager();
        lifecycle.addObserver(this);
    }

    private CharSequence getAppName(String str) {
        try {
            PackageManager packageManager = this.mPackageManager;
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("BTCompanionController", "Package Not Found", e);
            return null;
        }
    }

    private List<Association> getAssociations(final String str) {
        return CollectionUtils.filter(this.mCompanionDeviceManager.getAllAssociations(), new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getAssociations$0;
                lambda$getAssociations$0 = BluetoothDetailsCompanionAppsController.lambda$getAssociations$0(str, (Association) obj);
                return lambda$getAssociations$0;
            }
        });
    }

    private List<String> getPreferencesNeedToShow(String str, final PreferenceCategory preferenceCategory) {
        ArrayList arrayList = new ArrayList();
        Set set = (Set) getAssociations(str).stream().map(new Function() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Association) obj).getPackageName();
            }
        }).collect(Collectors.toSet());
        for (int i = 0; i < preferenceCategory.getPreferenceCount(); i++) {
            String key = preferenceCategory.getPreference(i).getKey();
            if (set.isEmpty() || !set.contains(key)) {
                arrayList.add(key);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            removePreference(preferenceCategory, (String) it.next());
        }
        return (List) set.stream().filter(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPreferencesNeedToShow$2;
                lambda$getPreferencesNeedToShow$2 = BluetoothDetailsCompanionAppsController.lambda$getPreferencesNeedToShow$2(PreferenceCategory.this, (String) obj);
                return lambda$getPreferencesNeedToShow$2;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getAssociations$0(String str, Association association) {
        return Objects.equal(str, association.getDeviceMacAddress());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPreferencesNeedToShow$2(PreferenceCategory preferenceCategory, String str) {
        return preferenceCategory.findPreference(str) == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeAssociationDialog$1(String str, String str2, PreferenceCategory preferenceCategory, DialogInterface dialogInterface, int i) {
        if (i == -1) {
            removeAssociation(str, str2, preferenceCategory);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreferences$3(String str, String str2, PreferenceCategory preferenceCategory, CharSequence charSequence, Context context, View view) {
        removeAssociationDialog(str, str2, preferenceCategory, charSequence, context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePreferences$4(Context context, Intent intent, Preference preference) {
        context.startActivity(intent);
        return true;
    }

    private static void removeAssociation(String str, String str2, PreferenceCategory preferenceCategory) {
        try {
            ICompanionDeviceManager asInterface = ICompanionDeviceManager.Stub.asInterface(ServiceManager.getService("companiondevice"));
            java.util.Objects.requireNonNull(asInterface);
            asInterface.disassociate(str2, str);
            removePreference(preferenceCategory, str);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeAssociationDialog(final String str, final String str2, final PreferenceCategory preferenceCategory, CharSequence charSequence, Context context) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BluetoothDetailsCompanionAppsController.lambda$removeAssociationDialog$1(str, str2, preferenceCategory, dialogInterface, i);
            }
        };
        new AlertDialog.Builder(context).setPositiveButton(R.string.bluetooth_companion_app_remove_association_confirm_button, onClickListener).setNegativeButton(17039360, onClickListener).setTitle(R.string.bluetooth_companion_app_remove_association_dialog_title).setMessage(((BluetoothDetailsController) this).mContext.getString(R.string.bluetooth_companion_app_body, charSequence, this.mCachedDevice.getName())).show();
    }

    private static void removePreference(PreferenceCategory preferenceCategory, String str) {
        Preference findPreference = preferenceCategory.findPreference(str);
        if (findPreference != null) {
            preferenceCategory.removePreference(findPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_companion_apps";
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mProfilesContainer = preferenceCategory;
        preferenceCategory.setLayoutResource(R.layout.preference_companion_app);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        updatePreferences(((BluetoothDetailsController) this).mContext, this.mCachedDevice.getAddress(), this.mProfilesContainer);
    }

    public void updatePreferences(final Context context, final String str, final PreferenceCategory preferenceCategory) {
        boolean z;
        BluetoothFeatureProvider bluetoothFeatureProvider = FeatureFactory.getFactory(context).getBluetoothFeatureProvider(context);
        boolean z2 = DeviceConfig.getBoolean("settings_ui", "bt_slice_settings_enabled", true);
        Uri bluetoothDeviceSettingsUri = bluetoothFeatureProvider.getBluetoothDeviceSettingsUri(this.mCachedDevice.getDevice());
        if (z2 && bluetoothDeviceSettingsUri != null) {
            preferenceCategory.removeAll();
            return;
        }
        HashSet hashSet = new HashSet();
        for (final String str2 : getPreferencesNeedToShow(str, preferenceCategory)) {
            final CharSequence appName = getAppName(str2);
            if (!TextUtils.isEmpty(appName) && hashSet.add(str2)) {
                HashSet hashSet2 = hashSet;
                CompanionAppWidgetPreference companionAppWidgetPreference = new CompanionAppWidgetPreference(context.getResources().getDrawable(R.drawable.ic_clear), new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        BluetoothDetailsCompanionAppsController.this.lambda$updatePreferences$3(str2, str, preferenceCategory, appName, context, view);
                    }
                }, context);
                try {
                    Drawable applicationIcon = this.mPackageManager.getApplicationIcon(str2);
                    final Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str2);
                    companionAppWidgetPreference.setIcon(applicationIcon);
                    companionAppWidgetPreference.setTitle(appName.toString());
                    companionAppWidgetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsCompanionAppsController$$ExternalSyntheticLambda2
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            boolean lambda$updatePreferences$4;
                            lambda$updatePreferences$4 = BluetoothDetailsCompanionAppsController.lambda$updatePreferences$4(context, launchIntentForPackage, preference);
                            return lambda$updatePreferences$4;
                        }
                    });
                    companionAppWidgetPreference.setKey(str2);
                    z = true;
                    companionAppWidgetPreference.setVisible(true);
                    preferenceCategory.addPreference(companionAppWidgetPreference);
                } catch (PackageManager.NameNotFoundException e) {
                    z = true;
                    Log.e("BTCompanionController", "Icon Not Found", e);
                }
                hashSet = hashSet2;
            }
        }
    }
}
