package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.PreferenceFragment;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import miui.provider.ExtraTelephony;
import miuix.internal.util.AttributeResolver;

/* loaded from: classes2.dex */
public abstract class SilentModeSettingsBase extends PreferenceFragment {
    protected static final Comparator<ZenRuleInfo> RULE_COMPARATOR = new Comparator<ZenRuleInfo>() { // from class: com.android.settings.notification.SilentModeSettingsBase.1
        private String key(ZenRuleInfo zenRuleInfo) {
            ZenModeConfig.ZenRule zenRule = zenRuleInfo.rule;
            return (ZenModeConfig.isValidScheduleConditionId(zenRule.conditionId) ? 1 : ZenModeConfig.isValidEventConditionId(zenRule.conditionId) ? 2 : 3) + zenRule.name;
        }

        @Override // java.util.Comparator
        public int compare(ZenRuleInfo zenRuleInfo, ZenRuleInfo zenRuleInfo2) {
            return key(zenRuleInfo).compareTo(key(zenRuleInfo2));
        }
    };
    protected ZenModeConfig mConfig;
    protected ContentResolver mContentResolver;
    protected Context mContext;
    private final Handler mHandler = new Handler();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    protected int mZenMode;

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        private SettingsObserver() {
            super(SilentModeSettingsBase.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ZEN_MODE_URI.equals(uri)) {
                SilentModeSettingsBase.this.updateZenMode(true);
            }
            if (this.ZEN_MODE_CONFIG_ETAG_URI.equals(uri)) {
                SilentModeSettingsBase.this.updateZenModeConfig(true);
            }
        }

        public void register() {
            SilentModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            SilentModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this);
        }

        public void unregister() {
            SilentModeSettingsBase.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public static class ZenRuleInfo {
        String id;
        ZenModeConfig.ZenRule rule;

        protected ZenRuleInfo() {
        }
    }

    private Set<Map.Entry<String, AutomaticZenRule>> getZenModeRules() {
        return NotificationManager.from(this.mContext).getAutomaticZenRules().entrySet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateZenMode(boolean z) {
        int i = Settings.Global.getInt(getContentResolver(), ExtraTelephony.ZEN_MODE, this.mZenMode);
        if (i == this.mZenMode) {
            return;
        }
        this.mZenMode = i;
        Log.d("ZenModeSettings", "updateZenMode mZenMode=" + this.mZenMode);
        if (z) {
            onZenModeChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateZenModeConfig(boolean z) {
        ZenModeConfig zenModeConfig = SilentModeUtils.getZenModeConfig(this.mContext);
        if (Objects.equals(zenModeConfig, this.mConfig)) {
            return;
        }
        this.mConfig = zenModeConfig;
        Log.d("ZenModeSettings", "updateZenModeConfig mConfig=" + this.mConfig);
        if (z) {
            onZenModeConfigChanged();
        }
    }

    protected ContentResolver getContentResolver() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mContentResolver = activity.getContentResolver();
        }
        return this.mContentResolver;
    }

    protected void maybeRefreshRules(boolean z, boolean z2) {
        if (z) {
            Log.d("ZenModeSettings", "Refreshed mRules=" + getZenModeRules());
            if (z2) {
                onZenModeConfigChanged();
            }
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        updateZenModeConfig(false);
        updateZenMode(false);
        Log.d("ZenModeSettings", "Loaded mConfig=" + this.mConfig);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.unregister();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateZenMode(true);
        updateZenModeConfig(true);
        this.mSettingsObserver.register();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(16908298);
        if (listView != null) {
            listView.setClipToPadding(false);
            listView.setPadding(0, 0, 0, (int) AttributeResolver.resolveDimension(view.getContext(), R.attr.paddingEnd));
        }
    }

    protected abstract void onZenModeChanged();

    protected abstract void onZenModeConfigChanged();

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean removeZenRule(String str) {
        boolean removeAutomaticZenRule = NotificationManager.from(this.mContext).removeAutomaticZenRule(str);
        maybeRefreshRules(removeAutomaticZenRule, true);
        return removeAutomaticZenRule;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ZenRuleInfo[] sortedRules() {
        int size = this.mConfig.automaticRules.size();
        ZenRuleInfo[] zenRuleInfoArr = new ZenRuleInfo[size];
        for (int i = 0; i < size; i++) {
            ZenRuleInfo zenRuleInfo = new ZenRuleInfo();
            zenRuleInfo.id = (String) this.mConfig.automaticRules.keyAt(i);
            zenRuleInfo.rule = (ZenModeConfig.ZenRule) this.mConfig.automaticRules.valueAt(i);
            zenRuleInfoArr[i] = zenRuleInfo;
        }
        Arrays.sort(zenRuleInfoArr, RULE_COMPARATOR);
        return zenRuleInfoArr;
    }
}
