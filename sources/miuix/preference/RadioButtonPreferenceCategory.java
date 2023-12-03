package miuix.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;

/* loaded from: classes5.dex */
public class RadioButtonPreferenceCategory extends PreferenceCategory {
    private SingleChoiceHelper mCheckedChoice;
    private int mCheckedPosition;
    private OnPreferenceChangeInternalListener mInternalListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class CategorySingleChoiceHelper extends SingleChoiceHelper {
        private RadioSetPreferenceCategory mCategory;

        CategorySingleChoiceHelper(RadioSetPreferenceCategory radioSetPreferenceCategory) {
            super(radioSetPreferenceCategory);
            this.mCategory = radioSetPreferenceCategory;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public Preference getPreference() {
            return this.mCategory;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper, android.widget.Checkable
        public void setChecked(boolean z) {
            super.setChecked(z);
            if (this.mCategory.getPrimaryPreference() != null) {
                this.mCategory.getPrimaryPreference().setChecked(z);
            }
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mCategory.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class PreferenceSingleChoiceHelper extends SingleChoiceHelper {
        RadioButtonPreference mPreference;

        PreferenceSingleChoiceHelper(RadioButtonPreference radioButtonPreference) {
            super(radioButtonPreference);
            this.mPreference = radioButtonPreference;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public Preference getPreference() {
            return this.mPreference;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mPreference.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public abstract class SingleChoiceHelper implements Checkable {
        Checkable mCheckable;

        SingleChoiceHelper(Checkable checkable) {
            this.mCheckable = checkable;
        }

        abstract Preference getPreference();

        @Override // android.widget.Checkable
        public boolean isChecked() {
            return this.mCheckable.isChecked();
        }

        @Override // android.widget.Checkable
        public void setChecked(boolean z) {
            this.mCheckable.setChecked(z);
        }

        abstract void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener);

        @Override // android.widget.Checkable
        public void toggle() {
            setChecked(!isChecked());
        }
    }

    public RadioButtonPreferenceCategory(Context context) {
        this(context, null);
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.preferenceCategoryRadioStyle);
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCheckedChoice = null;
        this.mCheckedPosition = -1;
        this.mInternalListener = new OnPreferenceChangeInternalListener() { // from class: miuix.preference.RadioButtonPreferenceCategory.1
            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public void notifyPreferenceChangeInternal(Preference preference) {
                SingleChoiceHelper parse = RadioButtonPreferenceCategory.this.parse(preference);
                RadioButtonPreferenceCategory.this.updateCheckedPreference(parse);
                RadioButtonPreferenceCategory.this.updateCheckedPosition(parse);
            }

            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public boolean onPreferenceChangeInternal(Preference preference, Object obj) {
                Checkable checkable = (Checkable) preference;
                Preference.OnPreferenceClickListener onPreferenceClickListener = RadioButtonPreferenceCategory.this.getOnPreferenceClickListener();
                if (onPreferenceClickListener != null) {
                    RadioButtonPreferenceCategory.this.checkPreferenceByInternal(preference, obj);
                    onPreferenceClickListener.onPreferenceClick(RadioButtonPreferenceCategory.this);
                }
                return !checkable.isChecked();
            }
        };
    }

    private boolean callChangeListenerByInternal(Object obj, Preference preference) {
        return preference.getOnPreferenceChangeListener() == null || preference.getOnPreferenceChangeListener().onPreferenceChange(preference, obj);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPreferenceByInternal(Preference preference, Object obj) {
        PreferenceGroup parent = preference.getParent() instanceof RadioSetPreferenceCategory ? preference.getParent() : preference;
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if ((singleChoiceHelper == null || parent != singleChoiceHelper.getPreference()) && callChangeListenerByInternal(obj, parent)) {
            setCheckedPreference(preference);
        }
    }

    private void clearChecked() {
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if (singleChoiceHelper != null) {
            singleChoiceHelper.setChecked(false);
        }
        this.mCheckedChoice = null;
        this.mCheckedPosition = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SingleChoiceHelper parse(Preference preference) {
        if (preference instanceof RadioButtonPreference) {
            return preference.getParent() instanceof RadioSetPreferenceCategory ? new CategorySingleChoiceHelper((RadioSetPreferenceCategory) preference.getParent()) : new PreferenceSingleChoiceHelper((RadioButtonPreference) preference);
        } else if (preference instanceof RadioSetPreferenceCategory) {
            return new CategorySingleChoiceHelper((RadioSetPreferenceCategory) preference);
        } else {
            throw new IllegalArgumentException("Only RadioButtonPreference or RadioSetPreferenceCategory can be added to RadioButtonPreferenceCategory");
        }
    }

    private void setCheckedPreferenceInternal(SingleChoiceHelper singleChoiceHelper) {
        singleChoiceHelper.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedPosition(SingleChoiceHelper singleChoiceHelper) {
        if (singleChoiceHelper.isChecked()) {
            int preferenceCount = getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                if (getPreference(i) == singleChoiceHelper.getPreference()) {
                    this.mCheckedPosition = i;
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedPreference(SingleChoiceHelper singleChoiceHelper) {
        if (singleChoiceHelper.isChecked()) {
            SingleChoiceHelper singleChoiceHelper2 = this.mCheckedChoice;
            if (singleChoiceHelper2 != null && singleChoiceHelper2.getPreference() != singleChoiceHelper.getPreference()) {
                this.mCheckedChoice.setChecked(false);
            }
            this.mCheckedChoice = singleChoiceHelper;
        }
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        SingleChoiceHelper parse = parse(preference);
        boolean addPreference = super.addPreference(preference);
        if (addPreference) {
            parse.setOnPreferenceChangeInternalListener(this.mInternalListener);
        }
        if (parse.isChecked()) {
            if (this.mCheckedChoice != null) {
                throw new IllegalStateException("Already has a checked item, please check state of new add preference");
            }
            this.mCheckedChoice = parse;
        }
        return addPreference;
    }

    public int getCheckedPosition() {
        SingleChoiceHelper singleChoiceHelper;
        if (this.mCheckedPosition == -1 && (singleChoiceHelper = this.mCheckedChoice) != null) {
            updateCheckedPosition(singleChoiceHelper);
        }
        return this.mCheckedPosition;
    }

    @Override // androidx.preference.PreferenceGroup
    public void removeAll() {
        super.removeAll();
        this.mCheckedPosition = -1;
        this.mCheckedChoice = null;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean removePreference(Preference preference) {
        SingleChoiceHelper parse = parse(preference);
        boolean removePreference = super.removePreference(preference);
        if (removePreference) {
            parse.setOnPreferenceChangeInternalListener(null);
            if (parse.isChecked()) {
                parse.setChecked(false);
                this.mCheckedPosition = -1;
                this.mCheckedChoice = null;
            }
        }
        return removePreference;
    }

    public void setCheckedPosition(int i) {
        SingleChoiceHelper parse = parse(getPreference(i));
        if (parse.isChecked()) {
            return;
        }
        setCheckedPreferenceInternal(parse);
        updateCheckedPreference(parse);
        this.mCheckedPosition = i;
    }

    public void setCheckedPreference(Preference preference) {
        if (preference == null) {
            clearChecked();
            return;
        }
        SingleChoiceHelper parse = parse(preference);
        if (parse.isChecked()) {
            return;
        }
        setCheckedPreferenceInternal(parse);
        updateCheckedPreference(parse);
        updateCheckedPosition(parse);
    }
}
