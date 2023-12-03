package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.R$styleable;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class ImagePreference extends Preference {
    private Preference.OnPreferenceClickListener mContentClickListener;
    private int mImageRes;
    private String mSecondTitle;

    public ImagePreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0, 0);
    }

    public ImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i, 0);
    }

    public ImagePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        setLayoutResource(R.layout.preference_image_view);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ImagePreference, i, i2);
        this.mImageRes = obtainStyledAttributes.getResourceId(R$styleable.ImagePreference_image, 0);
        this.mSecondTitle = obtainStyledAttributes.getString(R$styleable.ImagePreference_second_title);
        obtainStyledAttributes.recycle();
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        if (this.mImageRes != 0) {
            ((ImageView) view.findViewById(R.id.preference_image)).setImageResource(this.mImageRes);
        }
        view.setPadding(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        view.post(new Runnable() { // from class: com.android.settings.widget.ImagePreference.1
            @Override // java.lang.Runnable
            public void run() {
                if (ImagePreference.this.getShouldDisableView()) {
                    ImagePreference.this.setShouldDisableView(false);
                }
            }
        });
        view.setEnabled(false);
        View findViewById = view.findViewById(R.id.content);
        View findViewById2 = view.findViewById(R.id.arrow_right);
        View findViewById3 = view.findViewById(R.id.arrow_right_2);
        View findViewById4 = view.findViewById(R.id.second_title_container);
        boolean isEmpty = true ^ TextUtils.isEmpty(this.mSecondTitle);
        if (isEmpty) {
            findViewById4.setVisibility(0);
            ((TextView) findViewById4.findViewById(R.id.second_title)).setText(this.mSecondTitle);
        }
        if (this.mContentClickListener == null) {
            findViewById.setEnabled(false);
            findViewById4.setEnabled(false);
            findViewById2.setVisibility(8);
            findViewById3.setVisibility(8);
            return;
        }
        if (isEmpty) {
            findViewById4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.ImagePreference.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ImagePreference.this.mContentClickListener.onPreferenceClick(ImagePreference.this);
                }
            });
            findViewById.setEnabled(false);
        } else {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.ImagePreference.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ImagePreference.this.mContentClickListener.onPreferenceClick(ImagePreference.this);
                }
            });
            findViewById4.setEnabled(false);
        }
        findViewById2.setVisibility(isEmpty ? 8 : 0);
        findViewById3.setVisibility(isEmpty ? 0 : 8);
    }

    public void setContentClickListener(Preference.OnPreferenceClickListener onPreferenceClickListener) {
        this.mContentClickListener = onPreferenceClickListener;
    }
}
