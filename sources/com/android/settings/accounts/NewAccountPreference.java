package com.android.settings.accounts;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.SyncStatusObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.UserHandle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.preference.FolmeAnimationController;

@TargetApi(5)
/* loaded from: classes.dex */
class NewAccountPreference extends Preference implements Preference.OnPreferenceClickListener, FolmeAnimationController, SyncStatusObserver {
    private Account mAccount;
    private String mAuthority;
    private final String mFragment;
    private final Bundle mFragmentArguments;
    private Handler mHandler;
    private boolean mIsActive;
    private boolean mIsPending;
    public final CharSequence mTitle;
    private final int mTitleResId;
    private final String mTitleResPackageName;
    private Runnable mUpdateUIRunable;
    private UserHandle mUserHandle;
    private final MiuiAccountSettings miuiAccountSettings;
    private Object objectHandle;

    public NewAccountPreference(MiuiAccountSettings miuiAccountSettings, Context context, CharSequence charSequence, String str, int i, String str2, Bundle bundle, Drawable drawable) {
        super(context);
        this.objectHandle = null;
        this.mIsActive = false;
        this.mIsPending = false;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mUpdateUIRunable = null;
        this.miuiAccountSettings = miuiAccountSettings;
        this.mTitle = charSequence;
        this.mTitleResPackageName = str;
        this.mTitleResId = i;
        this.mFragment = str2;
        this.mFragmentArguments = bundle;
        this.mAccount = (Account) bundle.getParcelable("account");
        UserHandle userHandle = (UserHandle) bundle.getParcelable("android.intent.extra.USER");
        this.mUserHandle = userHandle;
        if (userHandle != null) {
            for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypesAsUser(userHandle.getIdentifier())) {
                Account account = this.mAccount;
                if (account != null) {
                    if (syncAdapterType.accountType.equals(account.type)) {
                        this.mAuthority = syncAdapterType.authority;
                    }
                }
            }
        }
        setLayoutResource(R.layout.preference_system_app_new);
        setTitle(charSequence);
        setIcon(drawable);
        setOnPreferenceClickListener(this);
    }

    private void setIsActive(boolean z) {
        this.mIsActive = z;
    }

    private void setIsPending(boolean z) {
        this.mIsPending = z;
    }

    private void updateEndUI() {
        Runnable runnable = this.mUpdateUIRunable;
        if (runnable != null) {
            this.mHandler.post(runnable);
        }
    }

    @TargetApi(8)
    public boolean isSyncing(List<SyncInfo> list, Account account, String str) {
        for (SyncInfo syncInfo : list) {
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (this.objectHandle == null) {
            this.objectHandle = ContentResolver.addStatusChangeListener(13, this);
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Folme.clean(preferenceViewHolder.itemView);
        View findViewById = preferenceViewHolder.itemView.findViewById(R.id.head);
        final View findViewById2 = preferenceViewHolder.itemView.findViewById(R.id.end);
        final TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        final ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(R.id.sync_img);
        textView.setSelected(true);
        if (this.mUpdateUIRunable == null && imageView != null && findViewById2 != null) {
            this.mUpdateUIRunable = new Runnable() { // from class: com.android.settings.accounts.NewAccountPreference.1
                @Override // java.lang.Runnable
                public void run() {
                    boolean z = NewAccountPreference.this.mIsActive || NewAccountPreference.this.mIsPending;
                    textView.setVisibility(z ? 8 : 0);
                    findViewById2.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.accounts.NewAccountPreference.1.1
                        @Override // android.view.View.AccessibilityDelegate
                        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                            accessibilityNodeInfo.setEnabled(true);
                        }
                    });
                    findViewById2.setEnabled(!z);
                    imageView.setVisibility(z ? 0 : 8);
                    imageView.setContentDescription(NewAccountPreference.this.getContext().getResources().getString(R.string.sync_in_progress));
                }
            };
        }
        if (findViewById != null) {
            if (CommonUtils.isRtl()) {
                findViewById.setBackgroundResource(R.drawable.preference_card_head_bg_rtl);
            } else {
                findViewById.setBackgroundResource(R.drawable.preference_card_head_bg);
            }
            Folme.useAt(findViewById).touch().setScale(1.0f, ITouchStyle.TouchType.DOWN).handleTouchOf(findViewById, new AnimConfig[0]);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accounts.NewAccountPreference.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (NewAccountPreference.this.mFragment != null) {
                        new SubSettingLauncher(NewAccountPreference.this.getContext()).setDestination(NewAccountPreference.this.mFragment).setArguments(NewAccountPreference.this.mFragmentArguments).setTitleText(NewAccountPreference.this.mTitle.toString()).launch();
                    }
                }
            });
        }
        if (findViewById2 != null) {
            if (CommonUtils.isRtl()) {
                findViewById2.setBackgroundResource(R.drawable.preference_card_end_bg_rtl);
            } else {
                findViewById2.setBackgroundResource(R.drawable.preference_card_end_bg);
            }
            Folme.useAt(findViewById2).touch().setScale(1.0f, ITouchStyle.TouchType.DOWN).handleTouchOf(findViewById2, new AnimConfig[0]);
            findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accounts.NewAccountPreference.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (NewAccountPreference.this.mAccount == null) {
                        NewAccountPreference newAccountPreference = NewAccountPreference.this;
                        newAccountPreference.mAccount = (Account) newAccountPreference.mFragmentArguments.getParcelable("account");
                    }
                    NewAccountPreference.this.miuiAccountSettings.requestOrCancelSync(NewAccountPreference.this.mTitleResPackageName, NewAccountPreference.this.mAccount, NewAccountPreference.this.mUserHandle, NewAccountPreference.this.mAuthority, true);
                }
            });
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        Object obj = this.objectHandle;
        if (obj != null) {
            ContentResolver.removeStatusChangeListener(obj);
            this.objectHandle = null;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(androidx.preference.Preference preference) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override // android.content.SyncStatusObserver
    public void onStatusChanged(int i) {
        onSyncStateUpdate();
    }

    public void onSyncStateUpdate() {
        int identifier = this.mUserHandle.getIdentifier();
        SyncStatusInfo syncStatusAsUser = ContentResolver.getSyncStatusAsUser(this.mAccount, this.mAuthority, identifier);
        boolean z = syncStatusAsUser == null ? false : syncStatusAsUser.pending;
        boolean z2 = syncStatusAsUser == null ? false : syncStatusAsUser.initialize;
        boolean isSyncing = isSyncing(ContentResolver.getCurrentSyncsAsUser(identifier), this.mAccount, this.mAuthority);
        int isSyncableAsUser = ContentResolver.getIsSyncableAsUser(this.mAccount, this.mAuthority, identifier);
        setIsActive(isSyncing && isSyncableAsUser >= 0 && !z2);
        setIsPending(z && isSyncableAsUser >= 0 && !z2);
        if ((!ContentResolver.getCurrentSyncs().isEmpty()) == false) {
            setIsActive(false);
            setIsPending(false);
        }
        updateEndUI();
        if (!this.miuiAccountSettings.mPackageTimeMap.containsKey(this.mAuthority)) {
            this.miuiAccountSettings.mPackageTimeMap.put(this.mAuthority, Long.valueOf(syncStatusAsUser.lastSuccessTime));
            return;
        }
        Long l = this.miuiAccountSettings.mPackageTimeMap.get(this.mAuthority);
        if (l != null) {
            long longValue = l.longValue();
            long j = syncStatusAsUser.lastSuccessTime;
            if (longValue < j) {
                this.miuiAccountSettings.mPackageTimeMap.put(this.mAuthority, Long.valueOf(j));
            }
        }
    }
}
