package com.android.settings.wifi.calling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.util.CollectionUtils;
import com.android.internal.widget.ViewPager;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.RtlCompatibleViewPager;
import com.android.settings.widget.SlidingTabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;

/* loaded from: classes2.dex */
public class WifiCallingSettings extends InstrumentedFragment implements HelpResourceProvider {
    private static final int[] EMPTY_SUB_ID_LIST = new int[0];
    private int mConstructionSubId;
    private List<SubscriptionInfo> mSil;
    private ActiveSubscriptionsListener mSubscriptionChangeListener;
    private SlidingTabLayout mTabLayout;
    private RtlCompatibleViewPager mViewPager;

    /* loaded from: classes2.dex */
    private final class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private InternalViewPagerListener() {
        }

        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
            WifiCallingSettings.this.updateTitleForCurrentSub();
        }
    }

    private int getConstructionSubId(Bundle bundle) {
        Intent intent = getActivity().getIntent();
        int intExtra = intent != null ? intent.getIntExtra("android.provider.extra.SUB_ID", -1) : -1;
        return (intExtra != -1 || bundle == null) ? intExtra : bundle.getInt("android.provider.extra.SUB_ID", -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$containsSubId$1(int i, int i2) {
        return i2 == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$subscriptionIdList$0(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return -1;
        }
        return subscriptionInfo.getSubscriptionId();
    }

    private void maybeSetViewForSubId() {
        if (this.mSil == null) {
            return;
        }
        int i = this.mConstructionSubId;
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            for (SubscriptionInfo subscriptionInfo : this.mSil) {
                if (i == subscriptionInfo.getSubscriptionId()) {
                    this.mViewPager.setCurrentItem(this.mSil.indexOf(subscriptionInfo));
                    return;
                }
            }
        }
    }

    private List<SubscriptionInfo> updateSubList() {
        List<SubscriptionInfo> selectableSubscriptions = getSelectableSubscriptions(getContext());
        if (selectableSubscriptions == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : selectableSubscriptions) {
            try {
                if (queryImsState(subscriptionInfo.getSubscriptionId()).isWifiCallingProvisioned()) {
                    arrayList.add(subscriptionInfo);
                }
            } catch (Exception unused) {
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitleForCurrentSub() {
        if (CollectionUtils.size(this.mSil) > 1) {
            getActivity().getActionBar().setTitle(SubscriptionManager.getResourcesForSubId(getContext(), this.mSil.get(this.mViewPager.getCurrentItem()).getSubscriptionId()).getString(R.string.wifi_calling_settings_title));
        }
    }

    protected boolean containsSubId(int[] iArr, final int i) {
        return Arrays.stream(iArr).anyMatch(new IntPredicate() { // from class: com.android.settings.wifi.calling.WifiCallingSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.IntPredicate
            public final boolean test(int i2) {
                boolean lambda$containsSubId$1;
                lambda$containsSubId$1 = WifiCallingSettings.lambda$containsSubId$1(i, i2);
                return lambda$containsSubId$1;
            }
        });
    }

    protected void finish() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            activity.finish();
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_wifi_calling;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 105;
    }

    protected List<SubscriptionInfo> getSelectableSubscriptions(Context context) {
        return SubscriptionUtil.getSelectableSubscriptionInfoList(context);
    }

    protected ActiveSubscriptionsListener getSubscriptionChangeListener(final Context context) {
        return new ActiveSubscriptionsListener(context.getMainLooper(), context) { // from class: com.android.settings.wifi.calling.WifiCallingSettings.1
            @Override // com.android.settings.network.ActiveSubscriptionsListener
            public void onChanged() {
                WifiCallingSettings.this.onSubscriptionChange(context);
            }
        };
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.mConstructionSubId = getConstructionSubId(bundle);
        super.onCreate(bundle);
        Log.d("WifiCallingSettings", "SubId=" + this.mConstructionSubId);
        if (this.mConstructionSubId != -1) {
            this.mSubscriptionChangeListener = getSubscriptionChangeListener(getContext());
        }
        this.mSil = updateSubList();
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.wifi_calling_settings_tabs, viewGroup, false);
        this.mTabLayout = (SlidingTabLayout) inflate.findViewById(R.id.sliding_tabs);
        RtlCompatibleViewPager rtlCompatibleViewPager = (RtlCompatibleViewPager) inflate.findViewById(R.id.view_pager);
        this.mViewPager = rtlCompatibleViewPager;
        rtlCompatibleViewPager.setOnPageChangeListener(new InternalViewPagerListener());
        maybeSetViewForSubId();
        return inflate;
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("android.provider.extra.SUB_ID", this.mConstructionSubId);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        List<SubscriptionInfo> list = this.mSil;
        if (list == null || list.size() <= 1) {
            this.mTabLayout.setVisibility(8);
        } else {
            this.mTabLayout.setViewPager(this.mViewPager);
        }
        updateTitleForCurrentSub();
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.start();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onStop();
    }

    protected void onSubscriptionChange(Context context) {
        if (this.mSubscriptionChangeListener == null) {
            return;
        }
        int[] subscriptionIdList = subscriptionIdList(this.mSil);
        List<SubscriptionInfo> updateSubList = updateSubList();
        int[] subscriptionIdList2 = subscriptionIdList(updateSubList);
        if (subscriptionIdList2.length > 0) {
            if (subscriptionIdList.length == 0) {
                this.mSil = updateSubList;
                return;
            } else if (subscriptionIdList.length == subscriptionIdList2.length && (!containsSubId(subscriptionIdList, this.mConstructionSubId) || containsSubId(subscriptionIdList2, this.mConstructionSubId))) {
                this.mSil = updateSubList;
                return;
            }
        }
        Log.d("WifiCallingSettings", "Closed subId=" + this.mConstructionSubId + " due to subscription change: " + Arrays.toString(subscriptionIdList) + " -> " + Arrays.toString(subscriptionIdList2));
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
            this.mSubscriptionChangeListener = null;
        }
        finish();
    }

    protected WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    protected int[] subscriptionIdList(List<SubscriptionInfo> list) {
        return list == null ? EMPTY_SUB_ID_LIST : list.stream().mapToInt(new ToIntFunction() { // from class: com.android.settings.wifi.calling.WifiCallingSettings$$ExternalSyntheticLambda1
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$subscriptionIdList$0;
                lambda$subscriptionIdList$0 = WifiCallingSettings.lambda$subscriptionIdList$0((SubscriptionInfo) obj);
                return lambda$subscriptionIdList$0;
            }
        }).toArray();
    }
}
