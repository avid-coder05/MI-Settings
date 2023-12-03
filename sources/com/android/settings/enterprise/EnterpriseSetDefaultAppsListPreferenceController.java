package com.android.settings.enterprise;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.EnterpriseDefaultApps;
import com.android.settings.applications.UserAppInfo;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.users.UserFeatureProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class EnterpriseSetDefaultAppsListPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final ApplicationFeatureProvider mApplicationFeatureProvider;
    private List<EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>>> mApps;
    private final EnterprisePrivacyFeatureProvider mEnterprisePrivacyFeatureProvider;
    private final SettingsPreferenceFragment mParent;
    private final PackageManager mPm;
    private final UserFeatureProvider mUserFeatureProvider;
    private List<UserInfo> mUsers;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps;

        static {
            int[] iArr = new int[EnterpriseDefaultApps.values().length];
            $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps = iArr;
            try {
                iArr[EnterpriseDefaultApps.BROWSER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CALENDAR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CONTACTS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.PHONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.MAP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.EMAIL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CAMERA.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public EnterpriseSetDefaultAppsListPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, PackageManager packageManager) {
        super(context);
        this.mUsers = Collections.emptyList();
        this.mApps = Collections.emptyList();
        this.mPm = packageManager;
        this.mParent = settingsPreferenceFragment;
        FeatureFactory factory = FeatureFactory.getFactory(context);
        this.mApplicationFeatureProvider = factory.getApplicationFeatureProvider(context);
        this.mEnterprisePrivacyFeatureProvider = factory.getEnterprisePrivacyFeatureProvider(context);
        this.mUserFeatureProvider = factory.getUserFeatureProvider(context);
        buildAppList();
    }

    private void buildAppList() {
        this.mUsers = new ArrayList();
        this.mApps = new ArrayList();
        for (UserHandle userHandle : this.mUserFeatureProvider.getUserProfiles()) {
            EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>> enumMap = null;
            boolean z = false;
            for (EnterpriseDefaultApps enterpriseDefaultApps : EnterpriseDefaultApps.values()) {
                List<UserAppInfo> findPersistentPreferredActivities = this.mApplicationFeatureProvider.findPersistentPreferredActivities(userHandle.getIdentifier(), enterpriseDefaultApps.getIntents());
                if (!findPersistentPreferredActivities.isEmpty()) {
                    if (!z) {
                        this.mUsers.add(findPersistentPreferredActivities.get(0).userInfo);
                        enumMap = new EnumMap<>(EnterpriseDefaultApps.class);
                        this.mApps.add(enumMap);
                        z = true;
                    }
                    ArrayList arrayList = new ArrayList();
                    Iterator<UserAppInfo> it = findPersistentPreferredActivities.iterator();
                    while (it.hasNext()) {
                        arrayList.add(it.next().appInfo);
                    }
                    enumMap.put((EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>>) enterpriseDefaultApps, (EnterpriseDefaultApps) arrayList);
                }
            }
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EnterpriseSetDefaultAppsListPreferenceController.this.lambda$buildAppList$0();
            }
        });
    }

    private CharSequence buildSummaryString(Context context, List<ApplicationInfo> list) {
        Object[] objArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            objArr[i] = list.get(i).loadLabel(this.mPm);
        }
        return list.size() == 1 ? objArr[0] : list.size() == 2 ? context.getString(R.string.app_names_concatenation_template_2, objArr[0], objArr[1]) : context.getString(R.string.app_names_concatenation_template_3, objArr[0], objArr[1], objArr[2]);
    }

    private void createPreferences(Context context, PreferenceGroup preferenceGroup, EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>> enumMap) {
        if (preferenceGroup == null) {
            return;
        }
        for (EnterpriseDefaultApps enterpriseDefaultApps : EnterpriseDefaultApps.values()) {
            List<ApplicationInfo> list = enumMap.get(enterpriseDefaultApps);
            if (list != null && !list.isEmpty()) {
                Preference preference = new Preference(context);
                preference.setTitle(getTitle(context, enterpriseDefaultApps, list.size()));
                preference.setSummary(buildSummaryString(context, list));
                preference.setOrder(enterpriseDefaultApps.ordinal());
                preference.setSelectable(false);
                preferenceGroup.addPreference(preference);
            }
        }
    }

    private String getTitle(Context context, EnterpriseDefaultApps enterpriseDefaultApps, int i) {
        switch (AnonymousClass1.$SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[enterpriseDefaultApps.ordinal()]) {
            case 1:
                return context.getString(R.string.default_browser_title);
            case 2:
                return context.getString(R.string.default_calendar_app_title);
            case 3:
                return context.getString(R.string.default_contacts_app_title);
            case 4:
                return context.getResources().getQuantityString(R.plurals.default_phone_app_title, i);
            case 5:
                return context.getString(R.string.default_map_app_title);
            case 6:
                return context.getResources().getQuantityString(R.plurals.default_email_app_title, i);
            case 7:
                return context.getResources().getQuantityString(R.plurals.default_camera_app_title, i);
            default:
                throw new IllegalStateException("Unknown type of default " + enterpriseDefaultApps);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateUi  reason: merged with bridge method [inline-methods] */
    public void lambda$buildAppList$0() {
        Context context = this.mParent.getContext();
        PreferenceGroup preferenceScreen = this.mParent.getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        if (!this.mEnterprisePrivacyFeatureProvider.isInCompMode() && this.mUsers.size() == 1) {
            createPreferences(context, preferenceScreen, this.mApps.get(0));
            return;
        }
        for (int i = 0; i < this.mUsers.size(); i++) {
            UserInfo userInfo = this.mUsers.get(i);
            PreferenceGroup preferenceCategory = new PreferenceCategory(context);
            preferenceScreen.addPreference(preferenceCategory);
            if (userInfo.isManagedProfile()) {
                preferenceCategory.setTitle(R.string.category_work);
            } else {
                preferenceCategory.setTitle(R.string.category_personal);
            }
            preferenceCategory.setOrder(i);
            createPreferences(context, preferenceCategory, this.mApps.get(i));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }
}
