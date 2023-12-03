package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;

/* loaded from: classes.dex */
public class ExpandDividerPreference extends Preference {
    static final String PREFERENCE_KEY = "expandable_divider";
    ImageView mImageView;
    private boolean mIsExpanded;
    private OnExpandListener mOnExpandListener;
    TextView mTextView;
    private String mTitleContent;

    /* loaded from: classes.dex */
    public interface OnExpandListener {
        void onExpand(boolean z);
    }

    public ExpandDividerPreference(Context context) {
        this(context, null);
    }

    public ExpandDividerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsExpanded = false;
        this.mTitleContent = null;
        setLayoutResource(R.layout.preference_expand_divider);
        setKey(PREFERENCE_KEY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTitle$0(String str) {
        this.mTextView.setText(str);
    }

    private void refreshState() {
        int i = this.mIsExpanded ? R.drawable.ic_settings_expand_less : R.drawable.ic_settings_expand_more;
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageResource(i);
        }
        setTitle(this.mTitleContent);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mTextView = (TextView) preferenceViewHolder.findViewById(R.id.expand_title);
        this.mImageView = (ImageView) preferenceViewHolder.findViewById(R.id.expand_icon);
        refreshState();
    }

    @Override // androidx.preference.Preference
    public void onClick() {
        this.mIsExpanded = !this.mIsExpanded;
        refreshState();
        OnExpandListener onExpandListener = this.mOnExpandListener;
        if (onExpandListener != null) {
            onExpandListener.onExpand(this.mIsExpanded);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsExpanded(boolean z) {
        this.mIsExpanded = z;
        refreshState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.mOnExpandListener = onExpandListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTitle(final String str) {
        this.mTitleContent = str;
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.postDelayed(new Runnable() { // from class: com.android.settings.fuelgauge.ExpandDividerPreference$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ExpandDividerPreference.this.lambda$setTitle$0(str);
                }
            }, 50L);
        }
    }
}
