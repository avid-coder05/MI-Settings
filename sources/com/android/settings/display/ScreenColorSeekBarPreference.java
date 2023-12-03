package com.android.settings.display;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class ScreenColorSeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private ColorStateList mColorStateList;
    private ColorStateList mDefaultColorStateList;
    private double mDegreePerEntry;
    private int mLevel;
    private CharSequence[] mLevelTexts;
    private int mSeekBarThumbOffset;
    private int mSeekBarThumbWidth;

    public ScreenColorSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScreenColorSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDegreePerEntry = 50.0d;
        this.mColorStateList = generateLevelTextColorStateList();
    }

    private ColorStateList generateLevelTextColorStateList() {
        Resources resources = getContext().getResources();
        Resources.Theme theme = getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        int[][] iArr = {new int[]{16842913}, new int[1]};
        theme.resolveAttribute(R.attr.preferencePrimaryTextColor, typedValue, true);
        return new ColorStateList(iArr, new int[]{resources.getColor(typedValue.resourceId), resources.getColor(typedValue.resourceId)});
    }

    private int levelToProgress(int i) {
        return (int) ((i * this.mDegreePerEntry) + 0.5d);
    }

    private int progressToLevel(int i) {
        double d = this.mDegreePerEntry;
        return (int) ((i + (d / 2.0d)) / d);
    }

    private void setSeekBarProgress(SeekBar seekBar, int i) {
        if (seekBar == null) {
            return;
        }
        if (i == 0 || i == 100) {
            seekBar.setThumbOffset(this.mSeekBarThumbOffset);
        } else {
            seekBar.setThumbOffset(this.mSeekBarThumbWidth / 2);
        }
        seekBar.setProgress(i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        if (seekBar != null) {
            this.mSeekBarThumbOffset = seekBar.getThumbOffset();
            Drawable thumb = seekBar.getThumb();
            this.mSeekBarThumbWidth = thumb == null ? 0 : thumb.getIntrinsicWidth();
        }
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.level_text);
        linearLayout.setPaddingRelative(seekBar.getPaddingStart(), linearLayout.getPaddingTop(), seekBar.getPaddingEnd(), linearLayout.getPaddingBottom());
        if (linearLayout.getChildCount() == 0) {
            int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(getContext().getResources().getConfiguration().locale);
            int i = 0;
            while (true) {
                if (i >= 3) {
                    break;
                }
                TextView textView = new TextView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2);
                if (i == 0) {
                    textView.setGravity((layoutDirectionFromLocale != 0 ? 5 : 3) | 16);
                    layoutParams.weight = 1.0f;
                } else if (i == 2) {
                    textView.setGravity((layoutDirectionFromLocale == 0 ? 5 : 3) | 16);
                    layoutParams.weight = 1.0f;
                } else {
                    textView.setGravity(17);
                    layoutParams.weight = 2.0f;
                }
                linearLayout.addView(textView, i, layoutParams);
                i++;
            }
        }
        seekBar.setOnSeekBarChangeListener(this);
        setSeekBarProgress(seekBar, levelToProgress(this.mLevel));
        int i2 = 0;
        while (i2 < linearLayout.getChildCount()) {
            TextView textView2 = (TextView) linearLayout.getChildAt(i2);
            textView2.setText(this.mLevelTexts[i2]);
            textView2.setSelected(i2 == this.mLevel);
            if (this.mDefaultColorStateList == null) {
                this.mDefaultColorStateList = textView2.getTextColors();
            }
            textView2.setTextColor(textView2.isEnabled() ? this.mColorStateList : this.mColorStateList.withAlpha(Color.alpha(this.mDefaultColorStateList.getColorForState(textView2.getDrawableState(), 0))));
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.preference_screen_color);
        return null;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int progressToLevel;
        if (z && (progressToLevel = progressToLevel(i)) != this.mLevel) {
            this.mLevel = progressToLevel;
            callChangeListener(Integer.valueOf(progressToLevel));
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
    }
}
