package com.android.settings.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.android.settings.MiuiSubSettingsDisablePreview;
import com.android.settings.SubSettings;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes.dex */
public class SubSettingLauncher {
    private final Context mContext;
    private final LaunchRequest mLaunchRequest;
    private boolean mLaunched;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LaunchRequest {
        Bundle arguments;
        String destinationName;
        Bundle extras;
        int flags;
        int mRequestCode;
        Fragment mResultListener;
        int sourceMetricsCategory = 100;
        CharSequence title;
        int titleResId;
        String titleResPackageName;
        int transitionType;
        UserHandle userHandle;

        LaunchRequest() {
        }
    }

    public SubSettingLauncher(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must be non-null.");
        }
        this.mContext = context;
        LaunchRequest launchRequest = new LaunchRequest();
        this.mLaunchRequest = launchRequest;
        launchRequest.transitionType = 0;
    }

    private void copyExtras(Intent intent) {
        Bundle bundle = this.mLaunchRequest.extras;
        if (bundle != null) {
            intent.replaceExtras(bundle);
        }
    }

    public SubSettingLauncher addFlags(int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.flags = i | launchRequest.flags;
        return this;
    }

    public void launch() {
        if (this.mLaunched) {
            throw new IllegalStateException("This launcher has already been executed. Do not reuse");
        }
        this.mLaunched = true;
        Intent intent = toIntent();
        if (SettingsFeatures.isFoldDevice()) {
            intent.addMiuiFlags(4);
        }
        UserHandle userHandle = this.mLaunchRequest.userHandle;
        boolean z = (userHandle == null || userHandle.getIdentifier() == UserHandle.myUserId()) ? false : true;
        LaunchRequest launchRequest = this.mLaunchRequest;
        Fragment fragment = launchRequest.mResultListener;
        boolean z2 = fragment != null;
        if (z && z2) {
            launchForResultAsUser(intent, launchRequest.userHandle, fragment, launchRequest.mRequestCode);
        } else if (z && !z2) {
            launchAsUser(intent, launchRequest.userHandle);
        } else if (z || !z2) {
            launch(intent);
        } else {
            launchForResult(fragment, intent, launchRequest.mRequestCode);
        }
    }

    void launch(Intent intent) {
        this.mContext.startActivity(intent);
    }

    void launchAsUser(Intent intent, UserHandle userHandle) {
        this.mContext.startActivityAsUser(intent, userHandle);
    }

    void launchForResult(Fragment fragment, Intent intent, int i) {
        fragment.startActivityForResult(intent, i);
    }

    void launchForResultAsUser(Intent intent, UserHandle userHandle, Fragment fragment, int i) {
        fragment.getActivity().startActivityForResultAsUser(intent, i, userHandle);
    }

    public SubSettingLauncher setArguments(Bundle bundle) {
        this.mLaunchRequest.arguments = bundle;
        return this;
    }

    public SubSettingLauncher setDestination(String str) {
        this.mLaunchRequest.destinationName = str;
        return this;
    }

    public SubSettingLauncher setExtras(Bundle bundle) {
        this.mLaunchRequest.extras = bundle;
        return this;
    }

    public SubSettingLauncher setResultListener(Fragment fragment, int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.mRequestCode = i;
        launchRequest.mResultListener = fragment;
        return this;
    }

    public SubSettingLauncher setSourceMetricsCategory(int i) {
        this.mLaunchRequest.sourceMetricsCategory = i;
        return this;
    }

    public SubSettingLauncher setTitleRes(int i) {
        return setTitleRes(null, i);
    }

    public SubSettingLauncher setTitleRes(String str, int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.titleResPackageName = str;
        launchRequest.titleResId = i;
        launchRequest.title = null;
        return this;
    }

    public SubSettingLauncher setTitleText(CharSequence charSequence) {
        this.mLaunchRequest.title = charSequence;
        return this;
    }

    public SubSettingLauncher setTransitionType(int i) {
        this.mLaunchRequest.transitionType = i;
        return this;
    }

    public SubSettingLauncher setUserHandle(UserHandle userHandle) {
        this.mLaunchRequest.userHandle = userHandle;
        return this;
    }

    public Intent toIntent() {
        Intent intent = new Intent("android.intent.action.MAIN");
        copyExtras(intent);
        intent.setClass(this.mContext, SubSettings.class);
        Bundle bundle = this.mLaunchRequest.arguments;
        if (bundle != null && bundle.getBoolean("extra_disable_preview", false)) {
            intent.setClass(this.mContext, MiuiSubSettingsDisablePreview.class);
        }
        if (TextUtils.isEmpty(this.mLaunchRequest.destinationName)) {
            throw new IllegalArgumentException("Destination fragment must be set");
        }
        intent.putExtra(":settings:show_fragment", this.mLaunchRequest.destinationName);
        int i = this.mLaunchRequest.sourceMetricsCategory;
        if (i >= 0) {
            intent.putExtra(":settings:source_metrics", i);
            intent.putExtra(":settings:show_fragment_args", this.mLaunchRequest.arguments);
            intent.putExtra(":settings:show_fragment_title_res_package_name", this.mLaunchRequest.titleResPackageName);
            intent.putExtra(":settings:show_fragment_title_resid", this.mLaunchRequest.titleResId);
            intent.putExtra(":android:show_fragment_title", this.mLaunchRequest.titleResId);
            intent.putExtra(":settings:show_fragment_title", this.mLaunchRequest.title);
            intent.addFlags(this.mLaunchRequest.flags);
            intent.putExtra("page_transition_type", this.mLaunchRequest.transitionType);
            return intent;
        }
        throw new IllegalArgumentException("Source metrics category must be set");
    }
}
