package com.android.settings.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceXmlParserUtils;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.RadioButtonPreference;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
public abstract class RadioButtonPickerFragment extends InstrumentedPreferenceFragment implements RadioButtonPreference.OnClickListener {
    static final String EXTRA_FOR_WORK = "for_work";
    private int mIllustrationId;
    private int mIllustrationPreviewId;
    protected int mUserId;
    protected UserManager mUserManager;
    private VideoPreference mVideoPreference;
    boolean mAppendStaticPreferences = false;
    private final Map<String, CandidateInfo> mCandidates = new ArrayMap();
    protected boolean mNeedShowIcon = true;

    /* loaded from: classes2.dex */
    public class MiuiRadioButtonPreference extends RadioButtonPreference {
        private boolean showIcon;

        public MiuiRadioButtonPreference(Context context, boolean z) {
            super(context);
            this.showIcon = z;
        }

        @Override // com.android.settingslib.widget.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceFeature
        public boolean hasIcon() {
            return this.showIcon;
        }

        @Override // com.android.settingslib.widget.RadioButtonPreference, com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
            if (imageView == null || Build.IS_TABLET) {
                return;
            }
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_preference_icon_min_width);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = dimensionPixelSize;
                layoutParams.height = dimensionPixelSize;
            }
            imageView.setLayoutParams(layoutParams);
        }
    }

    private void addIllustration(PreferenceScreen preferenceScreen) {
        VideoPreference videoPreference = new VideoPreference(getContext());
        this.mVideoPreference = videoPreference;
        videoPreference.setVideo(this.mIllustrationId, this.mIllustrationPreviewId);
        preferenceScreen.addPreference(this.mVideoPreference);
    }

    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
    }

    public RadioButtonPreference bindPreference(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2) {
        radioButtonPreference.setTitle(candidateInfo.loadLabel());
        radioButtonPreference.setIcon(Utils.getSafeIcon(candidateInfo.loadIcon()));
        radioButtonPreference.setKey(str);
        if (TextUtils.equals(str2, str)) {
            radioButtonPreference.setChecked(true);
        }
        radioButtonPreference.setEnabled(candidateInfo.enabled);
        radioButtonPreference.setOnClickListener(this);
        return radioButtonPreference;
    }

    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CandidateInfo getCandidate(String str) {
        return this.mCandidates.get(str);
    }

    protected abstract List<? extends CandidateInfo> getCandidates();

    protected abstract String getDefaultKey();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public abstract int getPreferenceScreenResId();

    protected int getRadioButtonPreferenceCustomLayoutResId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getSystemDefaultKey() {
        return null;
    }

    public void mayCheckOnlyRadioButton() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null || preferenceScreen.getPreferenceCount() != 1) {
            return;
        }
        Preference preference = preferenceScreen.getPreference(0);
        if (preference instanceof RadioButtonPreference) {
            ((RadioButtonPreference) preference).setChecked(true);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        Bundle arguments = getArguments();
        boolean z = arguments != null ? arguments.getBoolean(EXTRA_FOR_WORK) : false;
        UserHandle managedProfile = Utils.getManagedProfile(this.mUserManager);
        this.mUserId = (!z || managedProfile == null) ? UserHandle.myUserId() : managedProfile.getIdentifier();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        try {
            this.mAppendStaticPreferences = PreferenceXmlParserUtils.extractMetadata(getContext(), getPreferenceScreenResId(), 1025).get(0).getBoolean("staticPreferenceLocation");
        } catch (IOException e) {
            Log.e("RadioButtonPckrFrgmt", "Error trying to open xml file", e);
        } catch (XmlPullParserException e2) {
            Log.e("RadioButtonPckrFrgmt", "Error parsing xml", e2);
        }
        updateCandidates();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        setHasOptionsMenu(true);
        return onCreateView;
    }

    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        onRadioButtonConfirmed(radioButtonPreference.getKey());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRadioButtonConfirmed(String str) {
        boolean defaultKey = setDefaultKey(str);
        if (defaultKey) {
            updateCheckedState(str);
        }
        onSelectionPerformed(defaultKey);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSelectionPerformed(boolean z) {
    }

    protected abstract boolean setDefaultKey(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public void setIllustration(int i, int i2) {
        this.mIllustrationId = i;
        this.mIllustrationPreviewId = i2;
    }

    protected boolean shouldShowItemNone() {
        return false;
    }

    public void updateCandidates() {
        this.mCandidates.clear();
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates != null) {
            for (CandidateInfo candidateInfo : candidates) {
                this.mCandidates.put(candidateInfo.getKey(), candidateInfo);
            }
        }
        String defaultKey = getDefaultKey();
        String systemDefaultKey = getSystemDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        if (this.mIllustrationId != 0) {
            addIllustration(preferenceScreen);
        }
        if (!this.mAppendStaticPreferences) {
            addStaticPreferences(preferenceScreen);
        }
        int radioButtonPreferenceCustomLayoutResId = getRadioButtonPreferenceCustomLayoutResId();
        if (shouldShowItemNone()) {
            MiuiRadioButtonPreference miuiRadioButtonPreference = new MiuiRadioButtonPreference(getPrefContext(), this.mNeedShowIcon);
            if (radioButtonPreferenceCustomLayoutResId > 0) {
                miuiRadioButtonPreference.setLayoutResource(radioButtonPreferenceCustomLayoutResId);
            }
            miuiRadioButtonPreference.setIcon(R.drawable.ic_remove_circle);
            miuiRadioButtonPreference.setTitle(R.string.app_list_preference_none);
            miuiRadioButtonPreference.setChecked(TextUtils.isEmpty(defaultKey));
            miuiRadioButtonPreference.setOnClickListener(this);
            preferenceScreen.addPreference(miuiRadioButtonPreference);
        }
        if (candidates != null) {
            for (CandidateInfo candidateInfo2 : candidates) {
                RadioButtonPreference miuiRadioButtonPreference2 = new MiuiRadioButtonPreference(getPrefContext(), this.mNeedShowIcon);
                miuiRadioButtonPreference2.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                bindPreference(miuiRadioButtonPreference2, candidateInfo2.getKey(), candidateInfo2, defaultKey);
                bindPreferenceExtra(miuiRadioButtonPreference2, candidateInfo2.getKey(), candidateInfo2, defaultKey, systemDefaultKey);
                preferenceScreen.addPreference(miuiRadioButtonPreference2);
            }
        }
        mayCheckOnlyRadioButton();
        if (this.mAppendStaticPreferences) {
            addStaticPreferences(preferenceScreen);
        }
    }

    public void updateCheckedState(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            int preferenceCount = preferenceScreen.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference = preferenceScreen.getPreference(i);
                if (preference instanceof RadioButtonPreference) {
                    RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
                    if (radioButtonPreference.isChecked() != TextUtils.equals(preference.getKey(), str)) {
                        radioButtonPreference.setChecked(TextUtils.equals(preference.getKey(), str));
                    }
                }
            }
        }
    }
}
