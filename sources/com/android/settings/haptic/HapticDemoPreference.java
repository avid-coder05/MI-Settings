package com.android.settings.haptic;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.haptic.HapticDemoPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class HapticDemoPreference extends Preference implements FolmeAnimationController {
    private RelativeLayout mImgInteresting;
    private RelativeLayout mImgLimit;
    public View mRootView;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.haptic.HapticDemoPreference$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements View.OnClickListener {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0() {
            MiStatInterfaceUtils.trackPreferenceClick(HapticFragment.class.getName(), "haptic_limit");
            Intent intent = new Intent();
            intent.setClass(HapticDemoPreference.this.getContext(), HapticDetailActivity.class);
            HapticDemoPreference.this.getContext().startActivity(intent);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            view.postDelayed(new Runnable() { // from class: com.android.settings.haptic.HapticDemoPreference$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HapticDemoPreference.AnonymousClass1.this.lambda$onClick$0();
                }
            }, 100L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.haptic.HapticDemoPreference$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0() {
            MiStatInterfaceUtils.trackPreferenceClick(HapticFragment.class.getName(), "haptic_interesting");
            Intent intent = new Intent();
            intent.setClass(HapticDemoPreference.this.getContext(), HapticInterestingActivity.class);
            HapticDemoPreference.this.getContext().startActivity(intent);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            view.postDelayed(new Runnable() { // from class: com.android.settings.haptic.HapticDemoPreference$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HapticDemoPreference.AnonymousClass2.this.lambda$onClick$0();
                }
            }, 100L);
        }
    }

    public HapticDemoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.haptic_preview_layout);
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mRootView = view;
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.img_limit);
        this.mImgLimit = relativeLayout;
        Folme.useAt(relativeLayout).touch().handleTouchOf((View) this.mImgLimit, true, new AnimConfig[0]);
        this.mImgLimit.setOnClickListener(new AnonymousClass1());
        RelativeLayout relativeLayout2 = (RelativeLayout) this.mRootView.findViewById(R.id.img_interesting);
        this.mImgInteresting = relativeLayout2;
        Folme.useAt(relativeLayout2).touch().handleTouchOf((View) this.mImgInteresting, true, new AnimConfig[0]);
        this.mImgInteresting.setOnClickListener(new AnonymousClass2());
        this.mRootView.setPadding(0, 0, 0, 0);
        this.mRootView.setBackgroundColor(0);
    }
}
