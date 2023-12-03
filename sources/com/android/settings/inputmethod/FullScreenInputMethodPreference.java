package com.android.settings.inputmethod;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miuix.visual.check.VisualCheckBox;
import miuix.visual.check.VisualCheckGroup;
import miuix.visual.check.VisualCheckedTextView;

/* loaded from: classes.dex */
public class FullScreenInputMethodPreference extends Preference {
    private VisualCheckGroup.OnCheckedChangeListener mCheckedListener;
    private int mDefaultImageRes;
    private int mDefaultTextRes;
    private int mHighImageRes;
    private boolean mHighKeyboardChecked;
    private int mHighTextRes;
    private View mRootView;

    public FullScreenInputMethodPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.full_screen_keyboard_optimization_view);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mRootView == null) {
            this.mRootView = preferenceViewHolder.itemView;
        }
        VisualCheckGroup visualCheckGroup = (VisualCheckGroup) this.mRootView.findViewById(R.id.checkgroup);
        VisualCheckBox visualCheckBox = (VisualCheckBox) this.mRootView.findViewById(R.id.high_keyboard);
        VisualCheckBox visualCheckBox2 = (VisualCheckBox) this.mRootView.findViewById(R.id.default_keyboard);
        VisualCheckedTextView visualCheckedTextView = (VisualCheckedTextView) this.mRootView.findViewById(R.id.high_textview);
        VisualCheckedTextView visualCheckedTextView2 = (VisualCheckedTextView) this.mRootView.findViewById(R.id.default_textview);
        ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.high_imgview);
        ImageView imageView2 = (ImageView) this.mRootView.findViewById(R.id.default_imgview);
        visualCheckBox.setChecked(this.mHighKeyboardChecked);
        visualCheckBox2.setChecked(!this.mHighKeyboardChecked);
        int i = this.mHighTextRes;
        if (i != 0) {
            visualCheckedTextView.setText(i);
        }
        int i2 = this.mDefaultTextRes;
        if (i2 != 0) {
            visualCheckedTextView2.setText(i2);
        }
        int i3 = this.mHighImageRes;
        if (i3 != 0) {
            imageView.setImageResource(i3);
        }
        int i4 = this.mDefaultImageRes;
        if (i4 != 0) {
            imageView2.setImageResource(i4);
        }
        visualCheckGroup.setOnCheckedChangeListener(this.mCheckedListener);
    }

    public void setDefaultImage(int i) {
        this.mDefaultImageRes = i;
    }

    public void setDefaultText(int i) {
        this.mDefaultTextRes = i;
    }

    public void setHighImage(int i) {
        this.mHighImageRes = i;
    }

    public void setHighKeyboardChecked(boolean z) {
        this.mHighKeyboardChecked = z;
    }

    public void setHighText(int i) {
        this.mHighTextRes = i;
    }

    public void setOnCheckedChangeListener(VisualCheckGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckedListener = onCheckedChangeListener;
    }
}
