package com.android.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.security.ChooseLockSettingsHelper;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.maml.util.AppIconsHelper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.security.SecurityManager;
import miui.util.FeatureParser;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AccessControlSetApp extends AppCompatActivity {
    public static final HashSet<String> WHITE_LIST;

    /* loaded from: classes.dex */
    public static class AccessControlSetAppFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private ChooseLockSettingsHelper mChooseLockSettingsHelper;
        private Handler mHandler;
        private PackageManager mPm;
        private SecurityManager mSecurityManager;
        private HandlerThread mThread;
        private Handler mWorkHandler;
        private boolean mPasswordConfirmed = false;
        private final Comparator<ApplicationInfo> mComparator = new Comparator<ApplicationInfo>() { // from class: com.android.settings.AccessControlSetApp.AccessControlSetAppFragment.1
            private final Collator sCollator = Collator.getInstance();
            private final HashMap<ApplicationInfo, CharSequence> mAppLabelMap = new HashMap<>();

            private CharSequence getLabel(ApplicationInfo applicationInfo) {
                CharSequence charSequence = this.mAppLabelMap.get(applicationInfo);
                if (charSequence == null) {
                    CharSequence loadLabel = applicationInfo.loadLabel(AccessControlSetAppFragment.this.mPm);
                    this.mAppLabelMap.put(applicationInfo, loadLabel);
                    return loadLabel;
                }
                return charSequence;
            }

            @Override // java.util.Comparator
            public int compare(ApplicationInfo applicationInfo, ApplicationInfo applicationInfo2) {
                return this.sCollator.compare(getLabel(applicationInfo), getLabel(applicationInfo2));
            }
        };

        private void addPackageInfo(final ArrayList<ApplicationInfo> arrayList, final boolean z) {
            this.mWorkHandler.post(new Runnable() { // from class: com.android.settings.AccessControlSetApp.AccessControlSetAppFragment.3
                @Override // java.lang.Runnable
                public void run() {
                    Collections.sort(arrayList, AccessControlSetAppFragment.this.mComparator);
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        ApplicationInfo applicationInfo = (ApplicationInfo) it.next();
                        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(AccessControlSetAppFragment.this.getPrefContext());
                        CharSequence loadLabel = applicationInfo.loadLabel(AccessControlSetAppFragment.this.mPm);
                        if (loadLabel == null) {
                            loadLabel = applicationInfo.packageName;
                        }
                        checkBoxPreference.setTitle(loadLabel);
                        Drawable iconDrawable = AppIconsHelper.getIconDrawable(AccessControlSetAppFragment.this.getActivity(), applicationInfo, AccessControlSetAppFragment.this.mPm, 3600000L);
                        if (iconDrawable != null) {
                            checkBoxPreference.setIcon(iconDrawable);
                        }
                        checkBoxPreference.setKey(applicationInfo.packageName);
                        checkBoxPreference.setPersistent(false);
                        checkBoxPreference.setChecked(z);
                        checkBoxPreference.setOnPreferenceChangeListener(AccessControlSetAppFragment.this);
                        AccessControlSetAppFragment.this.getPreferenceScreen().addPreference(checkBoxPreference);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initCreate() {
            List<ApplicationInfo> installedApplications = this.mPm.getInstalledApplications(0);
            ArrayList<ApplicationInfo> arrayList = new ArrayList<>();
            ArrayList<ApplicationInfo> arrayList2 = new ArrayList<>();
            for (ApplicationInfo applicationInfo : installedApplications) {
                if (applicationInfo != null && ((applicationInfo.flags & 1) != 1 || AccessControlSetApp.WHITE_LIST.contains(applicationInfo.packageName))) {
                    if (this.mSecurityManager.getApplicationAccessControlEnabled(applicationInfo.packageName)) {
                        arrayList.add(applicationInfo);
                    } else {
                        arrayList2.add(applicationInfo);
                    }
                }
            }
            addPackageInfo(arrayList, true);
            addPackageInfo(arrayList2, false);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mSecurityManager = (SecurityManager) getSystemService("security");
            if (bundle != null) {
                this.mPasswordConfirmed = bundle.getBoolean("password_confirmed");
            }
            ChooseLockSettingsHelper chooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
            this.mChooseLockSettingsHelper = chooseLockSettingsHelper;
            if (!chooseLockSettingsHelper.isACLockEnabled()) {
                this.mPasswordConfirmed = true;
            }
            this.mPm = getPackageManager();
            HandlerThread handlerThread = new HandlerThread("AccessControlSetApp.Loader", -2);
            this.mThread = handlerThread;
            handlerThread.start();
            this.mWorkHandler = new Handler(this.mThread.getLooper());
            Handler handler = new Handler();
            this.mHandler = handler;
            if (this.mPasswordConfirmed) {
                initCreate();
            } else {
                handler.postDelayed(new Runnable() { // from class: com.android.settings.AccessControlSetApp.AccessControlSetAppFragment.2
                    @Override // java.lang.Runnable
                    public void run() {
                        AccessControlSetAppFragment.this.initCreate();
                    }
                }, 500L);
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.access_control_set_app);
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            Boolean bool = (Boolean) obj;
            String key = preference.getKey();
            SecurityManager securityManager = (SecurityManager) getSystemService("security");
            this.mSecurityManager = securityManager;
            securityManager.setApplicationAccessControlEnabled(key, bool.booleanValue());
            if (bool.booleanValue()) {
                return true;
            }
            this.mSecurityManager.removeAccessControlPass(key);
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            if (!this.mChooseLockSettingsHelper.isACLockEnabled() || this.mPasswordConfirmed) {
                this.mPasswordConfirmed = true;
            } else {
                getActivity().startActivityForResult(new Intent(getActivity(), ConfirmAccessControl.class), 100);
            }
            super.onStart();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStop() {
            super.onStop();
            if (this.mPasswordConfirmed) {
                this.mPasswordConfirmed = false;
            }
        }
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        WHITE_LIST = hashSet;
        hashSet.add("com.android.soundrecorder");
        hashSet.add(ContactsSyncInfoProvider.AUTHORITY);
        hashSet.add("com.android.browser");
        hashSet.add("com.mi.globalbrowser");
        hashSet.add("com.android.stk");
        hashSet.add("com.android.mms");
        if (!FeatureParser.getBoolean("is_pad", false)) {
            hashSet.add("com.android.thememanager");
        }
        hashSet.add("com.android.deskclock");
        hashSet.add("com.android.gallery3d");
        hashSet.add("com.android.updater");
        hashSet.add("com.mi.android.globalFileexplorer");
        hashSet.add("com.android.fileexplorer");
        hashSet.add(CalendarSyncInfoProvider.AUTHORITY);
        hashSet.add("com.android.vending");
        hashSet.add("com.android.apps.tag");
        hashSet.add("com.android.email");
        hashSet.add("com.miui.networkassistant");
        hashSet.add("com.android.providers.downloads.ui");
        hashSet.add("com.google.android.talk");
        hashSet.add("com.google.android.gm");
        hashSet.add("com.android.camera");
        hashSet.add("com.miui.camera");
        hashSet.add("com.miui.gallery");
        hashSet.add("com.miui.player");
        hashSet.add("com.miui.backup");
        hashSet.add("com.miui.notes");
        hashSet.add("com.xiaomi.market");
        hashSet.add("com.miui.antispam");
        hashSet.add("com.miui.video");
        hashSet.add("net.cactii.flash2");
        hashSet.add("com.xiaomi.gamecenter");
        hashSet.add("com.google.android.music");
        hashSet.add("com.google.android.youtube");
        hashSet.add("com.google.android.apps.plus");
        hashSet.add("com.facebook.orca");
        hashSet.add("com.android.chrome");
        hashSet.add(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        hashSet.add("com.xiaomi.payment");
        hashSet.add("com.mipay.wallet");
        hashSet.add("com.xiaomi.jr");
        hashSet.add("com.htc.album");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 100 || i2 == -1) {
            return;
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new AccessControlSetAppFragment()).commit();
        }
    }
}
