package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

/* loaded from: classes5.dex */
public class RadioSetPreferenceCategory extends PreferenceCategory implements Checkable {
    private boolean mChecked;
    private boolean mCheckedSet;
    private OnPreferenceChangeInternalListener mInternalListener;
    private OnPreferenceChangeInternalListener mInternalListenerProxy;
    private String mPrimaryKey;
    private RadioButtonPreference mPrimaryPreference;

    public RadioSetPreferenceCategory(Context context) {
        this(context, null);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.preferenceCategoryCheckableStyle);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mInternalListenerProxy = new OnPreferenceChangeInternalListener() { // from class: miuix.preference.RadioSetPreferenceCategory.1
            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public void notifyPreferenceChangeInternal(Preference preference) {
                if (preference instanceof RadioButtonPreference) {
                    RadioSetPreferenceCategory.this.setChecked(((RadioButtonPreference) preference).isChecked());
                }
                if (RadioSetPreferenceCategory.this.mInternalListener != null) {
                    RadioSetPreferenceCategory.this.mInternalListener.notifyPreferenceChangeInternal(preference);
                }
            }

            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public boolean onPreferenceChangeInternal(Preference preference, Object obj) {
                if (RadioSetPreferenceCategory.this.mInternalListener != null) {
                    return RadioSetPreferenceCategory.this.mInternalListener.onPreferenceChangeInternal(preference, obj);
                }
                return true;
            }
        };
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RadioSetPreferenceCategory, i, i2);
        this.mPrimaryKey = obtainStyledAttributes.getString(R$styleable.RadioSetPreferenceCategory_primaryKey);
        obtainStyledAttributes.recycle();
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        String str = this.mPrimaryKey;
        if (str == null) {
            if (getPreferenceCount() == 0) {
                if (!(preference instanceof RadioButtonPreference)) {
                    throw new IllegalArgumentException("The first preference must be RadioButtonPreference, if primary key is empty");
                }
                RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
                this.mPrimaryPreference = radioButtonPreference;
                radioButtonPreference.setOnPreferenceChangeInternalListener(this.mInternalListenerProxy);
            }
        } else if (str.equals(preference.getKey())) {
            RadioButtonPreference radioButtonPreference2 = this.mPrimaryPreference;
            if (radioButtonPreference2 != null && radioButtonPreference2 != preference) {
                throw new IllegalArgumentException("must not have two primary preference");
            }
            if (!(preference instanceof RadioButtonPreference)) {
                throw new IllegalArgumentException("Primary preference must be RadioButtonPreference");
            }
            RadioButtonPreference radioButtonPreference3 = (RadioButtonPreference) preference;
            this.mPrimaryPreference = radioButtonPreference3;
            radioButtonPreference3.setOnPreferenceChangeInternalListener(this.mInternalListenerProxy);
        }
        return super.addPreference(preference);
    }

    public RadioButtonPreference getPrimaryPreference() {
        return this.mPrimaryPreference;
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        if ((this.mChecked != z) || !this.mCheckedSet) {
            this.mChecked = z;
            this.mCheckedSet = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
        this.mInternalListener = onPreferenceChangeInternalListener;
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }
}
