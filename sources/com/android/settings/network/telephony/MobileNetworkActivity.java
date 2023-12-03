package com.android.settings.network.telephony;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import com.android.settings.R;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.network.MobileNetworkSummaryStatus$$ExternalSyntheticLambda3;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.helper.SelectableSubscriptions;
import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.List;
import java.util.function.Predicate;

/* loaded from: classes2.dex */
public class MobileNetworkActivity extends SettingsBaseActivity implements ProxySubscriptionManager.OnActiveSubscriptionChangedListener {
    static final String MOBILE_SETTINGS_TAG = "mobile_settings:";
    static final int SUB_ID_NULL = Integer.MIN_VALUE;
    private int mCurSubscriptionId;
    private boolean mFragmentForceReload = true;
    private boolean mPendingSubscriptionChange = false;
    ProxySubscriptionManager mProxySubscriptionMgr;

    private boolean doesIntentContainOptInAction(Intent intent) {
        return TextUtils.equals(intent != null ? intent.getAction() : null, "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN");
    }

    private ContactDiscoveryDialogFragment getContactDiscoveryFragment(int i) {
        return (ContactDiscoveryDialogFragment) getSupportFragmentManager().findFragmentByTag(ContactDiscoveryDialogFragment.getFragmentTag(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getSubscription$0(SubscriptionAnnotation subscriptionAnnotation) {
        return subscriptionAnnotation.getSubscriptionId() == this.mCurSubscriptionId;
    }

    private void maybeShowContactDiscoveryDialog(SubscriptionInfo subscriptionInfo) {
        int i;
        CharSequence charSequence;
        if (subscriptionInfo != null) {
            i = subscriptionInfo.getSubscriptionId();
            charSequence = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this);
        } else {
            i = -1;
            charSequence = "";
        }
        boolean z = doesIntentContainOptInAction(getIntent()) && MobileNetworkUtils.isContactDiscoveryVisible(this, i) && !MobileNetworkUtils.isContactDiscoveryEnabled(this, i);
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (z) {
            if (contactDiscoveryFragment == null) {
                contactDiscoveryFragment = ContactDiscoveryDialogFragment.newInstance(i, charSequence);
            }
            if (contactDiscoveryFragment.isAdded()) {
                return;
            }
            contactDiscoveryFragment.show(getSupportFragmentManager(), ContactDiscoveryDialogFragment.getFragmentTag(i));
        }
    }

    private void removeContactDiscoveryDialog(int i) {
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (contactDiscoveryFragment != null) {
            contactDiscoveryFragment.dismiss();
        }
    }

    private void updateTitleAndNavigation(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null) {
            setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this));
        }
    }

    private void validate(Intent intent) {
        if (doesIntentContainOptInAction(intent) && Integer.MIN_VALUE == intent.getIntExtra("android.provider.extra.SUB_ID", Integer.MIN_VALUE)) {
            throw new IllegalArgumentException("Intent with action SHOW_CAPABILITY_DISCOVERY_OPT_IN must also include the extra Settings#EXTRA_SUB_ID");
        }
    }

    String buildFragmentTag(int i) {
        return MOBILE_SETTINGS_TAG + i;
    }

    ProxySubscriptionManager getProxySubscriptionManager() {
        if (this.mProxySubscriptionMgr == null) {
            this.mProxySubscriptionMgr = ProxySubscriptionManager.getInstance(this);
        }
        return this.mProxySubscriptionMgr;
    }

    SubscriptionInfo getSubscription() {
        List<SubscriptionAnnotation> call = new SelectableSubscriptions(this, true).call();
        SubscriptionAnnotation orElse = this.mCurSubscriptionId != Integer.MIN_VALUE ? call.stream().filter(MobileNetworkSummaryStatus$$ExternalSyntheticLambda3.INSTANCE).filter(new Predicate() { // from class: com.android.settings.network.telephony.MobileNetworkActivity$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSubscription$0;
                lambda$getSubscription$0 = MobileNetworkActivity.this.lambda$getSubscription$0((SubscriptionAnnotation) obj);
                return lambda$getSubscription$0;
            }
        }).findFirst().orElse(null) : null;
        if (orElse == null) {
            orElse = call.stream().filter(MobileNetworkSummaryStatus$$ExternalSyntheticLambda3.INSTANCE).filter(new Predicate() { // from class: com.android.settings.network.telephony.MobileNetworkActivity$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((SubscriptionAnnotation) obj).isActive();
                }
            }).findFirst().orElse(null);
        }
        if (orElse == null) {
            return null;
        }
        return orElse.getSubInfo();
    }

    SubscriptionInfo getSubscriptionForSubId(int i) {
        return SubscriptionUtil.getAvailableSubscription(this, getProxySubscriptionManager(), i);
    }

    @Override // com.android.settings.network.ProxySubscriptionManager.OnActiveSubscriptionChangedListener
    public void onChanged() {
        if (!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            this.mPendingSubscriptionChange = true;
            return;
        }
        SubscriptionInfo subscription = getSubscription();
        int i = this.mCurSubscriptionId;
        updateSubscriptions(subscription, null);
        if (subscription != null) {
            if (subscription.getSubscriptionId() != i) {
                removeContactDiscoveryDialog(i);
            }
        } else if (i == Integer.MIN_VALUE || isFinishing() || isDestroyed()) {
        } else {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!((UserManager) getSystemService(UserManager.class)).isAdminUser()) {
            finish();
            return;
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        getProxySubscriptionManager().setLifecycle(getLifecycle());
        Intent intent = getIntent();
        validate(intent);
        int i = Integer.MIN_VALUE;
        if (bundle != null) {
            i = bundle.getInt("android.provider.extra.SUB_ID", Integer.MIN_VALUE);
        } else if (intent != null) {
            i = intent.getIntExtra("android.provider.extra.SUB_ID", Integer.MIN_VALUE);
        }
        this.mCurSubscriptionId = i;
        registerActiveSubscriptionsListener();
        SubscriptionInfo subscription = getSubscription();
        maybeShowContactDiscoveryDialog(subscription);
        updateSubscriptions(subscription, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ProxySubscriptionManager proxySubscriptionManager = this.mProxySubscriptionMgr;
        if (proxySubscriptionManager == null) {
            return;
        }
        proxySubscriptionManager.removeActiveSubscriptionsListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        validate(intent);
        setIntent(intent);
        int intExtra = intent != null ? intent.getIntExtra("android.provider.extra.SUB_ID", Integer.MIN_VALUE) : Integer.MIN_VALUE;
        int i = this.mCurSubscriptionId;
        this.mCurSubscriptionId = intExtra;
        this.mFragmentForceReload = intExtra == i;
        SubscriptionInfo subscription = getSubscription();
        updateSubscriptions(subscription, null);
        if (intExtra != i || !doesIntentContainOptInAction(intent)) {
            removeContactDiscoveryDialog(i);
        }
        if (doesIntentContainOptInAction(intent)) {
            maybeShowContactDiscoveryDialog(subscription);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        getProxySubscriptionManager().setLifecycle(getLifecycle());
        super.onStart();
        if (this.mPendingSubscriptionChange) {
            this.mPendingSubscriptionChange = false;
            onChanged();
        }
    }

    void registerActiveSubscriptionsListener() {
        getProxySubscriptionManager().addActiveSubscriptionsListener(this);
    }

    void saveInstanceState(Bundle bundle) {
        bundle.putInt("android.provider.extra.SUB_ID", this.mCurSubscriptionId);
    }

    void switchFragment(SubscriptionInfo subscriptionInfo) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", subscriptionId);
        if (intent != null && "android.settings.MMS_MESSAGE_SETTING".equals(intent.getAction())) {
            bundle.putString(":settings:fragment_args_key", "mms_message");
        }
        String buildFragmentTag = buildFragmentTag(subscriptionId);
        if (supportFragmentManager.findFragmentByTag(buildFragmentTag) != null) {
            if (!this.mFragmentForceReload) {
                Log.d("MobileNetworkActivity", "Keep current fragment: " + buildFragmentTag);
                return;
            }
            Log.d("MobileNetworkActivity", "Construct fragment: " + buildFragmentTag);
        }
        MobileNetworkSettings mobileNetworkSettings = new MobileNetworkSettings();
        mobileNetworkSettings.setArguments(bundle);
        beginTransaction.replace(R.id.content_frame, mobileNetworkSettings, buildFragmentTag);
        beginTransaction.commitAllowingStateLoss();
    }

    void updateSubscriptions(SubscriptionInfo subscriptionInfo, Bundle bundle) {
        if (subscriptionInfo == null) {
            return;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        updateTitleAndNavigation(subscriptionInfo);
        this.mCurSubscriptionId = subscriptionId;
        this.mFragmentForceReload = false;
    }
}
