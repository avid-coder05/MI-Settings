package com.android.settings.notification.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.notification.ConversationIconFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class NotificationSettings extends DashboardFragment {
    private static final boolean DEBUG = Log.isLoggable("NotifiSettingsBase", 3);
    protected NotificationBackend.AppRow mAppRow;
    protected Bundle mArgs;
    protected NotificationChannel mChannel;
    protected NotificationChannelGroup mChannelGroup;
    protected Context mContext;
    protected Drawable mConversationDrawable;
    protected ShortcutInfo mConversationInfo;
    protected Intent mIntent;
    private ViewGroup mLayoutView;
    protected boolean mListeningToPackageRemove;
    protected NotificationManager mNm;
    protected String mPkg;
    protected PackageInfo mPkgInfo;
    protected PackageManager mPm;
    protected List<String> mPreferenceFilter;
    protected RoleManager mRm;
    protected RestrictedLockUtils.EnforcedAdmin mSuspendedAppsAdmin;
    protected int mUid;
    protected int mUserId;
    protected NotificationBackend mBackend = new NotificationBackend();
    protected boolean mShowLegacyChannelConfig = false;
    protected List<NotificationPreferenceController> mControllers = new ArrayList();
    protected DependentFieldListener mDependentFieldListener = new DependentFieldListener();
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.notification.app.NotificationSettings.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            NotificationSettings.this.animateIn();
            if (NotificationSettings.this.mLayoutView != null) {
                NotificationSettings.this.mLayoutView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };
    protected final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.notification.app.NotificationSettings.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            PackageInfo packageInfo = NotificationSettings.this.mPkgInfo;
            if (packageInfo == null || TextUtils.equals(packageInfo.packageName, schemeSpecificPart)) {
                if (NotificationSettings.DEBUG) {
                    Log.d("NotifiSettingsBase", "Package (" + schemeSpecificPart + ") removed. RemovingNotificationSettingsBase.");
                }
                NotificationSettings.this.onPackageRemoved();
            }
        }
    };

    /* loaded from: classes2.dex */
    protected class DependentFieldListener {
        protected DependentFieldListener() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onFieldValueChanged() {
            NotificationSettings notificationSettings = NotificationSettings.this;
            Drawable drawable = notificationSettings.mConversationDrawable;
            if (drawable != null && (drawable instanceof ConversationIconFactory.ConversationIconDrawable)) {
                ((ConversationIconFactory.ConversationIconDrawable) drawable).setImportant(notificationSettings.mChannel.isImportantConversation());
            }
            PreferenceScreen preferenceScreen = NotificationSettings.this.getPreferenceScreen();
            Iterator<NotificationPreferenceController> it = NotificationSettings.this.mControllers.iterator();
            while (it.hasNext()) {
                it.next().displayPreference(preferenceScreen);
            }
            NotificationSettings.this.updatePreferenceStates();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateIn() {
        AnimatorSet buildAnimatorSet = buildAnimatorSet(this.mLayoutView, r0.getHeight(), 0.0f, 0.0f, 1.0f, 250);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0.0f, 1.0f);
        buildAnimatorSet.play(valueAnimator);
        buildAnimatorSet.start();
    }

    private static AnimatorSet buildAnimatorSet(View view, float f, float f2, float f3, float f4, int i) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(i);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, f, f2), ObjectAnimator.ofFloat(view, View.ALPHA, f3, f4));
        return animatorSet;
    }

    private PackageInfo findPackageInfo(String str, int i) {
        String[] packagesForUid;
        if (str != null && i >= 0 && (packagesForUid = this.mPm.getPackagesForUid(i)) != null) {
            for (String str2 : packagesForUid) {
                if (str.equals(str2)) {
                    try {
                        return this.mPm.getPackageInfo(str, 64);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w("NotifiSettingsBase", "Failed to load package " + str, e);
                    }
                }
            }
        }
        return null;
    }

    private void loadAppRow() {
        this.mAppRow = this.mBackend.loadAppRow(this.mContext, this.mPm, this.mRm, this.mPkgInfo);
    }

    private void loadChannel() {
        Intent intent = getActivity().getIntent();
        String stringExtra = intent != null ? intent.getStringExtra("android.provider.extra.CHANNEL_ID") : null;
        if (stringExtra == null && intent != null) {
            Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
            stringExtra = bundleExtra != null ? bundleExtra.getString("android.provider.extra.CHANNEL_ID") : null;
        }
        this.mChannel = this.mBackend.getChannel(this.mPkg, this.mUid, stringExtra, intent != null ? intent.getStringExtra("android.provider.extra.CONVERSATION_ID") : null);
    }

    private void loadChannelGroup() {
        NotificationChannelGroup group;
        NotificationChannel notificationChannel;
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        boolean z = notificationBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || ((notificationChannel = this.mChannel) != null && "miscellaneous".equals(notificationChannel.getId()));
        this.mShowLegacyChannelConfig = z;
        if (z) {
            NotificationBackend notificationBackend2 = this.mBackend;
            NotificationBackend.AppRow appRow2 = this.mAppRow;
            this.mChannel = notificationBackend2.getChannel(appRow2.pkg, appRow2.uid, "miscellaneous", null);
        }
        NotificationChannel notificationChannel2 = this.mChannel;
        if (notificationChannel2 == null || TextUtils.isEmpty(notificationChannel2.getGroup()) || (group = this.mBackend.getGroup(this.mPkg, this.mUid, this.mChannel.getGroup())) == null) {
            return;
        }
        this.mChannelGroup = group;
    }

    private void loadConversation() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null || TextUtils.isEmpty(notificationChannel.getConversationId()) || this.mChannel.isDemoted()) {
            return;
        }
        ShortcutInfo conversationInfo = this.mBackend.getConversationInfo(this.mContext, this.mPkg, this.mUid, this.mChannel.getConversationId());
        this.mConversationInfo = conversationInfo;
        if (conversationInfo != null) {
            NotificationBackend notificationBackend = this.mBackend;
            Context context = this.mContext;
            NotificationBackend.AppRow appRow = this.mAppRow;
            this.mConversationDrawable = notificationBackend.getConversationDrawable(context, conversationInfo, appRow.pkg, appRow.uid, this.mChannel.isImportantConversation());
        }
    }

    private void loadPreferencesFilter() {
        Intent intent = getActivity().getIntent();
        this.mPreferenceFilter = intent != null ? intent.getStringArrayListExtra("android.provider.extra.CHANNEL_FILTER_LIST") : null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void animatePanel() {
        if (this.mPreferenceFilter != null) {
            ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(R.id.main_content);
            this.mLayoutView = viewGroup;
            viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        }
    }

    protected void collectConfigActivities() {
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES").setPackage(this.mAppRow.pkg);
        List<ResolveInfo> queryIntentActivities = this.mPm.queryIntentActivities(intent, 0);
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found ");
            sb.append(queryIntentActivities.size());
            sb.append(" preference activities");
            sb.append(queryIntentActivities.size() == 0 ? " ;_;" : "");
            Log.d("NotifiSettingsBase", sb.toString());
        }
        Iterator<ResolveInfo> it = queryIntentActivities.iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (appRow.settingsIntent == null) {
                appRow.settingsIntent = intent.setPackage(null).setClassName(activityInfo.packageName, activityInfo.name);
                NotificationChannel notificationChannel = this.mChannel;
                if (notificationChannel != null) {
                    this.mAppRow.settingsIntent.putExtra("android.intent.extra.CHANNEL_ID", notificationChannel.getId());
                }
                NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
                if (notificationChannelGroup != null) {
                    this.mAppRow.settingsIntent.putExtra("android.intent.extra.CHANNEL_GROUP_ID", notificationChannelGroup.getId());
                }
            } else if (DEBUG) {
                Log.d("NotifiSettingsBase", "Ignoring duplicate notification preference activity (" + activityInfo.name + ") for package " + activityInfo.packageName);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = getActivity();
        this.mIntent = getActivity().getIntent();
        this.mArgs = getArguments();
        this.mPm = getPackageManager();
        this.mNm = NotificationManager.from(this.mContext);
        this.mRm = (RoleManager) this.mContext.getSystemService(RoleManager.class);
        Bundle bundle = this.mArgs;
        this.mPkg = (bundle == null || !bundle.containsKey(FunctionColumns.PACKAGE)) ? this.mIntent.getStringExtra("android.provider.extra.APP_PACKAGE") : this.mArgs.getString(FunctionColumns.PACKAGE);
        Bundle bundle2 = this.mArgs;
        int intExtra = (bundle2 == null || !bundle2.containsKey("uid")) ? this.mIntent.getIntExtra("app_uid", -1) : this.mArgs.getInt("uid");
        this.mUid = intExtra;
        if (intExtra < 0) {
            try {
                this.mUid = this.mPm.getPackageUid(this.mPkg, 0);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        PackageInfo findPackageInfo = findPackageInfo(this.mPkg, this.mUid);
        this.mPkgInfo = findPackageInfo;
        if (findPackageInfo != null) {
            int userId = UserHandle.getUserId(this.mUid);
            this.mUserId = userId;
            this.mSuspendedAppsAdmin = RestrictedLockUtilsInternal.checkIfApplicationIsSuspended(this.mContext, this.mPkg, userId);
            loadChannel();
            loadAppRow();
            loadChannelGroup();
            loadPreferencesFilter();
            collectConfigActivities();
            if (use(HeaderPreferenceController.class) != null) {
                getSettingsLifecycle().addObserver((LifecycleObserver) use(HeaderPreferenceController.class));
            }
            if (use(ConversationHeaderPreferenceController.class) != null) {
                getSettingsLifecycle().addObserver((LifecycleObserver) use(ConversationHeaderPreferenceController.class));
            }
            Iterator<NotificationPreferenceController> it = this.mControllers.iterator();
            while (it.hasNext()) {
                it.next().onResume(this.mAppRow, this.mChannel, this.mChannelGroup, null, null, this.mSuspendedAppsAdmin, this.mPreferenceFilter);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mIntent == null && this.mArgs == null) {
            Log.w("NotifiSettingsBase", "No intent");
            toastAndFinish();
        } else if (this.mUid >= 0 && !TextUtils.isEmpty(this.mPkg) && this.mPkgInfo != null) {
            startListeningToPackageRemove();
        } else {
            Log.w("NotifiSettingsBase", "Missing package or uid or packageinfo");
            toastAndFinish();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        stopListeningToPackageRemove();
        super.onDestroy();
    }

    protected void onPackageRemoved() {
        getActivity().finishAndRemoveTask();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mAppRow == null) {
            Log.w("NotifiSettingsBase", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        loadAppRow();
        if (this.mAppRow == null) {
            Log.w("NotifiSettingsBase", "Can't load package");
            finish();
            return;
        }
        loadChannel();
        loadConversation();
        loadChannelGroup();
        loadPreferencesFilter();
        collectConfigActivities();
    }

    protected void startListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            return;
        }
        this.mListeningToPackageRemove = true;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(FunctionColumns.PACKAGE);
        getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
    }

    protected void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = false;
            getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }

    protected void toastAndFinish() {
        Toast.makeText(this.mContext, R.string.app_not_found_dlg_text, 0).show();
        getActivity().finish();
    }
}
