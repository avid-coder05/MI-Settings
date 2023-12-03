package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Switch;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class MainSwitchPreference extends TwoStatePreference implements OnMainSwitchChangeListener {
    private MainSwitchBar mMainSwitchBar;
    private CharSequence mSummary;
    private final List<OnMainSwitchChangeListener> mSwitchChangeListeners;
    private CharSequence mTitle;

    public MainSwitchPreference(Context context) {
        super(context);
        this.mSwitchChangeListeners = new ArrayList();
        init(context, null);
    }

    public MainSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    public MainSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    public MainSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setLayoutResource(R$layout.settingslib_main_switch_layout);
        this.mSwitchChangeListeners.add(this);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, androidx.preference.R$styleable.Preference, 0, 0);
            setTitle(obtainStyledAttributes.getText(androidx.preference.R$styleable.Preference_android_title));
            setSummary(obtainStyledAttributes.getText(androidx.preference.R$styleable.Preference_android_summary));
            obtainStyledAttributes.recycle();
        }
    }

    private void registerListenerToSwitchBar() {
        Iterator<OnMainSwitchChangeListener> it = this.mSwitchChangeListeners.iterator();
        while (it.hasNext()) {
            this.mMainSwitchBar.addOnSwitchChangeListener(it.next());
        }
        this.mSwitchChangeListeners.clear();
    }

    public void addOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar == null) {
            this.mSwitchChangeListeners.add(onMainSwitchChangeListener);
        } else {
            mainSwitchBar.addOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(false);
        preferenceViewHolder.setDividerAllowedBelow(false);
        this.mMainSwitchBar = (MainSwitchBar) preferenceViewHolder.findViewById(R$id.settingslib_main_switch_bar);
        updateStatus(isChecked());
        registerListenerToSwitchBar();
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        super.setChecked(z);
    }

    public void removeOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar == null) {
            this.mSwitchChangeListeners.remove(onMainSwitchChangeListener);
        } else {
            mainSwitchBar.removeOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar == null || mainSwitchBar.isChecked() == z) {
            return;
        }
        this.mMainSwitchBar.setChecked(z);
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        this.mSummary = charSequence;
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar != null) {
            mainSwitchBar.setSummary(charSequence);
        }
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar != null) {
            mainSwitchBar.setTitle(charSequence);
        }
    }

    public void updateStatus(boolean z) {
        setChecked(z);
        MainSwitchBar mainSwitchBar = this.mMainSwitchBar;
        if (mainSwitchBar != null) {
            mainSwitchBar.setTitle(this.mTitle);
            this.mMainSwitchBar.setSummary(this.mSummary);
            this.mMainSwitchBar.show();
        }
    }
}
