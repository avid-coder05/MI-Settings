package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miuix.visual.check.VisualCheckGroup;
import miuix.visual.check.VisualCheckedTextView;

/* loaded from: classes.dex */
public class ScreenResolutionPreference extends Preference {
    private VisualCheckGroup.OnCheckedChangeListener mCheckedListener;
    Context mContext;
    private int mFHDHeight;
    private int mFHDWidth;
    private int mFhdImageRes;
    private int mFhdTextRes;
    private int mFhdTextResSummary;
    private ResolutionVisualCheckBox mFhdView;
    private int mQHDHeight;
    private int mQHDWidth;
    private boolean mQhdChecked;
    private int mQhdImageRes;
    private int mQhdTextRes;
    private int mQhdTextResSummary;
    private ResolutionVisualCheckBox mQhdView;
    private View mRootView;
    private boolean mSwitchEnabled;

    public ScreenResolutionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFHDWidth = 1080;
        this.mFHDHeight = 2400;
        this.mQHDWidth = 1440;
        this.mQHDHeight = 3200;
        this.mContext = context;
        setLayoutResource(R.layout.resolution_selection_view);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mRootView == null) {
            this.mRootView = preferenceViewHolder.itemView;
        }
        VisualCheckGroup visualCheckGroup = (VisualCheckGroup) this.mRootView.findViewById(R.id.checkgroup);
        this.mQhdView = (ResolutionVisualCheckBox) this.mRootView.findViewById(R.id.resolution_qhd);
        this.mFhdView = (ResolutionVisualCheckBox) this.mRootView.findViewById(R.id.resolution_fhd);
        VisualCheckedTextView visualCheckedTextView = (VisualCheckedTextView) this.mRootView.findViewById(R.id.qhd_text_view);
        VisualCheckedTextView visualCheckedTextView2 = (VisualCheckedTextView) this.mRootView.findViewById(R.id.fhd_text_view);
        VisualCheckedTextView visualCheckedTextView3 = (VisualCheckedTextView) this.mRootView.findViewById(R.id.qhd_text_view_summary);
        VisualCheckedTextView visualCheckedTextView4 = (VisualCheckedTextView) this.mRootView.findViewById(R.id.fhd_text_view_summary);
        ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.qhd_img_view);
        ImageView imageView2 = (ImageView) this.mRootView.findViewById(R.id.fhd_img_view);
        this.mQhdView.setChecked(this.mQhdChecked);
        this.mFhdView.setChecked(!this.mQhdChecked);
        int i = this.mQhdTextRes;
        if (i != 0) {
            visualCheckedTextView.setText(i);
        }
        int i2 = this.mFhdTextRes;
        if (i2 != 0) {
            visualCheckedTextView2.setText(i2);
        }
        if (this.mQhdTextResSummary != 0) {
            visualCheckedTextView3.setText(String.format(this.mContext.getResources().getString(this.mQhdTextResSummary), Integer.valueOf(this.mQHDHeight), Integer.valueOf(this.mQHDWidth)));
        }
        if (this.mFhdTextResSummary != 0) {
            visualCheckedTextView4.setText(String.format(this.mContext.getResources().getString(this.mFhdTextResSummary), Integer.valueOf(this.mFHDHeight), Integer.valueOf(this.mFHDWidth)));
        }
        int i3 = this.mQhdImageRes;
        if (i3 != 0) {
            imageView.setImageResource(i3);
        }
        int i4 = this.mFhdImageRes;
        if (i4 != 0) {
            imageView2.setImageResource(i4);
        }
        visualCheckGroup.setOnCheckedChangeListener(this.mCheckedListener);
        this.mQhdView.setCheckEnabled(this.mSwitchEnabled);
        this.mFhdView.setCheckEnabled(this.mSwitchEnabled);
    }

    public void setFHDSolution(int i, int i2) {
        this.mFHDWidth = i;
        this.mFHDHeight = i2;
    }

    public void setFhdImage(int i) {
        this.mFhdImageRes = i;
    }

    public void setFhdText(int i) {
        this.mFhdTextRes = i;
    }

    public void setFhdTextSummary(int i) {
        this.mFhdTextResSummary = i;
    }

    public void setOnCheckedChangeListener(VisualCheckGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckedListener = onCheckedChangeListener;
    }

    public void setQHDSolution(int i, int i2) {
        this.mQHDWidth = i;
        this.mQHDHeight = i2;
    }

    public void setQhdChecked(boolean z) {
        this.mQhdChecked = z;
    }

    public void setQhdImage(int i) {
        this.mQhdImageRes = i;
    }

    public void setQhdText(int i) {
        this.mQhdTextRes = i;
    }

    public void setQhdTextSummary(int i) {
        this.mQhdTextResSummary = i;
    }

    public void setSwitchEnabled(boolean z) {
        this.mSwitchEnabled = z;
        ResolutionVisualCheckBox resolutionVisualCheckBox = this.mQhdView;
        if (resolutionVisualCheckBox != null) {
            resolutionVisualCheckBox.setCheckEnabled(z);
        }
        ResolutionVisualCheckBox resolutionVisualCheckBox2 = this.mFhdView;
        if (resolutionVisualCheckBox2 != null) {
            resolutionVisualCheckBox2.setCheckEnabled(this.mSwitchEnabled);
        }
    }
}
