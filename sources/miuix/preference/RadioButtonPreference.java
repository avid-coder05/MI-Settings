package miuix.preference;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.CompoundButton;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.util.HapticFeedbackCompat;

/* loaded from: classes5.dex */
public class RadioButtonPreference extends CheckBoxPreference implements Checkable, MessageQueue.IdleHandler {
    private boolean mChangeFromClick;
    private HapticFeedbackCompat mHapticFeedbackCompat;
    private OnPreferenceChangeInternalListener mInternalListener;
    private View mRadioButton;

    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.radioButtonPreferenceStyle);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Looper.myQueue().addIdleHandler(this);
    }

    private void initHapticFeedback() {
        if (this.mHapticFeedbackCompat == null) {
            this.mHapticFeedbackCompat = new HapticFeedbackCompat(getContext());
        }
    }

    private void syncStartCheckAnim(CompoundButton compoundButton) {
        if (Build.VERSION.SDK_INT >= 24) {
            Drawable buttonDrawable = compoundButton.getButtonDrawable();
            if (buttonDrawable instanceof StateListDrawable) {
                Drawable current = buttonDrawable.getCurrent();
                if (current instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) current).start();
                }
            }
        }
    }

    @Override // androidx.preference.Preference
    public boolean callChangeListener(Object obj) {
        OnPreferenceChangeInternalListener onPreferenceChangeInternalListener = this.mInternalListener;
        boolean z = (onPreferenceChangeInternalListener != null ? onPreferenceChangeInternalListener.onPreferenceChangeInternal(this, obj) : true) && super.callChangeListener(obj);
        if (!z && this.mChangeFromClick) {
            this.mChangeFromClick = false;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void notifyChanged() {
        super.notifyChanged();
        OnPreferenceChangeInternalListener onPreferenceChangeInternalListener = this.mInternalListener;
        if (onPreferenceChangeInternalListener != null) {
            onPreferenceChangeInternalListener.notifyPreferenceChangeInternal(this);
        }
        if (this.mChangeFromClick) {
            initHapticFeedback();
            this.mHapticFeedbackCompat.performExtHapticFeedback(167);
            this.mChangeFromClick = false;
        }
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (getParent() instanceof RadioSetPreferenceCategory) {
            setLayoutResource(R$layout.miuix_preference_radiobutton);
        } else {
            setLayoutResource(R$layout.miuix_preference_radiobutton_two_state_background);
        }
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        View findViewById = view.findViewById(16908310);
        if (findViewById != null && (findViewById instanceof Checkable)) {
            ((Checkable) findViewById).setChecked(isChecked());
        }
        View findViewById2 = view.findViewById(16908304);
        if (findViewById2 != null && (findViewById2 instanceof Checkable)) {
            ((Checkable) findViewById2).setChecked(isChecked());
        }
        if (findViewById != null && findViewById2 != null && findViewById.getVisibility() == 0 && findViewById2.getVisibility() == 0) {
            findViewById2.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: miuix.preference.RadioButtonPreference.1
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                    accessibilityNodeInfo.setCheckable(false);
                }
            });
        }
        View findViewById3 = view.findViewById(16908289);
        this.mRadioButton = findViewById3;
        if (findViewById3 != null) {
            findViewById3.setImportantForAccessibility(2);
            if ((this.mRadioButton instanceof CompoundButton) && isChecked()) {
                syncStartCheckAnim((CompoundButton) this.mRadioButton);
            }
        }
        Folme.useAt(view).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(view, new AnimConfig[0]);
    }

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    protected void onClick() {
        this.mChangeFromClick = true;
        super.onClick();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        Looper.myQueue().removeIdleHandler(this);
        getSharedPreferences().edit().remove(getKey()).apply();
    }

    @Override // android.os.MessageQueue.IdleHandler
    public boolean queueIdle() {
        initHapticFeedback();
        return false;
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
