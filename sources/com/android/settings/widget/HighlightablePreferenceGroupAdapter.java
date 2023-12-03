package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/* loaded from: classes2.dex */
public class HighlightablePreferenceGroupAdapter extends PreferenceGroupAdapter {
    static final long DELAY_COLLAPSE_DURATION_MILLIS = 300;
    static final long DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    boolean mFadeInAnimated;
    final int mHighlightColor;
    private final String mHighlightKey;
    private int mHighlightPosition;
    private final int mNormalBackgroundRes;

    private void addHighlightBackground(PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        view.setTag(R.id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundColor(this.mHighlightColor);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(preferenceViewHolder);
            return;
        }
        this.mFadeInAnimated = true;
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(this.mNormalBackgroundRes), Integer.valueOf(this.mHighlightColor));
        ofObject.setDuration(200L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HighlightablePreferenceGroupAdapter.lambda$addHighlightBackground$1(view, valueAnimator);
            }
        });
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(4);
        ofObject.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        preferenceViewHolder.setIsRecyclable(false);
        requestRemoveHighlightDelayed(preferenceViewHolder);
    }

    public static void adjustInitialExpandedChildCount(SettingsPreferenceFragment settingsPreferenceFragment) {
        if (settingsPreferenceFragment == null || settingsPreferenceFragment.getPreferenceScreen() == null) {
            return;
        }
        Bundle arguments = settingsPreferenceFragment.getArguments();
        if (arguments == null || TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
            settingsPreferenceFragment.getInitialExpandedChildCount();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addHighlightBackground$1(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeHighlightBackground$2(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$0(PreferenceViewHolder preferenceViewHolder) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(preferenceViewHolder, true);
    }

    private void removeHighlightBackground(final PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        if (!z) {
            view.setTag(R.id.preference_highlighted, Boolean.FALSE);
            view.setBackgroundResource(this.mNormalBackgroundRes);
            Log.d("HighlightableAdapter", "RemoveHighlight: No animation requested - setting normal background");
            return;
        }
        Boolean bool = Boolean.TRUE;
        int i = R.id.preference_highlighted;
        if (!bool.equals(view.getTag(i))) {
            Log.d("HighlightableAdapter", "RemoveHighlight: Not highlighted - skipping");
            return;
        }
        int i2 = this.mHighlightColor;
        int i3 = this.mNormalBackgroundRes;
        view.setTag(i, Boolean.FALSE);
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i2), Integer.valueOf(i3));
        ofObject.setDuration(500L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HighlightablePreferenceGroupAdapter.lambda$removeHighlightBackground$2(view, valueAnimator);
            }
        });
        ofObject.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setBackgroundResource(HighlightablePreferenceGroupAdapter.this.mNormalBackgroundRes);
                preferenceViewHolder.setIsRecyclable(true);
            }
        });
        ofObject.start();
        Log.d("HighlightableAdapter", "Starting fade out animation");
    }

    void requestRemoveHighlightDelayed(final PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestRemoveHighlightDelayed$0(preferenceViewHolder);
            }
        }, 15000L);
    }

    void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        String str;
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition && (str = this.mHighlightKey) != null && TextUtils.equals(str, getItem(i).getKey())) {
            addHighlightBackground(preferenceViewHolder, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
            removeHighlightBackground(preferenceViewHolder, false);
        }
    }
}
