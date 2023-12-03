package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import java.util.ArrayList;
import java.util.List;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class MiuiSmartCoverSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mSmartcoverModeEnable;
    private List<ImageRadioButtonPreference> mDisplayPref = new ArrayList();
    private List<Integer> mSupportTypes = new ArrayList();
    private final Preference.OnPreferenceClickListener mPrefClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiSmartCoverSettingsFragment.1
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            if (preference != MiuiSmartCoverSettingsFragment.this.mSmartcoverModeEnable) {
                MiuiSmartCoverSettingsFragment.this.enableSmartcoverGroup(false, 1);
                ImageRadioButtonPreference imageRadioButtonPreference = (ImageRadioButtonPreference) preference;
                imageRadioButtonPreference.setChecked(true);
                SystemProperties.set("persist.sys.smallwin_type", String.valueOf(MiuiSmartCoverSettingsFragment.this.mSupportTypes.get(MiuiSmartCoverSettingsFragment.this.mDisplayPref.indexOf(imageRadioButtonPreference))));
            }
            MiuiSettings.System.setSmartCoverMode(MiuiSmartCoverSettingsFragment.this.mSmartcoverModeEnable.isChecked());
            return true;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ImageRadioButtonPreference extends RadioButtonPreference {
        private ImageView mImageView;
        private int mNormalImageRes;
        private int mSelectedImageRes;

        public ImageRadioButtonPreference(MiuiSmartCoverSettingsFragment miuiSmartCoverSettingsFragment, Context context, int i, int i2) {
            this(miuiSmartCoverSettingsFragment, context, null);
            this.mNormalImageRes = i;
            this.mSelectedImageRes = i2;
        }

        public ImageRadioButtonPreference(MiuiSmartCoverSettingsFragment miuiSmartCoverSettingsFragment, Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public ImageRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            setWidgetLayoutResource(R.layout.radio_preference_imageview);
        }

        @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
        public void onBindView(View view) {
            super.onBindView(view);
            view.setMinimumHeight((int) MiuiSmartCoverSettingsFragment.this.getResources().getDimension(R.dimen.smart_cover_item_min_height));
            ImageView imageView = (ImageView) view.findViewById(R.id.radio_button_image);
            this.mImageView = imageView;
            imageView.setImageResource((isChecked() && isEnabled()) ? this.mSelectedImageRes : this.mNormalImageRes);
        }
    }

    private void addSupportTypes(int i) {
        String str;
        int i2 = R.string.normal_smartcover_title;
        int i3 = R.drawable.smartcover_normal_normal;
        int i4 = R.drawable.smartcover_normal_selected;
        if (i == 0 || i == 1) {
            i2 = R.string.smallwindow_smartcover_title;
            i3 = R.drawable.smartcover_small_window_normal;
            i4 = R.drawable.smartcover_small_window_selected;
            str = "smallwindow";
        } else if (i == 2) {
            i2 = R.string.lattice_smartcover_title;
            i3 = R.drawable.smartcover_lattice_normal;
            i4 = R.drawable.smartcover_lattice_selected;
            str = "lattice";
        } else if (i != 3) {
            str = "normal";
        } else {
            i2 = R.string.full_transparent_smarrcover_title;
            i3 = R.drawable.smartcover_transparent_normal;
            i4 = R.drawable.smartcover_transparent_selected;
            str = "transparent";
        }
        initSmartCoverMode(new ImageRadioButtonPreference(this, getPrefContext(), i3, i4), i2, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableSmartcoverGroup(boolean z, int i) {
        for (int i2 = 0; i2 < this.mDisplayPref.size(); i2++) {
            ImageRadioButtonPreference imageRadioButtonPreference = this.mDisplayPref.get(i2);
            if (i == 0) {
                imageRadioButtonPreference.setEnabled(z);
            } else if (i == 1) {
                imageRadioButtonPreference.setChecked(z);
            }
        }
    }

    private void init() {
        boolean z = SystemProperties.getInt("persist.sys.smartcover_mode", 1) != 0;
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("smartcover_mode_enable");
        this.mSmartcoverModeEnable = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mSmartcoverModeEnable.setChecked(z);
        int[] intArray = FeatureParser.getIntArray("small_win_cover_type");
        if (intArray == null || intArray.length < 2) {
            return;
        }
        for (int i : intArray) {
            this.mSupportTypes.add(Integer.valueOf(i));
            addSupportTypes(i);
        }
        enableSmartcoverGroup(z, 0);
        int i2 = SystemProperties.getInt("persist.sys.smallwin_type", -2);
        (i2 == -2 ? this.mDisplayPref.get(0) : this.mDisplayPref.get(this.mSupportTypes.indexOf(Integer.valueOf(i2)))).setChecked(true);
        if (FeatureParser.getBoolean("support_multiple_small_win_cover", false)) {
            MiuiSettings.System.putBooleanForUser(getContentResolver(), "smart_cover_key", false, 0);
        }
    }

    private void initSmartCoverMode(ImageRadioButtonPreference imageRadioButtonPreference, int i, String str) {
        imageRadioButtonPreference.setTitle(i);
        imageRadioButtonPreference.setKey(str);
        imageRadioButtonPreference.setPersistent(false);
        getPreferenceScreen().addPreference(imageRadioButtonPreference);
        imageRadioButtonPreference.setOnPreferenceClickListener(this.mPrefClickListener);
        this.mDisplayPref.add(imageRadioButtonPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSmartCoverSettingsFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.smartcover_mode_settings);
        init();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSmartcoverModeEnable) {
            enableSmartcoverGroup(((Boolean) obj).booleanValue(), 0);
            return true;
        }
        return false;
    }
}
