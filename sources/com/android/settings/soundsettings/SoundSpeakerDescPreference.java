package com.android.settings.soundsettings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class SoundSpeakerDescPreference extends Preference {
    private Context mContext;
    private int mIconId;
    private boolean mIsHarman;
    private String mSummary;

    public SoundSpeakerDescPreference(Context context) {
        super(context);
        this.mIconId = 0;
        this.mIsHarman = false;
        init(context);
    }

    public SoundSpeakerDescPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIconId = 0;
        this.mIsHarman = false;
        init(context);
    }

    public SoundSpeakerDescPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIconId = 0;
        this.mIsHarman = false;
        init(context);
    }

    public SoundSpeakerDescPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mIconId = 0;
        this.mIsHarman = false;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setLayoutResource(R.layout.sound_speaker_desc_preference);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        TextView textView = (TextView) view.findViewById(R.id.speaker_summary);
        ImageView imageView = (ImageView) view.findViewById(R.id.speaker_harman);
        if (!TextUtils.isEmpty(this.mSummary)) {
            textView.setText(this.mSummary);
        }
        imageView.setVisibility(this.mIsHarman ? 0 : 8);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.speaker_icon);
        int i = this.mIconId;
        if (i != 0) {
            imageView2.setImageDrawable(this.mContext.getDrawable(i));
        }
    }

    public void setHarman(boolean z) {
        this.mIsHarman = z;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void setIcon(int i) {
        this.mIconId = i;
        notifyChanged();
    }

    public void setSummary(String str) {
        this.mSummary = str;
        notifyChanged();
    }
}
